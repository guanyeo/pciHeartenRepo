package guan.pcihearten;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

// Adapter for the recycler view

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyViewHolder> {
//    specifies variable
    private Context mContext;
    private List<album> albumList;

//    A class with items
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;

//    Initialize the items found in album_card.xml
        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }

//    Some sort of adapter
    public AlbumAdapter(Context mContext, List<album> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

//Create a view holder of album_card to be inflated
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card, parent, false);

        return new MyViewHolder(itemView);
    }

//Populates the recyclerView i think
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        album album = albumList.get(position);
        holder.title.setText(album.getName());
        holder.count.setText(album.getNumOfSongs() + " songs");

        // loading album cover using Glide library
        Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

//    Get size of all the list together
    @Override
    public int getItemCount() {
        return albumList.size();
    }














}
