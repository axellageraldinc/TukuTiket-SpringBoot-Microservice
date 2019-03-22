package com.axell.tukutiket.nggomlebu;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class NggomlebuServiceImplTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private QRCodeWriter qrCodeWriter;

    @InjectMocks
    private NggomlebuServiceImpl nggomlebuService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        ReflectionTestUtils.setField(nggomlebuService, "qrCodeImageFormat", "png", String.class);
        ReflectionTestUtils.setField(nggomlebuService, "qrCodeImageHeight", 60, Integer.class);
        ReflectionTestUtils.setField(nggomlebuService, "qrCodeImageWidth", 60, Integer.class);
    }

    @Test
    public void generateQRCodeSuccess() throws Exception {
        given(qrCodeWriter.encode(anyString(), any(BarcodeFormat.class), anyInt(), anyInt()))
                .willReturn(new BitMatrix(40, 40));

        GenerateQRCodeResponse response = nggomlebuService.generateQRCode(GenerateQRCodeRequest.builder()
                .userEmailAddress("1@1.com")
                .orderId("123")
                .build());

        assertThat(response, notNullValue());
        Path path = Paths.get(response.getImageName());
        assertThat(path.toFile().exists(), equalTo(true));
        path.toFile().delete();

        verify(qrCodeWriter, times(1)).encode(anyString(), any(BarcodeFormat.class), anyInt(), anyInt());
    }

    @Test(expected = RuntimeException.class)
    public void generateQRCodeFailed() throws Exception {
        given(qrCodeWriter.encode(anyString(), any(BarcodeFormat.class), anyInt(), anyInt()))
                .willThrow(IllegalArgumentException.class);

        nggomlebuService.generateQRCode(GenerateQRCodeRequest.builder()
                .userEmailAddress("1@1.com")
                .orderId("")
                .build());

        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(ErrorCode.GENERATE_QR_CODE_FAILED.toString());

        verify(qrCodeWriter, times(1)).encode(anyString(), any(BarcodeFormat.class), anyInt(), anyInt());
    }

    @Test
    public void downloadQRCodeImage_SUCCESS() throws Exception {
        Resource response = nggomlebuService.downloadQRCodeImage("resource-test-image.jpg");

        Assert.assertThat(response, notNullValue());
        Assert.assertThat(response.getFile().getName(), equalTo("resource-test-image.jpg"));
    }
}