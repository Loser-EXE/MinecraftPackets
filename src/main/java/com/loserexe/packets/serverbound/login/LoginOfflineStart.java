package com.loserexe.packets.serverbound.login;

import com.loserexe.utils.MinecraftUUID;
import com.loserexe.utils.VarInt;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class LoginOfflineStart {
    private static final int PACKET_ID = 0x00;

    public static byte[] getLoginStartPacket(String name) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream loginStart = new DataOutputStream(buffer);

        UUID uuid = UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));

        loginStart.writeByte(PACKET_ID);
        VarInt.write(loginStart, name.length());
        loginStart.writeBytes(name);
        loginStart.writeBoolean(false);
        loginStart.writeBoolean(true);
        MinecraftUUID.writeBytes(loginStart, uuid);

        return buffer.toByteArray();
    }
}
