-- Test User (password: test123)
-- BCrypt hash for "test123": $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.KjTjJqUcyfV3wXqR5i
INSERT INTO users (id, email, password, full_name, phone, shipping_address) VALUES
('user-001', 'test@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.KjTjJqUcyfV3wXqR5i', 'Test User', '1234567890', 'Test Address')
ON DUPLICATE KEY UPDATE email = VALUES(email);

-- Categories
INSERT INTO categories (id, name, name_en, name_ru, icon) VALUES
('cat-001', '家居用品', 'Home Goods', 'Товары для дома', 'ic_home'),
('cat-002', '个人护理', 'Personal Care', 'Личная гигиена', 'ic_personal'),
('cat-003', '厨房用品', 'Kitchen', 'Кухонные принадлежности', 'ic_kitchen'),
('cat-004', '食品', 'Food', 'Еда', 'ic_food'),
('cat-005', '清洁用品', 'Cleaning', 'Чистящие средства', 'ic_cleaning'),
('cat-006', '电子产品', 'Electronics', 'Электроника', 'ic_electronics'),
('cat-007', '服装配饰', 'Clothing & Accessories', 'Одежда и аксессуары', 'ic_clothing'),
('cat-008', '运动健身', 'Sports & Fitness', 'Спорт и фитнес', 'ic_sports')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  name_en = VALUES(name_en),
  name_ru = VALUES(name_ru),
  icon = VALUES(icon);

-- Products
INSERT INTO products (id, name, name_en, name_ru, description, description_en, description_ru, price, image_url, category_id, sold_count, stock, featured) VALUES
('prod-001', '竹纤维毛巾', 'Bamboo Towel', 'Бамбуковое полотенце', 
 '采用优质竹纤维材质，柔软舒适，吸水性好，环保健康。', 
 'Made of premium bamboo fiber, soft and comfortable, excellent water absorption, eco-friendly.',
 'Изготовлено из премиального бамбукового волокна, мягкое и удобное, отличное водопоглощение, экологически чистое.',
 39.90, 'product_bamboo_towel.png', 'cat-001', 856, 200, true)

ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  name_en = VALUES(name_en),
  name_ru = VALUES(name_ru),
  description = VALUES(description),
  description_en = VALUES(description_en),
  description_ru = VALUES(description_ru),
  price = VALUES(price),
  image_url = VALUES(image_url),
  category_id = VALUES(category_id),
  sold_count = VALUES(sold_count),
  stock = VALUES(stock),
  featured = VALUES(featured);

INSERT INTO products (id, name, name_en, name_ru, description, description_en, description_ru, price, image_url, category_id, sold_count, stock, featured) VALUES
('prod-002', '电动牙刷', 'Electric Toothbrush', 'Электрическая зубная щетка',
 '高频声波震动，深层清洁牙齿，保护牙龈健康。',
 'High-frequency sonic vibration, deep cleaning, protects gum health.',
 'Высокочастотная звуковая вибрация, глубокая очистка, защита здоровья десен.',
 129.00, 'product_electric_toothbrush.png', 'cat-002', 423, 150, true)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  name_en = VALUES(name_en),
  name_ru = VALUES(name_ru),
  description = VALUES(description),
  description_en = VALUES(description_en),
  description_ru = VALUES(description_ru),
  price = VALUES(price),
  image_url = VALUES(image_url),
  category_id = VALUES(category_id),
  sold_count = VALUES(sold_count),
  stock = VALUES(stock),
  featured = VALUES(featured);

INSERT INTO products (id, name, name_en, name_ru, description, description_en, description_ru, price, image_url, category_id, sold_count, stock, featured) VALUES
('prod-003', '洗发水', 'Shampoo', 'Шампунь',
 '深层清洁配方，滋养秀发，令头发柔顺亮泽。',
 'Deep cleaning formula, nourishes hair, leaves hair silky and shiny.',
 'Формула глубокой очистки, питает волосы, делает их шелковистыми и блестящими.',
 49.90, 'product_shampoo.png', 'cat-002', 1256, 300, true)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  name_en = VALUES(name_en),
  name_ru = VALUES(name_ru),
  description = VALUES(description),
  description_en = VALUES(description_en),
  description_ru = VALUES(description_ru),
  price = VALUES(price),
  image_url = VALUES(image_url),
  category_id = VALUES(category_id),
  sold_count = VALUES(sold_count),
  stock = VALUES(stock),
  featured = VALUES(featured);

INSERT INTO products (id, name, name_en, name_ru, description, description_en, description_ru, price, image_url, category_id, sold_count, stock, featured) VALUES
('prod-004', '液体肥皂', 'Liquid Soap', 'Жидкое мыло',
 '温和配方，泡沫丰富，滋润手部肌肤。',
 'Gentle formula, rich foam, moisturizes hands.',
 'Нежная формула, обильная пена, увлажняет руки.',
 24.90, 'product_liquid_soap.png', 'cat-002', 678, 250, false)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  name_en = VALUES(name_en),
  name_ru = VALUES(name_ru),
  description = VALUES(description),
  description_en = VALUES(description_en),
  description_ru = VALUES(description_ru),
  price = VALUES(price),
  image_url = VALUES(image_url),
  category_id = VALUES(category_id),
  sold_count = VALUES(sold_count),
  stock = VALUES(stock),
  featured = VALUES(featured);

INSERT INTO products (id, name, name_en, name_ru, description, description_en, description_ru, price, image_url, category_id, sold_count, stock, featured) VALUES
('prod-005', '保温杯', 'Thermos', 'Термос',
 '不锈钢材质，24小时保温保冷，方便携带。',
 'Stainless steel, 24-hour temperature retention, portable.',
 'Нержавеющая сталь, сохранение температуры 24 часа, удобно носить.',
 99.00, 'product_thermos.png', 'cat-003', 534, 180, true)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  name_en = VALUES(name_en),
  name_ru = VALUES(name_ru),
  description = VALUES(description),
  description_en = VALUES(description_en),
  description_ru = VALUES(description_ru),
  price = VALUES(price),
  image_url = VALUES(image_url),
  category_id = VALUES(category_id),
  sold_count = VALUES(sold_count),
  stock = VALUES(stock),
  featured = VALUES(featured);

INSERT INTO products (id, name, name_en, name_ru, description, description_en, description_ru, price, image_url, category_id, sold_count, stock, featured) VALUES
('prod-006', '有机大米', 'Organic Rice', 'Органический рис',
 '有机种植，无农药残留，营养丰富。',
 'Organically grown, pesticide-free, nutritious.',
 'Органическое выращивание, без пестицидов, питательный.',
 69.90, 'product_organic_rice.png', 'cat-004', 892, 500, true)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  name_en = VALUES(name_en),
  name_ru = VALUES(name_ru),
  description = VALUES(description),
  description_en = VALUES(description_en),
  description_ru = VALUES(description_ru),
  price = VALUES(price),
  image_url = VALUES(image_url),
  category_id = VALUES(category_id),
  sold_count = VALUES(sold_count),
  stock = VALUES(stock),
  featured = VALUES(featured);

INSERT INTO products (id, name, name_en, name_ru, description, description_en, description_ru, price, image_url, category_id, sold_count, stock, featured) VALUES
('prod-007', '新鲜苹果', 'Fresh Apples', 'Свежие яблоки',
 '新鲜采摘，口感脆甜，富含维生素。',
 'Freshly picked, crispy and sweet, rich in vitamins.',
 'Свежесобранные, хрустящие и сладкие, богаты витаминами.',
 24.90, 'product_fresh_apples.png', 'cat-004', 2156, 1000, true)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  name_en = VALUES(name_en),
  name_ru = VALUES(name_ru),
  description = VALUES(description),
  description_en = VALUES(description_en),
  description_ru = VALUES(description_ru),
  price = VALUES(price),
  image_url = VALUES(image_url),
  category_id = VALUES(category_id),
  sold_count = VALUES(sold_count),
  stock = VALUES(stock),
  featured = VALUES(featured);

INSERT INTO products (id, name, name_en, name_ru, description, description_en, description_ru, price, image_url, category_id, sold_count, stock, featured) VALUES
('prod-008', '洗衣液', 'Laundry Detergent', 'Стиральный порошок',
 '强力去污，护色护衣，清香持久。',
 'Strong stain removal, color protection, long-lasting fragrance.',
 'Сильное удаление пятен, защита цвета, стойкий аромат.',
 69.90, 'product_laundry_detergent.png', 'cat-005', 1456, 350, false)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  name_en = VALUES(name_en),
  name_ru = VALUES(name_ru),
  description = VALUES(description),
  description_en = VALUES(description_en),
  description_ru = VALUES(description_ru),
  price = VALUES(price),
  image_url = VALUES(image_url),
  category_id = VALUES(category_id),
  sold_count = VALUES(sold_count),
  stock = VALUES(stock),
  featured = VALUES(featured);
