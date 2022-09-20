package com.loserexe;

import com.loserexe.packets.slp.ServerListPing;

import java.io.IOException;

public class ServerListPingTest {
    public static void main(String[] args) throws IOException {
        ServerListPing serverListPing = new ServerListPing("mc.hypixel.net", 25565, -1);
    }
}
