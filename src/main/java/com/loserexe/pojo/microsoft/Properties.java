package com.loserexe.pojo.microsoft;

import com.google.gson.annotations.SerializedName;

public class Properties {
    @SerializedName("AuthMethod")
    private String authMethod;
    @SerializedName("SiteName")
    private String siteName;
    @SerializedName("RpsTicket")
    private String rpsTicket;

    @SerializedName("SandboxId")
    private String sandboxId;
    @SerializedName("UserTokens")
    private String[] userTokens = new String[1];

    public Properties(String token, String service) {
        if (service.equals("AuthXBL")) {
            this.authMethod = "RPS";
            this.siteName = "user.auth.xboxlive.com";
            this.rpsTicket = "d="+token;
        } else {
            this.sandboxId = "RETAIL";
            this.userTokens[0] = token;
        }
    }
}
