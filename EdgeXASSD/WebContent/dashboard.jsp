<%@page language="java" contentType="text/html" import="java.sql.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        
      <!--  <meta http-equiv=refresh content="5; url=/dashboard.html" />  Reindirizzare ad una pagina dopo 5 secondi -->  
        
        <title>DASHBOARD EdgeXASSD</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/css/materialize.min.css">
        
        <style type="text/css">
            body{
                background: url(image/roenkae.jpg);
                background-size: cover;
            }
            
            .drop-down-list{
                margin: 150px auto;
                width: 40%;
                padding: 30px;
            }
           
            
        </style>
    </head>
    <body class="red">
        <div class="container">
            
            <!-- FORM PER LA CREAZIONE DEL NODO SENSORE -->
                        
	            <div class="drop-down-list card" style="float:left; ">  
	            	<div class="center">
	                    <h5>Create  Sensor Node</h5>
	                </div>
	            	<div class="divider"></div>
	            	
	            <!--  <form action="ServletCreateSensorNode"  method="get" > -->	
	            	<form> 
	            		<div class="input-field">
	            			<input id="nameSensorNode" type="text" size="50" maxlength="300" title="Insert Sensor Node name" placeholder="Insert Sensor Node name" value="" name="device" >
	            		</div>
						<div class="input-field">
	                        
	                        <select id="protocol" name="idProtocol">
	                            <option>Select Protocol</option>
	                        </select>
	                        <!--
	                        <select style="" title="Seleziona un protocollo" size="1" name="idProtocol">
							<option selected="selected" value="0">Select Protocol</option> 
								<c:forEach items="${applicationScope.protocolList}" var="protocol" > 
									<option value="${protocol.idProtocol}"> ${protocol.protocol} </option> 
								</c:forEach>
							</select>
	                        -->
	                        	                        
	                    </div>             	
	            		<div class="center">
							<input class="btn" type="button" value="Submit" id="buttonCreate">          		
	                    <!-- <button class="btn" id="buttonCreate" >Create</button> CON <BUTTON> NON FUNZIONA IL redirect NELLE FUNZIONI JQUERY window.location.href = "dashboard.jsp" --> 
	                    </div>
	            	</form>
	          			 	            
	            </div>
                     
                     
                <!-- FORM PER LA ASSOCIAZIONE NODO SENSORE - SENSRI-->     
                          
	            <div class="drop-down-list card" style="float:right; ">
	                <div class="center">
	                    <h5>Associate Sensor to Sensor Node</h5>
	                </div>
	                <div class="divider"></div>
	                
	               
	         		<!--  <form action="ServletAssociateSensorToNode" method="get"> -->
	         		
	         		<form >
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
	                        <input class="btn" type="button" value="Submit" id="buttonSubmit">
	                       <!--  <button class="btn" id="buttonSubmit">Submit</button> -->
	                    </div>
	                </form>
	                
	    <!--      	<p>&nbsp;</p>
  		  			<p align="center" id="success"> 
  						<font size="4"> MESSAGGIO: ${messaggio} </font> 
  					</p>
	    -->                   
	                
	                
	            </div>
            
            </div>
            
            
        <div style="clear:both;"></div>
            
        <div class="divider"></div>
        
      
	  	<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.5.1/jquery.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script> <!-- Utilizzata per i drop down menù -->
  	    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script> <!-- Utilizzata per la finestra popout con gli avvisi in seguito all'associazione o alla creazione dei nodi sensori -->
  	    <script src="sweetalert2.all.min.js"></script>
  	    
        <script type="text/javascript">
            
        	$(document).ready(function () {        		
                
        		//MENU' SELEZIONE PROTOCOLLI
        		
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
        		
        		//MENU' DI SELEZIONE NODE SENSORE E 3 SENSORI	
        		
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
                    $('#sensor3').find('option').remove();
                    $('#sensor3').append('<option>Select Sensor 3</option>');
                    
                    let sensorNodeid = $('#sensorNode').val(); 
                    console.log("SENSOR NODE ID: " + sensorNodeid);
                    let data = {
                        operation: "sensor1",
                        idSensorNode: sensorNodeid //INVIO ANCHE L'ID DEL NODO SENSORE
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
                            	$('#sensor1').append('<option value="' + value.idSensor + '">' + value.name + '</option>') //E' IMPORTANTE SCRIVERE value.idSensor E value.name PERCHE' L'OGGETTO SENSOR HA I CAMPI idSensor e name
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
                    
                	 console.log("SONO IN SENSOR 1 ");
                	 
                	$('#sensor2').find('option').remove();
                    $('#sensor2').append('<option>Select Sensor 2</option>');

                    let sensorNodeid = $('#sensorNode').val(); //NON MI SERVE L'ID DEL NODO SENSORE
                    console.log("SENSOR NODE ID IN SENSOR 1: " + sensorNodeid);
                    let data = {
                        operation: "sensor2",
                        idSensorNode: sensorNodeid,
                    //    idSensor1: sensor1 //INVIO IL SENSORE SCELTO IN MODO TALE DA ESCLUDERLO DALLA LISTA DI SENSORI CHE SARà INDICATA PER LA TERZA SCELTA sensor3
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
                        idSensorNode: sensorNodeid,
                     //   idSensor2: sensor2
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

				//FARE FUNZIONE CLIC BOTTOM CREATE SENSOR NODE
                $('#buttonCreate').click(function () {	 
      			    console.log("CLIC BUTTOM");
      			    
      				let nameSensorNode = $('#nameSensorNode').val();
      				console.log("SENSOR NODE: " + nameSensorNode);
      				let protocol = $('#protocol').val();
      				console.log("PROTOCOL ID: " + protocol);
      			          				
      			    let dati = {
      			    	device : nameSensorNode,
      			    	idProtocol : protocol
      			    };
      			  //console.log("DEVICE: " + dati.device);
      				 $.ajax({      					 
      					 url: "ServletCreateSensorNode",
      					 dataType: "json",
      					 method: "POST",
      			         data: dati,    //$("#idForm").serialize()                    
      			         async: false, 
      			         success: function (messaggio, textStatus, jqXHR) { //messaggio è il messaggio ricevuto dalla servlet
      			        	console.log("SUCCESSS");
      			        	
      			         //	alert(messaggio); 
      			        	
      			        	//INSERIRE IL MESSAGGIO CON ANIMAZIONE
      			          	let m = messaggio.messaggio;      			   
    			      		console.log("messaggio: " + messaggio.messaggio);
	      			       	if(messaggio.result == true){ //associazione Nodo Sensore Sensore è andata a buon fine
	      			       		console.log("result: " + messaggio.result);
			      			  	let timerInterval
			      			    Swal.fire({
			      			    	icon: 'success',
			      			    //  title: m,
			      			    	text: m,
			      			    //  html: 'I will close in <b>3000</b> milliseconds.',
			      			      	timer: 2000,
			      				    showConfirmButton: true, //non mostrare il tasto ok
				      			  //confirmButtonText: 'Ho capito!'
			      			      	timerProgressBar: true,
			      			   /* 	didOpen: () => {
			      			        	Swal.showLoading()
			      			        	const b = Swal.getHtmlContainer().querySelector('b')
			      			        	timerInterval = setInterval(() => {
			      			          		b.textContent = Swal.getTimerLeft()
			      			        	}, 100)
			      			      	}, 
			      			      	willClose: () => {
			      			        	clearInterval(timerInterval)
			      			      	} */
			      			    }).then((result) => {
			       			    	if (result.dismiss === Swal.DismissReason.timer) {
			      			        	console.log('I was closed by the timer')
			      			      		window.location.href = "dashboard.jsp";
			      			      	}
			      			      	else{
			      			    		window.location.href = "dashboard.jsp";
			      			      	}
			      			    })     			   	
    			       		}
    			       		else{ //associazione Nodo Sensore Sensore NON è andata a buon fine      			       			
	      			       		console.log("result: " + messaggio.result);
	      			       		console.log("messaggio in else: " + messaggio.messaggio);
    			       			let timerInterval
			      			    Swal.fire({
			      			    	icon: 'error',
			      			   // 	title: m,
			      			    	text: m,
			      			   //     html: 'I will close in <b>3</b> milliseconds.',
			      			      	timer: 2000,
			      				  //showConfirmButton: true, //non mostrare il tasto ok
				      			  //confirmButtonText: 'Ho capito!'
			      			      	timerProgressBar: true,
			      			   /* 	didOpen: () => {
			      			        	Swal.showLoading()
			      			        	const b = Swal.getHtmlContainer().querySelector('b')
			      			        	timerInterval = setInterval(() => {
			      			          		b.textContent = Swal.getTimerLeft()
			      			        	}, 100)
			      			      	}, 
			      			      	willClose: () => {
			      			        	clearInterval(timerInterval)
			      			      	} */
			      			    }).then((result) => {
			       			    	if (result.dismiss === Swal.DismissReason.timer) {
			      			        	console.log('I was closed by the timer')
			      			      		window.location.href = "dashboard.jsp";
			      			      	}
			      			      	else{
			      			    		window.location.href = "dashboard.jsp";
			      			      	}
			      			    })       			       		
    			       		}
      			        	      			        	
      			         },
      			         error: function (jqXHR, textStatus, errorThrown) {
      			        	 console.log("ERROREEEE");
      			        	 alert('<form>State Unavailable</form>');
      			         },
      			         cache: false
      			     });
      				
      			   
      			}); 
                
                
                
      		  //$(document).on("click", "#buttonSubmit", function() {
     	      $('#buttonSubmit').click(function () {	 
      			    console.log("CLIC BUTTOM");
      			    
      				let sensorNodeid = $('#sensorNode').val();
      				console.log("SENSOR NODE ID: " + sensorNodeid);
      				let sensor1id = $('#sensor1').val();
      				console.log("SENSOR 1 ID: " + sensor1id);
      			    let sensor2id = $('#sensor2').val();
      			    console.log("SENSOR 2 ID: " + sensor2id);
      			    let sensor3id = $('#sensor3').val();
      			    console.log("SENSOR 3 ID: " + sensor3id);
      				
      			    let dati = {
      			        operation: "FINAL",
      			        idNodoSensore: sensorNodeid,
      			        idSensore1 : sensor1id, //sensor1
      			        idSensore2 : sensor2id, //sensor2
      			        idSensore3 : sensor3id  //sensor3
      			    };
      			  //	console.log("DATA: " + dati.operation);
      				 $.ajax({      					 
      					 url: "ServletAssociateSensorToNode",
      			         method: "POST",
      			   	     dataType: "json",
      			         data: dati,    //$("#idForm").serialize()                    
      			         async: false, //BASTARDO MI HA FATTO DANNARE
      			         success: function (messaggio, textStatus, jqXHR) { //messaggio è il messaggio ricevuto dalla servlet costituito da 4 campi: result che indica se l'associazione è andata a buon fine o ha sollevato errori; messaggio che contiene il messaggio da stampare; redirect che è una variabile booleana che indica se fare o meno il redirect; redirect_url che indica la pagina a cui effettuare il redirect
      			        	console.log("SUCCESSS");
      			        	console.log("DATA IN FINAL: " + messaggio);
      			              			       		
      			       	/*	PRIMA SOLUZIONE
      			       		alert(messaggio.messaggio) 	
      			        	window.location.href = "dashboard.jsp"; //affichè funzioni è necessario che il bottone, che attiva la funzione al click, sia in <input type=button> e NON <button>
      			      	*/		        
      			            			      	
	      			       	//SECONDA SOLUZIONE
	      			        let m = messaggio.messaggio;      			   
      			      		console.log("messaggio: " + messaggio.messaggio);
	      			       	if(messaggio.result == true){ //associazione Nodo Sensore Sensore è andata a buon fine
	      			       		console.log("result: " + messaggio.result);
			      			  	let timerInterval
			      			    Swal.fire({
			      			    	icon: 'success',
			      			    //  title: m,
			      			    	text: m,
			      			    //  html: 'I will close in <b>3000</b> milliseconds.',
			      			      	timer: 2000,
			      				    showConfirmButton: true, //non mostrare il tasto ok
				      			  //confirmButtonText: 'Ho capito!'
			      			      	timerProgressBar: true,
			      			   /* 	didOpen: () => {
			      			        	Swal.showLoading()
			      			        	const b = Swal.getHtmlContainer().querySelector('b')
			      			        	timerInterval = setInterval(() => {
			      			          		b.textContent = Swal.getTimerLeft()
			      			        	}, 100)
			      			      	}, 
			      			      	willClose: () => {
			      			        	clearInterval(timerInterval)
			      			      	} */
			      			    }).then((result) => {
			       			    	if (result.dismiss === Swal.DismissReason.timer) {
			      			        	console.log('I was closed by the timer')
			      			      		window.location.href = "dashboard.jsp";
			      			      	}
			      			      	else{
			      			    		window.location.href = "dashboard.jsp";
			      			      	}
			      			    })     			   	
      			       		}
      			       		else{ //associazione Nodo Sensore Sensore NON è andata a buon fine      			       			
	      			       		console.log("result: " + messaggio.result);
	      			       		console.log("messaggio in else: " + messaggio.messaggio);
      			       			let timerInterval
			      			    Swal.fire({
			      			    	icon: 'error',
			      			   // 	title: m,
			      			    	text: m,
			      			   //     html: 'I will close in <b>3</b> milliseconds.',
			      			      	timer: 2000,
			      				  //showConfirmButton: true, //non mostrare il tasto ok
				      			  //confirmButtonText: 'Ho capito!'
			      			      	timerProgressBar: true,
			      			   /* 	didOpen: () => {
			      			        	Swal.showLoading()
			      			        	const b = Swal.getHtmlContainer().querySelector('b')
			      			        	timerInterval = setInterval(() => {
			      			          		b.textContent = Swal.getTimerLeft()
			      			        	}, 100)
			      			      	}, 
			      			      	willClose: () => {
			      			        	clearInterval(timerInterval)
			      			      	} */
			      			    }).then((result) => {
			       			    	if (result.dismiss === Swal.DismissReason.timer) {
			      			        	console.log('I was closed by the timer')
			      			      		window.location.href = "dashboard.jsp";
			      			      	}
			      			      	else{
			      			    		window.location.href = "dashboard.jsp";
			      			      	}
			      			    })       			       		
      			       		}
      			             				      			      	
				      		/* versione più compatta della finestra popout
				      				Swal.fire({
				      				  	icon: 'success',
						      			// title: ,
						      			text: m,
						      			showConfirmButton: false,
						     	 //	    confirmButtonText: 'Yes, delete it!'
						      			timer: 3000
				      			      }).then((result) => {
				            		  	if (result.dismiss === Swal.DismissReason.timer) {
				            			        console.log('I was closed by the timer')
				            			      	window.location.href = "dashboard.jsp";
				            			}
				            			else{
				            			    	window.location.href = "dashboard.jsp";
				            			}   	    
				   			    	  })
				      		*/	      	
		      	    			    
		      			       		
		      			       	/*	console.log("m.redirect: " + messaggio.redirect);
		      			  			if (messaggio.redirect) {
		  			        			console.log("ENTRO NELL IF" );
		  			        			window.location.href = messaggio.redirect_url;
		  			            	//  window.location.href = "dashboard.jsp";
		  			                }
		  			       */	
		      			       		     			       		
		      			   /* 		//aspetto 3,1 secondi, sarebbe il tempo necessario per far apparire il messaggio che dura 3 secondi   		
		       			        	setTimeout(function() {
		      			        	 	console.log("IN FUNCTION TIMEOUT");	
		      			        	  	window.location.href = "dashboard.jsp";
		      			        	}, 3100);
		      			    */         					
      			         },
      			         error: function (jqXHR, textStatus, errorThrown) {
      			        	 console.log("ERROREEEE");
      			        	 alert('State Unavailable');
      			        	 window.location.href = "dashboard.jsp"
      			         },
      			         cache: false
      			     });
      				
      			   
      			}); 
                
    
         		
                
            });
                
        
      
     
            
    
		/*    var Dashboard = {};
		      
		    Dashboard.sendSn = (function(){
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
			        idNodoSensore: sensorNodeid,
			        idSensore1 : sensor1id, //sensor1
			        idSensore2 : sensor2id, //sensor2
			        idSensore3 : sensor3id  //sensor3
			    }; 	
				
				 $.ajax({
					 
					 url: "ServletAssociateSensorToNode",
			         method: "GET",
			         data: message,                         
			         success: function (message) {
			             console.log("DATA IN FINAL: " + data);
			             alert(message);                            
			         },
			         error: function (jqXHR, textStatus, errorThrown) {
			        	 console.log("ERROREEEE");
			        	 alert('<form>State Unavailable</form>');
			         },
			         cache: false
			     });
		    });
			*/	
	
    
        </script>
    </body>
</html>

