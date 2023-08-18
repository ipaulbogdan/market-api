package com.idorasi.marketapi.converter;

import com.idorasi.marketapi.dto.TransactionDto;
import com.idorasi.marketapi.model.Product;
import com.idorasi.marketapi.model.Transaction;

public class TransactionConverter {

    public static TransactionDto convertFromEntity(Transaction transaction) {
        return TransactionDto.builder()
                .publicId(transaction.getPublicId())
                .product(ProductConverter.convertToDto(transaction.getProduct()))
                .quantity(transaction.getQuantity())
                .buyerUsername(transaction.getBuyerUsername())
                .sellerUsername(transaction.getSellerUsername())
                .build();
    }
}
