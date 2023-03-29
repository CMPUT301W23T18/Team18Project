package com.example.team18project;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class for modelling QR codes.
 */
public class QRCode implements Parcelable {
    public static final int NULL_LOCATION = -1000;
    private String qid;
    private String value;
    private ArrayList<String> photoIds; //TODO different data type might be better, look into it
    private ArrayList<Comment> comments; //TODO tree might be better if comments can be replied to
    private double longitude;
    private double latitude;

    //TODO temporary method, copy paste this code to whereever you need it
    public static String getSHA256(String s) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(s.getBytes());
        String hashString = "";
        for (int i = 0; i < hashBytes.length; i++) {
            hashString += Integer.toHexString(((int)hashBytes[i]) & 0xFF);
        }

        return hashString;
    }

    public QRCode(String value, ArrayList<String> photoIds, ArrayList<Comment> comments, double longitude, double latitude) {
        this.value = value;
        this.photoIds = photoIds;
        this.comments = comments;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public QRCode(DocumentReference doc) {
        qid = doc.getId();
        Task task = doc.get();

        task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                photoIds = null; //TODO figure out how photos will be stored
                value = documentSnapshot.getString("value");
                longitude = documentSnapshot.getDouble("longitude");
                latitude = documentSnapshot.getDouble("latitude");
                ArrayList<DocumentReference> commentRefs = (ArrayList<DocumentReference>) documentSnapshot.get("comments");
                comments = new ArrayList<Comment>();

                //fill comments ArrayList
                for (int i = 0; i < commentRefs.size(); i++) {
                    comments.add(new Comment(commentRefs.get(i)));
                }
            }
        });
        while (!task.isComplete()) {
            //wait until document is read
        }
    }

    //Parcelable implementation

    protected QRCode(Parcel in) {
        comments = in.createTypedArrayList(Comment.CREATOR);
        photoIds = in.createStringArrayList();
        qid = in.readString();
        value = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
    }

    public static final Creator<QRCode> CREATOR = new Creator<QRCode>() {
        @Override
        public QRCode createFromParcel(Parcel in) {
            return new QRCode(in);
        }

        @Override
        public QRCode[] newArray(int size) {
            return new QRCode[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeTypedList(comments);
        dest.writeStringList(photoIds);
        dest.writeString(qid);
        dest.writeString(value);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
    }

    private static Bitmap cropImage(Bitmap image) {
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

    private static void uploadImage(Bitmap image, String path) {
        image = cropImage(image);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child(path);

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "ERROR: upload failed");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "upload succeeded :)");
            }
        });
    }

    public static void uploadQRCode(QRCode code, Bitmap image) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference codesCollection = db.collection("QRCodes");
        DocumentReference reference = codesCollection.document(code.getQid());
        Task readTask = reference.get();
        String imagePath = null;

        readTask.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String hash = documentSnapshot.getString("value");
                if (hash == null) { // this is a newly scanned qr code
                    Map<String, Object> data = new HashMap<>();
                    data.put("comments",new ArrayList<DocumentReference>());
                    data.put("latitude",code.getLatitude());
                    data.put("longitude",code.getLongitude());
                    data.put("photo", new ArrayList<String>());
                    data.put("value", code.getValue());
                    reference.set(data);
                }
            }
        });

        if (image != null) {
            imagePath = "images/img" + System.currentTimeMillis() + ".jpg";
            ;
            uploadImage(image, imagePath);
            reference.update("photo", FieldValue.arrayUnion(imagePath))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Image successfully added!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding image", e);
                        }
                    });
        }

    }

    public static String computeQid(double latitude, double longitude, String hash) {
        if (latitude == QRCode.NULL_LOCATION) {
            return hash + "_" + "null_location";
        }

        double roundedLat = (double) Math.round(latitude * 10000) / 10000;
        double roundedLong = (double) Math.round(longitude * 10000) / 10000;
        return hash + "_" + Double.toString(roundedLat) + "_" + Double.toString(roundedLong);
    }

    /**
     * Transform the QR codes unique value into a unique sprite that is represented as a bitmap
     * @param context
     * @return a bitmap
     */
    public Bitmap getVisual(Context context, int scaleFactor) {
        Bitmap combinedSprite;
        //start by splitting the value into three distinct keys
        char[] splitHash = value.toCharArray();
        int[] keyCodes = new int[3];
        for (int index = 0; index < 32; index++) {
            int key = index % 3;
            keyCodes[key] += (int) splitHash[index];
        }

        // load all of our pngs into arrays so we can select one
        int[] bodyIds = {R.drawable.imagegen_body_rock, R.drawable.imagegen_body_slime, R.drawable.imagegen_body_tree};
        int[] faceIds = {R.drawable.imagegen_face_happy, R.drawable.imagegen_face_mad, R.drawable.imagegen_face_shocked, R.drawable.imagegen_face_mood};
        int[] accessoryIds = {R.drawable.imagegen_accessory_horns_redblack, R.drawable.imagegen_accessory_bow, R.drawable.imagegen_accessory_crown, R.drawable.imagegen_accessory_feather, R.drawable.imagegen_accessory_leaf};
        // select one
        int bodyId = bodyIds[keyCodes[0] % bodyIds.length];
        int faceId = faceIds[keyCodes[1] % faceIds.length];
        int accessoryId = accessoryIds[keyCodes[2] % accessoryIds.length];
        // load the resources
        Bitmap bodyBitmap = BitmapFactory.decodeResource(context.getResources(), bodyId);
        Bitmap faceBitmap = BitmapFactory.decodeResource(context.getResources(), faceId);
        Bitmap accessoryBitmap = BitmapFactory.decodeResource(context.getResources(), accessoryId);
        // Create all variables needed to scale the image by the scale factor
        // *note all pngs are the same size so we just based the width and height off of an arbitrary one
        int baseWidth = bodyBitmap.getWidth();
        int baseHeight = bodyBitmap.getHeight();

        int newWidth = baseWidth * scaleFactor;
        int newHeight = baseHeight * scaleFactor;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleFactor, scaleFactor);

        combinedSprite = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        // Draw all the scaled images over each other to create our unique sprite
        Canvas canvas = new Canvas(combinedSprite);

        Bitmap scaledBody = Bitmap.createBitmap(bodyBitmap, 0, 0, baseWidth, baseHeight, matrix, true);
        canvas.drawBitmap(scaledBody, 0, 0, null);

        Bitmap scaledFace = Bitmap.createBitmap(faceBitmap, 0, 0, baseWidth, baseHeight, matrix, true);
        canvas.drawBitmap(scaledFace, 0, 0, null);

        Bitmap scaledAccessory = Bitmap.createBitmap(accessoryBitmap, 0, 0, baseWidth, baseHeight, matrix, true);
        canvas.drawBitmap(scaledAccessory, 0, 0, null);
        // Return the sprite
        return combinedSprite;
    }

    /**
     * Convert the QR codes hash functions(value) into a unique 4 part name string
     * @return unique name string
     */
    public String getName() {
        char[] splitHash = new char[64];
        splitHash = value.toCharArray();
        // convert the hash into 4 key codes which is the sum of all char int values in that quarter
        int[] keyCodes = new int[4];
        for (int index = 0; index < splitHash.length; index++) {
            int key = index % 4;
            keyCodes[key] += (int) splitHash[index];
        }
        String[] name = new String[4];
        // create all string arrays the name can be pulled from
        // note* do to the way this function is implemented each array can be expanded easily and indefinably by simply adding a new string at the end of an array
        String[] title = {"Emperor", "Empress", "King", "Queen", "Duke", "Duchess", "Prince", "Princes"};
        String[] firstName = {"Nolan", "Maximus", "Leo", "Victor", "Katie", "Kiara", "Selena", "Amy"};
        String[] SurName = {"Smith", "White", "Price", "Harper", "Chambers", "Cohen", "Hoffman", "Freenet"};
        String[] origin = {"Sinniko", "Sandgate", "Blastmore", "Wastehold", "Grimwall", "Driuru", "Smogmore", "Hookfort"};
        // select a value from each string array
        name[0] = title[keyCodes[0] % title.length];
        name[1] = firstName[keyCodes[1] % firstName.length];
        name[2] = SurName[keyCodes[2] % SurName.length];
        name[3] = "of " + origin[keyCodes[3] % origin.length];
        // merge the names characteristics and return it
        return TextUtils.join(" ", name);
    }

    /**
     * Compute the score of a QR code as it's sum value of integer byte conversions
     * @return the score as an integer
     */
    public int getScore() {
        char[] splitHash = value.toCharArray();
        int score = 0;
        for (char hash: splitHash) {
            score += (int) hash;
        }
        return score;
    }

    /**
     * Adds a comment to this QR code
     * @param comment the comment to be added
     */
    public void addComment(Comment comment) {
        comments.add(comment);
    }

    //getters and setters

    /**
     * Gets the hashed contents of the QR code
     * @return The hashed contents of the QR code
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the hashed contents of the QR code
     * @return The QR code's comments
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the Firestore document IDs of the QR code's photos
     * @return The Firestore document IDs of the QR code's photos
     */
    public ArrayList<String> getPhotoIds() {
        return photoIds;
    }

    /**
     * Sets the Firestore document IDs of the QR code's photos
     * @param photoIds The Firestore document IDs of the QR code's photos
     */
    public void setPhotoIds(ArrayList<String> photoIds) {
        this.photoIds = photoIds;
    }

    /**
     * Gets the comments of the QR code
     * @return The QR code's comments
     */
    public ArrayList<Comment> getComments() {
        return comments;
    }

    /**
     * Sets the comments of the QR code
     * @param comments The QR code's comments
     */
    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    /**
     * Gets the longitude of the location of the QR code
     * @return The longitude of the QR code's location
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Sets the longitude of the location of the QR code
     * @param longitude The longitude of the QR code's location
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the latitude of the location of the QR code
     * @return The latitude of the QR code's location
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Sets the latitude of the location of the QR code
     * @param latitude The latitude of the QR code's location
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the Firestore document ID the QR code
     * @return The QR code's Firestore document ID
     */
    public String getQid() {
        return qid;
    }

    /**
     * Sets the Firestore document ID of the QR code
     * @param qid The QR code's Firestore document ID
     */
    public void setQid(String qid) {
        this.qid = qid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QRCode qrCode = (QRCode) o;
        return qid.equals(qrCode.qid);
    }
}
