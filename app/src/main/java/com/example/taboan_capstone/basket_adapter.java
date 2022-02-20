package com.example.taboan_capstone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class basket_adapter extends RecyclerView.Adapter<basket_adapter.ViewHolder> implements PopupMenu.OnMenuItemClickListener {

    Context context;
    private List<basket_data> myData;

    public basket_adapter(Context context, List<basket_data> myData){
        this.context = context;
        this.myData = myData;
    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.basket_list,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView")int position) {
        holder.name.setText("Product name: "+myData.get(position).getName());
        holder.price.setText(String.valueOf("Product Price: "+myData.get(position).getPrice()));
        holder.qty.setText(String.valueOf("Product Quantity: "+myData.get(position).getQuan()));
        holder.total.setText(String.valueOf("Price Total: "+myData.get(position).getTotal()));

        byte[] bytes = Base64.decode(myData.get(position).getImage(),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
        holder.image.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return myData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView price;
        TextView qty;
        TextView total;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_basket);
            price = itemView.findViewById(R.id.price_basket);
            qty = itemView.findViewById(R.id.quantity_basket);
            total = itemView.findViewById(R.id.total);
            image = itemView.findViewById(R.id.image_basket);

        }
    }
}
