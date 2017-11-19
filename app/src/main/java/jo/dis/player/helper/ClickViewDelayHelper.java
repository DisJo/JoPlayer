package jo.dis.player.helper;

import android.os.SystemClock;
import android.view.View;

import java.util.HashMap;

/**
 * 防止快速点击view而有多个界面出现的问题
 */
public class ClickViewDelayHelper {

    private static HashMap<String, Long> clickCtrMap = new HashMap<String, Long>();

    private static long getLastClickTime(String key) {
        if (clickCtrMap.containsKey(key)) {
            return clickCtrMap.get(key);
        } else {
            clickCtrMap.put(key, 0L);
            return 0;
        }
    }

    private static void setLastClickTime(String key, Long time) {
        clickCtrMap.put(key, time);
    }

    private static long lastClickTime;
    // 确保同时只能打开一个播放页
    private static boolean preparePlayClicked;

    /**
     * 是否允许点击
     * 1秒内限制快速点击
     */
    public static boolean enableClick() {
        long time = SystemClock.elapsedRealtime();
        if (time - lastClickTime < 1000) {
            return false;
        }
        lastClickTime = time;
        return true;
    }

    /**
     * 是否允许点击
     * 800毫秒内限制快速点击
     */
    public static boolean enableClick2() {
        long time = SystemClock.elapsedRealtime();
        if (time - lastClickTime < 800) {
            return false;
        }
        lastClickTime = time;
        return true;
    }

    /**
     * 是否允许点击
     * 500毫秒内限制快速点击
     */
    public static boolean enableClick3() {
        long time = SystemClock.elapsedRealtime();
        if (time - lastClickTime < 500) {
            return false;
        }
        lastClickTime = time;
        return true;
    }

    /**
     * 是否允许点击
     * 2秒内限制快速点击
     */
    public static boolean enableClick4() {
        long time = SystemClock.elapsedRealtime();
        if (time - lastClickTime < 2000) {
            return false;
        }
        lastClickTime = time;
        return true;
    }

    public static boolean enableClick4(View view) {
        String key = view.hashCode() + "";
        long time = SystemClock.elapsedRealtime();
        if (time - getLastClickTime(key) < 2000) {
            return false;
        }
        setLastClickTime(key, time);
        return true;
    }

    /**
     * 是否允许点击
     * 2.5秒内限制快速点击
     */
    public static boolean enableClick5() {
        long time = SystemClock.elapsedRealtime();
        if (time - lastClickTime < 2500) {
            return false;
        }
        lastClickTime = time;
        return true;
    }

    /**
     * 是否允许点击
     * 0.3秒内限制快速点击
     */
    public static boolean enableClick6() {
        long time = SystemClock.elapsedRealtime();
        if (time - lastClickTime < 300) {
            return false;
        }
        lastClickTime = time;
        return true;
    }

    public static void setPreparePlayClicked(boolean preparePlayClicked) {
        ClickViewDelayHelper.preparePlayClicked = preparePlayClicked;
    }

    public static boolean isPreparePlayClicked() {
        return preparePlayClicked;
    }

    /**
     * 手动设置上次点击时间，用于处理特殊情况下的禁止点击
     * @param time
     */
    public static void updateClickTime(long time){
        lastClickTime = time;
    }
}
