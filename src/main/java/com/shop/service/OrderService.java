package com.shop.service;
import com.shop.entity.*;
import com.shop.repo.*;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class OrderService {
    private final CartItemRepository carts; private final ProductRepository products; private final OrderRepository orders;
    public OrderService(CartItemRepository carts, ProductRepository products, OrderRepository orders) { this.carts = carts; this.products = products; this.orders = orders; }
    @Transactional
    public Order place(User user, String address, String paymentMethod) {
        List<CartItem> items = carts.findByUser(user);
        if (items.isEmpty()) throw new IllegalStateException("Cart is empty");
        Order o = new Order(); o.setUser(user); o.setAddress(address); o.setPaymentMethod(paymentMethod);
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem ci : items) {
            Product p = ci.getProduct();
            if (p.getStock() < ci.getQuantity()) throw new IllegalStateException("Insufficient stock for " + p.getName());
            p.setStock(p.getStock() - ci.getQuantity()); products.save(p);
            OrderItem oi = new OrderItem(); oi.setOrder(o); oi.setProduct(p); oi.setQuantity(ci.getQuantity()); oi.setPrice(p.getPrice());
            o.getItems().add(oi);
            total = total.add(p.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())));
        }
        o.setTotal(total);
        if ("COD".equalsIgnoreCase(paymentMethod)) o.setPaymentStatus("COD_PENDING");
        orders.save(o); carts.deleteByUser(user);
        return o;
    }
    @Transactional
    public Order cancel(User user, Long id) {
        Order o = orders.findById(id).orElseThrow();
        if (!o.getUser().getId().equals(user.getId())) throw new IllegalStateException("Not your order");
        if (o.getStatus() != OrderStatus.PLACED && o.getStatus() != OrderStatus.PACKED) throw new IllegalStateException("Cannot cancel now");
        for (OrderItem oi : o.getItems()) { Product p = oi.getProduct(); p.setStock(p.getStock() + oi.getQuantity()); products.save(p); }
        o.setStatus(OrderStatus.CANCELLED); return orders.save(o);
    }
}
