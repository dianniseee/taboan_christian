package com.example.taboan_capstone;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class vegfruit extends Fragment{

    private static final String PRODUCT_URL = "https://capierap.online/apiVeggies.php";
    RecyclerView recyclerView;

    ProductAdapter adapter;
    List<product> productList;
    ImageView backPress, basket;


    Button productList_btn;


    public vegfruit() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_vegfruit, container, false);

        productList = new ArrayList<>();
        recyclerView = v.findViewById(R.id.recyclerView1);
        backPress = v.findViewById(R.id.backimg);
        basket = v.findViewById(R.id.basketimg);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        //adding some items to our list

        loadProducts();
        //creating recyclerview adapter
        adapter = new ProductAdapter(getContext(), productList);
        recyclerView.setAdapter(adapter);

        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User_Category fragment = new User_Category();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.mainLayout,fragment);
                fragmentTransaction.commit();
            }
        });

        basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User_basket fragment = new User_basket();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.mainLayout,fragment).addToBackStack("tag");
                fragmentTransaction.commit();
            }
        });



        return v;
    }

    private void loadProducts() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, PRODUCT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray products = new JSONArray(response);
                            for (int i =0; i<products.length(); i++){
                                JSONObject productObject = products.getJSONObject(i);

                                product product = new product(
                                        productObject.getInt("prod_id"),
                                        productObject.getString("prod_name"),
                                        productObject.getString("prod_desc"),
                                        productObject.getString("prod_category"),
                                        productObject.getDouble("prod_price"),
                                        productObject.getDouble("prod_quantity"),
                                        productObject.getString("image")
                                );

                                // product product = new product(name, desc, category, price, quantity, image);
                                productList.add(product);
                            }
                            adapter = new ProductAdapter(getContext(), productList);
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        if(response != null && response.data != null){
                            Toast.makeText(getContext(),"errorMessage:"+response.statusCode, Toast.LENGTH_SHORT).show();
                        }else{
                            String errorMessage=error.getClass().getSimpleName();
                            if(!TextUtils.isEmpty(errorMessage)){
                                Toast.makeText(getContext(),"errorMessage:"+errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(stringRequest);
    }

    public void setTv_productList_btn(){
        productList_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new meatsection();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.mainLayout,fragment);
                fragmentTransaction.commit();

            }
        });
    }

}