package it.openly.projects.million4ukraine.m4urest.utils;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
public class TileComposer {
    @Getter
    @Setter
    private BufferedImage mask;
    @Getter
    @Setter
    private BufferedImage image;
    private Random random;

    private int maskWidth;
    private int maskHeight;
    private int imageWidth;
    private int imageHeight;

    public TileComposer(BufferedImage mask, BufferedImage image) {
        this.mask = mask;
        this.image = image;
        this.random = new Random();
        this.maskWidth = mask.getWidth();
        this.maskHeight = mask.getHeight();
        this.imageWidth = image.getWidth();
        this.imageHeight = image.getHeight();

    }

    private int nextRandomInt(int bound) {
        return random.nextInt(bound);
    }

    @SneakyThrows
    public XY getRandomEmptySpot(int width, int height) {
        if(!(width > 0 && width < 100) || !(height > 0 && height < 100)) {
            throw new IndexOutOfBoundsException();
        }

        int attempt = 0;
        while(attempt++ < 5) {
            int tx = nextRandomInt(maskWidth);
            int ty = nextRandomInt(maskHeight);

            int rgb = mask.getRGB(tx, ty) & 0xffffff;
            if(rgb == 0) {
                return new XY(tx, ty);
            }
        }
        // If we get here it means we couldn't find a random one by apply random coordinates.
        // This means the picture is probably densely saturated so we compile a list of all available spots
        // and then specifically select one of those spots

        List<XY> availableCoords = new ArrayList<>();
        for(int y = 0; y < maskHeight - height; y += height) {
            for(int x = 0; x < maskWidth - width; x += width) {
                if(isEmptyArea(mask, x, y, width, height)) {
                    availableCoords.add(new XY(x, y));
                }
            }
        }
        if(availableCoords.isEmpty()) {
            throw new Exception("No empty spot could be found.");
        }

        return availableCoords.get(nextRandomInt(availableCoords.size()));
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
        int tileWidth = imageWidth / maskWidth;
        int tileHeight = imageHeight / maskHeight;

        int x = location.getX() * tileWidth;
        int y = location.getY() * tileHeight;

        Graphics g = image.createGraphics();
        g.drawImage(tile, x, y, null);
        g.dispose();

        setUsedArea(mask, location.getX(), location.getY(), sizeX, sizeY);
    }

    private void setUsedArea(BufferedImage mask, int tx, int ty, int width, int height) {
        try {
            for (int y = ty; y < ty + height; y++) {
                for (int x = tx; x < tx + width; x++) {
                    mask.setRGB(x, y, 0xffffffff);
                }
            }
        }
        catch(ArrayIndexOutOfBoundsException ex) {
            log.error("There's a bug that I haven't figured out yet. Here's some useful info: {}, {}, {}, {}, {}, {}", tx, ty, width, height, mask.getWidth(), mask.getHeight());
            throw ex;
        }
    }

    public Color getBackgroundColorFor(XY coords) {
        if(coords.getY() > maskHeight / 2) {
            return new Color(Constants.UKR_FLAG_GOLD);
        }
        return new Color(Constants.UKR_FLAG_AZURE);
    }

    public BufferedImage prepareTile(BufferedImage tileImage, Color backgroundColor, int sizeX, int sizeY) {
        double targetTileWidth = (double)(imageWidth * sizeX) / (double)maskWidth ;
        double targetTileHeight = (double)(imageHeight * sizeY) / (double)maskHeight;

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

    public BufferedImage getThumbnail(int width, int height) {
        BufferedImage thumbImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = thumbImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();

        return thumbImage;

    }
}
