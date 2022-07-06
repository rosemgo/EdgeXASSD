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

import org.json.JSONException;
import org.json.JSONObject;

public class SensorNodeREST extends SensorNode{

	@Override
	public void run() {
		try {
			var client = HttpClient.newBuilder().build();
			
			JSONObject jsonmsg = new JSONObject();
	    	
			jsonmsg.put("nameNode", "PROVA");
	    	jsonmsg.put("nameSensor", "PROVA SENSORE");  //nome del sensore appartenente al nodo sensore
	    	jsonmsg.put("type", "PROVA TYPE"); 
	    	jsonmsg.put("value", "PROVA VALORE");
	    	jsonmsg.put("data", new Date());
			
			HttpRequest request = HttpRequest.newBuilder()
			        .uri(URI.create("http://192.168.204.133:59986/api/v2/resource/sample-json/json"))//https://reqres.in/api/users
			        .timeout(Duration.ofMinutes(2))
			        .header("Content-Type", "application/json")
			        .POST(BodyPublishers.ofString( jsonmsg.toString() )) //BodyPublisher converte un oggetto java di alto livello in un flusso di dati
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
	

