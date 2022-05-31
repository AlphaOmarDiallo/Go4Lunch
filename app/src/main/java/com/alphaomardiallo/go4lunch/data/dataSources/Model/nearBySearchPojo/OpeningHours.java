package com.alphaomardiallo.go4lunch.data.dataSources.Model.nearBySearchPojo;

import com.google.gson.annotations.SerializedName;

public class OpeningHours {

    @SerializedName("open_now")
    private boolean openNow;

    public boolean isOpenNow() {
        return openNow;
    }
}