package com.example.admin.stocked;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class changeNameActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    FirebaseUser users;
    EditText changeName, changeSurname;
    Button Update, Cancel;
    DatabaseReference mDatabase;
    String userUID;
    User userdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        changeName = findViewById(R.id.oldMail1);
        changeSurname = findViewById(R.id.oldMail2);

        Update = findViewById(R.id.addTransactionButton2);
        Cancel = findViewById(R.id.button2);

        firebaseAuth = FirebaseAuth.getInstance();
        users = firebaseAuth.getCurrentUser();

        userUID = users.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("UserDetails");

        firebaseAuth = FirebaseAuth.getInstance();
        users = firebaseAuth.getCurrentUser();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userdata = dataSnapshot.getValue(User.class);
                    if (userdata != null) {
                        changeName.setText(userdata.getName());
                        changeSurname.setText(userdata.getSurname());
                    }
                    if (userdata == null) {
                        changeName.setText(R.string.noData);
                        changeSurname.setText(R.string.noData);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNames();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void updateNames() {

        String name,surname;
        name = changeName.getText().toString();
        surname = changeSurname.getText().toString();

        if(name.isEmpty()){
           changeName.setError(getString(R.string.empty_name));
           changeName.requestFocus();
        }

        if(surname.isEmpty()){
            changeSurname.setError(getString(R.string.empty_surname));
            changeSurname.requestFocus();
        }

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(surname)){
        userdata.setName(name);
        userdata.setSurname(surname);
        mDatabase.setValue(userdata);
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.updatedName), Snackbar.LENGTH_LONG).show();
        }
    }
}