package it.openly.projects.million4ukraine.m4urest.services;

import it.openly.projects.million4ukraine.m4urest.utils.Constants;
import it.openly.projects.million4ukraine.m4urest.utils.TileComposer;
import it.openly.projects.million4ukraine.m4urest.utils.XY;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

@Service
public class TileComposerService {

    private static final int TILE_SIZE = 96;
    private static final int HORIZONTAL_TILES = 200;
    private static final int VERTICAL_TILES = 112;

    private static final int THUMB_WIDTH = 1920;

    private TileComposer composer;


    @PostConstruct
    public void init() {
        composer = prepareTileComposer();
    }

    @SneakyThrows
    private TileComposer prepareTileComposer() {
        BufferedImage mask;
        BufferedImage image;

        try {
            mask = ImageIO.read(new File("image_mask.jpg"));
        }
        catch(Exception ex) {
            mask = new BufferedImage(HORIZONTAL_TILES, VERTICAL_TILES, TYPE_INT_RGB);
        }

        try {
            image = ImageIO.read(new File("image.jpg"));
        }
        catch(Exception ex) {
            image = new BufferedImage(TILE_SIZE * HORIZONTAL_TILES, TILE_SIZE * VERTICAL_TILES, TYPE_INT_RGB);
            buildUkrainianFlag(image);
        }

        return new TileComposer(mask, image);
    }

    private void buildUkrainianFlag(BufferedImage image) {
        Graphics2D g = image.createGraphics();
        g.setColor(new Color(Constants.UKR_FLAG_AZURE, false));
        g.drawRect(0, 0, image.getWidth(), image.getHeight() / 2);
        g.fillRect(0, 0, image.getWidth(), image.getHeight() / 2);
        g.setColor(new Color(Constants.UKR_FLAG_GOLD, false));
        g.drawRect(0, image.getHeight() / 2, image.getWidth(), image.getHeight() / 2);
        g.fillRect(0, image.getHeight() / 2, image.getWidth(), image.getHeight() / 2);

    }

    @SneakyThrows
    private void saveComposedImageData(TileComposer composer) {
        ImageIO.write(composer.getImage(), "jpg", new File("image.jpg"));
        ImageIO.write(composer.getMask(), "jpg", new File("image_mask.jpg"));
    }

    public XY applyTile(BufferedImage tileImage, int sizeX, int sizeY) {
        XY spot = composer.getRandomEmptySpot(sizeX, sizeY);
        Color backgroundColor = composer.getBackgroundColorFor(spot);
        BufferedImage tile = composer.prepareTile(tileImage, backgroundColor, sizeX, sizeY);
        composer.applyTile(spot, tile, sizeX, sizeY);
        saveComposedImageData(composer);
        return spot;
    }

    public BufferedImage getComposedImageThumbnail() {
        int thumbHeight = THUMB_WIDTH * VERTICAL_TILES * TILE_SIZE / HORIZONTAL_TILES / TILE_SIZE;
        return composer.getThumbnail(THUMB_WIDTH, thumbHeight);
    }

}
