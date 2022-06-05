package com.example.admin.stocked;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class deleteAccountActivity extends AppCompatActivity {
    Button delete,cancel;
    private ProgressBar progressBar;
    DatabaseReference idatabaseReference;

    FirebaseAuth firebaseAuth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);
        delete = findViewById(R.id.addTransactionButton2);
        cancel = findViewById(R.id.button2);
        progressBar = (ProgressBar) findViewById(R.id.progressbars);
        progressBar.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();
        idatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccount();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void deleteAccount(){

        progressBar.setVisibility(View.VISIBLE);
        if (user != null) {
            idatabaseReference.removeValue();
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if (task.isSuccessful()) {
                                Intent launchIntent = new Intent();
                                setResult(66,launchIntent);
                                //Toast.makeText(deleteAccountActivity.this, getString(R.string.profile_deleted), Toast.LENGTH_SHORT).show();
                                Snackbar.make(findViewById(android.R.id.content), getString(R.string.profile_deleted), Snackbar.LENGTH_SHORT).show();
                                firebaseAuth.signOut();

                                finish();
                            } else {
                                Snackbar.make(findViewById(android.R.id.content), getString(R.string.failed_delete_acc), Snackbar.LENGTH_SHORT).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }
    }
}