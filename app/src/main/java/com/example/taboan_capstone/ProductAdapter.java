package com.example.taboan_capstone;

import static com.example.taboan_capstone.login.SHARED_PREF_NAME;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> implements Filterable {


    Context mCtx;
    List<product> productList;
    List<product> list;
    ArrayList<product> cart = new ArrayList();
    String spName, name;
    double price;
    int id;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_USERNAME = "username";

    public ProductAdapter(Context mCtx, List<product> productList) {
        this.mCtx = mCtx;
        this.productList = productList;
        this.list = new ArrayList<>(productList);

    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vegfruit_list,null);
        ProductViewHolder holder = new ProductViewHolder(view);


        sharedPreferences = view.getContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        spName = sharedPreferences.getString(KEY_USERNAME, null);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, @SuppressLint("RecyclerView") int position) {
        product getposition = productList.get(position);
        holder.textViewName.setText(getposition.getProd_name());
        holder.textViewDesc.setText(getposition.getProd_desc());
        holder.textViewCategory.setText(getposition.getProd_category());
        holder.textViewPrice.setText(String.valueOf(getposition.getProd_price()));


        byte[] bytes = Base64.decode(getposition.getImage(),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
        holder.imageView.setImageBitmap(bitmap);

        holder.Qty.setText(Integer.toString(getposition.getQuant()));
        String available = "available";
        String notAvailable = "notAvailable";

        if(holder.textViewQuantity.equals("0")) {
            holder.textViewQuantity.setText(notAvailable);
        }else{
            holder.textViewQuantity.setText(available);
        }

        holder.addtobasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                id = productList.get(position).getId();
                name = productList.get(position).getProd_name();
                price = productList.get(position).getProd_price();

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext());
                alertDialog.setTitle("Add this item to basket?");
                alertDialog.setMessage("Enter Quantity");
                final EditText qty = new EditText(view.getContext());
                alertDialog.setView(qty);

                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                requestQueue = Volley.newRequestQueue(view.getContext());
                                stringRequest = new StringRequest(
                                        Request.Method.POST, "https://capierap.online/insertBasket.php",
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                if (response.equals("success")) {
                                                    Toast.makeText(view.getContext(), "Successfully Added to Cart!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(view.getContext(), "Error, please try again.", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(view.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                ) {
                                    @Nullable
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> User = new HashMap<>();
                                        User.put("1username", spName);
                                        User.put("1id", String.valueOf(id));
                                        User.put("1name", name);
                                        User.put("1price", String.valueOf(price));
                                        User.put("1qty", qty.getText().toString());
                                        User.put("1total", String.valueOf(productList.get(position).getProd_price() * Integer.parseInt(qty.getText().toString())));
                                        User.put("1image", getposition.getImage());
                                        return User;
                                    }
                                };
                                requestQueue.add(stringRequest);


                            }
                        });

                alertDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
            }
        });

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quant = Integer.parseInt((String) holder.Qty.getText());
                quant++;
                holder.Qty.setText(Integer.toString(quant));
                getposition.setQuant(quant);

                notifyDataSetChanged();
            }
        });

        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quant = Integer.parseInt((String) holder.Qty.getText());
                quant--;
                if(quant<1){
                    holder.addtobasket.setVisibility(View.VISIBLE);
                    holder.plus.setVisibility(View.INVISIBLE);
                    holder.minus.setVisibility(View.INVISIBLE);
                    holder.Qty.setVisibility(View.INVISIBLE);
                    getposition.setQuant(0);
                    cart.remove(getposition);
                    notifyDataSetChanged();
                }else{
                    holder.Qty.setText(Integer.toString(quant));
                    getposition.setQuant(quant);
                    notifyDataSetChanged();
                }

            }
        });



    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            //list=new ArrayList<>(models);
            ArrayList<product> filterlist = new ArrayList<>();
            if(charSequence.toString().isEmpty()){
                filterlist.addAll(list);

            }else{
                for(product item : list){
                    if(item.getProd_name().toLowerCase().startsWith(charSequence.toString().toLowerCase())){
                        filterlist.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filterlist;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            productList.clear();
            productList=(ArrayList<product>) filterResults.values;
            notifyDataSetChanged();
        }


    };

    class ProductViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        Button addtobasket,plus,minus;
        TextView textViewName, textViewDesc, textViewCategory, textViewPrice, textViewQuantity, Qty;
        CardView layout;
        public ProductViewHolder(@NonNull View itemView)
        {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDesc = itemView.findViewById(R.id.textViewDesc);
            textViewCategory = itemView.findViewById(R.id.textViewCategory);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            addtobasket = itemView.findViewById(R.id.addToBasket_btn);
            Qty =itemView.findViewById(R.id.quantity);
            int Quant = Integer.parseInt((String) Qty.getText());
            minus = itemView.findViewById(R.id.minus);
           plus = itemView.findViewById(R.id.plus);

            if(Quant>0){
                this.addtobasket.setVisibility(View.INVISIBLE);
                this.plus.setVisibility(View.VISIBLE);
                this.minus.setVisibility(View.VISIBLE);
                this.Qty.setVisibility(View.VISIBLE);
            }
        }
    }
}


