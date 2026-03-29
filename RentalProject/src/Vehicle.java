public abstract class Vehicle {
    private String licensePlate;
    private String make;
    private String model;
    private int year;
    private VehicleStatus status;

    public enum VehicleStatus { Available, Held, Rented, UnderMaintenance, OutOfService }

    public Vehicle(String make, String model, int year) {
    	this.make = capitalize(make);
    	this.model = capitalize(model);
        this.year = year;
        this.status = VehicleStatus.Available;
        this.licensePlate = null;
    }

    public Vehicle() {
        this(null, null, 0);
    }

    public void setLicensePlate(String plate) {
    	// If plate is invalid throw an IllegalArgumentException 
    	if (!isValidPlate(plate)) {
    		throw new IllegalArgumentException("Invalid license plate format. License plate must be exactly 3 letters followed by 3 numbers.");
    	}
    	
        this.licensePlate =  plate.toUpperCase();
    }

    public void setStatus(VehicleStatus status) {
    	this.status = status;
    }

    public String getLicensePlate() { return licensePlate; }

    public String getMake() { return make; }

    public String getModel() { return model;}

    public int getYear() { return year; }

    public VehicleStatus getStatus() { return status; }

    public String getInfo() {
        return "| " + licensePlate + " | " + make + " | " + model + " | " + year + " | " + status + " |";
    }
    
    // A helper method that capitalizes the first letter of a string and makes the rest lowercase
    private String capitalize(String input) {
    	// return null if input is a null or empty string
    	if (input == null || input.isEmpty()) {
    		return null;
    	}
    	
    	return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    // Returns true if plate is not null, not empty(should have a length of 6), and follows the format three letters followed by three numbers
    private boolean isValidPlate(String plate) {
    	
    	if (plate == null || plate.length() != 6) {
    		return false;
    	}
    	
    	// First three characters of the string ( beginning to 3)
    	String letters = plate.substring(0,3);
    	// Last three characters of the string (3 to end)
    	String numbers = plate.substring(3);
    	// Validate first three characters of the string are letters
    	for (int i = 0; i < letters.length(); i++) {
    		// If character is not a letter return false
    		if(!Character.isLetter(letters.charAt(i))) {
    			return false;
    		}
    	}
    	
    	// Validate the following three characters are numbers
    	for (int i = 0; i < numbers.length(); i++) {
    		// If character is not a digit return false
    		if(!Character.isDigit(numbers.charAt(i))) {
    			return false;
    		}
    	}
    	
    	return true;
    }
    
    @Override public String toString() {
    	// Format: plate "|" year  make model
    	return String.format("%s | %d %s %s", licensePlate, year, make, model);
    }
}
