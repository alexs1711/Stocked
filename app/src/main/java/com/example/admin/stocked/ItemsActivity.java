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

public class ItemsActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth firebaseAuth;
    RecyclerView mrecyclerview;
    DatabaseReference mdatabaseReference,CdatabaseReference,TdatabaseReference;
    private Button editBt,totalnºofselectedcategory,TotalCost,TotalSales;
    private TextView selectedcategoryname;
    public int counttotalnoofitem = 0;
    public float profit = 0,spent = 0;
    public static ItemsActivity app ;
    String catcoderef;
    Button dashboard, products, addprod, settings;
    String userUID;
    String currency="NONE";
    Boolean currencyLoad=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        DatabaseReference UdatabaseReference;

        //intent to receive the selected category
        Intent intent = getIntent();
        catcoderef=intent.getStringExtra("Product_Name_Intent");

        //textviews of total number and nº of stock in hand
        totalnºofselectedcategory = findViewById(R.id.TotalNumber);
        TotalCost = findViewById(R.id.TotalCost);
        TotalSales = findViewById(R.id.TotalSales);
        selectedcategoryname = findViewById(R.id.selectedcategory);

        //dashboard buttons

        //Buttons of the dashboard
        dashboard = findViewById(R.id.dashboard);
        dashboard.setOnClickListener((View.OnClickListener) this);

        products = findViewById(R.id.products);
        products.setOnClickListener((View.OnClickListener) this);

        addprod = findViewById(R.id.addtoDB);
        addprod.setOnClickListener((View.OnClickListener) this);

        settings = findViewById(R.id.settings);
        settings.setOnClickListener((View.OnClickListener) this);

        //conseguir usuario firebase
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser users = firebaseAuth.getCurrentUser();
        userUID = users.getUid();

        //referencia en la base de datos

        mdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("Items");//la cosa es que si quito la cosa de name funciona
        CdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("CategoryDetails").child(catcoderef);
        TdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("ItemTransactions");


        UdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("UserDetails").child("currency");

        UdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    currency = dataSnapshot.getValue(String.class);
                    currencyLoad = true;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        //here we obtain the name of the category with the category code so we will get the value in the node
        CdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Category editCat = dataSnapshot.getValue(Category.class);
                if(editCat!=null) {
                    String categoryname = editCat.getName();
                    selectedcategoryname.setText(categoryname);
                    //tener en cuenta valores nulos !!!!!
                    //java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.String com.example.admin.stocked.Category.getName()' on a null object reference
                    //        at com.example.admin.stocked.ItemsActivity$1.onDataChange(ItemsActivity.java:71)
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        //Recycler view layout id
        mrecyclerview = findViewById(R.id.recyclerViewCat);

        //Buttons references
        editBt = findViewById(R.id.editCat);
        editBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchIntent = new Intent(app, EditCategoryActivity.class);
                launchIntent.putExtra("Product_Name_Intent", catcoderef);// cambiar esto
                launchIntent.setType("text/plain");
                startActivityForResult(launchIntent,2);
            }

        });

        //static reference to this activity
        app = this;


        LinearLayoutManager manager = new LinearLayoutManager(this);//scrolleabe como list view para ver varias listas de elementos
        mrecyclerview.setLayoutManager(manager);
        mrecyclerview.setHasFixedSize(false);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mdatabaseReference.addValueEventListener(new ValueEventListener() {//listener para los cambios de los items
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                spent = 0f;
                profit = 0f;
                counttotalnoofitem = 0;
                if (dataSnapshot.exists()) {
                    if(currencyLoad){

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        //get individual items and check if they have the same cat code
                        Items it = ds.getValue(Items.class);
                        if(it.getCatuuid().equals(catcoderef)){
                            counttotalnoofitem = counttotalnoofitem + 1;
                            TdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("ItemTransactions").child(it.getUuid());
                            TdatabaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.exists()) {
                                        spent = 0f;
                                        profit = 0f;
                                        for (DataSnapshot ds : dataSnapshot.getChildren()){
                                            ItemTransaction itemtrans =   ds.getValue(ItemTransaction.class);
                                            if(itemtrans.isItemEntering()){
                                                spent = spent + (itemtrans.getPricePerUnity() * itemtrans.getQuantity());
                                            }
                                            if(!itemtrans.isItemEntering()){
                                                profit  = profit + (itemtrans.getPricePerUnity() * itemtrans.getQuantity());
                                            }
                                        }
                                        TotalCost.setText(spent+" "+currency);
                                        TotalSales.setText(profit+" "+currency);
                                    }
                                    else{

                                    TotalCost.setText(spent+" "+currency);
                                    TotalSales.setText(profit+" "+currency);}
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                    }
                        TotalCost.setText(spent+" "+currency);
                        TotalSales.setText(profit+" "+currency);

                    totalnºofselectedcategory.setText(Integer.toString(counttotalnoofitem));

                   
                }}else{
                    TotalCost.setText(spent + " " + currency);
                    TotalSales.setText(profit + " " + currency);
                    totalnºofselectedcategory.setText(counttotalnoofitem);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                TotalCost.setText(spent + " " + currency);
                TotalSales.setText(profit + " " + currency);
                totalnºofselectedcategory.setText(counttotalnoofitem);
            }
        });


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
                //
                startActivity(i);
                //this way we also close categories activity
                setResult(66,launchIntent);
                finish();
                break;
            case R.id.products:
                i = new Intent(this, CategoriesActivity.class);
                startActivity(i);
                setResult(66,launchIntent);
                finish();
                break;
            case R.id.addtoDB:
                i = new Intent(this, AddselectorActivity.class);
                startActivity(i);
                setResult(66,launchIntent);
                finish();
                break;
            case R.id.settings:
                i = new Intent(this, ConfigurationActivity.class);
                startActivity(i);
                setResult(66,launchIntent);
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
            setResult(66,data);
            finish();
        }
        if(resultCode==2)
        {
            finish();
        }
    }

    @Override
    protected  void  onStart() {
        super.onStart();

        Query query = mdatabaseReference.orderByChild("catuuid").equalTo(catcoderef);

        FirebaseRecyclerOptions<Items> options =
                new FirebaseRecyclerOptions.Builder<Items>()
                        .setQuery(query, Items.class)
                        .build();

        FirebaseRecyclerAdapter<Items, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Items, UsersViewHolder>(options)
        {
            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_list_layout, parent, false);
                return new ItemsActivity.UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ItemsActivity.UsersViewHolder holder, int position, @NonNull Items model) {
                holder.setDetails(getApplicationContext(), model.getItemname(),model.getImagelocation(), model.getUuid(),catcoderef);
            }
        };

        firebaseRecyclerAdapter.startListening();
        mrecyclerview.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public UsersViewHolder(View itemView){
            super(itemView);
            mView =itemView;
        }

        public void setDetails(Context ctx, final String itemname,final String imagelocation,final String uuid,final String CategoryCode){
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
                    Intent launchIntent = new Intent(app, ItemDetails.class);
                    launchIntent.putExtra("Item_uuid", uuid);//mando la random key de cada categoria para que asi abra esa categoria en concreto
                    launchIntent.putExtra("Code_cat", CategoryCode);
                    launchIntent.setType("text/plain");
                    app.startActivityForResult(launchIntent,66);
                }
            });
        }


    }
}