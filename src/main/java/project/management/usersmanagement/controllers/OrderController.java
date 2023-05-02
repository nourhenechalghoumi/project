package project.management.usersmanagement.controllers;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.management.usersmanagement.Config.CouponException;
import project.management.usersmanagement.Repository.CouponRepo;
import project.management.usersmanagement.Repository.OrderItemsRepository;
import project.management.usersmanagement.Repository.OrderRepo;
import project.management.usersmanagement.Repository.UserRepository;
import project.management.usersmanagement.dto.CreateOrderDto;
import project.management.usersmanagement.dto.OrderViewDto;
import project.management.usersmanagement.dto.PaymentDataDto;
import project.management.usersmanagement.entities.*;
import project.management.usersmanagement.mapper.OrderMapper;
import project.management.usersmanagement.security.services.IUser;
import project.management.usersmanagement.security.services.OrderServiceImplement;
import project.management.usersmanagement.security.services.ProductService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
@CrossOrigin("http://localhost:4200")
public class OrderController {

    @Autowired
    private OrderServiceImplement orderService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepo orderRepository;
    @Autowired
    private OrderItemsRepository orderItemsRepository;

    @Autowired
    private IUser userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private CouponRepo couponRepo;
//user
    @PostMapping("/create")
    public OrderViewDto createOrder(@RequestBody CreateOrderDto createOrderDto) throws StripeException {
        User user = userService.retrieveUserById(createOrderDto.getUserId());
        final Order order = orderService.save(
                Order.builder().user(user).status(EOrderStatus.CREATED).createdDate(new Date())
                        .build());
        List<OrderItem> orderItems = createOrderDto.getOrderItems().stream()
                .map(orderItemDto -> {
                    Product produit = productService.getProductById(
                            orderItemDto.getProductId());
                    return OrderItem.builder().product(produit)
                            .quantity(orderItemDto.getQuantity())
                            .unitprice(produit.getPrice())
                            .order(order)
                            .createdDate(new Date()).build();
                }).collect(Collectors.toList());
        order.setOrderItems(orderItems);
        order.setTotalPrice(orderItems.stream().mapToDouble(e -> e.getPrice()).sum());
        Order savedOrder = orderService.save(order);
        return orderMapper.toDto(savedOrder);
    }

//admin
    @GetMapping("/{id}")
    public ResponseEntity<OrderViewDto> getOrderById(@PathVariable("id") Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok().body(orderMapper.toDto(order));
    }
//admin
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderViewDto>> getOrdersByUser(@PathVariable("userId") Long userId) {
        User user = userRepository.getUserById(userId);
        List<Order> orders = orderService.getOrdersByUser(user);
        return ResponseEntity.ok().body(orderMapper.toDto(orders));
    }
    @GetMapping("/users/Allorders")
     public ResponseEntity<List<OrderViewDto>> getALLOrdersByUser() {
         List<Order> orders;
        orders = orderService.findAll();
        return ResponseEntity.ok().body(orderMapper.toDto(orders));
}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderById(@PathVariable("id") Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    return new RuntimeException("Order not found");
                });
        orderRepository.delete(order);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/coupon")
    public ResponseEntity<Order> applyCoupon(@PathVariable("userId") Long userId,
                                             @RequestParam("orderId") Long orderId,
                                             @RequestParam("couponCode") String couponCode) {
        User user = userRepository.getUserById(userId);
        Order order = orderService.getOrderById(orderId);
        try {
            order = orderService.applyCoupon(user, order, couponCode);
        } catch (CouponException e) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok().body(order);
    }
//bh hedhi bech traja3li l url ta3 l payement
    //user
    @GetMapping("/{id}/payment")
    public ResponseEntity<PaymentDataDto> payOrder(@PathVariable("id") Long id) throws StripeException {
        Order order = orderService.getOrderById(id);
        Session session = orderService.createSession(order.getId().toString(), order.getOrderItems());
        PaymentDataDto data=PaymentDataDto.builder()
                .paymentUrl(session.getUrl())
                .build();
        return ResponseEntity.ok().body(data);
    }
}

