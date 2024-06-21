package backendless.plane;

import backendless.Place;
import com.backendless.Backendless;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.List;

public class PlacePane extends GridPane {

    private TextField descriptionField = new TextField();
    private TextField latitudeField = new TextField();
    private TextField longitudeField = new TextField();
    private TextField hashtagsField = new TextField();
    private Button addButton = new Button("Add Place");
    private ListView<String> placeListView = new ListView<>();
    private ObservableList<String> placeList = FXCollections.observableArrayList();

    public PlacePane() {
        setHgap(10);
        setVgap(10);

        Label descriptionLabel = new Label("Description:");
        add(descriptionLabel, 0, 0);
        add(descriptionField, 1, 0);

        Label latitudeLabel = new Label("Latitude:");
        add(latitudeLabel, 0, 1);
        add(latitudeField, 1, 1);

        Label longitudeLabel = new Label("Longitude:");
        add(longitudeLabel, 0, 2);
        add(longitudeField, 1, 2);

        Label hashtagsLabel = new Label("Hashtags:");
        add(hashtagsLabel, 0, 3);
        add(hashtagsField, 1, 3);

        add(addButton, 1, 4);

        addButton.setOnAction(e -> addPlace());

        placeListView.setItems(placeList);
        add(new Label("Places:"), 0, 5);
        add(placeListView, 1, 5);

        loadPlaces();
    }

    private void addPlace() {
        String description = descriptionField.getText();
        double latitude = Double.parseDouble(latitudeField.getText());
        double longitude = Double.parseDouble(longitudeField.getText());
        String hashtags = hashtagsField.getText();

        Place place = new Place(description, latitude, longitude, hashtags);

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
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        Backendless.Data.of(Place.class).find(queryBuilder, new AsyncCallback<List<Place>>() {
            @Override
            public void handleResponse(List<Place> response) {
                Platform.runLater(() -> {
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
