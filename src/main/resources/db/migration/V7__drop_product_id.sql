ALTER TABLE order_items
    ADD CONSTRAINT uc_ea46f866edb61a96123703c00 UNIQUE (order_id);

ALTER TABLE order_items
DROP
COLUMN product_id;