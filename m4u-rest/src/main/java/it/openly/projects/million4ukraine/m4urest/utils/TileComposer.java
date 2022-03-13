package it.openly.projects.million4ukraine.m4urest.utils;

import lombok.*;

import java.awt.*;
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
    public XY getRandomEmptySpot(int width, int height) {
        if(!(width > 0 && width < 100) || !(height > 0 && height < 100)) {
            throw new IndexOutOfBoundsException();
        }

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
        for(int y = 0; y < mask.getHeight() - height; y += height) {
            for(int x = 0; x < mask.getWidth() - width; x += width) {
                if(isEmptyArea(mask, x, y, width, height)) {
                    availableCoords.add(new XY(x, y));
                }
            }
        }
        if(availableCoords.isEmpty()) {
            throw new Exception("No empty spot could be found.");
        }

        return availableCoords.get(random.nextInt(availableCoords.size()));
    }

    private boolean isEmptyArea(BufferedImage mask, int tx, int ty, int width, int height) {
        for(int y = ty; y < ty + height; y++) {
            for(int x = tx; x < tx + width; x++) {
                int rgb = mask.getRGB(x, y) & 0xffffff;
                if(rgb != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public void applyTile(XY location, BufferedImage tile, int sizeX, int sizeY) {
        // TODO: this currently doesn't validate the tile size.
        int tileWidth = image.getWidth() / mask.getWidth();
        int tileHeight = image.getHeight() / mask.getHeight();

        int x = location.getX() * tileWidth;
        int y = location.getY() * tileHeight;

        Graphics g = image.createGraphics();
        g.drawImage(tile, x, y, null);
        g.dispose();

        setUsedArea(mask, location.getX(), location.getY(), sizeX, sizeY);
    }

    private void setUsedArea(BufferedImage mask, int tx, int ty, int width, int height) {
        for(int y = ty; y < ty + height; y++) {
            for(int x = tx; x < tx + width; x++) {
                mask.setRGB(x, y, 0xffffffff);
            }
        }
    }

    public Color getBackgroundColorFor(XY coords) {
        if(coords.getY() > mask.getHeight() / 2) {
            return new Color(Constants.UKR_FLAG_GOLD);
        }
        return new Color(Constants.UKR_FLAG_AZURE);
    }

    public BufferedImage prepareTile(BufferedImage tileImage, Color backgroundColor, int sizeX, int sizeY) {
        double targetTileWidth = (double)(image.getWidth() * sizeX) / (double)mask.getWidth() ;
        double targetTileHeight = (double)(image.getHeight() * sizeY) / (double)mask.getHeight();

        double ratio = ((double)tileImage.getWidth()) / ((double)tileImage.getHeight());

        double tileHeight = targetTileHeight / ratio;
        double tileWidth = targetTileWidth;

        if(tileHeight > targetTileHeight) {
            tileHeight = targetTileHeight;
            tileWidth = targetTileWidth * ratio;
        }

        int xpos = (int)((targetTileWidth - tileWidth) / 2.0f);
        int ypos = (int)((targetTileHeight - tileHeight) / 2.0f);

        BufferedImage tile = new BufferedImage((int)targetTileWidth, (int)targetTileHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = tile.createGraphics();
        g.setBackground(backgroundColor);
        g.setColor(backgroundColor);
        g.drawRect(0, 0, (int)targetTileWidth, (int)targetTileHeight);
        g.fillRect(0, 0, (int)targetTileWidth, (int)targetTileHeight);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(tileImage, xpos, ypos, (int)tileWidth, (int)tileHeight, null);
        g.dispose();
        return tile;
    }
}
