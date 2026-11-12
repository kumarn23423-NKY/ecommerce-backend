# E-commerce Backend (Spring Boot 3, Java 17, MySQL)
1. Install JDK 17 or 21, Maven, MySQL 8. Edit DB user/password in `src/main/resources/application.properties`.
2. Run: `mvn spring-boot:run`  (DB `ecommerce` is auto-created; sample data is seeded)
3. Swagger: http://localhost:8080/swagger-ui.html
4. Admin login: admin@shop.com / admin123

Endpoints: POST /api/auth/register|login, GET /api/products?q=&category=&page=&sort=,
GET/POST/DELETE /api/cart, POST/GET /api/orders, POST /api/orders/{id}/cancel,
Admin: /api/admin/products, /api/admin/categories, /api/admin/orders, PUT /api/admin/orders/{id}/status?status=SHIPPED
