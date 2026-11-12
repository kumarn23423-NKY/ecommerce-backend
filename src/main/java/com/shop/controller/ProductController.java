package com.shop.controller;
import com.shop.entity.*;
import com.shop.repo.*;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
@RestController
public class ProductController {
    private final ProductRepository products; private final CategoryRepository cats;
    public ProductController(ProductRepository products, CategoryRepository cats) { this.products = products; this.cats = cats; }
    @GetMapping("/api/products")
    public Page<Product> list(@RequestParam(required = false) String q, @RequestParam(required = false) Long category,
                              @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "12") int size,
                              @RequestParam(defaultValue = "id") String sort, @RequestParam(defaultValue = "asc") String dir) {
        Sort s = dir.equalsIgnoreCase("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending();
        return products.search(q, category, PageRequest.of(page, size, s));
    }
    @GetMapping("/api/products/{id}") public Product get(@PathVariable Long id) { return products.findById(id).orElseThrow(); }
    @GetMapping("/api/categories") public List<Category> categories() { return cats.findAll(); }
    @PostMapping("/api/admin/products") public Product create(@Valid @RequestBody Product p) { p.setId(null); return products.save(p); }
    @PutMapping("/api/admin/products/{id}") public Product update(@PathVariable Long id, @Valid @RequestBody Product p) { p.setId(id); return products.save(p); }
    @DeleteMapping("/api/admin/products/{id}") public void delete(@PathVariable Long id) { products.deleteById(id); }
    @PostMapping("/api/admin/categories") public Category addCat(@RequestBody Category c) { c.setId(null); return cats.save(c); }
}
