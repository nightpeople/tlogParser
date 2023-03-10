CREATE TABLE IF NOT EXISTS dbversionlog (
  rid INT(10) NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  appbranch VARCHAR(32) NOT NULL COMMENT '应用程序分支',
  dbver INT(10) NOT NULL COMMENT '版本号',
  updatetime DATETIME NOT NULL,
  PRIMARY KEY (rid)
)
ENGINE = INNODB,
AUTO_INCREMENT = 6,
AVG_ROW_LENGTH = 3276,
CHARACTER SET utf8,
COLLATE utf8_general_ci,
COMMENT = 'DB版本日志';

insert into dbversionlog(appbranch,dbver,updatetime ) values('2141_CN_ZS',0,NOW());