package com.scienstechnologies.tollyteaser.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.scienstechnologies.tollyteaser.R;
import com.scienstechnologies.tollyteaser.activity.TTDetailsActivity;
import com.scienstechnologies.tollyteaser.model.ListModel;

/**
 * Created by onmymobile on 5/18/2017.
 */

public class TTListAdapter extends ArrayAdapter {
    private int Resource;
    private Context context;
    private LayoutInflater vi;
    ArrayList<ListModel> listModelsArray;


    public TTListAdapter(Context context, int resource, ArrayList chatUserName) {
        super(context, resource,chatUserName);
        Resource = resource;
        this.context = context;
        vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listModelsArray=chatUserName;


    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {

            holder=new ViewHolder();
            convertView = vi.inflate(R.layout.ttlist_item, parent, false);
            holder.title=(TextView)convertView.findViewById(R.id.title);
            holder.image=(ImageView) convertView.findViewById(R.id.image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }




        holder.title.setText(listModelsArray.get(position).getTitle());
        Picasso.with(context).load(listModelsArray.get(position).getImage_url()).resize(100,100).error(R.mipmap.tollyteaser).into(holder.image);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), TTDetailsActivity.class);
                intent.putExtra("title",listModelsArray.get(position).getTitle());
                intent.putExtra("image",listModelsArray.get(position).getImage_url());
                intent.putExtra("description",listModelsArray.get(position).getDescription());

                getContext().startActivity(intent);

            }
        });
        return convertView;
    }
    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    static class ViewHolder {
        ImageView image;
        TextView title;
        TextView description;


    }
}

