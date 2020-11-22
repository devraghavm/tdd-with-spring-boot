package com.raghav.tddwithspringboot.products.service;

import com.raghav.tddwithspringboot.products.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Optional<Product> findById(Integer id);

    List<Product> findAll();

    Product save(Product product);

    boolean update(Product product);

    boolean delete(Integer id);
}
