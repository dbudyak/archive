package com.testask.letsfly.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.testask.letsfly.R;
import com.testask.letsfly.model.City;

import java.util.Objects;

/**
 * Created by dbudyak on 24.03.17.
 */

public class Utils {
    @NonNull
    public static Bitmap getBitmapFromView(View customMarkerView) {
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();

        Bitmap resultBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return resultBitmap;
    }

    @NonNull
    public static BitmapDescriptor getBitmapDescriptor(Context context, @LayoutRes int layoutRes, @DrawableRes int drawableRes) {
        View customMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(layoutRes, null);
        ImageView iv = (ImageView) customMarkerView.findViewById(R.id.marker_image);
        iv.setImageResource(drawableRes);
        Bitmap markerBitmap = getBitmapFromView(customMarkerView);
        return BitmapDescriptorFactory.fromBitmap(markerBitmap);
    }

    @NonNull
    public static BitmapDescriptor getLabelBitmapDescriptor(Context context, String title) {
        View customMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.marker_cap_view, null);
        TextView markerImageView = (TextView) customMarkerView.findViewById(R.id.marker_text);
        markerImageView.setText(title);
        Bitmap markerBitmap = getBitmapFromView(customMarkerView);
        return BitmapDescriptorFactory.fromBitmap(markerBitmap);
    }

    public static boolean isValidInput(Context context, AutoCompleteTextView textView) {
        String text = textView.getText().toString();
        if (TextUtils.isEmpty(text)) {
            textView.setError(context.getString(R.string.error_field_empty));
            return false;
        }
        City city = (City) textView.getTag();
        if (Objects.isNull(city)) {
            textView.setError(context.getString(R.string.error_city_not_found));
            return false;
        }
        if (Objects.isNull(city.getIata()) || city.getIata().isEmpty() || Objects.isNull(city.getLocation())) {
            textView.setError(context.getString(R.string.error_field_data));
        }
        return true;
    }
}
