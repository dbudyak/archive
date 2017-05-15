package com.testask.letsfly.util;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by dbudyak on 24.03.17.
 */
public class TextInputListener implements TextWatcher {

    public interface OnTextListener {
        void onText(String input);
    }

    private OnTextListener onTextListener;

    private boolean acceptSuggestions = false;

    public TextInputListener(OnTextListener onTextListener) {
        this.onTextListener = onTextListener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        acceptSuggestions = count >= 2;
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (acceptSuggestions) {
            onTextListener.onText(s.toString());
        }
    }
}