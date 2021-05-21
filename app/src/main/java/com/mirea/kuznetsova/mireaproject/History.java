package com.mirea.kuznetsova.mireaproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class History extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ArrayList<Note> states = new ArrayList<>();
    private StateAdapter adapter;

    public History() {

    }

    public static History newInstance(String param1, String param2) {
        History fragment = new History();
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
    @SuppressLint("InflateParams")
    public void OnAdd(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle ("Add new history");

        view = LayoutInflater.from(getContext()).inflate(R.layout.dialog, null);

        builder.setView(view);

        final EditText title = (EditText)view.findViewById(R.id.name_test_save);
        final EditText text = (EditText)view.findViewById(R.id.text_save);

        builder.setPositiveButton ("Add", (dialog, which) -> {
            String a = title.getText().toString().trim();
            String b = text.getText().toString().trim();
            addHistory(a, b);
            Toast.makeText (getContext(), "History add", Toast.LENGTH_SHORT).show ();
        });
        builder.setNegativeButton ("Cancel", (dialog, which) -> {

        });
        builder.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);

        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.add_history_button);

        button.setOnClickListener(this::OnAdd);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);

        states.add(new Note("Summer","Starts here..."));

        adapter = new StateAdapter(getContext(), states);

        recyclerView.setAdapter(adapter);

        return view;
    }

    private void addHistory(String name, String text){
        states.add(new Note(name, text));
        adapter.notifyDataSetChanged();
    }

    static class Note{

        private String name, text;

        Note(String name, String text){
            this.name = name;
            this.text = text;
        }

        public String getName() {
            return name;
        }

        public String getText() {
            return text;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}