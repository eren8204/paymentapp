package com.example.paymentapp;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.getIntent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class IDFragment extends Fragment {

    private TextView memberidtext,usernametext,emailtext,timetext,datetext;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_i_d, container, false);


        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Hello, User!");
        String memberId = sharedPreferences.getString("memberId", "UP000000");
        String email = sharedPreferences.getString("email", "N/A");
        String date = sharedPreferences.getString("doj", "N/A");

        memberidtext = view.findViewById(R.id.memberid);
        usernametext = view.findViewById(R.id.name);
        emailtext = view.findViewById(R.id.email);
        datetext = view.findViewById(R.id.date);

        memberidtext.setText(memberId);
        usernametext.setText(username);
        emailtext.setText(email);
        datetext.setText(date);

        return view;
    }


}