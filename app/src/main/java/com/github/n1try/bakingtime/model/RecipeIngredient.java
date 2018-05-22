package com.github.n1try.bakingtime.model;

import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;

import com.github.n1try.bakingtime.utils.SerializationUtils;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "name")
public class RecipeIngredient implements Parcelable {
    private double quantity;
    private String measure;
    @SerializedName("ingredient")
    private String name;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(SerializationUtils.serialize(this));
    }

    private static RecipeIngredient readFromParcel(Parcel parcel) {
        try {
            return SerializationUtils.deserialize(parcel.readString(), RecipeIngredient.class);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static final Creator<RecipeIngredient> CREATOR = new Creator<RecipeIngredient>() {
        @Override
        public RecipeIngredient createFromParcel(Parcel in) {
            return readFromParcel(in);
        }

        @Override
        public RecipeIngredient[] newArray(int size) {
            return new RecipeIngredient[size];
        }
    };

    @Override
    public String toString() {
        return String.format("%s %ss of %s", quantity, measure.toLowerCase(), name);
    }

    public SpannableStringBuilder format() {
        String str = toString() + "\n";

        int i1 = str.indexOf(" ");
        int i2 = str.indexOf(" ", i1 + 1);
        int i3 = str.indexOf(" ", i2 + 1);

        SpannableStringBuilder builder = new SpannableStringBuilder(str);
        builder.setSpan(new StyleSpan(Typeface.BOLD), 0, i1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new StyleSpan(Typeface.BOLD), i3, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return builder;
    }
}
