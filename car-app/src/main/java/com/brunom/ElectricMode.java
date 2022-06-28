package com.brunom;

/**
 * Represents the electric drive mode, possible only when the battery is not under 10% and the car is moving under the speed of 90 Km/h.
 * @see DriveMode
 */
public class ElectricMode extends DriveMode{
    public ElectricMode(Car car){
        super(car);
        force = 11000d;
        maxSpeed = 90d;
    }

    @Override
    public void accelerate(double acceleration, double amount){
        acceleration = acceleration * amount;
        car.speed += acceleration;
        car.fuel = Math.max(car.fuel - 0.05*amount, 0d);
        car.battery = Math.max(car.battery - 0.4*amount, 0d);
        if(car.speed > maxSpeed || car.battery < 10d){
            car.mode = new NormalMode(car);
        }
        if(car.fuel == 0d)
            car.engine = false;
    }

    @Override
    public boolean isLegal(){
        return car.speed <= maxSpeed && car.battery >= 10d;
    }
}

