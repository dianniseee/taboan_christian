package com.example.taboan_capstone.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.models.ProductModel;
import com.example.taboan_capstone.models.SellerOrderModel;

import java.util.ArrayList;

public class AdapterSellerMenu extends  RecyclerView.Adapter<AdapterSellerMenu.AdapterSellerMenuHolder> {

    private Context context;
    private ArrayList<ProductModel> productModelArrayList;

    public AdapterSellerMenu(Context context, ArrayList<ProductModel> productModelArrayList) {
        this.context = context;
        this.productModelArrayList = productModelArrayList;
    }

    @NonNull
    @Override
    public AdapterSellerMenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_seller,null);
        return new AdapterSellerMenuHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AdapterSellerMenuHolder holder, int position) {
        ProductModel productModel = productModelArrayList.get(position);

        holder.productName.setText(""+productModel.getProd_name());
        holder.productDescription.setText(""+productModel.getProd_desc());
        holder.productPrice.setText("₱ "+productModel.getProd_price());

        if(productModel.getProd_avail().equals("Available")){
            holder.productStatus.setText(""+productModel.getProd_avail());
            holder.productStatus.setTextColor(context.getResources().getColor(R.color.colorGreen));
        }else{
            holder.productStatus.setText(""+productModel.getProd_avail());
            holder.productStatus.setTextColor(context.getResources().getColor(R.color.colorRed));
        }

        Glide.with(context).load(productModel.getProd_image()).into(holder.productImage);

    }

    @Override
    public int getItemCount() {
        return productModelArrayList.size();
    }

    class AdapterSellerMenuHolder extends RecyclerView.ViewHolder{

        private TextView productName,productDescription,productPrice,productStatus;
        private ImageView productImage;
        public AdapterSellerMenuHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.row_product_seller_name);
            productDescription = itemView.findViewById(R.id.row_product_seller_description);
            productPrice = itemView.findViewById(R.id.row_product_seller_price);
            productStatus = itemView.findViewById(R.id.row_product_seller_status);
            productImage = itemView.findViewById(R.id.row_product_seller_image);

        }
    }
}
