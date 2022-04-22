package com.example.taboan_capstone.activity.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.database.RoomDatabase;
import com.example.taboan_capstone.databinding.ActivityCustomerHomeBinding;
import com.example.taboan_capstone.databinding.ActivityDriverPortalBinding;
import com.example.taboan_capstone.models.SellerOrderModel;
import com.example.taboan_capstone.models.SellerStoreModel;
import com.example.taboan_capstone.views.AdapterDriverOrder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DriverPortalActivity extends AppCompatActivity {

    private ActivityDriverPortalBinding binding;
    private FirebaseAuth firebaseAuth;
    private RoomDatabase roomDatabase;
    private TextView driverStats;
    private ArrayList<SellerOrderModel> sellerOrderModelArrayList;
    private RecyclerView orderList;
    private RelativeLayout driverHasOrder, driverNoOrder,driverDelivery;
    private AdapterDriverOrder adapterDriverOrder;
    private TextView deliverBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDriverPortalBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        driverStats = findViewById(R.id.driver_portal_status_value);
        driverHasOrder = findViewById(R.id.driver_portal_order);
        driverNoOrder = findViewById(R.id.driver_portal_no_order);
        driverDelivery = findViewById(R.id.driver_portal_delivery);
        orderList = findViewById(R.id.driver_coming_orders);
        deliverBtn = findViewById(R.id.driver_portal_delivery_progress);

        deliverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DriverPortalActivity.this, DriverDeliveryActivity.class);
                startActivity(intent);
            }
        });

        getMarketOrder();

    }
    private void getMarketOrder(){

        if(Globals.currentDriver.getAvailStat().equals("Available") && Globals.currentDriver.getOnline().equals("true") ){
            loadOrders();

        }else if(Globals.currentDriver.getAvailStat().equals("Delivery") && Globals.currentDriver.getOnline().equals("true")){
            driverHasOrder.setVisibility(View.GONE);
            driverNoOrder.setVisibility(View.GONE);
            driverDelivery.setVisibility(View.VISIBLE);

        } else if(Globals.currentDriver.getAvailStat().equals("Offline") && Globals.currentDriver.getOnline().equals("true")){
            driverHasOrder.setVisibility(View.GONE);
            driverNoOrder.setVisibility(View.VISIBLE);
            driverDelivery.setVisibility(View.GONE);

        }
    }

    private void loadOrders(){

        DatabaseReference reference = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        reference.orderByChild("accountType").equalTo("Seller")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for(DataSnapshot ds : snapshot.getChildren()){
                            SellerStoreModel modelShop = ds.getValue(SellerStoreModel.class);

                            assert modelShop != null;
                            String shopUID = modelShop.getUid();

                            sellerOrderModelArrayList = new ArrayList<>();
                            DatabaseReference refOrders = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
                            refOrders.child(shopUID).child("Orders").orderByChild("orderStatus").equalTo("In Progress")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            for(DataSnapshot ds : snapshot.getChildren()){

                                                String timestamp = "" + System.currentTimeMillis();
                                                SellerOrderModel modelOrderShop = ds.getValue(SellerOrderModel.class);
                                                String orderStats = modelOrderShop.getOrderStatus();
                                                String orderDAccepted = ""+ds.child("orderDAccepted").getValue();

                                                if(orderStats.equals("In Progress") &&  orderDAccepted.equals("null")){
                                                    sellerOrderModelArrayList.add(modelOrderShop);
                                                }

                                                adapterDriverOrder = new AdapterDriverOrder(DriverPortalActivity.this, sellerOrderModelArrayList,firebaseAuth,timestamp);
                                                orderList.setAdapter(adapterDriverOrder);

                                                if(adapterDriverOrder.getItemCount() == 0){
                                                    driverHasOrder.setVisibility(View.GONE);
                                                    driverNoOrder.setVisibility(View.VISIBLE);

                                                }else{
                                                    driverHasOrder.setVisibility(View.VISIBLE);
                                                    driverNoOrder.setVisibility(View.GONE);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(DriverPortalActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    });
                        }
                        String timestamp = "" + System.currentTimeMillis();
                        adapterDriverOrder = new AdapterDriverOrder(DriverPortalActivity.this, sellerOrderModelArrayList,firebaseAuth,timestamp);
                        orderList.setAdapter(adapterDriverOrder);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(DriverPortalActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}