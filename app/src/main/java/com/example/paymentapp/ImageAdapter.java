package com.example.paymentapp;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;
import java.util.Objects;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private final Context context;
    private final List<String> imageUrls;

    public ImageAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        // Use Glide to load the image
        Log.d("Arshgendu", "Loading image: " + imageUrl);
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(holder.imageView);

        holder.imageView.setOnClickListener(v -> {
            showImageInDialog(imageUrl);
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
    private void showImageInDialog(String imageUrl) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_image);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView dialogImageView = dialog.findViewById(R.id.dialogImage);
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(dialogImageView);

        dialog.show();
    }
}