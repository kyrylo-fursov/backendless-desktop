package backendless;

import com.backendless.BackendlessUser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import com.backendless.Backendless;


public class MainApp extends Application {

    public static final String APP_ID = "A1520ABC-8D52-4A40-B9A8-945270F0F8C2";
    public static final String API_KEY = "07BEA6C1-A0BB-46E7-ABD4-7FD45756E1DD";
    public static final String SERVER_URL = "https://eu-api.backendless.com";

    public static void main(String[] args) {
        Backendless.setUrl(SERVER_URL);
        Backendless.initApp(APP_ID, API_KEY);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("User Management");

        TabPane tabPane = new TabPane();
        Tab registerTab = new Tab("Register");
        Tab loginTab = new Tab("Login");
        Tab fileOperationsTab = new Tab("File Operations");
        Tab userProfileTab = new Tab("User Profile");

        FileOperationsPane fileOperationsPane = new FileOperationsPane();

        registerTab.setContent(new RegisterPane());
        loginTab.setContent(new LoginPane(fileOperationsPane));
        fileOperationsTab.setContent(fileOperationsPane);
        userProfileTab.setContent(new UserProfilePane());

        tabPane.getTabs().addAll(registerTab, loginTab, fileOperationsTab, userProfileTab);

        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        BackendlessUser user = Backendless.UserService.CurrentUser();
        if (user != null && (Boolean) user.getProperty("trackLocation")) {
            LocationUpdater locationUpdater = new LocationUpdater(user);
            locationUpdater.start();
        }
    }
}