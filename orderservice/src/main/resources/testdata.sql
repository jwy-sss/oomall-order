-- MySQL dump 10.13  Distrib 8.0.21, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: oomall
-- ------------------------------------------------------
-- Server version	8.0.21

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `auth_new_user`
--

LOCK TABLES `auth_new_user` WRITE;
/*!40000 ALTER TABLE `auth_new_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `auth_new_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `auth_privilege`
--

LOCK TABLES `auth_privilege` WRITE;
/*!40000 ALTER TABLE `auth_privilege` DISABLE KEYS */;
INSERT INTO `auth_privilege` VALUES (2,'查看任意用户信息','/adminusers/{id}',0,'e935a1c80f67b643a14a35342e0e6035c853985dcc655f3cb66f8f389e12881f','2020-11-01 09:52:20','2020-11-02 21:51:45'),(3,'修改任意用户信息','/adminusers/{id}',2,'47c7b664dfe1392f11130b255fc2180bf29d0977c3f03140c6eb11d1a8ba9a11','2020-11-01 09:53:03','2020-11-02 21:51:45'),(4,'删除用户','/adminusers/{id}',3,'34c8fc0b1b95d3d1923193d4e33f577d898ba76a2b9a32f40012e2e8fce4e3cf','2020-11-01 09:53:36','2020-11-02 21:51:45'),(5,'恢复用户','/adminusers/{id}/release',2,'ed6f2194ba61f1ee561d0ae2e3074ceb4537b8a6ed3adac1751301d7226854f7','2020-11-01 09:59:24','2020-11-02 21:51:45'),(6,'禁止用户登录','/adminusers/{id}/forbid',2,'c92bd57248b366c24e64adee6645d13330aee4b74511f39572bcc1ea5740cdd2','2020-11-01 10:02:32','2020-11-02 21:51:45'),(7,'赋予用户角色','/adminusers/{id}/roles/{id}',1,'212e8971d4e3ded86d567fee22794b5c93084c6baa93ad5ad0898e4c18abe9fa','2020-11-01 10:02:35','2020-11-02 21:51:45'),(8,'取消用户角色','/adminusers/{id}/roles/{id}',3,'3a76849e4eb439060c53a3ab1c9c86692db0f25695966bd0d5d8e3d647e10de2','2020-11-01 10:03:16','2020-11-02 21:51:45'),(9,'新增角色','/roles',1,'0f0c7264ebf4471839e6f70a53cf8ba80db9be8425430c2bbc87ff425eab8370','2020-11-01 10:04:09','2020-11-02 21:51:45'),(10,'删除角色','/roles/{id}',3,'26cca90a93f45a63e8b833dd6c6e7719e26d19ee6a7310edabc5c639a1052b9e','2020-11-01 10:04:42','2020-11-02 21:51:45'),(11,'修改角色信息','/roles/{id}',2,'04c693372e4eed426572c53db4af7838f0ac73142abdce9571885b8fe8b47be7','2020-11-01 10:05:20','2020-11-02 21:51:45'),(12,'给角色增加权限','/roles/{id}/privileges/{id}',1,'d70522f0960bdd1cb61b45d1d221beb30cd5813d9a41eac23da339ec648f4fc4','2020-11-01 10:06:03','2020-11-02 21:51:46'),(13,'取消角色权限','/roleprivileges/{id}',3,'aa8f57955f6652636cdcee567abff1bacff7cb529cff3dba632b95fed328f3ac','2020-11-01 10:06:43','2020-11-03 21:30:31'),(14,'修改权限信息','/privileges/{id}',2,'291c2f3703a762ee80553c00ab3d303a889b1d969b1638b1cc392e6ff161ccbd','2020-11-01 10:08:18','2020-11-02 21:51:46'),(15,'查看所有用户的角色','/adminusers/{id}/roles',0,'93c99f9a033ede52179ebf49c68c46c79c850827d433233bcf356c1572b70447','2020-11-03 17:53:38','2020-11-03 19:48:47'),(16,'查看所有代理','/proxies',0,'65b3099754b9d076a6d690bd85b6e7951dfaf21335521422fa3ab253f08adeeb','2020-11-03 17:55:31','2020-11-03 19:48:47'),(17,'禁止代理关系','/allproxies/{id}',3,'f87cf617d983a0719ea6ef0129f407eb44de91afb02d60a9b83bbe6cade9436d','2020-11-03 17:57:45','2020-11-03 19:48:47'),(18,'取消任意用户角色','/adminuserroles/{id}',3,'130ee544d8ac5c9560d6ad9a095d2cfc2659e3c7825b2197239ca3909f37688a','2020-11-03 19:52:04','2020-11-03 19:56:43'),(19,'管理员设置用户代理关系','/ausers/{id}/busers/{id}:\n',1,'efd21d08fe39b6c6927b0cfb078b0fb774fdd4c5b4662603d1ebb92aad31de71','2020-11-04 13:10:02',NULL);
/*!40000 ALTER TABLE `auth_privilege` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `auth_role`
--

LOCK TABLES `auth_role` WRITE;
/*!40000 ALTER TABLE `auth_role` DISABLE KEYS */;
INSERT INTO `auth_role` VALUES (23,'管理员',1,'超级管理员，所有权限都有','2020-11-01 09:48:24','2020-11-01 09:48:24'),(80,'财务',1,NULL,'2020-11-01 09:48:24','2020-11-01 09:48:24'),(81,'客服',1,NULL,'2020-11-01 09:48:24','2020-11-01 09:48:24'),(82,'运营部',1,NULL,'2020-11-01 09:48:24','2020-11-01 09:48:24'),(83,'产品部',1,NULL,'2020-11-01 09:48:24','2020-11-01 09:48:24'),(84,'文案',1,NULL,'2020-11-01 09:48:24','2020-11-01 09:48:24'),(85,'总经办',1,NULL,'2020-11-01 09:48:24','2020-11-01 09:48:24'),(86,'库管',1,NULL,'2020-11-01 09:48:24','2020-11-01 09:48:24'),(87,'辅助管理员',1,'一般的管理员','2020-11-03 15:45:20',NULL);
/*!40000 ALTER TABLE `auth_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `auth_role_privilege`
--

LOCK TABLES `auth_role_privilege` WRITE;
/*!40000 ALTER TABLE `auth_role_privilege` DISABLE KEYS */;
INSERT INTO `auth_role_privilege` VALUES (1,23,2,1,'2020-11-01 10:11:21','bef152f568d0840afb361e9c9d1f991db5ee495ce3806e82090b75a329b2dfde'),(2,23,3,1,'2020-11-01 10:11:53','c117552062e29547a688dfe6e3f3575d67e80ab567d23d504ec32892169ce669'),(3,23,4,1,'2020-11-01 10:12:15','defd118dcc771d1107683a8e4c637f83088b4a76514374445ef95d027e4b65ef'),(4,23,5,1,'2020-11-01 10:12:15','0437187400fbcdbbeb1d17aff8d86cb8a13a1707662fb2cacfca014a33266472'),(5,23,6,1,'2020-11-01 10:12:15','6386c022a2b4261b63e8796046e3c8ce8bec0663032ada5189041104648c3604'),(6,23,7,1,'2020-11-01 10:12:15','d77368d6d7d9c84e887968796d9e55948019ca8c61bdd99a802971ff35dc6596'),(7,23,8,1,'2020-11-01 10:12:15','f252a6e530c2b696135facf2a47635d1242ce2ea745520b5994ec18ea2a1d30b'),(8,23,9,1,'2020-11-01 10:12:15','d091f02cb90217bd13d17cf8472f8a1064397e3921143009b461295e42ad7e72'),(9,23,10,1,'2020-11-01 10:12:15','e1ca5a1c2ac34e7e35325f5f8cf25eac429e5616ac71dbc40e031a7a766047b5'),(10,23,11,1,'2020-11-01 10:12:15','2cb45f7bcd408bbc80e655929c2de9de046dc7483b062b999ca9e5b6d221c985'),(11,23,12,1,'2020-11-01 10:12:15','bd6d477878c3499ccc5078f4a347410a241f9b251d39e1881109e81e83a62efa'),(12,23,13,1,'2020-11-01 10:12:15','2a61159392d46e33fd0cc5a370dcd5766f3da58c0573b103227d24ea742549c6'),(13,23,14,1,'2020-11-01 10:12:15','fc8810ea4f3778f36172a65546a79bc8c9f44a6eb7dc7390dde25da358a32e6b'),(14,87,2,1,'2020-11-01 10:12:15','d5b2f1a848658f401d0261d966a2825483a66c761c3c12142d6d75da069e31a3'),(15,84,14,1,'2020-11-01 10:12:15','302d7231609516b6648025c5610eaaad5eb407f8fa7248d7102f1c5609f0adb4'),(16,85,9,1,'2020-11-01 10:12:15','8d11def65b6d40fd57bca6709a40856ffda72859137b71fdbdbf60d9f335aee1'),(17,85,10,1,'2020-11-01 10:12:15','b5dc3de24caa4c394788c6d130ce6355971161a6f50b8ac5724891b75333c47c'),(18,87,15,1,'2020-11-01 10:12:15','59a8af7788517fbc9313e45515a0843b9c8fe16c85667e8fd0c4c1c165652a66'),(19,87,16,1,'2020-11-01 10:12:15','b86481c27207e44ca640af5c2e1aaf670ce62fe15a25743236ce8d6576f0f5a0'),(20,23,15,1,'2020-11-03 18:27:01','b03b77d4269704ebb137a4d2482713abef01d2b1bc6f369f3594cdaff8bce381'),(21,23,16,1,'2020-11-03 18:27:03','d01849ed97c8875a69fc3353d5fcca7471d72fcf4148774f0e8296e8accdd969'),(22,23,17,1,'2020-11-03 18:27:04','b321e0260221249a692c49016281daaef01043d869e55f30fb573b489215fc6d'),(23,85,11,1,'2020-11-03 18:27:06','8de3be9db9242259f325dd6271565a53d4020cf79793035d2e0ae44b17a45752'),(24,85,12,1,'2020-11-03 18:26:41','9bbe44e3d4bc8f40c59e1f4dcf449fb7b7a622640c4c4c621270165b42095be1'),(25,85,13,1,'2020-11-04 18:27:37','08238cb1a1ee1310c96a7b54147f996b335c3e3db0912b99cb4436c94b3e5265'),(26,86,2,1,'2020-11-03 18:28:01','35577bbb11d6849952ffee467cabddbcab3bb72a6c5546d5fcfd3294125dfae0'),(27,86,3,1,'2020-11-03 18:28:19','a6906c4ee34b3673cedd43f857f3d452d46a06ea96e93939f468bd24373c5afb'),(28,86,4,1,'2020-11-03 18:28:36','74c4bcc5395194d940cd41428a6c2fe86d7ba5d31be3bb405186e7844b92bf10'),(29,86,5,1,'2020-11-03 18:28:50','7ac26f6637e4c9d105097c1b52e1954ffcf89fb0415413dfe8cb2ee01cc8de14'),(30,86,6,1,'2020-11-03 18:29:05','ce503f2a32f8f73ff0a9c49079cde9d7b1fdd4443f9bbdb9f6bc2bf3dd251d41'),(31,86,7,1,'2020-11-03 18:29:19','22c66389b20617573c2ce310a4423a41025cb49bcb80b9b168c9efb14a8351d4'),(32,86,8,1,'2020-11-03 18:29:31','e1825f579fcef822372e03fdaa3344738a04f97819ffd0463c044c7b368a1340'),(33,86,17,1,'2020-11-03 18:29:44','e2a3ebd00fd1a47387871826a70c5a53394e373cd3fca028b36607471498aa5e');
/*!40000 ALTER TABLE `auth_role_privilege` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `auth_user`
--

LOCK TABLES `auth_user` WRITE;
/*!40000 ALTER TABLE `auth_user` DISABLE KEYS */;
INSERT INTO `auth_user` VALUES (1,'13088admin','BCB71451C344BFB09FC0403699098E9E','CEDCE1800461EBF9F8BFDAE042A16AD7',1,'5FEF64EE1FEBCA16B7E832C7399514C8E6EE0689F9840B3D43ACE844593499C4',1,'17C36DAFF9E446D927EDDFA23E167923',NULL,'2020-11-01 09:48:24','182.86.203.201',NULL,1,0,'2020-11-01 09:48:24','2020-11-01 09:48:24','674dac25204e27c63908b3051155177d94f2e5a2d5fbeeb4b6afda87cf6f5e5d',1),(46,'8131600001','BCB71451C344BFB09FC0403699098E9E','70506B1BD0A760B6F18B01258429445D',1,'A4D27D7D61F3CF8FFCA7478AF0AECC9D',0,'7E1D677978A25D2BB98064007797E3C1',NULL,'2020-11-01 09:48:24','111.73.169.226',NULL,1,0,'2020-11-01 09:48:24','2020-11-01 09:48:24','1eff3f5cef99bb65761a94a7a2c0a4452147e5471529178526b2776625182ecd',1),(47,'2721900002','BCB71451C344BFB09FC0403699098E9E','E85EC6A417CE482F1A978AD37A42E93F',1,'10BA7E6485D824A698848E3540550831',1,'17490D1F64460A3790004F822B884B5A',NULL,'2020-11-01 09:48:24','111.73.172.255',NULL,1,0,'2020-11-01 09:48:24','2020-11-01 09:48:24','f9d62d1becc66e5d01a98bc177d8b61eb3f97b0bef8c03eeb785c354f5a52e35',1),(48,'8532600003','BCB71451C344BFB09FC0403699098E9E','FB6D126F70DBABB3362C5C0105D60C04',1,'7905CA9A8B5B690F753637FFC1D2B4D0',0,'88DEFE73E23A4A728B7AE86885466B02',NULL,'2020-11-01 09:48:24','182.86.207.170',NULL,1,0,'2020-11-01 09:48:24','2020-11-01 09:48:24','39f6ebc18bcb9bb3f3f9383ea37a5ca0760614d870c014c72712e34c2285b472',1),(49,'7623000006','BCB71451C344BFB09FC0403699098E9E','C02850E8319C191A19835B437CEC7D11',1,'D422B9A6FA932F2E79F9E060B6F4DD18',1,'5E92BC21B180BA2F02106212AA18FA00',NULL,'2020-11-01 09:48:24','111.73.172.255',NULL,1,0,'2020-11-01 09:48:24','2020-11-01 09:48:24','800773d8f3cadaca9640c2a2b8014e75ebd5f7eb39495583e442d551a3716063',1),(50,'8043300005','BCB71451C344BFB09FC0403699098E9E','E49A3BBE88F19F38E10A7561E8942065',1,'DC9596938E91EB362A15B3B31594254B',1,'E13BD4F72A634AA85559306EBBF5FBE3',NULL,'2020-11-01 09:48:24','111.73.169.94',NULL,1,0,'2020-11-01 09:48:24','2020-11-01 09:48:24','b090a9d27d74fc2b7e0f812fd4b3003c0b4d1ac76d18c2f873e5115afa484dae',1),(51,'4216700007','BCB71451C344BFB09FC0403699098E9E','9673FC29957279394123A96BBFC8EA08',1,'0D5DC56CE2E29BF9A2D44B8276AE3F87',1,'5D9931472C6D5CBC11544EEAB25C7764',NULL,'2020-11-01 09:48:24','59.63.23.162',NULL,1,0,'2020-11-01 09:48:24','2020-11-01 09:48:24','be5a837e9da3600e01bca9237b9aac238cec45053dad8fbdf0ce6304fb2c03da',1),(52,'7728110000','BCB71451C344BFB09FC0403699098E9E','E337620BFCC4F9D398708623E752CE06',1,'0501F8F9DFB64EDC4B079535E61346FC',1,'D3A8F17C528B88AEDA4F7A9F3DA442B2',NULL,'2020-11-01 09:48:24','59.46.161.186',NULL,0,0,'2020-11-01 09:48:24','2020-11-01 09:48:24','3e7d352f68d3c6252d10b196f0609ea5e57e97ccc4307d3aa6335a132db23f16',1),(53,'8884810086','BCB71451C344BFB09FC0403699098E9E','33E072A014BB900D7D228F3D6874774C',1,'8353D137AEE0BF3E33B237CAB2DDB396',1,'D2A5F5EFBBCE023FD461BAE5952A014A',NULL,'2020-11-01 09:48:24','120.35.68.104',NULL,2,0,'2020-11-01 09:48:24','2020-11-01 09:48:24','65f6dc827ded1b017f9574328d3100553a42b324c4a6a883ab7146cfa9006466',1),(54,'9259200008','BCB71451C344BFB09FC0403699098E9E','7B3E9DFA134D3A4872E885D8666D87A0',1,'054C0899D9A395355E96A9FB47BB004D',1,'BFB92ECE98032BAF22F5813FFFA608B8',NULL,'2020-11-01 09:48:24','',NULL,2,0,'2020-11-01 09:48:24','2020-11-01 09:48:24','ccb14d865ffc77f8ff6a871e1b82b489c23c164bf65ee1df1dfa71ee1b18b87d',1),(55,'5961900008','BCB71451C344BFB09FC0403699098E9E','9E4E2B181CDD6CA4DB6ABDB56F5D573D',1,'D8B3B05AEBFC8C0FDE97691C9FDFA0A2',0,'4593EE662905CAED0083E4298A4A7EC8',NULL,'2020-11-01 09:48:24','111.73.169.58',NULL,3,0,'2020-11-01 09:48:24','2020-11-01 09:48:24','62ec771b184e83858267ee7d761903b7f36e49efbfc568ba5b667a87021c3011',1),(57,'9943200016','BCB71451C344BFB09FC0403699098E9E','B48FF78FC321CAC36219C5B5FC4A7CA8',1,'2B741ED6CFF59BB5BE5768D03B0E81BE',1,'E4DA4B0C52A59391BDC8E87A3F137DB0',NULL,'2020-11-01 09:48:24','111.73.175.84',NULL,1,3,'2020-11-01 09:48:24','2020-11-01 09:48:24','f85c29e2d8ffee6777b3065c79fb56cb24ecfe7edd7e7de36e6698202c760fb4',1),(58,'5264500009','BCB71451C344BFB09FC0403699098E9E','E96357D2D0F2C2D608848E6AA1D4C8B7',1,'42FA8B0C50BED17CBA9F44DF53DB10C7',1,'1603128F1D394B952317BCAEC5C9CEAF',NULL,'2020-11-01 09:48:24','111.73.133.9',NULL,1,2,'2020-11-01 09:48:24','2020-11-01 09:48:24','aa9c6a86f9d1edf61c35faa712e3850880a589df671cdadc55d2c61cfdc1363b',1),(59,'537300010','BCB71451C344BFB09FC0403699098E9E','9A8F4DC46A92754E91742714986C9929',1,'C8E9539BF93D6E6E93D237EB3BE9FCCC',1,'DD866B8491CB464A385DF6E2EB6A409A',NULL,'2020-11-01 09:48:24','111.73.172.255',NULL,1,1,'2020-11-01 09:48:24','2020-11-01 09:48:24','4d9a6ede96f795851b022656cfc84dbd18026bc36bc9d93cc5ffc17a65b57e52',1);
/*!40000 ALTER TABLE `auth_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `auth_user_proxy`
--

LOCK TABLES `auth_user_proxy` WRITE;
/*!40000 ALTER TABLE `auth_user_proxy` DISABLE KEYS */;
INSERT INTO `auth_user_proxy` VALUES (1,49,47,'2020-10-03 18:51:42','2021-11-03 18:51:52','2020-11-03 18:52:00','bb1378ee78a41e6a37abd37aa2247af1f2962fc229ca84cf53981fb6b2fe37bc',1),(2,49,46,'2020-05-03 18:52:25','2020-10-03 18:52:31','2020-11-03 18:52:37','006d2a321a041446b8c19f33bda62c49bdefe6bd12705d1be50c45dedb4842bb',1),(3,49,48,'2021-12-03 18:53:01','2022-11-03 18:53:19','2020-11-03 18:53:39','6c0504294505cbd9b280954ea8442b478bdfc4b1184dc49b9cc1055f026a24f8',1),(4,49,50,'2020-11-01 18:53:59','2020-12-03 18:54:07','2020-11-03 18:54:17','fba6d947de10ea75670dacc896e64fa393f44280ab55ff06f7a1f3333aee52b2',0),(5,49,51,'2020-05-03 18:54:29','2020-07-03 18:54:37','2020-11-03 18:54:42','7b6bef43e290a29c964a4d5bad7208309ca4b583ae029579d2ce3b5a70e5c6ec',1);
/*!40000 ALTER TABLE `auth_user_proxy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `auth_user_role`
--

LOCK TABLES `auth_user_role` WRITE;
/*!40000 ALTER TABLE `auth_user_role` DISABLE KEYS */;
INSERT INTO `auth_user_role` VALUES (76,23,1,1,'b1e5a21c5281ee65d835d3c9f8d2e52a69b4c9627b956e556e4c6bc922510533','2020-11-01 09:48:24'),(77,87,46,1,'4d4e9ae6002cc873f9faad2760bf322b993a4cef6b96b8385f607fcbc7097df8','2020-11-01 09:48:24'),(78,85,47,1,'a60e3b0458194239790b35e4235f2756844ad74af82da1376854f3dde08976c3','2020-11-01 09:48:24'),(79,86,48,1,'78cbf2c1ad3b42a5090b9d00fab42cbd46305b985b0391f0fd00a5261df8f419','2020-11-01 09:48:24'),(80,84,49,1,'b91d40341ca8de588f33564c15950f7c88019acdaad7f75e35005072d37f933e','2020-11-01 09:48:24'),(81,86,50,1,'61f051200ef331a3dbf36ca9dff7f7bb988c964f8589acadf8746e5f1a0d991e','2020-11-01 09:48:24'),(82,85,51,1,'172c8c801e5d78ac2cddfff1c15193190aab8d7cd894b51a56fbdcb9f6d392e6','2020-11-01 09:48:24'),(83,84,52,1,'3a1e7e5ab2663a21c5516b99d08bd0f98468e089c28aa407e6ceda6011704a31','2020-11-01 09:48:24'),(84,80,53,1,'5895907108b2a713d9453c95abaa22ce6ae271868cdfe754b9a89088c9460c1b','2020-11-01 09:48:24'),(85,86,54,1,'6b2ae50355ccaf1f8bf8651bd5ec18c8f95047864a6c9663e3981d77d386c626','2020-11-01 09:48:24'),(86,86,55,1,'a5ce35b485724f378023621ef88362ec15530dd189fc4636bcc88617e557860f','2020-11-01 09:48:24'),(87,23,57,1,'c68ebbce03ba0d077403323268611a67fa95d351c20e55e8c05b666f8535736b','2020-11-01 09:48:24'),(88,84,54,1,'0ed4b4c4fc0cb0d70a22f9e4b12da791a10d00cc8fdc59c9013b670f0bcf27eb','2020-11-01 09:48:24'),(89,87,50,1,'cee926e8ee2cfcf5b5aabe78f393dea4e5789d12bb15143e9eb90392a526d574','2020-11-01 09:48:24'),(90,84,51,1,'e5004c1dca8420b77cd7e58d9d9de5261af223ebb0186414187ba5fe3ec7ebd7','2020-11-03 21:33:52');
/*!40000 ALTER TABLE `auth_user_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

LOCK TABLES `order_order` WRITE;
/*!40000 ALTER TABLE `auth_user_role` DISABLE KEYS */;
INSERT INTO `order_order` VALUES (1,1,1,123456789,2,'wwq',121,'海韵','13571111','请收货',0,5,1,1,23,50,0,0,0,0,'2020-11-07 09:48:24',1234444,1,0,0,'2020-11-01 09:48:24',NULL),(2,1,1,123456780,0,'wwq',121,'海韵','13571111','请收货2',0,5,2,0,26,55,0,0,0,0,'2020-11-07 09:48:55',1234449,1,0,0,'2020-11-01 09:48:24',NULL)
/*!40000 ALTER TABLE `auth_user_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `auth_user_role` DISABLE KEYS */;
INSERT INTO `order_item` VALUES (1,1,1,5,12,3,'水杯',1,0,0,'2020-11-07 20:48:24'),(2,1,1,6,10,3,'茶壶',3,0,0,'2020-11-07 20:49:24')
/*!40000 ALTER TABLE `auth_user_role` ENABLE KEYS */;
UNLOCK TABLES;

LOCK TABLES `good_goods_sku` WRITE;
/*!40000 ALTER TABLE `auth_user_role` DISABLE KEYS */;
INSERT INTO `good_goods_sku` VALUES (1,1,123,'水杯',15,'未知',10,'http:123',5000,'大水杯',0),(2,2,123,'水杯',15,'未知',10,'http:123',5000,'小水杯',0),(3,3,123,'瓶子',15,'未知',10,'http:123',5000,'小瓶子',0)
/*!40000 ALTER TABLE `auth_user_role` ENABLE KEYS */;
UNLOCK TABLES;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-11-07  5:55:58

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (48050,1,1,'2016102361111',NULL,'刘勤',NULL,NULL,'13959288888',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,11,0,'2020-11-28 17:48:46','2020-11-28 17:48:46',NULL),
(48051,2,1,'2016102372222',NULL,'刘恩羽',NULL,NULL,'13959288888',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1,12,0,'2020-11-28 17:48:46','2020-11-28 17:48:46',NULL),
(48052,1,1,'2016102363333',NULL,'刘勤',NULL,NULL,'13959288888',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,21,0,'2020-11-28 17:48:46','2020-11-28 17:48:46',NULL),
(48053,4,1,'2016102394444',NULL,'刘齐嘉',NULL,NULL,'13959288888',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,22,0,'2020-11-28 17:48:46','2020-11-28 17:48:46',NULL),
(48054,1,1,'2016102385555',NULL,'刘勤',NULL,NULL,'13959288888',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,23,0,'2020-11-28 17:48:46','2020-11-28 17:48:46',NULL),
(48055,7,1,'2016102396666',NULL,'刘伟',NULL,NULL,'13959288888',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,2,24,0,'2020-11-28 17:48:46','2020-11-28 17:48:46',NULL),
(48056,7,1,'2016102397777',NULL,'刘伟',NULL,NULL,'13959288888',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,3,11,0,'2020-11-28 17:48:46','2020-11-28 17:48:46',NULL),
(48057,1,1,'2016102388888',NULL,'刘恩羽',NULL,NULL,'13959288888',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,4,11,0,'2020-11-28 17:48:46','2020-11-28 17:48:46',NULL);
UNLOCK TABLES;