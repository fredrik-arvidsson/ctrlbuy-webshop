DELETE FROM products;

INSERT INTO products (id, name, category, price, description, stock_quantity, image_url) VALUES
(1, 'iPhone 15 Pro Max', 'Smartphones', 14999.00, 'Den mest avancerade iPhone hittills', 25, '/images/products/iphone-15-pro-max.jpg'),
(2, 'iPhone 14', 'Smartphones', 12999.00, 'Populär iPhone med A15 Bionic', 30, '/images/products/iphone-14.jpg'),
(3, 'Samsung Galaxy S24 Ultra', 'Smartphones', 13999.00, 'Premium Android med S Pen', 18, '/images/products/samsung-s24-ultra.jpg'),
(4, 'Samsung Galaxy S23', 'Smartphones', 11999.00, 'Kraftfull Android smartphone', 22, '/images/products/samsung-s23.jpg'),
(5, 'Google Pixel 8 Pro', 'Smartphones', 10999.00, 'AI-driven kamera och ren Android', 15, '/images/products/google-pixel.jpg'),
(6, 'MacBook Pro 16 M3 Max', 'Laptops', 34999.00, 'Kraftfull laptop för professionella', 8, '/images/products/laptop.jpg'),
(7, 'MacBook Air M3', 'Laptops', 15999.00, 'Ultratunna laptop med M3 chip', 15, '/images/products/laptop.jpg'),
(8, 'Dell XPS 13', 'Laptops', 18999.00, 'Premium Windows laptop', 12, '/images/products/laptop.jpg'),
(9, 'ASUS ROG Strix Gaming', 'Laptops', 24999.00, 'Gaming laptop med RTX 4070', 6, '/images/products/asus-rog-strix-gaming.jpg'),
(10, 'ThinkPad X1 Carbon', 'Laptops', 22999.00, 'Affärslaptop med robust design', 10, '/images/products/laptop.jpg');

SELECT COUNT(*) as total_products FROM products;
