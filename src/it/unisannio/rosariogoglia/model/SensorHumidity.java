package it.unisannio.rosariogoglia.model;

public class SensorHumidity extends Sensor{

	@Override
	public double measurement() {
		
		Double value = (Math.random()*(50)-10); //genera valori compresi tra 40 - 10 gradi
    	//System.out.println("value: " + value);
		return value;
		
	}

}
