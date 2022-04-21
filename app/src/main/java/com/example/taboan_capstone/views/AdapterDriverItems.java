package com.example.taboan_capstone.views;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taboan_capstone.R;
import com.example.taboan_capstone.models.DriverProductModel;

import java.util.ArrayList;

public class AdapterDriverItems extends  RecyclerView.Adapter<AdapterDriverItems.AdapterDriverItemsHolder> {

    private final Context context;
    private final ArrayList<DriverProductModel> productModelArrayList;

    public AdapterDriverItems(Context context, ArrayList<DriverProductModel> productModelArrayList) {
        this.context = context;
        this.productModelArrayList = productModelArrayList;
    }

    @NonNull
    @Override
    public AdapterDriverItemsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.driver_cart_item,null);
        return new AdapterDriverItemsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterDriverItemsHolder holder, int position) {
        //CustomerCartModel productModel = customerCartModelArrayList.get(position);
        DriverProductModel driverProductModel = productModelArrayList.get(position);

        holder.quantity.setText(""+driverProductModel.getQuantity());
        holder.name.setText(""+driverProductModel.getProductName());
        holder.subtotal.setText("₱ "+driverProductModel.getSubtotal());
    }

    @Override
    public int getItemCount() {
        return productModelArrayList.size();
    }

    class  AdapterDriverItemsHolder extends RecyclerView.ViewHolder{

        private final TextView quantity;
        private final TextView name;
        private final TextView subtotal;

        public AdapterDriverItemsHolder(@NonNull View itemView) {
            super(itemView);

            quantity = itemView.findViewById(R.id.cart_item_quantity);
            name = itemView.findViewById(R.id.cart_item_name);
            subtotal = itemView.findViewById(R.id.cart_item_subtotal);
        }
    }
}
