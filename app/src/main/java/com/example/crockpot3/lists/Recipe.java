package com.example.crockpot3.lists;

public class Recipe { // simple data class for recipes with getters and setters for each attribute
    private String recipeTitle;
    private String thumbnailUrl;
    private String recipeId;

    public Recipe() {

    }

    public String getRecipeTitle(){
        return this.recipeTitle;
    }

    public void setRecipeTitle(String _recipeTitle){
        this.recipeTitle = _recipeTitle;
    }

    public String getThumbnailUrl(){
        return this.thumbnailUrl;
    }

    public void setThumbnailUrl(String _thumbnailUrl){
        this.thumbnailUrl = _thumbnailUrl;
    }

    public String getRecipeId(){
        return this.recipeId;
    }

    public void setRecipeId(String _recipeId){
        this.recipeId = _recipeId;
    }
}
