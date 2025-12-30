package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunit.groceryplus.R;

import java.util.ArrayList;
import java.util.List;

public class DrawableImageAdapter extends BaseAdapter {
    private Context context;
    private List<DrawableImage> drawableImages;
    private LayoutInflater inflater;
    private int selectedPosition = -1;

    public DrawableImageAdapter(Context context, List<DrawableImage> drawableImages) {
        this.context = context;
        this.drawableImages = drawableImages;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return drawableImages.size();
    }

    @Override
    public Object getItem(int position) {
        return drawableImages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DrawableImage drawableImage = drawableImages.get(position);
        
        // Show as numbered list: "1. apple.jpg"
        String numberedText = (position + 1) + ". " + drawableImage.getResourceName();
        holder.textView.setText(numberedText);
        
        // Set background for selected item
        if (position == selectedPosition) {
            holder.textView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));
        } else {
            holder.textView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }

        return convertView;
    }

    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();
    }

    public DrawableImage getSelectedImage() {
        if (selectedPosition >= 0 && selectedPosition < drawableImages.size()) {
            return drawableImages.get(selectedPosition);
        }
        return null;
    }

    private static class ViewHolder {
        TextView textView;
    }

    public static class DrawableImage {
        private String name;
        private String resourceName;

        public DrawableImage(String name, String resourceName) {
            this.name = name;
            this.resourceName = resourceName;
        }

        public String getName() {
            return name;
        }

        public String getResourceName() {
            return resourceName;
        }
    }
}
