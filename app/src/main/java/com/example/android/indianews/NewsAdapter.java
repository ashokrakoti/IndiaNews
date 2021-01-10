package com.example.android.indianews;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    public NewsAdapter(NewsItemClicked listener) {
        this.listener = listener;
    }

    //news items list holding pojo
    private List<NewsItem> newsItems = new ArrayList<>();
    private NewsItemClicked listener ;

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
         View view = layoutInflater.inflate(R.layout.item_news, parent, false);
         NewsViewHolder viewHolder = new NewsViewHolder(view);
         view.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 listener.OnItemClicked(newsItems.get(viewHolder.getAdapterPosition()));
             }
         });
         return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
     NewsItem currentItem = newsItems.get(position);
     holder.titleView.setText(currentItem.getTitle());
     holder.author.setText(currentItem.getAuthor());
     //setting the image came form the image url we got from the response object.
     Glide.with(holder.itemView.getContext()).load(currentItem.getImageUrl()).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return newsItems.size();
    }

    public void updateNews(List<NewsItem> updatedNews) {
     newsItems.clear();
     newsItems.addAll(updatedNews );

     notifyDataSetChanged();
    }


    //this is the custom inner class that we developed extending the RecyclerView.ViewHolder
    public class NewsViewHolder  extends RecyclerView.ViewHolder {

        private TextView titleView ;
        private ImageView image;
        private TextView author;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.imageView);
            author = itemView.findViewById(R.id.author);
        }

    }
}
