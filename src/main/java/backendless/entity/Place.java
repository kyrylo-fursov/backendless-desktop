package backendless.entity;


import com.backendless.BackendlessUser;

public class Place {

    private String description;
    private double latitude;
    private double longitude;
    private String hashtags;
    private String ownerId;

    public Place() {
    }

    public Place(String description, double latitude, double longitude, String hashtags, String ownerId) {
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.hashtags = hashtags;
        this.ownerId = ownerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getHashtags() {
        return hashtags;
    }

    public void setHashtags(String hashtags) {
        this.hashtags = hashtags;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String toString() {
        return "Place{" +
                "description='" + description + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", hashtags='" + hashtags + '\'' +
                ", ownerId='" + ownerId + '\'' +
                '}';
    }
}
