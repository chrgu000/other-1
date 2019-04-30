<html> 
<body> 
	<p>
		<h3>你好，${account.name}</h3>
	</p>
    <p>
    	设备:${instrument.device.serialNo}的日常维护:${instrument.name}已经到达检查预警要求
    	<br></br>
    	生产日期:${instrument.produceDate}
    	<br></br>
    	校验周期:${instrument.checkYear}年${instrument.checkMonth}月${instrument.checkDay}日
    	<br></br>
    	校验提前预警:${instrument.remindCheckYear}年${instrument.remindCheckMonth}月${instrument.remindCheckDay}日
    </p>
    
</body>  