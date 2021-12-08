package ca.davidpellegrini.tipcalculatorf21;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;

public class TipCalculatorFragment extends Fragment
        implements View.OnClickListener, TextWatcher, RadioGroup.OnCheckedChangeListener{

    private int tipPercent;
    private Button decreaseButton;
    private EditText billAmountEditText;
    private RadioGroup numPeopleGroup;
    private SeekBar tipPercentSeekBar;
    private TextView tipPercentTextView, tipAmountTextView, netAmountTextView, totalPerPersonTextView;
    private LinearLayout totalPerPersonRow;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myLayout = inflater.inflate(R.layout.fragment_tip_calculator, container, false);

        tipPercent = 20;

        // Step 1. declare the Views
        // find them from the layout
        decreaseButton = myLayout.findViewById(R.id.decreaseTipButton);
        Button increaseButton = myLayout.findViewById(R.id.increaseTipButton);
        billAmountEditText = myLayout.findViewById(R.id.billAmountEditText);
        numPeopleGroup = myLayout.findViewById(R.id.numPeopleGroup);
        tipPercentSeekBar = myLayout.findViewById(R.id.tipPercentSeekBar);
        tipPercentSeekBar.setProgress(tipPercent);
        tipPercentTextView = myLayout.findViewById(R.id.tipPercentTextView);
        numPeopleGroup = myLayout.findViewById(R.id.numPeopleGroup);
        totalPerPersonRow = myLayout.findViewById(R.id.totalPerPersonRow);
        tipAmountTextView = myLayout.findViewById(R.id.tipAmountTextView);
        netAmountTextView = myLayout.findViewById(R.id.netAmountTextView);
        totalPerPersonTextView = myLayout.findViewById(R.id.totalPerPersonTextView);

        // Step 2. have the View listen for an event
        // onClick for Buttons
        // Step3. this (the Activity/Class) needs to implement the listener
        decreaseButton.setOnClickListener(this);
        increaseButton.setOnClickListener(this);
        // TextChecked for EditTexts
        //billAmountEditText.addTextChangedListener(this);
        billAmountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updateScreen();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        // let AndroidStudio help you finish the listener for the RadioGroup
        //numPeopleGroup.setOnCheckedChangeListener(this);
        /*
        AndroidStudio has another interesting way to update this code
        numPeopleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                updateScreen();
            }
        });
         */
        numPeopleGroup.setOnCheckedChangeListener((radioGroup, i) -> updateScreen());

        tipPercentTextView.setOnClickListener(this);

        tipPercentSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    Log.i("SeekBar", "Seek bar was changed");
                    tipPercent = progress;
                    updateScreen();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return myLayout;
    }

    /*
        Like onCreate, onResume is a lifecycle method called as we open the app. It is also called
        when the app comes back from being stopped (hidden), or paused. Since it gets called more
        often, this is the better place to load any of our saved data
     */
    @Override
    public void onResume() {
        super.onResume();

        // first we can retrieve any data we have by getting it from SharedPreferences
        // each get method has two parts: the KEY (or id) and a default value in case it isn't found
        String billAmountString = prefs.getString("bill_amount_pref", "");
        tipPercent = prefs.getInt("tip_percent_pref", 20);
        int numPeople = prefs.getInt("num_people_pref", 2);

        // with the saved data we can update our views
        // in these cases instead of creating a variable, I'm just accessing a View and
        // "forgetting" about it. In most cases we need a typecast to be able to access methods
        billAmountEditText.setText(billAmountString);
        // if we don't use String.valueOf here, Android will assume tipPercent is a resource ID
        tipPercentTextView.setText(String.valueOf(tipPercent));
        tipPercentSeekBar.setProgress(tipPercent);
        // for the RadioButtons, we need to figure out which Button to check
        // in this case since numPeople isn't changing, a switch will do
        switch(numPeople){
            case 1:
                numPeopleGroup.check(R.id.onePersonRadioButton);
                break;
            case 3:
                numPeopleGroup.check(R.id.threePeopleRadioButton);
                break;
            case 4:
                numPeopleGroup.check(R.id.fourPeopleRadioButton);
                break;
            // since 2 was our default value before, we can use the same code from the default case
            case 2:
            default:
                numPeopleGroup.check(R.id.twoPeopleRadioButton);
                break;
        }

        // once we're done updating our Views, we can use our handy updateScreen method
        updateScreen();
    }

    /*
        onPause, onStop, and onDestroy are the three lifecycle methods used when closing an app. In this
        case for absolute safety, I like to call the saveData method for each lifecycle method. It
        is redundant, but I have seen instances where only providing onPause wasn't enough
     */
    @Override
    public void onPause() {
        saveData();
        super.onPause();
    }

    public void onStop(){
        saveData();
        super.onStop();
    }

    public void onDestroy(){
        saveData();
        super.onDestroy();
    }

    private void saveData(){
        // in order to save any data, we need to EDIT the SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();

        // we can get any KEY we've saved, including ones from our settings page
        // here false is a default value in case "save_values_pref" can't be found
        boolean saveValues = prefs.getBoolean("save_values_pref", false);
        //Log.i("PREFS", Boolean.toString(saveValues));

        // if the user checked "Save values"
        if(saveValues) {

            // collect any input the user has entered
            int checkedID = numPeopleGroup.getCheckedRadioButtonId();
            int numPeople = 2;
            // here I'm using an if instead of a switch since the IDs can change
            // with variable data, the if statement is technically more efficient
            if(checkedID == R.id.onePersonRadioButton)
                numPeople = 1;
                // we don't technically need this check since it's our default
//            else if(checkedID == R.id.twoPeopleRadioButton)
//                numPeople = 2;
            else if(checkedID == R.id.threePeopleRadioButton)
                numPeople = 3;
            else if(checkedID == R.id.fourPeopleRadioButton)
                numPeople = 4;

            // we can add KEYS and VALUES to the SharedPreferences
            editor.putString("bill_amount_pref", billAmountEditText.getText().toString());
            editor.putInt("num_people_pref", numPeople);
            editor.putInt("tip_percent_pref", tipPercent);
        }
        else{ //if the user doesn't want to save the data
            editor.clear(); //we should clear any saved values
            //then save the fact that we shouldn't save data
            editor.putBoolean("save_values_pref", false);
        }

        // putting data isn't enough, we also need to commit or apply our changes
        //editor.commit(); // commit is a synchronous task, so it could take some time
        editor.apply(); //apply is asynchronous so it is often preferred
    }

    /**
     We're using the main class onClickListener for now. This onClick method will handle
     any View presses, and will update the rest of the information on the screen
     */
    @Override
    public void onClick(View view) {
        //depending on which Button is clicked, increase or decrease the float value

        /*
        IDs aren't great to use for switch statements so I'll switch this to an if statement
        switch (view.getId()){
            case R.id.decreaseTipButton:
                tipPercent -= 5;
                if(tipPercent <= 0){
                    tipPercent = 0;
                }
                break;
            case R.id.increaseTipButton:
                tipPercent += 5;

                break;
            case R.id.tipPercentTextView:
                Toast.makeText(this, "You found the Easter Egg!", Toast.LENGTH_LONG).show();
                break;
            default:
                Log.e("ClickEvent", "You clicked the wrong button");
                break;
        }
        */
        int buttonID = view.getId();
        if(buttonID == R.id.decreaseTipButton){
            tipPercent -= 5;
            if(tipPercent <= 0)
                tipPercent = 0;
        }
        else if(buttonID == R.id.increaseTipButton)
            tipPercent += 5;
        else if(buttonID == R.id.tipPercentTextView)
            //when the this keyword references the Activity, change it to getActivity() to reference the parent Activity
            Toast.makeText(getActivity(), "You found the Easter Egg!", Toast.LENGTH_LONG).show();
        else
            Log.e("ClickEvent", "You clicked the wrong button");

        // how do I access the SeekBar?
        tipPercentSeekBar.setProgress(tipPercent, true);

        // refactored all of the screen updates into one method that every event can access
        updateScreen();
    }

    /**
     TextChangedListener - we don't need to track any of the data that's being changed before or
     after the text is updated, we only care about the change event
     */
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // we don't care about the value before
    }
    @Override
    public void afterTextChanged(Editable editable) {
        // we don't need to do anything after the value changes
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // as the user enters text, we can update the rest of the information on the screen
        updateScreen();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        if(radioGroup.getId() == R.id.numPeopleGroup){
            updateScreen();
        }
    }

    /**
     * A refactored method that will allow us to repeat the core functions of the app outside of
     * the event listeners.
     * 1. retrieve the bill amount data
     * 2. calculate the tip, and the net (total) amount
     * 3. retrieve the number of people splitting the bill
     * 4. calculate the amount each person will be paying
     * 5. update any TextViews
     */
    public void updateScreen(){
        /*
        AndroidStudio has an interesting optimization for us here!
        if(tipPercent == 0){
            decreaseButton.setEnabled(false);
        }
        else{
            decreaseButton.setEnabled(true);
        }
         */
        decreaseButton.setEnabled(tipPercent != 0);

        // convert the TextView into a usable float value
        // update the tip percent TextView
        // using NumberFormat will make sure that we always have the right percent format
        NumberFormat percent = NumberFormat.getPercentInstance();
        tipPercentTextView.setText(percent.format(tipPercent/100f));

        // "collect the bill from the user"
        String billAmountString = String.valueOf(billAmountEditText.getText());
        if(billAmountString.isEmpty()){
            billAmountString = "0";
        }
        float billAmount = Float.parseFloat(billAmountString);

        // calculate the tip
        float tipAmount = billAmount * (tipPercent / 100f);
        // calculate the total amount of the bill with the tip
        float netAmount = billAmount + tipAmount;
//        Log.d("tipPercent", String.valueOf(tipPercent));
//        Log.d("tipAmount", String.valueOf(tipAmount));

        // figure out how many people are splitting the bill

        int numPeople;

        totalPerPersonRow.setVisibility(View.VISIBLE);

        /*
        IDs aren't great to use for switch statements so I'll switch this to an if statement
        switch(numPeopleGroup.getCheckedRadioButtonId()){
            case R.id.onePersonRadioButton: //if(checkedButton == R.id.onePersonRadioButton)
                numPeople = 1;
                totalPerPersonRow.setVisibility(View.GONE);
                break;
            case R.id.threePeopleRadioButton: //else if(checkedButton == R.id.threePeopleRadioButton)
                numPeople = 3;
                break;
            case R.id.fourPeopleRadioButton: //else if(checkedButton == R.id.fourPeopleRadioButton)
                numPeople = 4;
                break;
            case R.id.twoPeopleRadioButton: //else if(checkedButton == R.id.twoPeopleRadioButton)
            default: // else
                numPeople = 2;
        }
        */

        int numPeopleID = numPeopleGroup.getCheckedRadioButtonId();
        // we don't actually need curly braces, they just help make sure we have less bugs
        if(numPeopleID == R.id.onePersonRadioButton){
            numPeople = 1;
            totalPerPersonRow.setVisibility(View.GONE);
        }
        else if(numPeopleID == R.id.threePeopleRadioButton)
            numPeople = 3;
        else if(numPeopleID == R.id.fourPeopleRadioButton)
            numPeople = 4;
        else
            numPeople = 2;

        //calculate total per person
        float totalPerPersonAmount = netAmount / numPeople;

        NumberFormat currency = NumberFormat.getCurrencyInstance();
        // using NumberFormat we can make sure we always have the right currency format
        // update the TextViews

        // format lets Java take care of the formatting instead of us
        tipAmountTextView.setText(currency.format(tipAmount));
        // format lets Java take care of the formatting instead of us
        netAmountTextView.setText(currency.format(netAmount));
        // format lets Java take care of the formatting instead of us
        totalPerPersonTextView.setText(currency.format(totalPerPersonAmount));
    }
}