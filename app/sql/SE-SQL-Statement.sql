-- phpMyAdmin SQL Dump
-- version 4.0.10.10
-- http://www.phpmyadmin.net
--
-- Host: 127.6.99.130:3306
-- Generation Time: Nov 01, 2015 at 09:55 AM
-- Server version: 5.5.45
-- PHP Version: 5.3.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `se`
--

-- --------------------------------------------------------

--
-- Table structure for table `app`
--

DROP TABLE IF EXISTS `app`;
CREATE TABLE IF NOT EXISTS `app` (
  `timestamp` datetime NOT NULL,
  `macaddress` varchar(50) NOT NULL,
  `appid` int(10) NOT NULL,
  PRIMARY KEY (`timestamp`,`macaddress`),
  KEY `app_fk1` (`macaddress`),
  KEY `app_fk2` (`appid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `applookup`
--

DROP TABLE IF EXISTS `applookup`;
CREATE TABLE IF NOT EXISTS `applookup` (
  `appid` int(10) NOT NULL,
  `appname` varchar(50) NOT NULL,
  `appcategory` varchar(50) NOT NULL,
  PRIMARY KEY (`appid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `demo`
--

DROP TABLE IF EXISTS `demo`;
CREATE TABLE IF NOT EXISTS `demo` (
  `macaddress` varchar(50) NOT NULL,
  `name` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `email` varchar(50) NOT NULL,
  `gender` varchar(3) NOT NULL,
  `cca` varchar(100) NOT NULL,
  PRIMARY KEY (`macaddress`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `app`
--
ALTER TABLE `app`
  ADD CONSTRAINT `app_fk1` FOREIGN KEY (`macaddress`) REFERENCES `demo` (`macaddress`),
  ADD CONSTRAINT `app_fk2` FOREIGN KEY (`appid`) REFERENCES `applookup` (`appid`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
