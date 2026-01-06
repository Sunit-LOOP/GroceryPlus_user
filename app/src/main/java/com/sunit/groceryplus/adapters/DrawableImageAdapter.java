package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.sunit.groceryplus.utils.ProductImageLoader;

import com.sunit.groceryplus.R;

import java.util.ArrayList;
import java.util.List;

/** DrawableImageAdapter - Handles image selection grid for products/categories in the Admin Panel. */
public class DrawableImageAdapter extends BaseAdapter {
    private Context context;
    private List<DrawableImage> drawableImages;
    private LayoutInflater inflater;
    private int selectedPosition = -1;

    /** Constructor. */
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
            convertView = inflater.inflate(R.layout.grid_item_drawable_image, parent, false);
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.drawableNameText);
            holder.imageView = convertView.findViewById(R.id.drawableImageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DrawableImage drawableImage = drawableImages.get(position);
        
        holder.textView.setText(drawableImage.getResourceName());
        
        // Load actual drawable image
        ProductImageLoader.load(context, holder.imageView, drawableImage.getResourceName(), R.drawable.product_icon);
        
        // Set background for selected item
        if (position == selectedPosition) {
            convertView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_blue_light));
        } else {
            convertView.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        }

        return convertView;
    }

    /** Sets the current selected item index. */
    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        notifyDataSetChanged();
    }

    /** Retrieves the currently selected image object. */
    public DrawableImage getSelectedImage() {
        if (selectedPosition >= 0 && selectedPosition < drawableImages.size()) {
            return drawableImages.get(selectedPosition);
        }
        return null;
    }

    /** ViewHolder for caching image grid components. */
    private static class ViewHolder {
        TextView textView; // Resource filename text
        ImageView imageView; // Preview thumbnail
    }

    /** DrawableImage - Represents a drawable resource with readable name and resource identifier. */
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
