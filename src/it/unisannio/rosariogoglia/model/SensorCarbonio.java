package it.unisannio.rosariogoglia.model;

public class SensorCarbonio extends Sensor{

	@Override
	public double measurement() {
		
		Double value = (Math.random()*(999)+1); //genera valori compresi tra 1 - 1000 ppm
    	//System.out.println("value: " + value);
		return value;
	}

	
	
}
