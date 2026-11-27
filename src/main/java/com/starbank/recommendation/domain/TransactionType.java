package com.starbank.recommendation.domain;

/**
 * Тип операции по продукту. Значения совпадают со значениями колонки transactions.type во внешней БД.
 */
public enum TransactionType {
    DEPOSIT,
    WITHDRAW
}
