package com.example.taboan_capstone.views;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.example.taboan_capstone.Constants;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.activity.customer.CustomerStoreDetailsActivity;
import com.example.taboan_capstone.models.SellerStoreModel;
import com.example.taboan_capstone.utils.FilterCategory;

import java.util.ArrayList;

public class AdapterStore extends RecyclerView.Adapter<AdapterStore.StoreHolder>  implements Filterable {

    private final Context context;
    public  ArrayList<SellerStoreModel> sellerStoreModelArrayList , filterList;
    private FilterCategory filterCategory;
    private String marketName;

    public AdapterStore(Context context, ArrayList<SellerStoreModel> sellerStoreModelArrayList,String marketName) {
        this.context = context;
        this.sellerStoreModelArrayList = sellerStoreModelArrayList;
        this.filterList = sellerStoreModelArrayList;
        this.marketName = marketName;
    }

    @NonNull
    @Override
    public StoreHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_shop,parent,false);
        return new StoreHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreHolder holder, int position) {
        SellerStoreModel sellerStoreModel = sellerStoreModelArrayList.get(position);

        String uid = sellerStoreModel.getUid();
        String storeName = sellerStoreModel.getStore_name();
        String storeCover = sellerStoreModel.getCover_photo();
        String storeOnline = sellerStoreModel.getOnline();
        String storeCat = sellerStoreModel.getStore_category();

        holder.storeName.setText(storeName);
        holder.storeCategory.setText(storeCat);
        Glide.with(context).load(storeCover).into(holder.storeCover);


        holder.storeCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CustomerStoreDetailsActivity.class);
                intent.putExtra(Constants.Companion.getSELLER_GET_UID(), ""+uid);
                intent.putExtra("uid",uid);
                intent.putExtra("market",marketName);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sellerStoreModelArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if(filterCategory == null){
            filterCategory = new FilterCategory(this,filterList);
        }
        return filterCategory;
    }

    static class StoreHolder extends RecyclerView.ViewHolder{

        private final ImageView storeCover;
        private  TextView storeName,storeCategory;
        public StoreHolder(@NonNull View itemView) {
            super(itemView);

        storeCover = itemView.findViewById(R.id.iv_store_cover);
        storeName = itemView.findViewById(R.id.tv_store_name);
        storeCategory = itemView.findViewById(R.id.iv_store_category);
        }
    }
}
