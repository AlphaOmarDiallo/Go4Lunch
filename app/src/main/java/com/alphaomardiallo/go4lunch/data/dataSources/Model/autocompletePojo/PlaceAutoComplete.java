package com.alphaomardiallo.go4lunch.data.dataSources.Model.autocompletePojo;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class PlaceAutoComplete{

	@SerializedName("predictions")
	private List<PredictionsItem> predictions;

	@SerializedName("status")
	private String status;

	public List<PredictionsItem> getPredictions(){
		return predictions;
	}

	public String getStatus(){
		return status;
	}
}