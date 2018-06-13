package tn.esprit.farguita.Main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;


import tn.esprit.farguita.AboutUs.AboutUsActivity;
import tn.esprit.farguita.Favoris.FavorisFragment;
import tn.esprit.farguita.LetsEat.LetsEatActivity;
import tn.esprit.farguita.Login.LoginActivity;
import tn.esprit.farguita.Logout.LogoutActivity;
import tn.esprit.farguita.NearBy.NearByActivity;
import tn.esprit.farguita.Profile.ProfileActivity;
import tn.esprit.farguita.Promotion.PromotionFragment;
import tn.esprit.farguita.R;
import tn.esprit.farguita.TimeLine.TimeLineFragment;
import tn.esprit.farguita.TipCalculator.SerializableFragment;
import tn.esprit.farguita.TipCalculator.TipFragment;
import tn.esprit.farguita.Utils.CurrentUser;
import tn.esprit.farguita.app.AppConfig;
import tn.esprit.farguita.helper.SQLiteHandler;
import tn.esprit.farguita.helper.SessionManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private SQLiteHandler db;
    private SessionManager session;

    private TextView nameTV,emailTV;
    private ImageView profilePhotoIV;
    private SerializableFragment currentFragment;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //tip calc frag
        if(savedInstanceState!=null&&savedInstanceState.getSerializable("currentFragment")!=null) {
            this.currentFragment = (SerializableFragment) savedInstanceState.getSerializable("currentFragment");
        }
        //otherwise we assign the default value to the currentFrsgment

        setTitle("weather");





        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());
        // Fetching user details from SQLite
        HashMap<String, String> user = db.getUserDetails();
        String name = user.get("name");
        String email = user.get("email");
        String uid = user.get("uid");
        String photo = user.get("photo");


        CurrentUser cu = new CurrentUser();
        cu.cuName=name;
        cu.cuEmail=email;
        cu.cuUid=uid;
        cu.cuPhoto=photo;
        Log.d("photo", "onCreate: "+photo);


        //display name and email
        emailTV=(TextView)navigationView.getHeaderView(0).findViewById(R.id.mainProfileEmail);
        nameTV=(TextView)navigationView.getHeaderView(0).findViewById(R.id.mainProfileName);
        nameTV.setText(name);
        emailTV.setText(email);



        //display profile photo
        profilePhotoIV=(ImageView)navigationView.getHeaderView(0).findViewById(R.id.imageViewProfile);
        Log.d("XXXX", "onCreate: "+cu.cuPhoto);
        if ((cu.cuPhoto!=null)&&(cu.cuPhoto.equals("nophoto")==false))
        {
            Picasso.with(getApplicationContext()).load(AppConfig.URL_SERVER+"FarguitaServer/ProfilePhotos/"+uid+".png").into(profilePhotoIV);
        }
        WeatherFragment profileFragment = new WeatherFragment();
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.mainFrameContent,profileFragment).commit();


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_session) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));


        } else if (id == R.id.nav_nearby) {
            startActivity(new Intent(MainActivity.this, NearByActivity.class));


        } else if (id == R.id.nav_favoris) {
             setTitle("yummy");
            FavorisFragment profileFragment = new FavorisFragment();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.mainFrameContent,profileFragment).commit();



        } else if (id == R.id.nav_letseat) {
            startActivity(new Intent(MainActivity.this, LetsEatActivity.class));




        } else if (id == R.id.tip_calculator) {

            setTitle("promo");
            BlankFragment profileFragment = new BlankFragment();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.mainFrameContent,profileFragment).commit();
            currentFragment = new TipFragment();
            showFragment(currentFragment);

        } else if (id == R.id.nav_share) {
            startActivity(new Intent(MainActivity.this, AboutUsActivity.class));

           /* setTitle("timeline");
            TimeLineFragment profileFragment = new TimeLineFragment();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.mainFrameContent,profileFragment).commit();*/


        } else if (id == R.id.nav_send) {
            setTitle("promo");
            PromotionFragment profileFragment = new PromotionFragment();
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.mainFrameContent,profileFragment).commit();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showProfile(View view) {
        Log.d("My Profile", "showProfile: opening profile");
        startActivity(new Intent(MainActivity.this, LogoutActivity.class));


    }

    public void showMyProfile(View view) {
        startActivity(new Intent(MainActivity.this, ProfileActivity.class));

    }

    public void saveRatingFavCard(View view) {
    }

    private void showFragment(Fragment frag) {

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (this.findViewById(R.id.mainFrameContent)!=null) {
            ft.replace( R.id.mainFrameContent, frag);
        }
        else {
            ft.replace( R.id.mainFrameContent, frag);
        }
        ft.commit();
    }

}
