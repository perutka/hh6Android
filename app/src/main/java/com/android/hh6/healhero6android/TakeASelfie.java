package com.android.hh6.healhero6android;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.net.URI;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.utils.URIBuilder;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class TakeASelfie extends AppCompatActivity implements View.OnClickListener{


    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 1999;

    private ImageView imageView; // variable to hold the image view in our activity_main.xml
    private TextView resultText; // variable to hold the text view in our activity_main.xml

    private Button btnCamera;   // button to open camera
    private Button btnEmotion;  // button to send emotion recognition request

    private static final int REQUEST_PERMISSION_CODE = 200;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_aselfie);


        // initiate our image view and text view
        imageView = (ImageView) findViewById(R.id.imageView);
        resultText = (TextView) findViewById(R.id.resultText);
        btnCamera = (Button)findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(this);
        btnEmotion = (Button)findViewById(R.id.btnEmotion);
        btnEmotion.setOnClickListener(this);
    }


    // when the "GET EMOTION" Button is clicked this function is called
    public void getEmotion(View view) {
        // run the GetEmotionCall class in the background
        GetEmotionCall emotionCall = new GetEmotionCall(imageView);
        emotionCall.execute();
    }


    // when the "GET IMAGE" Button is clicked this function is called
    public void getImage(View view) {
        // check if user has given us permission to access the camera
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    MY_CAMERA_PERMISSION_CODE);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    //result of the camera (aphoto)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }
    }



    // convert image to base 64 so that we can send the image to Emotion API
    public byte[] toBase64(ImageView imgPreview) {
        Bitmap bm = ((BitmapDrawable) imgPreview.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
        return baos.toByteArray();
    }




    // if permission is not given we get permission
    private void requestPermission() {
        ActivityCompat.requestPermissions(TakeASelfie.this,new String[]{READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
    }




    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case  R.id.btnCamera: {
                getImage(view);
                break;
            }
            case R.id.btnEmotion: {
                getEmotion(view);
                break;
            }
        }
    }


    // asynchronous class which makes the API call in the background
    private class GetEmotionCall extends AsyncTask<Void, Void, String> {


        private final ImageView img;


        GetEmotionCall(ImageView img) {
            this.img = img;
        }


        // this function is called before the API call is made
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            resultText.setText("Getting results...");
        }


        // this function is called when the API call is made
        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpclient = HttpClients.createDefault();
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {

                URIBuilder builder = new URIBuilder("https://westeurope.api.cognitive.microsoft.com/face/v1.0/detect");

                builder.setParameter("returnFaceId", "true");
                builder.setParameter("returnFaceLandmarks", "false");
                builder.setParameter("returnFaceAttributes", "emotion");


                URI uri = builder.build();
                HttpPost request = new HttpPost(uri);
                request.setHeader("Content-Type", "application/octet-stream");
                request.setHeader("Ocp-Apim-Subscription-Key", "5610a63860a84cf9a6488cf7869fba30");


                // Request body.The parameter of setEntity converts the image to base64
                request.setEntity(new ByteArrayEntity(toBase64(img)));


                // getting a response and assigning it to the string res
                HttpResponse response = httpclient.execute(request);
                HttpEntity entity = response.getEntity();
                String res = EntityUtils.toString(entity);


                return res;


            }
            catch (Exception e){
                return "null";
            }
        }


        // this function is called when we get a result from the API call
        @Override
        protected void onPostExecute(String result) {
            JSONArray jsonArray = null;
            try {
                // convert the string to JSONArray
                jsonArray = new JSONArray(result);
                String emotions = "";
                // get the scores object from the results
                for(int i = 0;i<jsonArray.length();i++) {
                    JSONObject jsonObject = new JSONObject(jsonArray.get(i).toString());
                    JSONObject eJson = jsonObject.getJSONObject("faceAttributes").getJSONObject("emotion");

                    emotions = elementToString(eJson,"anger") +
                            elementToString(eJson,"contempt") +
                            elementToString(eJson,"disgust") +
                            elementToString(eJson,"fear") +
                            elementToString(eJson,"happiness") +
                            elementToString(eJson,"neutral") +
                            elementToString(eJson,"sadness") +
                            elementToString(eJson,"surprise");
                }
                resultText.setText(emotions);


            } catch (JSONException e) {
                resultText.setText("error: " + e.getMessage() + ";   " + result);
            }
        }

        /**
         * Takes jsonObject and parses out one element of the object. The element must be double.
         * @param obj
         * @param name
         * @return string presenting the element. example: happiness: 95%
         * @throws JSONException
         */
        private String elementToString(JSONObject obj, String name) throws JSONException {
            return name + ": " + Double.toString(obj.getDouble(name)*100) + " %\n";
        }
    }

}



