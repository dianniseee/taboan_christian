package com.example.taboan_capstone;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class User_basket extends Fragment {

    RequestQueue requestQueue;
    StringRequest stringRequest;
    SharedPreferences sharedPreferences;
    List<basket_data> list;
    RecyclerView recyclerView;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_USERNAME = "username";
    String spName;
    ImageView backPress;
    View v;



    public User_basket() {
        // Required empty public constructor
    }

    public static User_basket newInstance(String param1, String param2) {
        User_basket fragment = new User_basket();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_user_basket, container, false);
        sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        spName = sharedPreferences.getString(KEY_USERNAME, null);
        list = new ArrayList<>();
        backPress = v.findViewById(R.id.backimg);
        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Home fragment = new Home();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.mainLayout,fragment);
                fragmentTransaction.commit();
            }
        });

        basketItems(spName);
        return v;
    }

    public void basketItems(String n) {
        recyclerView = v.findViewById(R.id.recyclerViews);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        requestQueue = Volley.newRequestQueue(getActivity());
        stringRequest = new StringRequest(
                Request.Method.POST,
                "https://capierap.online/getBasket.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray jArray = obj.getJSONArray("basket");

                            for (int i = 0; i < jArray.length(); i++) {
                                JSONObject jObject = jArray.getJSONObject(i);

                                basket_data data = new basket_data(
                                        jObject.getString("name_basket"),
                                        jObject.getDouble("price_basket"),
                                        jObject.getInt("qty_basket"),
                                        jObject.getDouble("total"),
                                        jObject.getString("image")
                                );
                                list.add(data);
                            }

                        } catch (JSONException errmsg) {
                            Toast.makeText(getContext(), errmsg.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        basket_adapter basket_adapter = new basket_adapter(getContext(), list);
                        recyclerView.setAdapter(basket_adapter);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "An Error Occurred!", Toast.LENGTH_LONG).show();
            }
        }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> Student = new HashMap<>();
                Student.put("username", n);
                return Student;
            }
        };

        requestQueue.add(stringRequest);
    }
}