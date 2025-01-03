package com.example.paymentapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;

import java.util.ArrayList;
import java.util.Objects;

public class BusinessFragment extends Fragment {
    private TextView uname,uid,m_status,id_status;
    private CardView my_team;
    @SuppressLint("CutPasteId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_business, container, false);
        ImageSlider imageSlider = view.findViewById(R.id.imageSlider);
        uname = view.findViewById(R.id.id_card_uid);
        uid  = view.findViewById(R.id.id_card_uid);
        m_status = view.findViewById(R.id.membership_status);
        id_status = view.findViewById(R.id.membership_status);
        my_team = view.findViewById(R.id.my_team);

        TextView marquee_text = view.findViewById(R.id.marquee_text);
        marquee_text.setSelected(true);
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        slideModels.add(new SlideModel(R.drawable.a, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.b, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.c, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.d, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.e, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.f, ScaleTypes.FIT));

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username","Hello!");
        String memberId = sharedPreferences.getString("memberId","UP000000");
        String status = sharedPreferences.getString("status","FREE");
        uname.setText(username);
        uid.setText(memberId);
        m_status.setText(status);
        id_status.setText(status);

        my_team.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), MyTeam.class);
                startActivity(intent);
            }
        });

        return  view;
    }
}