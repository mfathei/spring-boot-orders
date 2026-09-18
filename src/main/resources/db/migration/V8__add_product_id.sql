ALTER TABLE order_items
    ADD product_id BIGINT;

ALTER TABLE order_items
    ALTER COLUMN product_id SET NOT NULL;

ALTER TABLE order_items
    ADD CONSTRAINT uc_e4a0645d1bcf60087b8f0bf27 UNIQUE (order_id, product_id);