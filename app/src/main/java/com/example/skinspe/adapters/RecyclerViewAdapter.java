package com.example.skinspe.adapters;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skinspe.R;
import com.example.skinspe.models.Skin;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private SQLiteHelper mDBHelper;
    private SQLiteDatabase mDb;

    private Context context;
    ArrayList<Skin> skinItems = new ArrayList<>();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    public RecyclerViewAdapter(Context context, ArrayList<Skin> images) {
        this.context = context;
        this.skinItems = images;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_skin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageView img = (ImageView) holder.itemView.findViewById(R.id.icon);
        ImageView favBut = holder.itemView.findViewById(R.id.favBut);

        InputStream inputStream = null;
        try{
            Skin skinItem = skinItems.get(position);
            inputStream = context.getApplicationContext().getAssets().open("images/" + skinItem.getSkin());
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            img.setImageDrawable(drawable);
            if (skinItem.getFavStatus() == 0){
                favBut.setBackgroundResource(R.drawable.ic_favorites_false);
            } else {
                favBut.setBackgroundResource(R.drawable.ic_favorites_true);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            try{
                if(inputStream!=null)
                    inputStream.close();
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }


    @Override
    public int getItemCount() {
        return skinItems.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView favBut;
        ImageView img;

        ViewHolder(View itemView) {

            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.icon);
            favBut = itemView.findViewById(R.id.favBut);

            favBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDBHelper = new SQLiteHelper(context);

                    try {
                        mDBHelper.updateDataBase();
                    } catch (IOException mIOException) {
                        throw new Error("UnableToUpdateDatabase");
                    }

                    try {
                        mDb = mDBHelper.getWritableDatabase();
                    } catch (SQLException mSQLException) {
                        throw mSQLException;
                    }
                    int position = getAdapterPosition();

                    Skin skinItem = skinItems.get(position);

                    if (skinItem.getFavStatus() == 0){
                        skinItem.setFavStatus(1);
                        mDBHelper.add_fav(skinItem.getKey_id());
                        favBut.setBackgroundResource(R.drawable.ic_favorites_true);
                    } else if (skinItem.getFavStatus() == 1) {
                        skinItem.setFavStatus(0);
                        mDBHelper.remove_fav(skinItem.getKey_id());
                        favBut.setBackgroundResource(R.drawable.ic_favorites_false);
                    }
                }
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }


}
