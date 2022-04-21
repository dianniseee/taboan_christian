package com.example.taboan_capstone.activity.seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Adapter;
import android.widget.Toast;

import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.database.RoomDatabase;
import com.example.taboan_capstone.models.ProductModel;
import com.example.taboan_capstone.views.AdapterSellerMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class SellerMenuActivity extends AppCompatActivity {

    private RoomDatabase roomDatabase;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ProductModel> productList;
    private RecyclerView sellerMenuList;
    private AdapterSellerMenu adapterSellerMenu;

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
                        adapterSellerMenu = new AdapterSellerMenu(SellerMenuActivity.this, productList);
                        sellerMenuList.setAdapter(adapterSellerMenu);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SellerMenuActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}