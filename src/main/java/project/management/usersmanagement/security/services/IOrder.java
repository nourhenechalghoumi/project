package project.management.usersmanagement.security.services;

import org.springframework.social.ResourceNotFoundException;
import project.management.usersmanagement.Config.CouponException;
import project.management.usersmanagement.entities.*;

import java.util.List;
import java.util.Optional;

public interface IOrder {
    Order createOrder(User user, List<OrderItem> orderItems);
    List<Order> getOrdersByUser(User user);

    List<Order> getALLOrdersByUser(User user);

    Order getOrderById(Long id);
    Double calculateTotalPrice(List<OrderItem> orderItems);
    List<ERole> getUserRoles(User user);

    List<Order> findAll();

    //Purchase createPurchase(User user, Double amount);
    boolean isEligibleForCoupon(User user, Double amount);
    Order applyCoupon(User user, Order order, String couponCode) throws CouponException;

    Order save(Order order);

}

