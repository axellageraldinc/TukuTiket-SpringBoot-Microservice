package com.axell.tukutiket.nggomlebu;

import org.springframework.core.io.Resource;

import java.net.MalformedURLException;

public interface NggomlebuService {
    GenerateQRCodeResponse generateQRCode(GenerateQRCodeRequest generateQrCodeRequest);

    Resource downloadQRCodeImage(String qrCodeImageName) throws MalformedURLException;
}
