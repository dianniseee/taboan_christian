package com.example.taboan_capstone.activity.driver.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.taboan_capstone.Globals;
import com.example.taboan_capstone.R;
import com.example.taboan_capstone.activity.seller.SellerHistoryActivity;
import com.example.taboan_capstone.models.DriverOrderModel;
import com.example.taboan_capstone.models.SellerOrderModel;
import com.example.taboan_capstone.views.AdapterDriverHistory;
import com.example.taboan_capstone.views.AdapterSellerHistory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private FirebaseAuth firebaseAuth;
    private RecyclerView driverHistoryList;
    private ArrayList<DriverOrderModel> driverOrderModelArrayList;
    private AdapterDriverHistory adapterDriverHistory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        driverHistoryList = view.findViewById(R.id.driver_history_list);

        loadHistory();
        return view;
    }

    private void loadHistory(){
        driverOrderModelArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance(Globals.INSTANCE.getFirebaseLink()).getReference("Users");
        reference.child(Objects.requireNonNull(firebaseAuth.getUid())).child("Orders").orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        driverOrderModelArrayList.clear();

                        for(DataSnapshot ds : snapshot.getChildren()){
                            DriverOrderModel modelProduct = ds.getValue(DriverOrderModel.class);
                            driverOrderModelArrayList.add(modelProduct);

                        }
                        adapterDriverHistory = new AdapterDriverHistory(HistoryFragment.this.getContext(), driverOrderModelArrayList,firebaseAuth);
                        driverHistoryList.setAdapter(adapterDriverHistory);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(HistoryFragment.this.getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}