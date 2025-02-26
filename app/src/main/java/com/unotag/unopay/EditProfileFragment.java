package com.unotag.unopay;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Map;

public class EditProfileFragment extends Fragment {
    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);


        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
//        Map<String, ?> allEntries = prefs.getAll();
//        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
//            Log.d("SharedPrefs", entry.getKey() + ": " + entry.getValue().toString());
//        }

        String name = sharedPreferences.getString("username", "-");
        String ifscNo = sharedPreferences.getString("ifsc", "-");
        String accountNo = sharedPreferences.getString("acc_no", "-");
        String panNumber = sharedPreferences.getString("pan", "-");
        String bankName = sharedPreferences.getString("bank", "-");
        String phoneNumber = sharedPreferences.getString("mobile", "-");
        String aadharNumber = sharedPreferences.getString("aadhar", "-");
        String email = sharedPreferences.getString("email", "-");
        String nomineeName = sharedPreferences.getString("nominee_name", "-");
        String nomineeRelation = sharedPreferences.getString("relation", "-");
        String dateOfJoining = sharedPreferences.getString("doj", "-");
        String dateOfActivation = sharedPreferences.getString("doa", "-");
        String sponsorname = sharedPreferences.getString("sponsor_name", "");
        String sponsorid = sharedPreferences.getString("sponsor_id", "-");
        String sponsorno = sharedPreferences.getString("sponsor_no", "-");

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
        TextView nomineeRelationTextView = view.findViewById(R.id.nominee_relation);
        TextView dateOfActivationTextView = view.findViewById(R.id.date_of_activation);
        TextView sponsornameTextView = view.findViewById(R.id.sponsor_name);
        TextView sponsoridTextView = view.findViewById(R.id.sponsor_id);
        TextView sponsornoTextView = view.findViewById(R.id.sponsor_no);
        ImageButton call = view.findViewById(R.id.call_sponsor);
        ImageButton wsp = view.findViewById(R.id.wsp_sponsor);

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
        nomineeRelationTextView.setText(nomineeRelation);
        dateOfActivationTextView.setText(dateOfActivation);
        sponsornameTextView.setText(sponsorname.toUpperCase());
        sponsoridTextView.setText(sponsorid);
        sponsornoTextView.setText(sponsorno);
        if(sponsorno.equals("-")){
            call.setVisibility(GONE);
            wsp.setVisibility(GONE);
        }
        else{
            call.setVisibility(View.VISIBLE);
            wsp.setVisibility(View.VISIBLE);
        }

        call.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + sponsorno));
            startActivity(intent);
        });
        wsp.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + "+91"+sponsorno));
            startActivity(intent);
        });

        return view;
    }
}