<html>
    <head>
        <title>模板</title>
        <meta http-equiv="content-type" content="text/html; charset=UTF-8" />
    </head>
<body> 
	<p>
		<h3>你好，${account.name}</h3>
	</p>
    <p>
    	设备:${dailyMaintenance.device.serialNo}的日常维护:${dailyMaintenance.mainCategory}已经到达检查或更换预警要求
    	<br></br>
    	生产日期:${dailyMaintenance.produceDate}
    	<br></br>
    	维护周期(检查):${dailyMaintenance.checkYear}年${dailyMaintenance.checkMonth}月${dailyMaintenance.checkDay}日
    	<br></br>
    	维护周期(更换):${dailyMaintenance.exchangeYear}年${dailyMaintenance.exchangeMonth}月${dailyMaintenance.exchangeDay}日
    	<br></br>
    	检查提前预警:${dailyMaintenance.remindCheckYear}年${dailyMaintenance.remindCheckMonth}月${dailyMaintenance.remindCheckDay}日
    	<br></br>
    	更换提前预警:${dailyMaintenance.remindExchangeYear}年${dailyMaintenance.remindExchangeMonth}月${dailyMaintenance.remindExchangeDay}日
    </p>
    
</body>  