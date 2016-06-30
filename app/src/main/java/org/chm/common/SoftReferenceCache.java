package org.chm.common;

import android.graphics.drawable.Drawable;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 软映射缓存。
 * Created by chm on 2016/5/25.
 */
public class SoftReferenceCache {
    /**
     * 缓存的图片
     */
    private static Map<String,SoftReference<Drawable>> cache_image = new HashMap<>();

    /**
     * 缓存图片
     * @param id 图片唯一标识
     * @param drawable 图片资源。
     */
    public static void cache(String id,Drawable drawable){
        if(!cache_image.containsKey(id))
            cache_image.put(id,new SoftReference<>(drawable));
    }

    /**
     * 获取一个已经缓存的图片
     * @param id 缓存图片id
     * @return 缓存的图片
     */
    public static Drawable getCache(String id){
        if(cache_image.containsKey(id))
            return cache_image.get(id).get();
        else
            return null;
    }

    /**
     * 是否包含此缓存图片
     * @param id
     * @return
     */
    public static boolean hasCache(String id){
        return cache_image.containsKey(id);
    }

}
