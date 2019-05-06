package in.co.cfcs.digicard.MainActivity;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.loader.content.CursorLoader;

import java.io.ByteArrayOutputStream;

public class getPath {
    Context mContext;

    public String getRealPathFromURI(Context context, Uri myUri) {
        this.mContext = context;
        String[] proj = { MediaStore.Images.Media.DATA};

        CursorLoader loader = new CursorLoader(mContext, myUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

//public String getRealPathFromURI1(Uri uri) {
//    String[] proj = { MediaStore.Images.Media.DATA};
//    Cursor cursor = new CursorLoader(mContext, uri, proj, null, null, null);
//    cursor.moveToFirst();
//    int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//    return cursor.getString(idx);
//}

}
