package it.unisannio.rosariogoglia.model;

import java.net.SocketException;
import java.net.URISyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import it.unipr.netsec.mjcoap.coap.client.CoapClient;
import it.unipr.netsec.mjcoap.coap.message.CoapRequestMethod;
import it.unipr.netsec.mjcoap.coap.message.CoapResponse;
import it.unipr.netsec.mjcoap.coap.provider.CoapURI;
import it.unipr.netsec.mjcoap.coap.server.CoapResource;

public class SensorNodeCOAP extends SensorNode{

/*	public SensorNodeCOAP(String device, int id) {
		this.device = device;
		this.idSensorNode = id;
	}	
*/	
	//COPIARE CLIENT COAP
	
	@Override
	public void run() {
		
		
		
		//args.length > 0 ? args[0] : 
		CoapURI resource_uri;
		
		try {
		//	resource_uri = new CoapURI("coap://192.168.204.133/a1r/d2/json"); //IN EDGEX DEVO AVERE UN DEVICE PER OGNI NODO SENSORE
			resource_uri = new CoapURI("coap://192.168.204.133/a1r/"+ this.device +"/json");  //this.device DEVE CORRISPONDERE AL NOME DEL DISPOSITIVO CONTENUTO IN EDGEX	
			int udp_port=5683;
			CoapClient coap_client = new CoapClient(udp_port);
			
			//POTREI USARE UNA VARIABILE BOOLEANA DEL NODO SENSORE ED ESEGUIRE IL WHILE FINO A QUANDO QUESTA VARIABILE E' VERA. QUINDI INVECE DI USARE UN INTERRUPT, PER INTERROMPERE IL THREAD, POTREI SETTARE QUESTA VARIABILE A FALSE 
			while(true) {
			
				//ogni sensore associato al nodo sensore invia una misurazione
				for(int i=0; i<this.sensors.size(); i++) {
					
					//CREO UN MESSAGGIO JSON PER OGNI SENSORE ED EFFETTUARE L'INVIO
				    JSONObject jsonmsg = new JSONObject();
			    	jsonmsg.put("nameNode", this.device); //nome del sensore appartenente al nodo sensore
			    	jsonmsg.put("nameSensor", this.sensors.get(i).getName()); 
			    	jsonmsg.put("type", this.sensors.get(i).getType()); 
			    	jsonmsg.put("value", this.sensors.get(i).measurement());
			  
			    
				    System.out.println("MESSAGGIO INVIATO: " + jsonmsg);
					CoapResponse resp=coap_client.request(CoapRequestMethod.POST, resource_uri, CoapResource.FORMAT_TEXT_PLAIN_UTF8, jsonmsg.toString().getBytes());
							
					
					if (resp!=null) 
						System.out.println("Response: "+resp);
					else  
						System.out.println("Request failure");
								
				}
			
			}
		//	coap_client.halt();;
			
		
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
