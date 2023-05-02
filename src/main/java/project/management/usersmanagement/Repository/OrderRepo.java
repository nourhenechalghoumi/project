package project.management.usersmanagement.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.management.usersmanagement.entities.Order;
import project.management.usersmanagement.entities.User;

import java.util.List;
@Repository
public interface OrderRepo  extends JpaRepository<Order,Long> {
    List<Order> findByUser(User user);


}
