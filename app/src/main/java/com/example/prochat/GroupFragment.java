package com.example.prochat;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {

    private View groupfragmentview;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_group = new ArrayList<>();

    private DatabaseReference Groupref;



    public GroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         groupfragmentview = inflater.inflate(R.layout.fragment_group, container, false);
         Groupref = FirebaseDatabase.getInstance().getReference().child("Groups");

         Intializefield();

         ReviewandDisplay();

         list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                 String currentgroupname = parent.getItemAtPosition(position).toString();

                 Intent groupchatintent = new Intent(getContext(),Groupchat.class);
                 groupchatintent.putExtra("groupname",currentgroupname);
                 startActivity(groupchatintent);
             }
         });

         return groupfragmentview;
    }


    private void Intializefield() {
        list_view = groupfragmentview.findViewById(R.id.list_group_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,list_of_group);
        list_view.setAdapter(arrayAdapter);

    }


    private void ReviewandDisplay() {

        Groupref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();

                while(iterator.hasNext())
                {

                    set.add(((DataSnapshot)iterator.next()).getKey());
                }
                list_of_group.clear();
                list_of_group.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
