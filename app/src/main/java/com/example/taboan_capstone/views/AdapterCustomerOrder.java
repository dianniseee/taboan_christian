package com.example.taboan_capstone.views;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.activity.customer.CustomerHistoryDetailsActivity;
import com.example.taboan_capstone.activity.customer.CustomerOrderDetailsActivity;
import com.example.taboan_capstone.models.CustomerOrderDetailsModel;
import com.example.taboan_capstone.models.ProductModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class AdapterCustomerOrder extends  RecyclerView.Adapter<AdapterCustomerOrder.CustomerOrderHolder>{

    private final Context context;
    public ArrayList<CustomerOrderDetailsModel> customerOrderDetailsModelArrayList;
    private AdapterCustomerOrder.OnAdapterClick listener;
    private String fbUserId;

    public AdapterCustomerOrder(Context context, ArrayList<CustomerOrderDetailsModel> customerOrderDetailsModelArrayList, AdapterCustomerOrder.OnAdapterClick listener,String fbUserId) {
        this.context = context;
        this.customerOrderDetailsModelArrayList = customerOrderDetailsModelArrayList;
        this.listener = listener;
        this.fbUserId = fbUserId;
    }

    @NonNull
    @Override
    public CustomerOrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_items,parent,false);
        return new CustomerOrderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerOrderHolder holder, int position) {
        CustomerOrderDetailsModel orderDetail = customerOrderDetailsModelArrayList.get(position);

        String orderBy = orderDetail.getOrderBy();
        String orderSubtotal = orderDetail.getOrderTotal();
        String orderDateTime = orderDetail.getOrderDateTime();
        String orderStats = orderDetail.getOrderStatus();
        String orderTo = orderDetail.getOrderTo();
        String orderId = orderDetail.getOrderID();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderDateTime));
        String formattedDate = DateFormat.format("MM/dd/yyyy",calendar).toString();

        if(orderBy.equals(fbUserId)){

            loadStoreName(orderTo,holder);

            holder.orderTime.setText(formattedDate);
            holder.orderTotal.setText(orderSubtotal);
            holder.orderStatus.setText(orderStats);

            holder.orderView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                if(orderStats.equals("Completed")){
                        Intent intent = new Intent(context, CustomerHistoryDetailsActivity.class);
                        intent.putExtra("orderTo",orderTo);
                        intent.putExtra("orderId",orderId);
                        context.startActivity(intent);
                } else{
                    Intent intent = new Intent(context,CustomerOrderDetailsActivity.class);
                    intent.putExtra("orderTo",orderTo);
                    intent.putExtra("orderId",orderId);
                    context.startActivity(intent);
                     }
                }
            });
        }
    }

    private void loadStoreName(String storeId,CustomerOrderHolder holder){
        DatabaseReference ref = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        ref.orderByChild("uid").equalTo(storeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    String storeName = ""+ds.child("store_name").getValue();
                    holder.storeName.setText(storeName);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("AdapterCustomerOrder","loadStoreName "+ error.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return customerOrderDetailsModelArrayList.size();
    }

     class CustomerOrderHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        TextView storeName,orderTotal,orderTime,orderStatus;
        Button orderView;
        public CustomerOrderHolder(@NonNull View itemView) {
            super(itemView);

            storeName = itemView.findViewById(R.id.customer_order_store_name);
            orderTotal = itemView.findViewById(R.id.customer_order_total);
            orderTime = itemView.findViewById(R.id.customer_order_date);
            orderStatus = itemView.findViewById(R.id.customer_order_status);
            orderView = itemView.findViewById(R.id.customer_order_view);

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
