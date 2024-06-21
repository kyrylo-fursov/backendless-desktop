package backendless;

import backendless.plane.FileOperationsPane;
import backendless.plane.LoginPane;
import backendless.plane.PlacePane;
import backendless.plane.RegisterPane;
import backendless.plane.UserProfilePane;
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

    private FileOperationsPane fileOperationsPane;
    private PlacePane placePane;
    private UserProfilePane userProfilePane;

    public static void main(String[] args) {
        Backendless.setUrl(SERVER_URL);
        Backendless.initApp(APP_ID, API_KEY);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("User Management");

        fileOperationsPane = new FileOperationsPane();
        placePane = new PlacePane();
        userProfilePane = new UserProfilePane();

        TabPane tabPane = new TabPane();
        Tab registerTab = new Tab("Register");
        Tab loginTab = new Tab("Login");
        Tab fileOperationsTab = new Tab("File Operations");
        Tab userProfileTab = new Tab("User Profile");
        Tab placeTab = new Tab("Places");

        registerTab.setContent(new RegisterPane());
        loginTab.setContent(new LoginPane(fileOperationsPane, placePane, userProfilePane));
        fileOperationsTab.setContent(fileOperationsPane);
        userProfileTab.setContent(userProfilePane);
        placeTab.setContent(placePane);

        tabPane.getTabs().addAll(registerTab, loginTab, fileOperationsTab, userProfileTab, placeTab);

        Scene scene = new Scene(tabPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        BackendlessUser user = Backendless.UserService.CurrentUser();
        if (user != null) {
            userProfilePane.setLoggedInUser(user);
            fileOperationsPane.setLoggedInUser(user);
            placePane.setLoggedInUser(user);
        }
    }
}