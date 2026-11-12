package com.shop.repo;
import com.shop.entity.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderRepository extends JpaRepository<Order, Long> { List<Order> findByUserOrderByCreatedAtDesc(User user); }
