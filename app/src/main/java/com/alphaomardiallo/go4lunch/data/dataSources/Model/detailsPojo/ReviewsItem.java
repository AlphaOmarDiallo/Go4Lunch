package com.alphaomardiallo.go4lunch.data.dataSources.Model.detailsPojo;

import com.google.gson.annotations.SerializedName;

public class ReviewsItem{

	@SerializedName("author_name")
	private String authorName;

	@SerializedName("profile_photo_url")
	private String profilePhotoUrl;

	@SerializedName("author_url")
	private String authorUrl;

	@SerializedName("rating")
	private int rating;

	@SerializedName("language")
	private String language;

	@SerializedName("text")
	private String text;

	@SerializedName("time")
	private int time;

	@SerializedName("relative_time_description")
	private String relativeTimeDescription;

	public String getAuthorName(){
		return authorName;
	}

	public String getProfilePhotoUrl(){
		return profilePhotoUrl;
	}

	public String getAuthorUrl(){
		return authorUrl;
	}

	public int getRating(){
		return rating;
	}

	public String getLanguage(){
		return language;
	}

	public String getText(){
		return text;
	}

	public int getTime(){
		return time;
	}

	public String getRelativeTimeDescription(){
		return relativeTimeDescription;
	}
}