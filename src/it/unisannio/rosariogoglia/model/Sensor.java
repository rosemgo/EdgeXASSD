package it.unisannio.rosariogoglia.model;

import it.unisannio.rosariogoglia.model.SensorNode;

public abstract class Sensor {
	
	private int idSensor;
	private String name;
	private String type; //indica la tipologia del sensore: temperatura, umidità, velocità ecc.
	private String unitOfMeasurement;
	private SensorNode sensorNode; //Nodo sensore a cui è associato lo specifico sensore
	
	public Sensor() {}

	public Sensor(int idSensor, String name, String type, String unitOfMeasurement) {
		super();
		this.idSensor = idSensor;
		this.name = name;
		this.type = type;
		this.unitOfMeasurement = unitOfMeasurement;
	}
	
	public int getIdSensor() {
		return idSensor;
	}

	public void setIdSensor(int idSensor) {
		this.idSensor = idSensor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	public String getUnitOfMeasurement() {
		return unitOfMeasurement;
	}

	public void setUnitOfMeasurement(String unitOfMeasurement) {
		this.unitOfMeasurement = unitOfMeasurement;
	}
	
	
	public SensorNode getSensorNode() {
		return sensorNode;
	}

	public void setSensorNode(SensorNode sensorNode) {
		this.sensorNode = sensorNode;
	}

	public abstract double measurement();		

}
