package edu.cnm.deepdive.codebreaker;

import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFxMain extends Application {

  private static final String GAME_RESOURCE_BUNDLE_BASE_NAME = "game";
  private static final String WINDOW_TITLE_KEY = "window_title";
  private static final String MAIN_FXML_RESOURCE = "layouts/main.fxml";

  static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    ResourceBundle bundle = ResourceBundle.getBundle(GAME_RESOURCE_BUNDLE_BASE_NAME);
    ClassLoader classLoader = getClass().getClassLoader();
    stage.setTitle(bundle.getString(WINDOW_TITLE_KEY));
    FXMLLoader fxmlLoader =
        new FXMLLoader(classLoader.getResource(MAIN_FXML_RESOURCE), bundle);
    Scene scene = new Scene(fxmlLoader.load());
    stage.setScene(scene);
    stage.show();
  }

}
