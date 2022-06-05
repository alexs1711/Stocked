package com.example.admin.stocked;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class EditTransactionActivity extends AppCompatActivity {

    EditText priceT,quantityT;
    RadioButton Entering;
    String ItemUuid,TransactionUuid;
    Button updateTransactionBt,exitTransaction, deleteTransactionBt;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference,idatabaseReference;
    FirebaseUser users;
    TextView QuantityUnit,editDateBt,CurrencyTv;
    EditTransactionActivity app;
    Timestamp ts;
    String currency;
    ItemTransaction itemTload;
    private static final SimpleDateFormat sdf3 = new SimpleDateFormat("d/M/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_edit_transaction);
        priceT = findViewById(R.id.priceEdit);
        quantityT = findViewById(R.id.QuantityEdit);
        Entering =  findViewById(R.id.Entering);
        QuantityUnit = findViewById(R.id.QuantityUnity);
        CurrencyTv = findViewById(R.id.textView22);

        //buttons id
        updateTransactionBt = findViewById(R.id.addTransactionButton);
        exitTransaction = findViewById(R.id.button5);
        deleteTransactionBt = findViewById(R.id.deleteTransaction);

        //edit text date
        editDateBt = findViewById(R.id.editDate);

        //static reference to this activity
        app = this;

        //conseguir usuario firebase
        firebaseAuth = FirebaseAuth.getInstance();
        users = firebaseAuth.getCurrentUser();
        String userUID = users.getUid();

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

        Intent intent = getIntent();
        ItemUuid=intent.getStringExtra("Item_uuid");
        TransactionUuid=intent.getStringExtra("Transaction_Uuid");


        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("ItemTransactions").child(ItemUuid).child(TransactionUuid);

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

        //load data from transaction and set the text and everything

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    itemTload = dataSnapshot.getValue(ItemTransaction.class);
                    if(itemTload!=null){
                        priceT.setText(itemTload.getPricePerUnity()+"");
                        quantityT.setText(itemTload.getQuantity()+"");
                        if(itemTload.isItemEntering()){
                            Entering.isChecked();
                        }
                        Long parseDate = itemTload.getTimeStamp();
                        try {
                            ts = new Timestamp(parseDate);
                            String dateToFormat = sdf3.format(ts);
                            editDateBt.setText(dateToFormat);

                        } catch(Exception e) { //this generic but you can control another types of exception
                            System.out.println("Parser or conversion failed");
                            e.printStackTrace();
                            editDateBt.setText(R.string.exception);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateTransactionBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTransaction();
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

        deleteTransactionBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               deleteTransaction();
            }
        });

    }

    private void deleteTransaction(){
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.check_delete))
                .setPositiveButton(getString(R.string.DELETE), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        databaseReference.removeValue();
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).setIcon(R.drawable.ic_icon_trash)
                .show();
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
                    System.out.println("Parser or conversion failed");
                    e.printStackTrace();
                }

                editDateBt.setText(selectedDate);
            }
        });

        newFragment.show(app.getSupportFragmentManager(), "datePicker");
    }

    public void updateTransaction() {
        try {

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
                itemTload.setItemEntering(ItemEntering);
                itemTload.setPricePerUnity(priceF);
                itemTload.setQuantity(quantityF);
                itemTload.setTimeStamp(timestampL);
                //databaseReference.child("ItemTransactions").child(Code_cat).child(ItemUuid).child(randomTransactionCode).setValue(itemT);
                databaseReference.setValue(itemTload);
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.update_changes), Snackbar.LENGTH_LONG).show();
            }
        }
        catch (NumberFormatException e) {
            priceT.setError(getString(R.string.errorNumbers));
            quantityT.setError(getString(R.string.errorNumbers));
        }
    }
}
