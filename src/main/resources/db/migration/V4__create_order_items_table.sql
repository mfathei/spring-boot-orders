CREATE SEQUENCE IF NOT EXISTS order_items_seq START WITH 1 INCREMENT BY 50;

CREATE SEQUENCE IF NOT EXISTS revinfo_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE order_items
(
    id         BIGINT           NOT NULL,
    order_id   BIGINT           NOT NULL,
    product_id BIGINT           NOT NULL,
    quantity   DOUBLE PRECISION NOT NULL,
    price      DOUBLE PRECISION NOT NULL,
    CONSTRAINT pk_order_items PRIMARY KEY (id)
);

CREATE TABLE revchanges
(
    rev        BIGINT NOT NULL,
    entityname VARCHAR(255)
);

CREATE TABLE revinfo
(
    rev      BIGINT NOT NULL,
    revtstmp BIGINT,
    CONSTRAINT pk_revinfo PRIMARY KEY (rev)
);

ALTER TABLE order_items
    ADD CONSTRAINT uc_e4a0645d1bcf60087b8f0bf27 UNIQUE (order_id, product_id);

ALTER TABLE revchanges
    ADD CONSTRAINT fk_revchanges_on_default_tracking_modified_entities_changelog FOREIGN KEY (rev) REFERENCES revinfo (rev);