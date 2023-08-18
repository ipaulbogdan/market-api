package com.idorasi.marketapi.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import com.github.javafaker.Faker;
import com.idorasi.marketapi.client.ExchangeRateClient;
import com.idorasi.marketapi.converter.ProductConverter;
import com.idorasi.marketapi.converter.TransactionConverter;
import com.idorasi.marketapi.dto.ProductBuyDto;
import com.idorasi.marketapi.dto.ProductCreateDto;
import com.idorasi.marketapi.dto.ProductDto;
import com.idorasi.marketapi.dto.ProductUpdateDto;
import com.idorasi.marketapi.dto.TransactionDto;
import com.idorasi.marketapi.model.Price;
import com.idorasi.marketapi.model.Product;
import com.idorasi.marketapi.model.ProductType;
import com.idorasi.marketapi.model.RetiredProduct;
import com.idorasi.marketapi.model.Transaction;
import com.idorasi.marketapi.repo.ProductRepository;
import com.idorasi.marketapi.repo.RetiredProductRepository;
import com.idorasi.marketapi.repo.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;

@Service
@AllArgsConstructor
public class ProductService {

    private ProductRepository productRepository;
    private RetiredProductRepository retiredProductRepository;
    private TransactionRepository transactionRepository;
    private ExchangeRateClient exchangeRateClient;

    private Map<Currency, BigDecimal> eurRateMap;

    public ProductDto createProduct(ProductCreateDto productCreateDto) {
        var product = ProductConverter.convertFromDto(productCreateDto);

        return ProductConverter.convertToDto(productRepository.save(product));
    }

    public ProductDto updateProduct(UUID publicId, ProductUpdateDto productUpdateDto) {
        var product = productRepository.findProductByPublicId(publicId)
                .orElseThrow(() -> new EntityNotFoundException(format("Unable to find product with id %s", publicId)));

        if (productUpdateDto.name() != null) {
            product.setName(productUpdateDto.name());
        }

        if (productUpdateDto.description() != null) {
            product.setDescription(productUpdateDto.description());
        }

        if (productUpdateDto.itemsInStock() != null) {
            product.setItemsInStock(productUpdateDto.itemsInStock());
        }

        if (productUpdateDto.price() != null) {
            product.setPrice(productUpdateDto.price());
        }

        return ProductConverter.convertToDto(productRepository.save(product));
    }

    @Transactional
    public RetiredProduct retireProduct(UUID publicId, String reason) {
        var product = productRepository.findProductByPublicId(publicId)
                .orElseThrow(() -> new EntityNotFoundException(format("Unable to find product with id %s", publicId)));

        var retiredProduct = new RetiredProduct()
                .setPublicId(publicId)
                .setType(product.getType())
                .setName(product.getName())
                .setDescription(product.getDescription())
                .setRetirementReason(reason);

        var savedRetiredProduct = retiredProductRepository.save(retiredProduct);
        productRepository.delete(product);

        return savedRetiredProduct;
    }

    public List<ProductDto> listProducts(Set<String> types, Sort.Direction sortOrder) {
        updateExchangeRates();

        var enumTypesSet = types.stream()
                .map(ProductType::of)
                .collect(Collectors.toSet());

        var createdDateSort = Sort.by(sortOrder, "createdDate");

        var products = enumTypesSet.isEmpty()
                ? productRepository.findAll(createdDateSort)
                : productRepository.findAllByTypeIn(enumTypesSet, createdDateSort);

        return products.stream()
                .map(this::mapToDto)
                .toList();
    }

    private void updateExchangeRates() {
        eurRateMap = exchangeRateClient.getRate(Currency.getInstance("EUR"));
    }

    private ProductDto mapToDto(Product product) {
        Currency eurCurrency = Currency.getInstance("EUR");
        Currency usdCurrency = Currency.getInstance("USD");

        var eurRate = eurRateMap.get(product.getPrice().getCurrency());
        var eurPrice = new Price()
                .setCurrency(eurCurrency)
                .setValue(product.getPrice().getValue().divide(eurRate, MathContext.DECIMAL32));

        var usdRate = eurRateMap.get(usdCurrency);
        var usdPrice = new Price()
                .setCurrency(usdCurrency)
                .setValue(eurPrice.getValue().multiply(usdRate));

        return ProductConverter.convertToDto(product, Set.of(eurPrice, usdPrice));
    }

    @SneakyThrows
    @Transactional
    public TransactionDto buyProduct(UUID publicId, ProductBuyDto productBuyDto) {
        var product = productRepository.findProductByPublicId(publicId)
                .orElseThrow(() -> new EntityNotFoundException(format("Unable to find product with id %s", publicId)));

        validateAvailableStock(productBuyDto, product);

        var transaction = new Transaction()
                .setPublicId(UUID.randomUUID())
                .setProduct(product)
                .setQuantity(productBuyDto.quantity())
                .setBuyerUsername(new Faker().name().username())
                .setSellerUsername(new Faker().name().username());

        product.setItemsInStock(product.getItemsInStock() - productBuyDto.quantity());
        SECONDS.sleep(2);

        return TransactionConverter.convertFromEntity(transactionRepository.save(transaction));
    }

    private void validateAvailableStock(ProductBuyDto productBuyDto, Product product) {
        if (product.getItemsInStock().compareTo(productBuyDto.quantity()) < 0) {
            throw new IllegalArgumentException("Insufficient quantity available");
        }
    }
}
