package com.example.crockpot3.lists;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crockpot3.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> { // adapter class for binding recipe data to the recyclerview list cards
    private List<Recipe> recipeList;
    private OnItemClickListener listener;

    public RecipeAdapter(List<Recipe> recipeList, OnItemClickListener listener){
        this.recipeList = recipeList;
        this.listener = listener;
    }

    @Override
    public RecipeAdapter.RecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_recipe,
                parent, false);

        return new RecipeAdapter.RecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecipeAdapter.RecipeViewHolder holder, int position) {
        Recipe currentRecipe = recipeList.get(position);

        String thumbnailUrl = currentRecipe.getThumbnailUrl();

        Picasso.get().load(thumbnailUrl).into(holder.imageView); // using Picasso for simpler image loading

        holder.textView1.setText(currentRecipe.getRecipeTitle());
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener { // subclass for viewholder that also handles clicking recipes
        private ImageView imageView = itemView.findViewById(R.id.image_view);
        private TextView textView1 = itemView.findViewById(R.id.text_view_1);

        public RecipeViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        public ImageView getImageView() {
            return imageView;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }

        public TextView getTextView1() {
            return textView1;
        }

        public void setTextView1(TextView textView1) {
            this.textView1 = textView1;
        }

        @Override
        public void onClick(View v) { //handles clicking the list item
            int position = getBindingAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position);
            }
        }
    }

    public interface OnItemClickListener {
         default void onItemClick(int position){
        }
    }
}


