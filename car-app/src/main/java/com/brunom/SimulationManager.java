package com.brunom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import java.text.DecimalFormat;

public class SimulationManager{
    static DecimalFormat df = new DecimalFormat();
    public static void main(){
        Car car = Car.getInstance();
        Scanner input = new Scanner(System.in);
        String ans;
        ArrayList<Passenger> passengers = new ArrayList<>(
            Arrays.asList(
                new Passenger("Giorgio",59),
                new Passenger("Luca",70),
                new Passenger("Andrea",57),
                new Passenger("Lucia",50),
                new Passenger("Damiano",78)
            )
        );

        ArrayList<String> driveModeDescriptions = new ArrayList<>(
            Arrays.asList(
                "Good for urban driving. Allows to reach the maximum speed of 50 Km/h.",
                "For everyday driving. Allows to reach the maximum speed of 120 Km/h.",
                "Consumes battery and a few fuel. When the speed is above 90 Km/h or the battery is under 10% it automatically switches to Normal mode.",
                "For high speed driving. Allows to reach the maximum speed of 200 Km/h. Possible when there is only one passenger."
            )
        );

        
        while(true){
            clearScreen();
            System.out.println("-------- Main Menu --------\n0: Print car state\n1: Print available passengers\n2: Add a passenger\n3: Remove a passenger\n4: Passenger Editor\n5: Select drive mode\n6: Toggle engine\n7: Give gas\n8: Brake\n9: Gas Station\n10: Reset car state.\n---------------------------");
            ans = input.nextLine();

            System.out.println("");
            switch(ans){
                case "0":
                    clearScreen();
                    car.printState();
                    break;
                case "1":
                    clearScreen();
                    printPassengers(car, passengers, true);
                    System.out.println("");
                    break;
                case "2":
                    InputPassengerAdd(input, car, passengers);
                    break;
                case "3":
                    InputPassengerRemove(input, car);
                    break;
                case "4":
                    PassengerEditor(input, car, passengers);
                    break;
                case "5":
                    inputDriveMode(input, car, driveModeDescriptions);
                    break;
                case "6":
                    int s = car.toggleEngine();
                    switch(s){
                        case -1:
                            System.out.println("Cannot toggle engine if there is not any passenger!");
                            break;
                        case -2:
                            System.out.println("You are out of fuel! Use the '9' option to refuel.");
                            break;
                        case 0:
                            System.out.println(car.engine? "Engine turned on." : "Engine turned off.");
                            break;
                    }
                    break;
                case "7":
                    InputAccelerate(input, car);
                    break;
                case "8":
                    if(car.passengers.size() == 0){
                        System.out.println("Cannot brake: there are no passengers!");
                        break;
                    }
                    clearScreen();
                    car.brake();
                    System.out.println("Braking... Current speed: " + formatDouble(car.speed, 5) + " Km/h");
                    break;
                case "9":
                    GasStation(input, car);
                    break;
                case "10":
                    car.resetState();
                    System.out.println("Car state reset.");
                    break;

                default:
                    System.out.println("Unknown option.");
            }
            System.out.println("Press ENTER to continue.");
            input.nextLine();
        }
    }

    public static String formatDouble(double d, int digits){
        df.setMaximumFractionDigits(digits);
        String res = df.format(d).replace(',','.');
        return res;
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void printPassengers(Car car, ArrayList<Passenger> passengers, boolean availableOnly){
        System.out.println(availableOnly? "--- Available Passengers ---" : "--- Passengers ---");
        int n = 0;

        for(int i = 0; i < passengers.size(); i++){
            if(availableOnly && car.passengers.contains(passengers.get(i)))
                continue;
            n++;
            System.out.println((i + 1) + ": " + passengers.get(i).name + ", " + passengers.get(i).weight + " Kg");
        }
        if(n == 0){
            System.out.println("Empty");
        }
        System.out.println(availableOnly? "----------------------------" : "------------------");
    }

    public static void InputPassengerAdd(Scanner input, Car car, ArrayList<Passenger> passengers){
        int availablePassengers = 0;
        for(Passenger p : passengers){
            if(!car.passengers.contains(p)) availablePassengers++;
        }
        if(availablePassengers == 0){
            System.out.println("There are no available passengers!");
            return;
        }
        if(car.isFull()){
            System.out.println("The car is full!");
            return;
        }
        if(car.speed != 0){
            System.out.println("You cannot add passengers while the car is moving!");
            return; 
        }
        clearScreen();
        printPassengers(car, passengers, true);
        System.out.println((passengers.size() + 1) + ": Back to Main Menu\n");
        System.out.println("Select a passenger to add.");
        String ans = input.nextLine();
        int p = 0;
        try{
            p = Integer.parseInt(ans);
        }
        catch(NumberFormatException e){
            message(input, "\nPlease enter a valid number.");
            InputPassengerAdd(input, car, passengers);
            return;
        }
        System.out.println("");
        if(p == passengers.size() + 1){
            System.out.println("Operation canceled.");
            return;
        }

        try{
            int s = car.addPassenger(passengers.get(p-1));
            if(s == -3){
                message(input, "Please select a passenger that is not already in the car!");
                InputPassengerAdd(input, car, passengers);
                return;
            }
            else{
                System.out.println("Passenger " + passengers.get(p-1).name + " added successfully.");
            }    
        }
        catch(IndexOutOfBoundsException e){
            message(input, "Please enter a valid number.");
            InputPassengerAdd(input, car, passengers);
        }
        catch(IllegalStateException e){
            car.passengers.remove(passengers.get(p-1));
            System.out.println(e.getMessage());
        }
    }

    public static void InputPassengerRemove(Scanner input, Car car){
        if(car.passengers.size() == 0){
            System.out.println("There are no passengers!");
            return;
        }
        if(car.speed != 0.0d){
            System.out.println("You cannot remove passengers while the car is moving!\n");
            return;
        }
        clearScreen();
        car.printPassengers();
        System.out.println("");
        System.out.println((car.passengers.size() + 1) + ": Back to Main Menu\n");
        System.out.println("Select a passenger to remove.");
        String ans = input.nextLine();
        int p = 0;
        try{
            p = Integer.parseInt(ans);
        }
        catch(NumberFormatException e){
            message(input, "\nPlease enter a valid number.");
            InputPassengerRemove(input, car);
            return;
        }
        System.out.println("");
        if(p == car.passengers.size() + 1){
            System.out.println("Operation canceled.");
            return;
        }

        Passenger toDel = null;
        try{
            toDel = car.passengers.get(p-1);
            car.removePassenger(toDel);
            System.out.println("Passenger " + toDel.name +" removed.");
        }
        catch(IndexOutOfBoundsException e){
            message(input, "Please enter a valid number.");
            InputPassengerRemove(input, car);
        }
        catch(IllegalStateException e){
            car.addPassenger(toDel);
            message(input, "The current drive mode does not allow passenger removal!");
            InputPassengerRemove(input, car);
        }
        
    }

    public static boolean PassengerAlreadyExists(ArrayList<Passenger> passengers, String name){
        for(Passenger p: passengers){
            if(p.name.equals(name))
                return true;
        }
        return false;
    }

    public static void PassengerEditor(Scanner input, Car car, ArrayList<Passenger> passengers){
        while(true){
            clearScreen();
            System.out.println("--- Passenger Editor ---\n1: Print passengers\n2: New passenger\n3: Delete passenger\n------------------------\n4: Back to Main Menu\n");
            String ans = input.nextLine();
            switch(ans){
                case "1":
                    clearScreen();
                    printPassengers(null, passengers, false);
                    System.out.println("");
                    break;
                case "2":
                    Passenger p = new Passenger();

                    //Input name
                    clearScreen();
                    System.out.print("New passenger name ('back' to undo): ");
                    ans = input.nextLine();
                    if(ans.equals("back")){
                        System.out.println("\nOperation canceled.");
                        break;
                    }
                    
                    boolean br = false;
                    while(PassengerAlreadyExists(passengers, ans) || ans.equals("")){
                        message(input, ans.equals("")? "\nInvalid name!": "\nPassenger " + ans + " already exists!");
                        clearScreen();
                        System.out.print("New passenger name ('back' to undo): ");
                        ans = input.nextLine();
                        if(ans.equals("back")){
                            System.out.println("\nOperation canceled.");
                            br = true;
                            break;
                        }
                    }
                    if(br) break;
                    p.name = ans;
                    
                    //Input weight
                    clearScreen();
                    System.out.print("New passenger name: " + p.name + "\nNew passenger weight ('back' to undo): ");
                    ans = input.nextLine();
                    if(ans.equals("back")){
                        System.out.println("\nOperation canceled.");
                        break;
                    }

                    br = false;
                    while(!ans.matches("\\d+")){
                        message(input, "\nInvalid weight!");
                        clearScreen();
                        System.out.print("New passenger name: " + p.name + "\nNew passenger weight ('back' to undo): ");
                        ans = input.nextLine();
                        if(ans.equals("back")){
                            System.out.println("\nOperation canceled.");
                            br = true;
                            break;
                        }
                    }
                    if(br) break;
                    p.weight = Integer.parseInt(ans);
                    clearScreen();
                    System.out.println("New passenger name: " + p.name + "\nNew passenger weight: " + p.weight + "\n");
                    System.out.println("Passenger " + p.name + " created.");

                    passengers.add(p);
                    break;
                case "3":
                    clearScreen();
                    printPassengers(null, passengers, false);
                    System.out.println((passengers.size()+1) + ": Back to Passenger Editor");
                    System.out.println("\nSelect passenger to delete.");
                    ans = input.nextLine();
                    while(!ans.matches("\\d+") || Integer.parseInt(ans) < 1 || Integer.parseInt(ans) > passengers.size()+1){
                        message(input,"\nPlease enter a valid number!");
                        clearScreen();
                        printPassengers(null, passengers, false);
                        System.out.println((passengers.size()+1) + ": Back to Passenger Editor");
                        System.out.println("\nSelect passenger to delete.");
                        ans = input.nextLine();
                    }
                    if(ans.equals((passengers.size()+1) + "")){
                        System.out.println("\nOperation canceled.");
                        break;
                    }
                    Passenger toDel = passengers.get(Integer.parseInt(ans)-1);
                    if(car.passengers.contains(toDel)){
                        System.out.println("\nPassenger " + toDel.name + " is inside the car! Do you want to delete it anyway?(y/n)");
                        ans = input.nextLine();
                        if(ans.equals("y")){
                            try{
                                if(!car.removePassenger(toDel)){
                                    System.out.println("\nOperation failed. The car is moving.");
                                    break;
                                }
                            }
                            catch(IllegalStateException e){
                                System.out.println("\nOperation failed. " + e.getMessage());
                                car.addPassenger(toDel);
                                break;
                            }

                        }
                        else {
                            System.out.println("");
                            break;
                        }
                    }
                    passengers.remove(toDel);
                    System.out.println("\nPassenger " + toDel.name + " deleted.");
                    
                    
                    break;
                case "4":
                    System.out.println("\nExiting editor.");
                    return;
                default:
                    System.out.println("\nUnknow option.");
                    break;
            }
            System.out.println("Press ENTER to continue.");
            input.nextLine();
        }
    }
    
    public static void inputDriveMode(Scanner input, Car car, ArrayList<String> descriptions){
        clearScreen();
        String s = "------ Drive modes ------";
        int i = 0;
        for(; i < DriveModeEnum.values().length; i++){
            s += "\n" + (i+1) + ": " + DriveModeEnum.values()[i].toString() + " - " + descriptions.get(i);
        }
        s += "\n-------------------------\n" + (i+1) + ": Back to Main Menu\n\nSelect a drive mode.";
        System.out.println(s);
        String ans = input.nextLine();
        int d = 0;
        try{
            d = Integer.parseInt(ans);
        }
        catch(NumberFormatException e){
            message(input,"\nPlease enter a valid number.");
            inputDriveMode(input, car, descriptions);
            return;
        }
        System.out.println("");
        if(d == DriveModeEnum.values().length+1) {
            System.out.println("Operation canceled.");
            return;
        }
        int o;
        try{
            o = car.setMode(DriveModeEnum.values()[d - 1]);
        }
        catch(ArrayIndexOutOfBoundsException e){
            message(input, "Please enter a valid number.");
            inputDriveMode(input, car, descriptions);
            return;
        }
        switch(o){
            case 0:
                System.out.println("Driving Mode set to " + DriveModeEnum.values()[d - 1].name() + ".");
                break;
            case -1:
                System.out.println("The selected drive mode is illegal for the current state");
                break;
            case -2:
                message(input, "Please enter a valid number.");
                inputDriveMode(input, car, descriptions);
                break;
        }


    }

    public static void InputAccelerate(Scanner input, Car car){
        int ca = car.canAccelerate();
        if(ca == -1){
            System.out.println("Cannot accelerate: engine is off!");
            return;
        }
        else if(ca == -2){
            System.out.println("Cannot accelerate: there are no passengers!");
            return;
        }
        clearScreen();
        System.out.println("Choose an amount of acceleration. (max: 100.0)");
        String ans = input.nextLine();
        double a;
        try{
            a = Double.parseDouble(ans)/100;
        }
        catch(NumberFormatException e){
            message(input, "\nPlease insert a valid number!");
            InputAccelerate(input, car);
            return;
        }
        DriveMode oldmode = car.mode;
        a = car.accelerate(a);
        if(car.mode != oldmode){
            System.out.print("\nSwitching to Electric mode...");
        }
        if(car.speed == car.mode.maxSpeed){
            System.out.print("\nMax speed reached!");
        }
        if(car.fuel == 0d){
            System.out.print("\nYou are out of fuel! Turning engine off...");
        }
        System.out.println("\nAcceleration: " + formatDouble(a, 5) +" Km/h. Current speed: " + formatDouble(car.speed, 5) + " Km/h.");
    }

    public static void GasStation(Scanner input, Car car){
        if(car.speed != 0){
            System.out.println("Cannot refuel/charge while the car is moving!");
            return;
        }
        clearScreen();
        System.out.println("--- Gas Station ---\n1: Refuel\n2: Charge battery\n-------------------\n3: Back to Main Menu\n\nSelect an option.");
        String ans = input.nextLine();
        int o = 0;
        try{
            o = Integer.parseInt(ans);
        }
        catch(NumberFormatException e){
            message(input,"\nPlease enter a valid number.");
            GasStation(input, car);
            return;
        }
        if(o == 3){
            System.out.println("\nLeaving gas station...");
            return;
        }
        if(o != 1 && o != 2){
            message(input,"\nPlease enter a valid number.");
            GasStation(input, car);
            return;
        }
        if(o == 1){
            car.refuel();
            message(input, "\nGas tank filled up.");
            GasStation(input, car);
        }
        else{
            car.charge();
            message(input, "\nBattery charged.");
            GasStation(input, car);
        }

    }

    public static void message(Scanner input, String msg){
        System.out.println(msg + " \nPress ENTER to continue.\n");
        input.nextLine();
    }
}
    

