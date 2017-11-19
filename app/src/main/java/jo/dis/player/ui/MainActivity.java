package jo.dis.player.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import jo.dis.player.R;
import jo.dis.player.entity.OpusInfo;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getName();

    private PlayerDelegate mPlayerDelegate;
    private OpusInfo mInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        View view = findViewById(R.id.player_root);

        mInfo = new OpusInfo();
//        mInfo.setLink("http://fxvideo.bssdlbig.kugou.com/201711200112/9b7ec1e6af90f2b857e8f00312fc8a65/1addaaa68c415ccba3c1ba1978485f58.mp4");
        mInfo.setLink("http://fx.v.kugou.com/G051/M00/0C/15/04YBAFZoAViAWyKjAD90m5KXnso908.mp4");


        mPlayerDelegate = new PlayerDelegate(this, view, mInfo) {
            @Override
            public void onPrepared() {
                if (isPause) {//
                    onPause();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume ");
        if(mPlayerDelegate != null){
            mPlayerDelegate.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause ");
        if(mPlayerDelegate != null){
            mPlayerDelegate.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop ");
        if(mPlayerDelegate != null){
            mPlayerDelegate.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPlayerDelegate != null){
            mPlayerDelegate.setIsPageVisible(false);
        }
        Log.d(TAG, "onDestroy doClean");
        if(mPlayerDelegate != null){
            mPlayerDelegate.doClean();
        }

        if(mPlayerDelegate != null){
            mPlayerDelegate.release();
        }
    }
}
