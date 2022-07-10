package com.example.admin.stocked;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgottenPassword extends AppCompatActivity {
    private EditText passwordresetemail;
    private Button send;

    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private ProgressDialog processDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotten_password);
        passwordresetemail = findViewById(R.id.emailSignIn);
        progressBar = (ProgressBar) findViewById(R.id.progressbars);
        progressBar.setVisibility(View.GONE);
        send = findViewById(R.id.send);

        auth = FirebaseAuth.getInstance();
        processDialog = new ProgressDialog(this);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetpasword();
            }
        });
    }
    public void resetpasword() {

        final String resetemail = passwordresetemail.getText().toString();

        if (resetemail.isEmpty()) {
            passwordresetemail.setError(getString(R.string.empty_field));
            passwordresetemail.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(resetemail)

                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgottenPassword.this, getString(R.string.snackbar_password), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ForgottenPassword.this, getString(R.string.snacbar_failed_password), Toast.LENGTH_SHORT).show();
                        }

                        progressBar.setVisibility(View.GONE);
                    }
                });
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(ForgottenPassword.this,LoginActivity.class));
                finish();
            }
        }, 5000);



    }

}
