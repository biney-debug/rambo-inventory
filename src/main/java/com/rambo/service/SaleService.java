package com.rambo.service;

import com.rambo.dto.SaleRequestDTO;
import com.rambo.dto.SaleResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SaleService {

    SaleResponseDTO register(SaleRequestDTO dto);

    SaleResponseDTO findById(Long id);

    Page<SaleResponseDTO> findAll(Pageable pageable);

    List<SaleResponseDTO> findByProduct(Long productId);

    void delete(Long id);
}
