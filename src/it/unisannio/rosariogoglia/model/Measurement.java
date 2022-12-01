package it.unisannio.rosariogoglia.model;

import java.util.Date;

public class Measurement {
	
	private int idMeasurement;
	private float value;
	private String unitOfMeasurement;
	private String type;
	private Date dateMeasurement;
	private int idSensorNode;
	private int idSensor;
		
	public Measurement() {
		super();
	}

	public Measurement(int idMeasurement, float value, Date dateMeasurement, int sensorNode, int sensor) {
		super();
		this.idMeasurement = idMeasurement;
		this.value = value;
		this.dateMeasurement = dateMeasurement;
		this.idSensorNode = sensorNode;
		this.idSensor = sensor;
	}

	public int getIdMeasurement() {
		return idMeasurement;
	}

	public void setIdMeasurement(int idMeasurement) {
		this.idMeasurement = idMeasurement;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}
	
	public String getUnitOfMeasurement() {
		return unitOfMeasurement;
	}

	public void setUnitOfMeasurement(String unitOfMeasurement) {
		this.unitOfMeasurement = unitOfMeasurement;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getDateMeasurement() {
		return dateMeasurement;
	}

	public void setDateMeasurement(Date dateMeasurement) {
		this.dateMeasurement = dateMeasurement;
	}

	public int getSensorNode() {
		return idSensorNode;
	}

	public void setSensorNode(int sensorNode) {
		this.idSensorNode = sensorNode;
	}

	public int getSensor() {
		return idSensor;
	}

	public void setSensor(int sensor) {
		this.idSensor = sensor;
	}
		
	public String toString() {
		return ("idMeasurement: " + this.idMeasurement + ", value: " + this.value + ", idSensorNode: " + this.idSensorNode + ", idSensor: " + this.idSensor + ", data: " + this.dateMeasurement);
	}
	

}
