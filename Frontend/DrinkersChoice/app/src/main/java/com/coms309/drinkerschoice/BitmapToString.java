package com.coms309.drinkerschoice;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

//for you sammy
public class BitmapToString {
    String bitmapString;
    Bitmap finalBitmap;

	/**
	 * BitmapToString constructor
	 * @param str
	 */
	public BitmapToString(String str) {
        bitmapString = str;
        finalBitmap = StringToBit(str);
    }

	/**
	 * BitmapToString constructor
	 * @param bit
	 */
    public BitmapToString(Bitmap bit) {
        finalBitmap = bit;
        bitmapString = BitToString(bit);
    }

	/**
	 * converts a bitmap to a string.
	 * @param bitmap
	 * @return strReturn
	 */
	public String BitToString(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) 50/width);
        float scaleHeight = ((float) 50/height);
        if (50/width > 1 || 50/height > 1) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 1, byteArrayOutputStream);
            byte[] byteArr = byteArrayOutputStream.toByteArray();
            String strReturn = Base64.encodeToString(byteArr, Base64.DEFAULT);
            return strReturn;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);
        Bitmap resized = Bitmap.createBitmap(bitmap, 0,0, width, height, matrix, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.PNG, 1, byteArrayOutputStream);
        byte[] byteArr = byteArrayOutputStream.toByteArray();
        String strReturn = Base64.encodeToString(byteArr, Base64.DEFAULT);
        return strReturn;
    }

	/**
	 * converts a string to a bitmap
	 * @param str
	 * @return bitmap
	 */
	public Bitmap StringToBit(String str) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.decode(str, Base64.DEFAULT));
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        return bitmap;
    }

	/**
	 * @return finalBitmap
	 */
	public Bitmap getBitmap() {
        return finalBitmap;
    }

	/**
	 * @return bitmapString
	 */
	public String getBitmapString() {
        return bitmapString;
    }
}
