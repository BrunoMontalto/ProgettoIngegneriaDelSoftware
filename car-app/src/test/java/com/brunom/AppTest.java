package com.brunom;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * Unit test for simple App.
 */
public class AppTest {
    Car car;

    ArrayList<Passenger> passengersList = new ArrayList<>(
        Arrays.asList(
            new Passenger("Giorgio",59),
            new Passenger("Luca",70),
            new Passenger("Andrea",57),
            new Passenger("Lucia",50),
            new Passenger("Damiano",78),
            new Passenger("Carlo",61)
        )
    );

    @Before
    public void setUp(){
        Car.deleteInstance();
        car = Car.getInstance();
    }
    
    @Test
    public void testGetInstance(){
        Car c = Car.getInstance();
        assertTrue(c != null);
        Car.deleteInstance();
        Car.getInstance();
        assertTrue(c != null);
    }

    @Test
    public void testResetState(){
        car.resetState();
        assertTrue(car.speed == 0);
        assertTrue(car.mode.getClass().getSimpleName().equals("ComfortableMode"));
        assertTrue(car.fuel == 100d);
        assertTrue(car.battery == 100d);
        assertFalse(car.engine);
        assertTrue(car.passengers.size() == 0);
    }

    @Test
    public void shouldToggleEngine(){
        car.addPassenger(passengersList.get(0));
        assertTrue(car.toggleEngine() == 0);
        assertTrue(car.engine);
        assertTrue(car.toggleEngine() == 0);
        assertFalse(car.engine);
    }

    @Test
    public void shouldNotToggleEngine(){
        //There are no passengers, so the engine shouldn't turn on
        assertTrue(car.toggleEngine() == -1);
        assertTrue(!car.engine);
        
        car.addPassenger(passengersList.get(0));
        car.fuel = 0d;
        assertTrue(car.toggleEngine() == -2);
        assertTrue(!car.engine);
    }

    @Test
    public void weightShouldBeCarWeight(){
        //There are no passengers so the total weight should be the weight of the car
        assertTrue(car.weight == car.getTotalWeight());
    }

    @Test
    public void testSetMode(){
        car.addPassenger(passengersList.get(0));
        
        car.setMode(DriveModeEnum.Normal);
        assertTrue(car.mode.getClass().getSimpleName().equals("NormalMode"));

        car.setMode(DriveModeEnum.Comfortable);
        assertTrue(car.mode.getClass().getSimpleName().equals("ComfortableMode"));

        car.setMode(DriveModeEnum.Electric);
        assertTrue(car.mode.getClass().getSimpleName().equals("ElectricMode"));

        car.setMode(DriveModeEnum.Sport);
        assertTrue(car.mode.getClass().getSimpleName().equals("SportMode"));

        car.speed = 210d;
        assertTrue(car.setMode(DriveModeEnum.Sport) == -1);

        car.speed = 200d;
        assertTrue(car.setMode(DriveModeEnum.Sport) == 0);
        assertTrue(car.setMode(DriveModeEnum.Comfortable) == -1);
        assertTrue(car.setMode(DriveModeEnum.Electric) == -1);
        assertTrue(car.setMode(DriveModeEnum.Normal) == -1);

        car.speed = 120d;
        car.battery = 5d;
        assertTrue(car.setMode(DriveModeEnum.Comfortable) == -1);
        assertTrue(car.setMode(DriveModeEnum.Electric) == -1);
        assertTrue(car.setMode(DriveModeEnum.Normal) == 0);

        car.speed = 90d;
        car.battery = 100d;
        assertTrue(car.setMode(DriveModeEnum.Electric) == 0);
        assertTrue(car.setMode(DriveModeEnum.Comfortable) == -1);

        car.battery = 5d;
        assertTrue(car.setMode(DriveModeEnum.Electric) == -1);

        car.speed = 50d;
        assertTrue(car.setMode(DriveModeEnum.Comfortable) == 0);
    }

    @Test
    public void testCanAccelerate(){
        assertTrue(car.canAccelerate() == -1);
        car.engine = true;
        assertTrue(car.canAccelerate() == -2);
        car.addPassenger(passengersList.get(0));
        assertTrue(car.canAccelerate() == 0);
    }

    @Test
    public void shouldNotAccelerate(){
        car.engine = true;
        double oldSpeed = car.speed;
        assertTrue(car.accelerate(1d) == -2);
        assertTrue(car.speed == oldSpeed);

        car.engine = false;
        car.addPassenger(passengersList.get(0));
        assertTrue(car.accelerate(1d) == -1);
        assertTrue(car.speed == oldSpeed);
    }

    @Test
    public void accelerationShouldDecrease(){
        car.engine = true;
        car.addPassenger(passengersList.get(3));
        double firstAcceleration = car.accelerate(1d);

        car.speed = 0;
        car.addPassenger(passengersList.get(4));
        double secondAcceleration = car.accelerate(1d);

        assertTrue(firstAcceleration > secondAcceleration);
    }

    @Test
    public void accelerationShouldNotChange(){
        car.engine = true;
        //These 2 passenger weight 120 Kg together
        car.addPassenger(passengersList.get(1));
        car.addPassenger(passengersList.get(3));

        double firstAcceleration = car.accelerate(1d);
        
        //Let's remove all the passengers and stop the car
        car.resetState();
        car.engine = true;

        //These 2 passenger alsoweight 120 Kg together, so the acceleration shouldn't change
        car.addPassenger(passengersList.get(0));
        car.addPassenger(passengersList.get(5));

        double secondAcceleration = car.accelerate(1d);

        assertTrue(firstAcceleration == secondAcceleration);
    }

    @Test
    public void testAccelerationPowerRangeFix(){
        car.engine = true;
        car.addPassenger(passengersList.get(0));

        assertTrue(car.accelerate(500) == car.accelerate(1));
        assertTrue(car.accelerate(-500) == car.accelerate(0));
    }

    @Test
    public void testIsFull(){
        for(int i = 0; i < car.seats; i++){
            car.addPassenger(passengersList.get(i));
        }
        assertTrue(car.isFull());
    }

    @Test
    public void shouldAddPassenger(){
        assertTrue(car.addPassenger(passengersList.get(0)) == 0);
    }

    @Test
    public void shouldNotAddPassenger(){
        car.addPassenger(passengersList.get(1));
        car.addPassenger(passengersList.get(0));
        car.addPassenger(passengersList.get(2));
        car.addPassenger(passengersList.get(3));

        //At this point all seats are occupied
        assertTrue(car.addPassenger(passengersList.get(4)) == -1);
        assertTrue(car.passengers.size() == 4);

        //Let's free one seat
        car.removePassenger(0);

        car.speed = 10d;
        //Now that the car is moving the passenger shouldn't be added
        assertTrue(car.addPassenger(passengersList.get(1)) == -2);

        car.speed = 0d;
        //This passenger is already in the car so it shouldn't be added
        assertTrue(car.addPassenger(passengersList.get(0)) == -3);
        
        assertTrue(car.passengers.size() == 3);

        //Let's free 2 more seats and switch to sport mode
        car.removePassenger(0);
        car.removePassenger(0);
        car.setMode(DriveModeEnum.Sport);

        try{
            car.addPassenger(passengersList.get(0));
            fail("Expected IllegalStateException!");
        }
        catch(IllegalStateException e){
            car.removePassenger(passengersList.get(0));
            //success
        }

        assertTrue(car.passengers.size() == 1);
    }

    @Test
    public void shouldRemovePassenger(){
        car.addPassenger(passengersList.get(0));
        assertTrue(car.removePassenger(passengersList.get(0)));
        assertFalse(car.passengers.contains(passengersList.get(0)));

        car.addPassenger(passengersList.get(0));
        assertTrue(car.removePassenger(0) == passengersList.get(0));
        assertFalse(car.passengers.contains(passengersList.get(0)));
    }

    @Test
    public void shouldNotRemovePassenger(){
        car.addPassenger(passengersList.get(0));
        car.speed = 10d;
        assertFalse(car.removePassenger(passengersList.get(0)));
        assertTrue(car.passengers.contains(passengersList.get(0)));

        assertTrue(car.removePassenger(0) == null);
        assertTrue(car.passengers.contains(passengersList.get(0)));

        assertTrue(car.passengers.size() == 1);

        car.setMode(DriveModeEnum.Sport);
        car.speed = 0d;
        try{
            car.removePassenger(passengersList.get(0));
            fail("Expected IllegalStateException!");
        }
        catch(IllegalStateException e){
            //success
        }

        car.addPassenger(passengersList.get(0));
        try{
            car.removePassenger(0);
            fail("Expected IllegalStateException!");
        }
        catch(IllegalStateException e){
            //success
        }

        car.addPassenger(passengersList.get(0));
        try{
            car.removePassenger(1);
            fail("Expected IndexOutOfBoundsException!");
        }
        catch(IndexOutOfBoundsException e){
            //success
        }

        try{
            car.removePassenger(-1);
            fail("Expected IndexOutOfBoundsException!");
        }
        catch(IndexOutOfBoundsException e){
            //success
        }
    }

    @Test
    public void TestRefuelCharge(){
        car.speed = 10d;
        car.fuel = 0d;
        car.battery = 0d;
        assertFalse(car.refuel());
        assertFalse(car.charge());

        car.speed = 0;
        assertTrue(car.refuel());
        assertTrue(car.charge());
        assertTrue(car.fuel == 100d);
        assertTrue(car.battery == 100d);
    }

    @Test
    public void testPrintState(){
        car.printState();
        car.addPassenger(passengersList.get(0));
        car.engine = true;
        car.printState();
    }


    @Test
    public void testModeAcceleration(){
        car.addPassenger(passengersList.get(0));
        for(DriveModeEnum e:DriveModeEnum.values()){
            car.fuel = 100d;
            car.speed = 3d;
            double oldSpeed = 3d;
            car.engine = true;
            car.setMode(e);
            double a = car.accelerate(1);
            assertTrue(a == car.speed - oldSpeed);
            assertTrue(car.engine);
            if(e == DriveModeEnum.Electric) continue;
            car.speed = 0d;
            car.engine = true;
            car.fuel = 0.001;
            car.speed = car.mode.maxSpeed;
            car.accelerate(1);
            assertTrue(car.speed == car.mode.maxSpeed);
            assertFalse(car.engine);
        }

        car.speed = 90d;
        car.fuel = 100d;
        car.engine = true;
        car.setMode(DriveModeEnum.Electric);
        car.accelerate(1d);
        assertTrue(car.mode.getClass().getSimpleName().equals("NormalMode"));

        car.speed = 50d;
        car.battery = 10.01;
        car.setMode(DriveModeEnum.Electric);
        car.accelerate(1d);
        assertTrue(car.mode.getClass().getSimpleName().equals("NormalMode"));

        car.fuel = 0.0001;
        car.battery = 100d;
        car.setMode(DriveModeEnum.Electric);
        car.accelerate(1);
        assertFalse(car.engine);
    }

    @Test
    public void testBraking(){
        for(DriveModeEnum e:DriveModeEnum.values()){
            car.setMode(e);
            double oldBattery = 50d;
            double oldSpeed = car.speed;
            car.addPassenger(passengersList.get(0));
            car.brake();
            assertTrue(car.battery > oldBattery);
            assertTrue(car.speed <= oldSpeed);
        }
    }
}
