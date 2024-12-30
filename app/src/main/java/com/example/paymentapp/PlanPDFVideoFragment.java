package com.example.paymentapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlanPDFVideoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_plan_p_d_f_video, container, false);

        // Find the CardView from the inflated view
        CardView pdfCardView = rootView.findViewById(R.id.pdfCard);

        pdfCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), openPDF_activity.class);
                intent.putExtra("pdfName", "sample.pdf");
                startActivity(intent);
            }
        });

        return rootView;
    }
}
