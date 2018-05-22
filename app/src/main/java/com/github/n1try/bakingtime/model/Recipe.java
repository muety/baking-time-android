package com.github.n1try.bakingtime.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.n1try.bakingtime.utils.SerializationUtils;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(of = "name")
public class Recipe implements Parcelable {
    private int id;
    private int servings;
    private String name;
    @SerializedName("image")
    private String imageUrl;
    private List<RecipeIngredient> ingredients;
    private List<RecipeStep> steps;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(SerializationUtils.serialize(this));
    }

    private static Recipe readFromParcel(Parcel parcel) {
        try {
            return SerializationUtils.deserialize(parcel.readString(), Recipe.class);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return readFromParcel(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
}
