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

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private Context context;
    private List<String> bannerImages;

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
        Glide.with(context)
                .load(bannerImages.get(position))
                .centerCrop()
                .into(holder.bannerIv);
    }

    @Override
    public int getItemCount() {
        return bannerImages.size();
    }

    class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerIv;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerIv = itemView.findViewById(R.id.bannerIv);
        }
    }
}
