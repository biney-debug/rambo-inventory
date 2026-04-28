package com.rambo.mapper;

import com.rambo.dto.SaleResponseDTO;
import com.rambo.entity.Sale;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaleMapper {

    private final ModelMapper modelMapper;

    // Entity -> Response DTO
    public SaleResponseDTO toResponseDTO(Sale sale) {
        SaleResponseDTO dto = modelMapper.map(sale, SaleResponseDTO.class);
        // Product comes from a lazy relationship, must be mapped manually
        dto.setProductId(sale.getProduct().getId());
        dto.setProductName(sale.getProduct().getName());
        return dto;
    }
}
