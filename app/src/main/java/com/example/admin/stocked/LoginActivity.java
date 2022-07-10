package com.example.admin.stocked;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText Email;
    private EditText Password;
    private Button Login;
    private TextView passwordreset;
    private TextView Register;

    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private ProgressDialog processDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_v3);

        Email = (EditText) findViewById(R.id.emailSignIn);
        Password = (EditText) findViewById(R.id.password);
        Login = (Button) findViewById(R.id.Login);
        Register = (TextView) findViewById(R.id.registerbt);

        passwordreset = findViewById(R.id.forgotpassword);
        progressBar = (ProgressBar) findViewById(R.id.progressbars);
        progressBar.setVisibility(View.GONE);
        auth = FirebaseAuth.getInstance();
        processDialog = new ProgressDialog(this);



        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }

        });

        passwordreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ForgottenPassword.class));
                finish();
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });
    }








    public void validate(){

        String emailS = Email.getText().toString();
        String passwordS =Password.getText().toString();

        if (emailS.isEmpty()) {
            Email.setError(getString(R.string.empty_field));
            Email.requestFocus();
            return;
        }

        if (passwordS.isEmpty()) {
            Password.setError(getString(R.string.empty_field));
            Password.requestFocus();
            return;
        }

        if(!TextUtils.isEmpty(emailS) && !TextUtils.isEmpty(passwordS)){
        processDialog.setMessage(getString(R.string.wait));
        processDialog.show();

        auth.signInWithEmailAndPassword(emailS, passwordS).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    processDialog.dismiss();
                    Toast.makeText(LoginActivity.this, getString(R.string.login_succes), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, activity_dashboard.class));
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this,getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
                    processDialog.dismiss();
                }
            }
        });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //startActivity(new Intent(LoginActivity.this,MainActivity.class));
        finish();
    }
}
