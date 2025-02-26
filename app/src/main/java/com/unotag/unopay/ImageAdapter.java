package com.unotag.unopay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private final Context context;
    private final List<Bitmap> imageBitmaps; // Store bitmaps instead of URLs
    private int width = 0;

    public ImageAdapter(Context context,List<Bitmap> imageBitmaps,int width) {
        this.context = context;
        this.imageBitmaps = new ArrayList<>();
        this.width = width;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addImage(Bitmap bitmap) {
        imageBitmaps.add(bitmap);
        notifyDataSetChanged(); // Notify RecyclerView to update UI
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Bitmap bitmap = imageBitmaps.get(position);
        holder.imageView.setImageBitmap(bitmap); // Set bitmap to ImageView
        ViewGroup.LayoutParams layoutParams = holder.imageView.getLayoutParams();
        layoutParams.width = width; // Set width dynamically
        layoutParams.height = width; // Make height same as width
        holder.imageView.setLayoutParams(layoutParams);
        holder.imageView.setOnClickListener(v->{
            showImageDialog(bitmap);
        });
    }

    @Override
    public int getItemCount() {
        return imageBitmaps.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
    private void showImageDialog(Bitmap bitmap) {
        android.app.Dialog dialog = new android.app.Dialog(context);
        dialog.setContentView(R.layout.gallery_image);

        ImageView imageView = dialog.findViewById(R.id.dialogImageView);
        imageView.setImageBitmap(bitmap);

        dialog.show();
    }

}
