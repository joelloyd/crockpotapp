package com.example.crockpot3.lists;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crockpot3.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> { // adapter class that binds the ingredient data to recyclerview list items
    private List<Ingredient> ingredientList;

    public IngredientAdapter(List<Ingredient> ingredientList){
        this.ingredientList = ingredientList;
    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) { //create viewholder with correct list layout
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_ingredient,
                parent, false);

        return new IngredientViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {
        Ingredient currentItem = ingredientList.get(position);
        holder.textView1.setText(currentItem.getIngredientName());
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder{ // subclass extending existing recycler viewholder
        private TextView textView1 = itemView.findViewById(R.id.text_view_1);

        public IngredientViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }

        public TextView getTextView1() {
            return textView1;
        }

        public void setTextView1(TextView textView1) {
            this.textView1 = textView1;
        }
    }
}

