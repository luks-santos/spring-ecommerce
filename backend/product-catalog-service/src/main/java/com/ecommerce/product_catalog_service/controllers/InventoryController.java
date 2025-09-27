package com.ecommerce.product_catalog_service.controllers;

import com.ecommerce.product_catalog_service.dto.InventoryCreateDTO;
import com.ecommerce.product_catalog_service.entities.Inventory;
import com.ecommerce.product_catalog_service.services.InventoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService service;

    @GetMapping
    public List<Inventory> getAll() {
        return service.findAll();
    }

    @GetMapping("/product/{productId}")
    public Inventory getByProductId(@PathVariable UUID productId) {
        return service.findByProductId(productId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Inventory create(@Valid @RequestBody InventoryCreateDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/product/{productId}")
    public Inventory update(@PathVariable UUID productId, @Valid @RequestBody InventoryCreateDTO dto) {
        return service.update(productId, dto);
    }

    @PatchMapping("/product/{productId}/add")
    public Inventory addQuantity(
            @PathVariable UUID productId,
            @RequestParam @Positive(message = "A quantidade deve ser maior que zero") int qty) {
        return service.addQuantity(productId, qty);
    }

    @PatchMapping("/{productId}/remove")
    public Inventory removeQuantity(
            @PathVariable UUID productId,
            @RequestParam @Positive(message = "A quantidade deve ser maior que zero") int qty) {
        return service.removeQuantity(productId, qty);
    }
}

