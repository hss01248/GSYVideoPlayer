package com.example.gsyvideoplayer;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.view.View;
import android.widget.ImageView;

import com.example.gsyvideoplayer.listener.SampleListener;
import com.shuyu.gsyvideoplayer.GSYVideoPlayer;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.CommonUtil;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.NormalGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

/**
 * Created by Administrator on 2017/5/3 0003.
 */

public class WebPlayerManager {
    public boolean isPlay;
    public boolean isPause;
    public boolean isSamll;
    public OrientationUtils orientationUtils;
    public NormalGSYVideoPlayer webPlayer;

    public void onBackPressed(){
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo();
        }
    }

    public void onPause(){
        isPause = true;
    }
    public void onResume(){
        isPause = false;
    }
    public void onDestory(){
        GSYVideoPlayer.releaseAllVideos();
        if (orientationUtils != null)
            orientationUtils.releaseListener();
    }

    public void onConfigurationChanged(Configuration newConfig,Activity activity){
        //如果旋转了就全屏
        if (isPlay && !isPause && !isSamll) {
            if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
                if (!webPlayer.isIfCurrentIsFullscreen()) {
                    webPlayer.startWindowFullscreen(activity, true, true);
                }
            } else {
                //新版本isIfCurrentIsFullscreen的标志位内部提前设置了，所以不会和手动点击冲突
                if (webPlayer.isIfCurrentIsFullscreen()) {
                    StandardGSYVideoPlayer.backFromWindowFull(activity);
                }
                if (orientationUtils != null) {
                    orientationUtils.setEnable(true);
                }
            }
        }
    }


    public void onScrollChange( int scrollY,Activity activity) {
        if (!webPlayer.isIfCurrentIsFullscreen() && scrollY >= 0 && isPlay) {
            if (scrollY > webPlayer.getHeight()) {
                //如果是小窗口就不需要处理
                if (!isSamll) {
                    isSamll = true;
                    int size = CommonUtil.dip2px(activity, 150);
                    webPlayer.showSmallVideo(new Point(size, size), true, true);
                    orientationUtils.setEnable(false);
                }
            } else {
                if (isSamll) {
                    isSamll = false;
                    orientationUtils.setEnable(true);
                    //必须
                    webPlayer.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            webPlayer.hideSmallVideo();
                        }
                    }, 50);
                }
            }
            webPlayer.setTranslationY((scrollY <= webPlayer.getHeight()) ? -scrollY : -webPlayer.getHeight());
        }
    }

    public void initWebPlayer(final NormalGSYVideoPlayer webPlayer, final Activity activity,
                              String url, String title, String thumbUrl) {
        //WebPlayerManager playerManager = new WebPlayerManager();
        this.webPlayer = webPlayer;

        webPlayer.setUp(url, false, null, title);

        //增加封面
        ImageView imageView = new ImageView(activity);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //imageView.setImageURI(Uri.parse(thumbUrl));
        imageView.setImageResource(R.drawable.abc_switch_thumb_material);
        webPlayer.setThumbImageView(imageView);

        //增加title
        webPlayer.getTitleTextView().setVisibility(View.GONE);
        webPlayer.getTitleTextView().setText(title);
        webPlayer.getBackButton().setVisibility(View.GONE);

        //外部辅助的旋转，帮助全屏
        final OrientationUtils orientationUtils = new OrientationUtils(activity, webPlayer);
        this.orientationUtils = orientationUtils;
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);

        webPlayer.setIsTouchWiget(true);
        //关闭自动旋转
        webPlayer.setRotateViewAuto(false);
        webPlayer.setLockLand(false);
        webPlayer.setShowFullAnimation(false);
        webPlayer.setNeedLockFull(true);
        //detailPlayer.setOpenPreView(true);
        webPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接横屏
                orientationUtils.resolveByClick();

                //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                webPlayer.startWindowFullscreen(activity, true, true);
            }
        });

        webPlayer.setStandardVideoAllCallBack(new SampleListener() {
            @Override
            public void onPrepared(String url, Object... objects) {
                super.onPrepared(url, objects);
                //开始播放了才能旋转和全屏
                orientationUtils.setEnable(true);
                isPlay = true;
            }

            @Override
            public void onAutoComplete(String url, Object... objects) {
                super.onAutoComplete(url, objects);
            }

            @Override
            public void onClickStartError(String url, Object... objects) {
                super.onClickStartError(url, objects);
            }

            @Override
            public void onQuitFullscreen(String url, Object... objects) {
                super.onQuitFullscreen(url, objects);
                if (orientationUtils != null) {
                    orientationUtils.backToProtVideo();
                }
            }
        });

        webPlayer.setLockClickListener(new LockClickListener() {
            @Override
            public void onClick(View view, boolean lock) {
                if (orientationUtils != null) {
                    //配合下方的onConfigurationChanged
                    orientationUtils.setEnable(!lock);
                }
            }
        });

    }





}

