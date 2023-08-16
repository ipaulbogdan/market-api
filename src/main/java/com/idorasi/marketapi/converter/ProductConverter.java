package com.idorasi.marketapi.converter;

import java.util.Set;
import java.util.UUID;
import com.idorasi.marketapi.dto.ProductCreateDto;
import com.idorasi.marketapi.dto.ProductDto;
import com.idorasi.marketapi.model.Price;
import com.idorasi.marketapi.model.Product;
import com.idorasi.marketapi.model.ProductType;

public class ProductConverter {

    public static Product convertFromDto(ProductCreateDto productCreateDto) {
        return new Product()
                .setName(productCreateDto.name())
                .setPublicId(UUID.randomUUID())
                .setDescription(productCreateDto.description())
                .setPrice(productCreateDto.price())
                .setItemsInStock(productCreateDto.itemsInStock())
                .setType(ProductType.of(productCreateDto.type()));
    }

    public static ProductDto convertToDto(Product product) {
        return ProductDto.builder()
                .publicId(product.getPublicId())
                .name(product.getName())
                .description(product.getDescription())
                .price(Set.of(product.getPrice()))
                .createdDate(product.getCreatedDate())
                .itemsInStock(product.getItemsInStock())
                .productType(product.getType().getValue())
                .build();
    }

    public static ProductDto convertToDto(Product product, Set<Price> prices) {
        return ProductDto.builder()
                .publicId(product.getPublicId())
                .name(product.getName())
                .description(product.getDescription())
                .price(prices)
                .createdDate(product.getCreatedDate())
                .itemsInStock(product.getItemsInStock())
                .productType(product.getType().getValue())
                .build();
    }
}
