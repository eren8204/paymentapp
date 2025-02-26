package com.unotag.unopay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlanPDFVideoFragment extends Fragment {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_plan_p_d_f_video, container, false);

        // Configure PDF CardView
        CardView pdfCardView = rootView.findViewById(R.id.pdfCard);
        pdfCardView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), openPDF_activity.class);
            intent.putExtra("pdfName", "unopay.pdf");
            startActivity(intent);
        });


        WebView videoWebView = rootView.findViewById(R.id.video_web);
        videoWebView.getSettings().setJavaScriptEnabled(true);
        videoWebView.setWebChromeClient(new WebChromeClient());


        videoWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
                view.getContext().startActivity(intent);
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(intent);
                return true;
            }
        });

        String videoId = "aOfLe3xJvNc";
        String html = "<html>"
                + "<head>"
                + "<style>"
                + "body { margin: 0; padding: 0; }"
                + ".video-container { position: relative; padding-bottom: 56.25%; height: 0; overflow: hidden; }"
                + ".video-container iframe { position: absolute; top: 0; left: 0; width: 100%; height: 100%; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class=\"video-container\">"
                + "<iframe src=\"https://www.youtube.com/embed/" + videoId + "?autoplay=1\" "
                + "frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>"
                + "</div>"
                + "</body>"
                + "</html>";

        videoWebView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);

        return rootView;
    }
}
