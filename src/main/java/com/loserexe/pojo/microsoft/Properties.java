package com.loserexe.pojo.microsoft;

import com.google.gson.annotations.SerializedName;

public class Properties {
    @SerializedName("AuthMethod")
    private String authMethod = "RPS";
    @SerializedName("SiteName")
    private String siteName = "user.auth.xboxlive.com";
    @SerializedName("RpsTicket")
    private String rpsTicket;

    public Properties(String accessToken) {
        this.rpsTicket = "d="+accessToken;
    }
}
