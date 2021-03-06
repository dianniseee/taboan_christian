package com.example.taboan_capstone.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.models.ItemsModel;
import com.example.taboan_capstone.models.ProductModel;
import com.example.taboan_capstone.models.SellerOrderModel;
import com.example.taboan_capstone.models.SellerStoreModel;
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

public class AdapterDriverOrder extends  RecyclerView.Adapter<AdapterDriverOrder.AdapterDriverOrderHolder>{

    private Context context;
    private ArrayList<SellerOrderModel> sellerOrderModelArrayList;
    private FirebaseAuth firebaseAuth;
    private String timestamp;

    public AdapterDriverOrder(Context context, ArrayList<SellerOrderModel> sellerOrderModelArrayList, FirebaseAuth firebaseAuth,String timestamp) {
        this.context = context;
        this.sellerOrderModelArrayList = sellerOrderModelArrayList;
        this.firebaseAuth = firebaseAuth;
        this.timestamp = timestamp;
    }

    @NonNull
    @Override
    public AdapterDriverOrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_driver,parent,false);
        return new AdapterDriverOrderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDriverOrderHolder holder, int position) {
        SellerOrderModel sellerOrderModel = sellerOrderModelArrayList.get(position);

        holder.storeName.setText(sellerOrderModel.getOrderMarket());
        holder.orderId.setText("#" + sellerOrderModel.getOrderID());

        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updaterOrder(sellerOrderModel);
            }
        });

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void updaterOrder(SellerOrderModel sellerOrderModel){

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("orderID", "" + sellerOrderModel.getOrderID());
        hashMap.put("userID", "" + sellerOrderModel.getUserID());
        hashMap.put("orderBy", "" + sellerOrderModel.getOrderBy());
        hashMap.put("orderTo", "" + sellerOrderModel.getOrderTo());
        hashMap.put("orderMarket", "" + sellerOrderModel.getOrderMarket());
        hashMap.put("orderDateTime", "" + sellerOrderModel.getOrderDateTime());
        hashMap.put("orderDriverID","" + ""+firebaseAuth.getUid());
        hashMap.put("orderStatus", ""+ "Delivery");
        hashMap.put("orderSubTotal",""+sellerOrderModel.getOrderSubTotal());
        hashMap.put("orderTotal", "" + sellerOrderModel.getOrderTotal());

        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.child(firebaseAuth.getUid()).child("Orders").child(sellerOrderModel.getOrderID()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        addOrderItems(sellerOrderModel);
                        updateShopSelected(sellerOrderModel);
                        driverStatus();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addOrderItems(SellerOrderModel sellerOrderModel){
        String orderID = sellerOrderModel.getOrderID();
        String orderTo = sellerOrderModel.getOrderTo();

        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.child(orderTo).child("Orders").child(orderID).child("Items")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            ItemsModel itemsModel = ds.getValue(ItemsModel.class);

                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("id", "" + itemsModel.getId());
                            hashMap1.put("productID", "" + itemsModel.getProductID());
                            hashMap1.put("productName", "" + itemsModel.getProductName());
                            hashMap1.put("product_Desc", "" + itemsModel.getProduct_Desc());
                            hashMap1.put("product_category", "" + itemsModel.getProduct_category());
                            hashMap1.put("price_each", "" + itemsModel.getPrice_each());
                            hashMap1.put("quantity", "" + itemsModel.getQuantity());
                            hashMap1.put("subtotal", "" + itemsModel.getSubtotal());

                            DatabaseReference itemRef = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
                            itemRef.child(firebaseAuth.getUid()).child("Orders").child(orderID).child("Items").child(itemsModel.getProductID()).setValue(hashMap1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(context, "Orders Items Receive", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateShopSelected(SellerOrderModel sellerOrderModel){
        String orderID = sellerOrderModel.getOrderID();
        String orderTo = sellerOrderModel.getOrderTo();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("orderDriverID",""+firebaseAuth.getUid());
        hashMap.put("orderStatus","Delivery");

        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.child(orderTo).child("Orders").child(orderID).updateChildren(hashMap);

        Globals.INSTANCE.setAvailable("Delivery");
    }

    private void driverStatus(){

        String availStatus = "Delivery";

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("availStat",""+availStatus);
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.child(Objects.requireNonNull(firebaseAuth.getUid())).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Driver on Delivery status
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return sellerOrderModelArrayList.size();
    }

    class AdapterDriverOrderHolder extends RecyclerView.ViewHolder{

        private TextView storeName,orderId;
        private Button cancel,accept;
        public AdapterDriverOrderHolder(@NonNull View itemView) {
            super(itemView);

            storeName = itemView.findViewById(R.id.row_driver_shop_name);
            orderId = itemView.findViewById(R.id.row_driver_shop_order_id);
            cancel = itemView.findViewById(R.id.row_driver_shop_order_cancel);
            accept = itemView.findViewById(R.id.row_driver_shop_order_accept);

        }
    }
}
