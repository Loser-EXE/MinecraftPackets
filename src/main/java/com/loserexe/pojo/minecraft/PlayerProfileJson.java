package com.loserexe.pojo.minecraft;

import com.google.gson.annotations.SerializedName;

public class PlayerProfileJson {
    @SerializedName("id")
    private String uuid;
    @SerializedName("name")
    private String username;
    private Skin[] skins;
    private Cape[] capes;

    public String getUuid() {
        return this.uuid;
    }

    public String getUsername() {
        return this.username;
    }

    public Skin[] getSkins() {
        return this.skins;
    }

    public Cape[] getCapes() {
        return this.capes;
    }
}
