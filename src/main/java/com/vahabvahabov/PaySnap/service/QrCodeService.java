package com.vahabvahabov.PaySnap.service;

import com.google.zxing.WriterException;

import java.io.IOException;

public interface QrCodeService {

    public String generateQrCodeImage(String text, int width, int height) throws WriterException, IOException;
}
