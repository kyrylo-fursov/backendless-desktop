package backendless.plane;

import com.backendless.Backendless;
import com.backendless.persistence.DataQueryBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.BackendlessUser;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;


public class FriendsPane extends GridPane {

    private Label loggedInLabel = new Label("Logged in as: Not logged in");
    private TextField searchField = new TextField();
    private Button addButton = new Button("Add Friend");
    private Button deleteButton = new Button("Delete Friend");
    private ListView<String> friendsListView = new ListView<>();
    private ObservableList<String> friendsList = FXCollections.observableArrayList();
    private ListView<String> friendRequestsListView = new ListView<>();
    private ObservableList<String> friendRequestsList = FXCollections.observableArrayList();
    private BackendlessUser currentUser;
    private Gson gson = new Gson();
    private Type listType = new TypeToken<List<String>>() {}.getType();

    public FriendsPane() {
        setHgap(10);
        setVgap(10);
        setPadding(new Insets(10, 10, 10, 10));

        loggedInLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        add(loggedInLabel, 0, 0, 2, 1);

        Label searchLabel = new Label("Find User:");
        add(searchLabel, 0, 1);
        add(searchField, 1, 1);

        HBox buttonBox = new HBox(10, addButton, deleteButton);
        add(buttonBox, 1, 2);

        friendsListView.setItems(friendsList);
        friendsListView.setPrefHeight(200);
        add(new Label("Friends:"), 0, 3);
        add(friendsListView, 1, 3);

        friendRequestsListView.setItems(friendRequestsList);
        friendRequestsListView.setPrefHeight(200);
        add(new Label("Friend Requests:"), 0, 4);
        add(friendRequestsListView, 1, 4);

        Button acceptButton = new Button("Accept");
        Button declineButton = new Button("Decline");
        HBox requestButtonsBox = new HBox(10, acceptButton, declineButton);
        add(requestButtonsBox, 1, 5);

        addButton.setOnAction(e -> addFriend());
        deleteButton.setOnAction(e -> deleteFriend());
        acceptButton.setOnAction(e -> {
            String selectedRequest = friendRequestsListView.getSelectionModel().getSelectedItem();
            if (selectedRequest != null) {
                acceptFriendRequest(selectedRequest);
            } else {
                showAlert("Please select a friend request to accept.");
            }
        });
        declineButton.setOnAction(e -> {
            String selectedRequest = friendRequestsListView.getSelectionModel().getSelectedItem();
            if (selectedRequest != null) {
                declineFriendRequest(selectedRequest);
            } else {
                showAlert("Please select a friend request to decline.");
            }
        });

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
            loadFriends();
            loadFriendRequests();
        } else {
            disableInputs();
        }
    }

    private void disableInputs() {
        searchField.setDisable(true);
        addButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    private void enableInputs() {
        searchField.setDisable(false);
        addButton.setDisable(false);
        deleteButton.setDisable(false);
    }

    private void addFriend() {
        String email = searchField.getText();
        if (email.isEmpty()) {
            showAlert("Email cannot be empty.");
            return;
        }

        String whereClause = "email = '" + email + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);

        Backendless.Data.of(BackendlessUser.class).find(queryBuilder, new AsyncCallback<List<BackendlessUser>>() {
            @Override
            public void handleResponse(List<BackendlessUser> users) {
                if (!users.isEmpty()) {
                    BackendlessUser userToAdd = users.get(0);
                    sendFriendRequest(userToAdd);
                } else {
                    Platform.runLater(() -> showAlert("User not found."));
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Platform.runLater(() -> showAlert("Error finding user: " + fault.getMessage()));
            }
        });
    }

    private void deleteFriend() {
        String selectedFriend = friendsListView.getSelectionModel().getSelectedItem();
        if (selectedFriend == null) {
            showAlert("Please select a friend to delete.");
            return;
        }

        List<String> friends = getListFromJson(currentUser.getProperty("friends"));
        if (friends != null) {
            friends.remove(selectedFriend);
            currentUser.setProperty("friends", gson.toJson(friends));
            Backendless.UserService.update(currentUser, new AsyncCallback<BackendlessUser>() {
                @Override
                public void handleResponse(BackendlessUser updatedUser) {
                    Platform.runLater(() -> {
                        friendsList.remove(selectedFriend);
                        showAlert("Friend deleted successfully.");
                    });
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Platform.runLater(() -> showAlert("Error deleting friend: " + fault.getMessage()));
                }
            });
        }
    }

    private void loadFriends() {
        List<String> friends = getListFromJson(currentUser.getProperty("friends"));
        if (friends != null) {
            friendsList.setAll(friends);
        }
    }

    private void loadFriendRequests() {
        List<String> friendRequests = getListFromJson(currentUser.getProperty("friendRequests"));
        if (friendRequests != null) {
            friendRequestsList.setAll(friendRequests);
        }
    }

    private void sendFriendRequest(BackendlessUser userToAdd) {
        List<String> friendRequests = getListFromJson(userToAdd.getProperty("friendRequests"));
        if (friendRequests == null) {
            friendRequests = new ArrayList<>();
        }
        friendRequests.add(currentUser.getEmail());
        userToAdd.setProperty("friendRequests", gson.toJson(friendRequests));

        Backendless.UserService.update(userToAdd, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {
                Platform.runLater(() -> showAlert("Friend request sent."));
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Platform.runLater(() -> showAlert("Error sending friend request: " + fault.getMessage()));
            }
        });
    }

    private void acceptFriendRequest(String email) {
        String whereClause = "email = '" + email + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);

        Backendless.Data.of(BackendlessUser.class).find(queryBuilder, new AsyncCallback<List<BackendlessUser>>() {
            @Override
            public void handleResponse(List<BackendlessUser> users) {
                if (!users.isEmpty()) {
                    BackendlessUser userToAccept = users.get(0);

                    List<String> friends = getListFromJson(currentUser.getProperty("friends"));
                    if (friends == null) {
                        friends = new ArrayList<>();
                    }
                    friends.add(userToAccept.getEmail());
                    currentUser.setProperty("friends", gson.toJson(friends));

                    Backendless.UserService.update(currentUser, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser updatedUser) {
                            List<String> userToAcceptFriends = getListFromJson(userToAccept.getProperty("friends"));
                            if (userToAcceptFriends == null) {
                                userToAcceptFriends = new ArrayList<>();
                            }
                            userToAcceptFriends.add(currentUser.getEmail());
                            userToAccept.setProperty("friends", gson.toJson(userToAcceptFriends));

                            Backendless.UserService.update(userToAccept, new AsyncCallback<BackendlessUser>() {
                                @Override
                                public void handleResponse(BackendlessUser updatedUser) {
                                    Platform.runLater(() -> {
                                        showAlert("Friend request accepted.");
                                        loadFriends();
                                        loadFriendRequests();
                                    });
                                }

                                @Override
                                public void handleFault(BackendlessFault fault) {
                                    Platform.runLater(() -> showAlert("Error accepting friend request: " + fault.getMessage()));
                                }
                            });
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Platform.runLater(() -> showAlert("Error updating friends list: " + fault.getMessage()));
                        }
                    });
                } else {
                    Platform.runLater(() -> showAlert("User not found."));
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Platform.runLater(() -> showAlert("Error finding user: " + fault.getMessage()));
            }
        });
    }

    private void declineFriendRequest(String email) {
        List<String> friendRequests = getListFromJson(currentUser.getProperty("friendRequests"));
        if (friendRequests != null) {
            friendRequests.remove(email);
            currentUser.setProperty("friendRequests", gson.toJson(friendRequests));

            Backendless.UserService.update(currentUser, new AsyncCallback<BackendlessUser>() {
                @Override
                public void handleResponse(BackendlessUser updatedUser) {
                    Platform.runLater(() -> {
                        showAlert("Friend request declined.");
                        loadFriendRequests();
                    });
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Platform.runLater(() -> showAlert("Error declining friend request: " + fault.getMessage()));
                }
            });
        }
    }

    private List<String> getListFromJson(Object json) {
        if (json == null) {
            return new ArrayList<>();
        }
        try {
            if (json instanceof List) {
                return (List<String>) json;
            }
            String jsonString = gson.toJson(json);
            // Validate JSON
            if (!jsonString.startsWith("[") || !jsonString.endsWith("]")) {
                showAlert("Invalid JSON format: " + jsonString);
                return new ArrayList<>();
            }
            return gson.fromJson(jsonString, listType);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            showAlert("Error parsing JSON: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}