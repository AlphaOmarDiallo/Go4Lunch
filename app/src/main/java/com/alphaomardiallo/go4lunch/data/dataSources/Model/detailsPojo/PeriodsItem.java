package com.alphaomardiallo.go4lunch.data.dataSources.Model.detailsPojo;

import com.google.gson.annotations.SerializedName;

public class PeriodsItem {

    @SerializedName("close")
    private Close close;

    @SerializedName("open")
    private Open open;

    public Close getClose() {
        return close;
    }

    public Open getOpen() {
        return open;
    }
}