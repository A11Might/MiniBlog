-- MySQL dump 10.13  Distrib 8.0.15, for Win64 (x86_64)
--
-- Host: localhost    Database: miniblog
-- ------------------------------------------------------
-- Server version	8.0.15

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from_id` int(11) DEFAULT NULL,
  `to_id` int(11) DEFAULT NULL,
  `conversation_id` varchar(45) NOT NULL,
  `content` text,
  `status` int(11) DEFAULT NULL COMMENT '0-未读;1-已读;2-删除;',
  `create_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `index_from_id` (`from_id`),
  KEY `index_to_id` (`to_id`),
  KEY `index_conversation_id` (`conversation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=413 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
INSERT INTO `message` VALUES (379,1,155,'1','{&quot;actorId&quot;:155,&quot;entityType&quot;:1,&quot;entityId&quot;:369}',1,'2020-03-23 10:57:47'),(380,1,155,'1','{&quot;actorId&quot;:155,&quot;entityType&quot;:1,&quot;entityId&quot;:371}',1,'2020-03-24 02:00:19'),(381,1,155,'1','{&quot;actorId&quot;:155,&quot;entityType&quot;:1,&quot;entityId&quot;:370}',1,'2020-03-24 02:00:20'),(382,1,155,'1','{&quot;actorId&quot;:155,&quot;entityType&quot;:1,&quot;entityId&quot;:367}',1,'2020-03-24 02:00:21'),(383,1,155,'1','{&quot;actorId&quot;:155,&quot;entityType&quot;:1,&quot;entityId&quot;:365}',1,'2020-03-24 02:00:23'),(384,1,155,'1','{&quot;actorId&quot;:155,&quot;entityType&quot;:1,&quot;entityId&quot;:372}',1,'2020-03-24 02:00:29'),(385,1,155,'1','{&quot;actorId&quot;:155,&quot;entityType&quot;:1,&quot;entityId&quot;:373}',1,'2020-03-24 02:00:40'),(386,1,155,'2','{&quot;actorId&quot;:155,&quot;entityType&quot;:1,&quot;entityId&quot;:373}',1,'2020-03-24 02:09:10'),(387,1,155,'3','{&quot;actorId&quot;:155,&quot;entityType&quot;:3,&quot;entityId&quot;:144}',1,'2020-03-24 02:12:14'),(388,1,155,'1','{&quot;actorId&quot;:155,&quot;entityType&quot;:1,&quot;entityId&quot;:373}',1,'2020-03-24 02:25:55'),(389,1,155,'1','{&quot;actorId&quot;:155,&quot;entityType&quot;:2,&quot;entityId&quot;:262}',1,'2020-03-24 02:47:08'),(390,1,155,'1','{&quot;actorId&quot;:155,&quot;entityType&quot;:1,&quot;entityId&quot;:373}',1,'2020-03-24 02:48:11'),(391,1,155,'1_155','通知内容',1,'2020-03-25 05:49:29'),(392,1,155,'1_155','通知内容',1,'2020-03-25 05:49:29'),(393,1,155,'1_155','通知内容',1,'2020-03-25 05:49:29'),(394,1,155,'1_155','通知内容',1,'2020-03-25 05:49:29'),(395,1,155,'1_155','通知内容',1,'2020-03-25 05:49:29'),(396,1,155,'1_155','通知内容',1,'2020-03-25 05:49:29'),(397,1,155,'1_155','通知内容',1,'2020-03-25 05:49:29'),(398,1,155,'1_155','通知内容',1,'2020-03-25 05:49:29'),(399,1,155,'1_155','通知内容',1,'2020-03-25 05:49:29'),(400,1,155,'1_155','通知内容',1,'2020-03-25 05:49:29'),(401,1,155,'2','{&quot;actorId&quot;:133,&quot;entityType&quot;:1,&quot;entityId&quot;:381}',1,'2020-03-26 11:56:20'),(402,1,133,'3','{&quot;actorId&quot;:155,&quot;entityType&quot;:3,&quot;entityId&quot;:133}',1,'2020-03-28 03:47:58'),(403,1,133,'2','{&quot;actorId&quot;:155,&quot;entityType&quot;:1,&quot;entityId&quot;:398}',1,'2020-03-28 11:37:07'),(404,1,155,'1','{&quot;actorId&quot;:133,&quot;entityType&quot;:1,&quot;entityId&quot;:394}',1,'2020-03-28 11:52:29'),(405,1,155,'2','{&quot;actorId&quot;:133,&quot;entityType&quot;:1,&quot;entityId&quot;:394}',1,'2020-03-28 11:52:31'),(406,1,155,'3','{&quot;actorId&quot;:133,&quot;entityType&quot;:3,&quot;entityId&quot;:155}',1,'2020-03-28 11:52:34'),(407,1,155,'1','{&quot;actorId&quot;:155,&quot;entityType&quot;:1,&quot;entityId&quot;:393}',1,'2020-03-28 11:55:30'),(408,1,155,'1','{&quot;actorId&quot;:155,&quot;entityType&quot;:1,&quot;entityId&quot;:392}',1,'2020-03-28 11:56:20'),(409,1,155,'1','{&quot;actorId&quot;:155,&quot;entityType&quot;:1,&quot;entityId&quot;:393}',1,'2020-03-28 12:30:40'),(410,1,133,'1','{&quot;actorId&quot;:155,&quot;entityType&quot;:1,&quot;entityId&quot;:397}',1,'2020-03-31 01:57:02'),(411,1,155,'2','{&quot;actorId&quot;:133,&quot;entityType&quot;:1,&quot;entityId&quot;:401}',1,'2020-03-31 01:59:10'),(412,1,155,'1','{&quot;actorId&quot;:133,&quot;entityType&quot;:1,&quot;entityId&quot;:401}',1,'2020-03-31 01:59:14');
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-04-03 10:47:58
