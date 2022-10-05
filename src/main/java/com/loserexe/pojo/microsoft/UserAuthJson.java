package com.loserexe.pojo.microsoft;

import com.google.gson.annotations.SerializedName;

public class UserAuthJson {
    private String error;
    @SerializedName("error_description")
    private String errorDescription;
    
    @SerializedName("token_type")
    private String tokenType;
    private String scope;
    @SerializedName("expires_in")
    private int expiresIn;
    @SerializedName("ext_expires_in")
    private int extExpiresIn;
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("refresh_token")
    private String refreshToken;

    public String getError() {
        return this.error;
    }

    public String getErrorDescription() {
        return this.errorDescription;
    }

    public String getScope() {
        return this.scope;
    }

    public int getExpiresIn() {
        return this.expiresIn;
    }

    public int getExtExpiresIn() {
        return this.extExpiresIn;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getRefreshToken() {
        return this.refreshToken;
    }
}
