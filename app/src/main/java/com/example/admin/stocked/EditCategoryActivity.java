package com.example.admin.stocked;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditCategoryActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private TextView EditCatTopName, EditCatName, EditDesc;
    private DatabaseReference mdatabaseReference;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    String CatName, CatDesc, CatImageLocation,catcode,imagelocation,catNameS,editDescS;
    private CircleImageView EditCatImage;
    public Button uploadChanges,deleteCat,exitEditCats;
    Category loadCat,uploadCat;

    public static final int REQUEST = 1;

    public static final int FAILED = 2;

    public Uri imageUri;

    public boolean imageUpload = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        //get category code to edit with intent
        Intent intent = getIntent();
        catcode = intent.getStringExtra("Product_Name_Intent");

        //Ids of layout textviews and circleimageview
        EditCatTopName = findViewById(R.id.UpdateItemTopT);
        EditCatName = findViewById(R.id.UpdateCategoryName);
        EditDesc = findViewById(R.id.UpdateItemDescription);
        EditCatImage = findViewById(R.id.cat_upload_pic);

        //Ids of layout buttons
        uploadChanges = findViewById(R.id.addcategorybuttontodatabase);
        deleteCat = findViewById(R.id.deleteCategory);
        exitEditCats = findViewById(R.id.exitEditCats);

        // itempic selector image upload update to firebase database
        EditCatImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });

        uploadChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadChanges();
            }
        });

        deleteCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem();
            }
        });

        exitEditCats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //get user
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser users = firebaseAuth.getCurrentUser();
        String userUID = users.getUid();

        //get database reference so we get the data from the category that is going to be edited
        mdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("CategoryDetails").child(catcode);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Toast.makeText(this,"Comprobacion nuevo nombre child "+ catcode,Toast.LENGTH_SHORT);

        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //cargamos los datos iniciales
                loadCat = dataSnapshot.getValue(Category.class);
                if(loadCat!=null){
                CatName = loadCat.getName();
                CatDesc = loadCat.getDescription();
                CatImageLocation = loadCat.getImagelocation();
                EditCatTopName.setText(CatName);
                EditCatName.setText(CatName);
                EditDesc.setText(CatDesc);

                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.cameraicon)
                        .error(R.drawable.cameraicon)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.HIGH);
                Glide.with(getApplicationContext()).load(CatImageLocation).apply(options).into(EditCatImage);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void deleteItem() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.check_delete))
                .setPositiveButton(R.string.DELETE, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //borrar foto base datos
                        try{
                        final StorageReference riversRef = storageReference.child("CategoriesImages/" + catcode + ".jpg");
                        riversRef.delete();
                        riversRef.getDownloadUrl();
                        }
                        catch (Exception ex){

                        }
                        Intent launchIntent = new Intent();
                        setResult(2,launchIntent);
                        mdatabaseReference.removeValue();
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.CANCEL), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).setIcon(R.drawable.ic_icon_trash)
                .show();
    }



    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                imageUri = data.getData();
                EditCatImage.setImageURI(imageUri);
                Glide.with(EditCategoryActivity.this).load(imageUri).into(EditCatImage);
            } else if (resultCode == FAILED) {
                Toast.makeText(this, getString(R.string.UploadError), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadPicture() {
        //we check for imageUri, in case the user did not select any pic there will be an error so we wont upload nor set any pic in the firebase json
        if (imageUri == null) {
            imagelocation = "no photo";
            imageUpload = false;
        } else {
            imageUpload = true;
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle(getString(R.string.uploadingImage));
            pd.show();

            final StorageReference riversRef = storageReference.child("CategoriesImages/" + catcode + ".jpg");
            // Register observers to listen for when the download is done or if it fails
            riversRef.putFile(imageUri)
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
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.UploadedPic), Snackbar.LENGTH_LONG).show();
                    //String dw = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString()+"xd";

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progressPercent = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    pd.setMessage("Progress: " + (int) progressPercent + "%");
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
                                            imagelocation = imageUrl;
                                            uploadCat.setName(catNameS);
                                            uploadCat.setDescription(editDescS);
                                            uploadCat.setImagelocation(imagelocation);
                                            mdatabaseReference.setValue(uploadCat);
                                            Snackbar.make(findViewById(android.R.id.content), getString(R.string.update_changes), Snackbar.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }
                        }
                    });
        }
    }

    public void UploadChanges(){
        uploadCat = loadCat; // this way I assure that I obtain the same Uiid

         catNameS = EditCatName.getText().toString();
         editDescS = EditDesc.getText().toString();

         if(TextUtils.isEmpty(catNameS)){
             EditCatName.setError(getString(R.string.empty_name));
             EditCatName.requestFocus();
         }

        if(TextUtils.isEmpty(editDescS)){
            EditDesc.setError(getString(R.string.empty_desc));
            EditDesc.requestFocus();
        }

        if (!TextUtils.isEmpty(catNameS) && !TextUtils.isEmpty(editDescS)) {
            uploadPicture();
            if (!imageUpload) {
                //image not changed so we do not change the pic already set with the uploadcat=loadcat;
                uploadCat.setName(catNameS);
                uploadCat.setDescription(editDescS);
                mdatabaseReference.setValue(uploadCat);
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.update_changes), Snackbar.LENGTH_LONG).show();
            }
        }
    }
}