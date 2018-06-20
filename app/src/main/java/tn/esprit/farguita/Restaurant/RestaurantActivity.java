package tn.esprit.farguita.Restaurant;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import tn.esprit.farguita.Models.Restaurant;
import tn.esprit.farguita.R;

public class RestaurantActivity extends AppCompatActivity {
    private TextView restaurantName,restaurantDistance,restaurantPhone,restaurantAddress;
    private ImageView restaurantImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        restaurantName = findViewById(R.id.restaurantName);
        restaurantDistance = findViewById(R.id.distance);
       // restaurantPhone = findViewById(R.id.location);
        restaurantAddress = findViewById(R.id.detail_res_address);
        restaurantImage = findViewById(R.id.icon);

        restaurantName.setText( getIntent().getStringExtra("restaurantName"));
        restaurantDistance.setText( getIntent().getStringExtra("distance"));
        restaurantPhone.setText( getIntent().getStringExtra("phone"));
        restaurantAddress.setText( getIntent().getStringExtra("address"));
        Picasso.with(getApplicationContext()).load(getIntent().getStringExtra("photo"))
                .placeholder(R.drawable.placeholder).resize(100, 100).into(restaurantImage);


    }

    public void Callrestaurant(View view) {
        // Use format with "tel:" and phone number to create phoneNumber.
        String phoneNumber = String.format("tel: %s",
                getIntent().getStringExtra("phone"));
        // Create the intent.
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        // Set the data for the intent as the phone number.
        dialIntent.setData(Uri.parse(phoneNumber));
        // If package resolves to an app, send intent.
        if (dialIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(dialIntent);
        } else {
            Log.e("kk", "Can't resolve app for ACTION_DIAL Intent.");
        }
       /* Log.d("HHHHH","CCCCCCCCCCCCC");
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:0377778888"));

        if (ContextCompat.checkSelfPermission(RestaurantActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RestaurantActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
        }
        startActivity(callIntent);*/

    }

}
