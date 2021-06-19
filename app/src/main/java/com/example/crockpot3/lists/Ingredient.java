package com.example.crockpot3.lists;

public class Ingredient { // simple data class with getter and setter
    private String ingredientName;

    public Ingredient() {

    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String _ingredientName) {
        this.ingredientName = _ingredientName;
    }
}
