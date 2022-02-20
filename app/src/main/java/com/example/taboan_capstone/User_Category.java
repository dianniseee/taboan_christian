package com.example.taboan_capstone;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


public class User_Category extends Fragment {

    Button vegfruit_btn;
    Button meatseaction_btn;
    Button seafoodsection_btn;
    ImageView backPress;


    public User_Category() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user__category, container, false);
        backPress = v.findViewById(R.id.backimg);

        vegfruit_btn = v.findViewById(R.id.view_vf_btn);
        meatseaction_btn = v.findViewById(R.id.meatseaction_btn);
        seafoodsection_btn = v.findViewById(R.id.seafoodsection_btn);

        seafoodsection_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seafoodsection vegfruitf = new seafoodsection();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_slide_right, R.anim.exit_slide_left, R.anim.enter_slide_left, R.anim.exit_slide_right);
                transaction.replace(R.id.mainLayout,vegfruitf);
                transaction.commit();

            }
        });

        vegfruit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vegfruit vegfruitf = new vegfruit();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_slide_right, R.anim.exit_slide_left, R.anim.enter_slide_left, R.anim.exit_slide_right);
                transaction.replace(R.id.mainLayout,vegfruitf);
                transaction.commit();

            }
        });

        meatseaction_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                meatsection vegfruitf = new meatsection();
                FragmentTransaction transaction = getFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_slide_right, R.anim.exit_slide_left, R.anim.enter_slide_left, R.anim.exit_slide_right);
                transaction.replace(R.id.mainLayout,vegfruitf);
                transaction.commit();
            }
        });

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

        return v;
    }
}