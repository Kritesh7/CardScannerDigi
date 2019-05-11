package com.scanlibrary;

import android.net.Uri;


public interface IScanner {

    void onBitmapSelect(Uri uri, String selectedImagePath);

    void onScanFinish(Uri uri, String selectedPath);
}
