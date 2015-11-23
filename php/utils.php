<?php
/*****************************************
本文件用于定义一些通用工具函数

时间        作者    备注
-------------------------------------------
2015-05-13  黄长浩  初始版本

*****************************************/

// 检查一个Request的参数是否SET，而且值不为空
function isParamSetNotEmpty($paramName) 
{
	return (isset ( $_REQUEST [$paramName] ) && (! empty ( $_REQUEST [$paramName] )));
}

//获取当前页面的完整URL
function currentPageUrl() 
{
    $pageURL = 'http';

    if ($_SERVER["HTTPS"] == "on") 
    {
        $pageURL .= "s";
    }
    $pageURL .= "://";

    if ($_SERVER["SERVER_PORT"] != "80") 
    {
        $pageURL .= $_SERVER["SERVER_NAME"] . ":" . $_SERVER["SERVER_PORT"] . $_SERVER["REQUEST_URI"];
    } 
    else 
    {
        $pageURL .= $_SERVER["SERVER_NAME"] . $_SERVER["REQUEST_URI"];
    }
    return $pageURL;
}

//同步数据到wiki
function syncUser($phone,$name,$pwd,$syncAction){
	// A very simple PHP example that sends a HTTP POST to a remote site
	$url = 'http://x.suzhoumaker.com/syncAction.php';
	$ch = curl_init();

	curl_setopt($ch, CURLOPT_URL,$url);
	curl_setopt($ch, CURLOPT_POST, 1);
	curl_setopt($ch, CURLOPT_POSTFIELDS,"userId=".$phone."&pwd=".$pwd."&userName=".$name."&syncAction=".$syncAction);

	// in real life you should use something like:
	// curl_setopt($ch, CURLOPT_POSTFIELDS,
	//          http_build_query(array('postvar1' => 'value1')));

	// receive server response ...
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

	$server_output = curl_exec ($ch);

	curl_close ($ch);

	// further processing ....
	$mysql = new SaeMysql();
	if ($server_output == "OK") {
		$syncAction = 0;
	}

	$sql = "UPDATE Member SET m_sync_action = ".$syncAction.", m_updated_on=now() WHERE m_phone=" . $phone;
	//echo $sql;
	$mysql->runSql( $sql );
	if( $mysql->errno()!=0 ){
		die ( "Error:" . $mysql->errmsg () );
	}else{
		//
	}
	$mysql->closeDb ();
}

function syncPhone($id,$oldPhone,$newPhone){
	$url = 'http://x.suzhoumaker.com/syncAction.php';
	$ch = curl_init();

	curl_setopt($ch, CURLOPT_URL,$url);
	curl_setopt($ch, CURLOPT_POST, 1);
	curl_setopt($ch, CURLOPT_POSTFIELDS,"oldPhone=".$oldPhone."&newPhone=".$newPhone);

	// receive server response ...
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

	$server_output = curl_exec ($ch);

	curl_close ($ch);

	$syncAction = 3;
	// further processing ....
	$mysql = new SaeMysql();
	if ($server_output == "OK") {
		$syncAction = 0;
	}

	$sql = "UPDATE Member SET m_sync_action = ".$syncAction.", m_updated_on=now() WHERE m_num=" . $id;
	$mysql->runSql( $sql );
	if( $mysql->errno()!=0 ){
		die ( "Error:" . $mysql->errmsg () );
	}else{
		//
	}
	$mysql->closeDb ();
}

?>