package com.brunom;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Represents a car with different driving modes, 4 seats, a gas tank and a battery and an engine that can be turned on or off.
 * @see DriveMode
 * @see Passenger
 * @author brunom
 */
public class Car {
    private static Car car = null;
    /**
     * Represents if the engine is on or off.
     */
    public boolean engine = false;

    public double speed = 0;
    public DriveMode mode;

    public double fuel = 100d;
    public double battery = 100d;
    
    public ArrayList<Passenger> passengers = new ArrayList<Passenger>();
    /**
     * The maximum number of passengers.
     */
    public final int seats = 4;
    public final int weight = 900;
    
    private Car(){
        mode = new ComfortableMode(this);
    }

    /**
     * Creates the Car instance if it does not already exist and returns it.
     * @return the Car instance
     */
    public static Car getInstance(){
        if(car == null){
            car = new Car();
        }
        return car;
    }

    /**
     * Sets the car Instace to null.
     */
    public static void deleteInstance(){
        car = null;
    }

    /**
     * Resets speed, passengers, engine, fuel, battery and mode of the car.
     */
    public void resetState(){
        speed = 0;
        passengers.clear();
        fuel = 100d;
        battery = 100d;
        engine = false;
        mode = new ComfortableMode(this);
    }

    /**
     * If the car is not moving, toggles the state of the engine(on/off).
     * @return 0 if the engine has been toogled successfully, -1 if there are no passengers, -2 if there is no fuel.
     */
    public int toggleEngine(){
        if(passengers.size() == 0)
            return -1;
        if(fuel == 0d)
            return -2;
        engine = !engine;
        return 0;
    }

    /**
     * Returns the weight of the car considering the passengers inside.
     * @return the weight of the car considering the passengers inside.
     */
    public int getTotalWeight(){
        int tWeight = 0;
        for(Passenger p: passengers){
            tWeight += p.weight;
        }
        return tWeight + weight;
    }

    /**
     * Setter for mode.
     * @param mode the desired drive mode.
     * @return 0 if the mode has been changed successfully, -1 if the selected mode is illegal for the current car state.
     */
    public int setMode(DriveModeEnum mode){
        DriveMode oldMode = this.mode;
        if(mode == DriveModeEnum.Sport){
            car.mode = new SportMode(car);
            if(!car.mode.isLegal()){
                this.mode = oldMode;
                return -1;
            }
            return 0;
        }
        if(mode == DriveModeEnum.Comfortable){
            car.mode = new ComfortableMode(car);
            if(!car.mode.isLegal()){
                this.mode = oldMode;
                return -1;
            }
            return 0;
        }
        if(mode == DriveModeEnum.Electric){
            car.mode = new ElectricMode(car);
            if(!car.mode.isLegal()){
                this.mode = oldMode;
                return -1;
            }
            return 0;
        }

        car.mode = new NormalMode(car);
        if(!car.mode.isLegal()){
            this.mode = oldMode;
            return -1;
        }
        return 0;
    }
    
    /**
     * Determines if the car can accelerate.
     * @return 0 if the car can accelerate, -1 if the engine is off, -2 if there are no passenger.
     */
    public int canAccelerate(){
        if(!engine)
            return -1;
        if(passengers.size() == 0)
            return -2;
        return 0;
    }
    
    /**
     * Increases the car speed by a certain amount if the engine is on(depending on the number of passengers and their weight).
     * 
     * @param amount the amount of acceleration(from 0.0 to 1.0).
     * @return the acceleration after considering the weight of the passengers if the acceleration is successful, -1 if the engine is off, -2 if there are no passengers.
     */
    public double accelerate(double amount){
        int ca = canAccelerate();
        if(ca != 0)
            return ca;
        if(amount < 0) amount = 0;
        if(amount > 1) amount = 1;
        double oldSpeed = car.speed;
        int w = getTotalWeight();
        double acceleration = (car.mode.force/w);
        mode.accelerate(acceleration, amount);
        return car.speed - oldSpeed;
    }
    
    /**
     * Reduces the speed of the car. The brake is regenerative.
     */
    public void brake(){
        double oldSpeed = car.speed;
        car.speed /= 1.3;
        car.speed -= 1d;
        car.speed = Math.max(0d, car.speed);
        car.battery = Math.min(100d, car.battery + (oldSpeed - car.speed)/100d);
    }

    /**
     * Determines whether the car is full or not.
     * @return a boolean representing whether the car is full or not.
     */
    public boolean isFull(){
        return passengers.size() >= seats;
    }

    /**
      * Adds a passenger to the passengers list.
      *  
      * @param passenger the passenger to add.
      * @return 0 if the passenger has been added successfully, -1 if all the car seats are occupied, -2 if the car is moving, -3 if the passenger is already in the car.
      * @throws IllegalStateException if the current mode does not allow new passengers. The passenger will be added anyway.
      */
    public int addPassenger(Passenger passenger){
        if(isFull())
            return -1;
        if(speed != 0)
            return -2;
        if(passengers.contains(passenger))
            return -3;
        passengers.add(passenger);
        if(!mode.isLegal())
            throw new IllegalStateException("The current drive mode does not allow new passengers!");
        return 0;

    }

    /**
     * Removes the passenger at the selected index if the car is not moving.
     * 
     * @param index the index of the passenger to remove.
     * @return the removed passenger if the operation was sucessful, else null (if the car is moving).
     * @throws IndexOutOfBoundsException if the selected index is out of range (index >= passengers.size() || index < 0)
     * @throws IllegalStateException if the current drive mode does not allow passengers removal.
     */
    public Passenger removePassenger(int index){
        if(index >= passengers.size() || index < 0)
            throw new IndexOutOfBoundsException("Passenger with index " + index + " does not exsist.");
        if(speed != 0)
            return null;
        Passenger p = passengers.remove(index);
        if(!mode.isLegal())
            throw new IllegalStateException("The current drive mode does not allow passengers removal!");
        return p;
    }


    /**
     * Removes a passenger if the car is not moving.
     * 
     * @param p the passenger to remove.
     * @return a boolean representing whether the operation was successful or not.
     * @throws IllegalStateException if the current drive mode does not allow passengers removal.
     */
    public boolean removePassenger(Passenger p){
        if(speed != 0)
            return false;
        boolean b = passengers.remove(p);
        if(!mode.isLegal())
            throw new IllegalStateException("The current drive mode does not allow passengers removal!");
        return b;
    }

    /**
     * If the car is not moving, fills up the gas tank.
     * @return a boolean representing whether the operation was successful or not.
     */
    public boolean refuel(){
        if(car.speed != 0) return false;
        fuel = 100d;
        return true;
    }

    /**
     * If the car is not moving, charges the battery.
     * @return a boolean representing whether the operation was successful or not.
     */
    public boolean charge(){
        if(car.speed != 0) return false;
        battery = 100d;
        return true;
    }

    /**
     * Prints the name and the weight of all the passengers inside the car.
     */
    public void printPassengers(){
        String out = "";
        out += "--- Passengers inside(" + passengers.size() +") ---\n";
        int len = out.length()-1;
        if(passengers.size() == 0){
            out += "Empty\n";
        }
        else{
            for(int i = 0; i < passengers.size(); i++){
                out += (i + 1) + ": " + passengers.get(i).name + ", " + passengers.get(i).weight + " Kg\n"; 
            }
        }
        for(int i = 0; i < len; i++){
            out += "-";
        }
        System.out.print(out);
    }

    private static String formatDouble(double d, int digits){
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(digits);
        String res = df.format(d).replace(',','.');
        return res;
    }

    /**
     * Prints the current state of the car: engine, speed, drive mode, passengers.
     */
    public void printState(){
        /**
         * Prints the current state of the car (engine, speed, mode, fuel, charge, passengers)
         */

        String out = "Engine: " + (engine? "On" : "Off") + " | " + "Speed: " + formatDouble(speed, 5) + " Km/h | Drive Mode: " + mode.getClass().getSimpleName().replace("Mode","") + " | Fuel: " + formatDouble(fuel, 5) + "% | Battery: " + formatDouble(battery, 5) + "%\n";
        
        System.out.println(out);
        printPassengers();
        System.out.println("\n");
    }
}
