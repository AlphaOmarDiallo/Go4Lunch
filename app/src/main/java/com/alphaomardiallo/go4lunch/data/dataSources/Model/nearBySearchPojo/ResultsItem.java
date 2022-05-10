package com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ResultsItem{

	@SerializedName("types")
	private List<String> types;

	@SerializedName("business_status")
	private String businessStatus;

	@SerializedName("icon")
	private String icon;

	@SerializedName("rating")
	private double rating;

	@SerializedName("icon_background_color")
	private String iconBackgroundColor;

	@SerializedName("photos")
	private List<PhotosItem> photos;

	@SerializedName("reference")
	private String reference;

	@SerializedName("user_ratings_total")
	private int userRatingsTotal;

	@SerializedName("price_level")
	private int priceLevel;

	@SerializedName("scope")
	private String scope;

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

	@SerializedName("plus_code")
	private PlusCode plusCode;

	@SerializedName("place_id")
	private String placeId;

	@SerializedName("permanently_closed")
	private boolean permanentlyClosed;

	public List<String> getTypes(){
		return types;
	}

	public String getBusinessStatus(){
		return businessStatus;
	}

	public String getIcon(){
		return icon;
	}

	public double getRating(){
		return rating;
	}

	public String getIconBackgroundColor(){
		return iconBackgroundColor;
	}

	public List<PhotosItem> getPhotos(){
		return photos;
	}

	public String getReference(){
		return reference;
	}

	public int getUserRatingsTotal(){
		return userRatingsTotal;
	}

	public int getPriceLevel(){
		return priceLevel;
	}

	public String getScope(){
		return scope;
	}

	public String getName(){
		return name;
	}

	public OpeningHours getOpeningHours(){
		return openingHours;
	}

	public Geometry getGeometry(){
		return geometry;
	}

	public String getIconMaskBaseUri(){
		return iconMaskBaseUri;
	}

	public String getVicinity(){
		return vicinity;
	}

	public PlusCode getPlusCode(){
		return plusCode;
	}

	public String getPlaceId(){
		return placeId;
	}

	public boolean isPermanentlyClosed(){
		return permanentlyClosed;
	}
}