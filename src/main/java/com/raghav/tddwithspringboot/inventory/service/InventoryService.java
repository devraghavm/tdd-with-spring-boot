package com.raghav.tddwithspringboot.inventory.service;

import com.raghav.tddwithspringboot.inventory.model.InventoryRecord;

import java.util.Optional;

public interface InventoryService {
    Optional<InventoryRecord> getInventoryRecord(Integer productId);

    Optional<InventoryRecord> purchaseProduct(Integer productId, Integer quantityPurchased);
}
