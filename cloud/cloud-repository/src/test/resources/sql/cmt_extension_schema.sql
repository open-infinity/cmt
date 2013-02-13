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

DROP TABLE IF EXISTS cloud_provider_tbl;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE cloud_provider_tbl (
  id int(11) NOT NULL,
  name varchar(255) NOT NULL,
  PRIMARY KEY (id)
) ;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS availability_zone_tbl;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE availability_zone_tbl (
  id int(11) NOT NULL AUTO_INCREMENT,
  cloud_id int(11) NOT NULL,
  name varchar(255) NOT NULL,
  PRIMARY KEY (id), 
  CONSTRAINT fk_zone_cloud FOREIGN KEY (cloud_id) REFERENCES cloud_provider_tbl(id)
) ;

DROP TABLE IF EXISTS machine_type_tbl;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE machine_type_tbl (
  id int(11) NOT NULL,
  name varchar(255) NOT NULL,
  spec varchar(255) NOT NULL,
  PRIMARY KEY (id)
) ;


DROP TABLE IF EXISTS acl_cluster_type_tbl;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE acl_cluster_type_tbl (
  org_name varchar(50) NOT NULL,
  cluster_id int(11) NOT NULL,
  PRIMARY KEY (org_name, cluster_id),
  CONSTRAINT fk_acl_cluster_type FOREIGN KEY (cluster_id) REFERENCES cluster_type_tbl(id)
) ;

DROP TABLE IF EXISTS acl_cloud_provider_tbl;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE acl_cloud_provider_tbl (
  org_name varchar(50) NOT NULL,
  cloud_id int(11) NOT NULL,
  PRIMARY KEY (org_name, cloud_id),
  CONSTRAINT fk_acl_cloud_provider FOREIGN KEY (cloud_id) REFERENCES cloud_provider_tbl(id)
) ;

DROP TABLE IF EXISTS acl_availability_zone_tbl;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE acl_availability_zone_tbl (
  org_name varchar(50) NOT NULL,
  zone_id int(11) NOT NULL,
  PRIMARY KEY (org_name, zone_id),
  CONSTRAINT fk_acl_availability_zone FOREIGN KEY (zone_id) REFERENCES availability_zone_tbl(id)
) ;

DROP TABLE IF EXISTS acl_machine_type_tbl;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE acl_machine_type_tbl (
  org_name varchar(50) NOT NULL,
  machine_type_id int(11) NOT NULL,
  PRIMARY KEY (org_name, machine_type_id),
  CONSTRAINT fk_acl_machine_type FOREIGN KEY (machine_type_id) REFERENCES machine_type_tbl(id)
) ;

DROP TABLE IF EXISTS job_platform_parameter_tbl;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE job_platform_parameter_tbl (
  id int(11) AUTO_INCREMENT,
  job_id int(11),
  pkey varchar(255) NOT NULL,
  pvalue varchar(1000),
  PRIMARY KEY (id),
  CONSTRAINT fk_job_plaform FOREIGN KEY (job_id) REFERENCES job_tbl(job_id)
) ;
