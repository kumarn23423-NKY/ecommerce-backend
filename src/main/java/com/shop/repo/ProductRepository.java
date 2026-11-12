package com.shop.repo;
import com.shop.entity.Product;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("select p from Product p where (:q is null or lower(p.name) like lower(concat('%',:q,'%'))) and (:cat is null or p.category.id = :cat)")
    Page<Product> search(@Param("q") String q, @Param("cat") Long cat, Pageable pageable);
}
