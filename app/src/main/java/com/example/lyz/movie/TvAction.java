package com.example.lyz.movie;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.example.lyz.movie.R.id.tvaction_player;


public class TvAction extends AppCompatActivity {
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private Map<String, String> tvSource;
    private OrientationEventListener mOrientationListener;









    public boolean onOptionsItemSelected(MenuItem item) {//返回按钮
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }//返回按钮

    public void tvSourceInit() {
        tvSource = new HashMap<String, String>();
        try {
            test(tvSource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void test(Map<String, String> tvSource) throws IOException {//read json file
        myJson json =new myJson("data.json");//传文件进去
        JSONObject lan;
        JSONArray array = json.work("root");//指定json根路径
        for (int i = 0; i < array.length(); i++) {
            try {
                lan = array.getJSONObject(i);
                tvSource.put(lan.getString("name"),lan.getString("url"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initExo(String WebPosition) {
        /**
         * 创建播放器
         */
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();// 得到默认合适的带宽
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);// 创建跟踪的工厂
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);// 创建跟踪器

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        playerView.setPlayer(player);// 绑定player
        /**
         * 准备player
         */
        // 生成通过其加载媒体数据的DataSource实例
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(TvAction.this, "ExoPlayer"), bandwidthMeter);
        MediaSource mediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(WebPosition));// 创建要播放的媒体的MediaSource
        player.prepare(mediaSource);// 准备播放器的MediaSource
        player.setPlayWhenReady(true);// 当准备完毕后直接播放
    }

    private void initView() {
        playerView = (PlayerView) findViewById(tvaction_player);
    }

    @Override
    protected void onDestroy() {
        mOrientationListener.disable();
        player.release();
        super.onDestroy();
    }

}
