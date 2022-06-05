package com.example.admin.stocked;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class addcategoriesActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    DatabaseReference databaseReference;
    private StorageReference storageReference;
    public FirebaseUser users;
    private EditText itemcategory, categorydescription;
    private String itemCategoryS, categoryDescS, imagelocation;
    String userUID ;
    private boolean imageUpload = true;
    private Button addCat;
    //Image CircleView so we can change the default pic asgined for each recycler view element pic
    private CircleImageView catPic;
    // get request code from intents, in this case A get content intent
    public static final int REQUEST = 1;

    public static final int FAILED = 2;
    //we set here the image uri(universal resource identificator)
    public Uri CatImageUri;
    private String randomCat;
    Button dashboard, products, addprod, settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcategories);
        //here we get the firebase user(auth),the database reference and the storage reference
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        users = firebaseAuth.getCurrentUser();
        userUID = users.getUid();

        itemcategory = findViewById(R.id.UpdateCategoryName);
        categorydescription = findViewById(R.id.UpdateItemDescription);
        addCat = (Button) findViewById(R.id.addcategorybuttontodatabase);

        addCat.setOnClickListener(this);

        //catpic selector to firebase database
        catPic = findViewById(R.id.cat_upload_pic);
        catPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

        //Buttons of the dashboard
        dashboard = findViewById(R.id.dashboard);
        dashboard.setOnClickListener((View.OnClickListener) this);

        products = findViewById(R.id.products);
        products.setOnClickListener((View.OnClickListener) this);

        addprod = findViewById(R.id.addtoDB);
        addprod.setOnClickListener((View.OnClickListener) this);

        settings = findViewById(R.id.settings);
        settings.setOnClickListener((View.OnClickListener) this);
    }

    //void started when we press the circle image view
    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST);
    }

    //we get the result from the choosePicture() method, and then we set the result to a Uri
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                CatImageUri = data.getData();
                catPic.setImageURI(CatImageUri);
            } else if (resultCode == FAILED) {
                Toast.makeText(this, getString(R.string.Nopic), Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void uploadPicture(String randomCat) {

        //we check for imageUri, in case the user did not select any pic there will be an error so we wont upload nor set any pic in the firebase json
        if (CatImageUri == null) {
            imageUpload = false;
        } else {
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle(getString(R.string.uploadingImage));
            pd.show();

            final StorageReference riversRef = storageReference.child("CategoriesImages/" + randomCat + ".jpg");
            // Register observers to listen for when the download is done or if it fails
            riversRef.putFile(CatImageUri)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), getString(R.string.UploadError), Toast.LENGTH_LONG).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pd.dismiss();
                    Snackbar.make(findViewById(android.R.id.content),getString(R.string.UploadedPic), Snackbar.LENGTH_SHORT).show();
                    //String dw = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString()+"xd";

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    pd.setMessage(getString(R.string.progress) + (int) progressPercent + "%");
                }
            })/* https://stackoverflow.com/questions/50585334/tasksnapshot-getdownloadurl-method-not-working/55503926#55503926*/
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageUrl = uri.toString();
                                            //Toast.makeText(getApplicationContext(), "URL: " + imageUrl, Toast.LENGTH_LONG).show();
                                            imagelocation = imageUrl;
                                            //cambiar todo esto
                                            Category cat = new Category(randomCat,itemCategoryS, categoryDescS, imagelocation);
                                            databaseReference.child(userUID).child("CategoryDetails").child(randomCat).setValue(cat);
                                            Snackbar.make(findViewById(android.R.id.content), getString(R.string.catAdded), Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }
                    });
        }
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.addcategorybuttontodatabase:
                addcategory();
                break;
            case R.id.dashboard:
                finish();
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
                i = new Intent(this, ConfigurationActivity.class);
                startActivity(i);
                finish();
                break;
            default:
                break;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void addcategory() {
        itemCategoryS = itemcategory.getText().toString();
        categoryDescS = categorydescription.getText().toString();
        randomCat = UUID.randomUUID().toString();

        if (!TextUtils.isEmpty(itemCategoryS) && !TextUtils.isEmpty(categoryDescS)) {

            uploadPicture(randomCat);

            if (!imageUpload) {
                //Toast.makeText(addcategoriesActivity.this, "You have not picked any photo for the Category, so default photo will be set."  +randomCat, Toast.LENGTH_SHORT).show();
                imagelocation = "no photo";
                Category cat = new Category(randomCat,itemCategoryS, categoryDescS, imagelocation);
                databaseReference.child(userUID).child("CategoryDetails").child(randomCat).setValue(cat);
            }

            itemcategory.setText("");
            categorydescription.setText("");
            catPic.setImageDrawable(getResources().getDrawable(R.drawable.cameraicon));
            //Toast.makeText(addcategoriesActivity.this, itemCategoryS + " Added", Toast.LENGTH_SHORT).show();
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.catAdded), Snackbar.LENGTH_SHORT).show();

        } else {
            Toast.makeText(addcategoriesActivity.this, getString(R.string.fillall), Toast.LENGTH_SHORT).show();
        }
    }


}