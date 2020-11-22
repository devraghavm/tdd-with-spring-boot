package com.raghav.tddwithspringboot.products.controller;

import com.raghav.tddwithspringboot.products.model.Product;
import com.raghav.tddwithspringboot.products.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@RestController
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Integer id) {
        return productService.findById(id)
                .map(product -> {
                    try {
                        return ResponseEntity
                                .ok()
                                .eTag(Integer.toString(product.getVersion()))
                                .location(new URI("/product/" + product.getId()))
                                .body(product);
                    } catch (URISyntaxException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/products")
    public Iterable<Product> getProducts() {
        return productService.findAll();
    }

    /**
     * Creates a new product
     * @param product the product to create
     * @return The created product
     */
    @PostMapping("/product")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        logger.info("Creating new product with name: {}, quantity: {}", product.getName(), product.getQuantity());

        // Create a new product
        Product newProduct = productService.save(product);

        try {
            return ResponseEntity
                    .created(new URI("/product/" + newProduct.getId()))
                    .eTag(Integer.toString(newProduct.getVersion()))
                    .body(newProduct);
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Updates the fields in the specified product with the specified ID.
     * @param product   The product field values to update.
     * @param id        The ID of the product to update.
     * @param ifMatch   The eTag version of the product.
     * @return
     */
    @PutMapping("/product/{id}")
    public ResponseEntity<?> updateProduct(@RequestBody Product product,
                                           @PathVariable Integer id,
                                           @RequestHeader("If-Match") Integer ifMatch) {
        logger.info("Updating product with id: {}, name: {}, quantity: {}",
                id, product.getName(), product.getQuantity());

        // Get the existing product
        Optional<Product> existingProduct = productService.findById(id);

        return existingProduct.map(p -> {
            // Compare the eTags
            logger.info("Product with ID: {} has a version of {}. Update is for If-Match: {}",
                    id, p.getVersion(), ifMatch);
            if (!p.getVersion().equals(ifMatch)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            // Update the product
            p.setName(product.getName());
            p.setQuantity(product.getQuantity());
            p.setVersion(p.getVersion() + 1);

            logger.info("Updating product with ID: {} -> name={}, quantity={}, version={}",
                    p.getId(), p.getName(), p.getQuantity(), p.getVersion());
            // Update the product and return an OK response
            try {
                if (productService.update(p)) {
                    return ResponseEntity
                            .ok()
                            .location(new URI("/product/" + p.getId()))
                            .eTag(Integer.toString(p.getVersion()))
                            .body(p);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } catch (URISyntaxException e) {
                // An error occured trying to create the location URI, return an error
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes the product with specified ID
     * @param id    The ID of the product to delete
     * @return      A ResponseEntity with one of the following status codes:
     *              200 OK if the delete was successful
     *              404 Not Found If the product with the specified ID is not found
     *              500 Internal Server Error if an error occurs during deleting
     */
    @DeleteMapping("/product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        logger.info("Deleting product with ID: {}", id);

        // Get the existing product
        Optional<Product> existingProduct = productService.findById(id);

        return existingProduct.map(p -> {
            if (productService.delete(p.getId())) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }).orElse(ResponseEntity.notFound().build());
    }
}
