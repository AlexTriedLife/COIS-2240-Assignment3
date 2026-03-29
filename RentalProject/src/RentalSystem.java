import java.util.List;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class RentalSystem {
	// Private static instance to implement the singleton design pattern (allowing only one instance)
	private static RentalSystem instance;
	
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();
    
    // Private constructor prevents creation of multiple instances
    private RentalSystem() {
    	// Populate vehicles, customers, and rentalHistory with data from local storage
    	loadData();
    }
    
	// Global access point lazily creates the instance if it does not exist
	public static RentalSystem getInstance() {
		// Lazy instantiation
		if (instance == null) {
			instance = new RentalSystem();
		}
		return instance;
	}
    
    public boolean addVehicle(Vehicle vehicle) {
    	// Check if vehicle is already in the system by checking license plate in order to prevent duplicate vehicles
    	
    	// If findVehicleByPlate does not return null it means that plate already exists in the system
    	if (findVehicleByPlate(vehicle.getLicensePlate()) != null) {
    		// Log error
    		System.out.println("Error: Vehicle with license plate: " + vehicle.getLicensePlate() + " already exists in the system.");
    		
    		// Vehicle was not added
    		return false;
    	}

    	// Only add vehicle to system and save if vehicle doesn't already exist
        vehicles.add(vehicle);
        saveVehicle(vehicle);
        // Vehicle successfully added
        return true;
    }

    public boolean addCustomer(Customer customer) {
    	// Check if customer is already in the system by checking customerID in order to prevent duplicate customers
    	
    	// If findCustomerByID does not return null it means that customerID already exists in the system
    	if (findCustomerById(customer.getCustomerId()) != null) {
    		// Log error
    		System.out.println("Error: Customer with ID: " + customer.getCustomerId() + " already exists in the system.");
    		
    		// Customer was not added
    		return false;
    	}

    	// Only add customer to system and save if customer doesn't already exist
        customers.add(customer);
        saveCustomer(customer);
        // Customer successfully added
        return true;
    }

    public boolean rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
            vehicle.setStatus(Vehicle.VehicleStatus.Rented);
            // Create record for vehicle being rented
            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            // Add record to the system
            rentalHistory.addRecord(record);
            // Save record to file
            saveRecord(record);
            System.out.println("Vehicle rented to " + customer.getCustomerName());
        }
        else {
            System.out.println("Vehicle is not available for renting.");
            // Return false if vehicle isn't available
            return false;
        }
        
        // Return true if vehicle was available and was rented successfully
        return true;
    }

    public boolean returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
            vehicle.setStatus(Vehicle.VehicleStatus.Available);
            // Create record for vehicle being returned
            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            // Add record to system
            rentalHistory.addRecord(record);
            // Save record to file
            saveRecord(record);
            System.out.println("Vehicle returned by " + customer.getCustomerName());
        }
        else {
            System.out.println("Vehicle is not rented.");
            // Return false if vehicle is not rented or successfully returned 
            return false;
        }
        
        // Return true if vehicle was successfully returned
        return true;
    }    

    public void displayVehicles(Vehicle.VehicleStatus status) {
        // Display appropriate title based on status
        if (status == null) {
            System.out.println("\n=== All Vehicles ===");
        } else {
            System.out.println("\n=== " + status + " Vehicles ===");
        }
        
        // Header with proper column widths
        System.out.printf("|%-16s | %-12s | %-12s | %-12s | %-6s | %-18s |%n", 
            " Type", "Plate", "Make", "Model", "Year", "Status");
        System.out.println("|--------------------------------------------------------------------------------------------|");
    	  
        boolean found = false;
        for (Vehicle vehicle : vehicles) {
            if (status == null || vehicle.getStatus() == status) {
                found = true;
                String vehicleType;
                if (vehicle instanceof Car) {
                    vehicleType = "Car";
                } else if (vehicle instanceof Minibus) {
                    vehicleType = "Minibus";
                } else if (vehicle instanceof PickupTruck) {
                    vehicleType = "Pickup Truck";
                } else {
                    vehicleType = "Unknown";
                }
                System.out.printf("| %-15s | %-12s | %-12s | %-12s | %-6d | %-18s |%n", 
                    vehicleType, vehicle.getLicensePlate(), vehicle.getMake(), vehicle.getModel(), vehicle.getYear(), vehicle.getStatus().toString());
            }
        }
        if (!found) {
            if (status == null) {
                System.out.println("  No Vehicles found.");
            } else {
                System.out.println("  No vehicles with Status: " + status);
            }
        }
        System.out.println();
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }
    
    public void displayRentalHistory() {
        if (rentalHistory.getRentalHistory().isEmpty()) {
            System.out.println("  No rental history found.");
        } else {
            // Header with proper column widths
            System.out.printf("|%-10s | %-12s | %-20s | %-12s | %-12s |%n", 
                " Type", "Plate", "Customer", "Date", "Amount");
            System.out.println("|-------------------------------------------------------------------------------|");
            
            for (RentalRecord record : rentalHistory.getRentalHistory()) {                
                System.out.printf("| %-9s | %-12s | %-20s | %-12s | $%-11.2f |%n", 
                    record.getRecordType(), 
                    record.getVehicle().getLicensePlate(),
                    record.getCustomer().getCustomerName(),
                    record.getRecordDate().toString(),
                    record.getTotalAmount()
                );
            }
            System.out.println();
        }
    }
    
    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }
    
    public Customer findCustomerById(int id) {
        for (Customer c : customers)
            if (c.getCustomerId() == id)
                return c;
        return null;
    }
    
    
    // FILE I/O //
    
    
    // OUTPUT //
    
    // Save vehicle locally in a file called vehicles.txt
    public void saveVehicle(Vehicle vehicle) {
		// Create a BufferedWriter which appends Vehicle information to the file vehicles.txt
    	// If saving fails, print an error message
    	try (BufferedWriter writer = new BufferedWriter(new FileWriter("vehicles.txt", true))) {
    		// Comma separate vehicle attributes into a new string with the format "Plate, Make, Model, Year, Status"
    		String vehicleData = String.format("%s,%s,%s,%d,%s",
    				vehicle.getLicensePlate(),
    				vehicle.getMake(),
    				vehicle.getModel(),
    				vehicle.getYear(),
    				vehicle.getStatus()
    		);
    		
    		// Use writer to write vehicle data to file vehicles.txt
    		writer.write(vehicleData);
    		// Write a new line for the next data entry
    		writer.newLine();
    	
    	} catch (IOException e) {
			// Log error
			System.out.println("Error saving vehicle: " + e.getMessage());
		}
    }
    
    // Save customer locally in a file called customers.txt
    public void saveCustomer(Customer customer)	{
		// Create a BufferedWriter which appends Customer information to the file customers.txt
    	// If saving fails, print an error message
    	try (BufferedWriter writer = new BufferedWriter(new FileWriter("customers.txt", true))) {
    		// Comma separate customer attributes into a new string with the format "ID, Name"
    		String customerData = String.format("%d,%s",
    				customer.getCustomerId(),
    				customer.getCustomerName()
    		);
    		
    		// Write customer data to file
    		writer.write(customerData);
    		// Write a new line for the next data entry
    		writer.newLine();
    		
    	}	catch (IOException e) {
    		// Log error
    		System.out.println("Error saving customer: " + e.getMessage());
    	}
    }
    
    // Save RentalRecord locally in a file called rental_records.txt
    public void saveRecord(RentalRecord record) {
    	
    	// Create a BufferedWriter which appends RentalRecord to the file rental_records.txt
    	// If saving fails, print an error message
    	try (BufferedWriter writer = new BufferedWriter(new FileWriter("rental_records.txt", true))) {
    		// Comma separate RentalRecord attributes into a new string with the format "RecordType, VehiclePlate, CustomerName, RecordDate, RecordAmount"
    		String recordData = String.format("%s,%s,%s,%s,%.2f",
    				record.getRecordType(),
    				record.getVehicle().getLicensePlate(),
    				record.getCustomer().getCustomerName(),
    				record.getRecordDate().toString(),
    				record.getTotalAmount()			  				
    		);
    		// Write record data to file
    		writer.write(recordData);
    		// Write a new line for the next data entry
    		writer.newLine();

    	} catch (IOException e) {
    		// Log error
    		System.out.println("Error saving record: " + e.getMessage());

    	}

    }
    
    // INPUT //
    
    private void loadData() {
    	// Load vehicles from vehicles.txt
    	
    	// Read in data from vehicles.txt
    	try (BufferedReader reader = new BufferedReader(new FileReader("vehicles.txt"))) {
    		// The line to read in
    		String line;
    		while ((line = reader.readLine()) != null) {
    			// Split csv values(plate,make,model,year,status) to a string array
    			String vehicleData[] = line.split(",");
    			
    			// Extract the vehicle data
    			String plate = vehicleData[0];
    			String make = vehicleData[1];
    			String model = vehicleData[2];
    			int year = Integer.parseInt(vehicleData[3]);
    			// Match the string to the corresponding enum value 
    			Vehicle.VehicleStatus status = Vehicle.VehicleStatus.valueOf(vehicleData[4]);
    			
    			// Create the new vehicle and save it to the list vehicles
    			// 5 is number of seats
    			Vehicle vehicle = new Car(make, model, year, 5);
    			vehicle.setLicensePlate(plate);
    			vehicle.setStatus(status);
    			vehicles.add(vehicle);
    			
    		}
    		
		} catch (IOException e) {
			// Log error if file not found
			System.out.println("Error loading vehicles.txt: " + e.getMessage());
		}
    	
    	// Load customers from customers.txt
    	
    	// Read in data from customers.txt
    	try (BufferedReader reader = new BufferedReader(new FileReader("customers.txt"))) {
    		// The line to read in
    		String line;
    		while ((line = reader.readLine()) != null) {
    			// Split csv values(id,name) to a string array
    			String customerData[] = line.split(",");
    			
    			// Extract the vehicle data
    			int customerID = Integer.parseInt(customerData[0]);
    			String name = customerData[1];
    			
    			// Create the new Customer and add to the list customers
    			Customer customer = new Customer(customerID, name);
    			customers.add(customer);
    		}
    		
		} catch (IOException e) {
			// Log error if file not found
			System.out.println("Error loading customers.txt: " + e.getMessage());
		}
    	
    	// Load rental records from rental_records.txt
    	
    	// Read in data from customers.txt
    	try (BufferedReader reader = new BufferedReader(new FileReader("rental_records.txt"))) {
    		// The line to read in
    		String line;
    		while ((line = reader.readLine()) != null) {
    			// Split csv values (RecordType, VehiclePlate, CustomerName, RecordDate, RecordAmount) to a string array
    			String recordData[] = line.split(",");
    			
    			// Extract the record data
    			String recordType = recordData[0];
    			String vehiclePlate = recordData[1];
    			String customerName = recordData[2];
    			// Convert date string into a LocalDate
    			LocalDate recordDate = LocalDate.parse(recordData[3]);
    			double totalAmount = Double.parseDouble(recordData[4]);
    			
    			
    			// Find vehicle by plate
    			Vehicle vehicle = findVehicleByPlate(vehiclePlate);
    			
    			// Set to the corresponding customer in customers if found
    			Customer customer = null;
    			// Find if customer exists in customers
    			for (Customer c: customers) {
    				// If customer name is in the system
    				if(c.getCustomerName().equals(customerName)) {
    					customer = c;
    				}
    			}
    			
    			// If customer and vehicle are both valid create a new RentalRecord and add it to RentalHistory
    			if (vehicle != null && customer != null) {
    				RentalRecord record = new RentalRecord(vehicle, customer, recordDate, totalAmount, recordType);
    				rentalHistory.addRecord(record);
    				
    				// Update the vehicle state to match the state of the transaction in the rental history
    				// If vehicle is rented set its status to Rented, if returned, set status to Available
    				if (record.getRecordType().equalsIgnoreCase("RENT")) {
    					vehicle.setStatus(Vehicle.VehicleStatus.Rented);
    				} else if(record.getRecordType().equalsIgnoreCase("RETURN")) {
    					vehicle.setStatus(Vehicle.VehicleStatus.Available);
    				}
    			}
    		}
    		
		} catch (IOException e) {
			// Log error if file not found
			System.out.println("Error loading rental_records.txt: " + e.getMessage());
		}
    }
    
    public List<Customer> getCustomers() {
    	return this.customers;
    }
    
    public List<Vehicle> getAvailableVehicles() {
    	List<Vehicle> available = new ArrayList<>();
    	
    	for (Vehicle vehicle : this.vehicles) {
    		// If vehicle status is available add it to the available list
    		if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
    		available.add(vehicle);
    		}
    	}
    	
    	return available;
    }
    
    
}