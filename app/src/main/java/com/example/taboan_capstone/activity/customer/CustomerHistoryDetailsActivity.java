package com.example.taboan_capstone.activity.customer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.database.RoomDatabase;
import com.example.taboan_capstone.models.CustomerHistoryModel;
import com.example.taboan_capstone.views.AdapterCustomerCart;
import com.example.taboan_capstone.views.AdapterCustomerHistory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerHistoryDetailsActivity extends AppCompatActivity {


    private RoomDatabase roomDatabase;
    private FirebaseAuth firebaseAuth;

    private ImageView back,coverPhoto;
    private TextView storeName,marketName,orderId,subtotal,total;
    private RecyclerView orderList;

    private String getOrderId,getorderTo;

    private ArrayList<CustomerHistoryModel> customerHistoryModelArrayList;
    private AdapterCustomerHistory adapterCustomerHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_history_details);

        back = findViewById(R.id.customer_history_close_history);
        coverPhoto = findViewById(R.id.customer_history_cover_photo);
        storeName = findViewById(R.id.customer_history_store_name);
        marketName = findViewById(R.id.customer_history_market_name);
        orderId = findViewById(R.id.customer_history_order_id_value);
        subtotal = findViewById(R.id.customer_history_subtotal_value);
        total = findViewById(R.id.customer_history_total_value);
        orderList = findViewById(R.id.customer_history_order_list);

        firebaseAuth = FirebaseAuth.getInstance();

        if(getIntent() != null){
            getOrderId = getIntent().getStringExtra("orderId");
            getorderTo = getIntent().getStringExtra("orderTo");
        }

        orderId.setText(getOrderId);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loadStore(getorderTo);

        loadOrderDetails(getOrderId,getorderTo);
    }

    private void loadStore(String getOrderTo){
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.orderByChild("uid").equalTo(getOrderTo)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {


                            String sName = "" + ds.child("store_name").getValue();
                            String sMarket = "" + ds.child("store_market").getValue();
                            String shopImg = "" +ds.child("cover_photo").getValue();

                            Glide.with(CustomerHistoryDetailsActivity.this).load(shopImg).into(coverPhoto);
                            storeName.setText(sName);
                            marketName.setText(sMarket);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CustomerHistoryDetailsActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadOrderDetails(String getOrderId,String getOrderTo){
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users").child(getOrderTo);
        ref.child("Orders").orderByChild("orderID").equalTo(getOrderId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){

                            String orderTotal = ""+ds.child("orderTotal").getValue();
                            String orderTO = ""+ds.child("orderTo").getValue();
                            String orderId = ""+ds.child("orderID").getValue();
                            total.setText("₱ " +orderTotal);
                            subtotal.setText("₱ " + orderTotal);

                            loadOrderedItems(orderTO,orderId);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CustomerHistoryDetailsActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadOrderedItems(String getOrderTo,String getOrderId){

        customerHistoryModelArrayList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.child(getOrderTo).child("Orders").child(getOrderId).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        customerHistoryModelArrayList.clear();

                        for(DataSnapshot ds: snapshot.getChildren()){
                            CustomerHistoryModel modelOrderHistory = ds.getValue(CustomerHistoryModel.class);
                            customerHistoryModelArrayList.add(modelOrderHistory);
                        }

                        adapterCustomerHistory = new AdapterCustomerHistory(CustomerHistoryDetailsActivity.this, customerHistoryModelArrayList);
                        orderList.setAdapter(adapterCustomerHistory);
                        adapterCustomerHistory.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(CustomerHistoryDetailsActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}