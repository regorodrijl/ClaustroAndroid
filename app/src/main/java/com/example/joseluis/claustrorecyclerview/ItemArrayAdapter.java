package com.example.joseluis.claustrorecyclerview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ItemArrayAdapter extends RecyclerView.Adapter<ItemArrayAdapter.ViewHolder> {

    //All methods in this adapter are required for a bare minimum recyclerview adapter
    private int listItemLayout;
    private ArrayList<Item> itemList;
    // Constructor of the class
    public ItemArrayAdapter(int layoutId, ArrayList<Item> itemList) {
        listItemLayout = layoutId;
        this.itemList = itemList;
    }
    // obtener tamaño lista
    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(listItemLayout, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int listPosition) {
        TextView item = holder.item;
        item.setText(itemList.get(listPosition).getName());
        item.setBackgroundColor(Color.GREEN);
        if(!itemList.get(listPosition).getFirmado()){
            item.setBackgroundColor(0x00000000);
        }
    }

    // Static inner class to initialize the views of rows
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView item;
        private final Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            context = itemView.getContext();
            item = (TextView) itemView.findViewById(R.id.nombreTextView);
        }
        @Override
        public void onClick(View view) {
            final Intent intent;
          //  Toast.makeText(ClaustroRecyclerV.this, "onClick " + getLayoutPosition() + " " + item.getText(),Toast.LENGTH_SHORT ).show();
            Log.d("onclick", "onClick " + getLayoutPosition() + " " + item.getText());
            intent = new Intent(view.getContext(), pintando.class);
            intent.putExtra("nombre", item.getText());
            intent.putExtra("cod", getLayoutPosition());
            ((Activity) context).startActivity(intent);
        }
    }
}