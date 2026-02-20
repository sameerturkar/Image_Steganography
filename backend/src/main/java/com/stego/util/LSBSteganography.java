package com.stego.util;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class LSBSteganography {

    private static final String DELIMITER = "##END##";

    /**
     * Encodes a secret message into an image using LSB technique.
     * Modifies the least significant bit of each color channel (R,G,B) of each pixel.
     *
     * @param inputImagePath  Path to the original image
     * @param outputImagePath Path to save the encoded image
     * @param message         The secret message to hide
     * @throws IOException          if image read/write fails
     * @throws IllegalArgumentException if message is too long for the image
     */
    public void encodeMessage(String inputImagePath, String outputImagePath, String message) throws IOException {
        BufferedImage image = ImageIO.read(new File(inputImagePath));
        if (image == null) {
            throw new IOException("Could not read image: " + inputImagePath);
        }

        String fullMessage = message + DELIMITER;
        byte[] messageBytes = fullMessage.getBytes("UTF-8");

        int width = image.getWidth();
        int height = image.getHeight();
        int maxBytes = (width * height * 3) / 8;

        if (messageBytes.length > maxBytes) {
            throw new IllegalArgumentException(
                "Message too long! Max " + maxBytes + " bytes for this image. Message is " + messageBytes.length + " bytes.");
        }

        int bitIndex = 0;
        int totalBits = messageBytes.length * 8;
        boolean done = false;

        outerLoop:
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);

                int alpha = (pixel >> 24) & 0xFF;
                int red   = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8)  & 0xFF;
                int blue  =  pixel        & 0xFF;

                // Modify R channel
                if (bitIndex < totalBits) {
                    int bit = getBit(messageBytes, bitIndex++);
                    red = (red & ~1) | bit;
                }
                // Modify G channel
                if (bitIndex < totalBits) {
                    int bit = getBit(messageBytes, bitIndex++);
                    green = (green & ~1) | bit;
                }
                // Modify B channel
                if (bitIndex < totalBits) {
                    int bit = getBit(messageBytes, bitIndex++);
                    blue = (blue & ~1) | bit;
                }

                int newPixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                image.setRGB(x, y, newPixel);

                if (bitIndex >= totalBits) {
                    done = true;
                    break outerLoop;
                }
            }
        }

        if (!done && bitIndex < totalBits) {
            throw new IOException("Failed to encode entire message into image.");
        }

        // Save as PNG to preserve quality (no lossy compression)
        String format = "PNG";
        File outputFile = new File(outputImagePath);
        outputFile.getParentFile().mkdirs();
        ImageIO.write(image, format, outputFile);
    }

    /**
     * Decodes a hidden message from an LSB-encoded image.
     *
     * @param encodedImagePath Path to the encoded image
     * @return The decoded secret message
     * @throws IOException if image cannot be read
     */
    public String decodeMessage(String encodedImagePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(encodedImagePath));
        if (image == null) {
            throw new IOException("Could not read encoded image: " + encodedImagePath);
        }

        int width = image.getWidth();
        int height = image.getHeight();

        StringBuilder binaryData = new StringBuilder();

        outer:
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);

                int red   = (pixel >> 16) & 0xFF;
                int green = (pixel >> 8)  & 0xFF;
                int blue  =  pixel        & 0xFF;

                binaryData.append(red   & 1);
                binaryData.append(green & 1);
                binaryData.append(blue  & 1);

                // Check every 8 bits if we have found the delimiter
                if (binaryData.length() % 8 == 0 && binaryData.length() >= DELIMITER.length() * 8) {
                    String currentText = bitsToString(binaryData.toString());
                    if (currentText.contains(DELIMITER)) {
                        break outer;
                    }
                }
            }
        }

        String fullText = bitsToString(binaryData.toString());
        int delimIndex = fullText.indexOf(DELIMITER);
        if (delimIndex == -1) {
            throw new IOException("No hidden message found in this image, or image was not encoded with this tool.");
        }

        return fullText.substring(0, delimIndex);
    }

    /**
     * Checks if an image contains a hidden message.
     */
    public boolean hasHiddenMessage(String imagePath) {
        try {
            String decoded = decodeMessage(imagePath);
            return decoded != null && !decoded.isBlank();
        } catch (Exception e) {
            return false;
        }
    }

    // ---- Helper Methods ----

    private int getBit(byte[] bytes, int bitIndex) {
        int byteIndex = bitIndex / 8;
        int bitPosition = 7 - (bitIndex % 8);
        return (bytes[byteIndex] >> bitPosition) & 1;
    }

    private String bitsToString(String bits) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i + 8 <= bits.length(); i += 8) {
            String byteStr = bits.substring(i, i + 8);
            int charCode = Integer.parseInt(byteStr, 2);
            if (charCode == 0) break; // null terminator
            sb.append((char) charCode);
        }
        return sb.toString();
    }

    /**
     * Returns maximum number of characters that can be hidden in an image.
     */
    public int getMaxMessageLength(String imagePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(imagePath));
        if (image == null) throw new IOException("Cannot read image");
        return (image.getWidth() * image.getHeight() * 3) / 8 - DELIMITER.length();
    }
}
