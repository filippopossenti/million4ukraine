package it.openly.projects.million4ukraine.m4urest.services;

import it.openly.projects.million4ukraine.m4urest.utils.XY;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncTileComposerService {

    private final TileComposerService tileComposerService;

    public AsyncTileComposerService(TileComposerService tileComposerService) {
        this.tileComposerService = tileComposerService;
    }

    @Async
    public CompletableFuture<XY> applyTile(BufferedImage tileImage, int sizeX, int sizeY) {
        return CompletableFuture.completedFuture(tileComposerService.applyTile(tileImage, sizeX, sizeY));
    }

    @Async
    public CompletableFuture<BufferedImage> getComposedImageThumbnail() {
        return CompletableFuture.completedFuture(tileComposerService.getComposedImageThumbnail());
    }
}
