package com.example.taboan_capstone;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;


public class Home extends Fragment {

    public static int CLEAR_CART = 0;
    TextView tv_welcome, name;
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_USERNAME = "username";
    RequestQueue requestQueue;
    StringRequest stringRequest;
    Button logout, profile_btn;
    Button categoryBtn;
    Button shopbasketBtn;




    public Home() {
        // Required empty public constructor
    }

    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        tv_welcome = v.findViewById(R.id.tv_welcome);
        name = v.findViewById(R.id.tv_welcome1);
        logout = v.findViewById(R.id.logout);
        profile_btn = v.findViewById(R.id.profile_btn);


        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User_Profile vegfruitf = new User_Profile();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_slide_right, R.anim.exit_slide_left, R.anim.enter_slide_left, R.anim.exit_slide_right);
                transaction.replace(R.id.mainLayout,vegfruitf).addToBackStack("tag");
                transaction.commit();
            }
        });

        categoryBtn = v.findViewById(R.id.category_btn);
        shopbasketBtn = v.findViewById(R.id.shopbasket_btn);
        setCategoryBtn();
//        setshopbasketBtn();

        sharedPreferences = getActivity().getSharedPreferences(login.SHARED_PREF_NAME,MODE_PRIVATE);
        name.setText(sharedPreferences.getString("name",""));

//        setLogout();
        return v;
    }



//    public void setLogout() {
//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
//                alertDialog.setTitle("Logout");
//                alertDialog.setMessage("Are you sure you want to logout?");
//                alertDialog.setPositiveButton("YES",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                SharedPreferences.Editor editor = sharedPreferences.edit();
//                                editor.clear();
//                                editor.commit();
//
//                                login login = new login();
//                                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_slide_right, R.anim.exit_slide_left, R.anim.enter_slide_left, R.anim.exit_slide_right);
//                                transaction.replace(R.id.mainLayout, login);
//                                transaction.commit();
//                            }
//                        });
//
//                alertDialog.setNegativeButton("NO",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        });
//
//                alertDialog.show();
//            }
//        });
//    }


//    private void getUsername(String spName) {
//
//        requestQueue = Volley.newRequestQueue(getActivity());
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
//                            Toast.makeText(getContext(), errmsg.getMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void setCategoryBtn(){
        categoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User_Category vegfruitf = new User_Category();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_slide_right, R.anim.exit_slide_left, R.anim.enter_slide_left, R.anim.exit_slide_right);
                transaction.replace(R.id.mainLayout,vegfruitf);
                transaction.commit();

            }
        });
    }
}


