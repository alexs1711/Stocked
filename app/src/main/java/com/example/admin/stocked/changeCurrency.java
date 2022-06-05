package com.example.admin.stocked;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;


public class changeCurrency extends AppCompatActivity {


    TextView selectedTxt;
    NumberPicker textPicker;
    String selectedCurrency, loadedCurrency;
    Button Update, Exit;
    DatabaseReference idatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_currency);

        selectedTxt = (TextView) findViewById(R.id.selectText);
        textPicker = (NumberPicker) findViewById(R.id.txtPicker);
        Update = (Button) findViewById(R.id.addTransactionButton2);
        Exit = (Button) findViewById(R.id.button2);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        idatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).child("UserDetails").child("currency");

        idatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (!TextUtils.isEmpty(dataSnapshot.getValue(String.class))) {
                        loadedCurrency = dataSnapshot.getValue(String.class);
                        selectedTxt.setText(getString(R.string.actualCurrency)+" " + loadedCurrency);
                        populateTextPicker();
                    }
                } else {
                    selectedTxt.setText(getString(R.string.actualCurrency)+" " + "???");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadCurrency();
            }
        });

        Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void populateTextPicker(){
        //Populate NumberPicker values from String array values
        final String[] values = {"EUR", "USD", "GBP", "BRL", "JPY", "MXN"};

        textPicker.setMinValue(0);
        textPicker.setMaxValue(values.length - 1);
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(loadedCurrency)) {
                textPicker.setValue(i);
            }
        }
        textPicker.setDisplayedValues(values);
        textPicker.setWrapSelectorWheel(true);

        textPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                selectedTxt.setText(getString(R.string.selectedCurrency)+" "+values[i1]);
                selectedCurrency = values[i1];
            }
        });
    }

    public void uploadCurrency(){

        idatabaseReference.setValue(selectedCurrency);
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.updatedCurreny)+" "+selectedCurrency, Snackbar.LENGTH_SHORT).show();
    }

}