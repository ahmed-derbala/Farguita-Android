package tn.esprit.farguita.TipCalculator;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;

/**
 * This classes serves as a Serializable and Parcelable parent for all fragments.
 * @author pishchalov 1430169
 * @since 2016/12/08
 */

@SuppressLint("ParcelCreator")
public class SerializableFragment extends Fragment implements Serializable, Parcelable {

    private String TAG = "SerializableFragment";

    public SerializableFragment() {
        super();
        // Required empty public constructor
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Log.d(TAG, "");
    }
}