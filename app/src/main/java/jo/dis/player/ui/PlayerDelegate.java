package jo.dis.player.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import jo.dis.player.BuildConfig;
import jo.dis.player.JoApplication;
import jo.dis.player.R;
import jo.dis.player.cache.ProxyCacheException;
import jo.dis.player.cache.event.ProxyCacheExceptionEvent;
import jo.dis.player.cache.file.Md5FileNameGenerator;
import jo.dis.player.entity.OpusInfo;
import jo.dis.player.helper.ClickViewDelayHelper;
import jo.dis.player.helper.LocalCachePathHelper;
import jo.dis.player.helper.ReadPlayUrlCallback;
import jo.dis.player.utils.NetworkUtils;
import jo.dis.player.utils.StorageUtils;
import jo.dis.player.utils.SystemUtils;
import jo.dis.player.utils.ToastUtils;
import jo.dis.player.widget.FixedTextureView;

/**
 *
 * Created by Dis on 2017/11/18.
 */

public abstract class PlayerDelegate extends Delegate implements
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnVideoSizeChangedListener,
        TextureView.SurfaceTextureListener,
        View.OnClickListener {

    private static final String TAG = MainActivity.class.getName();

    private static final int MSG_QUIT = 0x001;
    private static final int MSG_DOPAUSE = 0x002;
    private static final int MSG_DOCLEAN = 0x003;
    private static final int MSG_IS_PLAY = 0x004;

    private int mScreenWidth;
    private int mScreenHeight;

    FixedTextureView mPlayView;
    View mPlayBtn;
    View mPreLoadingView;
    View mLoadingView;
    View mNetworkErrorView;

    private Handler mMediaPlayerHandler;

    private MediaPlayerThread mMediaPlayerThread;
    private MediaPlayer mMediaPlayer;

    private Surface mSurface;

    private String finalPlayUrl;
    private int seekPosition = 0;
    boolean mFromPowerOn;//从电源打开亮屏的状态中恢复

    private boolean mIsSurfaceCreated;
    private volatile boolean mIsPageVisible = false;//页面是否可见,如果离开则取消加载弹幕,不再显示弹窗引导

    boolean mManualPause;//用户是否主动点击了暂停
    private boolean mPlayStarted;//是否已经开始加载视频命令
    boolean mSizeSet;

    BroadcastReceiver mScreenOffReceiver;
    boolean mIsScreenOffRegistered;

    private OpusInfo mInfo;

    public PlayerDelegate(Activity activity, View view, OpusInfo opusInfo) {
        super(activity);
        mInfo = opusInfo;
        mScreenWidth = SystemUtils.getScreenWidth(getActivity());
        mScreenHeight = SystemUtils.getScreenHeight(getActivity());

        mPlayView = (FixedTextureView) view.findViewById(R.id.jo_play_view);
        mPlayView.setSurfaceTextureListener(this);

        mNetworkErrorView = view.findViewById(R.id.fx_sv_player_no_network_view);
        mNetworkErrorView.setOnClickListener(this);

        mPlayBtn = view.findViewById(R.id.jo_player_play_btn);
        mLoadingView = view.findViewById(R.id.jo_loading);
        mPreLoadingView = view.findViewById(R.id.jo_pre_loading);

        mMediaPlayerThread = new MediaPlayerThread();
        mMediaPlayerThread.start();
    }

    public void setOpusInfo(OpusInfo opusInfo){
        mInfo = opusInfo;
//        loadFirstFrameImage();
        if (mIsPageVisible && mIsSurfaceCreated) {
            playVideo();
            mPlayStarted = true;
        }
    }

    public void playVideo() {
        Log.d(TAG, "playVideo ");
        if (!mIsPageVisible) {
            return;
        }

        if (mInfo==null || TextUtils.isEmpty(mInfo.getLink())) {
            ToastUtils.showToastShort(JoApplication.getInstance(), "播放出错");
            Log.e(TAG, "OpusInfo is null, or url is null");
            return;
        }

        registerScreenOffBroadcastReceiver();
        if (!NetworkUtils.isNetworkConected(JoApplication.getInstance())) {
            mPreLoadingView.setVisibility(View.GONE);
            mLoadingView.setVisibility(View.GONE);
            mNetworkErrorView.setVisibility(View.VISIBLE);
            return;
        }
        // 播放消息为空 或者是播放的地址为空
        if (mInfo == null || TextUtils.isEmpty(mInfo.getLink())) {
            mPreLoadingView.setVisibility(View.GONE);
            mLoadingView.setVisibility(View.GONE);
            ToastUtils.showToastShort(JoApplication.getInstance(), "播放地址为空，请退出重试");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(mMediaPlayer == null){
                        mMediaPlayer = new MediaPlayer();
                    }

                    mMediaPlayer.reset();
                    mMediaPlayer.setOnInfoListener(PlayerDelegate.this);
                    mMediaPlayer.setOnVideoSizeChangedListener(PlayerDelegate.this);
                    mMediaPlayer.setOnBufferingUpdateListener(PlayerDelegate.this);
                    mMediaPlayer.setOnPreparedListener(PlayerDelegate.this);
                    mMediaPlayer.setOnErrorListener(PlayerDelegate.this);
                    mMediaPlayer.setOnCompletionListener(PlayerDelegate.this);
                    mMediaPlayer.setSurface(mSurface);

                    startProxy(mInfo.getLink());
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, String.format("playVideo exception=%s ", e.getMessage()));
                }
            }
        }).start();
    }

    /**
     * 启动代理
     */
    public void startProxy(String opusUrl) {
        String playUrl;
        // 是否有本地缓存
        playUrl = JoApplication.getProxy(JoApplication.getInstance()).getLocalCachePath(opusUrl);
        // 如果无本地缓存，进入代理
        if (playUrl == null) {
            if (LocalCachePathHelper.INSTANCE.isReadLocalCachePath(JoApplication.getInstance())) {
                playUrl = JoApplication.getProxy(JoApplication.getInstance()).getProxyUrl(opusUrl);
            } else {
                playUrl = opusUrl;
            }
        }
        mReadPlayUrlCallback.callBack(playUrl);
    }

    private ReadPlayUrlCallback mReadPlayUrlCallback = new ReadPlayUrlCallback() {
        @Override
        public void callBack(String playUrl) {
            try {
                finalPlayUrl = playUrl;

                if (!TextUtils.isEmpty(finalPlayUrl)) {
                    mMediaPlayer.setDataSource(finalPlayUrl);
                    mMediaPlayer.prepareAsync();
                } else {
                    ToastUtils.showToastShort(JoApplication.getInstance(), "播放地址为空，请退出重试");
                }
                // debugPlayUrl();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, String.format("playVideo exception=%s ", e.getMessage()));
            }
        }
    };

    //注册这个广播修复从电源键进入后台没有走surfaceDestroyed(),然后返回播放页时没有走surfaceCreated()方法,导致无法继续播放视频的问题
    private void registerScreenOffBroadcastReceiver() {
        if (mScreenOffReceiver == null) {
            mScreenOffReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(final Context context, final Intent intent) {
                    String action = intent.getAction();
                    if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                        mFromPowerOn = true;
                        mPlayStarted = true;
                        Log.d(TAG, "onReceive screen off");
                    }
                }
            };
        }
        if (!mIsScreenOffRegistered) {
            IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
            getActivity().registerReceiver(mScreenOffReceiver, intentFilter);
            mIsScreenOffRegistered = true;
        }
    }

    private void unregisterScreenOffReceiver() {
        if (mScreenOffReceiver != null && mIsScreenOffRegistered) {
            getActivity().unregisterReceiver(mScreenOffReceiver);
            mIsScreenOffRegistered = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fx_sv_player_no_network_view:
                if (ClickViewDelayHelper.enableClick()) {
                    mNetworkErrorView.setVisibility(View.GONE);
                    mLoadingView.setVisibility(View.VISIBLE);
                    playVideo();
                }
                break;
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        mSurface = new Surface(surfaceTexture);
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onSurfaceTextureAvailable getUserVisibleHint()=" + mIsPageVisible);
        }
        mIsSurfaceCreated = true;
        if (mIsPageVisible) {
            if (!mPlayStarted) {
                playVideo();
                mPlayStarted = true;
            }
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        mSurface.release();
        mSurface = null;

        Log.d(TAG, "onSurfaceTextureDestroyed ");
        mIsSurfaceCreated = false;
        mSizeSet = false;

        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (mMediaPlayer != null) {
            checkCacheAndPlay();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        Log.e(TAG, String.format("MediaPlayer.onError,what=%d,extra=%d", what, extra));
        if (!NetworkUtils.isNetworkConected(JoApplication.getInstance())) {
            mPreLoadingView.setVisibility(View.GONE);
            mLoadingView.setVisibility(View.GONE);
            mNetworkErrorView.setVisibility(View.VISIBLE);
        }
        return true;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            mLoadingView.setVisibility(View.VISIBLE);
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            mLoadingView.setVisibility(View.INVISIBLE);
        }
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onPrepared getUserVisibleHint=" + mIsPageVisible + " seekPosition=" + seekPosition);
        }
        if (!mIsPageVisible) {
            return;
        }

        mPlayBtn.setVisibility(View.GONE);
        mPreLoadingView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.INVISIBLE);
        mNetworkErrorView.setVisibility(View.GONE);

        if (seekPosition > 0) {//从后台回到前台
            mediaPlayer.seekTo(seekPosition);
            if (mManualPause) {
                //用户点击播放按钮时才播放视频和歌词
                mPlayBtn.setVisibility(View.VISIBLE);
            } else {//自动播放
                mediaPlayer.start();
                Log.d(TAG, "onPrepared1 postDelayed");
            }
        } else {//首次进入页面播放
            mediaPlayer.start();
            Log.d(TAG, "onPrepared2 postDelayed");
        }
//        mPlayView.setBackgroundColor(Color.TRANSPARENT);

        onPrepared();
    }

    public abstract void onPrepared();

    @Override
    public void onVideoSizeChanged(MediaPlayer mediaPlayer, int width, int height) {
        if (width == 0 || height == 0) {
            mSizeSet = false;
            return;
        }
        mSizeSet = true;
        Log.d("whr", "onVideoSizeChanged:" + width + "," + height);
        setVideoFixSize(width, height);
    }

    //设置播放视频的SurfaceView按视频比例相应缩放直到短边铺满屏幕,长边超出屏幕部分需要设置负的margin值,以保证视频中央区域不被裁剪.
    public void setVideoFixSize(final int videoWidth, final int videoHeight) {
        mPlayView.post(new Runnable() {
            @Override
            public void run() {
                // 根据根目录的大小来计算
                int rootW = mScreenWidth;
                int rootH = mScreenHeight;

                int newVideoHeight;
                int newVideoWidth;
                int marginLeft = 0;
                int marginTop = 0;
                if (rootH * videoWidth > rootW * videoHeight) {//视频高度需要放大, 宽度需要相应扩大且左右剪切
                    newVideoHeight = rootH;
                    newVideoWidth = (int) (videoWidth / (float) videoHeight * newVideoHeight);
                    marginLeft = (rootW - newVideoWidth) / 2;//为负值
                } else {//视频宽度需要放大, 高度需要相应扩大且上下剪切
                    newVideoWidth = rootW;
                    newVideoHeight = (int) (videoHeight / (float) videoWidth * newVideoWidth);
                    marginTop = (rootH - newVideoHeight) / 2;//为负值
                }
                Log.d("whr", String.format("setVideoFixSize,videoWidth=%d, videoHeight=%d,rootW=%d,rootH=%d,newVideoWidth=%d,newVideoHeight=%d", videoWidth, videoHeight, rootW, rootH, newVideoWidth, newVideoHeight));
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mPlayView.getLayoutParams();

                if (marginLeft != 0) {
                    layoutParams.width = newVideoWidth;
                    layoutParams.height = newVideoHeight;
                    layoutParams.leftMargin = marginLeft;
                    if (BuildConfig.DEBUG) {
                        Log.d("whr", String.format("setVideoFixSize,videoHeight=%d, videoWidth=%d,marginLeft=%d",
                                newVideoHeight, newVideoWidth, marginLeft));
                    }
                } else {
                    layoutParams.topMargin = marginTop;
                    layoutParams.width = newVideoWidth;
                    layoutParams.height = newVideoHeight;
                    if (BuildConfig.DEBUG) {
                        Log.d("whr", String.format("setVideoFixSize,videoWidth=%d, videoHeight=%d,marginTop=%d",
                                newVideoWidth, newVideoHeight, marginTop));
                    }
                }

                mPlayView.setFixedSize(layoutParams.width, layoutParams.height);
                mPlayView.setLayoutParams(layoutParams);
            }
        });
    }

    /**
     * 检测是否有缓存，有缓存则播放缓存，没有就直接播放
     */
    public void checkCacheAndPlay() {
        if (!TextUtils.isEmpty(mInfo.getLink())) {
            // 判断是否有本地缓存，如果有本地缓存 则重新加载一次视频，导向本地缓存路径
            String localCachePath = null;
            localCachePath = JoApplication.getProxy(JoApplication.getInstance()).getLocalCachePath(mInfo.getLink());
            if (!TextUtils.isEmpty(localCachePath) && !localCachePath.equals(finalPlayUrl)) {
                // 切换播放地址
                finalPlayUrl = localCachePath;
                playVideo();
            } else if (mMediaPlayer != null) {
                mMediaPlayer.start();
            }
        } else if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    /**
     * MediaPlayer操作线程
     */
    private class MediaPlayerThread extends Thread {

        private boolean isRunning = false;
        private boolean isPlaying;

        public MediaPlayerThread() {
            setName("LoveShoPlayFragment-MediaPlayerThread");
        }

        @Override
        public void run() {
            Looper.prepare();
            isRunning = true;
            mMediaPlayerHandler = new Handler(mMediaPlayerHandlerCallback);
            Looper.loop();
            isRunning = false;
            mMediaPlayerHandler = null;
        }

        public boolean isRunning() {
            return mMediaPlayerThread != null && isRunning;
        }

        public void setPlaying(boolean playing) {
            isPlaying = playing;
        }

        /**
         * 阻塞线程
         */
        public void quit() {
            if (isRunning()) {
                isRunning = false;

                Message msg = new Message();
                msg.what = MSG_QUIT;
                mMediaPlayerHandlerCallback.handleMessage(msg);
                this.interrupt();
                try {
                    this.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * MediaPlayer线程处理
     */
    private Handler.Callback mMediaPlayerHandlerCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_QUIT:
                    if (mMediaPlayerHandler != null) {
                        mMediaPlayerHandler.getLooper().quit();
                    }
                    break;
                case MSG_DOPAUSE:
                    try {
                        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                            mMediaPlayer.pause();
                            Log.d(TAG, "onPause getCurrentPosition");
                            seekPosition = mMediaPlayer.getCurrentPosition();
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                    break;
                case MSG_DOCLEAN:
                    try {
                        if (mMediaPlayer != null) {
                            mMediaPlayer.pause();
                            Log.d(TAG, "doClean getCurrentPosition");
                            seekPosition = mMediaPlayer.getCurrentPosition();
                            mMediaPlayer.stop();
                            mMediaPlayer = null;
                        }
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                    break;
                case MSG_IS_PLAY:
                    if (mMediaPlayer != null && mMediaPlayerThread != null) {
                        boolean isPlaying = false;
                        try {
                            isPlaying = mMediaPlayer.isPlaying();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mMediaPlayerThread.setPlaying(isPlaying);
                    }
                    break;
            }
            return false;
        }
    };

    public void onEventMainThread(ProxyCacheExceptionEvent event) {
        if (event.getThrowable() instanceof TimeoutException) { // 获取本地缓存失败
            LocalCachePathHelper.INSTANCE.setReadLocalExceptionCount(JoApplication.getInstance()); // 记录代理失败
            if (!TextUtils.isEmpty(mInfo.getLink())) {
                finalPlayUrl = mInfo.getLink();
                mReadPlayUrlCallback.callBack(mInfo.getLink());
            }
        } else if (event.getThrowable() instanceof ProxyCacheException) { // 写入缓存错误
            if (!TextUtils.isEmpty(mInfo.getLink())) {
                finalPlayUrl = mInfo.getLink();
                mReadPlayUrlCallback.callBack(mInfo.getLink());
            }
        }
    }

    public void setIsPageVisible(boolean isPageVisible){
        mIsPageVisible = isPageVisible;
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsPageVisible = true;

        if (!mManualPause) {
            if (seekPosition >= 0 && mMediaPlayer != null && !mMediaPlayer.isPlaying()) {//从onPause回到这里
                mPlayBtn.setVisibility(View.INVISIBLE);
                checkCacheAndPlay();
            }
        }

        if (mFromPowerOn) {
            mFromPowerOn = false;
            playVideo();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Message msg = new Message();
        msg.what = MSG_DOPAUSE;
        mMediaPlayerHandlerCallback.handleMessage(msg);

        if (mManualPause) {//手动暂停后再到后台或页面被部分遮挡
            seekPosition = mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : 0;
            Log.d(TAG, "onPause2 getCurrentPosition");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mPlayStarted = false;
    }

    public void doClean() {
        mPlayStarted = false;
        Message msg = new Message();
        msg.what = MSG_DOCLEAN;
        mMediaPlayerHandlerCallback.handleMessage(msg);

        unregisterScreenOffReceiver();
    }

    public void release() {
        if (mMediaPlayerThread != null) {
            mMediaPlayerThread.quit();
            mMediaPlayerThread = null;
        }
    }

    /**
     * 播放地址调试
     */
    private void debugPlayUrl() {
        //mMediaPlayer.setDataSource("http://fx.v.kugou.com/G051/M00/0C/15/04YBAFZoAViAWyKjAD90m5KXnso908.mp4");//有问题的视频
        //mMediaPlayer.setDataSource(mInfo.opusUrl);
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "dataSource url: " + finalPlayUrl);
            Log.d(TAG, "playVideo url: " + mInfo.getLink());
            if (finalPlayUrl.startsWith("http://127.0.0.1")) {
                ToastUtils.showToastShort(JoApplication.getInstance(), "本手机支持视频缓存!");
                File individualCacheDirectory = StorageUtils.getIndividualCacheDirectory(JoApplication.getInstance(), "video-cache");
                Md5FileNameGenerator generator = new Md5FileNameGenerator();
                String filename = generator.generate(mInfo.getLink());
                File file = new File(individualCacheDirectory + "/" + filename);
                if (file.exists()) {
                    ToastUtils.showToastLong(JoApplication.getInstance(), "视频缓存已存在:" + file.getAbsolutePath());
                    Log.d(TAG, "视频缓存已存在:" + file.getAbsoluteFile());
                } else {
                    ToastUtils.showToastLong(JoApplication.getInstance(), "视频缓存不存在:" + file.getAbsolutePath());
                    Log.d(TAG, "视频缓存不存在:" + file.getAbsolutePath());
                }
            } else {
                ToastUtils.showToastShort(JoApplication.getInstance(), "本手机不支持视频缓存!");
            }
        }
        //mMediaPlayer.setDataSource(mInfo.opusUrl);
        //mMediaPlayer.setDataSource("http://storagefx1.v.kugou.com/M01/03/20/CgEy11YV0C-Acpy1AMTxEIJzMPU051.mp4");
    }
}
