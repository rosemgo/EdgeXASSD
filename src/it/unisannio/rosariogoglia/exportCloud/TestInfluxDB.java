package it.unisannio.rosariogoglia.exportCloud;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

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

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.Statement;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

import it.unisannio.rosariogoglia.databaseUtil.DatabaseUtil;


//QUESTA CLASSE PUò ESSERE UN THREAD LANCIATO NEL CONTEXT INITIALIZER O NELLA SERVLET CHE AVVIA L'APP


public class TestInfluxDB {
  public static void main(final String[] args) throws InterruptedException {

	  
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
	          
	            	System.out.println("Messaggio ricevuto: " + topic + ": " + new String(message.getPayload()) ); //converte il payload del messaggio in stringa     
	            	
	            	//GIOCARE CON I CAMPI READING DEL MESSAGGIO
	            		            	
	            	
	                JSONObject jsonmsg = new JSONObject(new String(message.getPayload()));
	                System.out.println("Messaggio json RICEVUTO: " + jsonmsg);
	                
	                //"readings" è un array di 1 solo elemento
	                JSONObject obj = jsonmsg.getJSONArray("readings").getJSONObject(0); //l'array reading è costituito da un solo elemento con più campi
	                System.out.println("obj: " + obj);
	                
	                String j;
	                if(obj.getString("value").equals("")) { //i messaggi inviati con protocollo MQTT hanno il campo value vuoto, ed il campo objectValue contine il messaggio
	                	j = obj.getString("objectValue");
	                }
	                else { 
	                	j = obj.getString("value");
	                }
	                
	                 //prelevo il campo objectValue che contiene nome del sensore, tipologia, valore misurato
	                System.out.println("TUTTO: " + j);
	                JSONObject json = new JSONObject(new String(j)); //N.B. TRASFORMO IN JSON SOLO IL CAMPO objectValue
	                	
	                System.out.println("FINALE: " + json.toString());
	                
	                String nameSensorNode = json.getString("nameNode");
	                String nameSensor = json.getString("nameSensor");
	                String type = json.getString("type");
	                Double value = json.getDouble("value"); //QUI FUNZIONA
	                System.out.println("Valore: " + value);
	                System.out.println("nameSensor: " + nameSensor);
	                System.out.println("NameSensorNode: " + nameSensorNode);
	                System.out.println("Type: " + type);
	                
	              /*
	               * DA USARE SE SCELGO NELLE CLASSI SENSORNODE DI INVIARE UN SONO MESSAGGIO CON I DATI DI TUTTI I SENSORI DENTRO
	               * 
	               * JSONArray o = jsonmsg.getJSONArray("readings");
	                System.out.println("Array: " + o.toString());
	                
	              //Serve per scorrere un array di oggetti json 
	                for (int i = 0 ; i < o.length(); i++) {
	                	JSONObject obj = o.getJSONObject(i);
	                	value = obj.getString("value");
	                	System.out.println("value: " + value);
	                }
	              */     
	            
	              //formattare il messagio per come serve salvarlo
	                
	              Point point = Point
	              		  .measurement(nameSensorNode)
	              		  .addTag("host", clientId)
	              		  .addTag("Sensor Node", nameSensorNode)
	              		  .addTag("Sensor", nameSensor)
	              		  .addTag("type", type)
	              		  .addField(type, value) //value prelevato dal messaggio pubblicato nel topic
	              		  .time(Instant.now(), WritePrecision.NS);
	              
	              //creare più point, uno per ogni dato del sensore specifico
	    
	              WriteApiBlocking writeApi = client.getWriteApiBlocking();
	              writeApi.writePoint(bucket, org, point); 
	              
	              System.out.println("SCRIVO IN INFLUXDB");
	      
	   /*           
	              //SCRITTURA NEL DATABASE LOCALE 
	              Connection connection = DatabaseUtil.getConnection(); 
	              connection.setAutoCommit(false);	
	              
	              String sql = "INSERT INTO measurement (value, sensor,) VALUES (?, ?)";
	              PreparedStatement pstmt = connection.prepareStatement(sql);
	              pstmt.setDouble(1, Double.valueOf(valore));
	              pstmt.setString(2, clientId);
	              
	              System.out.println(pstmt);
	              
	              int insertRows = pstmt.executeUpdate();
	      		  System.out.println("righe inserite: "+ insertRows);
	      		  connection.commit();
	      */       
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



/*	
			
//SCRITTURA NEL DATABASE INFLUXDB 
	  
	  
 
//  Mem mem = new Mem();
//    mem.host = "host1";
//    mem.used_percent = 23.43234543;
//    mem.time = Instant.now();
//
//    WriteApiBlocking writeApi = client.getWriteApiBlocking();
// 	writeApi.writeRecord(bucket, org, WritePrecision.NS, mem);
  
  
    Point point = Point
    		  .measurement("temp")
    		  .addTag("host", "host1")
    		  .addField("valore", 43.43234543)
    		  .time(Instant.now(), WritePrecision.NS);

    WriteApiBlocking writeApi = client.getWriteApiBlocking();
    writeApi.writePoint(bucket, org, point);    
    

  //  String data = "mem, host=host1 used_percent=50.43234543";

  // WriteApiBlocking writeApi2 = client.getWriteApiBlocking();
  //  writeApi.writeRecord(bucket, org, WritePrecision.NS, data);
 

	       
    // Wait a few seconds in order to let the InfluxDB client
	 // write your points asynchronously (note: you can adjust the
	 // internal time interval if you need via 'enableBatch' call).
    Thread.sleep(5_000L);
       
    
    String query = "from(bucket: \"testBucket\") |> range(start: -1h)";
    List<FluxTable> tables = client.getQueryApi().query(query, org);

    for (FluxTable table : tables) {
      for (FluxRecord record : table.getRecords()) {
        System.out.println(record.toString());
      }
    }

    client.close();
    
    
 */ 
  
 

  
  /*
  @Measurement(name = "mem")
	  public static class Mem {
	    @Column(tag = true)
	    String host;
	    @Column
	    Double used_percent;
	    @Column(timestamp = true)
	    Instant time;
  }
  */
 


