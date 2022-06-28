package com.brunom;
/**
 * Represents the comfortable drive mode, possible only when moving under the speed of 50 Km/h.
 * @see DriveMode
 */
public class ComfortableMode extends DriveMode{
    public ComfortableMode(Car car){
        super(car);
        force = 10000d;
        maxSpeed = 50d;
    }

    @Override
    public void accelerate(double acceleration, double amount){
        acceleration = acceleration*amount;
        car.speed += acceleration;
        car.fuel = Math.max(car.fuel - 0.5*amount, 0d);
        if(car.speed > maxSpeed)
            car.speed = maxSpeed;
        if(car.fuel == 0d)
            car.engine = false;
    }

    @Override
    public boolean isLegal(){
        return car.speed <= maxSpeed;
    }
}
