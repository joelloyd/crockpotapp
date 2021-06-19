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

public class SavedRecipeAdapter extends RecyclerView.Adapter<SavedRecipeAdapter.SavedRecipeViewHolder> { // adapter class for showing saved recipes within a recyclerview card list
    private List<Recipe> savedRecipeList;
    private OnItemClickListener listener;

    public SavedRecipeAdapter(List<Recipe> recipeList, OnItemClickListener listener){
        this.savedRecipeList = recipeList;
        this.listener = listener;
    }

    @Override
    public SavedRecipeAdapter.SavedRecipeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_saved_recipe,
                parent, false);

        return new SavedRecipeAdapter.SavedRecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SavedRecipeAdapter.SavedRecipeViewHolder holder, int position) {
        Recipe currentSavedRecipe = savedRecipeList.get(position);

        String thumbnailUrl = currentSavedRecipe.getThumbnailUrl();

        Picasso.get().load(thumbnailUrl).into(holder.imageView); // using Picasso for easier image loading

        holder.textView1.setText(currentSavedRecipe.getRecipeTitle());
    }

    @Override
    public int getItemCount() {
        return savedRecipeList.size();
    }

    class SavedRecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener { // subclass for viewholder that also listens for clicks
        private ImageView imageView = itemView.findViewById(R.id.image_view);
        private TextView textView1 = itemView.findViewById(R.id.text_view_1);

        public SavedRecipeViewHolder(@NonNull @NotNull View itemView) {
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


