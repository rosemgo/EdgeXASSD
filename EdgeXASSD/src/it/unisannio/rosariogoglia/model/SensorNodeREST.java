package it.unisannio.rosariogoglia.model;

import static java.lang.System.out;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SensorNodeREST extends SensorNode{

	@Override
	public void run() {
		
		var client = HttpClient.newBuilder().build();
		
		try{
				
			//INVIO ASYNC EVENT				
			long time1 = System.currentTimeMillis();		
			long time2 = 0;
			
			while(true) {
				
				time2 = System.currentTimeMillis();
			    
			    if(time2 - time1 > 10000) {
					
			    	//Creo jsonArray, ad ogni iterazione aggiungo il singolo jsonmsg nell'array. 
			    	//Poi fuori dal for invoco una sola volta il publish ed invio un solo messaggio che contiene le misurazioni di tutti i sensori associati al nodo
			    	JSONArray jsonArray = new JSONArray();				    	
			    	
			    	for(int i=0; i<this.sensors.size(); i++) {
	            					    	
			    		JSONObject jsonmsg = new JSONObject();
			    		jsonmsg.put("idSensorNode", this.idSensorNode); 
			    		jsonmsg.put("nameNode", this.device); //nome del sensore appartenente al nodo sensore
				    	jsonmsg.put("idSensor", this.sensors.get(i).getIdSensor()); 
			    		jsonmsg.put("nameSensor", this.sensors.get(i).getName()); 
				    	jsonmsg.put("type", this.sensors.get(i).getType()); 
				    	jsonmsg.put("value", this.sensors.get(i).measurement());
				    	jsonmsg.put("unitOfMeasurement", sensors.get(i).getUnitOfMeasurement());
				    //	jsonmsg.put("data", new Date());
				    	
				    	System.out.println("MESS: " + jsonmsg.toString());
				    						    						    	
				    	jsonArray.put(jsonmsg);			    	
	
			    	}
			    	//separo la data in modo da non ripeterla per ogni sensore ma la inserisco nel messaggio una volta sola
			    	JSONObject date = new JSONObject();
			    	date.put("date", new Date());
			    	jsonArray.put(date); //aggiungo la data solo una volta				    	
			    	
			    	for(int i=0; i<jsonArray.length(); i++) {
			    		JSONObject j = jsonArray.getJSONObject(i);
			    		System.out.println("ELEMENTO " + i + " : "  + j.toString());
			    	}
			
		    	
		    	
				HttpRequest request = HttpRequest.newBuilder()  //sample-json è il nome del device SOSTITUIRE CON this.device
				      //.uri(URI.create("http://192.168.204.133:59986/api/v2/resource/"+ this.device +"/json")) // USARE QUESTO INDIRIZZO PER EDGEX SU MACCHINA VIRTUALE LOCALE
						.uri(URI.create("http://15.160.35.22:59986/api/v2/resource/"+ this.device +"/json")) // USARE QUESTO INDIRIZZO PER EDGEX SU CLOUD AWS
						.timeout(Duration.ofMinutes(2))
				        .header("Content-Type", "application/json")
				        .POST(BodyPublishers.ofByteArray( jsonArray.toString().getBytes() )) //BodyPublisher converte un oggetto java di alto livello in un flusso di dati
				        .build();
				  
					  /* Richiesta asincrona
					  client.sendAsync(request, BodyHandlers.ofString())
					        .thenApply(HttpResponse::body)
					        .thenAccept(System.out::println);
					   
					  Thread.sleep(10 * 1000);
					  System.out.println("FINE SLEEP");
					  */
				  				   
				 var response = client.send(request, BodyHandlers.ofString()); //E' il duale di BodyPublisher, converte un fusso di dati in un oggetto java di alto livello
				 out.printf("Response code is: %d %n", response.statusCode());
				 out.printf("The response body is:%n %s %n", response.body());
					 
				 time1 = System.currentTimeMillis();
			    
			    }
				 
			}				 
			 
    	} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
	

