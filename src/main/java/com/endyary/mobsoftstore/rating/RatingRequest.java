package com.endyary.mobsoftstore.rating;

/**
 * Application rating DTO request class
 */
public class RatingRequest {
    private long appId;
    private int rating;

    public RatingRequest(long appId, int rating) {
        this.appId = appId;
        this.rating = rating;
    }

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
