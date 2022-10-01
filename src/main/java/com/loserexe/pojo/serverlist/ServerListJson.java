package com.loserexe.pojo.serverlist;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.loserexe.adapters.factory.DescriptionAdapterFactory;
import com.loserexe.pojo.serverlist.handshake.info.Description;
import com.loserexe.pojo.serverlist.handshake.info.ModInfo;
import com.loserexe.pojo.serverlist.handshake.info.Version;
import com.loserexe.pojo.serverlist.handshake.player.Players;

public class ServerListJson {
    private Version version;
    private Players players;
    @JsonAdapter(DescriptionAdapterFactory.class)
    private Description description;
    private String favicon;
    @SerializedName("modinfo")
    private ModInfo modInfo;
    private boolean previewsChat;
    private boolean enforcesSecureChat;

    public Version getVersion() {
        return version;
    }

    public Players getPlayers() {
        return players;
    }

    public Description getDescription() {
        return description;
    }

    public String getFavicon() {
        return favicon;
    }

    public boolean isPreviewsChat() {
        return previewsChat;
    }

    public boolean isEnforcesSecureChat() {
        return enforcesSecureChat;
    }

    public ModInfo getModInfo() {
        return modInfo;
    }

    @Override
    public String toString() {
        return "ServerListPingJson{" +
                "version=" + version +
                ", players=" + players +
                ", description=" + description +
                ", favicon='" + favicon + '\'' +
                ", modinfo='" + modInfo + "\'" +
                ", previewsChat=" + previewsChat +
                ", enforcesSecureChat=" + enforcesSecureChat +
                '}';
    }
}
