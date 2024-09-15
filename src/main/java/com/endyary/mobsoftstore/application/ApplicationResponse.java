package com.endyary.mobsoftstore.application;

/**
 * Application DTO response record
 */
public record ApplicationResponse(long id, String name, String category, String description,
                                  String pictureSmall, String pictureBig, int downloadCount, String rating,
                                  int noOfRatings) {
}
