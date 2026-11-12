package com.shop.controller;
import com.shop.entity.*;
import com.shop.repo.*;
import com.shop.service.OrderService;
import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
@RestController
public class OrderController {
    record PlaceReq(String address, String paymentMethod) {}
    private final OrderService service; private final OrderRepository orders; private final UserRepository users;
    public OrderController(OrderService service, OrderRepository orders, UserRepository users) { this.service = service; this.orders = orders; this.users = users; }
    @PostMapping("/api/orders")
    public ResponseEntity<?> place(@AuthenticationPrincipal UserDetails ud, @RequestBody PlaceReq r) {
        try { return ResponseEntity.ok(service.place(users.findByEmail(ud.getUsername()).orElseThrow(), r.address(), r.paymentMethod())); }
        catch (IllegalStateException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }
    @GetMapping("/api/orders") public List<Order> mine(@AuthenticationPrincipal UserDetails ud) { return orders.findByUserOrderByCreatedAtDesc(users.findByEmail(ud.getUsername()).orElseThrow()); }
    @PostMapping("/api/orders/{id}/cancel")
    public ResponseEntity<?> cancel(@AuthenticationPrincipal UserDetails ud, @PathVariable Long id) {
        try { return ResponseEntity.ok(service.cancel(users.findByEmail(ud.getUsername()).orElseThrow(), id)); }
        catch (RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("error", e.getMessage())); }
    }
    @GetMapping("/api/admin/orders") public List<Order> all() { return orders.findAll(); }
    @PutMapping("/api/admin/orders/{id}/status")
    public Order status(@PathVariable Long id, @RequestParam OrderStatus status) { Order o = orders.findById(id).orElseThrow(); o.setStatus(status); return orders.save(o); }
}
