package com.example.fetchtest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String imagesJSON;

    private static final String JSON_ARRAY ="result";
    private static final String IMAGE_URL = "url";

    private JSONArray arrayImages= null;

    private int TRACK = 0;

    private static final String IMAGES_URL = "http://192.168.1.72/database/getfiles.php";

    private Button buttonMoveNext;
    private Button buttonMovePrevious;
    private ImageView imageView;
    private Button buttonFetchImages;
    TextView txtException;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtException = (TextView) findViewById(R.id.ExceptionMsg);
        imageView = (ImageView) findViewById(R.id.imageView);
        buttonFetchImages = (Button) findViewById(R.id.buttonFetchImages);
        buttonMoveNext = (Button) findViewById(R.id.buttonNext);
        buttonMovePrevious = (Button) findViewById(R.id.buttonPrev);
        buttonFetchImages.setOnClickListener(this);
        buttonMoveNext.setOnClickListener(this);
        buttonMovePrevious.setOnClickListener(this);
    }
    private void extractJSON(){
        try {
            JSONObject jsonObject = new JSONObject(imagesJSON);
            arrayImages = jsonObject.getJSONArray(JSON_ARRAY);
            System.out.println("Here here here");
        } catch (JSONException e) {
//            System.out.println("tHere there there there there there there there there there");
            e.printStackTrace();
        }
    }

    private void showImage(){
        try {
            JSONObject jsonObject = arrayImages.getJSONObject(TRACK);

            getImage(jsonObject.getString(IMAGE_URL));
            System.out.println("Show show show show Show show show show Show show show show Show show show show Show show show show");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void moveNext(){
        if(TRACK < arrayImages.length()){
            TRACK++;
            showImage();
        }
    }

    private void movePrevious(){
        if(TRACK>0){
            TRACK--;
            showImage();
        }
    }




    private void getAllImages() {
        class GetAllImages extends AsyncTask<String,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this, "Fetching Data","Please Wait...",true,true);
            }

            @Override
            protected void onPostExecute(String s) {

//                super.onPostExecute(s);
//                loading.dismiss();                    // this is used to cancel the loading icon, OK
//                System.out.println("TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST TEST");

////                Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
                imagesJSON = s;
                extractJSON();
                showImage();
            }


            @Override
            protected String doInBackground(String... params) {
                String uri = params[0];
                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");
                    }

                    return sb.toString().trim();
                }catch(Exception e){
                    System.out.println(e.toString());
                    txtException.setText(e.toString());
                    return "From exception!!";

//                    return null;
                }
            }
        }
        GetAllImages gai = new GetAllImages();
        gai.execute(IMAGES_URL);
    }
    private void getImage(String urlToImage){  //take a string as a parameter. The string would have the url to image extracted from json array.
        class GetImage extends AsyncTask<String,Void, Bitmap>{
            ProgressDialog loading;
            @Override
            protected Bitmap doInBackground(String... params) {
                URL url = null;
                Bitmap image = null; // bitmap all images that is shown in the  android app

                String urlToImage = params[0];
                try {
                    url = new URL(urlToImage);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                    System.out.println("image image image image image image image image image image image image image image image image image image image image image image image image image");
                } catch (MalformedURLException e) { // this happens when the url is invalid
                    e.printStackTrace();
                    System.out.println("The URL is invalid");
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                 return "Image";
                System.out.println("image image image image image image image image image image image image");
                return image;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this,"Downloading Image...","Please wait...",true,true);
                System.out.println("Downloading Downloading Downloading Downloading Downloading Downloading Downloading Downloading");
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                loading.dismiss();
                imageView.setImageBitmap(bitmap);
                System.out.println("Now now now now now");
            }
        }
        GetImage gi = new GetImage();
        gi.execute(urlToImage);
    }

    @Override
    public void onClick(View v) {
        if(v == buttonFetchImages) {
            getAllImages();
        }
        if(v == buttonMoveNext){
            moveNext();
        }
        if(v== buttonMovePrevious){
            movePrevious();
        }
    }}
