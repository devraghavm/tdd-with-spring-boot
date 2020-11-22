package com.raghav.tddwithspringboot.products.repository;

import com.raghav.tddwithspringboot.products.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ProductRepositoryImpl implements ProductRepository {
    private static final Logger logger = LoggerFactory.getLogger(ProductRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public ProductRepositoryImpl(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("products")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Optional<Product> findById(Integer id) {
        try {
            Product product = jdbcTemplate.queryForObject("select * from products where id = ?",
                    (rs, rowNum) -> {
                        Product p = new Product();
                        p.setId(rs.getInt("id"));
                        p.setName(rs.getString("name"));
                        p.setQuantity(rs.getInt("quantity"));
                        p.setVersion(rs.getInt("version"));
                        return p;
                    }, id);
            return Optional.of(product);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Product> findAll() {
        return jdbcTemplate.query("select * from products",
                (rs, rowNumber) -> {
                    Product product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setName(rs.getString("name"));
                    product.setQuantity(rs.getInt("quantity"));
                    product.setVersion(rs.getInt("version"));
                    return product;
                });
    }

    @Override
    public Product save(Product product) {
        // Build product parameters we want to save
        Map<String, Object> parameters = new HashMap<>(1);
        parameters.put("name", product.getName());
        parameters.put("quantity", product.getQuantity());
        parameters.put("version", product.getVersion());

        // Execute the query and get the generated key
        Number newId = simpleJdbcInsert.executeAndReturnKey(parameters);

        logger.info("Inserting product into database, generated key is: {}", newId);

        // Update the product's ID with the new key
        product.setId(newId.intValue());

        // Return the complete product
        return product;
    }

    @Override
    public boolean update(Product product) {
        return jdbcTemplate.update("update products set name = ?, quantity = ?, version = ? where id = ?",
                product.getName(),
                product.getQuantity(),
                product.getVersion(),
                product.getId()) == 1;
    }

    @Override
    public boolean delete(Integer id) {
        return jdbcTemplate.update("delete from products where id = ?", id) == 1;
    }
}
