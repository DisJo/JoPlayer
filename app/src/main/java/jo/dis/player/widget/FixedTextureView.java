package jo.dis.player.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * Created by Dis on 2017/11/18.
 */

public class FixedTextureView extends TextureView {

    private int fixedWidth;
    private int fixedHeight;

    public FixedTextureView(Context context) {
        super(context);
    }

    public FixedTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixedTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setFixedSize(int width, int height) {
        fixedHeight = height;
        fixedWidth = width;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (fixedWidth == 0 || fixedHeight == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            setMeasuredDimension(fixedWidth, fixedHeight);
        }
    }
}
