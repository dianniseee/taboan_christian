package com.example.taboan_capstone.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taboan_capstone.R;
import com.example.taboan_capstone.models.CustomerCartModel;
import com.example.taboan_capstone.models.ProductModel;

import java.util.ArrayList;
import java.util.List;

public class AdapterCustomerCart extends RecyclerView.Adapter<AdapterCustomerCart.CustomerCartHolder> {

    private final Context context;
    private final List<CustomerCartModel> customerCartModelArrayList;
    private OnAdapterClick listener;

    public AdapterCustomerCart(Context context, List<CustomerCartModel> customerCartModelArrayList, OnAdapterClick listener) {
        this.context = context;
        this.customerCartModelArrayList = customerCartModelArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CustomerCartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_user_item,null);
        return new CustomerCartHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerCartHolder holder, int position) {
        CustomerCartModel productModel = customerCartModelArrayList.get(position);

        String setName = productModel.getProductName();
        double setQuantity = productModel.getQuantity();
        double setSubtotal = productModel.getSubtotal();
        String setCategory = productModel.getProduct_category();

        if(setCategory.equals("Pieces") || setCategory.equals("Bundle")){
            int getQuantity = (int) setQuantity;

            holder.quantity.setText(String.valueOf(getQuantity));
        }else{
            holder.quantity.setText(String.valueOf(setQuantity));
        }

        holder.name.setText(setName);
        holder.subtotal.setText(String.valueOf("₱ "+setSubtotal));
    }

    @Override
    public int getItemCount() {
        return customerCartModelArrayList.size();
    }

    class  CustomerCartHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private final TextView quantity;
        private final TextView name;
        private final TextView subtotal;

        public CustomerCartHolder(@NonNull View itemView) {
            super(itemView);

            quantity = itemView.findViewById(R.id.cart_item_quantity);
            name = itemView.findViewById(R.id.cart_item_name);
            subtotal = itemView.findViewById(R.id.cart_item_subtotal);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onAdapterClick(v,getBindingAdapterPosition());
        }
    }

    public interface  OnAdapterClick{
        void onAdapterClick(View v,int position);
    }
}
