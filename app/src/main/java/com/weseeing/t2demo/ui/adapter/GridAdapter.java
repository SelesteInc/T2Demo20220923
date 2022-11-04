package com.weseeing.t2demo.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.weseeing.t2demo.R;

import java.util.ArrayList;
import java.util.List;

public class GridAdapter extends BaseAdapter {

    private Context mContext;
    private List<MediaData> mListData = new ArrayList<>();
    private ViewHolder viewHolder = null;
    public GridAdapter(Context context){
        mContext = context;
    }
    public void updateAdapter(List<MediaData> listData){
        mListData.clear();
        if (listData != null && listData.size() >0){
            mListData = listData;
        }
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public MediaData getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = getViewHolder(convertView,position);
        return convertView;
    }

    /**
     * 获取viewholder get viewholder
     *
     * @param convertView
     */
    private View getViewHolder(View convertView, int position) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_item_gird, null);
            viewHolder = new ViewHolder();
            viewHolder.imgView = (ImageView) convertView.findViewById(R.id.imgView);
            viewHolder.imgVideo = convertView.findViewById(R.id.videoIcon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MediaData mediaData = getItem(position);
        if(mediaData != null) {
            if (mediaData.type == MediaData.TYPE_VIDEO){
                viewHolder.imgVideo.setVisibility(View.VISIBLE);
            }else{
                viewHolder.imgVideo.setVisibility(View.GONE);
            }

            Glide.with(mContext)
                    .load(mediaData.url)
                    .fitCenter()
                    .into(viewHolder.imgView);
        }
        return convertView;
    }

    static class ViewHolder {
       ImageView imgView;
       ImageView imgVideo;
    }
}
