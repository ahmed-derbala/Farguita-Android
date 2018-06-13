package tn.esprit.farguita.Profile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tn.esprit.farguita.Favoris.MyAdapter;
import tn.esprit.farguita.Models.Restaurant;
import tn.esprit.farguita.R;
import tn.esprit.farguita.Utils.CurrentUser;
import tn.esprit.farguita.Utils.VolleyMultipartRequest;
import tn.esprit.farguita.app.AppConfig;
import tn.esprit.farguita.app.AppController;
import tn.esprit.farguita.helper.JsonArrayPostRequest;

import static tn.esprit.farguita.Utils.CurrentUser.cuEmail;
import static tn.esprit.farguita.Utils.CurrentUser.cuName;
import static tn.esprit.farguita.Utils.CurrentUser.cuUid;
import static tn.esprit.farguita.app.AppConfig.URL_GET_USER_COMMENTS;
import static tn.esprit.farguita.app.AppConfig.URL_SEND_PROFILE_PHOTO;

public class ProfileActivity extends AppCompatActivity {
    private ImageView profile_photo;
    private EditText profile_name,profile_email;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    JsonArrayPostRequest jsonArrayRequest ;
    RecyclerView rv;
    public static ArrayList<Restaurant> DataAdapterClassList = new ArrayList<Restaurant>();
    public static MyAdapter adapter = new MyAdapter(DataAdapterClassList);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("My Profile");
        CurrentUser cu = new CurrentUser();

        profile_photo = (ImageView) findViewById(R.id.profile_photo);
        if ((cu.cuPhoto != null) && (cu.cuPhoto.equals("nophoto") == false)) {
            Picasso.with(this).load(AppConfig.URL_SERVER + "FarguitaServer/ProfilePhotos/" + cuUid + ".png").into(profile_photo);
        }
        profile_name = (EditText) findViewById(R.id.profile_Name);
        profile_name.setText(cuName);
        profile_email = (EditText) findViewById(R.id.profile_Email);
        profile_email.setText(cuEmail);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        /*
 * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
 * performs a swipe-to-refresh gesture.
 */
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i("swipetriggered", "onRefresh called from SwipeRefreshLayout");

                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        startActivity(new Intent(ProfileActivity.this,ProfileActivity.class));
                        finish();
                    }
                }
        );

        getUserComments(cuUid);
        // Inflate the layout for this fragment
        rv = (RecyclerView) findViewById(R.id.myprofilecomments_recycler_view);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        rv.setLayoutManager(llm);
        DataAdapterClassList.clear();

    }





    public void changeProfilePhoto(View view) {
        Log.d("profileActivity", "changeProfilePhoto: photo clicked ");
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Changing profile photo")
                .setMessage("How you want to update your photo ?")
                .setPositiveButton("take photo", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //TAKE PHOTO WITH USER CAMERA
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                        }
                        Picasso.with(getApplicationContext()).load(AppConfig.URL_SERVER+"FarguitaServer/ProfilePhotos/"+cuUid+".png").into(profile_photo);


                    }
                })
                .setNegativeButton("upload from gallery", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //open gallery
                        //checking the permission
                        //if the permission is not given we will open setting to add permission
                        //else app will not open
                        Log.d("gallery", "onClick: gellery clicked");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.parse("package:" + getPackageName()));
                            finish();
                            startActivity(intent);
                            return;
                        }

                        //if everything is ok we will open image chooser
                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, 100);


                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        Picasso.with(this).load(AppConfig.URL_SERVER+"FarguitaServer/ProfilePhotos/"+cuUid+".png").into(profile_photo);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //from user camera
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.d("activityok", "activity result");

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            profile_photo.setImageBitmap(imageBitmap);
            //calling the method uploadBitmap to upload image
            uploadBitmap(imageBitmap);
            Log.d("activityresult", "onActivityResult: END");
        }

        //from gallery
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            //getting the image Uri
            Uri imageUri = data.getData();
            try {
                //getting bitmap object from uri
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                //displaying selected image to imageview
                profile_photo.setImageBitmap(bitmap);

                //calling the method uploadBitmap to upload image
                uploadBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void uploadBitmap(final Bitmap bitmap) {

        //getting the tag from the edittext
        final String tags = cuUid;

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST,URL_SEND_PROFILE_PHOTO,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
            * If you want to add more parameters with the image
            * you can do it here
            * here we have only one parameter with the image
            * which is tags
            * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tags", tags);
                return params;
            }

            /*
            * Here we are passing image by renaming it with a unique name
            * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                //long imagename = System.currentTimeMillis();
                params.put("pic", new DataPart(cuUid + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);

    }
    /*
    * The method is taking Bitmap as an argument
    * then it will return the byte[] array for the given bitmap
    * and we will send this array to the server
    * here we are using PNG Compression with 80% quality
    * you can give quality between 0 to 100
    * 0 means worse quality
    * 100 means best quality
    * */

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void getUserComments(final String username)
    {
        Log.d("getfavorisbyusername", "getFavorisByUsername: ");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", username);
        jsonArrayRequest = new JsonArrayPostRequest(URL_GET_USER_COMMENTS,
                new Response.Listener<JSONArray>()
                {

                    @Override
                    public void onResponse(JSONArray array) {
                        for(int i = 0; i<array.length(); i++) {
                            Restaurant GetDataAdapter2 = new Restaurant();

                            JSONObject json = null;
                            try {
                                json = array.getJSONObject(i);
                                GetDataAdapter2.setPlaceTitle(json.getString("placeTitle"));
                                GetDataAdapter2.setAddress(json.getString("address"));
                                GetDataAdapter2.setPlaceType(json.getString("commentText"));
                                GetDataAdapter2.setPhoto(json.getString("photo"));
                                GetDataAdapter2.setTelephone(json.getString("telephone"));
                                GetDataAdapter2.setPlaceId(json.getString("placeId"));


                            }
                            catch (JSONException e)
                            {

                                e.printStackTrace();
                            }

                            DataAdapterClassList.add(GetDataAdapter2);
                            rv.setAdapter(adapter);
                        }
                    }
                }, new Response.ErrorListener()
        {

            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("PLACETIITLE", "onResponse: "+error.getMessage());
            }
        },params);

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonArrayRequest, "hhh");
    }
    public void clear() {
        int size = this.DataAdapterClassList.size();
        this.DataAdapterClassList.clear();
        adapter.notifyItemRangeRemoved(0, size);
    }
}
