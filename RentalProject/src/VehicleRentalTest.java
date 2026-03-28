import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// For singleton testing
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;


class VehicleRentalTest {
	// Objects to test
	private RentalSystem testSystem;

	@BeforeEach
	void setUp() throws Exception {
		// Before each test get the global Access point of the RentalSystel (will create lazily if the RentalSystem does not exist yet)
		testSystem = RentalSystem.getInstance();
	}

	@Test
	void testLicensePlate() {
		// Valid objects for testing
		Vehicle vehicle1 = new Car("Toyota", "Corolla",2019, 5);
		Vehicle vehicle2 = new Minibus("Honda", "Civic", 2021, true);
		Vehicle vehicle3 = new PickupTruck("Ford", "Focus", 2024, 100, true);
		// Vehicle to assign an invalid plate
		Vehicle invalidVehicle = new Car("Shelby", "Cobra", 1962, 2);
		
		// Set valid license plates for the valid vehicles
		vehicle1.setLicensePlate("AAA100");
		vehicle2.setLicensePlate("ABC567");
		vehicle3.setLicensePlate("ZZZ999");
		
		
		// Test valid plates
		
		// Make sure vehicle 1's plate is valid and gets assigned 
		assertEquals("AAA100", vehicle1.getLicensePlate());
		
		// Vehicle 2's plate should equal "ABC567"
		assertTrue("ABC567".equals(vehicle2.getLicensePlate()));
		
		// Vehicle 3's plate should not be null
		assertNotNull(vehicle3.getLicensePlate());
		
		// Test invalid plates
		
		// Set plate to an empty string and verify it returns an IllegalArgumentException
		assertThrows(IllegalArgumentException.class,  () -> {
			// Set an invalid empty plate which should throw an exception
			invalidVehicle.setLicensePlate("");
		});
		
		// Set plate as null and verify it returns an IllegalArgumentException
		assertThrows(IllegalArgumentException.class,  () -> { 
			// Set an invalid null plate which should throw an exception
			invalidVehicle.setLicensePlate(null);
		});
		
		// Set plate over 6 characters with 4 numbers
		assertThrows(IllegalArgumentException.class, () -> {
			// Invalid format, should throw exception
			invalidVehicle.setLicensePlate("AAA1000");
		});
		
		// Set plate under 6 characters with only 2 numbers
		assertThrows(IllegalArgumentException.class, () -> {
			// Invalid format, should throw exception
			invalidVehicle.setLicensePlate("ZZZ99");
		});
		
		// After setting invalid plates invalidVehicle should have a null plate
		assertNull(invalidVehicle.getLicensePlate());
	}
	
	@Test 
	void testRentAndReturnVehicle() {
		// Vehicles to test
		Vehicle car = new Car("Volkswagen", "Beetle", 1990, 5);
		Vehicle truck = new PickupTruck("Dodge", "Ram", 2007, 1315, true);
		
		// Set valid plates
		car.setLicensePlate("BEE713");
		truck.setLicensePlate("RAM007");
		
		// Customers to test
		Customer customer1 = new Customer(38, "Mohamed");
		Customer customer2 = new Customer(39, "Emma");

		// Vehicles should both be AVAILABLE after creation
		assertEquals(Vehicle.VehicleStatus.Available, car.getStatus());
		assertEquals(Vehicle.VehicleStatus.Available, truck.getStatus());
		
		// Test the renting logic using the single instance of RentalSystem
		
		// Set the rental date to today
		LocalDate date = LocalDate.now();
		
		// Ensure a valid rental returns true
		assertTrue(testSystem.rentVehicle(car, customer1, date, 50.0));
		assertTrue(testSystem.rentVehicle(truck, customer2, date, 75.0));
		
		// Ensure both vehicles are marked as RENTED after being rented
		assertEquals(Vehicle.VehicleStatus.Rented, car.getStatus());
		assertEquals(Vehicle.VehicleStatus.Rented, truck.getStatus());
		
		// Ensure attempting to rent a vehicle which is already rented returns false
		// Rent the same vehicles again, asserting failure
		assertFalse(testSystem.rentVehicle(car, customer1, date, 50.0));
		assertFalse(testSystem.rentVehicle(truck, customer2, date, 75.0));
		
		// Test the returning logic
		
		// Ensure a valid return is successful and returns true
		assertTrue(testSystem.returnVehicle(car, customer1, date, 0.0));
		assertTrue(testSystem.returnVehicle(truck, customer2, date, 0.0));
		
		// Ensure both vehicles are marked as AVAILABLE after being returned
		assertEquals(Vehicle.VehicleStatus.Available, car.getStatus());
		assertEquals(Vehicle.VehicleStatus.Available, truck.getStatus());
		
		// Return the same vehicles again ensuring a vehicle cannot be returned without being rented first
		assertFalse(testSystem.returnVehicle(car, customer1, date, 0.0));
		assertFalse(testSystem.returnVehicle(truck, customer2, date, 0.0));
		
	}
	
	@Test
	void testSingletonRentalSystem() {
		try {
			// Get the constructor of the RentalSystemClass
			Constructor<RentalSystem> constructor = RentalSystem.class.getDeclaredConstructor();
			
			// Get the modifiers from the RentalSystem constructor
			int modifiers = constructor.getModifiers();
			
			// Assert the the constructor has the modifier PRIVATE
			assertEquals(Modifier.PRIVATE, modifiers);
			
			// Get the single instance of the RentalSystem using the global access point
			RentalSystem instance = RentalSystem.getInstance();
			
			// Assert the single instance of RentalSystem is not null
			assertNotNull(instance);
			
		} catch (Exception e) {
			// If a method throws an exception, make the test fail and display why
			fail("Test failed due to exception: " + e.getMessage());
		}
		
	}
}
