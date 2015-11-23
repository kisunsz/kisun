<?php

/*****************************************
本文件用于设置一些系统的参数

时间        作者    备注
-------------------------------------------
2015-05-08  黄长浩  初始版本
2015-05-18  黄长浩  增加发邮件相关的参数

*****************************************/

//COOKIE失效的天数
define( "CONST_COOKIE_EXPIRE_DAYS", 100 );

//LOGIN失效的天数
//即，用户登录这么多天后，需要重新登录
define( "CONST_LOGIN_EXPIRE_DAYS", 90 );

//发邮件相关
define( "CONST_SMTP_HOST", "smtp.suzhoumaker.com" );
define( "CONST_SMTP_PORT", 25 );
define( "CONST_SMTP_USERNAME", "info@suzhoumaker.com" );
define( "CONST_SMTP_PASSWORD", "Welcome3" );
define( "CONST_EMAIL_FROM_ADDRESS", "info@suzhoumaker.com" );
define( "CONST_EMAIL_FROM_NAME", "苏州创客空间" );
define( "CONST_EMAIL_REPLYTO_ADDRESS", "info@suzhoumaker.com" );
define( "CONST_EMAIL_REPLYTO_NAME", "苏州创客空间" );
?>