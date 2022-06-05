package com.example.admin.stocked;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class changeMailActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button changeemail,Cancel;
    EditText emailTv,email2Tv,change,passwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_mail);

        // Initialising the email and password
        emailTv = findViewById(R.id.oldMail1);
        email2Tv = findViewById(R.id.oldMail2);
        change = findViewById(R.id.newMail);
        passwd = findViewById(R.id.passwordcheck);
        final EditText password = findViewById(R.id.passwordcheck);
        changeemail = findViewById(R.id.addTransactionButton2);
        Cancel = findViewById(R.id.button2);

        // click on this to change email
        changeemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeemail();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }



    // Here we are going to change our email using firebase re-authentication
    private void changeemail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email1 =emailTv.getText().toString();
        String email2 = email2Tv.getText().toString();
        String newmail = change.getText().toString();
        String password = passwd.getText().toString();



        if(!email1.equals(email2)){
            emailTv.setError(getString(R.string.mailsNoMatch));
            emailTv.requestFocus();
        }
        if(!email1.equals(user.getEmail())){
            emailTv.setError(getString(R.string.mailNotCorrect));
            emailTv.requestFocus();
        }

        if(!email2.equals(user.getEmail())){
            email2Tv.setError(getString(R.string.mailNotCorrect));
            email2Tv.requestFocus();
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email1).matches()) {
            emailTv.setError(getString(R.string.not_valid_email));
            emailTv.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email2).matches()) {
            email2Tv.setError(getString(R.string.not_valid_email));
            email2Tv.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(newmail).matches()) {
            change.setError(getString(R.string.not_valid_email));
            change.requestFocus();
            return;
        }

        if(password.isEmpty()){
            passwd.setError(getString(R.string.empty_field));
            passwd.requestFocus();
        }

        else {

            try{
                AuthCredential credential = EmailAuthProvider.getCredential(email1, password);

                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        Log.d("value", "User re-authenticated.");

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.updateEmail(change.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.emailSnackbar) + change.getText().toString(), Snackbar.LENGTH_LONG).show();
                                }
                                else {
                                    passwd.setError(getString(R.string.not_valid_passwd));
                                    passwd.requestFocus();
                                }
                            }
                        });

                    }
                });
            }
            catch (Exception e){
                passwd.setError(getString(R.string.not_valid_passwd));
                passwd.requestFocus();
            }
            // Get auth credentials from the user for re-authentication
             // Current Login Credentials

            // Prompt the user to re-provide their sign-in credentials

        }
    }
}