package jo.dis.player.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

/**
 * 常用系统数据或处理工具类
 * <p/>
 * <ul>
 * <li>{@link #getMemoryClass(android.content.Context)} 获取当前系统每个app的内存等级，即最大使用内存</li>
 * <li>{@link #myPid()} 获取进程ID</li>
 * <li>{@link #getCurProcessName(android.content.Context)} 获取进程名</li>
 * <li>{@link #isSDCardAvailable()} 检测储存卡是否可用</li>
 * <li>{@link #getSysModel()} 获取系统型号</li>
 * <li>{@link #getSysVersion()} 获取系统版本号</li>
 * <li>{@link #getVersionCode(android.content.Context)} 获取当前系统版本号</li>
 * <li>{@link #getVersionName(android.content.Context)} 获取当前系统版本名</li>
 * <li>{@link #isCurAppTop(android.content.Context)} 判断当前程序是否前台进程</li>
 * <li>{@link #getAlbumPath(android.content.Context)} 获取系统相册路径, 耗时操作</li>
 * <li>{@link #newInstanceMenu(android.content.Context)} 初始化一个空{@link android.view.Menu}</li>
 * <li>{@link #checkPermissions(android.content.Context, String)} 检查权限</li>
 * <li>{@link #getDeviceIMSI(android.content.Context)} 获取IMSI值，如果获取失败，则返回空串</li>
 * <li>{@link #getDeviceId(android.content.Context)} 获取IMSI值，如果获取失败，则返回空串</li>
 * </ul>
 */
public class SystemUtils {

    private SystemUtils() {
    }

    /**
     * 获取当前系统每个app的内存等级，即最大使用内存
     *
     * @param context
     * @return
     */
    public static int getMemoryClass(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return activityManager.getMemoryClass();
    }

    /**
     * 获取进程ID
     *
     * @return
     */
    public static int myPid() {
        return android.os.Process.myPid();
    }

    /**
     * 获取进程名
     *
     * @param context
     * @return
     */
    public static String getCurProcessName(Context context) {
        if (context != null) {
            ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
                if (appProcess.pid == myPid()) {
                    return appProcess.processName;
                }
            }
        }
        return "";
    }

    /**
     * 检测储存卡是否可用
     *
     * @return
     */
    public static boolean isSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取系统型号
     *
     * @return
     */
    public static String getSysModel() {
        return android.os.Build.MODEL;
    }

    /**
     * 获取系统版本号
     *
     * @return
     */
    public static String getSysVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取当前系统版本号
     *
     * @return
     */
    public static int getVersionCode(Context context) {
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            return info.versionCode;
        } catch (NameNotFoundException e) {
            return 1;
        }
    }

    /**
     * 获取当前系统版本名
     *
     * @return
     */
    public static String getVersionName(Context context) {
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            return info.versionName;
        } catch (NameNotFoundException e) {
            return "";
        }
    }

    /**
     * 判断当前程序是否前台进程
     *
     * @param context
     * @return
     */
    public static boolean isCurAppTop(Context context) {
        if (context == null) {
            return false;
        }
        String curPackageName = context.getPackageName();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            RunningTaskInfo info = list.get(0);
            String topPackageName = info.topActivity.getPackageName();
            String basePackageName = info.baseActivity.getPackageName();
            if (topPackageName.equals(curPackageName) && basePackageName.equals(curPackageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取屏幕密度
     *
     * @param activity
     * @return
     */
    public static int getDpi(Context activity) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = activity.getResources().getDisplayMetrics();
        int densityDpi = dm.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
        return densityDpi;
    }

    public static float getScale(Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        return scale;
    }

//    /**
//     * 获取系统相册路径, 耗时操作
//     *
//     * @param context
//     * @return
//     */
//    public static String getAlbumPath(Context context) {
//        if (context == null) {
//            return null;
//        }
//        try {
//            ContentResolver cr = context.getContentResolver();
//            ContentValues values = new ContentValues();
//            values.put(Images.Media.TITLE, "title");
//            values.put(Images.Media.DESCRIPTION, "description");
//            values.put(Images.Media.MIME_TYPE, "image/jpeg");
//            Uri url = cr.insert(Images.Media.EXTERNAL_CONTENT_URI, values);
//            // 查询系统相册数据
//            Cursor cursor = Images.Media.query(cr, url, new String[]{Images.Media.DATA});
//            String albumPath = null;
//            if (cursor != null && cursor.moveToFirst()) {
//                int column_index = cursor.getColumnIndexOrThrow(Images.Media.DATA);
//                albumPath = cursor.getString(column_index);
//                try {
//                    cursor.close();
//                } catch (Exception e) {
//                }
//            }
//            cr.delete(url, null, null);
//            if (TextUtils.isEmpty(albumPath)) {
//                return null;
//            }
//
//            albumPath = FileUtils.getParentPath(albumPath);
//            File albumDir = new File(albumPath);
//            if (!albumDir.exists()) {
//                albumDir.mkdirs();
//            }
//            return albumPath;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    /**
     * 初始化一个空{@link android.view.Menu}
     *
     * @param context
     * @return
     */
    public static Menu newInstanceMenu(Context context) {
        try {
            Class menuBuilder = Class.forName("com.android.internal.view.menu.MenuBuilder");
            Constructor constructor = menuBuilder.getConstructor(Context.class);
            return (Menu) constructor.newInstance(context);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断Intent是否有效
     *
     * @param context
     * @param intent
     * @return true 有效
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 检查权限
     *
     * @param context    上下文
     * @param permission 需要检查的权限
     */
    public static boolean checkPermissions(Context context, String permission) {
        PackageManager localPackageManager = context.getPackageManager();
        int flag = localPackageManager.checkPermission(permission, context.getPackageName());
        return flag == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 获取IMSI值，如果获取失败，则返回空串;
     * 需要权限 android.permission.READ_PHONE_STATE
     *
     * @param context 上下文
     */
    public static String getDeviceIMSI(Context context) {
        String imsi = "";
        if (context != null && checkPermissions(context, "android.permission.READ_PHONE_STATE")) {
            TelephonyManager tm = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            try {
                imsi = tm.getSubscriberId();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (imsi == null) {
                imsi = "";
            }

        }
        return imsi;
    }

    /**
     * 获取手机号码,如果获取失败，则返回空串;
     * 需要权限 android.permission.READ_PHONE_STATE
     *
     * @param context 上下文
     */
    public static String getPhoneNumber(Context context) {
        String number = "";
        if (context != null && checkPermissions(context, "android.permission.READ_PHONE_STATE")) {
            TelephonyManager tm = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            try {
                number = tm.getLine1Number();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (number == null) {
                number = "";
            }
        }
        return number;
    }

    /**
     * 获取设备号(IMEI),如果获取失败，则返回空串;
     * 需要权限 android.permission.READ_PHONE_STATE
     *
     * @param context 上下文
     */
    public static String getDeviceId(Context context) {
        String deviceId = "";
        if (context != null && checkPermissions(context, "android.permission.READ_PHONE_STATE")) {
            TelephonyManager tm = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            try {
                deviceId = tm.getDeviceId();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (deviceId == null) {
                deviceId = "";
            }
        }
        return deviceId;
    }

    /**
     * 获取屏幕宽
     *
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int getDisplayWidth(Context context) {
        if (context == null) {
            return 0;
        }
        int width = 0;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        try {
            Class<?> cls = Display.class;
            Class<?>[] parameterTypes = {Point.class};
            Point parameter = new Point();
            Method method = cls.getMethod("getSize", parameterTypes);
            method.invoke(display, parameter);
            width = parameter.x;
        } catch (Exception e) {
            width = display.getWidth();
        }
        return width;
    }

    /**
     * 获取屏幕高
     *
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int getDisplayHeight(Context context) {
        if (context == null) {
            return 0;
        }
        int height = 0;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        try {
            Class<?> cls = Display.class;
            Class<?>[] parameterTypes = {Point.class};
            Point parameter = new Point();
            Method method = cls.getMethod("getSize", parameterTypes);
            method.invoke(display, parameter);
            height = parameter.y;
        } catch (Exception e) {
            height = display.getHeight();
        }
        return height;
    }

    /**
     * 获取屏幕高(包括底部虚拟按键)
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        int height = 0;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealMetrics(displayMetrics);
            } else {
                display.getMetrics(displayMetrics);
            }
            height = displayMetrics.heightPixels;
        } catch (Exception e) {
            height = display.getHeight();
        }
        return height;
    }

    /**
     * 如果>0则有虚拟导航栏
     *
     * @param context
     * @return
     */
    public static int getNavibarHeight(Context context) {
        return getScreenHeight(context) - getDisplayHeight(context);
    }

    /**
     * 获取屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 获取系统状态栏bar条高
     *
     * @param activity
     * @return
     */
    public static int getStatusBarHeight(Activity activity) {
        if (activity == null) {
            return 0;
        }
        int statusHeight = 0;
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        statusHeight = frame.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass
                        .getField("status_bar_height")
                        .get(localObject)
                        .toString());
                statusHeight = activity.getResources()
                        .getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight2(Context context) {
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object obj = clazz.newInstance();
            Field field = clazz.getField("status_bar_height");
            int id = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SystemUtils.dip2px(context, 25);
    }

    /**
     * 获取应用展示高度
     */
    public static int getAppHeight(Activity activity) {
        return getDisplayHeight(activity) - getStatusBarHeight(activity);
    }

    /**
     * dp转成px
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px转成dp
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 显示软键盘
     *
     * @param context
     * @param edit
     */
    public static void showSoftInput(Context context, View edit) {
        if (context == null || edit == null) {
            return;
        }
        InputMethodManager inputManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null && inputManager.isActive(edit)) {
            inputManager.showSoftInput(edit, 0);
        }
    }

    /**
     * 隐藏软键盘
     */
    public static void hideSoftInput(Window window) {
        try {
            window.setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        } catch (Exception e) {
        }
    }

    /**
     * 隐藏软键盘
     */
    public static void hideSoftInput(Activity activity) {
        if (activity == null) {
            return;
        }
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null && inputManager.isActive()) {
            View focusView = activity.getCurrentFocus();
            if (focusView != null) {
                inputManager.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
        }
    }

    /**
     * 隐藏软键盘
     *
     * @param context
     * @param edit
     */
    public static void hideSoftInput(Context context, View edit) {
        try {
            InputMethodManager inputManager = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(edit.getWindowToken(), 0);
        } catch (Exception e) {
        }
    }

    /**
     * 读取RAW文件内容
     *
     * @param context
     * @param resid
     * @param encoding
     * @return
     */
//    public static String getRawFileContent(Context context, int resid, String encoding) {
//        InputStream is = null;
//        try {
//            is = context.getResources().openRawResource(resid);
//        } catch (Exception e) {
//            // Resource NotFoundException
//        }
//        if (is != null) {
//            ByteArrayBuffer bab = new ByteArrayBuffer(1024);
//            int read;
//            try {
//                while ((read = is.read()) != -1) {
//                    bab.append(read);
//                }
//                return EncodingUtils.getString(bab.toByteArray(), encoding);
//            } catch (UnsupportedEncodingException e) {
//            } catch (IOException e) {
//            } finally {
//                try {
//                    is.close();
//                } catch (IOException e) {
//                }
//            }
//        }
//        return "";
//    }

    /**
     * 获取系统当前时间，单位秒
     *
     * @return
     */
    public static long currentSystemTimeSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取设备任意时刻的UUID
     *
     * @return
     */
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * 获取 虚拟按键的高度
     *
     * @param context
     * @return
     */
    public static int getBottomStatusHeight(Context context) {
        int totalHeight = getScreenHeight(context);
        int contentHeight = getDisplayHeight(context);
        return totalHeight  - contentHeight;
    }


}


