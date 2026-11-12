package com.shop.controller;
import com.shop.entity.*;
import com.shop.repo.UserRepository;
import com.shop.security.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth")
public class AuthController {
    record RegisterReq(@NotBlank String name, @Email String email, @Size(min = 6) String password) {}
    record LoginReq(@Email String email, @NotBlank String password) {}
    private final UserRepository users; private final PasswordEncoder enc; private final AuthenticationManager am; private final JwtUtil jwt;
    public AuthController(UserRepository users, PasswordEncoder enc, AuthenticationManager am, JwtUtil jwt) { this.users = users; this.enc = enc; this.am = am; this.jwt = jwt; }
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterReq r) {
        if (users.existsByEmail(r.email())) return ResponseEntity.badRequest().body(Map.of("error", "Email already registered"));
        User u = new User(); u.setName(r.name()); u.setEmail(r.email()); u.setPassword(enc.encode(r.password())); u.setRole(Role.CUSTOMER);
        users.save(u);
        return ResponseEntity.ok(Map.of("token", jwt.generate(u.getEmail(), u.getRole().name()), "name", u.getName(), "role", u.getRole()));
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReq r) {
        try { am.authenticate(new UsernamePasswordAuthenticationToken(r.email(), r.password())); }
        catch (AuthenticationException e) { return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials")); }
        User u = users.findByEmail(r.email()).orElseThrow();
        return ResponseEntity.ok(Map.of("token", jwt.generate(u.getEmail(), u.getRole().name()), "name", u.getName(), "role", u.getRole()));
    }
}
