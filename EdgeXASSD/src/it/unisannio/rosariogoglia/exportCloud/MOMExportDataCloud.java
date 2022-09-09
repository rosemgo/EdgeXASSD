package it.unisannio.rosariogoglia.exportCloud;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

import it.unisannio.rosariogoglia.databaseUtil.DatabaseUtil;

public class MOMExportDataCloud extends Thread{

	public MOMExportDataCloud() {}
	
	public void run() {
		
	//	String broker = "tcp://broker.mqttdashboard.com:1883"; 
		String broker = "ssl://b-413cd668-b305-412f-8d1f-fa5db15c2aba-1.mq.eu-south-1.amazonaws.com:8883";
		String topic = "EdgeXROS"; 
		int qos = 1;
		String clientId= "MOM InfluxDB Cloud";
	    MemoryPersistence persistence = new MemoryPersistence();
	     
	    
	    // You can generate an API token from the "API Tokens Tab" in the UI
	    // String token = System.getenv("QV_kNbM52QA8yTWUgsVyZ7cARaCSbOti5IESfyB9NludugSjSdfpqSQOXLBoRnU8aYyfFv7AAOVK2gjdODFudg==");
	    
	    //TOKEN UNICO
	    // String token = "QV_kNbM52QA8yTWUgsVyZ7cARaCSbOti5IESfyB9NludugSjSdfpqSQOXLBoRnU8aYyfFv7AAOVK2gjdODFudg==";
	    //   System.out.println("token: " + token);
	       
	       String token = "wmCu42trECNPDIUeLRqqbn2i50YEkGvZ7aPFvuR6M-nDkDpX5pCqK0raQdL3lkIU1QcRsJzjSPcnS7AR_yPhyA==";
	       
	       String bucket = "EdgeX-SensorNode-Monitoring";
	       String org = "rosariogoglia@gmail.com";

	       InfluxDBClient client = InfluxDBClientFactory.create("https://eu-central-1-1.aws.cloud2.influxdata.com", token.toCharArray());
	   
	    
	      try { 
				
	      		MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
				
	      		MqttConnectOptions connOpts = new MqttConnectOptions();
		        connOpts.setCleanSession(true); //no persistent session
		        connOpts.setKeepAliveInterval(1000);
		        
		        //parametri di connessione ad ActiveMQ su Amazon AWS
		        connOpts.setUserName("admin");
		        connOpts.setPassword("rosariomarcofrancesco".toCharArray());
		        
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
		            public void messageArrived(String topic, MqttMessage message) throws MqttPersistenceException, MqttException, JSONException, ClassNotFoundException, SQLException, IOException {
		          
		            	//L'intero messaggio: {"profileName":"Test-Device-MQTT-Profile","apiVersion":"v2","readings":[{"profileName":"Test-Device-MQTT-Profile","origin":1658578376503387770,"valueType":"Object","resourceName":"json","objectValue":"[{\"nameSensor\":\"TEMPERATURA 1\",\"data\":\"Sat Jul 23 14:10:26 CEST 2022\",\"type\":\"temperature\",\"value\":39.622178153642984,\"nameNode\":\"DeviceMQTT\"},{\"nameSensor\":\"TEMPERATURA 2\",\"data\":\"Sat Jul 23 14:10:26 CEST 2022\",\"type\":\"temperature\",\"value\":9.591273316256629,\"nameNode\":\"DeviceMQTT\"},{\"nameSensor\":\"TEMPERATURA 3\",\"data\":\"Sat Jul 23 14:10:26 CEST 2022\",\"type\":\"temperature\",\"value\":-2.2434264189229687,\"nameNode\":\"DeviceMQTT\"}]","id":"aa32a430-3358-416f-8450-f08732ae13f6","deviceName":"DeviceMQTT","value":""}],"origin":1658578376503410914,"id":"5ddd9a3f-18bf-4190-a8ff-db55b8d15080","sourceName":"json","deviceName":"DeviceMQTT"}
		            	System.out.println("Messaggio ricevuto: " + topic + ": " + new String(message.getPayload()) ); //converte il payload del messaggio in stringa     
		            	
		                JSONObject jsonmsg = new JSONObject(new String(message.getPayload()));
		                System.out.println("Messaggio json RICEVUTO: " + jsonmsg);
		                
		              /*JsonObject jsobj = new JsonObject();
		                Gson jsob = new Gson();
		                JsonElement l = jsob.toJsonTree(new String(message.getPayload()));
		              */
		                	                
		              //"readings" è un array di un solo elemento
		                JSONObject readings = jsonmsg.getJSONArray("readings").getJSONObject(0); //l'array reading è costituito da un solo elemento con più campi
		                System.out.println("readings: " + readings);
		                
		              // valueReading contiene il campo value o objectValue che contiene la misurazione effettuata da ogni sensore associato al nodo: -->
		              // --> "objectValue":"[{\"nameSensor\":\"TEMPERATURA 1\",\"data\":\"Sat Jul 23 14:10:26 CEST 2022\",\"type\":\"temperature\",\"value\":39.622178153642984,\"nameNode\":\"DeviceMQTT\"},{\"nameSensor\":\"TEMPERATURA 2\",\"data\":\"Sat Jul 23 14:10:26 CEST 2022\",\"type\":\"temperature\",\"value\":9.591273316256629,\"nameNode\":\"DeviceMQTT\"},{\"nameSensor\":\"TEMPERATURA 3\",\"data\":\"Sat Jul 23 14:10:26 CEST 2022\",\"type\":\"temperature\",\"value\":-2.2434264189229687,\"nameNode\":\"DeviceMQTT\"}]","id":"aa32a430-3358-416f-8450-f08732ae13f6","deviceName":"DeviceMQTT","value":""}]
		                String valueReadingsString = null; 
		                if(readings.getString("value").equals("")) { //i messaggi inviati con protocollo MQTT e REST hanno il campo 'value' vuoto, ed il campo 'objectValue' contiene il messaggio
		                	
		                	JSONArray objectValue = readings.optJSONArray("objectValue"); //restituisce null se objectValue non è un array
                	        
		                	if(objectValue!=null) { 
		                		//è un array
		                		System.out.println("objectValue è un JSONarray");
	                	        valueReadingsString = objectValue.toString();
		                	}
		                	else {
		                		System.out.println("objectValue è un JSONobject");
                	    		valueReadingsString = readings.getString("objectValue");
    		                	System.out.println("ObjectValue: " + valueReadingsString);		                		
		                	}        		
	
		                }
		                else { 
		                	valueReadingsString = readings.getString("value");
		                	System.out.println("Value: " + valueReadingsString);
		                }
		                
		                JSONArray valueReadingsJson = new JSONArray(valueReadingsString);//N.B. TRASFORMO IN JSON SOLO IL CAMPO objectValue/Value
		                System.out.println("Array Value Json: " + valueReadingsJson);
		                
		                
		                //ESTRAGGO DALL'ARRAY valueReading LA MISURAZIONE DI OGNI SINGOLO SENSORE
		                //{\"nameSensor\":\"TEMPERATURA 1\",\"data\":\"Sat Jul 23 14:10:26 CEST 2022\",\"type\":\"temperature\",\"value\":39.622178153642984,\"nameNode\":\"DeviceMQTT\"}
		                for(int i=0; i<valueReadingsJson.length(); i++) {
		                	JSONObject sensorValue = valueReadingsJson.getJSONObject(i); //estraggo la misurazione effettuata del singolo sensore 
		                	
		                	if(!sensorValue.has("date")) {
		                	
			                	System.out.println("Singola lettura: " + sensorValue);
			                	
			                	Integer idSensorNode = sensorValue.getInt("idSensorNode");
			                	String nameSensorNode = sensorValue.getString("nameNode");
			                	Integer idSensor = sensorValue.getInt("idSensor");
			                	String nameSensor = sensorValue.getString("nameSensor");
				                String type = sensorValue.getString("type");
				                String unitOfMeasurement = sensorValue.getString("unitOfMeasurement");
				                Float value = (float) sensorValue.getDouble("value"); 
				                System.out.println("Valore: " + value);
				                System.out.println("nameSensor: " + nameSensor);
				                System.out.println("NameSensorNode: " + nameSensorNode);
				                System.out.println("Type: " + type);
				                System.out.println("Unit Of Measurement: " + unitOfMeasurement);
				             
				                //EFFETTUO LA SCRITTURA NEL DATABASE INFLUX DB				                
		                	
	//			                Point point = Point
	//				              		  .measurement(nameSensorNode)
	//				              		  .addTag("host", clientId)
	//				              		  .addTag("Sensor Node", nameSensorNode)
	//				              		  .addTag("Sensor", nameSensor)
	//				              		  .addTag("type", type)
	//				              		  .addField(type, value) //value prelevato dal messaggio pubblicato nel topic
	//				              		  .time(Instant.now(), WritePrecision.NS);
	//				              
	//				              //creare più point, uno per ogni dato del sensore specifico
	//				              WriteApiBlocking writeApi = client.getWriteApiBlocking();
	//				              writeApi.writePoint(bucket, org, point);    
	//				      
	//				              System.out.println("SCRIVO IN INFLUXDB");
					              
					                         
					              //SCRITTURA NEL DATABASE LOCALE 
					                
					              Connection connection = DatabaseUtil.getConnection(); 
					              connection.setAutoCommit(false);	
					              
					              String sql = "INSERT INTO measurement (value, unitOfMeasurement, type, time, sensorNode_idsensorNode, sensor_idsensor) VALUES (?, ?, ?, ?, ?, ?)";
					              PreparedStatement pstmt = connection.prepareStatement(sql);
					              pstmt.setFloat(1, Float.valueOf(value));
					              pstmt.setString(2, unitOfMeasurement);
					              pstmt.setString(3, type);
					              pstmt.setDate(4, new java.sql.Date(new Date().getTime()));
					              pstmt.setInt(5, Integer.valueOf(idSensorNode));
					              pstmt.setInt(6, Integer.valueOf(idSensor));
					             				             
					              
					              System.out.println(pstmt);
					              
					              int insertRows = pstmt.executeUpdate();
					      		  System.out.println("righe inserite: "+ insertRows);
					      		  connection.commit();
				             
		                	}
		                	else
		                		System.out.println("DATA : " + sensorValue.get("date"));
		                	}
     
		            }
		        
		            @Override
		            // Called when an outgoing publish is complete
		            public void deliveryComplete(IMqttDeliveryToken token) { 
		                System.out.println("delivery complete " + token);
		            }

		        });
				
				sampleClient.subscribe(topic, 1); // subscribe al topicCommand con QoS = 1
				
	      	} catch (MqttSecurityException e) {
				e.printStackTrace();
			} catch (MqttException e) {
				 System.out.println("reason "+e.getReasonCode());
	           System.out.println("msg "+e.getMessage());
	           System.out.println("loc "+e.getLocalizedMessage());
	           System.out.println("cause "+e.getCause());
	           System.out.println("excep "+e);
	           e.printStackTrace();
			}
		
		
	}
	
	
	
	
}
