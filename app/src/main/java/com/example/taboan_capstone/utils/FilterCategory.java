package com.example.taboan_capstone.utils;

import android.annotation.SuppressLint;
import android.widget.Filter;

import com.example.taboan_capstone.models.SellerStoreModel;
import com.example.taboan_capstone.views.AdapterStore;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class FilterCategory extends Filter {

   private AdapterStore adapterStore;
   private ArrayList<SellerStoreModel> sellerStoreModelArrayList;

    public FilterCategory(AdapterStore adapterStore, ArrayList<SellerStoreModel> sellerStoreModelArrayList) {
        this.adapterStore = adapterStore;
        this.sellerStoreModelArrayList = sellerStoreModelArrayList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();

        if(charSequence != null && charSequence.length() > 0){

            ArrayList<SellerStoreModel> filterStore = new ArrayList<>();
            for(int i = 0; i < sellerStoreModelArrayList.size(); i++){
                if(sellerStoreModelArrayList.get(i).getStore_category().toLowerCase().contains(charSequence.toString().toLowerCase())){
                    filterStore.add(sellerStoreModelArrayList.get(i));
                }
            }
            results.count = filterStore.size();
            results.values = filterStore;

        }else{
            results.count = sellerStoreModelArrayList.size();
            results.values = sellerStoreModelArrayList;
        }

        return results;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            adapterStore.sellerStoreModelArrayList = (ArrayList<SellerStoreModel>) filterResults.values;
            adapterStore.notifyDataSetChanged();
    }
}
