INSERT INTO `cloud_provider_tbl` VALUES (1,'Eucalyptus');

INSERT INTO `availability_zone_tbl` VALUES (1,1,'dev-pilvi1');

INSERT INTO `cluster_type_tbl` VALUES (1,1,'ig','Identity Gateway',-1,0,1,12,NULL,NULL),(2,1,'bas','BAS Platform',-1,0,1,12,NULL,NULL),(3,1,'portal','Portal Platform',5,0,1,12,NULL,NULL),(4,1,'mq','Service Platform',5,0,1,12,NULL,NULL),(5,1,'rdbms','Relational Database Management',-1,0,1,1,NULL,NULL),(6,1,'nosql','NoSQL Repository',-1,1,6,12,3,10),(7,1,'bigdata','Big Data Repository',-1,1,7,12,3,10),(8,1,'ee','EE Platform',-1,0,1,12,NULL,NULL),(9,1,'ecm','Enterprise Content Management',-1,0,1,12,NULL,NULL);

INSERT INTO `machine_type_tbl` VALUES (0,'Small','Cores: 1, RAM: 512MB, Disk: 10GB'),(1,'Medium','Cores: 2, RAM: 1GB, Disk: 10GB'),(2,'Large','Cores: 4, RAM: 2GB, Disk: 10GB'),(3,'XLarge','Cores: 8, RAM: 3GB, Disk: 10GB'),(4,'XXLarge','Cores: 16, RAM: 4GB, Disk: 10GB');
