package com.example.taboan_capstone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Dashboard extends AppCompatActivity {

    TextView tv_welcome;
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_USERNAME = "username";
    RequestQueue requestQueue;
    StringRequest stringRequest;
    Button logout;
    Button categoryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()){
                    case R.id.home:
                        return true;
                    case R.id.category:
//                        startActivity(new Intent(getApplicationContext(),Category.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.prodile:
//                        startActivity(new Intent(getApplicationContext(),Profile.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.basket:
//                        startActivity(new Intent(getApplicationContext(),Basket.class));
                        overridePendingTransition(0,0);
                        return true;
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.homeLayout, selectedFragment).commit();
                }
                return true;
            }
        });

//        tv_welcome = findViewById(R.id.tv_welcome);
//        logout = findViewById(R.id.logout);
//
//        categoryBtn = findViewById(R.id.profile_btn);
//
//        sharedPreferences = Dashboard.this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
//        String spName = sharedPreferences.getString(KEY_USERNAME, null);
//
//        getUsername(spName);

    }


//    private void getUsername(String spName) {
//
//        requestQueue = Volley.newRequestQueue(Dashboard.this);
//        stringRequest = new StringRequest(
//                Request.Method.POST,
//                "https://capierap.online/getName.php",
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//
//                            JSONObject jsonObject = new JSONObject(response);
//                            JSONArray jsonArray = jsonObject.getJSONArray("name");
//
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                JSONObject jObject = jsonArray.getJSONObject(i);
//
//                                tv_welcome.setText("Welcome "+jObject.getString("name")+"!");
//                            }
//
//                        } catch (JSONException errmsg) {
//                            Toast.makeText(Dashboard.this, errmsg.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(Dashboard.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//        ) {
//            @Nullable
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> Person = new HashMap<>();
//                Person.put("username", spName);
//                return Person;
//            }
//        };
//
//        requestQueue.add(stringRequest);
//    }


    }
