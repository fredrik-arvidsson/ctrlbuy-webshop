-- MySQL dump 10.13  Distrib 5.7.24, for osx10.9 (x86_64)
--
-- Host: localhost    Database: webshop
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cart_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cart_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `quantity` int NOT NULL DEFAULT '1',
  `unit_price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cart_product` (`cart_id`,`product_id`),
  KEY `idx_cart_item_cart_id` (`cart_id`),
  KEY `idx_cart_item_product_id` (`product_id`),
  CONSTRAINT `fk_cart_item_cart` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_cart_item_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_quantity_positive` CHECK ((`quantity` > 0)),
  CONSTRAINT `chk_unit_price_positive` CHECK ((`unit_price` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Cart items - products in shopping carts';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_items`
--

LOCK TABLES `cart_items` WRITE;
/*!40000 ALTER TABLE `cart_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `carts`
--

DROP TABLE IF EXISTS `carts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `carts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `session_id` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKb5o626f86h46m4s7ms6ginnop` (`user_id`),
  CONSTRAINT `FKb5o626f86h46m4s7ms6ginnop` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carts`
--

LOCK TABLES `carts` WRITE;
/*!40000 ALTER TABLE `carts` DISABLE KEYS */;
/*!40000 ALTER TABLE `carts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categories` (
  `category_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `id` bigint NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=91 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'Mobiles',0,NULL),(2,'Computers',0,NULL),(3,'Accessories',0,NULL),(4,'Tablets',0,NULL),(5,'Smartwatches',0,NULL),(6,'Headphones',0,NULL),(7,'Cameras',0,NULL),(8,'Printers',0,NULL),(9,'Monitors',0,NULL),(10,'Keyboards',0,NULL),(11,'Mice',0,NULL),(12,'Speakers',0,NULL),(13,'Routers',0,NULL),(14,'Projectors',0,NULL),(15,'Scanners',0,NULL),(16,'External Hard Drives',0,NULL),(17,'USB Flash Drives',0,NULL),(18,'Memory Cards',0,NULL),(19,'Webcams',0,NULL),(20,'Smart Home Devices',0,NULL),(21,'Fitness Trackers',0,NULL),(22,'Drones',0,NULL),(23,'VR Headsets',0,NULL),(24,'Gaming Consoles',0,NULL),(25,'Smart Glasses',0,NULL),(26,'E-Readers',0,NULL),(27,'Portable Chargers',0,NULL),(28,'Bluetooth Trackers',0,NULL),(29,'Docking Stations',0,NULL),(30,'Graphics Cards',0,NULL);
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_entity`
--

DROP TABLE IF EXISTS `customer_entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer_entity` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_entity`
--

LOCK TABLES `customer_entity` WRITE;
/*!40000 ALTER TABLE `customer_entity` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer_entity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customers` (
  `customer_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (1,'Alice Johnson','updatedemail1@example.com','2025-02-02 12:05:25'),(2,'Bob Smith','bob@example.com','2025-02-02 12:05:25'),(3,'Charlie Brown','charlie@example.com','2025-02-02 12:05:25'),(4,'David Harris','david@example.com','2025-02-02 12:05:25'),(5,'Emma Wilson','emma@example.com','2025-02-02 12:05:25'),(6,'Frank Thomas','frank@example.com','2025-02-02 12:05:25'),(7,'Grace Lee','grace@example.com','2025-02-02 12:05:25'),(8,'Henry Martin','henry@example.com','2025-02-02 12:05:25'),(9,'Irene King','irene@example.com','2025-02-02 12:05:25'),(10,'Jack Turner','jack@example.com','2025-02-02 12:05:25'),(11,'Karen Hill','karen@example.com','2025-02-02 12:05:25'),(12,'Leo Scott','leo@example.com','2025-02-02 12:05:25'),(13,'Mia Green','mia@example.com','2025-02-02 12:05:25'),(14,'Noah Lewis','noah@example.com','2025-02-02 12:05:25'),(15,'Olivia Adams','olivia@example.com','2025-02-02 12:05:25'),(16,'Peter Nelson','peter@example.com','2025-02-02 12:05:25'),(17,'Quinn Baker','quinn@example.com','2025-02-02 12:05:25'),(18,'Rachel Carter','rachel@example.com','2025-02-02 12:05:25'),(19,'Sam Foster','sam@example.com','2025-02-02 12:05:25'),(20,'Tina Roberts','tina@example.com','2025-02-02 12:05:25'),(21,'Uma Johnson','uma@example.com','2025-02-02 12:05:25'),(22,'Victor Clark','victor@example.com','2025-02-02 12:05:25'),(23,'Wendy Mitchell','wendy@example.com','2025-02-02 12:05:25'),(24,'Xander Allen','xander@example.com','2025-02-02 12:05:25'),(25,'Yara Perez','yara@example.com','2025-02-02 12:05:25'),(26,'Zane Collins','zane@example.com','2025-02-02 12:05:25'),(27,'Abby Wright','updatedemail27@example.com','2025-02-02 12:05:25'),(28,'Brian Evans','brian@example.com','2025-02-02 12:05:25'),(29,'Carly Walker','carly@example.com','2025-02-02 12:05:25'),(30,'Derek Young','derek@example.com','2025-02-02 12:05:25'),(31,'Ella Hall','ella@example.com','2025-02-02 12:05:25'),(32,'Finn Hill','finn@example.com','2025-02-02 12:05:25'),(33,'Gina Lopez','gina@example.com','2025-02-02 12:05:25'),(34,'Harry Kelly','harry@example.com','2025-02-02 12:05:25'),(35,'Ivy Miller','ivy@example.com','2025-02-02 12:05:25'),(36,'John Rivera','john@example.com','2025-02-02 12:05:25'),(37,'Kara Sanchez','kara@example.com','2025-02-02 12:05:25'),(38,'Luke Peterson','luke@example.com','2025-02-02 12:05:25'),(39,'Molly Reed','molly@example.com','2025-02-02 12:05:25'),(40,'Nina Hughes','nina@example.com','2025-02-02 12:05:25'),(41,'Oscar Price','oscar@example.com','2025-02-02 12:05:25'),(42,'Paula Sanders','paula@example.com','2025-02-02 12:05:25'),(43,'Quentin Butler','quentin@example.com','2025-02-02 12:05:25'),(44,'Rita Edwards','rita@example.com','2025-02-02 12:05:25'),(45,'Steve Patterson','steve@example.com','2025-02-02 12:05:25'),(46,'Tara Bell','tara@example.com','2025-02-02 12:05:25'),(47,'Ulysses Moore','ulysses@example.com','2025-02-02 12:05:25'),(48,'Vera Collins','vera@example.com','2025-02-02 12:05:25'),(49,'Will Harris','will@example.com','2025-02-02 12:05:25'),(50,'New Customer','newcustomer@example.com','2025-03-12 16:56:18');
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `manufacturers`
--

DROP TABLE IF EXISTS `manufacturers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `manufacturers` (
  `manufacturer_id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  PRIMARY KEY (`manufacturer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `manufacturers`
--

LOCK TABLES `manufacturers` WRITE;
/*!40000 ALTER TABLE `manufacturers` DISABLE KEYS */;
INSERT INTO `manufacturers` VALUES (1,'Apple'),(2,'Samsung'),(3,'Dell'),(4,'Sony'),(5,'HP'),(6,'Lenovo'),(7,'Asus'),(8,'Acer'),(9,'Microsoft'),(10,'Google'),(11,'Huawei'),(12,'LG'),(13,'Toshiba'),(14,'Panasonic'),(15,'IBM'),(16,'Fujitsu'),(17,'Sharp'),(18,'Nokia'),(19,'Motorola'),(20,'HTC');
/*!40000 ALTER TABLE `manufacturers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `price` double NOT NULL,
  `product_name` varchar(255) NOT NULL,
  `quantity` int NOT NULL,
  `order_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt4dc2r9nbvbujrljv3e23iibt` (`order_id`),
  CONSTRAINT `FKt4dc2r9nbvbujrljv3e23iibt` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item_entity`
--

DROP TABLE IF EXISTS `order_item_entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_item_entity` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `price` double NOT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `quantity` int NOT NULL,
  `order_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKedfj2cef8raynpggckkrx4cpr` (`order_id`),
  CONSTRAINT `FKedfj2cef8raynpggckkrx4cpr` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item_entity`
--

LOCK TABLES `order_item_entity` WRITE;
/*!40000 ALTER TABLE `order_item_entity` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_item_entity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `price` double NOT NULL,
  `product_name` varchar(255) NOT NULL,
  `quantity` int NOT NULL,
  `order_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbioxgbv59vetrxe0ejfubep1w` (`order_id`),
  KEY `FKocimc7dtr037rh4ls4l95nlfi` (`product_id`),
  CONSTRAINT `FKbioxgbv59vetrxe0ejfubep1w` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `FKocimc7dtr037rh4ls4l95nlfi` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (45,13499,'Samsung Galaxy S24 Ultra',1,19,2),(46,15999,'MacBook Air M3',1,19,6),(47,13499,'Samsung Galaxy S24 Ultra',1,20,2),(48,11999,'Google Pixel 8 Pro',1,20,3),(49,14999,'iPhone 15 Pro',1,21,1),(50,11999,'Google Pixel 8 Pro',1,21,3),(51,14999,'iPhone 15 Pro',1,22,1),(52,15999,'MacBook Air M3',1,22,6),(53,13499,'Samsung Galaxy S24 Ultra',1,23,2),(54,11999,'Google Pixel 8 Pro',1,23,3),(55,13499,'Samsung Galaxy S24 Ultra',1,24,2),(56,15999,'MacBook Air M3',1,24,6),(57,9499,'Samsung Galaxy S23',1,24,5),(58,11999,'Google Pixel 8 Pro',2,26,3),(59,14999,'iPhone 15 Pro',1,27,1),(60,15999,'MacBook Air M3',1,27,6),(61,13499,'Samsung Galaxy S24 Ultra',1,28,2),(62,11999,'Google Pixel 8 Pro',1,28,3),(63,15999,'MacBook Air M3',1,29,6),(64,14999,'iPhone 15 Pro',1,29,1),(65,13499,'Samsung Galaxy S24 Ultra',1,30,2),(66,11999,'Google Pixel 8 Pro',1,30,3),(67,15999,'MacBook Air M3',1,30,6),(68,13499,'Samsung Galaxy S24 Ultra',2,31,2),(69,9499,'Samsung Galaxy S23',1,31,5),(70,14999,'iPhone 15 Pro',1,32,1),(71,11999,'Google Pixel 8 Pro',1,32,3),(72,15999,'MacBook Air M3',1,32,6),(73,11999,'Google Pixel 8 Pro',1,33,3),(74,13499,'Samsung Galaxy S24 Ultra',1,33,2);
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `delivery_address` varchar(255) NOT NULL,
  `delivery_city` varchar(255) NOT NULL,
  `delivery_name` varchar(255) NOT NULL,
  `delivery_phone` varchar(255) DEFAULT NULL,
  `delivery_postal_code` varchar(255) NOT NULL,
  `order_date` datetime(6) NOT NULL,
  `order_number` varchar(255) NOT NULL,
  `payment_method` varchar(255) DEFAULT NULL,
  `payment_transaction_id` varchar(255) DEFAULT NULL,
  `status` enum('CANCELLED','CONFIRMED','DELIVERED','PENDING','PROCESSING','SHIPPED') NOT NULL,
  `total_amount` double NOT NULL,
  `user_id` bigint DEFAULT NULL,
  `payment_status` enum('CANCELLED','COMPLETED','FAILED','PARTIALLY_REFUNDED','PENDING','PROCESSING','REFUNDED') DEFAULT NULL,
  `refunded_at` datetime(6) DEFAULT NULL,
  `transaction_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKnthkiu7pgmnqnu86i2jyoe2v7` (`order_number`),
  KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`),
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (19,'Sankt Paulsgatan 8','Stockholm','Fredrik Arvidsson','0725544417','11846','2025-06-11 00:24:43.639567','CB001','INVOICE',NULL,'PENDING',29498,13,'PENDING',NULL,NULL),(20,'Sankt Paulsgatan 8','Stockholm','Fredrik Arvidsson','0725544417','11846','2025-06-11 09:16:37.261616','CB002','INVOICE',NULL,'PENDING',25498,13,'PENDING',NULL,NULL),(21,'Sankt Paulsgatan 8','Stockholm','Fredrik Arvidsson','0725544417','11846','2025-06-11 13:00:06.701956','CB003','INVOICE',NULL,'PENDING',26998,13,'PENDING',NULL,NULL),(22,'Sankt Paulsgatan 8','Stockholm','Fredrik Arvidsson','0725544417','11846','2025-06-11 13:10:37.473225','CB004','INVOICE',NULL,'PENDING',30998,13,'PENDING',NULL,NULL),(23,'Sankt Paulsgatan 8','Stockholm','Fredrik Arvidsson','0725544417','11846','2025-06-11 15:50:51.894037','CB005','INVOICE',NULL,'PENDING',25498,13,'PENDING',NULL,NULL),(24,'Sankt Paulsgatan 8','Stockholm','Fredrik Arvidsson','0725544417','11846','2025-06-12 15:33:27.482980','CB006','INVOICE',NULL,'PENDING',38997,13,'PENDING',NULL,NULL),(26,'Sankt Paulsgatan 8','Stockholm','Fredrik Arvidsson','0725544417','11846','2025-06-12 15:35:40.302409','CB007','INVOICE',NULL,'PENDING',23998,13,'PENDING',NULL,NULL),(27,'Sankt Paulsgatan 8','Stockholm','Fredrik Arvidsson','0725544417','11846','2025-06-16 02:26:23.573814','CB008','INVOICE',NULL,'PENDING',30998,13,'PENDING',NULL,NULL),(28,'Sankt Paulsgatan 8','Stockholm','Admin Användare','0725544417','11846','2025-06-16 10:03:43.661518','CB009','INVOICE',NULL,'PENDING',25498,24,'PENDING',NULL,NULL),(29,'Sankt Paulsgatan 8','Stockholm','Fredrik Arvidsson','0725544417','11846','2025-06-16 10:05:53.195720','CB010','INVOICE',NULL,'PENDING',30998,13,'PENDING',NULL,NULL),(30,'Sankt Paulsgatan 8','Stockholm','Fredrik Arvidsson','0725544417','11846','2025-06-16 10:42:09.676112','CB011','INVOICE',NULL,'PENDING',41497,13,'PENDING',NULL,NULL),(31,'Sankt Paulsgatan 8','Stockholm','Fredrik Arvidsson','0725544417','11846','2025-06-16 10:50:40.389257','CB012','INVOICE',NULL,'PENDING',36497,13,'PENDING',NULL,NULL),(32,'Sankt Paulsgatan 8','Stockholm','Fredrik Arvidsson','0725544417','11846','2025-06-16 15:40:41.648714','CB013','INVOICE',NULL,'PENDING',42997,13,'PENDING',NULL,NULL),(33,'Sankt Paulsgatan 8','Stockholm','Fredrik Arvidsson','0725544417','11846','2025-06-18 19:57:17.681895','CB014','INVOICE',NULL,'PENDING',25498,13,'PENDING',NULL,NULL);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders_backup`
--

DROP TABLE IF EXISTS `orders_backup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `orders_backup` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `customer_id` int NOT NULL,
  `order_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `total_amount` decimal(10,2) DEFAULT NULL,
  `shipment_id` int DEFAULT NULL,
  `delivery_address` text,
  `status` varchar(50) DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `customer_id` (`customer_id`),
  KEY `fk_shipment` (`shipment_id`),
  KEY `FK_orders_users` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders_backup`
--

LOCK TABLES `orders_backup` WRITE;
/*!40000 ALTER TABLE `orders_backup` DISABLE KEYS */;
INSERT INTO `orders_backup` VALUES (1,1,'2025-02-02 12:23:27',1099.98,NULL,NULL,NULL,NULL),(2,2,'2025-02-02 12:23:27',799.98,NULL,NULL,NULL,NULL),(3,1,'2025-02-02 12:23:27',49.99,NULL,NULL,NULL,NULL),(4,3,'2025-02-02 12:23:27',249.50,NULL,NULL,NULL,NULL),(5,4,'2025-02-02 12:23:27',459.99,NULL,NULL,NULL,NULL),(6,5,'2025-02-02 12:23:27',329.89,NULL,NULL,NULL,NULL),(7,6,'2025-02-02 12:23:27',150.75,NULL,NULL,NULL,NULL),(8,7,'2025-02-02 12:23:27',879.49,NULL,NULL,NULL,NULL),(9,8,'2025-02-02 12:23:27',620.00,NULL,NULL,NULL,NULL),(10,9,'2025-02-02 12:23:27',199.99,NULL,NULL,NULL,NULL),(11,10,'2025-02-02 12:23:27',999.99,NULL,NULL,NULL,NULL),(12,11,'2025-02-02 12:23:27',189.99,NULL,NULL,NULL,NULL),(13,12,'2025-02-02 12:23:27',79.99,NULL,NULL,NULL,NULL),(14,13,'2025-02-02 12:23:27',459.99,NULL,NULL,NULL,NULL),(15,14,'2025-02-02 12:23:27',229.99,NULL,NULL,NULL,NULL),(16,15,'2025-02-02 12:23:27',89.99,NULL,NULL,NULL,NULL),(17,16,'2025-02-02 12:23:27',299.99,NULL,NULL,NULL,NULL),(18,17,'2025-02-02 12:23:27',139.99,NULL,NULL,NULL,NULL),(19,18,'2025-02-02 12:23:27',519.99,NULL,NULL,NULL,NULL),(20,19,'2025-02-02 12:23:27',109.99,NULL,NULL,NULL,NULL),(21,20,'2025-02-02 12:23:27',649.99,NULL,NULL,NULL,NULL),(22,21,'2025-02-02 12:23:27',250.00,NULL,NULL,NULL,NULL),(23,22,'2025-02-02 12:23:27',719.99,NULL,NULL,NULL,NULL),(24,23,'2025-02-02 12:23:27',799.99,NULL,NULL,NULL,NULL),(25,24,'2025-02-02 12:23:27',929.99,NULL,NULL,NULL,NULL),(26,25,'2025-02-02 12:23:27',350.00,NULL,NULL,NULL,NULL),(27,26,'2025-02-02 12:23:27',119.99,NULL,NULL,NULL,NULL),(28,27,'2025-02-02 12:23:27',320.00,NULL,NULL,NULL,NULL),(29,28,'2025-02-02 12:23:27',89.99,NULL,NULL,NULL,NULL),(30,29,'2025-02-02 12:23:27',150.00,NULL,NULL,NULL,NULL),(31,30,'2025-02-02 12:23:27',99.99,NULL,NULL,NULL,NULL),(32,31,'2025-02-02 12:23:27',549.99,NULL,NULL,NULL,NULL),(33,32,'2025-02-02 12:23:27',449.99,NULL,NULL,NULL,NULL),(34,33,'2025-02-02 12:23:27',619.99,NULL,NULL,NULL,NULL),(35,34,'2025-02-02 12:23:27',199.99,NULL,NULL,NULL,NULL),(36,35,'2025-02-02 12:23:27',300.00,NULL,NULL,NULL,NULL),(37,36,'2025-02-02 12:23:27',750.00,NULL,NULL,NULL,NULL),(38,37,'2025-02-02 12:23:27',400.00,NULL,NULL,NULL,NULL),(39,38,'2025-02-02 12:23:27',650.00,NULL,NULL,NULL,NULL),(40,39,'2025-02-02 12:23:27',380.00,NULL,NULL,NULL,NULL),(41,40,'2025-02-02 12:23:27',500.00,NULL,NULL,NULL,NULL),(42,1,'2025-02-02 23:00:00',NULL,1,'123 Main St','New',NULL);
/*!40000 ALTER TABLE `orders_backup` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders_products`
--

DROP TABLE IF EXISTS `orders_products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `orders_products` (
  `product_id` bigint DEFAULT NULL,
  `orders_id` bigint DEFAULT NULL,
  `order_id` bigint NOT NULL,
  KEY `FK43vke5jd6eyasd92t3k24kdxq` (`product_id`),
  KEY `FKe4y1sseio787e4o5hrml7omt5` (`order_id`),
  CONSTRAINT `FK43vke5jd6eyasd92t3k24kdxq` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FKe4y1sseio787e4o5hrml7omt5` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders_products`
--

LOCK TABLES `orders_products` WRITE;
/*!40000 ALTER TABLE `orders_products` DISABLE KEYS */;
/*!40000 ALTER TABLE `orders_products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(10,2) NOT NULL,
  `authorization_code` varchar(50) DEFAULT NULL,
  `card_type` varchar(20) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `failure_reason` varchar(500) DEFAULT NULL,
  `gateway_response` varchar(1000) DEFAULT NULL,
  `gateway_transaction_id` varchar(100) DEFAULT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `masked_card_number` varchar(20) DEFAULT NULL,
  `merchant_reference` varchar(100) DEFAULT NULL,
  `processed_at` datetime(6) DEFAULT NULL,
  `status` enum('CANCELLED','COMPLETED','FAILED','PARTIALLY_REFUNDED','PENDING','PROCESSING','REFUNDED') NOT NULL,
  `transaction_id` varchar(100) DEFAULT NULL,
  `type` enum('BANK_TRANSFER','CASH','CREDIT_CARD','CRYPTOCURRENCY','DEBIT_CARD','GIFT_CARD','MOBILE_PAYMENT','PAYPAL','REFUND','STRIPE') DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `user_agent` varchar(500) DEFAULT NULL,
  `order_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK81gagumt0r8y3rmudcgpbk42l` (`order_id`),
  CONSTRAINT `FK81gagumt0r8y3rmudcgpbk42l` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `price` double NOT NULL,
  `stock` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `is_active` bit(1) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `category_id` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(1000) DEFAULT NULL,
  `is_featured` bit(1) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `manufacturer_id` int DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `sale_price` decimal(10,2) DEFAULT NULL,
  `stock_quantity` int DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `view_count` int DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,_binary '','Smartphones',1,NULL,'Senaste iPhone med titanium design och A17 Pro chip',_binary '','/images/products/iphone15promax.jpg',1,'iPhone 15 Pro',14999.00,NULL,12,'2025-06-15 21:17:01.000000',45),(2,_binary '','Smartphones',1,NULL,'Flaggskipp Android telefon med S Pen',_binary '','/images/samsung-s24-ultra.jpg',1,'Samsung Galaxy S24 Ultra',13499.00,NULL,8,NULL,38),(3,_binary '','Smartphones',1,NULL,'Google flagship med AI-funktioner',_binary '','/images/google-pixel.jpg',1,'Google Pixel 8 Pro',11999.00,NULL,10,NULL,23),(4,_binary '','Smartphones',1,NULL,'Populär iPhone modell',_binary '','/images/iphone-14.jpg',1,'iPhone 14',10999.00,NULL,15,NULL,67),(5,_binary '','Smartphones',1,NULL,'Kraftfull Android smartphone',_binary '','/images/samsung-s23.jpg',1,'Samsung Galaxy S23',9499.00,NULL,18,NULL,34),(6,_binary '','Laptops',2,NULL,'Ultratunna laptop med M3 chip',_binary '','/images/products/macbookairm3.jpg',1,'MacBook Air M3',15999.00,NULL,5,NULL,67),(7,_binary '','Laptops',2,NULL,'Premium Windows laptop',_binary '','/images/products/dellxps13.jpg',1,'Dell XPS 13',11999.00,NULL,7,NULL,23),(8,_binary '','Laptops',2,NULL,'Professionell laptop för krävande arbete',_binary '','/images/products/macbookpro14.jpg',1,'MacBook Pro 14\"',24999.00,NULL,4,NULL,89),(9,_binary '','Laptops',2,NULL,'Affärslaptop med robust design',_binary '','/images/products/thinkpadx1.jpg',1,'Lenovo ThinkPad X1',16999.00,NULL,6,NULL,45),(10,_binary '','Laptops',2,NULL,'Convertible laptop med touchscreen',_binary '','/images/products/hpspectrex360.jpg',1,'HP Spectre x360',13999.00,NULL,8,NULL,56),(11,_binary '','Laptops',2,NULL,'Gaming laptop med RTX 4070',_binary '','/images/asus-rog-strix-gaming.jpg',1,'ASUS ROG Strix Gaming',18999.00,NULL,5,NULL,123),(12,_binary '','Laptops',2,NULL,'Elegant Windows laptop',_binary '','/images/products/surfacelaptop.jpg',1,'Microsoft Surface Laptop',12999.00,NULL,9,NULL,34),(13,_binary '','Tablets',3,NULL,'Professionell surfplatta med M2 chip',_binary '','/images/ipad-pro.jpg',1,'iPad Pro 12.9\"',12999.00,NULL,10,NULL,34),(14,_binary '','Tablets',3,NULL,'Kraftfull surfplatta för vardagsanvändning',_binary '','/images/ipad-air.jpg',1,'iPad Air',7999.00,NULL,15,NULL,78),(15,_binary '','Tablets',3,NULL,'Premium Android surfplatta',_binary '','/images/samsung-tab.jpg',1,'Samsung Galaxy Tab S9',8999.00,NULL,12,NULL,45),(16,_binary '','Tablets',3,NULL,'Kompakt surfplatta',_binary '','/images/ipad-mini.jpg',1,'iPad Mini',5999.00,NULL,20,NULL,56),(17,_binary '','Tablets',3,NULL,'Laptop-ersättande surfplatta',_binary '','/images/surface-pro.jpg',1,'Microsoft Surface Pro 9',11999.00,NULL,8,NULL,67),(18,_binary '','Gaming',4,NULL,'Senaste generationens spelkonsol från Sony',_binary '','/images/products/playstation5.jpg',1,'PlayStation 5',5999.00,NULL,6,NULL,234),(19,_binary '','Gaming',4,NULL,'Kraftfull spelkonsol från Microsoft',_binary '','/images/products/xboxseriesx.jpg',1,'Xbox Series X',5799.00,NULL,8,NULL,189),(20,_binary '','Gaming',4,NULL,'Bärbar spelkonsol med OLED skärm',_binary '','/images/products/nintendoswitcholed.jpg',1,'Nintendo Switch OLED',3499.00,NULL,15,NULL,156),(21,_binary '','Gaming',4,NULL,'Bärbar PC gaming handheld',_binary '','/images/steam-deck.jpg',1,'Steam Deck',4999.00,NULL,7,NULL,123),(22,_binary '','Gaming',4,NULL,'Kompakt next-gen spelkonsol',_binary '','/images/xbox-series-s.jpg',1,'Xbox Series S',3299.00,NULL,12,NULL,167),(23,_binary '','Hörlurar',5,NULL,'Trådlösa hörlurar med brusreducering',_binary '','/images/products/airpodspro2.jpg',1,'AirPods Pro 2',2999.00,NULL,25,NULL,167),(24,_binary '','Hörlurar',5,NULL,'Premium brusreducerande hörlurar',_binary '','/images/products/sonywh1000xm5.jpg',1,'Sony WH-1000XM5',3799.00,NULL,12,NULL,89),(25,_binary '','Hörlurar',5,NULL,'Komfortabla brusreducerande hörlurar',_binary '','/images/bose-qc45.jpg',1,'Bose QuietComfort 45',3299.00,NULL,15,NULL,123),(26,_binary '','Hörlurar',5,NULL,'Audiofil-kvalitet hörlurar',_binary '','/images/sennheiser-momentum.jpg',1,'Sennheiser Momentum 4',3599.00,NULL,10,NULL,67),(27,_binary '','Hörlurar',5,NULL,'Trådlösa hörlurar med spatial audio',_binary '','/images/products/airpods3.jpg',1,'AirPods 3',1999.00,NULL,30,NULL,234),(28,_binary '','Hörlurar',5,NULL,'Gaming headset med surround sound',_binary '','/images/steelseries-arctis.jpg',1,'SteelSeries Arctis 7P',1599.00,NULL,18,NULL,156),(29,_binary '','Smartklockor',6,NULL,'Smartklocka med avancerade hälsofunktioner',_binary '','/images/apple-watch.jpg',1,'Apple Watch Series 9',4999.00,NULL,18,NULL,123),(30,_binary '','Smartklockor',6,NULL,'Android smartklocka med hälsoövervakning',_binary '','/images/apple-watch.jpg',1,'Samsung Galaxy Watch 6',3299.00,NULL,14,NULL,67),(31,_binary '','Smartklockor',6,NULL,'GPS smartklocka för utomhusaktiviteter',_binary '','/images/garmin-watch.jpg',1,'Garmin Fenix 7',6999.00,NULL,8,NULL,89),(32,_binary '','Smartklockor',6,NULL,'Prisvärd smartklocka från Apple',_binary '','/images/apple-watch-se.jpg',1,'Apple Watch SE',2999.00,NULL,25,NULL,156),(33,_binary '','Smartklockor',6,NULL,'Fitness-fokuserad smartklocka',_binary '','/images/fitbit-versa.jpg',1,'Fitbit Versa 4',2299.00,NULL,20,NULL,134),(34,_binary '','Electronics',7,NULL,'4K Smart TV med Quantum Dot teknologi',_binary '','/images/samsung-tv.jpg',1,'Samsung 65\" QLED TV',16999.00,NULL,4,NULL,89),(35,_binary '','Electronics',7,NULL,'Premium OLED TV med fantastisk bildkvalitet',_binary '','/images/sony-tv.jpg',1,'Sony 55\" OLED TV',18999.00,NULL,3,NULL,76),(36,_binary '','Electronics',7,NULL,'Professionell 4K bildskärm',_binary '','/images/lg-monitor.jpg',1,'LG 27\" 4K Monitor',4999.00,NULL,12,NULL,67),(37,_binary '','Electronics',7,NULL,'Stor 4K bildskärm för produktivitet',_binary '','/images/dell-monitor.jpg',1,'Dell UltraSharp 32\"',7999.00,NULL,8,NULL,45),(38,_binary '','Electronics',7,NULL,'144Hz gaming bildskärm',_binary '','/images/asus-monitor.jpg',1,'ASUS Gaming Monitor 24\"',2999.00,NULL,15,NULL,123),(39,_binary '','Electronics',7,NULL,'Ergonomisk trådlös mus för produktivitet',_binary '','/images/logitech-mouse.jpg',1,'Logitech MX Master 3S',1299.00,NULL,30,NULL,234),(40,_binary '','Electronics',7,NULL,'RGB mekaniskt tangentbord',_binary '','/images/gaming-keyboard.jpg',1,'Mechanical Gaming Keyboard',1899.00,NULL,25,NULL,189),(41,_binary '','Electronics',7,NULL,'Professionell mirrorless kamera',_binary '','/images/canon-camera.jpg',1,'Canon EOS R6 Mark II',26999.00,NULL,3,NULL,45),(42,_binary '','Electronics',7,NULL,'Kompakt drönare med 4K kamera',_binary '','/images/dji-drone.jpg',1,'DJI Mini 4 Pro',8999.00,NULL,8,NULL,56),(43,_binary '','Electronics',7,NULL,'HD webbkamera för videosamtal',_binary '','/images/webcam.jpg',1,'Webcam Logitech C920',899.00,NULL,40,NULL,167),(44,_binary '','Electronics',7,NULL,'Multifunktionell dockningsstation',_binary '','/images/usb-hub.jpg',1,'USB-C Hub',799.00,NULL,50,NULL,234),(45,_binary '','Electronics',7,NULL,'Snabb extern SSD för lagring',_binary '','/images/ssd-drive.jpg',1,'Portable SSD 1TB',1299.00,NULL,35,NULL,156),(46,_binary '','Electronics',7,NULL,'Trådlös laddningsplatta för smartphones',_binary '','/images/wireless-charger.jpg',1,'Wireless Charger',399.00,NULL,60,NULL,278),(47,_binary '','Electronics',7,NULL,'Portabel högtalare med kraftfullt ljud',_binary '','/images/bluetooth-speaker.jpg',1,'Bluetooth Speaker',1599.00,NULL,25,NULL,189),(48,_binary '','Electronics',7,NULL,'Snabb trådlös router för hemmanätverk',_binary '','/images/wifi-router.jpg',1,'Router WiFi 6',1999.00,NULL,15,NULL,123),(49,_binary '','Smartphones',NULL,'2025-06-15 20:59:38.000000','Apples senaste flaggskepp med A17 Pro-chip och titaniumdesign',_binary '','/images/iphone-15-pro-max.jpg',NULL,'iPhone 15 Pro Max',14995.00,13995.00,25,'2025-06-15 20:59:38.000000',0),(50,_binary '','Smartphones',NULL,'2025-06-15 20:59:38.000000','Premiumtelefon med S Pen och avancerad AI-fotografi',_binary '','/images/samsung-s24-ultra.jpg',NULL,'Samsung Galaxy S24 Ultra',13495.00,NULL,30,'2025-06-15 20:59:38.000000',0),(51,_binary '','Smartphones',NULL,'2025-06-15 20:59:38.000000','AI-driven fotografering och ren Android-upplevelse',_binary '\0','/images/google-pixel.jpg',NULL,'Google Pixel 8 Pro',10995.00,9995.00,20,'2025-06-15 20:59:38.000000',0),(52,_binary '','Smartphones',NULL,'2025-06-15 20:59:38.000000','Snabb laddning och högpresterande gaming-telefon',_binary '\0','/images/products/oneplus12.jpg',NULL,'OnePlus 12',8995.00,NULL,15,'2025-06-15 20:59:38.000000',0),(53,_binary '','Laptops',NULL,'2025-06-15 20:59:38.000000','Kraftfull och energieffektiv laptop för proffs och studenter',_binary '','/images/products/macbookairm3.jpg',NULL,'MacBook Air M3 15\"',18995.00,17995.00,12,'2025-06-15 20:59:38.000000',0),(54,_binary '','Laptops',NULL,'2025-06-15 20:59:38.000000','Ultrabook med premium-design och 4K-skärm',_binary '\0','/images/products/dellxps13.jpg',NULL,'Dell XPS 13 Plus',16995.00,NULL,8,'2025-06-15 20:59:38.000000',0),(55,_binary '','Laptops',NULL,'2025-06-15 20:59:38.000000','Gaming-laptop med RTX 4070 och RGB-belysning',_binary '','/images/asus-rog-g16.jpg',NULL,'ASUS ROG Strix G16',22995.00,21495.00,6,'2025-06-15 20:59:38.000000',0),(56,_binary '','Laptops',NULL,'2025-06-15 20:59:38.000000','Affärslaptop med militär hållbarhet',_binary '\0','/images/products/thinkpadx1.jpg',NULL,'Lenovo ThinkPad X1 Carbon',19995.00,NULL,10,'2025-06-15 20:59:38.000000',0),(57,_binary '','Hörlurar',NULL,'2025-06-15 20:59:38.000000','Trådlösa hörlurar med aktiv brusreducering',_binary '','/images/products/airpodspro2.jpg',NULL,'AirPods Pro 2',2995.00,2695.00,50,'2025-06-15 20:59:38.000000',0),(58,_binary '','Hörlurar',NULL,'2025-06-15 20:59:38.000000','Branschledande brusreducering över örat',_binary '','/images/products/sonywh1000xm5.jpg',NULL,'Sony WH-1000XM5',3995.00,NULL,35,'2025-06-15 20:59:38.000000',0);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `helpful_count` int DEFAULT NULL,
  `is_verified_purchase` bit(1) DEFAULT NULL,
  `rating` int NOT NULL,
  `review_text` text NOT NULL,
  `review_title` varchar(100) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpl51cejpw4gy5swfar8br9ngi` (`product_id`),
  KEY `FKcgy7qjc1r99dp117y9en6lxye` (`user_id`),
  CONSTRAINT `FKcgy7qjc1r99dp117y9en6lxye` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKpl51cejpw4gy5swfar8br9ngi` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `reviews_chk_1` CHECK (((`rating` >= 1) and (`rating` <= 5)))
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roles` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `test_entity`
--

DROP TABLE IF EXISTS `test_entity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `test_entity` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `field1` varchar(255) DEFAULT NULL,
  `field2` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `test_entity`
--

LOCK TABLES `test_entity` WRITE;
/*!40000 ALTER TABLE `test_entity` DISABLE KEYS */;
INSERT INTO `test_entity` VALUES (19,NULL,NULL,'kenneth'),(20,NULL,NULL,'kenneth'),(21,NULL,NULL,'1000'),(22,NULL,NULL,'1000'),(23,NULL,NULL,NULL),(24,NULL,NULL,NULL),(25,NULL,NULL,NULL),(26,NULL,NULL,NULL),(27,NULL,NULL,NULL),(28,NULL,NULL,'hej');
/*!40000 ALTER TABLE `test_entity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_roles` (
  `user_id` bigint NOT NULL,
  `role` varchar(255) DEFAULT NULL,
  KEY `FKhfh9dx7w3ubf1co1vdev94g3f` (`user_id`),
  CONSTRAINT `FKhfh9dx7w3ubf1co1vdev94g3f` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES (13,'USER'),(13,'ROLE_ADMIN'),(22,'USER'),(22,'ROLE_USER'),(24,'USER'),(24,'ROLE_ADMIN'),(25,'USER'),(25,'ROLE_ADMIN'),(29,'USER'),(30,'USER'),(30,'ROLE_ADMIN'),(29,'ROLE_ADMIN'),(29,'USER');
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `email_verified` bit(1) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `reset_token` varchar(255) DEFAULT NULL,
  `reset_token_expiry` datetime(6) DEFAULT NULL,
  `username` varchar(255) NOT NULL,
  `verification_token` varchar(255) DEFAULT NULL,
  `verification_token_expiry` datetime(6) DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (13,_binary '','fredrik.g.arvidsson@gmail.com',_binary '','Fredrik','Arvidsson','$2a$12$R9h/cIPz0gi.URNNX3kh2OPST9/PgBkqquzi.Ss7KIUgO2t0jWMUW',NULL,NULL,'fredrik',NULL,NULL,'2025-06-08 16:46:38.494256','2025-06-08 16:46:38.494256'),(22,_binary '','user@example.com',_binary '\0','Test','Användare','$2a$10$H/oWMiZ32sXxPKfMhCTbZ.89STtndgVEchw3Q7Rc6SYJgLiykNcry',NULL,NULL,'user',NULL,NULL,'2025-06-11 16:06:27.581479','2025-06-11 16:06:27.581479'),(24,_binary '','admin@ctrlbuy.com',_binary '','Admin','Användare','$2a$10$nN1Z32EgvGydNJsWhupDJeYjPVjrFhLIboxST/W4NFb8edMiixcpS',NULL,NULL,'admin',NULL,NULL,'2025-06-13 10:33:20.508589','2025-06-13 10:33:20.508589'),(25,_binary '','developer@ctrlbuy.com',_binary '','Developer','Admin','$2a$10$nN1Z32EgvGydNJsWhupDJeYjPVjrFhLIboxST/W4NFb8edMiixcpS',NULL,NULL,'developer',NULL,NULL,'2025-06-13 13:00:54.245481','2025-06-13 13:00:54.245481'),(29,_binary '','zarkow@yahoo.com',_binary '','Nils','Nisseson','$2a$10$N9qo8uLOickgx2ZrVzaeme.Q7.ltCPONOhO5.YTmGZZjUJZk7LKu',NULL,NULL,'nisse',NULL,'2025-06-19 20:00:39.335974','2025-06-18 20:00:39.336506','2025-06-18 20:00:39.336506'),(30,_binary '','superadmin@ctrlbuy.com',_binary '','Super','Admin','$2a$10$nN1Z32EgvGydNJsWhupDJeYjPVjrFhLIboxST/W4NFb8edMiixcpS',NULL,NULL,'superadmin',NULL,NULL,'2025-06-19 01:38:05.000000','2025-06-19 01:38:05.000000');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `verification_tokens`
--

DROP TABLE IF EXISTS `verification_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `verification_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `expiry_date` datetime(6) NOT NULL,
  `token` varchar(255) NOT NULL,
  `used` bit(1) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6q9nsb665s9f8qajm3j07kd1e` (`token`),
  UNIQUE KEY `UKdqp95ggn6gvm865km5muba2o5` (`user_id`),
  CONSTRAINT `FK54y8mqsnq1rtyf581sfmrbp4f` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `verification_tokens`
--

LOCK TABLES `verification_tokens` WRITE;
/*!40000 ALTER TABLE `verification_tokens` DISABLE KEYS */;
/*!40000 ALTER TABLE `verification_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `webshop`
--

DROP TABLE IF EXISTS `webshop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `webshop` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKgvkd4oyye75f9lw6d4s3pjjcy` (`email`),
  UNIQUE KEY `UKpmqrum8530x615uqrt51om5n5` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `webshop`
--

LOCK TABLES `webshop` WRITE;
/*!40000 ALTER TABLE `webshop` DISABLE KEYS */;
/*!40000 ALTER TABLE `webshop` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-24  8:59:53
