package com.example.taboan_capstone.activity.seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.database.RoomDatabase;
import com.example.taboan_capstone.models.SellerOrderModel;
import com.example.taboan_capstone.views.AdapterSellerOrder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class SellerPortalActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private RoomDatabase roomDatabase;
    private RecyclerView orderList;
    private CardView orderRequest;
    private TextView orderRequestValue;
    private ArrayList<SellerOrderModel> sellerOrderModelArrayList;
    private AdapterSellerOrder adapterSellerOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_portal);
        orderRequest = findViewById(R.id.seller_portal_card_view);
        orderRequestValue = findViewById(R.id.seller_portal_request_order_value);
        orderList = findViewById(R.id.seller_portal_order_list);

        firebaseAuth = FirebaseAuth.getInstance();

        orderRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SellerPortalActivity.this,SellerOrdersActivity.class);
                startActivity(intent);
            }
        });

        loadOrderValue();
        loadOrders();
    }

    private void loadOrderValue(){
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users").child(Objects.requireNonNull(firebaseAuth.getUid())).child("Orders");
        ref.orderByChild("orderStatus").equalTo("Waiting").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalOrders = (int) snapshot.getChildrenCount();
                orderRequestValue.setText("" +totalOrders);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SellerPortalActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadOrders(){
        sellerOrderModelArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users").child(Objects.requireNonNull(firebaseAuth.getUid()));
        ref.child("Orders").orderByChild("orderStatus").equalTo("In Progress")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        sellerOrderModelArrayList.clear();

                        for(DataSnapshot ds : snapshot.getChildren()){
                            SellerOrderModel sellerOrderModel = ds.getValue(SellerOrderModel.class);
                            sellerOrderModelArrayList.add(sellerOrderModel);
                        }
                        adapterSellerOrder = new AdapterSellerOrder(SellerPortalActivity.this, sellerOrderModelArrayList);
                        orderList.setAdapter(adapterSellerOrder);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SellerPortalActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}