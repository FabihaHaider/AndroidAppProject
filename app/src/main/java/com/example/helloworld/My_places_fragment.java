package com.example.helloworld;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link My_places_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class My_places_fragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private MyAdapter myadapter;
    private ArrayList<Model> arrayList;
    private String owner_email;
    private Toolbar toolbar;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public My_places_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment demo.
     */
    // TODO: Rename and change types and number of parameters
    public static My_places_fragment newInstance(String param1, String param2) {
        My_places_fragment fragment = new My_places_fragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.activity_my_places, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String owner_email = user.getEmail();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Place");
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        arrayList = new ArrayList<>();
        myadapter = new MyAdapter(getContext(), arrayList);
        recyclerView.setAdapter(myadapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){

                    String email = dataSnapshot.child("owner_email").getValue().toString();
                    String name = dataSnapshot.child("name").getValue().toString();
                    String location = dataSnapshot.child("address").getValue().toString();
                    String charge = dataSnapshot.child("amount_of_charge").getValue().toString();
                    String charge_rate = dataSnapshot.child("charge_unit").getValue().toString();
                    if(email.equals(owner_email)) {
                        Model model = new Model("Name: " + name, "Location: " + location, "Amount: Tk " + charge + " " + charge_rate);
                        model.setImage(R.drawable.logo);
                        arrayList.add(model);
                    }
                }
                myadapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        return view;
    }
}