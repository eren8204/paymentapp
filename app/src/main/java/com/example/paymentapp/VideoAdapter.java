package com.example.paymentapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private final Context context;
    private final List<String> videoPaths;

    public VideoAdapter(Context context, List<String> videoPaths) {
        this.context = context;
        this.videoPaths = videoPaths;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        String videoPath = videoPaths.get(position);
        holder.bind(videoPath);
    }

    @Override
    public int getItemCount() {
        return videoPaths.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {

        private final WebView webView;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            webView = itemView.findViewById(R.id.video_web);
        }

        @SuppressLint("SetJavaScriptEnabled")
        public void bind(String videoPath) {
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebChromeClient(new WebChromeClient());
            webView.setWebViewClient(new WebViewClient() {
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
                    + "<iframe src=\"https://www.youtube.com/embed/" + videoPath + "?autoplay=1\" "
                    + "frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
        }
    }
}
