package com.example.jsoupdemo;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;

import androidx.appcompat.app.AppCompatActivity;

//A small program to grab paragraphs and pictures from a given website and copy them into your app.

public class MainActivity extends AppCompatActivity {

    private EditText getURL;
    private String url;
    private Button getButton;
    private TextView result;
    private ImageView getImage;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getURL = findViewById(R.id.getURL);
        result = findViewById(R.id.result);
        result.setMovementMethod(new ScrollingMovementMethod());
        getButton = findViewById(R.id.getButton);
        getImage = findViewById(R.id.image);

        getButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                getWebsite();
            }
        });
    }

    //The meat and potatoes
    private void getWebsite() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();

                try {
                    url  = getURL.getText().toString();

                    //Part of Jsoup - points towards the website we are extracting from.
                    Document document = Jsoup.connect("https://"+url).get();

                    String title = document.title(); //pulls website title

                    Elements data = document.select("p"); //scans website html for content using the paragraph tag.
                    Element image = document.select("img").first(); //looks for first image tag and stores it as element
                    String imgSrc = image.absUrl("src"); //looks inside image element for the source
                    InputStream input = new java.net.URL(imgSrc).openStream(); //downloads image from website.
                    bitmap = BitmapFactory.decodeStream(input); //Creates the bitmap;

                    builder.append(title).append("\n");
                    for (Element i : data) {
                        builder.append(i.text()).append("\n");
                    }
                } catch (IOException e) {
                    builder.append("Error: ").append(e.getMessage());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getImage.setImageBitmap(bitmap);
                        result.setText(builder.toString());
                    }
                });
            }
        }).start();
    }
}