package com.example.team18project.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.example.team18project.model.ImageArrayAdapter;
import com.example.team18project.model.QRCode;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ViewImagesController {
    /**
     * get the image of a QRCode
     * @param code the qrcode to get image for
     * @param images the ImageArrayAdaptor object to update
     */
    public void getImages(QRCode code, ImageArrayAdapter images) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        Log.d("testing", code.getValue());
        ArrayList<String> photoIDs = code.getPhotoIds();
        for (String photoPath : photoIDs) {
            Log.d("testing", photoPath);
            StorageReference imageRef = storage.getReference().child(photoPath);
            final long ONE_MEGABYTE = 1024 * 1024;
            imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Log.d("testing", "made it here");
                    Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    //
                    float scaleFactor = 7.5F;

                    int baseWidth = image.getWidth();
                    int baseHeight = image.getHeight();

                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleFactor, scaleFactor);

                    Bitmap scaledBody = Bitmap.createBitmap(image, 0, 0, baseWidth, baseHeight, matrix, true);
                    images.add(scaledBody);
                }
            });
        }

    }

}
