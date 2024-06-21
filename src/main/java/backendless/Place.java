package backendless;


public class Place {

    private String description;
    private double latitude;
    private double longitude;
    private String hashtags;

    public Place() {
    }

    public Place(String description, double latitude, double longitude, String hashtags) {
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.hashtags = hashtags;
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

    @Override
    public String toString() {
        return "Place{" +
                "description='" + description + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", hashtags='" + hashtags + '\'' +
                '}';
    }
}
