package com.example.taboan_capstone.activity.seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.activity.driver.DriverDeliveryActivity;
import com.example.taboan_capstone.database.RoomDatabase;
import com.example.taboan_capstone.models.DriverOrderModel;
import com.example.taboan_capstone.models.DriverProductModel;
import com.example.taboan_capstone.models.SellerOrderModel;
import com.example.taboan_capstone.models.SellerProductModel;
import com.example.taboan_capstone.views.AdapterDriverItems;
import com.example.taboan_capstone.views.AdapterSellerItems;
import com.example.taboan_capstone.views.AdapterSellerOrder;
import com.example.taboan_capstone.views.AdapterSellerProduct;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SellerPortalActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private RoomDatabase roomDatabase;
    private RecyclerView orderList;
    private CardView orderRequest;
    private TextView orderRequestValue;
    private ArrayList<SellerOrderModel> sellerOrderModelArrayList;
    private ArrayList<SellerProductModel> sellerProductModelArrayList;
    private AdapterSellerOrder adapterSellerOrder;
    private AdapterSellerOrder.OnAdapterClick mListener;
    private AdapterSellerItems adapterSellerItems;
    private String customerFullName;

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
        loadOrders(this);
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

    private void loadOrders(Context context){
        sellerOrderModelArrayList = new ArrayList<>();
        sellerProductModelArrayList = new ArrayList<>();

        setOnclickListenerAdapter(context);

        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users").child(Objects.requireNonNull(firebaseAuth.getUid()));
        ref.child("Orders").orderByChild("orderStatus").equalTo("In Progress")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        sellerOrderModelArrayList.clear();

                        for(DataSnapshot ds : snapshot.getChildren()){
                            SellerOrderModel sellerOrderModel = ds.getValue(SellerOrderModel.class);
                            sellerOrderModelArrayList.add(sellerOrderModel);

                            DatabaseReference refItems = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
                            refItems.child(Objects.requireNonNull(firebaseAuth.getUid())).child("Orders").child(sellerOrderModel.getOrderID()).child("Items")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            sellerProductModelArrayList.clear();

                                            for(DataSnapshot ds : snapshot.getChildren()){

                                                SellerProductModel driverProductModel = ds.getValue(SellerProductModel.class);
                                                sellerProductModelArrayList.add(driverProductModel);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(SellerPortalActivity.this, "1"+error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });


                            DatabaseReference custRef = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
                            custRef.orderByChild("uid").equalTo(sellerOrderModel.getOrderBy())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for(DataSnapshot ds : snapshot.getChildren()){

                                                String fName = ""+ds.child("first_name").getValue();
                                                String lName = ""+ds.child("last_name").getValue();
                                                customerFullName = fName+" "+lName;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(SellerPortalActivity.this, "8"+error.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        adapterSellerOrder = new AdapterSellerOrder(SellerPortalActivity.this, sellerOrderModelArrayList,mListener);
                        orderList.setAdapter(adapterSellerOrder);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SellerPortalActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setOnclickListenerAdapter(Context context){
        mListener = new AdapterSellerOrder.OnAdapterClick() {
            @Override
            public void onAdapterClick(View v, int position) {

                String getOrderId = sellerOrderModelArrayList.get(position).getOrderID();
                String getOrderBy = sellerOrderModelArrayList.get(position).getOrderBy();
                String getOrderTo = sellerOrderModelArrayList.get(position).getOrderTo();

                showDialog(context,getOrderId,getOrderBy,getOrderTo);
            }
        };
    }

    private void showDialog(Context context,String getOrderId, String orderBy,String getOrderTo){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.custom_dialog_seller_items,null);

        SellerOrderModel sellerOrderModel = new SellerOrderModel();

        TextView orderId = view.findViewById(R.id.custom_dialog_seller_order_id);
        TextView orderName = view.findViewById(R.id.custom_dialog_seller_order_name);
        TextView totalValue = view.findViewById(R.id.custom_dialog_seller_total_value);
        Button orderReady = view.findViewById(R.id.custom_dialog_seller_delivered);
        RecyclerView orderItems = view.findViewById(R.id.custom_dialog_seller_order_items);

        orderId.setText(""+sellerOrderModel.getOrderID());
        orderName.setText(""+customerFullName);
        totalValue.setText(""+sellerOrderModel.getOrderTotal());

        adapterSellerItems = new AdapterSellerItems(context,sellerProductModelArrayList);
        orderItems.setAdapter(adapterSellerItems);

        orderReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateOrders(getOrderId,orderBy,getOrderTo);
            }
        });

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateOrders(String orderId, String orderTo, String orderBy){
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("orderStatus","Ready");

        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.child(orderTo).child("Orders").child(orderId).updateChildren(hashMap);

        DatabaseReference custRef = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        custRef.child(orderBy).child("Orders").child(orderId).updateChildren(hashMap);

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