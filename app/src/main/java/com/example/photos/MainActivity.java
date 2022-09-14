package com.example.photos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private int PERMISSION_CODE = 1;
    private RelativeLayout homeRL;
    private TextView titleTV, albumTV, photoIdTV;
    private TextInputEditText idEdit;
    private ImageView photoIV, searchIV, photoIVsmall;
    private Button saveButton;
    String url = "https://jsonplaceholder.typicode.com/photos/";
    private String photoId = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        homeRL = findViewById(R.id.idRLHome);
        titleTV = findViewById(R.id.idTVTitle);
        albumTV = findViewById(R.id.idTVAlbum);
        photoIdTV = findViewById(R.id.idTVid);
        idEdit = findViewById(R.id.idEditId);
        photoIV = findViewById(R.id.idIVphoto);
        photoIVsmall = findViewById(R.id.idIVPhotoSmall);
        searchIV = findViewById(R.id.idIVSearch);
        saveButton = findViewById(R.id.saveButton);


        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoId = idEdit.getText().toString();
                if (photoId.equals("0")) {
                    Toast.makeText(MainActivity.this, "Enter photo id", Toast.LENGTH_SHORT).show();
                } else {
                    new GetDataFromURL().execute(url);
                }
            }
        });
    }

    private class GetDataFromURL extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null)
                    connection.disconnect();
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONArray obj = new JSONArray(result);
                JSONObject response = obj.getJSONObject(Integer.parseInt(photoId) - 1);
                String photoId = response.getString("id");
                photoIdTV.setText(photoId);

                String albumId = response.getString("albumId");
                albumTV.setText(albumId);

                String photoIcon = response.getString("url");
                Picasso.get().load(photoIcon).into(photoIV);

                String photoIcon2 = response.getString("thumbnailUrl");
                Picasso.get().load(photoIcon2).into(photoIVsmall);

                String title = response.getString("title");
                titleTV.setText(title);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

        public void save(View view) {

            try {
                Drawable drawable = photoIV.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                File file;
                String path = Environment.getExternalStorageDirectory().toString();

                file = new File(path, "photo1" + ".jpg");
                OutputStream stream = null;
                stream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                stream.flush();
                stream.close();
                Toast.makeText(this, "Изображение сохранено", Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }


    }