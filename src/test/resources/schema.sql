-- Схема выданной банком БД (используется только в тестах и для локального запуска без файла transaction.mv.db).
-- Приложение эти таблицы не создаёт и не изменяет: база подключена в режиме READ_ONLY.

CREATE TABLE IF NOT EXISTS users
(
    id         UUID PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL,
    first_name VARCHAR(50)  NOT NULL,
    last_name  VARCHAR(50)  NOT NULL
);

CREATE TABLE IF NOT EXISTS products
(
    id   UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20)  NOT NULL
);

CREATE TABLE IF NOT EXISTS transactions
(
    id         UUID PRIMARY KEY,
    user_id    UUID           NOT NULL,
    product_id UUID           NOT NULL,
    type       VARCHAR(20)    NOT NULL,
    amount     DECIMAL(20, 2) NOT NULL,
    CONSTRAINT fk_transactions_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_transactions_product FOREIGN KEY (product_id) REFERENCES products (id)
);

CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON transactions (user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_product_id ON transactions (product_id);
