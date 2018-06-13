package tn.esprit.farguita.Favoris;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tn.esprit.farguita.Models.Restaurant;
import tn.esprit.farguita.NearBy.NearByActivity;
import tn.esprit.farguita.Profile.ProfileActivity;
import tn.esprit.farguita.R;
import tn.esprit.farguita.app.AppConfig;
import tn.esprit.farguita.app.AppController;
import tn.esprit.farguita.helper.JsonArrayPostRequest;

import static tn.esprit.farguita.Utils.CurrentUser.cuName;
import static tn.esprit.farguita.app.AppConfig.URL_GET_FAVORIS_BY_USERNAME;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavorisFragment extends Fragment {
    JsonArrayPostRequest jsonArrayRequest ;
    RecyclerView rv;
    public static ArrayList<Restaurant> DataAdapterClassList = new ArrayList<Restaurant>();
    public static MyAdapter adapter = new MyAdapter(DataAdapterClassList);
    private ProgressDialog pDialog;



    public FavorisFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Progress dialog
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
        pDialog.setMessage("Loading bookmarks ...");
        pDialog.show();

        Log.d("FAVORIFRAGMENT", "INTERING ON CREATE VIEW: ");

        getFavorisByUsername(cuName);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favoris, container, false);
        pDialog.dismiss();
         rv = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        DataAdapterClassList.clear();
        rv.setAdapter(adapter);

        //swipe delete
        SwipeableRecyclerViewTouchListener swipeTouchListener =
                new SwipeableRecyclerViewTouchListener(rv,
                        new SwipeableRecyclerViewTouchListener.SwipeListener() {
                            @Override
                            public boolean canSwipeLeft(int position) {
                                return true;
                            }

                            @Override
                            public boolean canSwipeRight(int position) {
                                return true;
                            }

                            @Override
                            public void onDismissedBySwipeLeft(RecyclerView rv, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    Toast.makeText(getContext(), DataAdapterClassList.get(position).getIdfav().toString(), Toast.LENGTH_SHORT).show();

                                    deleteFavoris(DataAdapterClassList.get(position).getIdfav());

//                                    Toast.makeText(MainActivity.this, mItems.get(position) + " swiped left", Toast.LENGTH_SHORT).show();
                                    DataAdapterClassList.remove(position);

                                    adapter.notifyItemRemoved(position);

                                }
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onDismissedBySwipeRight(RecyclerView rv, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    Log.d("**IDFAV", "onDismissedBySwipeRight: "+DataAdapterClassList.get(position).getIdfav());
                                    deleteFavoris(DataAdapterClassList.get(position).getIdfav());

//                                    Toast.makeText(MainActivity.this, mItems.get(position) + " swiped right", Toast.LENGTH_SHORT).show();
                                    DataAdapterClassList.remove(position);
                                    adapter.notifyItemRemoved(position);

                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
        rv.addOnItemTouchListener(swipeTouchListener);

        //end swipe delete
        return rootView;
    }



    private void getFavorisByUsername(final String username)
    {
        Log.d("getfavorisbyusername", "getFavorisByUsername: ");
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        jsonArrayRequest = new JsonArrayPostRequest(URL_GET_FAVORIS_BY_USERNAME,
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
                                GetDataAdapter2.setPlaceType(json.getString("placeType"));
                                GetDataAdapter2.setPhoto(json.getString("photo"));
                                GetDataAdapter2.setTelephone(json.getString("telephone"));
                                GetDataAdapter2.setPlaceId(json.getString("placeId"));
                                GetDataAdapter2.setIdfav(json.getString("id"));



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
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
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

    //delete fragment
  /*  private void deleteFavoris(final String id) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";
        //pDialog.setMessage("Registering ...");
       // showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_AJOUTER_FAVORIS, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.d("del", "onResponse: ENTERING ON RESPONSE");
                Log.d("del", "Favoris Response: " + response.toString());
               // hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Log.d("dele", "onResponse: ENTERING ERROR");
                        Toast.makeText(getContext(), jObj.getString("error_msg"), Toast.LENGTH_LONG).show();

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("del", "Favoris Error: " + error.getMessage());
                // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(), "you should log in first", Toast.LENGTH_LONG).show();

               // hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                Toast.makeText(getContext(), "*************"+id, Toast.LENGTH_LONG).show();

                params.put("id", id);






                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    //end delete fragment
    */



          private void deleteFavoris(final String id) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SUPPRIMER_FAVORIS, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {


                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Toast.makeText(getContext(), jObj.getString("error_msg"), Toast.LENGTH_LONG).show();

                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                Toast.makeText(getActivity(), "you should log in first", Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id",id);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}