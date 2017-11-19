/*
 * Copyright (c) 2014. kugou.com
 */

package jo.dis.player.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.Toast;


/**
 * <p>Created by LeonLee on 2014/9/4 17:21.<p/>
 * <p><a href="mailto:liwenlong1@kugou.net">Email:liwenlong1@kugou.net</a></p>
 * Toast处理工具类
 * <p/>
 * <ul>
 * 显示文本的Toast
 * <li>{@link #showToast(android.content.Context, CharSequence, int)} 显示Toast</li>
 * <li>{@link #showToastLong(android.content.Context, CharSequence)} 显示长时间的Toast</li>
 * <li>{@link #showToastShort(android.content.Context, CharSequence)} 显示短时间的Toast</li>
 * </ul>
 * <ul>
 * 显示资源id的Toast
 * <li>{@link #showToast(android.content.Context, int, int)} 显示Toast</li>
 * <li>{@link #showToastLong(android.content.Context, int)} 显示长时间的Toast</li>
 * <li>{@link #showToastShort(android.content.Context, int)} 显示短时间的Toast</li>
 * </ul>
 */
public class ToastUtils {
    private ToastUtils() {
    }

    /**
     * 显示时间短的Toast
     *
     * @param context 上下文
     * @param msg     显示的内容
     */
    public static Toast showToastShort(Context context, CharSequence msg) {
        return showToast(context, msg, Toast.LENGTH_SHORT);
    }


    /**
     * 显示时间短的Toast
     *
     * @param context 上下文
     * @param resId   显示的资源ID
     */
    public static Toast showToastShort(Context context, int resId) {
        return showToast(context, resId, Toast.LENGTH_SHORT);
    }

    /**
     * 显示时间短的Toast
     *
     * @param context 上下文
     * @param msg     显示的内容
     */
    public static Toast showToastShortWithGravity(Context context, CharSequence msg, int gravity) {
        return showToast(context, msg, Toast.LENGTH_SHORT, gravity, 0, 0);
    }

    /**
     * 显示时间短的Toast
     *
     * @param context 上下文
     * @param resId   显示的资源ID
     */
    public static Toast showToastShortWithGravity(Context context, int resId, int gravity) {
        return showToast(context, resId, Toast.LENGTH_SHORT, gravity, 0, 0);
    }

    /**
     * 显示时间长的Toast
     *
     * @param context 上下文
     * @param msg     显示的内容
     */
    public static Toast showToastLong(Context context, CharSequence msg) {
        return showToast(context, msg, Toast.LENGTH_LONG);
    }

    /**
     * 显示时间长的Toast
     *
     * @param context 上下文
     * @param resId   显示的资源ID
     */
    public static Toast showToastLong(Context context, int resId) {
        return showToast(context, resId, Toast.LENGTH_LONG);
    }

    /**
     * 显示时间长的Toast
     *
     * @param context 上下文
     * @param msg     显示的内容
     * @param gravity 要显示的文本重心
     */
    public static Toast showToastLongWithGravity(Context context, CharSequence msg, int gravity) {
        return showToast(context, msg, Toast.LENGTH_LONG, gravity, 0, 0);
    }

    /**
     * 显示时间长的Toast
     *
     * @param context 上下文
     * @param resId   显示的资源ID
     * @param gravity 要显示的文本重心
     */
    public static Toast showToastLongWithGravity(Context context, int resId, int gravity) {
        return showToast(context, resId, Toast.LENGTH_LONG, gravity, 0, 0);
    }

    /**
     * 显示时间短的Toast
     *
     * @param context 上下文
     * @param resId   显示的资源ID
     * @param gravity 要显示的文本重心
     * @param bgResId 背景颜色
     */
    public static Toast showToastShortWithGravity(Context context, int resId, int gravity, int bgResId) {
        return showToast(context, resId, Toast.LENGTH_SHORT, gravity, 0, 0, bgResId);
    }

    /**
     * 显示时间短的Toast
     *
     * @param context 上下文
     * @param text    显示的文字
     * @param gravity 要显示的文本重心
     * @param bgResId 背景颜色
     */
    public static Toast showToastShortWithGravity(Context context, final CharSequence text, int gravity, int bgResId) {
        return showToast(context, text, Toast.LENGTH_SHORT, gravity, 0, 0, bgResId);
    }


    /**
     * 显示Toast，自动处理主线程与非主线程
     *
     * @param context  上下文
     * @param resId    显示的资源ID
     * @param duration 时长
     */
    public static Toast showToast(Context context, int resId, int duration) {
        if (context == null) {
            return null;
        }
        String text = context.getString(resId);
        return showToast(context, text, duration);
    }

    /**
     * 显示Toast，自动处理主线程与非主线程
     *
     * @param context  上下文
     * @param text     要显示的toast内容
     * @param duration 时长
     */
    public static Toast showToast(final Context context, final CharSequence text, final int duration) {
        return showToast(context, text, duration, Gravity.NO_GRAVITY, 0, 0);
    }

    /**
     * 显示Toast，自动处理主线程与非主线程
     *
     * @param context  上下文
     * @param resId    显示的资源ID
     * @param duration 时长
     * @param gravity  要显示的文本重心
     * @param offsetX
     * @param offsetY
     */
    private static Toast showToast(final Context context, final int resId, final int duration, final int gravity, final int offsetX, final int offsetY) {
        String text = context.getString(resId);
        return showToast(context, text, duration, gravity, offsetX, offsetY);
    }

    /**
     * 显示Toast，自动处理主线程与非主线程
     *
     * @param context  上下文
     * @param resId    显示的资源ID
     * @param duration 时长
     * @param gravity  要显示的文本重心
     * @param offsetX
     * @param offsetY
     * @param bgResId  背景颜色
     */
    private static Toast showToast(final Context context, final int resId, final int duration, final int gravity, final int offsetX, final int offsetY, final int bgResId) {
        String text = context.getString(resId);
        return showToast(context, text, duration, gravity, offsetX, offsetY, bgResId);
    }


    /**
     * 显示Toast，自动处理主线程与非主线程
     *
     * @param context  上下文
     * @param text     要显示的toast内容
     * @param duration 时长
     * @param gravity  要显示的文本重心
     * @param offsetX
     * @param offsetY
     */
    private static Toast showToast(final Context context, final CharSequence text, final int duration, final int gravity, final int offsetX, final int offsetY) {
        return showToast(context, text, duration, gravity, offsetX, offsetY, 0);
    }

    /**
     * 显示Toast，自动处理主线程与非主线程
     *
     * @param context  上下文
     * @param text     要显示的toast内容
     * @param duration 时长
     * @param gravity  要显示的文本重心
     * @param offsetX
     * @param offsetY
     */
    private static Toast showToast(final Context context, final CharSequence text, final int duration, final int gravity, final int offsetX, final int offsetY, final int bgResId) {
        if (context == null) {
            return null;
        }
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Toast toast = Toast.makeText(context, text, duration);
            if (gravity != Gravity.NO_GRAVITY) {
                toast.setGravity(gravity, offsetX, offsetY);
            }
            if (bgResId != 0) {
                toast.getView().setBackgroundResource(bgResId);
            }
            toast.show();
            return toast;
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(context, text, duration);
                    if (gravity != Gravity.NO_GRAVITY) {
                        toast.setGravity(gravity, offsetX, offsetY);
                    }
                    if (bgResId != 0) {
                        toast.getView().setBackgroundResource(bgResId);
                    }
                    toast.show();
                }
            });
        }
        return null;
    }

//    public static void showCustomToast(Context context, int imageResId, int backgroundResId, CharSequence toastDescription, int gravity, int duration){
//        Toast mToast = new Toast(context);
//        LayoutInflater inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View layout = inflater.inflate(R.layout.fx3_common_icon_toast_layout, null);
//        LinearLayout fxLlBg = (LinearLayout)layout.findViewById(R.id.fx_ll_bg);
//        if(backgroundResId < 0){
//            backgroundResId = 0;
//        }
//        fxLlBg.setBackgroundResource(backgroundResId);
//        TextView toastDescriptionTextView = (TextView) layout.findViewById(R.id.comm_progress_description);
//        toastDescriptionTextView.setText(toastDescription);
//        ImageView toastImage = (ImageView) layout.findViewById(R.id.comm_progress_loading);
//        if (imageResId <= 0) {
//            toastImage.setVisibility(View.GONE);
//        } else {
//            toastImage.setVisibility(View.VISIBLE);
//            toastImage.setImageResource(imageResId);
//        }
//        mToast.setView(layout);
//        mToast.setDuration(duration);
//        mToast.setGravity(gravity, 0, 0);
//        mToast.show();
//    }
}
