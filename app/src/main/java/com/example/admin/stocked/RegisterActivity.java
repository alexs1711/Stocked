package com.example.admin.stocked;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText  editTextEmail, editTextPassword, editTextPhone,editTextcPassword,editUserName,editUserSurname;
    public Button UserRegisterBtn;
    private ProgressBar progressBar;
    private TextView Login;


    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_v1);

        editUserName = findViewById(R.id.NameRegister);
        editUserSurname = findViewById(R.id.SurNameRegister);

        editTextEmail = findViewById(R.id.emailRegister);
        editTextPassword = findViewById(R.id.passwordRegister);
        editTextcPassword= findViewById(R.id.confirmPassword);
        UserRegisterBtn= findViewById(R.id.button_register);
        Login = findViewById(R.id.loginbt);
//        editTextPhone = findViewById(R.id.edit_text_phone);
        progressBar = findViewById(R.id.progressbarr);
        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        //  findViewById(R.id.button_register).setOnClickListener(this);

        UserRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }

        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            //handle the already login user
        }
    }
//    public void addStudent(){
//        String studentNameValue = editTextName.getText().toString();
//        String mcneeseIdValue = editTextEmail.getText().toString();
//        if(!TextUtils.isEmpty(studentNameValue)&&!TextUtils.isEmpty(mcneeseIdValue)){
//            String id = FirebaseDatabase.getInstance().getReference("Users").push().getKey();
//            User students = new User(studentNameValue,mcneeseIdValue);
//            // databaseReference.child(bttnName.getText().toString()).push().setValue(students);
//            FirebaseDatabase.getInstance().getReference("Users").setValue(students);
//            editTextName.setText("");
//            editTextEmail.setText("");
//            Toast.makeText(RegisterActivity.this,"Student Details Added",Toast.LENGTH_SHORT).show();
//        }
//        else{
//            Toast.makeText(RegisterActivity.this,"Please Fill Fields",Toast.LENGTH_SHORT).show();
//        }
//    }


    private void registerUser() {
        final String username = editUserName.getText().toString().trim();
        final String surname = editUserSurname.getText().toString().trim();
        final String name = "EUR";
        final String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString().trim();
        String cpassword = editTextcPassword.getText().toString().trim();

        // final String phone = editTextPhone.getText().toString().trim();

        if (username.isEmpty()) {
            editUserName.setError(getString(R.string.empty_field));
            editUserName.requestFocus();
            return;
        }

        if (surname.isEmpty()) {
            editUserSurname.setError(getString(R.string.empty_field));
            editUserSurname.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            editTextEmail.setError(getString(R.string.empty_field));
            editTextEmail.requestFocus();
            return;
        }





        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(getString(R.string.not_valid_email));
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError(getString(R.string.empty_field));
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError(getString(R.string.need_more_length));
            editTextPassword.requestFocus();
            return;
        }
        if(!password.equals(cpassword)){
            editTextcPassword.setError(getString(R.string.password_dont_match));
            editTextcPassword.requestFocus();
            return;
        }

//        if (phone.isEmpty()) {
//            editTextPhone.setError(getString(R.string.input_error_phone));
//            editTextPhone.requestFocus();
//            return;
//        }
//
//        if (phone.length() != 10) {
//            editTextPhone.setError(getString(R.string.input_error_phone_invalid));
//            editTextPhone.requestFocus();
//            return;
//        }


        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

//                            addStudent();

                            final User user = new User(
                                    name,
                                    email,username,surname

                            );
                            //.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            //important to retrive data and send data based on user email
                            FirebaseUser usernameinfirebase = mAuth.getCurrentUser();

                            String UserUID = mAuth.getCurrentUser().getUid();

                            FirebaseDatabase.getInstance().getReference("Users").child(UserUID).child("UserDetails").setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, getString(R.string.register_success), Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(RegisterActivity.this,activity_dashboard.class));
                                    } else {
                                        //display a failure message
                                    }
                                }
                            });

                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this,getString(R.string.register_fail), Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
        finish();
    }
    //    //Set UserDisplay Name
//    private void userProfile()
//    {
//        FirebaseUser user = mAuth.getCurrentUser();
//        if(user!= null)
//        {
//            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
//                    .setDisplayName(editTextName.getText().toString().trim())
//                    //.setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))  // here you can set image link also.
//                    .build();
//
//            user.updateProfile(profileUpdates)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//
//                            }
//                        }
//                    });
//        }
//    }



}
