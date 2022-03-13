package it.openly.projects.million4ukraine.m4urest.services;

import it.openly.projects.million4ukraine.m4urest.utils.TileComposer;
import it.openly.projects.million4ukraine.m4urest.utils.XY;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

@Service
public class TileComposerService {

    // FIXME: this whole thing won't work in production. Switch to an asyncservice with producer/consumer pattern

    @SneakyThrows
    private TileComposer prepareTileComposer() {
        BufferedImage mask;
        BufferedImage image;

        try {
            mask = ImageIO.read(new File("image_mask.jpg"));
        }
        catch(Exception ex) {
            mask = new BufferedImage(100, 100, TYPE_INT_RGB);
        }

        try {
            image = ImageIO.read(new File("image.jpg"));
        }
        catch(Exception ex) {
            image = new BufferedImage(10000, 10000, TYPE_INT_RGB);
        }

        return new TileComposer(mask, image);
    }

    @SneakyThrows
    private void saveComposedImageData(TileComposer composer) {
        ImageIO.write(composer.getImage(), "jpg", new File("image.jpg"));
        ImageIO.write(composer.getMask(), "jpg", new File("image_mask.jpg"));
    }

    public XY applyTile(BufferedImage image) {
        TileComposer composer = prepareTileComposer();
        XY spot = composer.getRandomEmptySpot();
        Color backgroundColor = composer.getBackgroundColorFor(spot);
        BufferedImage tile = composer.prepareTile(image, backgroundColor);
        composer.applyTile(spot, tile);
        saveComposedImageData(composer);
        return spot;
    }

    public BufferedImage getComposedImageThumbnail() {
        TileComposer composer = prepareTileComposer();
        Image image = composer.getImage();


        int thumbWidth = 1920;
        int thumbHeight = 1920;

        BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = thumbImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);
        g.dispose();

        return thumbImage;
    }

    @SneakyThrows
    public byte[] getImageData(BufferedImage image) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", bos);

        return bos.toByteArray();
    }


}
