package com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaceNearBy {

	@SerializedName("next_page_token")
	private String nextPageToken;

	@SerializedName("html_attributions")
	private List<Object> htmlAttributions;

	@SerializedName("results")
	private List<ResultsItem> results;

	@SerializedName("status")
	private String status;

	public String getNextPageToken(){
		return nextPageToken;
	}

	public List<Object> getHtmlAttributions(){
		return htmlAttributions;
	}

	public List<ResultsItem> getResults(){
		return results;
	}

	public String getStatus(){
		return status;
	}
}