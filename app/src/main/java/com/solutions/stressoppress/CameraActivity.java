package com.solutions.stressoppress;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.FaceAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CameraActivity extends AppCompatActivity {

    private ImageView imageView;
    private int [] emotionArray = new int[3];
    private String displayString = new String();
    private DatabaseReference mDatabase;
    private String genre,user,emotion;
    static final String EMOTION = "0", GENRE = "1",USER = "2";
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        Intent intent = getIntent();

        user = intent.getStringExtra(MainActivity.USER);

        Button btnCamera = (Button)findViewById(R.id.btnCamera);
        imageView = (ImageView)findViewById(R.id.ImageView);

        btnCamera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
                Toast.makeText(CameraActivity.this, "Open Camera Clicked",
                        Toast.LENGTH_LONG).show();
                Log.d("Camera", "Camera Clicked");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        imageView.setImageBitmap(bitmap);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        //bitmap.recycle();
        //int[] emotions = annotateImage(byteArray);
        try {
            new RetrieveAnnotationTask().execute(byteArray).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Toast.makeText(CameraActivity.this, displayString + " "+ count + " " + emotionArray[0] + emotionArray[1] + emotionArray[2], Toast.LENGTH_LONG).show();
        //if (emotionArray[0] == 0) emotionArray[0] = 1;
        //if (emotionArray[1] == 0) emotionArray[1] = 1;
        //if (emotionArray[2] == 0) emotionArray[2] = 1;
        emotionArray[0] = 100;
        emotionArray[1] = 100;
        emotionArray[2] = 100;



        //if (count >= 1) {
            chooseGenre(emotionArray);
            Intent intentLink = new Intent(CameraActivity.this, LinkActivity.class);
            intentLink.putExtra(EMOTION, emotion);
            intentLink.putExtra(GENRE, genre);
            intentLink.putExtra(USER, user);
            CameraActivity.this.startActivity(intentLink);
    }

    long[][] preferenceIndex;

    // anger, joy, sorrow
    public void chooseGenre(int[] emotionScores){

        final Context context = this;
        final String[] genres = {"Hip Hop","Pop","Country","Classical","Rock","Jazz","Romance","Blues"};
        final String[] emotions = {"Anger","Joy","Sorrow"};
        final int[] finalEmotionScores = emotionScores;

        preferenceIndex = new long[8][3];

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int j=0;
                for (String genre:genres){
                    int i=0;
                    for (String emotion:emotions){
                        preferenceIndex[j][i] = finalEmotionScores[i]*(long)(dataSnapshot.child(genre).child(emotion).getValue());
                        i++;
                    }
                    j++;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        // apply operation
        mDatabase.child(user).addListenerForSingleValueEvent(listener);

        long max = 0;
        int maxJ = 0;
        for (int j=0;j<8;j++){
            for (int i=0;i<3;i++){
                if (preferenceIndex[j][i]>max) {
                    max = preferenceIndex[j][i];
                    maxJ = j;
                }
            }
        }

        genre = genres[maxJ];

    }

    private static final String KEY = "AIzaSyCUx1PEECGoPZ1P_jDt7vJaB6GnP3t_tSY";

    class RetrieveAnnotationTask extends AsyncTask<byte[], Void, int[]> {
        protected int[] doInBackground(byte[]... byteArray) {
            int [] emotions = new int[3];


            HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
            VisionRequestInitializer initializer = new VisionRequestInitializer(KEY);
            Vision vision = new Vision.Builder(httpTransport, jsonFactory, null)
                    .setVisionRequestInitializer(initializer)
                    .build();

            AnnotateImageRequest imageRequest = new AnnotateImageRequest();
            Image image = new Image();
            image.encodeContent(byteArray[0]); // questionable
            imageRequest.setImage(image);

            Feature desiredFeature = new Feature();
            desiredFeature.setType("FACE_DETECTION");
            imageRequest.setFeatures(Arrays.asList(desiredFeature));

            BatchAnnotateImagesRequest batchRequest = new BatchAnnotateImagesRequest();
            batchRequest.setRequests(Arrays.asList(imageRequest));

            BatchAnnotateImagesResponse batchResponse = new BatchAnnotateImagesResponse();

            try {
                batchResponse = vision.images().annotate(batchRequest).execute();
            }
            catch (IOException e) {
                Log.e("THIS IS AN ERROR", e.getMessage());
                /*emotionArray[0] = 10;
                emotionArray[1] = 10;
                emotionArray[2] = 10;*/
                return emotions;
            }

            List<FaceAnnotation> faces = batchResponse.getResponses().get(0).getFaceAnnotations();

            // only using first face, if there are multiple
            if (faces == null) {
                Log.e("NO_FACE_EXCEPTION", "There wasn't a face detected");
                //Toast.makeText(CameraActivity.this, "No faces were detected. Try again!", Toast.LENGTH_LONG).show();
                /*emotionArray[0] = 5;
                emotionArray[1] = 5;
                emotionArray[2] = 5;*/
                return emotions;
            }
            //Toast.makeText(CameraActivity.this, "Got Here!", Toast.LENGTH_LONG).show();
            /*emotionArray[0] = 1000;
            emotionArray[1] = 1000;
            emotionArray[2] = 1000;*/

            //count = faces.size();

            String anger = faces.get(0).getAngerLikelihood();
            String joy = faces.get(0).getJoyLikelihood();
            String sorrow = faces.get(0).getSorrowLikelihood();

            emotionArray[0] = getCorrespondingNumber(anger);
            emotionArray[1] = getCorrespondingNumber(joy);
            emotionArray[2] = getCorrespondingNumber(sorrow);

            displayString = sorrow;

            return emotions;
        }

        protected void onPostExecute(int[] vals) {
            //emotionArray = vals;
        }
    }

    public int getCorrespondingNumber(String chance) {
        if (chance.equals("VERY_LIKELY")) {
            return 4;
        }
        if (chance.equals("LIKELY")) {
            return 3;
        }
        if (chance.equals("POSSIBLE")) {
            return 2;
        }
        if (chance.equals("UNLIKELY")|| chance.equals("VERY_UNLIKELY")) {
            return 1;
        }
        return 0;
    }



    /*public int[] annotateImage(byte[] byteArray) {
        //Toast.makeText(this, "TEST", Toast.LENGTH_LONG).show();


        int [] emotions = new int[3];

        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        VisionRequestInitializer initializer = new VisionRequestInitializer(KEY);
        Vision vision = new Vision.Builder(httpTransport, jsonFactory, null)
                .setVisionRequestInitializer(initializer)
                .build();

        AnnotateImageRequest imageRequest = new AnnotateImageRequest();
        Image image = new Image();
        image.encodeContent(byteArray);
        imageRequest.setImage(image);

        Feature desiredFeature = new Feature();
        desiredFeature.setType("FACE_DETECTION");
        imageRequest.setFeatures(Arrays.asList(desiredFeature));

        BatchAnnotateImagesRequest batchRequest = new BatchAnnotateImagesRequest();
        batchRequest.setRequests(Arrays.asList(imageRequest));
        
        try {
            BatchAnnotateImagesResponse batchResponse = vision.images().annotate(batchRequest).execute();
        }
        catch (IOException e) {
            Log.e("THIS IS AN ERROR", "ERROR!!!");
        }

        return emotions;
    }*/
}
