package com.example.admin.stocked;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import de.hdodenhof.circleimageview.CircleImageView;

public class scanItemsActivity extends AppCompatActivity implements View.OnClickListener {
    public static EditText resultsearcheview;
    private FirebaseAuth firebaseAuth;
    Adapter adapter;
    RecyclerView mrecyclerview;
    DatabaseReference mdatabaseReference;
    public static scanItemsActivity app;
    Button dashboard, products, addprod, settings;
    String userUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_items);
        firebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser users = firebaseAuth.getCurrentUser();
        userUID = users.getUid();

        mdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("Items");
        resultsearcheview = findViewById(R.id.searchfield);

        app = this;//referencia estatica a el contexto de la aplicacion para que cada elemento del recyvler lo reciba
        mrecyclerview = findViewById(R.id.recyclerViews);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mrecyclerview.setLayoutManager(manager);
        mrecyclerview.setHasFixedSize(true);

        //Buttons of the dashboard
        dashboard = findViewById(R.id.dashboard);
        dashboard.setOnClickListener((View.OnClickListener) this);

        products = findViewById(R.id.products);
        products.setOnClickListener((View.OnClickListener) this);

        addprod = findViewById(R.id.addtoDB);
        addprod.setOnClickListener((View.OnClickListener) this);

        settings = findViewById(R.id.settings);
        settings.setOnClickListener((View.OnClickListener) this);

        mrecyclerview.setLayoutManager(new LinearLayoutManager(this));


        resultsearcheview.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                String editableText = s.toString();
                firebasesearch(editableText);
            }
        });
    }

    public void firebasesearch(String searchtext) {
        Query firebaseSearchQuery = mdatabaseReference.orderByChild("itemname").startAt(searchtext).endAt(searchtext + "\uf8ff");

        FirebaseRecyclerOptions<Items> options =
                new FirebaseRecyclerOptions.Builder<Items>()
                        .setQuery(firebaseSearchQuery, Items.class)
                        .build();

        FirebaseRecyclerAdapter<Items, scanItemsActivity.UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Items, scanItemsActivity.UsersViewHolder>(options) {
            @NonNull
            @Override
            public scanItemsActivity.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_list_layout, parent, false);
                return new scanItemsActivity.UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull scanItemsActivity.UsersViewHolder holder, int position, @NonNull Items model) {
                holder.setDetails(getApplicationContext(), model.getItemname(), model.getImagelocation(), model.getUuid(), model.getCatuuid());
            }
        };

        firebaseRecyclerAdapter.startListening();
        mrecyclerview.setAdapter(firebaseRecyclerAdapter);


    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.dashboard:
                i = new Intent(this, activity_dashboard.class);
                startActivity(i);
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
        Intent i;
        i = new Intent(this, activity_dashboard.class);
        startActivity(i);
        finish();
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebasesearch("");
       /* Query query = mdatabaseReference.orderByChild("catuuid");

        FirebaseRecyclerOptions<Items> options =
                new FirebaseRecyclerOptions.Builder<Items>()
                        .setQuery(query, Items.class)
                        .build();

        FirebaseRecyclerAdapter<Items, scanItemsActivity.UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Items, scanItemsActivity.UsersViewHolder>(options)
        {
            @NonNull
            @Override
            public scanItemsActivity.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_list_layout, parent, false);
                return new scanItemsActivity.UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull scanItemsActivity.UsersViewHolder holder, int position, @NonNull Items model) {
                holder.setDetails(getApplicationContext(), model.getItemname(),model.getImagelocation(), model.getUuid(),model.getCatuuid());
            }
        };

        firebaseRecyclerAdapter.startListening();
        mrecyclerview.setAdapter(firebaseRecyclerAdapter);*/
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDetails(Context ctx, final String itemname, final String imagelocation, final String uuid, final String CategoryCode) {
            TextView item_name = (TextView) mView.findViewById(R.id.viewproductname);
            CircleImageView itemPic = mView.findViewById(R.id.item_image);
            /*hacer funcion para que cuando reciba el url compruebe si lo tiene, en caso de no tenerlo que ponga la img default*/

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.cameraicon)
                    .error(R.drawable.cameraicon)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);

            Glide.with(ctx).load(imagelocation).apply(options).into(itemPic);

            item_name.setText(itemname);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(app, "you clicked " + getAdapterPosition() + " name " + itemname + " uuid: " + uuid, Toast.LENGTH_SHORT).show();
                    Intent launchIntent = new Intent(app, ItemDetails.class);
                    launchIntent.putExtra("Item_uuid", uuid);//mando la random key de cada categoria para que asi abra esa categoria en concreto
                    launchIntent.putExtra("Code_cat", CategoryCode);
                    launchIntent.setType("text/plain");
                    app.startActivity(launchIntent);
                }
            });
        }


    }

}
