package com.example.paymentapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;

public class EditProfileFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);


        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
//        Map<String, ?> allEntries = prefs.getAll();
//        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//            Log.d("SharedPrefs", entry.getKey() + ": " + entry.getValue().toString());
//        }

        String name = sharedPreferences.getString("username", "null");
        String ifscNo = sharedPreferences.getString("IFSC", "null");
        String accountNo = sharedPreferences.getString("account_number", "null");
        String panNumber = sharedPreferences.getString("pan_number", "null");
        String bankName = sharedPreferences.getString("bank_name", "null");
        String phoneNumber = sharedPreferences.getString("mobile", "null");
        String aadharNumber = sharedPreferences.getString("aadhar_number", "null");
        String email = sharedPreferences.getString("email", "null");
        String nomineeName = sharedPreferences.getString("nominee_name", "null");
        String dateOfJoining = sharedPreferences.getString("doj", "null");

        TextView nameTextView = view.findViewById(R.id.name);
        TextView ifscNoTextView = view.findViewById(R.id.ifsc_no);
        TextView accountNoTextView = view.findViewById(R.id.account_no);
        TextView panNumberTextView = view.findViewById(R.id.pannumber);
        TextView bankNameTextView = view.findViewById(R.id.bankname);
        TextView phoneNumberTextView = view.findViewById(R.id.phone);
        TextView aadharNumberTextView = view.findViewById(R.id.aadhar_number);
        TextView emailTextView = view.findViewById(R.id.email);
        TextView nomineeNameTextView = view.findViewById(R.id.nominee_name);
        TextView dateOfJoiningTextView = view.findViewById(R.id.date_of_issue);

        nameTextView.setText(name);
        ifscNoTextView.setText(ifscNo);
        accountNoTextView.setText(accountNo);
        panNumberTextView.setText(panNumber);
        bankNameTextView.setText(bankName);
        phoneNumberTextView.setText(phoneNumber);
        aadharNumberTextView.setText(aadharNumber);
        emailTextView.setText(email);
        nomineeNameTextView.setText(nomineeName);
        dateOfJoiningTextView.setText(dateOfJoining);

        return view;

    }
}