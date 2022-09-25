package it.unisannio.rosariogoglia.model;

public class SensorAmmoniaca extends Sensor{

	@Override
	public double measurement() {
		
		Double value = (Math.random()*(499)+1); //genera valori compresi tra 1 - 500 gradi
    	//System.out.println("value: " + value);
		return value;
	}
	
	

}
