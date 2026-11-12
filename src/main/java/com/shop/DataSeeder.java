package com.shop;
import com.shop.entity.*;
import com.shop.repo.*;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
@Component
public class DataSeeder implements CommandLineRunner {
    private final UserRepository users; private final CategoryRepository cats; private final ProductRepository products; private final PasswordEncoder enc;
    public DataSeeder(UserRepository users, CategoryRepository cats, ProductRepository products, PasswordEncoder enc) { this.users = users; this.cats = cats; this.products = products; this.enc = enc; }
    @Override public void run(String... args) {
        if (users.count() == 0) {
            User a = new User(); a.setName("Admin"); a.setEmail("admin@shop.com"); a.setPassword(enc.encode("admin123")); a.setRole(Role.ADMIN); users.save(a);
        }
        if (cats.count() == 0) {
            Category e = new Category(); e.setName("Electronics"); cats.save(e);
            Category c = new Category(); c.setName("Clothing"); cats.save(c);
            String[][] d = {{"Wireless Mouse","599","50","E"},{"Bluetooth Speaker","1999","30","E"},{"Cotton T-Shirt","399","100","C"},{"Denim Jeans","1299","60","C"}};
            for (String[] x : d) { Product p = new Product(); p.setName(x[0]); p.setDescription("Sample " + x[0]); p.setPrice(new BigDecimal(x[1])); p.setStock(Integer.parseInt(x[2])); p.setCategory(x[3].equals("E") ? e : c); products.save(p); }
        }
    }
}
