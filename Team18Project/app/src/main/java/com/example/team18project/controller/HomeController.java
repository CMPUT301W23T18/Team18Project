package com.example.team18project.controller;

import android.graphics.Bitmap;

import com.example.team18project.model.Player;
import com.example.team18project.model.QRCode;

public class HomeController {

    private Player player;

    public HomeController(Player player) {
        this.player = player;
    }

    public void addScannedQRCode(QRCode code, Bitmap image) {

        String path = null;

        if (image != null) {
            image = cropImage(image);
            path = FirebaseWriter.getInstance().addImage(image);
        }

        FirebaseWriter.getInstance().addQRCode(code, path);

    }

    private Bitmap cropImage(Bitmap image) {
        int width  = image.getWidth();
        int height = image.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width) ? width : height;
        int cropW = (width - height) / 2;
        cropW = (cropW < 0) ? 0: cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0) ? 0: cropH;
        Bitmap cropImg = Bitmap.createBitmap(image, cropW, cropH, newWidth, newHeight);
        return cropImg;
    }
}
