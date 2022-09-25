package com.loserexe;

import com.loserexe.protocol.ServerListPing;
import com.loserexe.utils.Favicon;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class ServerListPingTest {
    public static void main(String[] args) throws IOException {
        ServerListPing serverListPing = new ServerListPing("mc.hypixel.net", 25565, -1);

        System.out.println(serverListPing.getServerListPingJson().getDescription().getText());

        Favicon.faviconToPng(serverListPing.getServerListPingJson().getFavicon(), "C:\\Users\\LoserEXE\\IdeaProjects\\MinecraftPackets\\favicon.png");
    }
}
