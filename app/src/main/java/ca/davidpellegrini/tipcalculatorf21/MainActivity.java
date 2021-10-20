package ca.davidpellegrini.tipcalculatorf21;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
    //Step 3.
        implements View.OnClickListener, TextWatcher, RadioGroup.OnCheckedChangeListener {

    private int tipPercent = 20;
    private float netAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Step 1. declare the Views
        // find them from the layout
        Button decreaseButton = findViewById(R.id.decreaseTipButton);
        Button increaseButton = findViewById(R.id.increaseTipButton);
        EditText billAmountEditText = findViewById(R.id.billAmountEditText);
        RadioGroup numPeopleGroup = findViewById(R.id.numPeopleGroup);
        SeekBar tipPercentSeekBar = findViewById(R.id.tipPercentSeekBar);
        tipPercentSeekBar.setProgress(tipPercent);
        TextView tipPercentTextView = findViewById(R.id.tipPercentTextView);

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
        numPeopleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                updateScreen();
            }
        });
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
    }


//    public static void setTipPercent(float tip){
//        tipPercent = tip;
//    }
//
//    public static float getTipPercent(){ return tipPercent; }

    // Step 4. Define what the listener does
    // in our case it's update the tip percent, then do some math and update the display
    @Override
    /**
        We're using the main class onClickListener for now. This onClick method will handle
        any View presses, and will update the rest of the information on the screen
     */
    public void onClick(View view) {
        //depending on which Button is clicked, increase or decrease the float value

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

        // how do I access the SeekBar?
        SeekBar tipPercentSeekBar = findViewById(R.id.tipPercentSeekBar);
        tipPercentSeekBar.setProgress(tipPercent, true);

        // refactored all of the screen updates into one method that every event can access
        updateScreen();
    }

    /**
        TextChganedListener - we don't need to track any of the data that's being changed before or
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
        Button decreaseButton = findViewById(R.id.decreaseTipButton);
        if(tipPercent == 0){
            decreaseButton.setEnabled(false);
        }
        else{
            decreaseButton.setEnabled(true);
        }
        // convert the TextView into a useable float value
        TextView tipPercentTV = findViewById(R.id.tipPercentTextView);
        // update the tip percent TextView
        tipPercentTV.setText(String.valueOf(tipPercent));

        // "collect the bill from the user"
        EditText billAmountEditText = findViewById(R.id.billAmountEditText);
        String billAmountString = String.valueOf(billAmountEditText.getText());
        if(billAmountString.isEmpty()){
            billAmountString = "0";
        }
        float billAmount = Float.parseFloat(billAmountString);

        // calculate the tip
        float tipAmount = billAmount * (tipPercent / 100f);
        // calculate the total amount of the bill with the tip
        netAmount = billAmount + tipAmount;
//        Log.d("tipPercent", String.valueOf(tipPercent));
//        Log.d("tipAmount", String.valueOf(tipAmount));

        // figure out how many people are splitting the bill
        RadioGroup numPeopleGroup = findViewById(R.id.numPeopleGroup);
        int numPeople = 2;
        LinearLayout totalPerPersonRow = findViewById(R.id.totalPerPersonRow);
        totalPerPersonRow.setVisibility(View.VISIBLE);

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
        // we don't actually need curly braces, they just help make sure we have less bugs
        // we could also use a switch statement instead
//        int checkedButton = numPeopleGroup.getCheckedRadioButtonId(); //this gives the ID of the View
//        if(checkedButton == R.id.onePersonRadioButton) {
//            numPeople = 1;
//        }
//        else if(checkedButton == R.id.twoPeopleRadioButton) {
//            numPeople = 2;
//        }
//        else if(checkedButton == R.id.threePeopleRadioButton) {
//            numPeople = 3;
//        }
//        else if(checkedButton == R.id.fourPeopleRadioButton) {
//            numPeople = 4;
//        }

        //calculate total per person
        float totalPerPersonAmount = netAmount / numPeople;

        // update the TextViews
        TextView tipAmountTextView = findViewById(R.id.tipAmountTextView);
        tipAmountTextView.setText("$"+ tipAmount);
        TextView netAmountTextView = findViewById(R.id.netAmountTextView);
        netAmountTextView.setText("$"+ netAmount);
        TextView totalPerPersonTextView = findViewById(R.id.totalPerPersonTextView);
        totalPerPersonTextView.setText("$" + totalPerPersonAmount);
    }

    public void decreaseTip(View view){
        tipPercent -= 5;
        updateScreen();
    }

    public void increaseTip(View view){
        tipPercent += 5;
        updateScreen();
    }

    public void changeTip(View view){
        Log.i("OnClickAttribute", "Clicked a button with an attribute");
        if(view.getId() == R.id.decreaseTipButton){
            tipPercent -= 5;
        }
        else if(view.getId() == R.id.increaseTipButton){
            tipPercent += 5;
        }
        updateScreen();
    }
}

//class ButtonListener implements View.OnClickListener {
//    public void onClick(View v){
//        switch(v.getId()){
//            case R.id.decreaseTipButton:
//                MainActivity.tipPercent -= 0.05f;
//                break;
//            case R.id.increaseTipButton:
//                MainActivity.tipPercent += 0.05f;
//                break;
//        }
//        MainActivity.updateScreen();
//    }
//}
