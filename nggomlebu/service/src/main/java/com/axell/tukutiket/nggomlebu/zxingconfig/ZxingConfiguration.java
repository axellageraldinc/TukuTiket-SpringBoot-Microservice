package com.axell.tukutiket.nggomlebu.zxingconfig;

import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZxingConfiguration {
    @Bean
    public QRCodeWriter qrCodeWriter() {
        return new QRCodeWriter();
    }
}
