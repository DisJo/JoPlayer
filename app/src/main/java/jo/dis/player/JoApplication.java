package jo.dis.player;

import android.app.Application;
import android.content.Context;

import jo.dis.player.cache.HttpProxyCacheServer;

/**
 * Created by Dis on 2017/11/18.
 */

public class JoApplication extends Application {

    private HttpProxyCacheServer proxy;//MV播放下载缓存代理服务器

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getInstance() {
        return mContext;
    }

    public static HttpProxyCacheServer getProxy(Context context) {
        JoApplication app = (JoApplication) context.getApplicationContext();
        if (app.proxy == null) {
            app.proxy = app.newProxy();
        }
        return app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
//                .maxCacheFilesCount(5)//最多缓存5个视频文件
                .maxCacheSize(64 * 1024 * 1024)
                .build();
    }
}
