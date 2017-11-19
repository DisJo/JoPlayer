package jo.dis.player.ui;

import android.app.Activity;
import android.os.Message;
import android.view.View;

import de.greenrobot.event.EventBus;

/**
 * 轻量级的界面UI框架
 */
public abstract class Delegate {

    protected Activity mActivity;

    protected View mView;

    protected volatile boolean isAlive = false;
    protected volatile boolean isPause = false;
    // 使用场景多在Fragment中
    protected boolean isHidden = false;

    public Delegate(Activity activity) {
        this.mActivity = activity;
        EventBus.getDefault().register(this);

        isAlive = true;
    }

    /**
     * 加入View
     */
    public void attachView(View view) {
        this.mView = view;
    }

    /**
     * 分离View
     */
    public void detachView() {
        this.mView = null;
    }

    public void onPause() {
        isPause = true;
    }

    public void onResume() {
        isPause = false;
    }

    public void onStop() {

    }

    public void onStart() {

    }

    public void onDestroy() {
        isAlive = false;
        EventBus.getDefault().unregister(this);
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
        onHiddenChanged(hidden);
    }

    public void onHiddenChanged(boolean hidden) {
    }

    public void onTrimMemory(int level) {

    }

    /**
     * 生成一个空的消息
     */
    public static Message obtainMessage(int what) {
        Message msg = Message.obtain();
        msg.what = what;
        return msg;
    }

    /**
     * 生成一个空的消息
     */
    public static Message obtainMessage(int what, int arg1, int arg2) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        return msg;
    }

    /**
     * 生成一个空的消息
     */
    public static Message obtainMessage(int what, int arg1, int arg2, Object object) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.arg1 = arg1;
        msg.arg2 = arg2;
        msg.obj = object;
        return msg;
    }

    /**
     * 生成一个消息
     */
    public static Message obtainMessage(int what, Object object) {
        Message msg = Message.obtain();
        msg.what = what;
        msg.obj = object;
        return msg;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public boolean isHostInvalid() {
        return null == mActivity || mActivity.isFinishing();
    }
}
