package com.raghav.tddwithspringboot.products.repository;

import com.raghav.tddwithspringboot.products.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    /**
     * Returns the produce with the specified ID.
     * @param id    Id of the product.
     * @return      The requested product if found.
     */
    Optional<Product> findById(Integer id);

    /**
     * Returns all products in the database.
     * @return  All products in the database.
     */
    List<Product> findAll();

    /**
     * Saves the specified product to the database.
     * @param product   The product to save to database.
     * @return          The saved product.
     */
    Product save(Product product);

    /**
     * Updates the specified product, identified by its ID.
     * @param product   The product to update.
     * @return          True if the update succeeded, otherwise false
     */
    boolean update(Product product);

    /**
     * Deletes the product with the specified ID.
     * @param id    Id of the product to be deleted.
     * @return      True if the operation was successful.
     */
    boolean delete(Integer id);
}
