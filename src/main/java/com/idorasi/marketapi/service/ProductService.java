package com.idorasi.marketapi.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import com.idorasi.marketapi.client.ExchangeRateClient;
import com.idorasi.marketapi.converter.ProductConverter;
import com.idorasi.marketapi.dto.ProductCreateDto;
import com.idorasi.marketapi.dto.ProductDto;
import com.idorasi.marketapi.dto.ProductUpdateDto;
import com.idorasi.marketapi.model.Price;
import com.idorasi.marketapi.model.Product;
import com.idorasi.marketapi.model.ProductType;
import com.idorasi.marketapi.model.RetiredProduct;
import com.idorasi.marketapi.repo.ProductRepository;
import com.idorasi.marketapi.repo.RetiredProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
@AllArgsConstructor
public class ProductService {

    private ProductRepository productRepository;
    private RetiredProductRepository retiredProductRepository;
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
        eurRateMap = exchangeRateClient.getRate(Currency.getInstance("EUR"));

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
}
