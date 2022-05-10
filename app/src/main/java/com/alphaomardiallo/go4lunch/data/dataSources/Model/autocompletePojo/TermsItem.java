package com.alphaomardiallo.go4lunch.data.dataSources.Model.autocompletePojo;

import com.google.gson.annotations.SerializedName;

public class TermsItem{

	@SerializedName("offset")
	private int offset;

	@SerializedName("value")
	private String value;

	public int getOffset(){
		return offset;
	}

	public String getValue(){
		return value;
	}
}