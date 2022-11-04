package com.weseeing.t2demo.ui.adapter;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.weseeing.framework.t.SeeTDevice;
import com.weseeing.t2demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LinHui.Xiao
 * @data Created by 2016/11/24 16:58
 */

public class DeviceAdapter  extends BaseAdapter {
    private  static  final String TAG ="DeviceAdapter";
    private List<Object> mDeviceList;
    private OnConnectClickListener onClickListener;
    private LayoutInflater mInflater;
    public DeviceAdapter(Activity context){
        mInflater = context.getLayoutInflater();
        mDeviceList = new ArrayList<>();
    }

    public void updateWiFiDeviceData( List<SeeTDevice> data){
        if(data != null){
            mDeviceList.clear();
            mDeviceList.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void updateBluetoothDeviceData( List<BluetoothDevice> data){
        if(data != null){
            mDeviceList.clear();
            mDeviceList.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void setOnConnectClickListener(OnConnectClickListener listener){
        onClickListener = listener;
    }

    @Override
    public int getCount() {

        return mDeviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDeviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if( null != convertView){
            viewHolder = (ViewHolder)convertView.getTag();
        }else {
            convertView = mInflater.inflate(R.layout.layout_device_item,viewGroup,false);
            viewHolder = new ViewHolder();
            viewHolder.findView(convertView);
            convertView.setTag(viewHolder);
        }
        bindView(viewHolder,position);
        return convertView;
    }

    private void bindView(ViewHolder viewHolder ,int position){
        Object device = getItem(position);
        if( null != device ){
            if (device instanceof  BluetoothDevice){
                BluetoothDevice bluetoothDevice =(BluetoothDevice)device;
                viewHolder.tvTilte.setText(bluetoothDevice.getName());
                viewHolder.tvSubtitle.setText(bluetoothDevice.getAddress());
                viewHolder.butConnect.setTag(position);
                viewHolder.butConnect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(onClickListener != null){
                            onClickListener.onClick(getItem((int)view.getTag()));
                        }
                    }
                });
            }else if(device instanceof SeeTDevice){
                SeeTDevice wiFiDevice =(SeeTDevice)device;
                viewHolder.tvTilte.setText(wiFiDevice.getMsg());
                viewHolder.tvSubtitle.setText(wiFiDevice.getIp());
                viewHolder.butConnect.setTag(position);
                viewHolder.butConnect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(onClickListener != null){
                            onClickListener.onClick(getItem((int)view.getTag()));
                        }
                    }
                });
            }
        }
    }

    static class  ViewHolder{
        TextView tvTilte;
        TextView tvSubtitle;
        Button butConnect;

        public void findView(View view){
            tvTilte = (TextView)view.findViewById(R.id.tvTitle);
            tvSubtitle = (TextView)view.findViewById(R.id.tvSubtitle);
            butConnect = (Button)view.findViewById(R.id.butConnect);
        }
    }

    public interface  OnConnectClickListener{
        void onClick(Object device);
    }
}
