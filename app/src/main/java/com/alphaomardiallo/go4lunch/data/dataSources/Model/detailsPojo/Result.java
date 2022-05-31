package com.alphaomardiallo.go4lunch.data.dataSources.Model.detailsPojo;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("utc_offset")
    private int utcOffset;

    @SerializedName("formatted_address")
    private String formattedAddress;

    @SerializedName("types")
    private List<String> types;

    @SerializedName("website")
    private String website;

    @SerializedName("business_status")
    private String businessStatus;

    @SerializedName("icon")
    private String icon;

    @SerializedName("rating")
    private double rating;

    @SerializedName("icon_background_color")
    private String iconBackgroundColor;

    @SerializedName("address_components")
    private List<AddressComponentsItem> addressComponents;

    @SerializedName("photos")
    private List<PhotosItem> photos;

    @SerializedName("url")
    private String url;

    @SerializedName("reference")
    private String reference;

    @SerializedName("user_ratings_total")
    private int userRatingsTotal;

    @SerializedName("reviews")
    private List<ReviewsItem> reviews;

    @SerializedName("name")
    private String name;

    @SerializedName("opening_hours")
    private OpeningHours openingHours;

    @SerializedName("geometry")
    private Geometry geometry;

    @SerializedName("icon_mask_base_uri")
    private String iconMaskBaseUri;

    @SerializedName("vicinity")
    private String vicinity;

    @SerializedName("adr_address")
    private String adrAddress;

    @SerializedName("plus_code")
    private PlusCode plusCode;

    @SerializedName("formatted_phone_number")
    private String formattedPhoneNumber;

    @SerializedName("international_phone_number")
    private String internationalPhoneNumber;

    @SerializedName("place_id")
    private String placeId;

    public int getUtcOffset() {
        return utcOffset;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public List<String> getTypes() {
        return types;
    }

    public String getWebsite() {
        return website;
    }

    public String getBusinessStatus() {
        return businessStatus;
    }

    public String getIcon() {
        return icon;
    }

    public double getRating() {
        return rating;
    }

    public String getIconBackgroundColor() {
        return iconBackgroundColor;
    }

    public List<AddressComponentsItem> getAddressComponents() {
        return addressComponents;
    }

    public List<PhotosItem> getPhotos() {
        return photos;
    }

    public String getUrl() {
        return url;
    }

    public String getReference() {
        return reference;
    }

    public int getUserRatingsTotal() {
        return userRatingsTotal;
    }

    public List<ReviewsItem> getReviews() {
        return reviews;
    }

    public String getName() {
        return name;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public String getIconMaskBaseUri() {
        return iconMaskBaseUri;
    }

    public String getVicinity() {
        return vicinity;
    }

    public String getAdrAddress() {
        return adrAddress;
    }

    public PlusCode getPlusCode() {
        return plusCode;
    }

    public String getFormattedPhoneNumber() {
        return formattedPhoneNumber;
    }

    public String getInternationalPhoneNumber() {
        return internationalPhoneNumber;
    }

    public String getPlaceId() {
        return placeId;
    }
}