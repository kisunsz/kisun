<?php
/*****************************************
本文件用于定义一些通用工具函数

时间        作者    备注
-------------------------------------------
2015-05-15  孙忠    初始版本
2015-05-18  黄长浩  将发邮件相关的参数移到config.php文件中
*****************************************/

// 邮件发送器类
class Mailer extends PHPMailer{
	public function SendEmail($p_subject,$p_body) {
		$this->CharSet = "utf-8";
		$this->IsSMTP ();
		$this->Host = CONST_SMTP_HOST;
		$this->SMTPAuth = true;
		$this->Username = CONST_SMTP_USERNAME;
		$this->Password = CONST_SMTP_PASSWORD;
		$this->Port = CONST_SMTP_PORT;
		$this->From = CONST_EMAIL_FROM_ADDRESS;
		$this->FromName = CONST_EMAIL_FROM_NAME;
		$this->AddReplyTo(CONST_EMAIL_REPLYTO_ADDRESS, CONST_EMAIL_REPLYTO_NAME);
		//$this->AddBCC("kingsley_sun@163.com","K");
		$this->IsHTML ( true );
		$this->Subject = $p_subject;
		$this->Body = $p_body;
		
		if (! $this->Send ()) {
			die ( $this->ErrorInfo );
			exit ();
		}
	}
	public function bodyTemp($myBody){
		return $this->bodyHeader().$myBody.$this->bodyFooter();
	}
	private function bodyHeader(){
		return "<!DOCTYPE html><html><head><meta http-equiv='content-type' content='text/html; charset=UTF-8'></head><body>";
	}
	private function bodyFooter(){
		return "</body></html>";
	}
}

?>