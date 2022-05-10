package com.alphaomardiallo.go4lunch.data.dataSources.Model.autocompletePojo;

import com.google.gson.annotations.SerializedName;

public class MatchedSubstringsItem{

	@SerializedName("offset")
	private int offset;

	@SerializedName("length")
	private int length;

	public int getOffset(){
		return offset;
	}

	public int getLength(){
		return length;
	}
}