<!DOCTYPE html>
<html>
<meta http-equiv="content-type" content="text/html; charset=utf-8" />
<body>
<title>Some stats</title>
<p><big>Statistics</big></p>
<?php 
    $array = new SplFixedArray(8);
    for($i=0;$i<8;$i++){
         $array[$i] = 0;
    }
	$handle = fopen("data", "r");
	if ($handle) {
		while (($line = fgets($handle)) !== false) {
			$line = str_replace(["\n","\r"],"", $line);
			$tmp = explode(" ", $line);
            //echo(count($tmp)." ".$tmp[0]);
            echo("<table border=4<tr>");
            echo("<td>".$tmp[0]."</td>");
            for($i = 1; $i < count($tmp); $i++){
                
                $num = $tmp[$i];
                $int = intval($num);
                if($int > 0 && $int <= 8){
                    $array[$int-1] = $array[$int-1]+1;
                }        
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
                    echo("<td>____</td>");
                }
                
            }
            echo("</tr></table>\n");
            //echo("<p>".implode(",",$tmp)."z</p>");
		}
		fclose($handle);
	} else {
		echo("Configuration files not configurated");
	}
?>
<script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript">
      google.load("visualization", "1", {packages:["corechart"]});
      google.setOnLoadCallback(drawChart);
      function drawChart() {
        var data = google.visualization.arrayToDataTable([
          ['Task', 'Repartition'],
          <?php
          for($i=0;$i<8;$i++){
                echo("['Classification $i', ".$array[$i]." ],\n");
          }
          
          ?>
        ]);

        var options = {
          title: 'Summary',
          pieHole: 0.4,
          slices: {
            0: { color: 'blue' },
            1: { color: 'blueviolet' },
            2: { color: 'brown' },
            3: { color: 'crimson' },
            4: { color: 'gold' },
            5: { color: 'darkgreen' },
            6: { color: 'darkred' },
            7: { color: 'lime' }
          }
        };

        var chart = new google.visualization.PieChart(document.getElementById('donutchart'));
        chart.draw(data, options);
      }
    </script>
    <div id="donutchart" style="width: 900px; height: 500px;"></div>
</body>
</html>

