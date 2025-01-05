package com.example.paymentapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

public class MoreFragment extends Fragment {


    private CardView companydocument,aboutus,privacyPolicy,termsandco,refundPolicy,account_deletion,raiseticket;
    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);
        TextView userName = view.findViewById(R.id.userName);
        TextView userId = view.findViewById(R.id.userId);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "Hello, !");
        String memberId = sharedPreferences.getString("memberId", "UP000000");
        userName.setText(username);
        userId.setText(memberId);

        privacyPolicy = view.findViewById(R.id.privacyPolicy);
        termsandco = view.findViewById(R.id.termsandco);
        refundPolicy = view.findViewById(R.id.refundPolicy);
        companydocument=view.findViewById(R.id.companydocument);

        companydocument.setOnClickListener(v -> {
            assert getFragmentManager() != null;
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment, new Company_Document());
            transaction.commit();
        });

        aboutus=view.findViewById(R.id.aboutus);
        aboutus.setOnClickListener(v -> {
          Intent intent=new Intent(getActivity(),Aboutus_Activity.class);
          startActivity(intent);
        });

        privacyPolicy.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), PrivacyPolicy.class);
            startActivity(intent);
        });
        termsandco.setOnClickListener(v-> {
            Intent intent = new Intent(getActivity(), Terms.class);
            startActivity(intent);
        });
        refundPolicy.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), RefundPolicy.class);
            startActivity(intent);
        });
        account_deletion.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), AccountDeletion.class);
            startActivity(intent);
        });

        raiseticket=view.findViewById(R.id.raiseticket);
        raiseticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),RaiseTicket_Activity.class);
                startActivity(intent);
            }
        });



        return view;
    }

}