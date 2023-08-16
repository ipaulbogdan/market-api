package com.idorasi.marketapi.model;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;

import static java.lang.String.format;

@AllArgsConstructor
public enum ProductType {

    ITALIAN_PASTA("Italian pasta"),
    GALUSTE_BANATENE("Galuste banatene"),
    SPATZLE("Spatzle");

    private static final Map<String, ProductType> map;

    static {
        map = Arrays.stream(ProductType.values())
                .collect(Collectors.toMap(ProductType::getValue, Function.identity()));
    }

    private final String value;

    public String getValue() {
        return value;
    }

    public static ProductType of(String type) {
        if (map.containsKey(type)) {
            return map.get(type);
        } else {
            throw new IllegalArgumentException(format("Product type %s no supported", type));
        }
    }



}
