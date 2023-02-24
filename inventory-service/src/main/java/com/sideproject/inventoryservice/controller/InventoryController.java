package com.sideproject.inventoryservice.controller;

import com.sideproject.inventoryservice.dto.InventoryResponse;
import com.sideproject.inventoryservice.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    // http://localhost:8082/api/inventory?skuCode=item1&skuCode= item2
    //
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode)
    {
        return this.inventoryService.isInStock(skuCode);
    }
}
