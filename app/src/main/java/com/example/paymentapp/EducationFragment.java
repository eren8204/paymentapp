package com.example.paymentapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

public class EducationFragment extends Fragment {
    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private List<Video> videoList;
    @SuppressLint({"MissingInflatedId", "SetJavaScriptEnabled"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_education, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<String> videoPaths = Arrays.asList(
                "aOfLe3xJvNc",
                "zTmdjit_Tyc",
                "xtPmjYV-iPI",
                "vi_jn_4JfgE"
        );
        videoAdapter = new VideoAdapter(getContext(), videoPaths);
        recyclerView.setAdapter(videoAdapter);
        return view;
    }

}