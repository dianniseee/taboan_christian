package com.example.taboan_capstone.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.models.ProductModel;

import java.util.ArrayList;

public class AdapterSellerProduct extends  RecyclerView.Adapter<AdapterSellerProduct.ProductHolder>  {

    private final Context context;
    public ArrayList<ProductModel> productModelArrayList;
    private OnAdapterClick listener;

    public AdapterSellerProduct(Context context, ArrayList<ProductModel> productModelArrayList, OnAdapterClick listener) {
        this.context = context;
        this.productModelArrayList = productModelArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.store_items,parent,false);
        return new ProductHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
            ProductModel productModel = productModelArrayList.get(position);

            String cover = productModel.getProd_image2();
            String price = productModel.getProd_price();
            String name = productModel.getProd_name();
            String available = productModel.getProd_avail();

            holder.productPrice.setText("₱ "+price);
            holder.productName.setText(""+name);
            Glide.with(context).load(cover).into(holder.productCover);

            if(available.equals("Available")){
                holder.productCover.setVisibility(View.VISIBLE);
                holder.prodAvailable.setVisibility(View.GONE);
            }else{
//                holder.productCover.setVisibility(View.GONE);
//                holder.prodAvailable.setVisibility(View.VISIBLE);
                holder.productBase.setVisibility(View.GONE);
            }

    }

    @Override
    public int getItemCount() {
        return productModelArrayList.size();
    }

    class ProductHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView productCover;
        private TextView productPrice,productName,prodAvailable;
        private LinearLayout productBase;
        public ProductHolder(@NonNull View itemView) {
            super(itemView);

            productCover = itemView.findViewById(R.id.seller_product_cover);
            productPrice = itemView.findViewById(R.id.seller_product_price);
            productName = itemView.findViewById(R.id.seller_product_name);
            prodAvailable = itemView.findViewById(R.id.selleR_product_available);
            productBase = itemView.findViewById(R.id.seller_product_base);
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
