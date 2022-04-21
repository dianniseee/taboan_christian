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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterSellerPendingOrder extends  RecyclerView.Adapter<AdapterSellerPendingOrder.AdapterSellerPendingOrderHolder> {

    private Context context;
    private ArrayList<SellerOrderModel> sellerOrderModelArrayList;
    private AdapterSellerPendingOrder.OnAdapterClick listener;
    private FirebaseAuth firebaseAuth;

    public AdapterSellerPendingOrder(Context context, ArrayList<SellerOrderModel> sellerOrderModelArrayList,AdapterSellerPendingOrder.OnAdapterClick listener,FirebaseAuth firebaseAuth) {
        this.context = context;
        this.sellerOrderModelArrayList = sellerOrderModelArrayList;
        this.listener = listener;
        this.firebaseAuth = firebaseAuth;
    }

    @NonNull
    @Override
    public AdapterSellerPendingOrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_seller,null);
        return new AdapterSellerPendingOrderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterSellerPendingOrderHolder holder, int position) {
        SellerOrderModel sellerOrderModel = sellerOrderModelArrayList.get(position);
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users").child(firebaseAuth.getUid()).child("Orders").child(sellerOrderModel.getOrderID()).child("Items");

        holder.orderID.setText(sellerOrderModel.getOrderID());
        holder.orderStatus.setText(sellerOrderModel.getOrderStatus());
        holder.orderStatus.setTextColor(context.getResources().getColor(R.color.colorGreen));

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

    class AdapterSellerPendingOrderHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView orderID,orderItems,orderStatus;
        public AdapterSellerPendingOrderHolder(@NonNull View itemView) {
            super(itemView);
            orderID = itemView.findViewById(R.id.seller_row_order_id_value);
            orderItems = itemView.findViewById(R.id.seller_row_order_item_value);
            orderStatus = itemView.findViewById(R.id.seller_row_order_status);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onAdapterClick(view,getBindingAdapterPosition());
        }
    }
    public interface OnAdapterClick{
        void onAdapterClick(View v,int position);
    }
}
