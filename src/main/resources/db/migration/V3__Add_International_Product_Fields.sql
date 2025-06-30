-- V3__Add_International_Product_Fields.sql
-- Lägger till internationalisering och leveransfält till products-tabellen

-- Lägg till nya kolumner för internationalisering
ALTER TABLE products 
ADD COLUMN origin_country VARCHAR(100) DEFAULT NULL,
ADD COLUMN is_domestic BOOLEAN DEFAULT FALSE,
ADD COLUMN estimated_delivery_days INT DEFAULT NULL,
ADD COLUMN shipping_weight DECIMAL(8,3) DEFAULT NULL,
ADD COLUMN customs_code VARCHAR(20) DEFAULT NULL,
ADD COLUMN requires_special_handling BOOLEAN DEFAULT FALSE;

-- Uppdatera befintliga produkter med svenska standardvärden
UPDATE products 
SET 
    origin_country = 'Sverige',
    is_domestic = TRUE,
    estimated_delivery_days = 1,
    requires_special_handling = FALSE
WHERE origin_country IS NULL;

-- Lägg till index för bättre prestanda
CREATE INDEX idx_products_origin_country ON products(origin_country);
CREATE INDEX idx_products_is_domestic ON products(is_domestic);
CREATE INDEX idx_products_delivery_days ON products(estimated_delivery_days);

-- Kommentarer för dokumentation
ALTER TABLE products 
MODIFY COLUMN origin_country VARCHAR(100) COMMENT 'Produktens ursprungsland',
MODIFY COLUMN is_domestic BOOLEAN COMMENT 'TRUE om produkten är svensk/domestisk',
MODIFY COLUMN estimated_delivery_days INT COMMENT 'Uppskattad leveranstid i arbetsdagar',
MODIFY COLUMN shipping_weight DECIMAL(8,3) COMMENT 'Fraktvikt i kilogram',
MODIFY COLUMN customs_code VARCHAR(20) COMMENT 'Tullkod för internationella leveranser',
MODIFY COLUMN requires_special_handling BOOLEAN COMMENT 'Kräver särskild hantering vid frakt';
