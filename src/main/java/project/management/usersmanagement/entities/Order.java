package project.management.usersmanagement.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@Entity(name="orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;


    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "total_price")
    private Double totalPrice;

    @Enumerated(EnumType.STRING)
    private EOrderStatus status;


    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;
    //new
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "order")
    private Purchase purchase;

    @ManyToOne()
    @JsonIgnore
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    private Date updatedAt;

    @OneToOne(fetch = FetchType.LAZY)
    private Coupon coupon;

    public Order() {

    }
}
