package it.unisannio.rosariogoglia.model;


import java.util.List;

public abstract class SensorNode extends Thread{
		
	protected String device;
	protected int idSensorNode; //id incrementale ed univoco, ogni sensore di uno specifico protocollo deve avere un id crescente
	protected transient List<Sensor> sensors; ////SOSTITUIRE CON UN ARRAY DI SENSORI sensore: temperatura, umidità, velocità
	protected String protocollo;
	protected boolean exitThread;	
	
	
	public SensorNode() {}
	
	
	
	public SensorNode(String device, int id, String protocollo/*, List<Sensor> sensors*/) {
		this.device = device;
		this.idSensorNode = id;
		this.protocollo = protocollo;
	//	this.sensors = sensors;		
	}

	public String getDevice() {
		return device;
	}
	
	public void setDevice(String device) {
		this.device = device;
	}

	public int getIdSensorNode() {
		return this.idSensorNode;
	}

	public void setIdSensorNode(int id) {
		this.idSensorNode = id;
	}
	
	public List<Sensor> getSensors() {
		return sensors;
	}
	public void setSensors(List<Sensor> sensor) {
		this.sensors = sensor;
	}
	
	public String getProtocollo() {
		return protocollo;
	}

	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}

	public String toString() {
		return (this.idSensorNode + " " + this.device + " " + this.protocollo);
	}
	
	public abstract void run();	
	
	public void stopThreadh() {
		exitThread = true;
	}

}
