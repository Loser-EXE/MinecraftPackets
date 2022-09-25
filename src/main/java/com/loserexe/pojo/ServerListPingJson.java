package com.loserexe.pojo;

import com.google.gson.annotations.JsonAdapter;
import com.loserexe.adapters.DescriptionAdapterFactory;
import com.loserexe.pojo.info.Description;
import com.loserexe.pojo.info.Version;
import com.loserexe.pojo.player.Players;

public class ServerListPingJson {
    private Version version;
    private Players players;
    @JsonAdapter(DescriptionAdapterFactory.class)
    private Description description;
    private String favicon;
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

    @Override
    public String toString() {
        return "ServerListPingJson{" +
                "version=" + version +
                ", players=" + players +
                ", description=" + description +
                ", favicon='" + favicon + '\'' +
                ", previewsChat=" + previewsChat +
                ", enforcesSecureChat=" + enforcesSecureChat +
                '}';
    }
}
