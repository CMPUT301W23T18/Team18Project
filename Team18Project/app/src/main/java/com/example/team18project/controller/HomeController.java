package com.example.team18project.controller;

import android.graphics.Bitmap;

import com.example.team18project.model.Player;
import com.example.team18project.model.QRCode;

public class HomeController {

    private Player player;

    /**
     * contructer that passes player onto controller
     * @param player instance of player that gets passed into controller
     */
    public HomeController(Player player) {
        this.player = player;
    }

    /**
     * adds scanned qr code to the database
     * @param code code of the QRCode
     * @param image image of the QRCode
     */
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
