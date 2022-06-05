package com.example.admin.stocked;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class activity_dashboard extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    DatabaseReference mdatabaseReference, idatabaseReference, cdatabaseReference, itdatabaseReference;
    String userUID ;
    int TotalItems = 0, TotalCategories = 0;
    float TotalSales = 0f, TotalCost = 0f, TotalProfit = 0f;
    TextView totalsalesnumber, totalcostnumber, totalitemsnumber, totalcatsnumber, totalprofit,topTextName;
    Button dashboard, products, addprod, settings, search;
    static String currency,name;
    public User userdata;
    public Boolean currencyLoad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //conseguir usuario firebase
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser users = firebaseAuth.getCurrentUser();
        userUID = users.getUid();

        DatabaseReference UdatabaseReference;

        UdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("UserDetails");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        dashboard = findViewById(R.id.dashboard);
        dashboard.setOnClickListener(this);

        products = findViewById(R.id.products);
        products.setOnClickListener(this);

        addprod = findViewById(R.id.addtoDB);
        addprod.setOnClickListener(this);

        settings = findViewById(R.id.settings);
        settings.setOnClickListener(this);

        search = findViewById(R.id.searchItem);
        search.setOnClickListener(this);

        //ids de los textviews
        totalsalesnumber = findViewById(R.id.totalsalesnumber);
        totalcostnumber = findViewById(R.id.totalcostnumber);
        totalitemsnumber = findViewById(R.id.totalitemsnumber);
        totalcatsnumber = findViewById(R.id.totalcatsnumber);
        totalprofit = findViewById(R.id.totalprofit);
        topTextName = findViewById(R.id.topText);

        UdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    userdata = dataSnapshot.getValue(User.class);
                    currency = userdata.getCurrency();
                    name = userdata.getName();
                    TotalProfit = TotalSales - TotalCost;
                    totalprofit.setText(TotalProfit +" "+ currency);
                    totalcostnumber.setText(TotalCost +" "+currency);
                    totalsalesnumber.setText(TotalSales +" "+currency);
                    topTextName.setText(getString(R.string.welcome)+" "+name);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        idatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("Items");

        idatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    TotalItems = (int) dataSnapshot.getChildrenCount();
                    totalitemsnumber.setText(Integer.toString(TotalItems));

                } else {
                    totalitemsnumber.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        cdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("CategoryDetails");

        cdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    TotalCategories = (int) dataSnapshot.getChildrenCount();
                    totalcatsnumber.setText(Integer.toString(TotalCategories));
                } else {
                    totalcatsnumber.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        itdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("ItemTransactions");

        itdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        for (DataSnapshot dss : ds.getChildren()) {
                            ItemTransaction ts = dss.getValue(ItemTransaction.class);
                            if (ts != null) {
                                if (ts.isItemEntering()) {
                                    TotalCost = TotalCost + (ts.getQuantity() * ts.getPricePerUnity());
                                }
                                if (!ts.isItemEntering()) {
                                    TotalSales = TotalSales + (ts.getQuantity() * ts.getPricePerUnity());
                                }
                            }
                        }
                    }
                } else {
                    TotalCost = 0f;
                    TotalSales = 0f;
                }
                TotalProfit = TotalSales - TotalCost;
                totalprofit.setText(TotalProfit +" "+ currency);
                totalcostnumber.setText(TotalCost +" "+currency);
                totalsalesnumber.setText(TotalSales +" "+currency);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.dashboard:
                break;
            case R.id.products:
                i = new Intent(this, CategoriesActivity.class);
                startActivity(i);
                break;
            case R.id.addtoDB:
                i = new Intent(this, AddselectorActivity.class);
                startActivity(i);
                break;
            case R.id.settings:
                i = new Intent(this, ConfigurationActivity.class);
                startActivityForResult(i,66);
                break;
            case R.id.searchItem:
                i = new Intent(this, scanItemsActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==66)
        {
            finish();
        }
    }


}


