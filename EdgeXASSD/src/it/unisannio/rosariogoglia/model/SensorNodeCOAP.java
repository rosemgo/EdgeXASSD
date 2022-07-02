package it.unisannio.rosariogoglia.model;

import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import it.unipr.netsec.mjcoap.coap.client.CoapClient;
import it.unipr.netsec.mjcoap.coap.message.CoapRequestMethod;
import it.unipr.netsec.mjcoap.coap.message.CoapResponse;
import it.unipr.netsec.mjcoap.coap.provider.CoapURI;
import it.unipr.netsec.mjcoap.coap.server.CoapResource;

public class SensorNodeCOAP extends SensorNode{


	
	@Override
	public void run() {
		
		System.out.println("INIZIO RUN");
		
		//args.length > 0 ? args[0] : 
		CoapURI resource_uri;
		
		try {
		//	resource_uri = new CoapURI("coap://192.168.204.133/a1r/d2/json"); //IN EDGEX DEVO AVERE UN DEVICE PER OGNI NODO SENSORE
			resource_uri = new CoapURI("coap://192.168.204.133/a1r/"+ this.device +"/json");  //this.device DEVE CORRISPONDERE AL NOME DEL DISPOSITIVO CONTENUTO IN EDGEX	
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
					for(int i=0; i<this.sensors.size(); i++) {
						
						//CREARE UN MESSAGGIO JSON PER OGNI SENSORE ED EFFETTUARE L'INVIO
						      		
		            
		            	JSONObject jsonmsg = new JSONObject();
				    	jsonmsg.put("nameNode", this.device); //nome del sensore appartenente al nodo sensore
				    	jsonmsg.put("nameSensor", this.sensors.get(i).getName()); 
				    	jsonmsg.put("type", this.sensors.get(i).getType()); 
				    	jsonmsg.put("value", this.sensors.get(i).measurement());
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
