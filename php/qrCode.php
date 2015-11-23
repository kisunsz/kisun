<?php
include_once 'phpqrcode.php';

if(isset( $_REQUEST ['p_val'] )) 
{
	$value = $_REQUEST ['p_val'];
	$errorCorrectionLevel = "M"; // L,M,Q,H
	$matrixPointSize = "6"; // 1-10
	QRcode::png ( $value, false, $errorCorrectionLevel, $matrixPointSize );
	exit ();
}
?>
