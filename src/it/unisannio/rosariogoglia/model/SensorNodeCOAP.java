package it.unisannio.rosariogoglia.model;

import java.net.SocketException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.unipr.netsec.mjcoap.coap.client.CoapClient;
import it.unipr.netsec.mjcoap.coap.message.CoapMessageFactory;
import it.unipr.netsec.mjcoap.coap.message.CoapRequest;
import it.unipr.netsec.mjcoap.coap.message.CoapRequestMethod;
import it.unipr.netsec.mjcoap.coap.message.CoapResponse;
import it.unipr.netsec.mjcoap.coap.message.CoapResponseCode;
import it.unipr.netsec.mjcoap.coap.provider.CoapURI;
import it.unipr.netsec.mjcoap.coap.server.AbstractCoapServer;
import it.unipr.netsec.mjcoap.coap.server.CoapResource;
import it.unisannio.rosariogoglia.model.SensorNodeCOAP;
import it.unisannio.rosariogoglia.model.SensorNodeCOAP.ClientCOAP;
import it.unisannio.rosariogoglia.model.SensorNodeCOAP.ServerCOAP;


public class SensorNodeCOAP extends SensorNode{

//	private static  Object lock = new Object();
	
	private int port; //porta di ascolto della componente server del nodo sensore COAP, utilizzata dal device COAP per comunicare col server.
	
	public SensorNodeCOAP(int port) {
		super();
		this.port = port;
	}
	
	
	public SensorNodeCOAP() {}

	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}



	public class ClientCOAP extends Thread{
				
		public void run() {
		
			while(true) {
				
//		synchronized (SensorNodeCOAP.this.lock) {
			
		//	System.out.println("ENTRA UN THREAD ALLA VOLTA");
			
			System.out.println("INIZIO RUN IN CLIENT COAP: " + SensorNodeCOAP.this.device );
			
			//args.length > 0 ? args[0] : 
			CoapURI resource_uri;
			
			try {
				//	resource_uri = new CoapURI("coap://192.168.204.133/a1r/d2/json"); //IN EDGEX DEVO AVERE UN DEVICE (nell'esempio sarebbe d2) PER OGNI NODO SENSORE
				
				//USARE L'INDIRIZZIO coap://192.168.204.133/a1r/ PER EDGEX SULLA MACCHINA VIRTUALE LOCALE
			 //	resource_uri = new CoapURI("coap://192.168.204.133/a1r/"+ SensorNodeCOAP.this.device +"/json");  //this.device DEVE CORRISPONDERE AL NOME DEL DISPOSITIVO CONTENUTO IN EDGEX	
			 
				//USARE L'INDIRIZZIO coap://15.160.35.22/a1r/ PER EDGEX SUL CLOUD AWS
				resource_uri = new CoapURI("coap://15.160.35.22/a1r/"+ SensorNodeCOAP.this.device +"/json");  //this.device DEVE CORRISPONDERE AL NOME DEL DISPOSITIVO CONTENUTO IN EDGEX	
				 
				
				//SOLO UN THREAD COAP ALLA VOLTA PUO' FARE DA CLIENT SULLA STESSA PORTA, ALTRIMENTI SI OTTIENE L'ERRORE Address already in use: Cannot bind
		//	    int udp_port=5683;
		//	    CoapClient coap_client = new CoapClient(udp_port);
				CoapClient coap_client = new CoapClient(); //SE NON INSERISCO LA PORTA, POSSO AVERE N CLIENT CONTEMPORANEAMENTE
				
				System.out.println("CONNESSO AL SERVER COAP: " + resource_uri + " " + coap_client.toString());
				
				//INVIO ASYNC EVENT				
				
				long time1 = System.currentTimeMillis();		
				long time2 = 0;
				//String resource = "json";
				
				while(true) {
				
					time2 = System.currentTimeMillis();
				    
				    if(time2 - time1 > 30000) {
						
				    	//Creo jsonArray, ad ogni iterazione aggiungo il singolo jsonmsg nell'array. 
				    	//Poi fuori dal for invoco una sola volta il publish ed invio un solo messaggio che contiene le misurazioni di tutti i sensori associati al nodo
				    	JSONArray jsonArray = new JSONArray();				    	
				    	
				    	for(int i=0; i<SensorNodeCOAP.this.sensors.size(); i++) {
		            					    	
					    	JSONObject jsonmsg = new JSONObject();
					    	jsonmsg.put("idSensorNode", SensorNodeCOAP.this.idSensorNode); 
					    	jsonmsg.put("nameNode", SensorNodeCOAP.this.device); //nome del sensore appartenente al nodo sensore
					    	jsonmsg.put("idSensor", SensorNodeCOAP.this.sensors.get(i).getIdSensor()); 
					    	jsonmsg.put("nameSensor", SensorNodeCOAP.this.sensors.get(i).getName()); 
					    	jsonmsg.put("type", SensorNodeCOAP.this.sensors.get(i).getType()); 
					    	jsonmsg.put("value", SensorNodeCOAP.this.sensors.get(i).measurement());
					    	jsonmsg.put("unitOfMeasurement", sensors.get(i).getUnitOfMeasurement());
					    //  jsonmsg.put("data", new Date());
					    	
					    	System.out.println("MESS: " + jsonmsg.toString());
					    						    						    	
					    	jsonArray.put(jsonmsg);			    	

				    	}
				    	//separo la data in modo da non ripeterla per ogni sensore ma la inserisco nel messaggio una volta sola
				    	JSONObject date = new JSONObject();
				    //	date.put("date", new Date());
				    					    	
				    	String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
				        System.out.println("DATA DELLA MISURAZIONE: " + timeStamp);
				        
				        date.put("date", timeStamp);
				        jsonArray.put(date); //aggiungo la data solo una volta				    	
				        
				    	for(int i=0; i<jsonArray.length(); i++) {
				    		JSONObject j = jsonArray.getJSONObject(i);
				    		System.out.println("ELEMENTO " + i + " : "  + j.toString());
				    	}
				    	
				    	CoapResponse resp = coap_client.request(CoapRequestMethod.POST, resource_uri, CoapResource.FORMAT_TEXT_PLAIN_UTF8, jsonArray.toString().getBytes());
						//resp = coap_client.request(CoapRequestMethod.POST, resource_uri, 0, "751".getBytes());
				    	
								
				    	if (resp!=null) 
				    		System.out.println("Response: " + resp);
						else  
							System.out.println("Request failure");
				    	
									
						time1 = System.currentTimeMillis();
				    }
				
				}
				
		//		coap_client.halt();

		//		System.out.println("FINE DEL RUN");
		
		
			
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			
		}
			
			
			
			
		}
	//	}
	}
	
	/**
	 * Componente server del nodo sensore COAP, utilizzata per ricevere i comandi dal device in EdgeX
	 * @author rosar
	 *
	 */
	public class ServerCOAP extends Thread{
			
		public void run(){
			
			System.out.println("SERVER STARTED");
			
			try {
				//il server è in ascolto sulla porta specifica passata al nodo sensore
				new AbstractCoapServer(SensorNodeCOAP.this.port) {
								
					@Override
					protected void handleGetRequest(CoapRequest req) {
										
						//System.out.println(" ");
						//System.out.println("SERVER STARTED");
						
						System.out.println("messageID"+req.getMessageId());
						System.out.println("tipologia di mess: " + req.getType()); 
							
						try {
								System.out.println("uri"+req.getRequestUri());
								System.out.println("uri PATH:" + req.getRequestUriPath());
						} catch (URISyntaxException e) {
								e.printStackTrace();
						}
						System.out.println("method "+req.getMethod());
						System.out.println("Payload "+req.getPayload());
							
							
						if(req.getRequestUriPath().contains("json")) {
							
							System.out.println("RICHIESTA INVIO COMANDO SERVER COAP");
							
							
							try {	
								//Creo jsonArray, ad ogni aggiungo il singolo jsonmsg nell'array. 
						    	//Poi fuori dal for invoco una sola volta il publish ed invio un solo messaggio che contiene le misurazioni di tutti i sensori associati al nodo
						    	JSONArray jsonArray = new JSONArray();				    	
						    	
						    	for(int i=0; i<SensorNodeCOAP.this.sensors.size(); i++) {
				            					    	
							    	JSONObject jsonmsg = new JSONObject();
							    	
							    	jsonmsg.put("idSensorNode", SensorNodeCOAP.this.idSensorNode);
									jsonmsg.put("nameNode", SensorNodeCOAP.this.device);
									jsonmsg.put("idSensor", SensorNodeCOAP.this.sensors.get(i).getIdSensor()); 
									//nome del sensore appartenente al nodo sensore
							    	jsonmsg.put("nameSensor", SensorNodeCOAP.this.sensors.get(i).getName()); 
							    	jsonmsg.put("type", SensorNodeCOAP.this.sensors.get(i).getType()); 
							    	jsonmsg.put("value", SensorNodeCOAP.this.sensors.get(i).measurement());
							    	jsonmsg.put("unitOfMeasurement", sensors.get(i).getUnitOfMeasurement());
							   // 	jsonmsg.put("data", new Date());
						    	
							    	System.out.println("MESS: " + jsonmsg.toString());
							    						    						    	
							    	jsonArray.put(jsonmsg);			    	

						    	}
						    	
						    	//separo la data in modo da non ripeterla per ogni sensore ma la inserisco nel messaggio una volta sola
						    	JSONObject date = new JSONObject();
						    //	date.put("date", new Date());
						    					    	
						    	String timeStamp = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
						        System.out.println("DATA DELLA MISURAZIONE: " + timeStamp);
						        
						        date.put("date", timeStamp);
						        jsonArray.put(date); //aggiungo la data solo una volta				    	
												    	
					    		//STAMPO IL CONTENUTO DEL MESSAGGIO			    	
						    	for(int i=0; i<jsonArray.length(); i++) {
						    		JSONObject j = jsonArray.getJSONObject(i);
						    		System.out.println("ELEMENTO " + i + " : "  + j.toString());
						    	}					    	
								
							    //CoapResponse resp = coap_client.request(CoapRequestMethod.POST, resource_uri, 0, "739".getBytes());
													    	
							    CoapResponse resp = CoapMessageFactory.createResponse(req, CoapResponseCode._2_05_Content);
								resp.setPayload(CoapResource.FORMAT_TEXT_PLAIN_UTF8, jsonArray.toString().getBytes());
								respond(req,resp);
								System.out.println("RISPOSTA SERVER COAP: " + resp.getPayload().toString());
								System.out.println(resp.getRemoteSoAddress());
							
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else{
							CoapResponse resp = CoapMessageFactory.createResponse(req,CoapResponseCode._2_05_Content);
							resp.setPayload(CoapResource.FORMAT_TEXT_PLAIN_UTF8,"1".getBytes());
							respond(req,resp);	
						}
						
					}
				};
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		
		
	}
	
	
	
	
	@Override
	public void run() {
		
		//far partire un thread client coap ed uno server coap
		
		//AVVIO LA COMPONENTE CLIENT COAP DEL NODO SENSORE 
		ClientCOAP client = new ClientCOAP(); //provare direttamente ClientCOAP client = new ClientCOAP();  
		client.start();
				
		//POSSO METTERE UN CONTROLLO PER SCEGLERE SE ESEGUIRE O MENO IL THREAD DEL SERVER
		//AVVIO LA COMPONENTE SERVER COAP DEL NODO SENSORE 
		ServerCOAP server = new ServerCOAP();
		server.start();
			
	}
	
	
	
	
	
	
	
	

}
