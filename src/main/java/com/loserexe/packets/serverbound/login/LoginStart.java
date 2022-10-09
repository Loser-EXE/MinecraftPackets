package com.loserexe.packets.serverbound.login;

import com.loserexe.pojo.minecraft.PlayerCertificatesJson;
import com.loserexe.pojo.minecraft.PlayerProfileJson;
import com.loserexe.utils.MinecraftUUID;
import com.loserexe.utils.VarInt;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginStart {
    private static final Logger logger = LogManager.getLogger(LoginStart.class.getName());
    private static final int PACKET_ID = 0x00;

    public static byte[] getLoginStartPacket(String name) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        DataOutputStream loginStart = new DataOutputStream(buffer);

        UUID uuid = UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
        logger.debug("Gennerated uuid from " + name + ": " + uuid);

        loginStart.writeByte(PACKET_ID);
        logger.debug("Wrote to packet: ID = " + PACKET_ID);
        VarInt.write(loginStart, name.length());
        logger.debug("Wrote to packet: nameLength = " + name.length());
        loginStart.writeBytes(name);
        logger.debug("Wrote to packet: name = " + name);
        loginStart.writeBoolean(false);
        logger.debug("Wrote to packet: HasSigData = " + false);
        loginStart.writeBoolean(true);
        logger.debug("Wrote to packet: HasUUID = " + true);
        MinecraftUUID.writeBytes(loginStart, uuid.toString());
        logger.debug("Wrote to packet: UUID = " + uuid);

        return buffer.toByteArray();
    }

	public static byte[] getLoginStartPacket(String name, PlayerProfileJson playerProfile, PlayerCertificatesJson playerCertificate) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		DataOutputStream loginStart = new DataOutputStream(buffer);

		String[] splitTimestamp = playerCertificate.getExpiresAt().split("T");

		long timestamp = Timestamp.valueOf(splitTimestamp[0] + " " + splitTimestamp[1].split("Z")[0]).getTime();
		String[] publicKeySplit = playerCertificate.getKeyPair().getPublicKey().split("\n");
		String publicKey = "";
		for (int i = 0; i < publicKeySplit.length; i++) {
				if (!publicKeySplit[i].contains("RSA PUBLIC KEY")) {
						publicKey += publicKeySplit[i];
				}
		}
		byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey.getBytes());
		byte[] publicKeySigBytes = Base64.getDecoder().decode(playerCertificate.getPublicKeySignature().getBytes());

		loginStart.write(PACKET_ID);
		logger.debug("Wrote to packet: ID = " + PACKET_ID);
		VarInt.write(loginStart, playerProfile.getUsername().length());
		logger.debug("Wrote to packet: nameLength = " + playerProfile.getUsername().length());
		loginStart.writeBytes(playerProfile.getUsername());
		logger.debug("Wrote to packet: name = " + playerProfile.getUsername());
		loginStart.writeBoolean(true);
		logger.debug("Wrote to packet: HasSigData = " + true);
		loginStart.writeLong(timestamp);
		logger.debug("Wrote to packet: Timestamp = " + timestamp);
		VarInt.write(loginStart, publicKeyBytes.length); 
		logger.debug("Wrote to packet: PublicKeyLength = " + publicKeyBytes.length); 
		loginStart.write(publicKeyBytes);
		logger.debug("Wrote to packet: PublicKey");
		VarInt.write(loginStart, publicKeySigBytes.length);
		logger.debug("Wrote to packet: SignatureLength = " + publicKeySigBytes.length);
		loginStart.write(publicKeySigBytes);
		logger.debug("Wrote to packet: Signature");
		loginStart.writeBoolean(true);
		logger.debug("Wrote to packet: HasUUID = " + true);
		MinecraftUUID.writeBytes(loginStart, playerProfile.getUuid());
		logger.debug("Wrote to packet: UUID = " + playerProfile.getUuid());

		return buffer.toByteArray();
	}
}
