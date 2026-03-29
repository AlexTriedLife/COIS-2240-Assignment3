import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class RentalSystemGUI extends Application {

	private RentalSystem rentalSystem;
	// The window layout
	private BorderPane root;
	
	@Override 
	public void start(Stage primaryStage) {
		try {
			// Get the single Rental System instance
			rentalSystem = RentalSystem.getInstance();
			
			// GUI setup
			root = new BorderPane();
			
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
	
	// Main method for running the GUI
	
	public static void main(String[] args) {
		launch(args);
	}

}
