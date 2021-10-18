package ca.davidpellegrini.tipcalculatorf21;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
    //Step 3.
        implements View.OnClickListener, TextWatcher {

    private float tipPercent = 20;

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


        // Step 2. have the View listen for an event
        // onClick for Buttons
        // Step3. this (the Activity/Class) needs to implement the listener
        decreaseButton.setOnClickListener(this);
        increaseButton.setOnClickListener(this);
        // TextChecked for EditTexts
        billAmountEditText.addTextChangedListener(this);
        // let AndroidStudio help you finish the listener for the RadioGroup
        numPeopleGroup.setOnCheckedChangeListener(this);
    }

    // Step 4. Define what the listner does
    // in our case it's update the tip percent, then do some math and update the display
    @Override
    /**
        We're using the main class onClickListener for now. This onClick method will handle
        any View presses, and will update the rest of the information on the screen
     */
    public void onClick(View view) {
        // convert the TextView into a useable float value
        TextView tipPercentTV = findViewById(R.id.tipPercentTextView);
        String tipPercentString = String.valueOf(tipPercentTV.getText());
        tipPercent = Float.parseFloat(tipPercentString);
        //depending on which Button is clicked, increase or decrease the float value
        switch (view.getId()){
            case R.id.decreaseTipButton:
                tipPercent -= 5;
                break;
            case R.id.increaseTipButton:
                tipPercent += 5;
                break;
            default:
                Log.e("ClickEvent", "You clicked the wrong button");
        }
        // update the tip percent TextView
        tipPercentTV.setText(String.valueOf(tipPercent));

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
        // "collect the bill from the user"
        EditText billAmountEditText = findViewById(R.id.billAmountEditText);
        String billAmountString = String.valueOf(billAmountEditText.getText());
        if(billAmountString.isEmpty()){
            billAmountString = "0";
        }
        float billAmount = Float.parseFloat(billAmountString);

        // calculate the tip
        float tipAmount = billAmount * (tipPercent / 100);
        // calculate the total amount of the bill with the tip
        float netAmount = billAmount + tipAmount;
//        Log.d("tipPercent", String.valueOf(tipPercent));
//        Log.d("tipAmount", String.valueOf(tipAmount));

        // figure out how many people are splitting the bill
        RadioGroup numPeopleGroup = findViewById(R.id.numPeopleGroup);
        int numPeople = 2;

        switch(numPeopleGroup.getCheckedRadioButtonId()){
            case R.id.onePersonRadioButton: //if(checkedButton == R.id.onePersonRadioButton)
                numPeople = 1;
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
}