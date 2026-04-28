package com.rambo.service;

import com.rambo.dto.SaleRequestDTO;
import com.rambo.dto.SaleResponseDTO;

import java.util.List;

public interface SaleService {

    SaleResponseDTO register(SaleRequestDTO dto);

    SaleResponseDTO findById(Long id);

    List<SaleResponseDTO> findAll();

    List<SaleResponseDTO> findByProduct(Long productId);
}
