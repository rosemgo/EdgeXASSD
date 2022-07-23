package it.unisannio.rosariogoglia.model;

import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.Date;

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

public class SensorNodeCOAP extends SensorNode{

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
			
			System.out.println("INIZIO RUN IN CLIENT COAP");
			
			//args.length > 0 ? args[0] : 
			CoapURI resource_uri;
			
			try {
			//	resource_uri = new CoapURI("coap://192.168.204.133/a1r/d2/json"); //IN EDGEX DEVO AVERE UN DEVICE PER OGNI NODO SENSORE
				resource_uri = new CoapURI("coap://192.168.204.133/a1r/"+ SensorNodeCOAP.this.device +"/json");  //this.device DEVE CORRISPONDERE AL NOME DEL DISPOSITIVO CONTENUTO IN EDGEX	
				int udp_port=5683;
				CoapClient coap_client = new CoapClient(udp_port);
				
				System.out.println("CONNESSO AL SERVER COAP: " + resource_uri + " " + coap_client.toString());
				
				//INVIO ASYNC EVENT				
				
				long time1 = System.currentTimeMillis();		
				long time2 = 0;
				//String resource = "json";
				
				while(true) {
				
					time2 = System.currentTimeMillis();
				    
				    if(time2 - time1 > 10000) {
						//ogni sensore associato al nodo sensore invia una misurazione
						for(int i=0; i<SensorNodeCOAP.this.sensors.size(); i++) {
							
							//CREARE UN MESSAGGIO JSON PER OGNI SENSORE ED EFFETTUARE L'INVIO
							JSONObject jsonmsg = new JSONObject();
					    	jsonmsg.put("nameNode", SensorNodeCOAP.this.device); //nome del sensore appartenente al nodo sensore
					    	jsonmsg.put("nameSensor", SensorNodeCOAP.this.sensors.get(i).getName()); 
					    	jsonmsg.put("type", SensorNodeCOAP.this.sensors.get(i).getType()); 
					    	jsonmsg.put("value", SensorNodeCOAP.this.sensors.get(i).measurement());
					    	jsonmsg.put("data", new Date());
					    				    	
						    System.out.println("MESSAGGIO INVIATO: " + jsonmsg);
							CoapResponse resp=coap_client.request(CoapRequestMethod.POST, resource_uri, CoapResource.FORMAT_TEXT_PLAIN_UTF8, jsonmsg.toString().getBytes());
								
						//	resp = coap_client.request(CoapRequestMethod.POST, resource_uri, 0, "751".getBytes());
							
							
							if (resp!=null) 
								System.out.println("Response: "+resp);
							else  
								System.out.println("Request failure");
							
							
						}
					
						time1 = System.currentTimeMillis();
				    }
				
				}
				
			//	coap_client.halt();

			//	System.out.println("FINE DEL RUN");
		
		
			
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
								
								JSONObject jsonmsg = new JSONObject();
						    	try {
									jsonmsg.put("nameNode", "PIPPO");
									jsonmsg.put("nameSensor", "PLUTO");
									jsonmsg.put("data", new Date());
								} catch (JSONException e) {
									e.printStackTrace();
								}
						    	
						    	//CoapResponse resp = coap_client.request(CoapRequestMethod.POST, resource_uri, 0, "739".getBytes());
												    	
						    	CoapResponse resp = CoapMessageFactory.createResponse(req, CoapResponseCode._2_05_Content);
								resp.setPayload(CoapResource.FORMAT_TEXT_PLAIN_UTF8, jsonmsg.toString().getBytes());
								respond(req,resp);
								System.out.println("RISPOSTA: " + resp.getPayload().toString());
								System.out.println(resp.getRemoteSoAddress());
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
		ClientCOAP client = new SensorNodeCOAP().new ClientCOAP(); //provare direttamente ClientCOAP client = new ClientCOAP();  
		client.start();
				
		//POSSO METTERE UN CONTROLLO PER SCEGLERE SE ESEGUIRE O MENO IL THREAD DEL SERVER
		//AVVIO LA COMPONENTE SERVER COAP DEL NODO SENSORE 
		ServerCOAP server = new SensorNodeCOAP().new ServerCOAP();
		server.start();
	
	
		
	}
	
	
	
	
	
	
	
	

}
