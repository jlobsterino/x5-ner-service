package com.c_shark.x5_ner_service.repository;

import com.c_shark.x5_ner_service.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<Product> findByNameContaining(@Param("searchText") String searchText);

    @Query("SELECT p FROM Product p JOIN p.brand b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :brandName, '%'))")
    List<Product> findByBrandNameContaining(@Param("brandName") String brandName);

    @Query("SELECT p FROM Product p JOIN p.category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :categoryName, '%'))")
    List<Product> findByCategoryNameContaining(@Param("categoryName") String categoryName);

    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN p.brand b " +
            "LEFT JOIN p.category c " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "OR LOWER(b.name) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<Product> searchProducts(@Param("searchText") String searchText);

    @Query("SELECT p FROM Product p WHERE p.inStock = true")
    List<Product> findAllInStock();
}
