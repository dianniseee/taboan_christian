package com.example.taboan_capstone.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taboan_capstone.R;
import com.example.taboan_capstone.models.DriverProductModel;
import com.example.taboan_capstone.models.SellerProductModel;

import java.util.ArrayList;

public class AdapterSellerItems extends  RecyclerView.Adapter<AdapterSellerItems.AdapterDriverItemsHolder> {

    private final Context context;
    private final ArrayList<SellerProductModel> sellerProductModelArrayList;

    public AdapterSellerItems(Context context, ArrayList<SellerProductModel> sellerProductModelArrayList) {
        this.context = context;
        this.sellerProductModelArrayList = sellerProductModelArrayList;
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
        SellerProductModel sellerProductModel = sellerProductModelArrayList.get(position);

        holder.quantity.setText(""+sellerProductModel.getQuantity());
        holder.name.setText(""+sellerProductModel.getProductName());
        holder.subtotal.setText("₱ "+sellerProductModel.getSubtotal());
    }

    @Override
    public int getItemCount() {
        return sellerProductModelArrayList.size();
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
