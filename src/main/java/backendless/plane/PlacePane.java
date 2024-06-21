package backendless.plane;

import backendless.Place;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import javafx.scene.layout.HBox;

import java.util.List;

public class PlacePane extends GridPane {

    private Label loggedInLabel = new Label("Logged in as: Not logged in");
    private TextField descriptionField = new TextField();
    private TextField latitudeField = new TextField();
    private TextField longitudeField = new TextField();
    private TextField hashtagsField = new TextField();
    private Button addButton = new Button("Add Place");
    private Button updateButton = new Button("Update Places");
    private ListView<String> placeListView = new ListView<>();
    private ObservableList<String> placeList = FXCollections.observableArrayList();
    private BackendlessUser currentUser;

    public PlacePane() {
        setHgap(10);
        setVgap(10);
        setPadding(new Insets(10, 10, 10, 10));

        loggedInLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        add(loggedInLabel, 0, 0, 2, 1);

        Label descriptionLabel = new Label("Description:");
        add(descriptionLabel, 0, 1);
        descriptionField.setPromptText("Enter description");
        add(descriptionField, 1, 1);

        Label latitudeLabel = new Label("Latitude:");
        add(latitudeLabel, 0, 2);
        latitudeField.setPromptText("Enter latitude");
        add(latitudeField, 1, 2);

        Label longitudeLabel = new Label("Longitude:");
        add(longitudeLabel, 0, 3);
        longitudeField.setPromptText("Enter longitude");
        add(longitudeField, 1, 3);

        Label hashtagsLabel = new Label("Hashtags:");
        add(hashtagsLabel, 0, 4);
        hashtagsField.setPromptText("Enter hashtags");
        add(hashtagsField, 1, 4);

        addButton.setOnAction(e -> addPlace());
        HBox buttonBox = new HBox(10, addButton, updateButton);
        add(buttonBox, 1, 5);

        placeListView.setItems(placeList);
        placeListView.setPrefHeight(200);
        add(new Label("Places:"), 0, 6);
        add(placeListView, 1, 6);

        updateButton.setOnAction(e -> loadPlaces());

        disableInputs();
    }

    public void setLoggedInUser(BackendlessUser user) {
        this.currentUser = user;
        if (user != null) {
            Platform.runLater(this::updateUIForLoggedInUser);
        }
    }

    private void updateUIForLoggedInUser() {
        loggedInLabel.setText("Logged in as: " + (currentUser != null ? currentUser.getEmail() : "Not logged in"));
        if (currentUser != null) {
            enableInputs();
            loadPlaces();
        } else {
            disableInputs();
        }
    }

    private void disableInputs() {
        descriptionField.setDisable(true);
        latitudeField.setDisable(true);
        longitudeField.setDisable(true);
        hashtagsField.setDisable(true);
        addButton.setDisable(true);
        updateButton.setDisable(true);
    }

    private void enableInputs() {
        descriptionField.setDisable(false);
        latitudeField.setDisable(false);
        longitudeField.setDisable(false);
        hashtagsField.setDisable(false);
        addButton.setDisable(false);
        updateButton.setDisable(false);
    }

    private void addPlace() {
        String description = descriptionField.getText();
        double latitude = Double.parseDouble(latitudeField.getText());
        double longitude = Double.parseDouble(longitudeField.getText());
        String hashtags = hashtagsField.getText();

        Place place = new Place(description, latitude, longitude, hashtags, currentUser);

        Backendless.Data.of(Place.class).save(place, new AsyncCallback<Place>() {
            @Override
            public void handleResponse(Place response) {
                Platform.runLater(() -> {
                    showAlert("Place added successfully.");
                    placeList.add(response.toString());
                });
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Platform.runLater(() -> showAlert("Error adding place: " + fault.getMessage()));
            }
        });
    }

    private void loadPlaces() {
        if (currentUser == null) {
            return;
        }

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause("owner.objectId = '" + currentUser.getObjectId() + "'");
        Backendless.Data.of(Place.class).find(queryBuilder, new AsyncCallback<List<Place>>() {
            @Override
            public void handleResponse(List<Place> response) {
                Platform.runLater(() -> {
                    placeList.clear();
                    for (Place place : response) {
                        placeList.add(place.toString());
                    }
                });
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Platform.runLater(() -> showAlert("Error loading places: " + fault.getMessage()));
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}