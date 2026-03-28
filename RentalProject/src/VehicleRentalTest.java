import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VehicleRentalTest {
	// Objects to test
	private RentalSystem testSystem;
	private Customer testCustomer;
	private Vehicle testVehicle;

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

}
