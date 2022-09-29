package com.loserexe.packets.serverbound.login;

import com.loserexe.utils.MinecraftUUID;
import com.loserexe.utils.VarInt;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginOfflineStart {
    private static final Logger logger = LogManager.getLogger(LoginOfflineStart.class.getName());
    private static final int PACKET_ID = 0x00;

    public static byte[] getLoginStartPacket(String name) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream loginStart = new DataOutputStream(buffer);

        UUID uuid = UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
        logger.info("Gennerated uuid from " + name + ": " + uuid);

        loginStart.writeByte(PACKET_ID);
        logger.info("Wrote to packet: ID = " + PACKET_ID);
        VarInt.write(loginStart, name.length());
        logger.info("Wrote to packet: nameLength = " + name.length());
        loginStart.writeBytes(name);
        logger.info("Wrote to packet: name = " + name);
        loginStart.writeBoolean(false);
        logger.info("Wrote to packet: HasSigData = " + false);
        loginStart.writeBoolean(true);
        logger.info("Wrote to packet: HasUUID = " + true);
        MinecraftUUID.writeBytes(loginStart, uuid);
        logger.info("Wrote to packet: UUID = " + uuid);

        return buffer.toByteArray();
    }
}
