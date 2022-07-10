package com.example.admin.stocked;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();

        if(user != null){
            finish();
            startActivity(new Intent(this, activity_dashboard.class));
            overridePendingTransition(0,0);
        }
        else {
            finish();
            startActivity(new Intent(this,LoginActivity.class));
            overridePendingTransition(0,0);
        }

    }
}

