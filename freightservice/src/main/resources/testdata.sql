
--
-- Dumping data for table `freight_model`
--
--运费模板号之后单数给你用作重量的，双数给我做件数的
LOCK TABLES `freight_model` WRITE;
/*!40000 ALTER TABLE `auth_privilege` DISABLE KEYS */;
INSERT INTO `freight_model` VALUES (1,1,'默认',1,0,1,'2020-11-26 10:02:35','2020-11-26 21:51:45');
INSERT INTO `freight_model` VALUES (2,1,'2233',0,1,1,'2020-11-26 10:02:35','2020-11-26 21:51:45');
INSERT INTO `freight_model` VALUES (3,3,'3333',0,0,1,'2020-11-26 10:02:35','2020-11-26 21:51:45');
INSERT INTO `freight_model` VALUES (4,1,'4444',0,0,1,'2020-11-26 10:02:35','2020-11-26 21:51:45');
INSERT INTO `freight_model` VALUES (5,1,'5566',0,0,1,'2020-11-26 10:02:35','2020-11-26 21:51:45');
UNLOCK TABLES;

LOCK TABLES `piece_freight_model` WRITE;
/*!40000 ALTER TABLE `auth_privilege` DISABLE KEYS */;
INSERT INTO `piece_freight_model` VALUES (1,2,2,1,1,2,1,'2020-11-26 10:02:35','2020-11-26 21:51:45');
INSERT INTO `piece_freight_model` VALUES (2,2,2,1,1,2,2,'2020-11-26 10:02:35','2020-11-26 21:51:45');
UNLOCK TABLES;

LOCK TABLES `weight_freight_model` WRITE;
/*!40000 ALTER TABLE `auth_privilege` DISABLE KEYS */;
INSERT INTO `weight_freight_model` VALUES (1,1,2,2,1,1,1,1,1,11,'2020-11-26 10:02:35','2020-11-26 21:51:45');
UNLOCK TABLES;