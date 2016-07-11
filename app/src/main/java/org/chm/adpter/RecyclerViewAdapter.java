package org.chm.adpter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import org.chm.bean.Qid;

import org.chm.common.Common;
import org.chm.dummy.DummyData;
import org.chm.examination.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 列表内容适配器
 * Created by chm on 2016/5/16.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ItemViewHoder>  {

    private List<Qid> goodsBeanList ;
//    private ImageLoader imageLoader ;//异步加载图片
//    private RecyclerView recyclerView;
    private Context context;
    public RecyclerViewAdapter(Context context, RecyclerView recyclerView){
        this.goodsBeanList = new ArrayList<>();
        this.context = context;
//        this.recyclerView = recyclerView;

        //构造测试数据
        this.goodsBeanList = DummyData.getQList();

//        this.imageLoader = new ImageLoader();
    }
    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    @Override
    public ItemViewHoder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_waterfall_item,parent,false);
        return new ItemViewHoder(view);
    }


    @Override
    public void onBindViewHolder(final ItemViewHoder holder, final int position) {
        Qid goodsBean = this.goodsBeanList.get(position);
//        View view = holder.itemView;

//        ImageView imageView = (ImageView) view.findViewById(R.id.masonry_item_img);
//        String coverURL = this.goodsBeanList.get(position).getCoverURL();
//            imageView.setTag(goodsBean.getId());

//        Drawable cacheImage = this.imageLoader.loadImage(goodsBean.getId(),coverURL, new ImageLoaderCallback() {
//            @Override
//            public void imageLoaded(String uid,String url, Drawable image) {
//                ImageView iv = (ImageView) recyclerView.findViewWithTag(uid);
//                if(iv!=null) {
//                    if(image!=null) {
//                        iv.setImageDrawable(image);
//                    }else{
//                        iv.setImageDrawable(context.getDrawable(R.drawable.loading));
//                    }
//                    Animation animation = AnimationUtils.loadAnimation(RecyclerViewAdapter.this.context,R.anim.fadein);
//                    iv.startAnimation(animation);
//                }
//            }
//        });
//        if(cacheImage!=null) {
//            imageView.setImageDrawable(cacheImage);
//            Animation animation = AnimationUtils.loadAnimation(RecyclerViewAdapter.this.context,R.anim.fadein);
//            imageView.startAnimation(animation);
//        }else{
//            imageView.setImageDrawable(context.getDrawable(R.drawable.loading));
//        }
        holder.itemView.setTag(R.id.tag_qid_id, goodsBean.getId());
        holder.itemView.setTag(R.id.tag_qid_answer_id, goodsBean.getAnswerId());
        holder.textView.setText(goodsBean.getId());
        if (goodsBean.isFinished()) {
            holder.textView.setBackgroundColor(this.context.getResources().getColor(R.color.grassGreen));
        } else {
            holder.textView.setBackgroundColor(this.context.getResources().getColor(R.color.whiteGray));
        }
        //如果设置了回调，则设置点击事件
        if (mOnItemClickLitener != null)
        {
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mOnItemClickLitener.onItemClick(holder.itemView, position);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return this.goodsBeanList.size();
    }


    class ItemViewHoder extends RecyclerView.ViewHolder{
        TextView textView;
        public ItemViewHoder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_title);
        }
    }
}
