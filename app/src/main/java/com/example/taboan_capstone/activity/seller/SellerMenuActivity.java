package com.example.taboan_capstone.activity.seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.MotionEvent;
import android.widget.Adapter;
import android.widget.Toast;

import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.activity.admin.AdminDashboardActivity;
import com.example.taboan_capstone.activity.customer.CustomerHistoryDetailsActivity;
import com.example.taboan_capstone.activity.customer.CustomerOrderDetailsActivity;
import com.example.taboan_capstone.database.RoomDatabase;
import com.example.taboan_capstone.models.ProductModel;
import com.example.taboan_capstone.views.AdapterSellerMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SellerMenuActivity extends AppCompatActivity {

    private RoomDatabase roomDatabase;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ProductModel> productList;
    private RecyclerView sellerMenuList;
    private AdapterSellerMenu adapterSellerMenu;
    private AdapterSellerMenu.OnAdapterClick onAdapterClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_menu);

        firebaseAuth = FirebaseAuth.getInstance();
        sellerMenuList = findViewById(R.id.seller_menu_list);

        loadProducts();

    }

    private void loadProducts() {
        productList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        reference.child(Objects.requireNonNull(firebaseAuth.getUid())).child("Product").orderByChild("prod_id")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        productList.clear();

                        for(DataSnapshot ds : snapshot.getChildren()){
                            ProductModel modelProduct = ds.getValue(ProductModel.class);
                            productList.add(modelProduct);

                        }
                        adapterSellerMenu = new AdapterSellerMenu(SellerMenuActivity.this, productList,onAdapterClick);
                        sellerMenuList.setAdapter(adapterSellerMenu);
                        adapterSellerMenu.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SellerMenuActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        setOnAdapterClick();
    }

    private void setOnAdapterClick(){
        onAdapterClick = (v, position) -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(SellerMenuActivity.this);
            builder.setMessage("Setup menu list")
                    .setCancelable(false)
                    .setPositiveButton(Html.fromHtml("<font color='#000000'>Available</font>"), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("prod_avail","Available");
                            String prod_id = productList.get(position).getProd_id();

                            DatabaseReference reference = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
                            reference.child(Objects.requireNonNull(firebaseAuth.getUid())).child("Product").child(prod_id)
                                    .updateChildren(hashMap);
                        }
                    }).setNegativeButton(Html.fromHtml("<font color='#000000'>Out of Stock</font>"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("prod_avail","Out of Stock");
                    String prod_id = productList.get(position).getProd_id();

                    DatabaseReference reference = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
                    reference.child(Objects.requireNonNull(firebaseAuth.getUid())).child("Product").child(prod_id)
                            .updateChildren(hashMap);
                }
            })
            .setNeutralButton(Html.fromHtml("<font color='#000000'>Close</font>"), new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

        };
    }
}