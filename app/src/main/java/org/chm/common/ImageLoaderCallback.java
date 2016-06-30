package org.chm.common;

import android.graphics.drawable.Drawable;

/**
 * 图片加载完成回调。
 * Created by chm on 2016/5/19.
 */
public interface ImageLoaderCallback {
    void imageLoaded(String uid, String url, Drawable image);
}
