-- ═══════════════════════════════════════════════════════
--  RAMBO - PostgreSQL initialization script
--  Docker runs this already connected to rambo_db
-- ═══════════════════════════════════════════════════════

-- ─── TABLE: products ──────────────────────────────────────
CREATE TABLE IF NOT EXISTS products (
                                        id              BIGSERIAL       PRIMARY KEY,
                                        name            VARCHAR(150)    NOT NULL UNIQUE,
    category        VARCHAR(80)     NOT NULL,
    purchase_price  NUMERIC(10,2)   NOT NULL CHECK (purchase_price > 0),
    sale_price      NUMERIC(10,2)   NOT NULL CHECK (sale_price > 0),
    stock           INTEGER         NOT NULL DEFAULT 0 CHECK (stock >= 0),
    minimum_stock   INTEGER         NOT NULL DEFAULT 2 CHECK (minimum_stock >= 0),
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW()
    );

-- ─── TABLE: sales ─────────────────────────────────────────
CREATE TABLE IF NOT EXISTS sales (
                                     id              BIGSERIAL       PRIMARY KEY,
                                     product_id      BIGINT          NOT NULL REFERENCES products(id),
    quantity        INTEGER         NOT NULL CHECK (quantity > 0),
    unit_price      NUMERIC(10,2)   NOT NULL CHECK (unit_price > 0),
    unit_cost       NUMERIC(10,2)   NOT NULL CHECK (unit_cost > 0),
    total_amount    NUMERIC(12,2)   NOT NULL,
    profit          NUMERIC(12,2)   NOT NULL,
    customer        VARCHAR(150),
    notes           VARCHAR(300),
    sold_at         TIMESTAMP       NOT NULL DEFAULT NOW()
    );

-- ─── INDEXES ──────────────────────────────────────────────
CREATE INDEX IF NOT EXISTS idx_sales_product_id  ON sales(product_id);
CREATE INDEX IF NOT EXISTS idx_sales_sold_at     ON sales(sold_at DESC);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);

-- ─── SAMPLE DATA ──────────────────────────────────────────
INSERT INTO products (name, category, purchase_price, sale_price, stock, minimum_stock)
SELECT * FROM (VALUES
                   ('Toyota Hilux steering bar',  'Bars',     45.00, 75.0

