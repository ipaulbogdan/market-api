package com.idorasi.marketapi.controller;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import com.idorasi.marketapi.dto.ProductBuyDto;
import com.idorasi.marketapi.dto.ProductCreateDto;
import com.idorasi.marketapi.dto.ProductDto;
import com.idorasi.marketapi.dto.ProductUpdateDto;
import com.idorasi.marketapi.dto.TransactionDto;
import com.idorasi.marketapi.model.RetiredProduct;
import com.idorasi.marketapi.service.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class ProductController {

    private ProductService productService;

    @PostMapping("/api/products")
    public ProductDto post(@RequestBody ProductCreateDto productCreateDto) {
        return productService.createProduct(productCreateDto);
    }

    @PostMapping("/api/products/{publicId}/retire")
    public RetiredProduct retire(@PathVariable UUID publicId, @RequestParam(required = false) String reason) {
        return productService.retireProduct(publicId, reason);
    }

    @PostMapping("/api/products/{publicId}")
    public TransactionDto buy(@RequestBody ProductBuyDto productBuyDto, @PathVariable UUID publicId) {
        return productService.buyProduct(publicId, productBuyDto);
    }

    @PutMapping("/api/products/{publicId}")
    public ProductDto update(@RequestBody ProductUpdateDto productUpdateDto, @PathVariable UUID publicId) {
        return productService.updateProduct(publicId, productUpdateDto);
    }

    @GetMapping("/api/products")
    public List<ProductDto> listProducts(@RequestParam(required = false) Set<String> types,
                                         @RequestParam(defaultValue = "DESC") Sort.Direction sortOrder) {
        return productService.listProducts(types == null ? Collections.emptySet() : types, sortOrder);
    }

}
