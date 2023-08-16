package com.idorasi.marketapi.dto;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import com.idorasi.marketapi.model.Price;
import lombok.Builder;

@Builder
public record ProductDto(UUID publicId,
                         String name,
                         String description,
                         String productType,
                         Set<Price> price,
                         LocalDate createdDate,
                         Long itemsInStock) {

}
