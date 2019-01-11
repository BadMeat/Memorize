package com.dolan.user.memorize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Badmeat on 04/03/2018.
 */

public class RecycleAdapter extends Adapter<RecycleAdapter.ImageViewHolder> {

    private List<String> images = new ArrayList<>();
    private List<String> titleImage = new ArrayList<>();
    private List<String> tanggalList = new ArrayList<>();
    private List<Integer> likedList = new ArrayList<>();
    private List<String> kodeList = new ArrayList<>();
    public static final String TAG = RecycleAdapter.class.getSimpleName();
    private Context context;
    MyDbManager manager;

    public RecycleAdapter(
            List<String> images, Context context, List<String> titleImage,
            List<String> tanggalList, List<Integer> likedList, List<String> kodeList) {
        this.images = images;
        this.context = context;
        this.titleImage = titleImage;
        this.tanggalList = tanggalList;
        this.likedList = likedList;
        this.kodeList = kodeList;
        manager = new MyDbManager(context, "", null, 0);
    }

    @Override
    public RecycleAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_album, parent, false);
        ImageViewHolder holder = new ImageViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecycleAdapter.ImageViewHolder holder, final int position) {
        Glide.with(this.context).load(images.get(position)).into(holder.album);
        Typeface font = Typeface.createFromAsset(context.getAssets(), "font/Love_Letters.ttf");
        holder.albumText.setText(titleImage.get(position));
        holder.albumDate.setText(tanggalList.get(position));
        holder.albumText.setTypeface(font);
        holder.albumDate.setTypeface(font);
        /**
         * Handle Like Button
         * ========================================================
         */
        holder.liked.setLiked(likedList.get(position) != 0);
        holder.liked.setOnLikeListener(new OnLikeListener() {
            Content content = new Content();

            @Override
            public void liked(LikeButton likeButton) {
                content.setKode(kodeList.get(position));
                content.setDeskripsi(titleImage.get(position));
                content.setLiked(1);
                boolean result = manager.UpdateData(content);
                if (result) {
                    Log.d(TAG, "Berhasil");
                } else {
                    Log.d(TAG, "GAGAL");
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                content.setKode(kodeList.get(position));
                content.setDeskripsi(titleImage.get(position));
                content.setLiked(0);
                boolean result = manager.UpdateData(content);
                if (result) {
                    Log.d(TAG, "Berhasil");
                } else {
                    Log.d(TAG, "GAGAL");
                }
            }
        });

        /**
         * ========================================================
         */
        holder.lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, GalleryActivity.class);
                intent.putExtra("image_url", images.get(position));
                intent.putExtra("image_name", titleImage.get(position));
                intent.putExtra("image_liked", likedList.get(position));
                context.startActivity(intent);
                ((Activity) context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView album;
        private TextView albumDate;
        private TextView albumText;
        private LinearLayout lay;
        private LikeButton liked;

        public ImageViewHolder(View itemView) {
            super(itemView);
            album = itemView.findViewById(R.id.album);
            albumText = itemView.findViewById(R.id.album_text);
            lay = itemView.findViewById(R.id.laylayku);
            albumDate = itemView.findViewById(R.id.album_date);
            liked = itemView.findViewById(R.id.heart_button);
        }
    }
}
