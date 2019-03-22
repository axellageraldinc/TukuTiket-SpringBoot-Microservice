package com.axell.tukutiket.nggomlebu;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class NggomlebuServiceImpl implements NggomlebuService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NggomlebuServiceImpl.class);
    private static final String IMAGE_DIRECTORY = System.getProperty("user.dir") + "/";

    @Value("${qrcode.image.format}")
    private String qrCodeImageFormat;
    @Value("${qrcode.image.height}")
    private Integer qrCodeImageHeight;
    @Value("${qrcode.image.width}")
    private Integer qrCodeImageWidth;

    @Autowired
    private QRCodeWriter qrCodeWriter;

    @Override
    @RabbitListener(queues = "${queue.payment.verified}")
    @SendTo(value = "${queue.qrcode.created}")
    public GenerateQRCodeResponse generateQRCode(GenerateQRCodeRequest generateQrCodeRequest) {
        try {
            String imageName = UUID.randomUUID().toString() + "." + qrCodeImageFormat;
            BitMatrix bitMatrix = qrCodeWriter.encode(generateQrCodeRequest.getOrderId(), BarcodeFormat.QR_CODE, qrCodeImageWidth, qrCodeImageHeight);
            Path path = Paths.get(IMAGE_DIRECTORY + imageName);
            MatrixToImageWriter.writeToPath(bitMatrix, qrCodeImageFormat, path);

            return GenerateQRCodeResponse.builder()
                    .orderId(generateQrCodeRequest.getOrderId())
                    .imageName(imageName)
                    .build();
        } catch (Exception ex) {
            LOGGER.error("Error generate qr code : " + ex.getLocalizedMessage() + " | " + ex.getMessage());
            throw new RuntimeException(ErrorCode.GENERATE_QR_CODE_FAILED.toString());
        }
    }

    @Override
    public Resource downloadQRCodeImage(String qrCodeImageName) throws MalformedURLException {
        Path filePath = Paths.get(IMAGE_DIRECTORY).toAbsolutePath().normalize().resolve(qrCodeImageName).normalize();
        return new UrlResource(filePath.toUri());
    }
}
