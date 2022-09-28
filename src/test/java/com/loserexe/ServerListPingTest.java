package com.loserexe;

import com.google.gson.Gson;
import com.loserexe.pojo.serverlist.mod.Mod;
import com.loserexe.protocol.ServerList;
import com.loserexe.utils.Favicon;

import java.io.IOException;
import java.util.List;

public class ServerListPingTest {
    public static void main(String[] args) throws IOException {
        ServerList serverList = new ServerList("mc.loserexe.com", 25565, -1);

        Gson gson = new Gson();
        System.out.println(gson.toJson(serverList.getServerListJson()));

        try {
            Favicon.faviconToPng(serverList.getServerListJson().getFavicon(), "C:\\Users\\LoserEXE\\IdeaProjects\\MinecraftPackets\\favicon.png");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
