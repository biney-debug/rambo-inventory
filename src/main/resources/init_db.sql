-- ═══════════════════════════════════════════════════════
--  RAMBO - PostgreSQL initialization script
--  Run in pgAdmin or psql before starting the backend
-- ═══════════════════════════════════════════════════════

-- 1. Create the database (run while connected to 'postgres')
CREATE DATABASE rambo_db
    WITH ENCODING = 'UTF8'
    TEMPLATE = template0;

-- 2. Connect to rambo_db and run the rest
\c rambo_db;

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

-- ─── INDEXES for faster queries ───────────────────────────
CREATE INDEX IF NOT EXISTS idx_sales_product_id ON sales(product_id);
CREATE INDEX IF NOT EXISTS idx_sales_sold_at    ON sales(sold_at DESC);
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);

-- ─── SAMPLE DATA (optional — delete if not needed) ────────
INSERT INTO products (name, category, purchase_price, sale_price, stock, minimum_stock) VALUES
    ('Toyota Hilux steering bar',    'Bars',    45.00, 75.00,  8, 2),
    ('Hyundai H100 front spring',    'Springs', 80.00, 130.00, 4, 2),
    ('Universal steering pin',       'Pins',    12.00, 22.00, 20, 5),
    ('14mm rubber bushing',          'Bushings', 5.50, 10.00, 30, 8),
    ('Nissan Urvan brake pads',      'Brakes',  35.00, 58.00,  6, 2),
    ('Toyota oil filter',            'Filters',  8.00, 15.00, 15, 4);

-- ─── VERIFY ───────────────────────────────────────────────
SELECT 'Tables created successfully' AS status;
SELECT name, category, stock, sale_price FROM products ORDER BY name;

