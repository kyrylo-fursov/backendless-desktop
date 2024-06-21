package backendless;


import com.backendless.BackendlessUser;

public class Place {

    private String description;
    private double latitude;
    private double longitude;
    private String hashtags;
    private BackendlessUser owner;

    public Place() {
    }

    public Place(String description, double latitude, double longitude, String hashtags, BackendlessUser owner) {
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.hashtags = hashtags;
        this.owner = owner;
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

    public BackendlessUser getOwner() {
        return owner;
    }

    public void setOwner(BackendlessUser owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Place{" +
                "description='" + description + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", hashtags='" + hashtags + '\'' +
                ", owner='" + (owner != null ? owner.getObjectId() : "null") + '\'' +
                '}';
    }
}
