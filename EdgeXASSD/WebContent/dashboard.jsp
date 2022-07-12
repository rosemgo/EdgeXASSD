<%@page language="java" contentType="text/html" import="java.sql.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Dependent Select Option</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css">
        <style type="text/css">
            body{
                background: url(image/roenkae.jpg);
                background-size: cover;
            }
            .drop-down-list{
                margin: 150px auto;
                width: 50%;
                padding: 30px;
            }
        </style>
    </head>
    <body class="cyan">
        <div class="container">
           
           
           
      <!--   INSERIRE VERSO SINISTRA IL FORM PER LA CREAZIONE DEL NODO SENSORE -->
            
            
            
	            <div class="drop-down-list card" style="float:left; ">  
	            	<div class="center">
	                    <h5>Create  Sensor Node</h5>
	                </div>
	            	<div class="divider"></div>
	            	
	            	<form action="ServletCreateSensorNode" method="get">
	            		<div class="input-field">
	            			<input id="nameSensorNode" type="text" size="50" maxlength="300" title="Insert Sensor Node name" placeholder="Insert Sensor Node name" value="" name="device" >
	            		</div>
						<div class="input-field">
	                        <select id="protocol" name="idProtocol">
	                            <option>Select Protocol</option>
	                        </select>
	                    </div>             	
	            		<div class="center">
	                        <button class="btn" id="buttonCreate">Create</button>
	                    </div>
	            	</form>
	          			 
	            
	            
	            </div>
            
            
            
            
            
	            <div class="drop-down-list card" style="float:right; ">
	                <div class="center">
	                    <h5>Dependent Select Item</h5>
	                </div>
	                <div class="divider"></div>
	                
	         		<form action="ServletAssociateSensorToNode" method="get">
	                    <div class="input-field">
	                        <select id="sensorNode" name="idNodoSensore">
	                            <option>Select Sensor Node</option>
	                        </select>
	                    </div>
	                    <div class="input-field">
	                        <select id="sensor1" name="idSensore1">
	                            <option>Select Sensor 1</option>
	                        </select>
	                    </div>
	                    <div class="input-field">
	                        <select id="sensor2" name="idSensore2">
	                            <option>Select Sensor 2</option>
	                        </select>
	                    </div>
	                    <div class="input-field">
	                        <select id="sensor3" name="idSensore3">
	                            <option>Select Sensor 3</option>
	                        </select>
	                    </div>
	                    <div class="center">
	                        <button class="btn" id="buttonSubmit">Submit</button>
	                    </div>
	                </form>
	                
	            </div>
            
            </div>
            
            
        <div style="clear:both;"></div>
            
        <div class="divider">DIVIDER</div>
        
      
	  	<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.5.1/jquery.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>
        <script type="text/javascript">
            //MENU' DI SELEZIONE NODE SENSORE E 3 SENSORI
        	$(document).ready(function () {        		
                
        		$.ajax({
                	url: "ServletAjaxProtocolList",
                    method: "GET",
                    data: {operation: 'protocolList'},
                    success: function (data, textStatus, jqXHR) {
  
                    	console.log("Protocol List: " + data);
                        let protocolList = $.parseJSON(data);
                        console.log("protocolList: " + protocolList);
                     
                        $.each(protocolList, function (key, value) {	
                    	  	console.log("value id: " + value.idProtocol );
                        	console.log("value name: " + value.protocol);
                            $('#protocol').append('<option value="' + value.idProtocol + '">' + value.protocol + '</option>')
                        });
                        $('select').formSelect();
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        $('#protocol').append('<option>Protocol Unavailable</option>');
                    },
                    cache: false
                });
        		
        		
        		
        		
        		$.ajax({
                	url: "ServletAjaxAssociateSensorToNode",
                    method: "GET",
                    data: {operation: 'sensorNode'},
                    success: function (data, textStatus, jqXHR) {
               	  		console.log("DATAAAAAA: ");
                    	console.log("data SENSOR NODE: " + data);
                        let listaSensorsNode = $.parseJSON(data);
                        console.log("listaSensorsNode: " + listaSensorsNode);
                     
                        $.each(listaSensorsNode, function (key, value) {	
                    	  	console.log("value id: " + value.idSensorNode );
                        	console.log("value name: " + value.device );
                            $('#sensorNode').append('<option value="' + value.idSensorNode + '">' + value.device + '</option>')
                        });
                        $('select').formSelect();
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        $('#sensorNode').append('<option>Sensor Node Unavailable</option>');
                    },
                    cache: false
                });


                $('#sensorNode').change(function () {
                    $('#sensor1').find('option').remove();
                    $('#sensor1').append('<option>Select Sensor 1</option>'); 
                    $('#sensor2').find('option').remove();
                    $('#sensor2').append('<option>Select Sensor 2</option>');

                    let sensorNodeid = $('#sensorNode').val(); //NON MI SERVE L'ID DEL NODO SENSORE
                    console.log("SENSOR NODE ID: " + sensorNodeid);
                    let data = {
                        operation: "sensor1",
                        id: sensorNodeid
                    }; 
                    
                    $.ajax({
                        url: "ServletAjaxAssociateSensorToNode",
                        method: "GET",
                        data: data,
                        success: function (data, textStatus, jqXHR) {
                            console.log("DATA IN SENSOR: " + data);
                            let obj = $.parseJSON(data); //RICEVE LA LISTA DI SENSORI in json, IL JSON IN AUTOMATICO INSERISCE I NOMI DEI CAMPI DELL'OGGETTO COME KIAVI [{"idSensor":1,"name":"Temperatura 1","type":"temp"},{"idSensor":2,"name":"Temperatura 2","type":"temp"},{"idSensor":3,"name":"Temperatura 3","type":"temp"},{"idSensor":4,"name":"Umidità 1","type":"humidity"},{"idSensor":5,"name":"Umidità 2","type":"humidity"},{"idSensor":6,"name":"Umiità 3","type":"humidity"}]
                            console.log("OBJ: " + obj.idSensor)
                            console.log("OBJ.name: " + obj.name)
                            $.each(obj, function (key, value) {
                            	$('#sensor1').append('<option value="' + value.idSensor + '">' + value.name + '</option>') //E' IMPORTANTE SCRIVERE value.idSensor E value.name PERCHE' L'OGGETTO SENSOR HA I CAMPI isSensor e name
                            	console.log("key: " + key );
                            	console.log("value id: " + value.idSensor );
                            	console.log("value name: " + value.name );
                            });
                            $('select').formSelect();
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            $('#sensor1').append('<option>State Unavailable</option>');
                        },
                        cache: false
                    });
                                  
                });
                
                
                $('#sensor1').change(function () {
                    $('#sensor2').find('option').remove();
                    $('#sensor2').append('<option>Select Sensor 2</option>');

                    let sensorNodeid = $('#sensorNode').val(); //NON MI SERVE L'ID DEL NODO SENSORE
                    console.log("SENSOR NODE ID: " + sensorNodeid);
                    let data = {
                        operation: "sensor2",
                        id: sensorNodeid
                    }; //SONO ARRIVATO QUI

                    $.ajax({
                        url: "ServletAjaxAssociateSensorToNode",
                        method: "GET",
                        data: data,
                        success: function (data, textStatus, jqXHR) {
                            console.log("DATA IN SENSOR: " + data);
                            let obj = $.parseJSON(data); //RICEVE LA LISTA DI SENSORI
                            console.log("OBJ: " + obj)
                            console.log("OBJ.name: " + obj.name)
                            $.each(obj, function (key, value) {
                            	$('#sensor2').append('<option value="' + value.idSensor + '">' + value.name + '</option>')
                            	console.log("key: " + key );
                            	console.log("value id: " + value.idSensor );
                            	console.log("value name: " + value.name );
                            });
                            $('select').formSelect();
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            $('#sensor2').append('<option>State Unavailable</option>');
                        },
                        cache: false
                    });
                                  
                });
                                
                
                $('#sensor2').change(function () {
                    $('#sensor3').find('option').remove();
                    $('#sensor3').append('<option>Select Sensor 3</option>');

                    let sensorNodeid = $('#sensorNode').val(); //NON MI SERVE L'ID DEL NODO SENSORE
                    console.log("SENSOR NODE ID: " + sensorNodeid);
                    let data = {
                        operation: "sensor2",
                        id: sensorNodeid
                    }; 

                    $.ajax({
                        url: "ServletAjaxAssociateSensorToNode",
                        method: "GET",
                        data: data,
                        success: function (data, textStatus, jqXHR) {
                            console.log("DATA IN SENSOR: " + data);
                            let obj = $.parseJSON(data); //RICEVE LA LISTA DI SENSORI
                            console.log("OBJ: " + obj)
                            console.log("OBJ.name: " + obj.name)
                            $.each(obj, function (key, value) {
                            	$('#sensor3').append('<option value="' + value.idSensor + '">' + value.name + '</option>')
                            	console.log("key: " + key );
                            	console.log("value id: " + value.idSensor );
                            	console.log("value name: " + value.name );
                            });
                            $('select').formSelect();
                        },
                        error: function (jqXHR, textStatus, errorThrown) {
                            $('#sensor3').append('<option>State Unavailable</option>');
                        },
                        cache: false
                    });
                                  
                });
                
             
                
                
                
                
                /* APPUNTO DA RICORDARE come fare il parse con jsnon
					$.ajax({
					  type: 'GET',
					  url: 'http://example/functions.php',
					  data: {get_param: 'value'},
					  var json = $.parseJSON(j);
					  //si può iterare il dato json var j ='[{"id":"1","name":"test1"},{"id":"2","name":"test2"},{"id":"3","name":"test3"},{"id":"4","name":"test4"},{"id":"5","name":"test5"}]'; 
					  //con $().each
					  $(json).each(function (i, val) {
					    $.each(val, function (k, v) {
					      console.log(k + " : " + v);
					    });
					  }); 
					  
					  oppure: 
					  dataType: 'json',
					  success: function (data) {
					    var names = data
					    $('#cand').html(data);
					  }
					});
                
                */
                
                
                
      //        $(document).on("click", "#buttonSubmit", function() {
   /*	  		$('#buttonSubmit').click(function () {	 
                    console.log("CLIC BUTTOM");
                    
                	let sensorNodeid = $('#sensorNode').val();
                	console.log("SENSOR NODE ID: " + sensorNodeid);
                	let sensor1id = $('#sensor1').val();
                	console.log("SENSOR 1 ID: " + sensor1id);
                    let sensor2id = $('#sensor2').val();
                    console.log("SENSOR 2 ID: " + sensor2id);
                    let sensor3id = $('#sensor3').val();
                    console.log("SENSOR 3 ID: " + sensor3id);
    				
                    let data = {
                        operation: "FINAL",
                        idSN: sensorNodeid,
                       	idSens1 : sensor1id,
                       	idSens2 : sensor2id,
                       	idSens3 : sensor3id
                    }; 	
                	
    				 $.ajax({
                        
    					 url: "ServletAssociateSensorToNode",
                         method: "GET",
                         data: data,                         
                         success: function (data) {
                             console.log("DATA IN FINAL: " + data);
                             alert(data);                            
                         },
                         error: function (jqXHR, textStatus, errorThrown) {
                        	 console.log("ERROREEEE");
                        	 alert('<option>State Unavailable</option>');
                         },
                         cache: false
                     });
    				
                   
                }); 
         */       
         
         			
         		
                
            });
                
    /*            
            $(document).ready(function () {
            	
            	$.ajax({
                	url: "ServletAjaxProtocolList",
                    method: "GET",
                    data: {operation: 'protocolList'},
                    success: function (data, textStatus, jqXHR) {
  
                    	console.log("Protocol List: " + data);
                        let protocolList = $.parseJSON(data);
                        console.log("protocolList: " + protocolList);
                     
                        $.each(protocolList, function (key, value) {	
                    	  	console.log("value id: " + value.idProtocol );
                        	console.log("value name: " + value.protocol);
                            $('#protocol').append('<option value="' + value.idProtocol + '">' + value.protocol + '</option>')
                        });
                        $('select').formSelect();
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        $('#protocol').append('<option>Protocol Unavailable</option>');
                    },
                    cache: false
                });
            	
            	
            });
      
    */  
      
      
      
        </script>
    </body>
</html>

