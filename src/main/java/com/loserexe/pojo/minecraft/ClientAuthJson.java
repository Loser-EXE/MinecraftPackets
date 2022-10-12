package com.loserexe.pojo.minecraft;

public class ClientAuthJson {
    private String error;

    private String accessToken;
    private String selectedProfile;
    private String serverId;

    public ClientAuthJson(String accessToken, String uuid, String serverHash) {
        this.accessToken = accessToken;
        this.selectedProfile = uuid;
        this.serverId = serverHash;
    }

    public String getError() {
        return this.error;
    }
}
