package com.brunom;
/**
 * An abstract class that represents a generic drive mode.
 */
public abstract class DriveMode {
    protected Car car;
    public double force;
    public double maxSpeed;

    public DriveMode(Car car){
        this.car = car;
    }

    /**
     * Accelerates the car consuming fuel and/or battery depending on the drive mode. If the car runs out of fuel, it turns the engine off.
     * @param acceleration the acceleration.
     * @param amount a factor between 0.0 and 1.0
     */
    public abstract void accelerate(double acceleration, double amount);
    /**
     * Defines if the current drive mode is legal or not for the current car state (for example SportMode is illegal when there are more than one passenger).
     * @return if the current drive mode is legal or not for the current car state (for example SportMode is illegal when there are more than one passenger).
     */
    public abstract boolean isLegal();
}