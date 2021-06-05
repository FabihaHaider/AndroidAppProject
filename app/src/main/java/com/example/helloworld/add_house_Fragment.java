package com.example.helloworld;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link add_house_Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class add_house_Fragment extends Fragment {

    private TextView house_no, road_no, area, postal_code, flat_size, flat_price;
    private Button add_house;
    int house_cnt = 0;
    DatabaseReference ref;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public add_house_Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment add_house.
     */
    // TODO: Rename and change types and number of parameters
    public static add_house_Fragment newInstance(String param1, String param2) {
        add_house_Fragment fragment = new add_house_Fragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        View view = inflater.inflate(R.layout.fragment_add_house, container, false);
        house_no = view.findViewById(R.id.plainText_house_number);
        road_no = view.findViewById(R.id.plainText_road_number);
        area = view.findViewById(R.id.plainText_area);
        postal_code = view.findViewById(R.id.plainText_postal_code);
        add_house = (Button) view.findViewById(R.id.button_add_house);

        add_house.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertHouseData();
            }
        });

        return view;
    }



    public void insertHouseData() {
        int house_number = Integer.parseInt(house_no.getText().toString().trim());
        String road_number = road_no.getText().toString().trim();
        String are = area.getText().toString().trim();
        String postalCode = postal_code.getText().toString().trim();
//        String size = flat_size.getText().toString();
//        int price = Integer.parseInt(flat_price.getText().toString());
        ref = FirebaseDatabase.getInstance().getReference().child("House");


        House house = new House(house_number, road_number, are, postalCode);

        ref.push().child(String.valueOf(house_cnt+1)).setValue(house);

    }
}