<!DOCTYPE html>
<html>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<body>
<title>Some stats</title>
<p><big>Statistics</big></p>
<?php 
	$handle = fopen("data", "r");
	if ($handle) {
		while (($line = fgets($handle)) !== false) {
			$line = str_replace(["\n","\r"], "", $line);
			$tmp = explode(' ', $line);
            //echo(count($tmp)." ".$tmp[0]);
            echo("<table border=4<tr>");
            echo("<td>".$tmp[0]."</td>");
            for($i = 1; $i < count($tmp); $i++){
                $num = $tmp[$i];        
                if($num == "1"){
                    echo("<td BGCOLOR=\"blue\">____</td>");
                }else if($num == "2"){
                    echo("<td BGCOLOR=\"blueviolet\">____</td>");
                }else if($num == "3"){
                    echo("<td BGCOLOR=\"brown\">____</td>");
                }else if($num == "4"){
                    echo("<td BGCOLOR=\"crimson\">____</td>");
                }else if($num == "5"){
                    echo("<td BGCOLOR=\"gold\">____</td>");
                }else if($num == "6"){
                    echo("<td BGCOLOR=\"darkgreen\">____</td>");
                }else if($num == "7"){
                    echo("<td BGCOLOR=\"darkred\">____</td>");
                }else if($num == "8"){
                    echo("<td BGCOLOR=\"lime\">____</td>");
                }else{
                    echo("<td>___</td>");
                }
                //echo("<td>".$tmp[$i]."</td>");
            }
            echo("</tr></table>\n");
		}
		fclose($handle);
	} else {
		echo("Configuration files not configurated");
	}
?>
</body>
</html>

