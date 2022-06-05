package com.example.admin.stocked;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class CategoriesActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    RecyclerView mrecyclerview;
    DatabaseReference mdatabaseReference, idatabaseReference;
    DatabaseReference mImageReference;
    private TextView totalnoofCats, totalnoofItems;
    private int counttotalnoofCats = 0, counttotalnoofItems = 0;
    public static CategoriesActivity app;
    public CircleImageView productpic;
    Button dashboard, products, addprod, settings;
    String userUID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        dashboard = findViewById(R.id.dashboard);
        dashboard.setOnClickListener(this);

        products = findViewById(R.id.products);
        products.setOnClickListener(this);

        addprod = findViewById(R.id.addtoDB);
        addprod.setOnClickListener(this);

        settings = findViewById(R.id.settings);
        settings.setOnClickListener(this);


        //stock in hand and n of items textviews
        totalnoofCats = findViewById(R.id.countAll);
        totalnoofItems = findViewById(R.id.countAll2);

        //conseguir usuario firebase
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser users = firebaseAuth.getCurrentUser();
        userUID = users.getUid();

        //database reference using the previous resultemail and in this case we want to view the categories
        mdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("CategoryDetails");
        idatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("Items");
        mrecyclerview = findViewById(R.id.recyclerViewP);

        //static reference so static methods can refer to this activity
        app = this;

        //


        LinearLayoutManager manager = new LinearLayoutManager(this);//scrolleabe como list view para ver varias listas de elementos
        mrecyclerview.setLayoutManager(manager);
        mrecyclerview.setHasFixedSize(false);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(this));

        //listener for the nº of cats, firebase method
        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    counttotalnoofCats = (int) dataSnapshot.getChildrenCount();
                    totalnoofCats.setText(Integer.toString(counttotalnoofCats));

                } else {
                    totalnoofCats.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //listener to count the number of items
        idatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    counttotalnoofItems = (int) dataSnapshot.getChildrenCount();
                    totalnoofItems.setText(Integer.toString(counttotalnoofItems));

                } else {
                    totalnoofCats.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        Query query = mdatabaseReference.limitToLast(50);

        FirebaseRecyclerOptions<Category> options =
                new FirebaseRecyclerOptions.Builder<Category>()
                        .setQuery(query, Category.class)
                        .build();

        FirebaseRecyclerAdapter<Category, ProductViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Category, ProductViewHolder>
                (options) {
            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_list_layout, parent, false);
                return new CategoriesActivity.ProductViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CategoriesActivity.ProductViewHolder holder, int position, @NonNull Category model) {
                holder.setDetails(getApplicationContext(), model.getName(), model.getImagelocation(), model.getUuid());
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
                //go back to activity home without creating a new one
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                //
                startActivity(i);
                finish();
                break;
            case R.id.products:
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==66)
        {
            finish();
        }
        if(resultCode==2)
        {
            finish();
        }

    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDetails(Context ctx, final String productname, final String imagelocation, final String CatKey) {
            //autogenerar imagen con la inicial de cada uno de los productos estaria bien
            TextView product_name = (TextView) mView.findViewById(R.id.viewproductname);
            product_name.setText(productname);
            CircleImageView itemPic = mView.findViewById(R.id.item_image);
            //try catch glide

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.cameraicon)
                    .error(R.drawable.cameraicon)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);

            Glide.with(ctx).load(imagelocation).apply(options).into(itemPic);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent launchIntent = new Intent(app, ItemsActivity.class);
                    launchIntent.putExtra("Product_Name_Intent", CatKey);//mando la random key de cada categoria para que asi abra esa categoria en concreto
                    launchIntent.setType("text/plain");
                    //start activity with request code listener so if user press home or settings buttons we will close all activities
                    app.startActivityForResult(launchIntent,66);
                }
            });
        }

    }

}