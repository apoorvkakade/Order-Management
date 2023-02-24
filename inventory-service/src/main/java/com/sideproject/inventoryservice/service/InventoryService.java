package com.sideproject.inventoryservice.service;

import com.sideproject.inventoryservice.dto.InventoryResponse;
import com.sideproject.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCodes)
    {
        // should also check if product sku code even exists in inventory
        // if not return false for that Inventory response dto
        return this.inventoryRepository.findBySkuCodeIn(skuCodes)
                .stream()
                .map(inventory ->
                    InventoryResponse.builder().skuCode(inventory.getSkuCode())
                            .isInStock(inventory.getQuantity() > 0)
                            .build()
                ).toList();
    }
}
