package com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo;

import com.google.gson.annotations.SerializedName;

public class Northeast{

	@SerializedName("lng")
	private double lng;

	@SerializedName("lat")
	private double lat;

	public double getLng(){
		return lng;
	}

	public double getLat(){
		return lat;
	}
}