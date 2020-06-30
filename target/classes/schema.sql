CREATE TABLE  `users` (
  `username` varchar(50) PRIMARY KEY ,
  `password` varchar(50) NOT NULL,
  enabled tinyint(1) NOT NULL
);
CREATE TABLE  `authorities` (
  `username` varchar(50) NOT NULL,
  `authority` varchar(50) NOT NULL,
  UNIQUE KEY `ix_auth_username` (`username`,`authority`),
  CONSTRAINT `fk_authorities_users` FOREIGN KEY (`username`) REFERENCES `users` (`username`)
);
CREATE TABLE IF NOT EXISTS persistent_logins (
  username varchar(64) NOT NULL,
  series varchar(64) PRIMARY KEY,
  token varchar(64) NOT NULL,
  last_used timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

