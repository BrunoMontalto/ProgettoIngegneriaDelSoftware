package com.brunom;

/**
 * Represents the sport drive mode, possible only when moving under the speed of 200 Km/h and there is exactly one passenger.
 * @see DriveMode
 */
public class SportMode extends DriveMode{
    public SportMode(Car car){
        super(car);
        force = 15000d;
        maxSpeed = 200d;
    }

    @Override
    public void accelerate(double acceleration, double amount){
        acceleration = acceleration*amount;
        car.speed += acceleration;
        car.fuel = Math.max(car.fuel - 0.8*amount, 0d);
        if(car.speed > maxSpeed)
            car.speed = maxSpeed;
        if(car.fuel == 0d)
            car.engine = false;
    }

    @Override
    public boolean isLegal(){
        return car.passengers.size() == 1 && car.speed <= maxSpeed;
    }
    
}
