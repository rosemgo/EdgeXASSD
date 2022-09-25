package it.unisannio.rosariogoglia.model;

public class SensorAzoto extends Sensor{

	@Override
	public double measurement() {
		
		Double value = (Math.random()*(9.95)+0.05); //genera valori compresi tra 0.05 - 10 ppm
    	//System.out.println("value: " + value);
		return value;
	
	}
	
	

}
