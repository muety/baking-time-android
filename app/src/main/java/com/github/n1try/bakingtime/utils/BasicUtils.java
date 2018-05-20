package com.github.n1try.bakingtime.utils;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;

public class BasicUtils {
    public static SpannableStringBuilder styleTitle(String appTitle) {
        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(appTitle);
        spannableBuilder.setSpan(new TypefaceSpan("casual"), 0, appTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableBuilder.setSpan(new StyleSpan(Typeface.BOLD), 0, appTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableBuilder.setSpan(new ForegroundColorSpan(Color.WHITE), 0, appTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableBuilder;
    }
}
