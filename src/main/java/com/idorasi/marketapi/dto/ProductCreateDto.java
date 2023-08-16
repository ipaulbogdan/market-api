package com.idorasi.marketapi.dto;

import com.idorasi.marketapi.model.Price;

public record ProductCreateDto(String name,
                               String description,
                               String type,
                               Price price,
                               Long itemsInStock) {
}
