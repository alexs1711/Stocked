package com.example.admin.stocked;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class addTransaction extends AppCompatActivity {
    EditText priceT,quantityT;
    RadioButton Entering;
    String ItemUuid,Code_cat;
    Button addTransactionBt,exitTransaction;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference,idatabaseReference;
    FirebaseUser users;
    TextView QuantityUnit,editDateBt,CurrencyTv;
    addTransaction app;
    Timestamp ts;
    String userUID;
    String currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);
        priceT = findViewById(R.id.priceEdit);
        quantityT = findViewById(R.id.QuantityEdit);
        Entering =  findViewById(R.id.Entering);
        QuantityUnit = findViewById(R.id.QuantityUnity);
        CurrencyTv = findViewById(R.id.textView22);

        //buttons id
        addTransactionBt = findViewById(R.id.addTransactionButton);
        exitTransaction = findViewById(R.id.button5);

        //edit text date
        editDateBt = findViewById(R.id.editDate);

        //static reference to this activity
        app = this;

        //conseguir usuario firebase
        firebaseAuth = FirebaseAuth.getInstance();
        users = firebaseAuth.getCurrentUser();
        userUID = users.getUid();

        //obtener moneda
        DatabaseReference UdatabaseReference;


        UdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("UserDetails").child("currency");

        UdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currency = dataSnapshot.getValue(String.class);
                    CurrencyTv.setText(currency);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUID);

        Intent intent = getIntent();
        ItemUuid=intent.getStringExtra("Item_uuid");
        Code_cat=intent.getStringExtra("Code_cat");

        //idatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("ItemByCategory").child(Code_cat).child(ItemUuid);
        idatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("Items").child(ItemUuid);
        idatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Items item = dataSnapshot.getValue(Items.class);
                    if (item != null) {
                        QuantityUnit.setText(item.getItemunits());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addTransactionBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTransaction();
            }
        });

        exitTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        editDateBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = day + "/" + (month+1) + "/" + year;
                final String parseDate = year+"-"+(month+1)+"-"+day;
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    ts = new Timestamp(((java.util.Date)dateFormat.parse(parseDate)).getTime());
                } catch(Exception e) { //this generic but you can control another types of exception
                    e.printStackTrace();
                }

                editDateBt.setText(selectedDate);
            }
        });

        newFragment.show(app.getSupportFragmentManager(), "datePicker");
    }

    public void addTransaction() {
        try {
            final String randomTransactionCode = UUID.randomUUID().toString();
            String priceS = priceT.getText().toString();
            String quantityS = quantityT.getText().toString();
            String DateS = editDateBt.getText().toString();

            float priceF = Float.parseFloat(priceS);
            float quantityF = Float.parseFloat(quantityS);
            Boolean ItemEntering = false;

            if (priceS.isEmpty()) {
                priceT.setError(getString(R.string.insertPriceError));
                priceT.requestFocus();
            }
            if (quantityS.isEmpty()) {
                quantityT.setError(getString(R.string.insertQuantityError));
                quantityT.requestFocus();
            }
            if (DateS.isEmpty()) {
                editDateBt.setError(getString(R.string.insertDateError));
                editDateBt.requestFocus();
            }
            else {
                if (Entering.isChecked()) {
                    ItemEntering = true;
                }
                Long timestampL = ts.getTime();
                ItemTransaction itemT = new ItemTransaction(timestampL,randomTransactionCode,ItemUuid,quantityF,priceF,ItemEntering);
                //databaseReference.child("ItemTransactions").child(Code_cat).child(ItemUuid).child(randomTransactionCode).setValue(itemT);
                databaseReference.child("ItemTransactions").child(ItemUuid).child(randomTransactionCode).setValue(itemT);
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.addTransaction), Snackbar.LENGTH_LONG).show();
            }
        }
        catch (NumberFormatException e) {
            priceT.setError(getString(R.string.errorNumbers));
            quantityT.setError(getString(R.string.errorNumbers));
        }
    }
}