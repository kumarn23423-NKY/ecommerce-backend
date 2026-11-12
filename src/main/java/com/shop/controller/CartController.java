package com.shop.controller;
import com.shop.entity.*;
import com.shop.repo.*;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/cart")
public class CartController {
    record AddReq(Long productId, int quantity) {}
    private final CartItemRepository carts; private final ProductRepository products; private final UserRepository users;
    public CartController(CartItemRepository carts, ProductRepository products, UserRepository users) { this.carts = carts; this.products = products; this.users = users; }
    @GetMapping public List<CartItem> get(@AuthenticationPrincipal UserDetails ud) { return carts.findByUser(users.findByEmail(ud.getUsername()).orElseThrow()); }
    @PostMapping
    public CartItem add(@AuthenticationPrincipal UserDetails ud, @RequestBody AddReq r) {
        User u = users.findByEmail(ud.getUsername()).orElseThrow(); Product p = products.findById(r.productId()).orElseThrow();
        CartItem ci = carts.findByUserAndProduct(u, p).orElseGet(() -> { CartItem n = new CartItem(); n.setUser(u); n.setProduct(p); return n; });
        ci.setQuantity(Math.max(1, ci.getQuantity() + r.quantity())); return carts.save(ci);
    }
    @DeleteMapping("/{id}") public void remove(@PathVariable Long id) { carts.deleteById(id); }
}
