package com.example.admin.stocked;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class changePasswordActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    FirebaseUser users;
    private ProgressBar progressBar;
    public Button changePassword,Cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        progressBar = (ProgressBar) findViewById(R.id.progressbars);
        progressBar.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();
        users = firebaseAuth.getCurrentUser();
        Cancel = findViewById(R.id.button2);

        changePassword = findViewById(R.id.addTransactionButton2);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordMethod();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public void changePasswordMethod(){
        progressBar.setVisibility(View.VISIBLE);
        firebaseAuth.sendPasswordResetEmail(users.getEmail())

                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(findViewById(android.R.id.content), getString(R.string.snackbar_password), Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), getString(R.string.snacbar_failed_password), Snackbar.LENGTH_SHORT).show();
                        }

                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}