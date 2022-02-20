package com.example.taboan_capstone;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class login extends Fragment {


    EditText et_uName, et_password;
    TextView tv_register;
    Button btn_login;
    private ProgressBar progressBar;
    RequestQueue requestQueue;
    StringRequest stringRequest;
    SharedPreferences sharedPreferences;
    TextView alert;
    String spName;

    public static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_USERNAME = "username";

    public login() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        sharedPreferences = getActivity().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        spName = sharedPreferences.getString(KEY_USERNAME, null);

        et_uName = v.findViewById(R.id.et_Uname);
        et_password = v.findViewById(R.id.et_password);
        btn_login = v.findViewById(R.id.btn_login);
        tv_register = v.findViewById(R.id.tv_register);
        progressBar = v.findViewById(R.id.progressBar);
        alert = v.findViewById(R.id.alert);
        setTv_register();
        setBtn_Login();

        if(spName!=null){
            if(spName.equalsIgnoreCase("admin")){
                admin admin1 = new admin();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_slide_right,R.anim.exit_slide_left, R.anim.enter_slide_left, R.anim.exit_slide_right);
                transaction.replace(R.id.mainLayout,admin1);
                transaction.commit();
            }else{
                Home home = new Home();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_slide_right,R.anim.exit_slide_left, R.anim.enter_slide_left, R.anim.exit_slide_right);
                transaction.replace(R.id.mainLayout,home);
                transaction.commit();
            }

        }

        return v;
    }

    private void setBtn_Login() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });
    }
    public void setTv_register(){
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register register1 = new register();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_slide_right,R.anim.exit_slide_left, R.anim.enter_slide_left, R.anim.exit_slide_right);
                transaction.replace(R.id.mainLayout,register1).addToBackStack("tag");
                transaction.commit();

            }
        });
    }
    private void userLogin(){

        String username_str = et_uName.getText().toString().trim();
        String pass_str = et_password.getText().toString().trim();

        if(username_str.isEmpty()){
            et_uName.setError("Username Required!");
            et_uName.requestFocus();
            return;
        }
        if(pass_str.isEmpty()){
            et_password.setError("Password Required!");
            et_password.requestFocus();
            return;
        }
        else{
            login();
        }

        progressBar.setVisibility(View.VISIBLE);

    }
    public void loginAs(String username){

    }
    public void login(){

        requestQueue = Volley.newRequestQueue(getActivity());
        stringRequest = new StringRequest(
                Request.Method.POST,
                "https://capierap.online/login.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {

                            if(et_uName.getText().toString().equals("admin")){
                                admin admin1 = new admin();

                                Bundle bundle = new Bundle();
                                bundle.putString("username", et_uName.getText().toString());
                                admin1.setArguments(bundle);

                                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_slide_right,R.anim.exit_slide_left, R.anim.enter_slide_left, R.anim.exit_slide_right);
                                transaction.replace(R.id.mainLayout,admin1);
                                transaction.commit();

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString(KEY_USERNAME,et_uName.getText().toString());
                                editor.apply();
                            }else{
                                JSONObject jsonobject = new JSONObject(response);

                                String success = jsonobject.getString("success");
                                JSONArray jsonarray = jsonobject.getJSONArray("login");
                                JSONObject jo = jsonarray.getJSONObject(0);

                                if(success.equals("1")) {
                                    Toast.makeText(getActivity(), "Logged in", Toast.LENGTH_SHORT).show();

                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString(KEY_USERNAME,et_uName.getText().toString());

                                    //Toast.makeText(getApplicationContext(), jsonarray.toString(), Toast.LENGTH_LONG).show();
                                    editor.putString("name",jo.getString("name"));
                                    editor.putString("username",jo.getString("username"));
                                    editor.putString("password",jo.getString("password"));
                                    editor.putString("contact",jo.getString("contact"));
                                    editor.putString("address",jo.getString("address"));
                                    editor.apply();
                                    //Toast.makeText(Login.this,sharedPreferences.getString("un",""),Toast.LENGTH_LONG).show();

                                    Home home = new Home();
                                    FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_slide_right,R.anim.exit_slide_left, R.anim.enter_slide_left, R.anim.exit_slide_right);
                                    transaction.replace(R.id.mainLayout,home);
                                    transaction.commit();




                                }else{
                                    Toast.makeText(getActivity(), "Logged In Failed", Toast.LENGTH_SHORT).show();

                                }
                            }




                        } catch (JSONException e) {
                            Toast.makeText(getActivity(), "Logged In Failed", Toast.LENGTH_SHORT).show();

                            e.printStackTrace();

                        }


//                        if (response.equals("success")) {
//                            Toast.makeText(getActivity(), "Logged in", Toast.LENGTH_SHORT).show();
//
//                            if(et_uName.getText().toString().equals("admin")){
//                                admin admin1 = new admin();
//
//                                Bundle bundle = new Bundle();
//                                bundle.putString("username", et_uName.getText().toString());
//                                admin1.setArguments(bundle);
//
//                                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_slide_right,R.anim.exit_slide_left, R.anim.enter_slide_left, R.anim.exit_slide_right);
//                                transaction.replace(R.id.mainLayout,admin1);
//                                transaction.commit();
//
//                                SharedPreferences.Editor editor = sharedPreferences.edit();
//                                editor.putString(KEY_USERNAME,et_uName.getText().toString());
//                                editor.apply();
//                            }else{
//
//
//                                Home home = new Home();
//
//                                Bundle bundle = new Bundle();
//                                bundle.putString("username", et_uName.getText().toString());
//                                home.setArguments(bundle);
//
//                                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_slide_right,R.anim.exit_slide_left, R.anim.enter_slide_left, R.anim.exit_slide_right);
//                                transaction.replace(R.id.mainLayout,home);
//                                transaction.commit();
//
////                                Intent intent = new Intent(getActivity(), Dashboard.class);
////                                Bundle bundle = new Bundle();
////                                bundle.putString("username", et_uName.getText().toString());
////                                intent.putExtras(bundle);
////                                startActivity(intent);
//
//
//                                SharedPreferences.Editor editor = sharedPreferences.edit();
//                                editor.putString(KEY_USERNAME,et_uName.getText().toString());
//                                editor.apply();
//                            }
//
//                        }else{
//                            Toast.makeText(getActivity(), "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
//                            progressBar.setVisibility(View.GONE);
//                        }
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
                Map<String, String> Person = new HashMap<>();
                Person.put("username", et_uName.getText().toString());
                Person.put("password", et_password.getText().toString());
                return Person;
            }

        };

        requestQueue.add(stringRequest);
    }
}