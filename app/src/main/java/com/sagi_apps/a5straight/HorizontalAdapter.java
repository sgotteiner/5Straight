package com.sagi_apps.a5straight;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by User on 15/07/2018.
 */

class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {


    List<GameToChoose> gamesList = Collections.emptyList();
    Context context;


    public HorizontalAdapter(List<GameToChoose> gamesList, Context context) {
        this.gamesList = gamesList;
        this.context = context;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txtNameAndRank;

        public MyViewHolder(View view) {
            super(view);
            txtNameAndRank = (TextView) view.findViewById(R.id.txtNameAndRank);
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_to_choose, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.txtNameAndRank.setText(gamesList.get(position).gettxtNameAndRank());

        holder.txtNameAndRank.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
            }

        });

    }


    @Override
    public int getItemCount() {
        return gamesList.size();
    }
}

