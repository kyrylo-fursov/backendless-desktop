package backendless;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class LocationUpdater extends ScheduledService<Void> {

    private BackendlessUser user;

    public LocationUpdater(BackendlessUser user) {
        this.user = user;
        setPeriod(Duration.minutes(1));
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<>() {
            @Override
            protected Void call() {
                if ((Boolean) user.getProperty("trackLocation")) {
                    double[] geoPoint = getCurrentLocation();
                    user.setProperty("latitude", geoPoint[0]);
                    user.setProperty("longitude", geoPoint[1]);
                    Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser updatedUser) {
                            System.out.println("Location updated: " + geoPoint[0] + ", " + geoPoint[1]);
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            System.err.println("Error updating location: " + fault.getMessage());
                        }
                    });
                }
                return null;
            }
        };
    }

    private double[] getCurrentLocation() {
        double latitude = 0.0;
        double longitude = 0.0;
        latitude = Math.random() * 90;
        longitude = Math.random() * 180;
        return new double[]{latitude, longitude};
    }
}