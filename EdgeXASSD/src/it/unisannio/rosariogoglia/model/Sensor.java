package it.unisannio.rosariogoglia.model;


public abstract class Sensor {
	
	private int idSensor;
	private String name;
	private String type; //indica la tipologia del sensore: temperatura, umidità, velocità ecc.
	private SensorNode sensorNode; //Nodo sensore a cui è associato lo specifico sensore
	
	
	public Sensor() {}

	public Sensor(int idSensor, String name, String type) {
		super();
		this.idSensor = idSensor;
		this.name = name;
		this.type = type;
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
	
	public SensorNode getSensorNode() {
		return sensorNode;
	}

	public void setSensorNode(SensorNode sensorNode) {
		this.sensorNode = sensorNode;
	}

	public abstract double measurement();	

}
