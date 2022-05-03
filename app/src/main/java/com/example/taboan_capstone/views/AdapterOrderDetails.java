package com.example.taboan_capstone.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taboan_capstone.R;
import com.example.taboan_capstone.models.CustomerHistoryModel;
import com.example.taboan_capstone.models.CustomerOrderDetailsModel;
import com.example.taboan_capstone.models.CustomerOrderDetailsProductModel;

import java.util.ArrayList;

public class AdapterOrderDetails extends RecyclerView.Adapter<AdapterOrderDetails.AdapterCustomerHistoryHolder>{

    private final Context context;
    private final ArrayList<CustomerOrderDetailsProductModel> customerOrderDetailsProductModelArrayList;

    public AdapterOrderDetails(Context context, ArrayList<CustomerOrderDetailsProductModel> customerOrderDetailsProductModelArrayList) {
        this.context = context;
        this.customerOrderDetailsProductModelArrayList = customerOrderDetailsProductModelArrayList;
    }

    @NonNull
    @Override
    public AdapterCustomerHistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_user_item,null);
        return new AdapterCustomerHistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterCustomerHistoryHolder holder, int position) {
        CustomerOrderDetailsProductModel customerOrderDetailsProductModel = customerOrderDetailsProductModelArrayList.get(position);

        String setName = customerOrderDetailsProductModel.getProductName();
        double setQuantity = Double.parseDouble(customerOrderDetailsProductModel.getQuantity());
        double setSubtotal = Double.parseDouble(customerOrderDetailsProductModel.getSubtotal());
        String setCategory = customerOrderDetailsProductModel.getProduct_category();

        if(setCategory.equals("Pieces") || setCategory.equals("Bundle")){
            int getQuantity = (int) setQuantity;

            holder.quantity.setText(String.valueOf(getQuantity));
        }else{
            holder.quantity.setText(String.valueOf(setQuantity));
        }

        holder.name.setText(setName);
        holder.subtotal.setText("₱ " + setSubtotal);
    }

    @Override
    public int getItemCount() {
        return customerOrderDetailsProductModelArrayList.size();
    }

    class AdapterCustomerHistoryHolder extends RecyclerView.ViewHolder{

        private final TextView quantity;
        private final TextView name;
        private final TextView subtotal;

        public AdapterCustomerHistoryHolder(@NonNull View itemView) {
            super(itemView);

            quantity = itemView.findViewById(R.id.cart_item_quantity);
            name = itemView.findViewById(R.id.cart_item_name);
            subtotal = itemView.findViewById(R.id.cart_item_subtotal);
        }
    }
}
