package com.github.n1try.bakingtime.model;

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
public class Recipe {
    private int id;
    private int servings;
    private String name;
    @SerializedName("image")
    private String imageUrl;
    private List<RecipeIngredient> ingredients;
    private List<RecipeStep> steps;
}
