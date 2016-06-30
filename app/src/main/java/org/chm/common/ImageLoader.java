package org.chm.common;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 图片加载器
 * Created by chm on 2016/5/19.
 */
public class ImageLoader {


    //读取图片所用线程池。
    private ExecutorService cachedThreadPools ;
    public ImageLoader(){
        this.cachedThreadPools = Executors.newCachedThreadPool();
    }

    public Drawable loadImage(final String uid,final String url, final ImageLoaderCallback callback){
        //先从缓存中取。
        if(SoftReferenceCache.hasCache(url)){
            Drawable drawable = SoftReferenceCache.getCache(url);
            return drawable;
        }

        //回调
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                callback.imageLoaded(uid,url, (Drawable) msg.obj);
            }
        };
        //如果缓存没有，则开个新线程去读取图片。
        Thread work = new Thread(){
            @Override
            public void run() {
               Drawable drawable = loadDrawableFromURL(url);
                if(drawable!=null)
                    SoftReferenceCache.cache(url,drawable);
                Message message = handler.obtainMessage(0,drawable);
                handler.sendMessage(message);
            }
        };
        this.cachedThreadPools.execute(work);
        return null;
    }

    //加载网络图片
    public static Drawable loadDrawableFromURL(String url){
        URL _url;
        InputStream _is = null;
        try {
            _url = new URL(url);
            _is = (InputStream) _url.getContent();
            Drawable _result = Drawable.createFromStream(_is,"src");
            return _result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(_is!=null)
                try {
                    _is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }
}
