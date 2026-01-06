package com.sunit.groceryplus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sunit.groceryplus.R;

import java.util.List;

/** BannerAdapter - Handles the carousel display of promotion banners on the home screen. */
public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private Context context;
    private List<String> bannerImages;

    /** Constructor. */
    public BannerAdapter(Context context, List<String> bannerImages) {
        this.context = context;
        this.bannerImages = bannerImages;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        String imageSource = bannerImages.get(position);
        
        // Check if imageSource is a drawable resource name
        int resId = context.getResources().getIdentifier(imageSource, "drawable", context.getPackageName());
        
        if (resId != 0) {
            // It's a local drawable
            Glide.with(context)
                    .load(resId)
                    .centerCrop()
                    .into(holder.bannerIv);
        } else {
            // It's a URL or path or unknown, try loading as string
            Glide.with(context)
                    .load(imageSource)
                    .centerCrop()
                    .placeholder(R.drawable.ic_product_placeholder)
                    .error(R.drawable.ic_product_placeholder)
                    .into(holder.bannerIv);
        }
    }

    @Override
    public int getItemCount() {
        return bannerImages.size();
    }

    /** ViewHolder for the banner image view. */
    class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerIv; // The banner image component

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerIv = itemView.findViewById(R.id.bannerIv);
        }
    }
}
