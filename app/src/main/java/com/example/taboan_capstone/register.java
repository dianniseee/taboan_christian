package com.example.taboan_capstone;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class register extends Fragment {

    EditText et_fname, et_address, et_contact, et_username, et_pass, et_rep_pass;
    Button btn_register;
    TextView tv_login;
    private ProgressBar progressBar2;
    RequestQueue requestQueue;
    StringRequest stringRequest;


    public register() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);

        et_fname = v.findViewById(R.id.et_fname);
        et_address = v.findViewById(R.id.et_address);
        et_contact = v.findViewById(R.id.et_contact);
        et_username = v.findViewById(R.id.et_username);
        et_pass = v.findViewById(R.id.et_pass);
        et_rep_pass = v.findViewById(R.id.et_repeat_pass);
        btn_register = v.findViewById(R.id.btn_register);
        tv_login = v.findViewById(R.id.tv_login);
        setTv_login();
        setBtn_register();
        return v;
    }

    private void setBtn_register() {
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();}
        });
    }

    public void setTv_login(){
        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login register1 = new login();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_slide_right,R.anim.exit_slide_left, R.anim.enter_slide_left, R.anim.exit_slide_right);
                transaction.replace(R.id.mainLayout,register1).addToBackStack("tag");
                transaction.commit();

            }
        });
    }
    private void registerUser() {

        String username_str = et_username.getText().toString().trim();
        String pass_str = et_pass.getText().toString().trim();
        String rep_pass_str = et_rep_pass.getText().toString().trim();

        if (username_str.isEmpty()) {
            et_username.setError("Username Required!");
            et_username.requestFocus();
            return;
        }
        if (pass_str.isEmpty()) {
            et_pass.setError("Password Required!");
            et_pass.requestFocus();
            return;
        }
        if (rep_pass_str.isEmpty()) {
            et_rep_pass.setError("Password Required!");
            et_rep_pass.requestFocus();
            return;
        }
        if (pass_str.length() < 8) {
            et_pass.setError("Min password length should be 8 characters!");
            et_pass.requestFocus();
            return;
        }
        if (username_str.length() > 10) {
            et_username.setError("Username Max Length(10)");
            et_username.requestFocus();
            return;
        }
        if (!pass_str.equals(rep_pass_str)) {
            et_rep_pass.setError("Password doesn't match!");
            et_rep_pass.requestFocus();
            return;
        } else {
            register();
        }

       // progressBar2.setVisibility(View.VISIBLE);
    }
    public void register() {
        requestQueue = Volley.newRequestQueue(getContext());
        stringRequest = new StringRequest(
                Request.Method.POST, "https://capierap.online/InsertUser.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("success")) {
                            Toast.makeText(getActivity(), "Successfully Registered", Toast.LENGTH_SHORT).show();
                           // progressBar2.setVisibility(View.GONE);

                            login login1 = new login();
                            FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_slide_right, R.anim.exit_slide_left, R.anim.enter_slide_left, R.anim.exit_slide_right);
                            transaction.replace(R.id.mainLayout, login1).addToBackStack("tag");
                            transaction.commit();

                        } else {
                            Toast.makeText(getActivity(), "Username Already Exists.", Toast.LENGTH_SHORT).show();
                           // progressBar2.setVisibility(View.GONE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> User = new HashMap<>();
                User.put("username", et_username.getText().toString());
                User.put("password", et_pass.getText().toString());
                User.put("name", et_fname.getText().toString());
                User.put("contact", et_contact.getText().toString());
                User.put("address", et_address.getText().toString());
                return User;
            }
        };
        requestQueue.add(stringRequest);
    }
}