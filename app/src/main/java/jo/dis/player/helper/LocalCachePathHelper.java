package jo.dis.player.helper;

import android.content.Context;

import jo.dis.player.Constant;
import jo.dis.player.utils.SharedPreferencesUtil;


/**
 * 是否读取本地播放缓存帮助类
 * Created by diszhou on 2016/7/14.
 */
public enum LocalCachePathHelper {

    /**
     * 单例
     */
    INSTANCE;

    /**
     * 限定数
     */
    private final int EXCEPTION_COUNT = 3;

    /**
     * 应用周期内读取缓存失败时标记
     */
    private boolean evenFail;

    /**
     * 超过三次不再读取本地缓存
     *
     * @param context
     * @return
     */
    public boolean isReadLocalCachePath(Context context) {
        if (evenFail) { // 如果应用周期内已经标记过就返回
            return false;
        }

        int exceptionCount = (int) SharedPreferencesUtil.get(context, Constant.KEY_CACHE_IS_READ_LOCAL_PATH, 0);
        return exceptionCount <= EXCEPTION_COUNT;
    }

    /**
     * 记录读取缓存失败数
     *
     * @param context
     */
    public void setReadLocalExceptionCount(Context context) {
        if (evenFail) {
            return;
        }
        evenFail = true;
        Integer exceptionCount = (Integer) SharedPreferencesUtil.get(context, Constant.KEY_CACHE_IS_READ_LOCAL_PATH, 0);
        SharedPreferencesUtil.put(context, Constant.KEY_CACHE_IS_READ_LOCAL_PATH, ++exceptionCount);
    }
}
