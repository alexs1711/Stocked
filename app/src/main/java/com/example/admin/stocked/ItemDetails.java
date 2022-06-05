package com.example.admin.stocked;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import de.hdodenhof.circleimageview.CircleImageView;

public class ItemDetails extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    DatabaseReference mdatabaseReference,idatabaseReference,TdatabaseReference,TTdatabaseReference;
    private TextView UpdateCategoryTop,ItemDescription,TotalValueT,TotalStockT,QuantityText;
    Button addTransaction,editItem;
    RecyclerView mrecyclerview;
    public static ItemDetails app;
    static String itemUnit = "";
    LinearLayoutManager manager;
    String userUID;
    Float totalValue = 0f;
    Float StockAvailable = 0f;
    static String currency;
    private static final SimpleDateFormat sdf3 = new SimpleDateFormat("dd/MM/yyyy");
    Button dashboard, products, addprod, settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);
        app=this;

        //intent to receive the selected category
        Intent intent = getIntent();
        final String ItemUuid=intent.getStringExtra("Item_uuid");
        final String Code_cat=intent.getStringExtra("Code_cat");

        //conseguir usuario firebase
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser users = firebaseAuth.getCurrentUser();
        userUID = users.getUid();

        UpdateCategoryTop = findViewById(R.id.UpdateItemTopT);
        ItemDescription = findViewById(R.id.UpdateItemDescription);
        TotalValueT = findViewById(R.id.TotalValueItem);
        TotalStockT = findViewById(R.id.totalStock);
        QuantityText = findViewById(R.id.QuantityText);
        //buttons ids
        addTransaction = findViewById(R.id.UpdateItem);
        editItem = findViewById(R.id.editItem);

        //Buttons of the dashboard
        dashboard = findViewById(R.id.dashboard);
        dashboard.setOnClickListener((View.OnClickListener) this);

        products = findViewById(R.id.products);
        products.setOnClickListener((View.OnClickListener) this);

        addprod = findViewById(R.id.addtoDB);
        addprod.setOnClickListener((View.OnClickListener) this);

        settings = findViewById(R.id.settings);
        settings.setOnClickListener((View.OnClickListener) this);

        //obtener moneda
        DatabaseReference UdatabaseReference;


        UdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("UserDetails").child("currency");

        UdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currency = dataSnapshot.getValue(String.class);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        addTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = new Intent(getApplicationContext(), addTransaction.class);
                launchIntent.putExtra("Item_uuid", ItemUuid);//mando la random key del item
                launchIntent.putExtra("Code_cat", Code_cat);
                launchIntent.setType("text/plain");
                startActivity(launchIntent);
            }
        });

        editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = new Intent(getApplicationContext(), EditItemActivity.class);
                launchIntent.putExtra("Item_uuid", ItemUuid);//mando la random key del item
                launchIntent.putExtra("Code_cat", Code_cat);
                launchIntent.setType("text/plain");
                startActivityForResult(launchIntent,2);
            }
        });

        mdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("Items").child(ItemUuid);
        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                itemUnit = "not loaded";
                 Items it = dataSnapshot.getValue(Items.class);
                    if (it != null) {
                        itemUnit = it.getItemunits();
                    }
                    else{
                        itemUnit = "not specified";
                    }
                QuantityText.setText(itemUnit);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //idatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(resultemail).child("ItemByCategory").child(Code_cat).child(ItemUuid);
        idatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("Items").child(ItemUuid);
        idatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Items item = dataSnapshot.getValue(Items.class);
                    UpdateCategoryTop.setText(item.getItemname());
                    ItemDescription.setText(item.getDescription());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //TdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(resultemail).child("ItemTransactions").child(Code_cat).child(ItemUuid);
        TdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("ItemTransactions").child(ItemUuid);
        TdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totalValue = 0f;
                StockAvailable = 0f;
                if(dataSnapshot.exists()){
                     totalValue = 0f;
                     StockAvailable = 0f;
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        ItemTransaction itemT = ds.getValue(ItemTransaction.class);
                        if(itemT.isItemEntering()){
                            totalValue = totalValue - (itemT.getPricePerUnity()*itemT.getQuantity());
                            StockAvailable = StockAvailable + (itemT.getQuantity());
                        }
                        else if(!itemT.isItemEntering()){
                            totalValue = totalValue + (itemT.getPricePerUnity()*itemT.getQuantity());
                            StockAvailable =  StockAvailable - (itemT.getQuantity());
                        }
                    }
                    TotalValueT.setText(totalValue + " "+currency);
                    TotalStockT.setText(StockAvailable + "");

                }
                else {
                    TotalValueT.setText(totalValue + " "+currency);
                    TotalStockT.setText(StockAvailable + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mrecyclerview = findViewById(R.id.Rtransactions);
        manager = new LinearLayoutManager(this);//scrolleabe como list view para ver varias listas de elementos
        mrecyclerview.setLayoutManager(manager);
        mrecyclerview.setHasFixedSize(false);
        //mrecyclerview.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    public void onClick(View view) {
        Intent i;
        Intent launchIntent = new Intent();
        switch (view.getId()) {
            case R.id.dashboard:
                i = new Intent(this, activity_dashboard.class);
                //go back to activity home without creating a new one
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                //this way we also close categories activity with code 66 (it can be any code we want)
                setResult(66,launchIntent);
                this.finish();
                break;
            case R.id.products:
                i = new Intent(this, CategoriesActivity.class);
                startActivity(i);
                setResult(66,launchIntent);
                this.finish();
                break;
            case R.id.addtoDB:
                i = new Intent(this, AddselectorActivity.class);
                startActivity(i);
                setResult(66,launchIntent);
                this.finish();
                break;
            case R.id.settings:
                i = new Intent(this, ConfigurationActivity.class);
                startActivity(i);
                setResult(66,launchIntent);
                this.finish();
                break;
            default:
                break;
        }
    }

    //if item deleted the destroy this activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==2)
        {
            setResult(2,data);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Query query = TdatabaseReference.orderByChild("timeStamp");

        FirebaseRecyclerOptions<ItemTransaction> options =
                new FirebaseRecyclerOptions.Builder<ItemTransaction>()
                        .setQuery(query, ItemTransaction.class)
                        .build();

        FirebaseRecyclerAdapter<ItemTransaction, ItemDetails.ProductViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ItemTransaction, ItemDetails.ProductViewHolder>
                        (options)
        {
            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.transaction_list_layout, parent, false);
                return new  ItemDetails.ProductViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ItemDetails.ProductViewHolder holder, int position, @NonNull ItemTransaction model) {
                holder.setDetails(getApplicationContext(), model.isItemEntering() , model.getPricePerUnity(),model.getQuantity(),model.getTimeStamp(),model.getItemuuid(),model.getUuid(),getString(R.string.entering),getString(R.string.leaving));
            }

        };

        firebaseRecyclerAdapter.startListening();
        manager.setReverseLayout(true);/*order by most recent date instead of oldest one*/
        manager.setStackFromEnd(true);//when you reverse layout it stack from the end since it's reversing the order literally it back so it stack from the top :)
        mrecyclerview.setAdapter(firebaseRecyclerAdapter);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public ProductViewHolder(View itemView){
            super(itemView);
            mView =itemView;
        }

        public void setDetails(Context ctx, final Boolean ItemEntering, final Float pricePerUnit, final float quantity,final Long date,final String ItemUuid,final String TransactionUuid,String entering,String leaving) {
            CircleImageView item_image = mView.findViewById(R.id.item_image);
            TextView EnteringText = (TextView) mView.findViewById(R.id.EnteringText);
            TextView priceT = (TextView) mView.findViewById(R.id.priceT);
            TextView ItemQuantity = (TextView) mView.findViewById(R.id.ItemQuantity);
            TextView TimeText = (TextView) mView.findViewById(R.id.TimeText);

            if(ItemEntering){
                Resources res = app.getResources();
                Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.ic_arrow_down, null);
                item_image.setImageDrawable(drawable);
                EnteringText.setText(entering);
                priceT.setText("-"+pricePerUnit*quantity+" "+currency);
                //no set text color since it's already green
                ItemQuantity.setText("+"+quantity+" "+itemUnit);
            }
            else{
                Resources res = app.getResources();
                Drawable drawable = ResourcesCompat.getDrawable(res, R.drawable.ic_arrow_up, null);
                item_image.setImageDrawable(drawable);
                EnteringText.setText(leaving);
                priceT.setText("+"+pricePerUnit*quantity+" "+currency);
                ItemQuantity.setText("-"+quantity+" "+itemUnit);
                ItemQuantity.setTextColor(Color.RED);
            }
            Timestamp pp = new Timestamp(date);

            String dateToFormat = sdf3.format(pp);
            TimeText.setText(dateToFormat);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(app, "you clicked "+quantity + " "+ pricePerUnit*quantity, Toast.LENGTH_SHORT).show();
                    Intent launchIntent = new Intent(app, EditTransactionActivity.class);
                    launchIntent.putExtra("Item_uuid", ItemUuid);//mando la random key del item
                    launchIntent.putExtra("Transaction_Uuid", TransactionUuid);//key de la transaccion
                    launchIntent.setType("text/plain");
                    app.startActivity(launchIntent);
                }
            });
        }

    }
}