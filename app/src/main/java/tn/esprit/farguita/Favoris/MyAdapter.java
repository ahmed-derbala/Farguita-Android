package tn.esprit.farguita.Favoris;

/**
 * Created by ahmed on 12/23/17.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tn.esprit.farguita.Models.Restaurant;
import tn.esprit.farguita.R;
import tn.esprit.farguita.app.AppConfig;
import tn.esprit.farguita.app.AppController;

import static tn.esprit.farguita.Utils.CurrentUser.cuUid;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private ArrayList<Restaurant> mDataset;
    public static String googlePlacesPhotoUrl="https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=";
    public static Context p;
    public String TAG="myadapter";
    private static ProgressDialog pDialog;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public CardView mCardView;
        public TextView mPlaceTitle;
        public TextView mPlaceType;
        public TextView mAddress;
        public TextView mTelephone;
        public ImageView mPhoto;
        public RatingBar mRatingbar;
        public EditText mComment;
        public Button mSaveRating;



        public MyViewHolder(View v) {
            super(v);
            mCardView = (CardView) v.findViewById(R.id.card_view);
            mPlaceTitle = (TextView) v.findViewById(R.id.placeTitle_favcard);
            mPlaceType = (TextView) v.findViewById(R.id.placeType_favcard);
            mAddress = (TextView) v.findViewById(R.id.address_favcard);
            mTelephone = (TextView) v.findViewById(R.id.telephone_favcard);
            mPhoto = (ImageView) v.findViewById(R.id.fav_photo);
            //mRatingbar = (RatingBar) v.findViewById(R.id.rateBar_favcard);
            mComment = (EditText) v.findViewById(R.id.comment_favcard);
            mSaveRating = (Button) v.findViewById(R.id.saveRating_favcard);
            pDialog = new ProgressDialog(v.getContext());






        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(ArrayList<Restaurant> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v);
        p=parent.getContext();
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Picasso.with(p).load(googlePlacesPhotoUrl+mDataset.get(position).getPhoto()+"&key="+AppConfig.API_KEY).into(holder.mPhoto);
        holder.mPlaceTitle.setText(mDataset.get(position).getPlaceTitle());
        holder.mAddress.setText(mDataset.get(position).getAddress());
        holder.mPlaceType.setText(mDataset.get(position).getPlaceType());
        if (mDataset.get(position).getTelephone()==null)
        {
            holder.mTelephone.setText("");

        }
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentValue = mDataset.get(position).toString();
                Log.d("CardView", "CardView Clicked: " + currentValue+"palceid="+mDataset.get(position).getPlaceId());
            }
        });

        holder.mSaveRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ajouterComment(mDataset.get(position).getPlaceId(),cuUid,holder.mComment.getText().toString());

            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    private void ajouterComment(final String placeId, final String userId, final String commentText) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";
        Log.d("myadapter", "ajouterFavoris: ENTERING FAVORIS");
        pDialog.setMessage("Commenting ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_AJOUTER_COMMENT, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: ENTERING ON RESPONSE");
                Log.d(TAG, "comment Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Log.d(TAG, "onResponse: ENTERING ERROR");
                        Toast.makeText(p, jObj.getString("error_msg"), Toast.LENGTH_LONG).show();

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(p,
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Favoris Error: " + error.getMessage());
                // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(p, "you should log in first", Toast.LENGTH_LONG).show();

                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("placeId", placeId);
                params.put("userId", cuUid);
                params.put("commentText", commentText);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}