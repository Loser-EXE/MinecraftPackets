package com.loserexe.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class Favicon {
    public static void faviconToPng(String favicon, String path) throws IOException {
        if (favicon == null) throw new IOException("Server has a blank favicon");

        String faviconString = favicon.split(",", 2)[1];

        Base64.Decoder decoder = Base64.getDecoder();
        byte[] imageBytes = decoder.decode(faviconString);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        byteArrayInputStream.close();

        File output = new File(path);
        ImageIO.write(image, "png", output);
    }
}
