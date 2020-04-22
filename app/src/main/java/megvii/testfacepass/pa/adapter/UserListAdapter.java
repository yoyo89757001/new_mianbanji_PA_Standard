package megvii.testfacepass.pa.adapter;

import android.content.Context;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.List;


import io.objectbox.query.LazyList;
import megvii.testfacepass.pa.MyApplication;
import megvii.testfacepass.pa.R;
import megvii.testfacepass.pa.beans.Subject;

import megvii.testfacepass.pa.view.GlideRoundTransform;


/**
 * Created by xingchaolei on 2017/12/5.
 */

public class UserListAdapter extends BaseAdapter {

    private List<Subject> mGroupNames;
    private LayoutInflater mLayoutInflater;
    private ItemDeleteButtonClickListener mItemDeleteButtonClickListener;

    private Context context;
    private RequestOptions myOptions2 =null;


    public UserListAdapter(List<Subject> data, Context context) {
        mGroupNames=data;
        this.context=context;
        myOptions2 = new RequestOptions()
                .fitCenter()
                .error(R.drawable.erroy_bg)
                //   .transform(new GlideCircleTransform(MyApplication.myApplication, 2, Color.parseColor("#ffffffff")));
                .transform(new GlideRoundTransform(context, 20));
    }

    public List<Subject> getData() {
        return mGroupNames;
    }

    public void setData(LazyList<Subject> data) {
        mGroupNames = data;
    }

    public void setOnItemDeleteButtonClickListener(ItemDeleteButtonClickListener listener) {
        mItemDeleteButtonClickListener = listener;
    }

    @Override
    public int getCount() {
        if (mGroupNames==null || mGroupNames.size()==0 || null==mGroupNames.get(0)){
            Log.d("UserListAdapter", "mGroupNames.size():000");
            return 0;
        }else {
            Log.d("UserListAdapter", "mGroupNames.size():" + mGroupNames.size());
            return mGroupNames.size();
        }

    }

    @Override
    public Object getItem(int position) {
        if (mGroupNames==null || mGroupNames.size()==0){
            return null;
        }else {
            return  mGroupNames.get(position);
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.layout_item_group_nameuser, parent, false);
            holder = new ViewHolder();
            holder.groupNameTv =  convertView.findViewById(R.id.tv_group_name);
            holder.deleteGroupIv =  convertView.findViewById(R.id.iv_delete_group);
            holder.touxiang1 =  convertView.findViewById(R.id.touxiang1);
            holder.touxiang2 =  convertView.findViewById(R.id.touxiang2);
            holder.touxiang3 =  convertView.findViewById(R.id.touxiang3);
            holder.id1 =  convertView.findViewById(R.id.id1);
            holder.id2 =  convertView.findViewById(R.id.id2);
            holder.id3 =  convertView.findViewById(R.id.id3);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.deleteGroupIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemDeleteButtonClickListener != null) {
                    mItemDeleteButtonClickListener.OnItemDeleteButtonClickListener(position);
                }
            }
        });

        holder.groupNameTv.setText(mGroupNames.get(position).getName()+" ID:"+mGroupNames.get(position).getTeZhengMa());
        if (mGroupNames.get(position).getFaceIds1()!=null){
            holder.id1.setVisibility(View.VISIBLE);
            holder.id1.setText("图片1的ID:"+mGroupNames.get(position).getFaceIds1());
        }else {
            holder.id1.setVisibility(View.GONE);
        }
        if (mGroupNames.get(position).getFaceIds2()!=null){
            holder.id2.setVisibility(View.VISIBLE);
            holder.id2.setText("图片2的ID:"+mGroupNames.get(position).getFaceIds2());
        }else {
            holder.id2.setVisibility(View.GONE);
        }

        if (mGroupNames.get(position).getFaceIds3()!=null){
            holder.id3.setVisibility(View.VISIBLE);
            holder.id3.setText("图片3的ID:"+mGroupNames.get(position).getFaceIds3());
        }else {
            holder.id3.setVisibility(View.GONE);
        }

        try {
            String p1=mGroupNames.get(position).getFaceIds1();
            String p2=mGroupNames.get(position).getFaceIds2();
            String p3=mGroupNames.get(position).getFaceIds3();

            if (p1!=null){
                holder.touxiang1.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(MyApplication.SDPATH3+ File.separator+p1+".png")
                        .apply(myOptions2)
                        .into(holder.touxiang1);
            }else {
                holder.touxiang1.setVisibility(View.GONE);
            }
            if (p2!=null){
                holder.touxiang2.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(MyApplication.SDPATH3+ File.separator+p2+".png")
                        .apply(myOptions2)
                        .into(holder.touxiang2);
            }else{
                holder.touxiang2.setVisibility(View.GONE);
            }
            if (p3!=null){
                holder.touxiang3.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(MyApplication.SDPATH3+ File.separator+p3+".png")
                        .apply(myOptions2)
                        .into(holder.touxiang3);
            }else {
                holder.touxiang3.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }


    public static class ViewHolder {
        TextView groupNameTv,id1,id2,id3;
        ImageView deleteGroupIv,touxiang1,touxiang2,touxiang3;
    }


    public interface ItemDeleteButtonClickListener {

        void OnItemDeleteButtonClickListener(int position);

    }
}
