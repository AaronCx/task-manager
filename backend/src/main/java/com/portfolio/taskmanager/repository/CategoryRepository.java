package com.portfolio.taskmanager.repository;

import com.portfolio.taskmanager.entity.Category;
import com.portfolio.taskmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByOwnerOrderByNameAsc(User owner);

    Optional<Category> findByIdAndOwner(Long id, User owner);

    boolean existsByNameAndOwner(String name, User owner);
}
