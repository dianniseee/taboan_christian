package com.example.taboan_capstone.views;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.models.SellerOrderModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterSellerOrder extends RecyclerView.Adapter<AdapterSellerOrder.AdapterSellerOrderHolder> {

    private final Context context;
    private ArrayList<SellerOrderModel> sellerOrderModelArrayList;

    public AdapterSellerOrder(Context context, ArrayList<SellerOrderModel> sellerOrderModelArrayList) {
        this.context = context;
        this.sellerOrderModelArrayList = sellerOrderModelArrayList;
    }

    @NonNull
    @Override
    public AdapterSellerOrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_seller,null);
        return new AdapterSellerOrderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSellerOrderHolder holder, int position) {
        SellerOrderModel sellerOrderModel = sellerOrderModelArrayList.get(position);
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users").child(sellerOrderModel.getOrderTo()).child("Orders").child(sellerOrderModel.getOrderID()).child("Items");

        holder.orderID.setText(sellerOrderModel.getOrderID());
        holder.orderStatus.setText(sellerOrderModel.getOrderStatus());
        holder.orderStatus.setTextColor(context.getResources().getColor(R.color.colorBlue));
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalOrders = (int) snapshot.getChildrenCount();

                holder.orderItems.setText(""+totalOrders);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("OrdersCount",""+ error.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return sellerOrderModelArrayList.size();
    }

    class AdapterSellerOrderHolder extends RecyclerView.ViewHolder{

        private TextView orderID,orderItems,orderStatus;
        public AdapterSellerOrderHolder(@NonNull View itemView) {
            super(itemView);
            orderID = itemView.findViewById(R.id.seller_row_order_id_value);
            orderItems = itemView.findViewById(R.id.seller_row_order_item_value);
            orderStatus = itemView.findViewById(R.id.seller_row_order_status);
        }
    }
}
