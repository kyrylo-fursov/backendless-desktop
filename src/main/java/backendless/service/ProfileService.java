package backendless.service;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import javafx.application.Platform;

import static backendless.service.FileOperations.showAlert;
import static backendless.MainApp.SERVER_URL;

public class ProfileService {
    public static void updateProfilePicture(String userId, String filePath) {
        String destinationPath = "/avatars/" + userId + ".jpg";
        Backendless.Files.copyFile(filePath, destinationPath, new AsyncCallback<String>() {
            @Override
            public void handleResponse(String response) {
                // Update the user profile with the avatar URL
                String avatarUrl = SERVER_URL + destinationPath;
                BackendlessUser user = Backendless.UserService.CurrentUser();
                user.setProperty("avatar", avatarUrl);
                Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser updatedUser) {
                        Platform.runLater(() -> showAlert("Profile picture updated successfully."));
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Platform.runLater(() -> showAlert("Error updating profile: " + fault.getMessage()));
                    }
                });
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Platform.runLater(() -> showAlert("Error copying file: " + fault.getMessage()));
            }
        });
    }
}
