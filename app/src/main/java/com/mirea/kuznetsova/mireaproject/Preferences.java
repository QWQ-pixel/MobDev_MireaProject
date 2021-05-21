package com.mirea.kuznetsova.mireaproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

public class Preferences extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private static final String PREF_NAME = "Name";
    private static final String ADDRESS = "Address";
    SharedPreferences settings;

    public Preferences() {
    }

    public static Preferences newInstance(String param1, String param2) {
        Preferences fragment = new Preferences();
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
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

         View view = inflater.inflate(R.layout.fragment_preferences, container, false);

         settings = requireActivity().getPreferences(Context.MODE_PRIVATE);

         Button saveName = view.findViewById(R.id.saveButton);

         Button loadName = view.findViewById(R.id.getButton);

         saveName.setOnClickListener(v -> {

             EditText nameBox = (EditText) view.findViewById(R.id.nameBox);

             EditText addressBox = (EditText) view.findViewById(R.id.address);

             String name = nameBox.getText().toString();

             SharedPreferences.Editor prefEditor = settings.edit();

             String address = addressBox.getText().toString();

             prefEditor.putString(PREF_NAME, name);

             prefEditor.apply();

             prefEditor.putString(ADDRESS, address);

             prefEditor.apply();
         });
         loadName.setOnClickListener(v -> {

             TextView nameView = (TextView) view.findViewById(R.id.nameView);

             TextView addressView = (TextView) view.findViewById(R.id.addressView);

             String name = settings.getString(PREF_NAME,"unknown");

             String address = settings.getString(ADDRESS, "unknown");

             nameView.setText(name);

             addressView.setText(address);

         });
        return view;
    }
}