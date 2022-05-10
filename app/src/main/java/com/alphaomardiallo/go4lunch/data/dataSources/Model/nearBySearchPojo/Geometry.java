package com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo;

import com.google.gson.annotations.SerializedName;

public class Geometry{

	@SerializedName("viewport")
	private Viewport viewport;

	@SerializedName("location")
	private Location location;

	public Viewport getViewport(){
		return viewport;
	}

	public Location getLocation(){
		return location;
	}
}