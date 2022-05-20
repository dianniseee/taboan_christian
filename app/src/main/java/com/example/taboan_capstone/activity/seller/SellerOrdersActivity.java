package com.example.taboan_capstone.activity.seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.database.RoomDatabase;
import com.example.taboan_capstone.models.SellerOrderModel;
import com.example.taboan_capstone.views.AdapterSellerPendingOrder;
import com.example.taboan_capstone.views.AdapterSellerProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SellerOrdersActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private RoomDatabase roomDatabase;

    private ArrayList<SellerOrderModel> sellerOrderModelArrayList;
    private AdapterSellerPendingOrder adapterSellerPendingOrder;
    private AdapterSellerPendingOrder.OnAdapterClick mAdapterClick;
    private RecyclerView incomingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_orders);

        incomingList = findViewById(R.id.seller_order_incoming_list);

        firebaseAuth = FirebaseAuth.getInstance();
        loadOrders();
        setOnClickListenerAdapter();
    }

    private void loadOrders(){

        sellerOrderModelArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users").child(Objects.requireNonNull(firebaseAuth.getUid()));
        ref.child("Orders").orderByChild("orderStatus").equalTo("Waiting")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        sellerOrderModelArrayList.clear();

                        for(DataSnapshot ds : snapshot.getChildren()){
                            SellerOrderModel sellerOrderModel = ds.getValue(SellerOrderModel.class);
                            sellerOrderModelArrayList.add(sellerOrderModel);
                        }
                        adapterSellerPendingOrder = new AdapterSellerPendingOrder(SellerOrdersActivity.this, sellerOrderModelArrayList,mAdapterClick,firebaseAuth);

                        incomingList.setAdapter(adapterSellerPendingOrder);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SellerOrdersActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setOnClickListenerAdapter(){
        mAdapterClick = new AdapterSellerPendingOrder.OnAdapterClick() {
            @Override
            public void onAdapterClick(View v, int position) {
                String orderTo = sellerOrderModelArrayList.get(position).getOrderTo();
                String orderID = sellerOrderModelArrayList.get(position).getOrderID();

                AlertDialog.Builder builder = new AlertDialog.Builder(SellerOrdersActivity.this);
                View view = getLayoutInflater().inflate(R.layout.order_status_dialog,null);
                builder.setView(view);
                Button orderAccept = (Button) view.findViewById(R.id.order_status_dialog_accept);
                Button orderCancel = (Button) view.findViewById(R.id.order_status_dialog_cancel);



                AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    orderAccept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getOrderDetails(orderID,orderTo,true);
                            dialog.dismiss();
                        }
                    });

                    orderCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getOrderDetails(orderID,orderTo,false);
                            dialog.dismiss();
                        }
                    });

                },500);
            }
        };
    }

    private void getOrderDetails(String orderID,String orderTO,boolean isAccept){

        String orderStat;
        if(isAccept){ orderStat = "In Progress"; }else{ orderStat = "Cancelled"; }

        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.child(Objects.requireNonNull(firebaseAuth.getUid())).child("Orders").orderByChild("orderID").equalTo(orderID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            String orderStatus = ""+ds.child("orderStatus").getValue();
                            String orderBy = ""+ds.child("orderBy").getValue();
                            if(orderStatus.equals("Waiting")){

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("orderStatus", ""+orderStat);

                                DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
                                ref.child(Objects.requireNonNull(firebaseAuth.getUid())).child("Orders").child(orderID).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(SellerOrdersActivity.this, "Order Updated", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SellerOrdersActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                                ref.child(Objects.requireNonNull(orderBy)).child("Orders").child(orderID).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) { }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SellerOrdersActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SellerOrdersActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}