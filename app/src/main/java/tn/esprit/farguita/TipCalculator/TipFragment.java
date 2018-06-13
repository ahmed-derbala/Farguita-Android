package tn.esprit.farguita.TipCalculator;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

import tn.esprit.farguita.R;

import static java.lang.Math.abs;


/**
 * This fragment contains complete tip calculator functionality
 * @author pishchalv 1430169
 * @since 2016.12.01
 */
@SuppressLint("ParcelCreator")
public class TipFragment extends SerializableFragment implements Serializable {


    private View view;

    public TipFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle saveBundle) {
        super.onCreate(saveBundle);
    }


    /**
     * Collects data input from the fields
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_tip, container, false);
        view.findViewById(R.id.radioButton10).setOnClickListener(rbClickListener);
        view.findViewById(R.id.radioButton15).setOnClickListener(rbClickListener);
        view.findViewById(R.id.radioButton20).setOnClickListener(rbClickListener);
        view.findViewById(R.id.calculateButton).setOnClickListener(calculateClickListener);
        return view;
    }

    /**
     * Radio button seter for tips
     */
    private View.OnClickListener rbClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            EditText et = (EditText) v.getRootView().findViewById(R.id.tipsInput);

            if (v.getId() == R.id.radioButton10) {
                et.setText(Double.toString(10.0));
            }
            if (v.getId() == R.id.radioButton15) {
                et.setText(Double.toString(15.0));
            }
            if (v.getId() == R.id.radioButton20) {
                et.setText(Double.toString(20.0));

            }
        }
    };

    /**
     * Handlse the user click on the calculate button, display result of the calculation
     */
    private View.OnClickListener calculateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText tips = (EditText) v.getRootView().findViewById(R.id.tipsInput);
            EditText billInput = (EditText) v.getRootView().findViewById(R.id.billInput);
            EditText number = (EditText) v.getRootView().findViewById(R.id.persInput);

            ArrayList<Double> results = validateAndCalculate(tips, billInput, number);

            TextView tipsTotal = (TextView) v.getRootView().findViewById(R.id.tipsOutput);
            TextView billTotal = (TextView) v.getRootView().findViewById(R.id.totalOutput);
            TextView billPerCap = (TextView) v.getRootView().findViewById(R.id.ouputPerCap);
            tipsTotal.setText(results.get(0).toString());
            billTotal.setText(results.get(1).toString());
            billPerCap.setText(results.get(2).toString());
        }
    };

    /**
     * Validates the input and calculates result
     * @param tips
     * @param billInput
     * @param number
     * @return
     */
    private ArrayList<Double> validateAndCalculate(EditText tips, EditText billInput, EditText number) {
        double tipsPerc = 0.0;
        double billNet = 0.0;
        int persons = 1;
        try {
            tipsPerc = abs(Double.parseDouble(tips.getText().toString()));
            billNet = abs(Double.parseDouble(billInput.getText().toString()));
            persons = abs(Integer.parseInt(number.getText().toString()));
        }
        catch(Exception e) {
            //Reference: http://stackoverflow.com/questions/2115758/how-do-i-display-an-alert-dialog-on-android
            new AlertDialog.Builder(this.getActivity())
                    .setTitle(R.string.error)
                    .setMessage(R.string.invalidInput)
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }}).show();
        }
        ArrayList<Double> results = new ArrayList<>();
        results.add(Math.round(tipsPerc*billNet)/100.0);
        results.add(Math.round(tipsPerc*billNet+billNet*100)/100.0);
        results.add(Math.round((tipsPerc*billNet+billNet*100)/persons)/100.0);
        return results;
    }


}
