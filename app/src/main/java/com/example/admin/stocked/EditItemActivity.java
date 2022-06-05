package com.example.admin.stocked;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditItemActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText itemname, itemUnitEdit, itemDescriptionEdit;
    private TextView itembarcode,UpdateItemTopT;
    private FirebaseAuth firebaseAuth;
    public static TextView resulttextview;
    Button scanbutton, updateItem,deleteItem,exitEditItem;
    private CircleImageView itemPic;
    public Uri imageUri;
    public boolean imageUpload = true;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    DatabaseReference mdatabaseReference;
    DatabaseReference mDatabase;
    String itemcategoryValue, imagelocation;
    Category cat,selectedcat;
    Spinner spin;
    EditItemActivity app;
    public static EditItemActivity ItemListapp;

    public static final int REQUEST = 1;

    public static final int FAILED = 2;

    FirebaseUser users;

    List<String> categories;

    //add textview strings
    String itemnameValue, itemUnits, itembarcodeValue, CatCode, itemdesc;
    //intent strings
    String ItemUuid,Code_cat;
    //user strings


    HashMap<String, String> CatNameKey = new HashMap<String, String>();

    @Override
    // arreglar que cuando borre la categoria compruebe que ya no existe y no deje insertar ya que el spinner lo carga y claro te deja meterlo pero eso ya no existe
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        //get intent text
        Intent intent = getIntent();
        ItemUuid=intent.getStringExtra("Item_uuid");
        Code_cat=intent.getStringExtra("Code_cat");

        //
        app = this;

        //database reference
        firebaseAuth = FirebaseAuth.getInstance();
        users = firebaseAuth.getCurrentUser();
        String userUID = users.getUid();

        mdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("Items").child(ItemUuid);
        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("CategoryDetails");

        //layout ids buttons

        updateItem = findViewById(R.id.UpdateItem);
        scanbutton = findViewById(R.id.buttonscan);
        deleteItem = findViewById(R.id.deleteItem);
        exitEditItem = findViewById(R.id.exitEditItem);

        //layout ids textviews

        resulttextview = findViewById(R.id.UpdateItemBarCode);
        itemname = findViewById(R.id.UpdateItemName);
        itemUnitEdit = findViewById(R.id.UpdateItemUnits);
        itembarcode = findViewById(R.id.UpdateItemBarCode);
        itemDescriptionEdit = findViewById(R.id.UpdateItemDescription);
        UpdateItemTopT = findViewById(R.id.UpdateItemTop);

        //static reference to this app so it can be referred from external methods
        ItemListapp = this;

        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        spin = (Spinner) findViewById(R.id.spinner2);
        spin.setOnItemSelectedListener(ItemListapp);
        loadCategories();

        //storage references to database instances

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // itempic selector image upload to firebase database
        itemPic = findViewById(R.id.image_upload_pic);
        itemPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePicture();
            }
        });
        // String result = finaluser.substring(0, finaluser.indexOf("@"));


        scanbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ScanCodeActivityEdit.class));
            }
        });

        updateItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                additem();
            }
        });

        deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem();

            }
        });

        exitEditItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //load data from already created item
        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //cargamos los datos iniciales
                Items loadItem = dataSnapshot.getValue(Items.class);
                if(loadItem != null){
                UpdateItemTopT.setText(loadItem.getItemname());
                itemname.setText(loadItem.getItemname());
                itemUnitEdit.setText(loadItem.getItemunits());
                itembarcode.setText(loadItem.getItembarcode());
                itemDescriptionEdit.setText(loadItem.getDescription());
                imagelocation = loadItem.getImagelocation();

                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.cameraicon)
                        .error(R.drawable.cameraicon)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.HIGH);
                Glide.with(getApplicationContext()).load(loadItem.getImagelocation()).apply(options).into(itemPic);}
                if (loadItem == null){
                    Intent launchIntent = new Intent();
                    setResult(2,launchIntent);
                    finish();
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
                .setPositiveButton(getString(R.string.DELETE), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent launchIntent = new Intent();
                        setResult(2,launchIntent);
                        //borrar foto base datos
                        final StorageReference riversRef = storageReference.child("ItemImages/" + ItemUuid + ".jpg");
                        if (riversRef!=null){ riversRef.delete();}
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
                itemPic.setImageURI(imageUri);
            } else if (resultCode == FAILED) {
                Toast.makeText(this, getString(R.string.UploadError), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadPicture() {

        //we check for imageUri, in case the user did not select any pic there will be an error so we wont upload nor set any pic in the firebase json
        if (imageUri == null) {
            imageUpload = false;
        } else {
            final ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle(getString(R.string.uploadingImage));
            pd.show();
            final String randomKey = UUID.randomUUID().toString();

            final StorageReference riversRef = storageReference.child("ItemImages/" + ItemUuid + ".jpg");
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
                                            imagelocation = imageUrl;
                                            Items items = new Items(ItemUuid, itemnameValue, itembarcodeValue, imagelocation, itemdesc, itemUnits,CatCode);
                                            //databaseReference.child(resultemail).child("Items").child(itembarcodeValue).setValue(items);  useless child since it's everything in the items by category
                                            mdatabaseReference.setValue(items);
                                        }
                                    });
                                }
                            }
                        }
                    });
        }
    }

    public void loadCategories() {
        categories = new ArrayList<>();//creo una lista. una arraylist con la clase category
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        //String cate = ds.getKey(); code update no longer using the getKey(name of the node) since now it´s a random UUID so I´m gonna be using the name of the category attribute obtaining the ds value converted to Category.class
                        //crear una arraylist o algo parecido con el que comparar la posicion clickeada con el string del spinner en el itemselected

                        cat = ds.getValue(Category.class);
                        String name = cat.getName();
                        //String UUID = cat.getUUID();
                        //Toast.makeText(getApplicationContext(),cat.getUuid(), Toast.LENGTH_LONG).show();
                        CatNameKey.put(cat.getName(), cat.getUuid());
                        System.out.println();
                        categories.add(cat.getName());//para agregar a la List tengo que especificar que quiero meter un arraylist el cual es una categoria

                        //getting default selected category data

                    }

                    ArrayAdapter<String> aa = new ArrayAdapter<>(EditItemActivity.this, android.R.layout.simple_spinner_item, categories);//tal vez crear layout para el spinner?
                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    //Setting the ArrayAdapter data on the Spinner
                    spin.setAdapter(aa);

                    //getting selected category by default
                    selectedcat = dataSnapshot.child(Code_cat).getValue(Category.class);
                    String preSelectedCat = selectedcat.getName();
                    if (preSelectedCat != null) {
                        int spinnerPosition = aa.getPosition(preSelectedCat);
                        spin.setSelection(spinnerPosition);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    // addding item to databse
    public void additem() {

        /*maybe optimize the way i get the not filled things with more error request focus*/
        itemnameValue = itemname.getText().toString();
        itemUnits = itemUnitEdit.getText().toString();
        itembarcodeValue = itembarcode.getText().toString();
        itemdesc = itemDescriptionEdit.getText().toString();


        if (TextUtils.isEmpty(itemcategoryValue)) {
            Toast.makeText(EditItemActivity.this, getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
        }

        if (itembarcodeValue.isEmpty()) {
            itembarcode.setError(getString(R.string.empty_field));
            itembarcode.requestFocus();
            return;
        }


        if (!TextUtils.isEmpty(itemnameValue) && !TextUtils.isEmpty(itemcategoryValue) && !TextUtils.isEmpty(itemUnits)) {

            uploadPicture();//this goes first in case the image is not uploaded so we will check it with success listener and changing the boolean to true

            if (!imageUpload) {//in case boolean
                Items items = new Items(ItemUuid, itemnameValue, itembarcodeValue, imagelocation, itemdesc, itemUnits,CatCode);//removal of itemCategoryValue since it has no sense
                //databaseReference.child(resultemail).child("Items").child(itembarcodeValue).setValue(items); useless child since it's everything in the items by category
                mdatabaseReference.setValue(items);

            }
            itemPic.setImageDrawable(getResources().getDrawable(R.drawable.cameraicon));
            Snackbar.make(findViewById(android.R.id.content), getString(R.string.update_changes), Snackbar.LENGTH_LONG).show();
        } else {
            Toast.makeText(EditItemActivity.this, getString(R.string.fillall), Toast.LENGTH_SHORT).show();
        }
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        itemcategoryValue = arg0.getItemAtPosition(position).toString();
        CatCode = CatNameKey.get(itemcategoryValue);
        //Toast.makeText(getApplicationContext(),CatCode , Toast.LENGTH_LONG).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}