package com.sen.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MyToggleButton extends View {

    private Bitmap backgroundBitmap;
    private Bitmap slideButtonBitmap;
    private boolean currentState;
    private int currentX;
    private boolean isTouching;
    private OnToggleButtonStateChangeListener mListener;

    // 配置样式时 调用
    public MyToggleButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // 在布局文件中使用控件时 调用
    public MyToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 从布局文件中获取控件的自定义属性值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MytoggleButton);
        int backgroundResValue = typedArray.getResourceId(R.styleable.MytoggleButton_backgroundRes, 0);
        int slideButtonResValue = typedArray.getResourceId(R.styleable.MytoggleButton_slideButtonRes, 0);
        currentState = typedArray.getBoolean(R.styleable.MytoggleButton_state, true);
//        String namespace = "http://schemas.android.com/apk/res/com.sen.banner";
//        int backgroundResValue = attrs.getAttributeResourceValue(namespace, "backgroundRes", 0);
//        int slideButtonResValue = attrs.getAttributeResourceValue(namespace, "slideButtonRes", 0);
//        currentState = attrs.getAttributeBooleanValue(namespace, "state", false);
        setBackgroundRes(backgroundResValue);
        setSlideButtonRes(slideButtonResValue);
    }

    // 在代码中new对象时 调用
    public MyToggleButton(Context context) {
        super(context);
    }

    // 测量自己的宽高，把背景图片的宽高设置为自己的宽高
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 把背景图片的宽高设置为自己的宽高
        setMeasuredDimension(backgroundBitmap.getWidth(), backgroundBitmap.getHeight());
    }

    // 绘制自己
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制背景
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);

        // 根据手指触摸的位置绘制滑动块图片
        if (isTouching) {
            int left = currentX - slideButtonBitmap.getWidth() / 2;// 让滑动块画的中心画到手指触摸的位置
            // 限制滑动块的滑动范围
            if (left < 0) {// 如果超出左边，直接绘制到0位置
                left = 0;
            } else if (left > backgroundBitmap.getWidth() - slideButtonBitmap.getWidth()) {
                left = backgroundBitmap.getWidth() - slideButtonBitmap.getWidth();
            }
            canvas.drawBitmap(slideButtonBitmap, left, 0, null);
        } else {
            // 绘制滑动块 根据外界的设置，展示开或关
            if (!currentState) {// 关状态
                canvas.drawBitmap(slideButtonBitmap, 0, 0, null);
            } else {// 开状态
                canvas.drawBitmap(slideButtonBitmap, backgroundBitmap.getWidth() - slideButtonBitmap.getWidth(), 0, null);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouching = true;
                // 获取手指触摸时相对于自己的x位置
                currentX = (int) event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                currentX = (int) event.getX();
                break;
            case MotionEvent.ACTION_UP:
                isTouching = false;
                currentX = (int) event.getX();
                // 手指抬起时，根据手指触摸的位置与背景图片一半比较，离哪边近，滑动块就滑动到哪
//			currentState = currentX>backgroundBitmap.getWidth()/2;
                // 当状态发生变化时，调用外界传递进来的监听器的onStateChanged
                boolean tempState = currentX > backgroundBitmap.getWidth() / 2;
                if (tempState != currentState) {// 状态发生变化
                    currentState = tempState;
                    if (mListener != null) {
                        mListener.onStateChanged(currentState);
                    }
                }
                break;

            default:
                break;
        }
        // 重新绘制控件，重新调用onDraw
        invalidate();
//		postInvalidate();在子线程中调用
        return true;// 消费掉事件
    }

    // 外界设置开关背景图片资源
    public void setBackgroundRes(int backgroundRes) {
        // 把传进来的图片资源 转换成Bitmap对象
        backgroundBitmap = BitmapFactory.decodeResource(getResources(), backgroundRes);
    }

    // 外界设置滑动块图片资源
    public void setSlideButtonRes(int slideButtonRes) {
        // 把传进来的图片资源 转换成Bitmap对象
        slideButtonBitmap = BitmapFactory.decodeResource(getResources(), slideButtonRes);
    }

    // 外界设置开关状态
    public void setState(boolean state) {
        this.currentState = state;
    }

    // 对外暴露开关状态发生变化的接口
    public interface OnToggleButtonStateChangeListener {
        // 当开关状态发生变化时 调用
        void onStateChanged(boolean state);
    }

    // 让外界设置状态变化的监听器
    public void setOnToggleButtonStateChangeListener(OnToggleButtonStateChangeListener listener) {
        this.mListener = listener;
    }

}
