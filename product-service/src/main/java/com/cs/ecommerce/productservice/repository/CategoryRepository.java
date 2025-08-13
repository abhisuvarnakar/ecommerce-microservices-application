package com.cs.ecommerce.productservice.repository;

import com.cs.ecommerce.productservice.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query(value = "SELECT * FROM t_category WHERE is_parent = 1 AND is_active = true",
            nativeQuery = true)
    List<Category> findActiveParentsNative();

    @Query(value = "SELECT * FROM t_category WHERE parent_id IN (:parentIds) AND is_active = true",
            nativeQuery = true)
    List<Category> findActiveChildrenByParentIdsNative(@Param("parentIds") List<Long> parentIds);

    @Query(value = "SELECT id, name, description, is_active, created_at, parent_id " +
            "FROM t_category WHERE is_active = true", nativeQuery = true)
    List<Category> findAllActiveNative();

    Optional<Category> findByName(String categoryName);

    @Query("SELECT c FROM Category c WHERE c.name = :categoryName AND c.isActive = true")
    Optional<Category> findByNameAndActiveTrue(@Param("categoryName") String categoryName);

    boolean existsByName(String name);

    boolean existsByNameAndParentId(String name, Long parentId);
}
