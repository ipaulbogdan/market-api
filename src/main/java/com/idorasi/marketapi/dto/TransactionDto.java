package com.idorasi.marketapi.dto;

import java.util.UUID;
import lombok.Builder;

@Builder
public record TransactionDto(
        UUID publicId,
        ProductDto product,
        Long quantity,
        String buyerUsername,
        String sellerUsername) {

}
