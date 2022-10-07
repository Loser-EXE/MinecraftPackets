package com.loserexe.pojo.minecraft;

import com.google.gson.annotations.SerializedName;

public class AuthMinecraft {
    @SerializedName("identityToken")
    private String identityToken;

    private String username;
    private String[] roles;
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("token_type")
    private String tokenType;
    @SerializedName("exipres_in")
    private int expiresIn;

    public AuthMinecraft(String identityToken) {
        this.identityToken = identityToken;
    }

    public String getUsername() {
        return this.username;
    }

    public String[] getRoles() {
        return this.roles;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getTokenType() {
        return this.tokenType;
    }

    public int getExpiresIn() {
        return this.expiresIn;
    }
}
