package com.sen.banner;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private ViewPager viewpager;
    private ArrayList<ImageView> imgs;
    private TextView tv_title;
    private String[] strs;
    private LinearLayout ll_points;
    private int preRedpointIndex = 0;// 记录前一个红点的位置
    private boolean isRunning = true;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2_main);
        MyToggleButton toggleButton = (MyToggleButton) findViewById(R.id.toggle);
        toggleButton.setOnToggleButtonStateChangeListener(new MyToggleButton.OnToggleButtonStateChangeListener() {
            @Override
            public void onStateChanged(boolean state) {

                if (toast == null) {
                    toast = Toast.makeText(MainActivity.this, "" + state, Toast.LENGTH_LONG);
                }
                toast.setText("" + state);
                toast.show();
            }
        });
        initView();
        startThread();
    }

    private void startThread() {
        new Thread() {
            public void run() {
                while (isRunning) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // 让ViewPager切换到下一页
                            viewpager.setCurrentItem(viewpager.getCurrentItem() + 1);

                        }
                    });

                }
            }

            ;
        }.start();
    }

    @Override
    protected void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }

    private void initView() {
        ll_points = (LinearLayout) findViewById(R.id.ll_points);
        tv_title = (TextView) findViewById(R.id.tv_title);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        // 准备数据
        initData();
        // 设置Adapter
        viewpager.setAdapter(new MyAdapter());

        // 监听ViewPager的滑动
        viewpager.setOnPageChangeListener(new MyListener());
        // 默认设置标题为第一条
        tv_title.setText(strs[0]);
        // 把第一个点设置为红色
        ll_points.getChildAt(0).setBackgroundResource(R.drawable.point_red);
        // 把ViewPager设置到最大值的一半，实现向左无限循环
        viewpager.setCurrentItem(Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2 % imgs.size()));
//		viewpager.setCurrentItem(Integer.MAX_VALUE/2-3);
    }

    private void initData() {
        imgs = new ArrayList<ImageView>();
        // 把图片资源转换成ImageView
        int[] imgids = new int[]{R.drawable.a,
                R.drawable.b,
                R.drawable.c,
                R.drawable.d,
                R.drawable.e
        };
        strs = new String[]{
                "黑曼巴科比",
                "小皇帝詹姆斯",
                "狼王加内特",
                "石斧邓肯",
                "答案艾弗森"

        };
        for (int i = 0; i < imgids.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(imgids[i]);
            imgs.add(imageView);

            // 创建点指示器
            ImageView point = new ImageView(this);
            point.setBackgroundResource(R.drawable.point_normal);
            LayoutParams params = new LayoutParams(10, 10);
            params.leftMargin = 5;// 设置左边距
            point.setLayoutParams(params);
            // 把点添加到容器中
            ll_points.addView(point);
        }
    }

    class MyListener implements OnPageChangeListener {
        // viewpager 滑动时 调用
        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {

        }

        // 当滑动到某一页时调用
        @Override
        public void onPageSelected(int position) {
            position = position % imgs.size();
            // 把前一个点变成白色
            ll_points.getChildAt(preRedpointIndex).setBackgroundResource(R.drawable.point_normal);
            tv_title.setText(strs[position]);
            // 当滑动到某一页时，把当前位置对应的点变成红色
            ll_points.getChildAt(position).setBackgroundResource(R.drawable.point_red);
            preRedpointIndex = position;
        }

        // 滑动状态变化时 调用
        @Override
        public void onPageScrollStateChanged(int state) {

        }

    }

    class MyAdapter extends PagerAdapter {
        // 返回ViewPager的条目数目
        @Override
        public int getCount() {
            return Integer.MAX_VALUE;//imgs.size();// 把size设置成非常大的值，实现向右无限循环
        }

        // 判断instantiateItem方法返回的Object和当前展示的条目控件的对应关系
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        // 当滑动时，如果左或右，超出了相邻的位置后，会删除那个条目
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            System.out.println("destroyItem:" + position);
            viewpager.removeView((View) object);
        }

        // 类型Listview中的getView 方法
        // 预加载机制，setAdapter方法调用，默认显示0位置，预加载1位置
        // 滑动到1，加载2位置， 当前ViewPager中有3个条目，当前位置加上左右各一个
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            System.out.println("instantiateItem:" + position);
            // 针对position取模，如果position大于图片的数量，从头显示
            position = position % imgs.size();
            // 根据位置获取ImageView
            ImageView imageView = imgs.get(position);
            // 把控件添加到ViewPager中
            viewpager.addView(imageView);
//			container.addView(imageView);
            return imageView;
        }

    }

}
