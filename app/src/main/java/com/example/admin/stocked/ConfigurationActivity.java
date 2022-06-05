package com.example.admin.stocked;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ConfigurationActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    private Button dashboard, products, addprod, settings, logout,language,changeName,changePassword,ChangeMail,deleteAcc,changeCurrency;
    private TextView Tusername;
    FirebaseUser users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        firebaseAuth = FirebaseAuth.getInstance();
        users = firebaseAuth.getCurrentUser();

        dashboard = findViewById(R.id.dashboard);
        dashboard.setOnClickListener(this);

        products = findViewById(R.id.products);
        products.setOnClickListener(this);

        addprod = findViewById(R.id.addtoDB);
        addprod.setOnClickListener(this);

        settings = findViewById(R.id.settings);
        settings.setOnClickListener(this);

        logout = findViewById(R.id.logoutButton);
        logout.setOnClickListener(this);

        Tusername = findViewById(R.id.textView13);

        String finaluser = users.getEmail();
        Tusername.setText(finaluser);

        changeName = findViewById(R.id.ChangeName);
        changeName.setOnClickListener(this);

        changePassword = findViewById(R.id.ChangePassword);
        changePassword.setOnClickListener(this);

        ChangeMail = findViewById(R.id.ChangeMail);
        ChangeMail.setOnClickListener(this);

        deleteAcc = findViewById(R.id.DeleteAccount);
        deleteAcc.setOnClickListener(this);

        changeCurrency = findViewById(R.id.changeCurrency);
        changeCurrency.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.dashboard:
                i = new Intent(this, activity_dashboard.class);
                //go back to activity home without creating a new one
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                break;
            case R.id.products:
                i = new Intent(this, CategoriesActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.addtoDB:
                i = new Intent(this, AddselectorActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.settings:
                break;
            case R.id.logoutButton:
                Logout();
                break;
            case R.id.ChangeName:
                i = new Intent(this,changeNameActivity.class);
                startActivity(i);
                break;
            case R.id.ChangePassword:
                i = new Intent(this,changePasswordActivity.class);
                startActivity(i);
                break;
            case R.id.ChangeMail:
                i = new Intent(this,changeMailActivity.class);
                startActivity(i);
                break;
            case R.id.DeleteAccount:
                i = new Intent(this,deleteAccountActivity.class);
                startActivityForResult(i,66);
                break;
            case R.id.changeCurrency:
                i = new Intent(this,changeCurrency.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    private void Logout()
    {
        firebaseAuth.signOut();
        startActivity(new Intent(ConfigurationActivity.this,MainActivity.class));
        Toast.makeText(ConfigurationActivity.this, getString(R.string.LogoutSuccesful), Toast.LENGTH_SHORT).show();
        Intent launchIntent = new Intent();
        setResult(66,launchIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==66)
        {
            setResult(66,data);
            Logout();
        }
    }
}