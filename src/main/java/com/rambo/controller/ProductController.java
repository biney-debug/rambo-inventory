package com.rambo.controller;

import com.rambo.dto.ProductRequestDTO;
import com.rambo.dto.ProductResponseDTO;
import com.rambo.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // GET /api/products?page=0&size=20 → paginated list
    @GetMapping
    public ResponseEntity<Page<ProductResponseDTO>> listAll(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(productService.findAll(pageable));
    }

    // GET /api/products/{id} → get one product
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    // GET /api/products/low-stock → only products triggering the alert
    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductResponseDTO>> getLowStock() {
        return ResponseEntity.ok(productService.findLowStock());
    }

    // GET /api/products/category/{category} → filter by category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponseDTO>> getByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.findByCategory(category));
    }

    // POST /api/products → create a new product
    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(dto));
    }

    // PUT /api/products/{id} → full update
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO dto) {
        return ResponseEntity.ok(productService.update(id, dto));
    }

    // PATCH /api/products/{id}/restock?quantity=10 → add units without touching other fields
    @PatchMapping("/{id}/restock")
    public ResponseEntity<ProductResponseDTO> restock(
            @PathVariable Long id,
            @RequestParam @Min(value = 1, message = "Quantity must be at least 1") Integer quantity) {
        return ResponseEntity.ok(productService.restock(id, quantity));
    }

    // DELETE /api/products/{id} → delete product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
