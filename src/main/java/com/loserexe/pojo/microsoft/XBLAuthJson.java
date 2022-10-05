package com.loserexe.pojo.microsoft;

import com.google.gson.annotations.SerializedName;

public class XBLAuthJson {
    @SerializedName("Properties")
    private Properties properties;
    @SerializedName("RelyingParty")
    private String relyingParty = "http://auth.xboxlive.com";
    @SerializedName("TokenType")
    private String tokenType = "JWT";

    @SerializedName("IssueInstant")
    private String issueInstant;
    @SerializedName("NotAfter")
    private String notAfter;
    @SerializedName("Token")
    private String token;
    @SerializedName("DisplayClaims")
    private DisplayClaims displayClaims;

    public XBLAuthJson(String accessToken) {
        this.properties = new Properties(accessToken);
    }

    public String getIssueInstant() {
        return this.issueInstant;
    }

    public String getNotAfter() {
        return this.notAfter;
    }

    public String getToken() {
        return this.token;
    }

    public DisplayClaims getDisplayClaims() {
        return this.displayClaims;
    }
}
