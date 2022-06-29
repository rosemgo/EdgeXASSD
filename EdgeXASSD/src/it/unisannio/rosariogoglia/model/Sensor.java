package it.unisannio.rosariogoglia.model;

public abstract class Sensor {
	
	private int idSensor;
	private String name;
	private String type; //indica la tipologia del sensore: temperatura, umidità, velocità ecc.
	
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
	
	public abstract double measurement();	

}
