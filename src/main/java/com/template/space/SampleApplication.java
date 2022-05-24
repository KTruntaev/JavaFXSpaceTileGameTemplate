package com.template.space;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SampleApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SampleApplication.class.getResource("sample-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        
        stage.setTitle("Space Tile Game");
        stage.setScene(scene);
        
        
        SampleController controller = fxmlLoader.getController();
        controller.setup();

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}