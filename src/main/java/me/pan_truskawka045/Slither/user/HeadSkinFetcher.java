package me.pan_truskawka045.Slither.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.pan_truskawka045.Slither.SpigotSlitherPlugin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Log4j2
public class HeadSkinFetcher {

    private static final int HEAD_SIZE = 8;
    private static final int CHANNELS_PER_PIXEL = 3;
    private static final int CONNECTION_TIMEOUT_MILLIS = 5_000;

    private final SpigotSlitherPlugin plugin;

    public CompletableFuture<byte[]> fetch(URL skinUrl) {
        if (skinUrl == null) {
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.supplyAsync(() -> {
            try (InputStream skinStream = openSkinStream(skinUrl)) {
                BufferedImage skin = ImageIO.read(skinStream);
                if (skin == null) {
                    throw new IOException("Skin URL did not return an image");
                }
                return headSkinFrom(skin);
            } catch (IOException | IllegalArgumentException exception) {
                log.warn("Could not load skin from {}", skinUrl, exception);
                return null;
            }
        }, plugin.getAsyncExecutor());
    }

    private byte[] headSkinFrom(BufferedImage skin) {
        if (skin.getWidth() < 48 || skin.getHeight() < 16) {
            throw new IllegalArgumentException("Skin texture must contain the base and overlay head regions");
        }

        byte[] headSkin = new byte[HEAD_SIZE * HEAD_SIZE * CHANNELS_PER_PIXEL];
        for (int y = 0; y < HEAD_SIZE; y++) {
            for (int x = 0; x < HEAD_SIZE; x++) {
                int basePixel = skin.getRGB(8 + x, 8 + y);
                int overlayPixel = skin.getRGB(40 + x, 8 + y);
                int offset = (y * HEAD_SIZE + x) * CHANNELS_PER_PIXEL;

                headSkin[offset] = (byte) compositeChannel(basePixel, overlayPixel, 16);
                headSkin[offset + 1] = (byte) compositeChannel(basePixel, overlayPixel, 8);
                headSkin[offset + 2] = (byte) compositeChannel(basePixel, overlayPixel, 0);
            }
        }
        return headSkin;
    }

    private InputStream openSkinStream(URL skinUrl) throws IOException {
        URLConnection connection = skinUrl.openConnection();
        connection.setConnectTimeout(CONNECTION_TIMEOUT_MILLIS);
        connection.setReadTimeout(CONNECTION_TIMEOUT_MILLIS);
        return connection.getInputStream();
    }

    private int compositeChannel(int basePixel, int overlayPixel, int shift) {
        int overlayAlpha = overlayPixel >>> 24;
        return (overlayAlpha == 0 ? basePixel : overlayPixel) >>> shift & 0xFF;
    }
}
