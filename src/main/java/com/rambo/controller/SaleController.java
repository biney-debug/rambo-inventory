package com.rambo.controller;

import com.rambo.dto.SaleRequestDTO;
import com.rambo.dto.SaleResponseDTO;
import com.rambo.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    // GET /api/sales → all sales
    @GetMapping
    public ResponseEntity<List<SaleResponseDTO>> listAll() {
        return ResponseEntity.ok(saleService.findAll());
    }

    // GET /api/sales/{id} → single sale detail
    @GetMapping("/{id}")
    public ResponseEntity<SaleResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.findById(id));
    }

    // GET /api/sales/product/{productId} → sale history for a specific product
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<SaleResponseDTO>> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(saleService.findByProduct(productId));
    }

    // POST /api/sales → register a new sale
    @PostMapping
    public ResponseEntity<SaleResponseDTO> register(@Valid @RequestBody SaleRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(saleService.register(dto));
    }
}
