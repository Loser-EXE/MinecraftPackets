package com.loserexe;

import com.google.gson.Gson;
import com.loserexe.protocol.ServerList;
import com.loserexe.utils.Favicon;

import java.io.IOException;

public class ServerListPingTest {
    public static void main(String[] args) throws IOException {
        ServerList serverList = new ServerList("play.rlcrafters.ca", 25565, -1);

        Gson gson = new Gson();
        System.out.println(gson.toJson(serverList.getServerListPingJson()));
        try {
            Favicon.faviconToPng(serverList.getServerListPingJson().getFavicon(), "C:\\Users\\LoserEXE\\IdeaProjects\\MinecraftPackets\\favicon.png");
        } catch (Exception e) {
            throw new IOException("Error writing favicon: " + e.getMessage());
        }
    }
}
