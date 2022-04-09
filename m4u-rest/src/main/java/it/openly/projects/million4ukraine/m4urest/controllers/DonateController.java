package it.openly.projects.million4ukraine.m4urest.controllers;

import it.openly.projects.million4ukraine.m4urest.services.AsyncTileComposerService;
import it.openly.projects.million4ukraine.m4urest.services.DataService;
import it.openly.projects.million4ukraine.m4urest.utils.DataCleaner;
import it.openly.projects.million4ukraine.m4urest.utils.XY;
import it.openly.projects.million4ukraine.m4urest.views.M4UMessage;
import it.openly.projects.million4ukraine.m4urest.views.NameAndMessage;
import it.openly.projects.million4ukraine.m4urest.views.PaymentMessage;
import it.openly.projects.million4ukraine.m4urest.views.Response;
import lombok.SneakyThrows;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@CrossOrigin
@RestController
public class DonateController {

    private final AsyncTileComposerService asyncTileComposerService;
    private final DataService dataService;

    public DonateController(AsyncTileComposerService asyncTileComposerService, DataService dataService) {
        this.asyncTileComposerService = asyncTileComposerService;
        this.dataService = dataService;
    }

    @PostMapping("submit")
    public Response submit(@RequestBody M4UMessage request) {
        DataCleaner.xssClean(request);
        DataCleaner.constrainSize(request);

        XY spot = asyncTileComposerService.selectRandomEmptySpot(request.getSizeX(), request.getSizeY());
        dataService.savePreDonationMessage(request, spot);
        return new Response(request.getId());
    }

    @PostMapping("process_donation")
    @SneakyThrows
    public void processDonation(@RequestBody PaymentMessage paymentMessage) {
        M4UMessage request = dataService.loadMessage(paymentMessage.getUuid());
        XY spot = new XY(request.getX(), request.getY());

        String dataurl = request.getImageDataurl();
        String payload = dataurl.substring(dataurl.indexOf(",") + 1);

        byte[] imageData = Base64.decodeBase64(payload);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));

        asyncTileComposerService.applyTile(image, request.getSizeX(), request.getSizeY(), spot).get();

        dataService.savePostDonationMessage(request, paymentMessage.getAmountDonated());
    }

    @GetMapping(value = "thumbnail", produces = MediaType.IMAGE_JPEG_VALUE)
    @SneakyThrows
    public @ResponseBody byte[] thumbnail(@RequestParam("ts") long ts) {
        BufferedImage image = asyncTileComposerService.getComposedImageThumbnail().get();
        return getImageData(image);
    }

    @GetMapping(value = "latestdonations")
    public List<NameAndMessage> getLatestDonations() {
        return dataService.getLatestMessages();
    }

    @SneakyThrows
    private byte[] getImageData(BufferedImage image) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", bos);

        return bos.toByteArray();
    }


}
