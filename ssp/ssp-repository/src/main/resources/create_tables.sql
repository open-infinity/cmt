
DROP TABLE IF EXISTS `user_tbl`;

CREATE TABLE `user_tbl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `phone` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `address_line_1` varchar(255) NOT NULL,
  `address_line_2` varchar(255) NOT NULL,
  `city` varchar(255) NOT NULL,
  `state_province_region` varchar(255) NOT NULL,
  `postal_code` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `payment_tbl`;

CREATE TABLE `payment_tbl` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(5) NOT NULL,
  `paymentType` varchar(255) NOT NULL,
  `payPalEmail` varchar(255) NOT NULL,
  `payPalPassword` varchar(255) NOT NULL,
  `creditCardCountry` varchar(255) NOT NULL,
  `creditCardType` varchar(255) NOT NULL,
  `creditCardNumber` varchar(255) NOT NULL,
  `creditCardCSC` varchar(255) NOT NULL,
  `creditCardExpirationMonth` varchar(255) NOT NULL,
  `creditCardExpirationYear` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
