package it.openly.projects.million4ukraine.m4urest.utils;

import lombok.*;

import java.awt.RenderingHints;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@AllArgsConstructor
public class TileComposer {
    @Getter
    @Setter
    private BufferedImage mask;
    @Getter
    @Setter
    private BufferedImage image;
    private Random random;

    public TileComposer(BufferedImage mask, BufferedImage image) {
        this(mask, image, new Random());
    }

    @SneakyThrows
    public XY getRandomEmptySpot() {
        int attempt = 0;
        while(attempt++ < 5) {
            int tx = random.nextInt(mask.getWidth());
            int ty = random.nextInt(mask.getHeight());

            int rgb = mask.getRGB(tx, ty) & 0xffffff;
            if(rgb == 0) {
                return new XY(tx, ty);
            }
        }
        // If we get here it means we couldn't find a random one by apply random coordinates.
        // This means the picture is probably densely saturated so we compile a list of all available spots
        // and then specifically select one of those spots

        List<XY> availableCoords = new ArrayList<>();
        for(int y = 0; y < mask.getHeight(); y++) {
            for(int x = 0; x < mask.getWidth(); x++) {
                int rgb = mask.getRGB(x, y) & 0xffffff;
                if(rgb == 0) {
                    availableCoords.add(new XY(x, y));
                }
            }
        }
        if(availableCoords.isEmpty()) {
            throw new Exception("No empty spot could be found.");
        }

        return availableCoords.get(random.nextInt(availableCoords.size()));
    }

    public void applyTile(XY location, BufferedImage tile) {
        // TODO: this currently doesn't validate the tile size.
        int tileWidth = image.getWidth() / mask.getWidth();
        int tileHeight = image.getHeight() / mask.getHeight();

        int x = location.getX() * tileWidth;
        int y = location.getY() * tileHeight;

        Graphics g = image.createGraphics();
        g.drawImage(tile, x, y, null);
        g.dispose();

        mask.setRGB(location.getX(), location.getY(), 0xffffffff);
    }

    public BufferedImage prepareTile(BufferedImage tileImage) {
        int tileWidth = image.getWidth() / mask.getWidth();
        int tileHeight = image.getHeight() / mask.getHeight();

        BufferedImage tile = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = tile.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(tileImage, 0, 0, tileWidth, tileHeight, null);
        g.dispose();
        return tile;
    }
}
