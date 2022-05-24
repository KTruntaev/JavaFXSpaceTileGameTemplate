module com.example.spacetemplatecredition {
	requires javafx.controls;
	requires javafx.fxml;


	opens com.template.space to javafx.fxml;
	exports com.template.space;
}