package com.example.gsyvideoplayer;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;

import com.example.gsyvideoplayer.view.ScrollWebView;
import com.shuyu.gsyvideoplayer.video.NormalGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by shuyu on 2016/12/26.
 */

public class WebDetailActivity extends AppCompatActivity {

    @BindView(R.id.scroll_webView)
    ScrollWebView webView;
    @BindView(R.id.web_player)
    NormalGSYVideoPlayer webPlayer;
    @BindView(R.id.web_top_layout)
    NestedScrollView webTopLayout;
   // @BindView(R.id.web_top_layout_video)
   // RelativeLayout webTopLayoutVideo;

   // private boolean isPlay;
    //private boolean isPause;
    //private boolean isSamll;
    WebPlayerManager manager;

   // private OrientationUtils orientationUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_detail);
        ButterKnife.bind(this);
        String url = "http://baobab.wdjcdn.com/14564977406580.mp4";
        String thumbUrl = "https://pic2.zhimg.com/d951be75390e7828791f25e044071c1d_b.png";
        manager= new WebPlayerManager();
        manager.initWebPlayer(webPlayer,this,url,"title",thumbUrl);


        //String url = "https://d131x7vzzf85jg.cloudfront.net/upload/documents/paper/b2/61/00/00/20160420_115018_b544.mp4";


        //resolveNormalVideoUI();




        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.loadUrl("https://www.baidu.com");

        webTopLayout.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                manager.onScrollChange(scrollY,WebDetailActivity.this);

                /*if (!webPlayer.isIfCurrentIsFullscreen() && scrollY >= 0 && manager.isPlay) {
                    if (scrollY > webPlayer.getHeight()) {
                        //如果是小窗口就不需要处理
                        if (!manager.isSamll) {
                            manager.isSamll = true;
                            int size = CommonUtil.dip2px(WebDetailActivity.this, 150);
                            webPlayer.showSmallVideo(new Point(size, size), true, true);
                            manager.orientationUtils.setEnable(false);
                        }
                    } else {
                        if (manager.isSamll) {
                            manager.isSamll = false;
                            manager.orientationUtils.setEnable(true);
                            //必须
                            webTopLayoutVideo.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    webPlayer.hideSmallVideo();
                                }
                            }, 50);
                        }
                    }
                    webTopLayoutVideo.setTranslationY((scrollY <= webTopLayoutVideo.getHeight()) ? -scrollY : -webTopLayoutVideo.getHeight());
                }*/
            }
        });

    }



    @Override
    public void onBackPressed() {

        /*if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }*/
        manager.onBackPressed();

        if (StandardGSYVideoPlayer.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        super.onPause();
        manager.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       manager.onDestory();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        manager.onConfigurationChanged(newConfig,this);
    }


    private void resolveNormalVideoUI() {
        //增加title
        webPlayer.getTitleTextView().setVisibility(View.GONE);
        webPlayer.getTitleTextView().setText("测试视频");
        webPlayer.getBackButton().setVisibility(View.GONE);
    }
}
