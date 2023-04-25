package com.example.demo.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.example.demo.dto.request.ProductDTO;
import com.example.demo.dto.request.ProductSearchCriteriaDTO;

public interface IProductService {
	
	ProductDTO findById(Long id);

    List<ProductDTO> findAll();

    ResponseEntity<?> newProduct(ProductDTO productDto);

    void deleteById(Long id);

    List<ProductDTO> searchProduct(ProductSearchCriteriaDTO criteria);
    
   

}
