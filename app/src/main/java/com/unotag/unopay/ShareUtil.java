package com.unotag.unopay;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.FileOutputStream;

public class ShareUtil {

    public static void shareApp(Context context, String memberId) {
        try {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.uno);

            File cachePath = new File(context.getExternalCacheDir(), "shared_images");
            cachePath.mkdirs();
            File imageFile = new File(cachePath, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();

            Uri imageUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", imageFile);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Install this amazing app and use my ID: " + memberId + " to get ₹649 as signup bonus! \n\n" +
                            "https://play.google.com/store/apps/details?id=" + context.getPackageName() +
                            "&referrer=utm_source%3Dgoogle%26utm_campaign%3D" + memberId
            );
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Download this amazing app!");

            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(Intent.createChooser(shareIntent, "Share via"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
