package com.loserexe.pojo.microsoft;

import com.google.gson.annotations.SerializedName;

public class AuthUserJson {
    private String error;
    @SerializedName("error_description")
    private String errorDescription;
    @SerializedName("token_type")
    private String tokenType;
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("id_token")
    private String idToken;

    public String getError() {
        return this.error;
    }

    public String getErrorDescription() {
        return this.errorDescription;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getIdToken() {
        return this.idToken;
    }

    public String getTokenType() {
        return this.tokenType;
    }
}
