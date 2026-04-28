package com.rambo.mapper;

import com.rambo.dto.ProductRequestDTO;
import com.rambo.dto.ProductResponseDTO;
import com.rambo.entity.Product;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final ModelMapper modelMapper;

    // Request DTO -> Entity (used on create)
    public Product toEntity(ProductRequestDTO dto) {
        return Product.builder()
                .name(dto.getName().trim())
                .category(dto.getCategory().trim())
                .purchasePrice(dto.getPurchasePrice())
                .salePrice(dto.getSalePrice())
                .stock(dto.getStock())
                .minimumStock(dto.getMinimumStock())
                .build();
    }

    // Entity -> Response DTO (returned to the client)
    public ProductResponseDTO toResponseDTO(Product product) {
        ProductResponseDTO dto = modelMapper.map(product, ProductResponseDTO.class);
        // Calculated fields that ModelMapper cannot resolve automatically
        dto.setProfitMargin(product.getProfitMargin());
        dto.setLowStock(product.isLowStock());
        return dto;
    }

    // Update existing entity from request DTO (used on PUT)
    public void updateEntityFromDTO(ProductRequestDTO dto, Product product) {
        product.setName(dto.getName().trim());
        product.setCategory(dto.getCategory().trim());
        product.setPurchasePrice(dto.getPurchasePrice());
        product.setSalePrice(dto.getSalePrice());
        product.setStock(dto.getStock());
        product.setMinimumStock(dto.getMinimumStock());
    }
}
