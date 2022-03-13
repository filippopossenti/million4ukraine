package it.openly.projects.million4ukraine.m4urest.controllers;

import it.openly.projects.million4ukraine.m4urest.services.DataService;
import it.openly.projects.million4ukraine.m4urest.services.TileComposerService;
import it.openly.projects.million4ukraine.m4urest.utils.DataCleaner;
import it.openly.projects.million4ukraine.m4urest.utils.XY;
import it.openly.projects.million4ukraine.m4urest.views.M4UMessage;
import it.openly.projects.million4ukraine.m4urest.views.NameAndMessage;
import lombok.SneakyThrows;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.List;

@CrossOrigin
@RestController
public class DonateController {

    private final TileComposerService tileComposerService;
    private final DataService dataService;

    public DonateController(TileComposerService tileComposerService, DataService dataService) {
        this.tileComposerService = tileComposerService;
        this.dataService = dataService;
    }

    @PostMapping("submit")
    @SneakyThrows
    public void submit(@RequestBody M4UMessage request) {
        DataCleaner.xssClean(request);
        DataCleaner.constrainSize(request);

        String dataurl = request.getImageDataurl();
        String payload = dataurl.substring(dataurl.indexOf(",") + 1);

        byte[] imageData = Base64.decodeBase64(payload);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));

        XY spot = tileComposerService.applyTile(image, request.getSizeX(), request.getSizeY());
        dataService.saveMessage(request, spot);
    }

    @GetMapping(value = "thumbnail", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody byte[] thumbnail(@RequestParam("ts") long ts) {
        BufferedImage image = tileComposerService.getComposedImageThumbnail();
        return tileComposerService.getImageData(image);
    }

    @GetMapping(value = "latestdonations")
    public List<NameAndMessage> getLatestDonations() {
        return dataService.getLatestMessages();
    }

}
