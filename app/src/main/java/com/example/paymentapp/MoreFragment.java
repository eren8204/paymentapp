package com.example.paymentapp;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
public class MoreFragment extends Fragment {

    CardView aboutus;
    CardView companydocment;
    CardView Gallery;
    CardView RaiseTicket;
    CardView terms;
    CardView Privacy;
    CardView Refund;
    CardView Rateus;
    CardView Accountdeletion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, container, false);

        aboutus = view.findViewById(R.id.aboutus);
        companydocment = view.findViewById(R.id.companydocument);
        Gallery = view.findViewById(R.id.Gallery);
        RaiseTicket = view.findViewById(R.id.raiseticket);
        terms = view.findViewById(R.id.termsandcondition);
        Privacy = view.findViewById(R.id.privacypolicy);
        Refund = view.findViewById(R.id.refund);
        Rateus = view.findViewById(R.id.rateus);
        Accountdeletion = view.findViewById(R.id.accountdeletion);




 return view;
    }

}