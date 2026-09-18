CREATE SEQUENCE IF NOT EXISTS products_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE products
(
    id          BIGINT           NOT NULL,
    name        VARCHAR(255)     NOT NULL,
    description VARCHAR(255)     NOT NULL,
    price       DOUBLE PRECISION NOT NULL,
    quantity    INTEGER          NOT NULL,
    enabled     BOOLEAN          NOT NULL,
    created_at  TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_products PRIMARY KEY (id)
);

ALTER TABLE order_items
    ADD created_at TIMESTAMP(6) WITHOUT TIME ZONE;

ALTER TABLE order_items
    ADD updated_at TIMESTAMP(6) WITHOUT TIME ZONE;

ALTER TABLE order_items
    ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE order_items
    ALTER COLUMN updated_at SET NOT NULL;

ALTER TABLE products
    ADD CONSTRAINT uc_products_name UNIQUE (name);