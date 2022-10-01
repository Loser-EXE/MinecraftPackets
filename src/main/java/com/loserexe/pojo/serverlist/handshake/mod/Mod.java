package com.loserexe.pojo.serverlist.handshake.mod;

import com.google.gson.annotations.SerializedName;

public class Mod {
    @SerializedName("modid")
    private String modId;
    private String version;

    public String getModId() {
        return modId;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "Mod{" +
                "modId='" + modId + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
