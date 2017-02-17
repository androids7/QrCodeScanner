package com.duoyi.qrdecode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.duoyi.provider.qrscan.camera.CameraConfigurationManager;

import java.io.IOException;

public class DecodeEntry {
    static {
        System.loadLibrary("qrscan");
    }

    public static String decodeFromFile(String filename, BarcodeFormat barcodeFormat) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap scanBitmap;
        options.inJustDecodeBounds = true; // 先获取原大小
        scanBitmap = BitmapFactory.decodeFile(filename, options);
        options.inJustDecodeBounds = false; // 获取新的大小

        int heightSampleSize = (int) (options.outHeight / CameraConfigurationManager.screenHeight);
        int widhtSampleSize = (int) (options.outWidth / CameraConfigurationManager.screenWidth);
        int sampleSize = 1;
        if (heightSampleSize >= 1 || widhtSampleSize >= 1 ){
            sampleSize = heightSampleSize > widhtSampleSize? heightSampleSize:widhtSampleSize;
        }

        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(filename, options);
        return getPixelsByBitmap(scanBitmap, barcodeFormat);
    }

    public static String getPixelsByBitmap(Bitmap bitmap, BarcodeFormat barcodeFormat) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int size = width * height;

        int pixels[] = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();
        if (barcodeFormat != null) {
            return decodeFileFromJNI(barcodeFormat.get(), pixels, width, height);
        } else {
            return decodeFileFromJNI(BarcodeFormat.BARCODE | BarcodeFormat.QRCODE, pixels, width, height);
        }
    }

    public static String getDecodeResult(BarcodeFormat barcodeFormat, byte[] data, int dataWidth,
                                         int dataHeight, int left, int top, int width, int height) {
        if (barcodeFormat != null) {
            return decodeFromJNI(barcodeFormat.get(), data, dataWidth, dataHeight, left, top, width, height);
        } else {
            return decodeFromJNI(BarcodeFormat.BARCODE | BarcodeFormat.QRCODE, data, dataWidth, dataHeight, left, top, width, height);
        }

    }

    public native static String decodeFromJNI(int decodeCode, byte[] data, int dataWidth,
                                              int dataHeight, int left, int top, int width, int height);

    public native static String decodeFileFromJNI(int decodeCode, int[] pixels, int width, int height);

}
