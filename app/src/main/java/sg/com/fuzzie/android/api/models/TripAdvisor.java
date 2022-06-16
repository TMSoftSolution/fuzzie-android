package sg.com.fuzzie.android.api.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by faruq on 06/02/17.
 */

public class TripAdvisor {

    @SerializedName("reviews_count")
    private int reviewsCount;

    @SerializedName("rating")
    private double rating;

    @SerializedName("link")
    private String link;

    public Integer getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(int reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
