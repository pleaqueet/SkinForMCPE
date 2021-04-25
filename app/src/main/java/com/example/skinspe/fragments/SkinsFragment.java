package com.example.skinspe.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.skinspe.DetailedActivity;
import com.example.skinspe.R;
import com.example.skinspe.adapters.RecyclerViewAdapter;
import com.example.skinspe.adapters.SQLiteHelper;
import com.example.skinspe.models.Skin;

import java.io.IOException;
import java.util.ArrayList;


public class SkinsFragment extends Fragment {

    private SQLiteHelper mDBHelper;
    private SQLiteDatabase mDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_skins, null);

        mDBHelper = new SQLiteHelper(getContext());

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


        Cursor cursor = mDb.query("skins", new String[]{"_id", "skin", "favorite"}, null,null,null, null, null);

        int favorite;
        int id;
        String skin = null;
        ArrayList<Skin> skinItems = new ArrayList<>();

        while (cursor.moveToNext()) {
            favorite = cursor.getInt(cursor.getColumnIndex("favorite"));
            id = cursor.getInt(cursor.getColumnIndex("_id"));
            skin = cursor.getString(cursor.getColumnIndex("skin"));
            skinItems.add(new Skin(skin, id, favorite));
        }
        cursor.close();


        // инициализация списка скинов
        RecyclerView recView = rootView.findViewById(R.id.recViewSkins);
        recView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(getContext(), skinItems);
        adapter.setClickListener(this::onItemClick);

        recView.setAdapter(adapter);

        return rootView;
    }

    public  void onItemClick(View view, int position) {

        mDBHelper = new SQLiteHelper(getContext());

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


        Cursor cursor = mDb.query("skins", new String[]{"skin"}, null,null,null, null, null);
        String skin = null;
        ArrayList<String> skins = new ArrayList<>();
        while (cursor.moveToNext()) {
            skin = cursor.getString(cursor.getColumnIndex("skin"));
            skins.add(skin);
        }

        cursor.close();

        // переход на детализированный экран
        Intent intent = new Intent(getActivity().getApplicationContext(), DetailedActivity.class);
        intent.putExtra("skin", skins.get(position));
        startActivity(intent);
    }
}