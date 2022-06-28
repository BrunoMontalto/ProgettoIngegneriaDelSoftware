package com.brunom;

/**
 * Represents the normal drive mode, possible only when moving under the speed of 120 Km/h.
 * @see DriveMode
 */
public class NormalMode extends DriveMode{
    public NormalMode(Car car){
        super(car);
        force = 12000d;
        maxSpeed = 120d;
    }

    @Override
    public void accelerate(double acceleration, double amount){
        acceleration = acceleration*amount;
        car.speed += acceleration;
        car.fuel = Math.max(car.fuel - 0.6*amount, 0d);
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
