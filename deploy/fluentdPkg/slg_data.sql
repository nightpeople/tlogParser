-- MySQL dump 10.13  Distrib 8.0.28, for Linux (x86_64)
--
-- Host: localhost    Database: slg_data
-- ------------------------------------------------------
-- Server version	8.0.28

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

USE slg_data;

--
-- Table structure for table `CoinConsume`
--

DROP TABLE IF EXISTS `CoinConsume`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `CoinConsume` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vClientIp` varchar(255) DEFAULT NULL,
  `vRoleName` varchar(255) DEFAULT NULL,
  `iCountry` int DEFAULT NULL,
  `iOfficeLv` int DEFAULT NULL,
  `iHomeLv` int DEFAULT NULL,
  `iCost` bigint DEFAULT NULL,
  `iShopType` int DEFAULT NULL,
  `iPropType` int DEFAULT NULL,
  `iPropId` int DEFAULT NULL,
  `iPropNum` int DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  `iNewCash` bigint DEFAULT NULL,
  `iOperate` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CreateAccount`
--

DROP TABLE IF EXISTS `CreateAccount`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `CreateAccount` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vClientIp` varchar(255) DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10598 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `CreateRole`
--

DROP TABLE IF EXISTS `CreateRole`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `CreateRole` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vClientIp` varchar(255) DEFAULT NULL,
  `vRoleName` varchar(255) DEFAULT NULL,
  `iCountry` int DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10503 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `MiniGame`
--

DROP TABLE IF EXISTS `MiniGame`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `MiniGame` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vRoleName` varchar(255) DEFAULT NULL,
  `iCountry` int DEFAULT NULL,
  `iOfficeLv` int DEFAULT NULL,
  `iHomeLv` int DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  `miniGameType` varchar(255) DEFAULT NULL,
  `level` int DEFAULT NULL,
  `spendTime` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6041 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `MoneyFlow`
--

DROP TABLE IF EXISTS `MoneyFlow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `MoneyFlow` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vRoleName` varchar(255) DEFAULT NULL,
  `iCountry` int DEFAULT NULL,
  `iOfficeLv` int DEFAULT NULL,
  `iHomeLv` int DEFAULT NULL,
  `iValueBefore` bigint DEFAULT NULL,
  `iValueAfter` bigint DEFAULT NULL,
  `iValue` bigint DEFAULT NULL,
  `iValueType` int DEFAULT NULL,
  `iOperate` int DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1032394 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `OnlineCount`
--

DROP TABLE IF EXISTS `OnlineCount`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `OnlineCount` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `dtEventTime` datetime DEFAULT NULL,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iAccountCount` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1029 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `PropFlow`
--

DROP TABLE IF EXISTS `PropFlow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `PropFlow` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vRoleName` varchar(255) DEFAULT NULL,
  `iCountry` int DEFAULT NULL,
  `iOfficeLv` int DEFAULT NULL,
  `iHomeLv` int DEFAULT NULL,
  `iOperate` int DEFAULT NULL,
  `iPropId` varchar(255) DEFAULT NULL,
  `iCount` bigint DEFAULT NULL,
  `iTotalCount` bigint DEFAULT NULL,
  `iIdentifier` int DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=937552 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Recharge`
--

DROP TABLE IF EXISTS `Recharge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Recharge` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vRoleName` varchar(255) DEFAULT NULL,
  `iCountry` int DEFAULT NULL,
  `iOfficeLv` int DEFAULT NULL,
  `iHomeLv` int DEFAULT NULL,
  `vSN` varchar(255) DEFAULT NULL,
  `ts` int unsigned DEFAULT NULL,
  `iPayDelta` int DEFAULT NULL,
  `iNewCash` bigint DEFAULT NULL,
  `vSubType` varchar(255) DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=242 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `RoleLogin`
--

DROP TABLE IF EXISTS `RoleLogin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `RoleLogin` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vClientIp` varchar(255) DEFAULT NULL,
  `vRoleName` varchar(255) DEFAULT NULL,
  `iCountry` int DEFAULT NULL,
  `iOfficeLv` int DEFAULT NULL,
  `iHomeLv` int DEFAULT NULL,
  `iCoin` bigint DEFAULT NULL,
  `dtCreateTime` datetime DEFAULT NULL,
  `iOnlineTotalTime` int DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14465 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `RoleLogout`
--

DROP TABLE IF EXISTS `RoleLogout`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `RoleLogout` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `dtLoginTime` datetime DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vClientIp` varchar(255) DEFAULT NULL,
  `dtCreateTime` datetime DEFAULT NULL,
  `iOnlineTime` int DEFAULT NULL,
  `iOnlineTotalTime` int DEFAULT NULL,
  `vRoleName` varchar(255) DEFAULT NULL,
  `iCountry` int DEFAULT NULL,
  `iOfficeLv` int DEFAULT NULL,
  `iHomeLv` int DEFAULT NULL,
  `iCoin` bigint DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14614 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cityChange`
--

DROP TABLE IF EXISTS `cityChange`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cityChange` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `cityId` varchar(255) DEFAULT NULL,
  `cityType` int DEFAULT NULL,
  `countryBefore` int DEFAULT NULL,
  `countryAfter` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=241 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cityFight`
--

DROP TABLE IF EXISTS `cityFight`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cityFight` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vRoleName` varchar(255) DEFAULT NULL,
  `iCountry` int DEFAULT NULL,
  `iOfficeLv` int DEFAULT NULL,
  `iHomeLv` int DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  `cityId` varchar(255) DEFAULT NULL,
  `heroId` varchar(255) DEFAULT NULL,
  `enemyheroId` varchar(255) DEFAULT NULL,
  `isNpc` int DEFAULT NULL,
  `result` int DEFAULT NULL,
  `loseTroop` int DEFAULT NULL,
  `credit` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23736 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `climb`
--

DROP TABLE IF EXISTS `climb`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `climb` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vRoleName` varchar(255) DEFAULT NULL,
  `iCountry` int DEFAULT NULL,
  `iOfficeLv` int DEFAULT NULL,
  `iHomeLv` int DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  `defeatNum` int DEFAULT NULL,
  `reward` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1097 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ctask`
--

DROP TABLE IF EXISTS `ctask`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ctask` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vRoleName` varchar(255) DEFAULT NULL,
  `iCountry` int DEFAULT NULL,
  `iOfficeLv` int DEFAULT NULL,
  `iHomeLv` int DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  `tasktype` varchar(255) DEFAULT NULL,
  `chapterId` int DEFAULT NULL,
  `taskId` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=50330 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `equipUpgrade`
--

DROP TABLE IF EXISTS `equipUpgrade`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `equipUpgrade` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vRoleName` varchar(255) DEFAULT NULL,
  `iCountry` int DEFAULT NULL,
  `iOfficeLv` int DEFAULT NULL,
  `iHomeLv` int DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  `equipId` varchar(255) DEFAULT NULL,
  `cost` varchar(255) DEFAULT NULL,
  `levelBefore` int DEFAULT NULL,
  `levelAfter` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `gtask`
--

DROP TABLE IF EXISTS `gtask`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gtask` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vRoleName` varchar(255) DEFAULT NULL,
  `iCountry` int DEFAULT NULL,
  `iOfficeLv` int DEFAULT NULL,
  `iHomeLv` int DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  `maxTimes` int DEFAULT NULL,
  `times` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5531 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `guide`
--

DROP TABLE IF EXISTS `guide`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `guide` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vRoleName` varchar(255) DEFAULT NULL,
  `iCountry` int DEFAULT NULL,
  `iOfficeLv` int DEFAULT NULL,
  `iHomeLv` int DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  `guideType` varchar(255) DEFAULT NULL,
  `guideNum` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=81975 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `heroStar`
--

DROP TABLE IF EXISTS `heroStar`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `heroStar` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vRoleName` varchar(255) DEFAULT NULL,
  `iCountry` int DEFAULT NULL,
  `iOfficeLv` int DEFAULT NULL,
  `iHomeLv` int DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  `heroId` varchar(255) DEFAULT NULL,
  `cost` varchar(255) DEFAULT NULL,
  `levelAfter` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6295 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `homeBuild`
--

DROP TABLE IF EXISTS `homeBuild`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `homeBuild` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vRoleName` varchar(255) DEFAULT NULL,
  `iCountry` int DEFAULT NULL,
  `iOfficeLv` int DEFAULT NULL,
  `iHomeLv` int DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  `buildingId` varchar(255) DEFAULT NULL,
  `levelBefore` bigint DEFAULT NULL,
  `levelAfter` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=114512 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `mining`
--

DROP TABLE IF EXISTS `mining`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mining` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vRoleName` varchar(255) DEFAULT NULL,
  `iCountry` int DEFAULT NULL,
  `iOfficeLv` int DEFAULT NULL,
  `iHomeLv` int DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  `result` int DEFAULT NULL,
  `reward` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `newPlayer`
--

DROP TABLE IF EXISTS `newPlayer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `newPlayer` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vRoleName` varchar(255) DEFAULT NULL,
  `iCountry` int DEFAULT NULL,
  `iOfficeLv` int DEFAULT NULL,
  `iHomeLv` int DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  `isNew` int DEFAULT NULL,
  `keyEvent` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=82929 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `officeRight`
--

DROP TABLE IF EXISTS `officeRight`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `officeRight` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vRoleName` varchar(255) DEFAULT NULL,
  `iCountry` int DEFAULT NULL,
  `iOfficeLv` int DEFAULT NULL,
  `iHomeLv` int DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  `officeRightId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9073 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pk`
--

DROP TABLE IF EXISTS `pk`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pk` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vRoleName` varchar(255) DEFAULT NULL,
  `iCountry` int DEFAULT NULL,
  `iOfficeLv` int DEFAULT NULL,
  `iHomeLv` int DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  `rankBefore` int DEFAULT NULL,
  `rankAfter` int DEFAULT NULL,
  `result` int DEFAULT NULL,
  `reward` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7292 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pve`
--

DROP TABLE IF EXISTS `pve`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pve` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vRoleName` varchar(255) DEFAULT NULL,
  `iCountry` int DEFAULT NULL,
  `iOfficeLv` int DEFAULT NULL,
  `iHomeLv` int DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  `pveType` int DEFAULT NULL,
  `result` int DEFAULT NULL,
  `loseTroop` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11004 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `pveCombat`
--

DROP TABLE IF EXISTS `pveCombat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pveCombat` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vRoleName` varchar(255) DEFAULT NULL,
  `iCountry` int DEFAULT NULL,
  `iOfficeLv` int DEFAULT NULL,
  `iHomeLv` int DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  `battleId` varchar(255) DEFAULT NULL,
  `repeat` int DEFAULT NULL,
  `result` int DEFAULT NULL,
  `reward` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10213 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `shop`
--

DROP TABLE IF EXISTS `shop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `shop` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vRoleName` varchar(255) DEFAULT NULL,
  `iCountry` int DEFAULT NULL,
  `iOfficeLv` int DEFAULT NULL,
  `iHomeLv` int DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  `shop` varchar(255) DEFAULT NULL,
  `orderId` int DEFAULT NULL,
  `cost` varchar(255) DEFAULT NULL,
  `get` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9676 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `skillLevel`
--

DROP TABLE IF EXISTS `skillLevel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `skillLevel` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `iEventId` varchar(255) DEFAULT NULL,
  `iWorldId` int DEFAULT NULL,
  `iUin` varchar(255) DEFAULT NULL,
  `dtEventTime` datetime DEFAULT NULL,
  `iRoleId` bigint DEFAULT NULL,
  `vRoleName` varchar(255) DEFAULT NULL,
  `iCountry` int DEFAULT NULL,
  `iOfficeLv` int DEFAULT NULL,
  `iHomeLv` int DEFAULT NULL,
  `iLoginWay` varchar(255) DEFAULT NULL,
  `heroId` varchar(255) DEFAULT NULL,
  `skillId` varchar(255) DEFAULT NULL,
  `cost` varchar(255) DEFAULT NULL,
  `levelAfter` int DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25669 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-12-08 15:48:49
