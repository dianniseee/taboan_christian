package com.example.taboan_capstone;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class User_Profile extends Fragment {

    SharedPreferences sharedPreferences ;
    TextView name,username,contact,address;
    Button logout_btn;
    ImageView backPress;

    public User_Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user__profile, container, false);
        sharedPreferences = getActivity().getSharedPreferences(login.SHARED_PREF_NAME,MODE_PRIVATE);
        name = v.findViewById(R.id.TVname);
        username = v.findViewById(R.id.TVusername);
        contact = v.findViewById(R.id.TVcontact);
        address = v.findViewById(R.id.TVaddress);
        logout_btn = v.findViewById(R.id.logout);
        backPress = v.findViewById(R.id.backimg);

        name.setText(sharedPreferences.getString("name",""));
        username.setText(sharedPreferences.getString("username",""));
        contact.setText(sharedPreferences.getString("contact",""));
        address.setText(sharedPreferences.getString("address",""));

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

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle("Logout");
                alertDialog.setMessage("Are you sure you want to logout?");
                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.clear();
                                editor.apply();


                                login login1 = new login();

                                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_slide_right,R.anim.exit_slide_left, R.anim.enter_slide_left, R.anim.exit_slide_right);
                                transaction.replace(R.id.mainLayout,login1);
                                transaction.commit();
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


        return v;
    }
}