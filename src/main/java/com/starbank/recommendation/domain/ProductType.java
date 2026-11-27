package com.starbank.recommendation.domain;

/**
 * Тип банковского продукта. Значения совпадают со значениями колонки products.type во внешней БД.
 */
public enum ProductType {
    DEBIT,
    CREDIT,
    INVEST,
    SAVING
}
