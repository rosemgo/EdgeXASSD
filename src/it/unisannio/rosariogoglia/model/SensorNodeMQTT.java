package it.unisannio.rosariogoglia.model;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SensorNodeMQTT extends SensorNode{

		
	
public void run(){
			
		//IL BROKER UTILIZZATO E' MOSQUITTO INTERNO AD EDGEX (COME MICROSERVIZIO)
	//	String broker = "tcp://192.168.204.133:1883"; //USARE QUESTO INDIRIZZO PER EDGEX SU MACCHINA VIRTUALE LOCALE
		String broker = "tcp://15.160.35.22:1883"; //USARE QUESTO INDIRIZZO PER EDGEX SU CLOUD AWS
		String commandTopic = "command/"+ this.device +"/#"; //cambiare con CommandTopic   
		String responseTopic = "command/response/"+ this.device +"/"; //cambiare con ResponseTopic 
		String dataTopic = "incoming/data/" + this.device +"/"; //this.device � il nome del Nodo Sensore, e deve coincidere con il NameDevice interno ad EdgeX
		int qos = 1;
		String clientId= this.device + this.idSensorNode;
        MemoryPersistence persistence = new MemoryPersistence();
		
        try {
        	
        	MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
			MqttConnectOptions connOpts = new MqttConnectOptions();
	        connOpts.setCleanSession(true); //no persistent session
	        connOpts.setKeepAliveInterval(1000);
	        
	        System.out.println("Connecting to broker: "+broker);
	       
			sampleClient.connect(connOpts);						
			System.out.println("Connected");
		     			
			
			
		sampleClient.setCallback(new MqttCallback() {
		        
	            @Override
	            // Called when the client lost the connection to the broker
	            public void connectionLost(Throwable cause) { 
	                System.out.println("client lost connection " + cause);
	            }
	        
	            @Override
	            public void messageArrived(String topic, MqttMessage message) throws MqttPersistenceException, MqttException, JSONException {
	          
	            	System.out.println("INTERO MESS: " + message.toString());
	            	
	            	System.out.println("Messaggio ricevuto: " + topic + ": " + new String(message.getPayload()) ); //converte il payload del messaggio in stringa     

	            	String[] words = topic.split("/");
	            	String cmd = words[2];
	                String method = words[3];
	                String uuid = words[4];
	                
	                JSONObject jsonmsg = new JSONObject();
	                
	                if(method.equals("set")) {
	                	switch (cmd) {
							case "message":
								jsonmsg.put("message", new String(message.getPayload()));
							break;
							case "json":
								jsonmsg.put("json", new String(message.getPayload()));
							break;
						default:
							break;
						}	                	
	                }
	                else {
	                	switch (cmd) {
							case "ping":
								jsonmsg.put("ping", "PONG, time: " + System.currentTimeMillis());
							break;
							case "message":
								jsonmsg.put("message", "Sono il Nodo Sensore "+device+" "+idSensorNode+"");
							break;		
							case "randnum": 
								jsonmsg.put("randnum", 12.123); //CAMBIARE CON FLOAT ED UN FOR CHE INVIA VALORI DI MISURAZIONE PER OGNI SENSORE
				                break;
				            case "json":{ //creo un messaggio json per ogni sensore ed invio la misurazione //SI PU� CAMBIARE
				            								    	
				            	//creo un messaggio json per ogni sensore ed invio la misurazione //SI PU� CAMBIARE
						          
				            	JSONArray jsonArray = new JSONArray();	
				            	
				            	for(int i=0; i<sensors.size(); i++) {
				            		JSONObject jsonmsg2 = new JSONObject();
				            		jsonmsg2.put("idSensorNode", idSensorNode); 
				            		jsonmsg2.put("nameNode", device); //nome del sensore appartenente al nodo sensore
				            		jsonmsg2.put("idSensor", sensors.get(i).getIdSensor()); 
				            		jsonmsg2.put("nameSensor", sensors.get(i).getName()); 
							    	jsonmsg2.put("type", sensors.get(i).getType()); 
							    	jsonmsg2.put("value", sensors.get(i).measurement());
							    	jsonmsg2.put("unitOfMeasurement", sensors.get(i).getUnitOfMeasurement());
							    //	jsonmsg2.put("data", new Date());
				            		
				            		jsonArray.put(jsonmsg2);
				            	}
				            	
				            	//separo la data in modo da non ripeterla per ogni sensore ma la inserisco nel messaggio una volta sola
						    	JSONObject date = new JSONObject();
						    //	date.put("date", new Date());
						    	String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
						        System.out.println("DATA DELLA MISURAZIONE: " + timeStamp);
						    	
						        date.put("date", timeStamp);
						        
						        jsonArray.put(date); //aggiungo la data solo una volta	
				            	
				            	jsonmsg.put("json", jsonArray.toString());
				            								    
				                break;	
				            }
						default:
							break;
						}
	                	
	                }

	               
	               System.out.println("MESSAGGIO MODIFICATO: " + jsonmsg.toString());
	               
	               //uuid � indicato dal device in edgeX che invia il comando 
		           //sampleClient.publish("command/response/mqtt1/" + uuid, jsonmsg.toString().getBytes(), 2, false); // QoS = 2
		           sampleClient.publish(responseTopic + uuid, jsonmsg.toString().getBytes(), 2, false); 
		           
	            }
	            
	            @Override
	            // Called when an outgoing publish is complete
	            public void deliveryComplete(IMqttDeliveryToken token) { 
	                System.out.println("delivery complete " + token);
	            }

	        });
        	
			sampleClient.subscribe(commandTopic, 1); // subscribe al topicCommand con QoS = 1
			//sampleClient.subscribe("command/mqtt1/#", 1);  //per abilitare il multilevel
	
			
			//INVIO ASYNC EVENT				
			
			long time1 = System.currentTimeMillis();		
			long time2 = 0;
			String resource = "json"; //la risorsa inviata come evento async � sempre json
		
	//******QUI VA PROTETTO IL THREAD*****		
//			while(!exitThread) { //VARIABILE BOLEANA USATA PER PROTEGGERE IL THREAD, SETTARE QUESTA VARIABILE A FALSE QUANDO SI VUOLE UCCIDERE IL THREAD, OVVIAMENTE IL METODO RUN NN SAR� INTERROTTO SUBITO COME CON UN INTERRUPT.  
//			while(!Thread.interrupted()) {	//PROTEGGO IL THREAD DA UNA INTERRUZIONE IMPROVVISA

			
			//ogni 5 secondi invio un PONG sul topic DataTopic. Dal lato di EdgeX c'� il dispositivo MQTT-custom-device che � iscritto come broker al topic DataTopic
			while(true) {
				
				time2 = System.currentTimeMillis();
						    
			    if(time2 - time1 > 30000) {
		    	
			    	
			    	//Creo jsonArray, ad ogni aggiungo il singolo jsonmsg nell'array. 
			    	//Poi fuori dal for invoco una sola volta il publish ed invio un solo messaggio che contiene le misurazioni di tutti i sensori associati al nodo
			    	JSONArray jsonArray = new JSONArray();				    	
			    	
			    	for(int i=0; i<this.sensors.size(); i++) {
	            					    	
				    	JSONObject jsonmsg = new JSONObject();
				    	jsonmsg.put("idSensorNode", this.idSensorNode); 
				    	jsonmsg.put("nameNode", this.device); //nome del Sensor NODO che deve coincidere con il nome del Device in terno ad EdgeX 
				    	jsonmsg.put("idSensor", this.sensors.get(i).getIdSensor()); 
				    	jsonmsg.put("nameSensor", this.sensors.get(i).getName()); //nome del sensore appartenente al nodo sensore
				    	jsonmsg.put("type", this.sensors.get(i).getType()); 
				    	jsonmsg.put("value", this.sensors.get(i).measurement());
				    	jsonmsg.put("unitOfMeasurement", sensors.get(i).getUnitOfMeasurement());
				    //	jsonmsg.put("data", new Date());
				    	
				    	System.out.println("MESS: " + jsonmsg.toString());
				    	System.out.println("DataTOpic: " + dataTopic + resource);
				    						    	
				    	jsonArray.put(jsonmsg);			    	

			    	}
			    	
			    	//separo la data in modo da non ripeterla per ogni sensore ma la inserisco nel messaggio una volta sola
			    	JSONObject date = new JSONObject();
//			    	date.put("date", new Date());
			    	String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
			        System.out.println("DATA DELLA MISURAZIONE: " + timeStamp);
			    	
			        date.put("date", timeStamp);
			    	jsonArray.put(date); //aggiungo la data solo una volta	
			    	
			    	for(int i=0; i<jsonArray.length(); i++) {
			    		JSONObject j = jsonArray.getJSONObject(i);
			    		System.out.println("ELEMENTO " + i + " : "  + j.toString());
			    	}
			    	
			    	//DataTopic --> incoming/data/mqtt1/resource aggiungere la resource da inviare
			    	//N.B. incoming/data/mqtt1/json mqtt1 � il nome del dispositivo interno ad edgeX
					sampleClient.publish(dataTopic + resource, jsonArray.toString().getBytes() , 1, // QoS = 2
								                false);
					System.out.println("INVIO MESSAGGIO DATA TOPIC MULTI");
			    	
					time1 = System.currentTimeMillis();
			    	
			    }
				
				
			}	
			
	//}	
    //    }
		   
		} catch (MqttSecurityException e) {
			e.printStackTrace();
		} catch (MqttException e) {
		     System.out.println("THREAD UCCISO");
			 System.out.println("reason "+e.getReasonCode());
	         System.out.println("msg "+e.getMessage());
	         System.out.println("loc "+e.getLocalizedMessage());
	         System.out.println("cause "+e.getCause());
	         System.out.println("excep "+e);
	       //  e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

        
	
		
}
	
		

}
