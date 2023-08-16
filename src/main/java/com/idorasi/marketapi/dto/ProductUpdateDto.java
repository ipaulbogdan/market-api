package com.idorasi.marketapi.dto;

import com.idorasi.marketapi.model.Price;

public record ProductUpdateDto(String name, String description, Price price, Long itemsInStock) {
}
