
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;


public class RentalSystemGUI extends Application {

	private RentalSystem rentalSystem;
	// The window layout
	private BorderPane root;
	
	@Override 
	public void start(Stage primaryStage) {
		try {
			// Get the single Rental System instance
			rentalSystem = RentalSystem.getInstance();
			
			
			root = new BorderPane();
			
			// Set the default view to addVehicle
			root.setCenter(createAddVehicleComponent());
			
			// Scene setup
			Scene scene = new Scene(root, 800, 600);
			primaryStage.setTitle("Vehicle Rental App");
			primaryStage.setScene(scene);
			primaryStage.show();
			
			// GUI components
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
		
	}
	
	// Display information or error
	private void displayMessage(Alert.AlertType type,String message) {
		Alert alert = new Alert(type);
		alert.setTitle(type.toString());
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	// The component for adding a vehicle
	
	private GridPane createAddVehicleComponent() {
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setAlignment(Pos.CENTER);
		
		// A drop down for select vehicle type
		ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList("Car", "Minibus", "Pickup Truck"));
		// Set default vehicle type to car
		typeBox.setValue("Car");
		
		// Inputs for vehicle details
		TextField makeField = new TextField();
		TextField modelField = new TextField();
		TextField yearField = new TextField();
		TextField plateField = new TextField();
		plateField.setPromptText("e.g: AAA000");
		
		
		// Dynamic inputs and labels which only show depending on vehicle type
		
		// For Car
		TextField seatsField = new TextField();		
		Label seatsLabel = new Label("Number of Seats:");
		
		// For MiniBus
		CheckBox accessibleBox = new CheckBox("Vehicle is Accessible");
		
		// For PickupTruck
		TextField cargoField = new TextField();
		cargoField.setPromptText("Cargo size (kg): e.g. 1000");
		Label cargoLabel = new Label("Cargo size:");
		CheckBox trailerBox = new CheckBox("Has trailer");
		
		
		// The area containing the dynamic components. Changes what displays depending on Vehicle type, space elements by 10 pixels
		VBox dynamicArea = new VBox(10);
		
		// Set dynamic area to show car fields by default. Fields are shown in an HBox with spacing of 10 pixels
		dynamicArea.getChildren().addAll(new HBox(10, seatsLabel, seatsField));
				
		// Button for submission
		Button submitBtn = new Button("Add Vehicle");
		
		// Update dynamic area if ComboBox value changes
		typeBox.setOnAction(event -> {
			// Clear the fields being displayed
			dynamicArea.getChildren().clear();
			// Get the type selected in the combo box
			String selectedType = typeBox.getValue();
			
			// Display the appropriate fields depending on selected vehicle type. Add label then input
			if (selectedType.equals("Car")) {
				dynamicArea.getChildren().addAll(new HBox(10, seatsLabel, seatsField));
			} else if (selectedType.equals("Minibus")) {
				dynamicArea.getChildren().addAll(new HBox(accessibleBox));
			} else if (selectedType.equals("Pickup Truck")) {
				// Add the label and input together and the checkbox below
				dynamicArea.getChildren().addAll(new HBox(10, cargoLabel, cargoField), trailerBox);

			}
		});
						
		
		
		
		
		
		
		// Grid layout
		
		// Add a label to the top of the grid spanning two columns
		grid.add(new Label("Add new Vehicle"), 0, 0, 2, 1);
		// Add the vehicle type selector to the second row of the grid and add a label to the left of it
		grid.add(new Label("Type:"), 0, 1);
		grid.add(typeBox, 1, 1);
		// Create the input for make in the third row of the grid and add a label to the left of it
		grid.add(new Label("Make:"), 0, 2);
		grid.add(makeField, 1, 2);
		// Create the input for model in the fourth row of the grid and add a label to the left of it
		grid.add(new Label("Model:"), 0, 3);
		grid.add(modelField, 1, 3);
		// Create the input for year in the fifth row of the grid and add a label to the left of it
		grid.add(new Label("Year:"), 0, 4);
		grid.add(yearField, 1, 4);
		// Create the input for license plate in the sixth row of the grid and add a label to the left of it
		grid.add(new Label("License Plate:"), 0, 5);
		grid.add(plateField, 1, 5);
		// Add the dynamic area to the seventh row spanning two columns
		grid.add(dynamicArea, 0, 6, 2, 1);
		// Add the submit button to the eighth row of the grid
		grid.add(submitBtn, 1, 7);
		
		
		// Event for the submit button
		submitBtn.setOnAction(event -> {
			try {
				// Get the values of the inputs
				String type = typeBox.getValue();
				String make = makeField.getText();
				String model = modelField.getText();
				int year = Integer.parseInt(yearField.getText());
				String plate = plateField.getText();
				
				Vehicle newVehicle = null;
				
				// Instantiate subclass based on type selection
				if(type.equals("Car")) {
					int seats = Integer.parseInt(seatsField.getText());
					newVehicle = new Car(make, model, year, seats);
				} else if (type.equals("Minibus")) {
					boolean accessible = accessibleBox.isSelected();
					newVehicle = new Minibus(make, model, year, accessible);
				} else if (type.equals("Pickup Truck")) {
					double cargoSize = Double.parseDouble(cargoField.getText());
					boolean hasTrailer = trailerBox.isSelected();
					newVehicle = new PickupTruck(make, model, year, cargoSize, hasTrailer);
				}
				
				// Set the license plate
				newVehicle.setLicensePlate(plate);
				
				// Add vehicle to system and verify success
				if (rentalSystem.addVehicle(newVehicle)) {
					// Display success 
					displayMessage(AlertType.INFORMATION, "Vehicle added successfully.");
					
					// Clear fields
					makeField.clear();
					modelField.clear();
					yearField.clear();
					plateField.clear();
					seatsField.clear();
					cargoField.clear();
					// Unselect check boxes
					accessibleBox.setSelected(false);
					trailerBox.setSelected(false);
				} else {
					// Display failure
					displayMessage(AlertType.ERROR, "Vehicle could not be added.");
				}
				
			} catch (Exception e) {
				displayMessage(AlertType.ERROR, e.getMessage());
			}
		});

		
		
		return grid;
		
	}
	
	// Main method for running the GUI
	
	public static void main(String[] args) {
		launch(args);
	}

}
