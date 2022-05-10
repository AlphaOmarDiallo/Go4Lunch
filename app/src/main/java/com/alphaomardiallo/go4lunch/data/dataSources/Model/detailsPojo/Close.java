package com.alphaomardiallo.go4lunch.data.dataSources.Model.detailsPojo;

import com.google.gson.annotations.SerializedName;

public class Close{

	@SerializedName("time")
	private String time;

	@SerializedName("day")
	private int day;

	public String getTime(){
		return time;
	}

	public int getDay(){
		return day;
	}
}