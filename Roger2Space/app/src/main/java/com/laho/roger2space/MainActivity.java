package com.laho.roger2space;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Random;


public class MainActivity extends FragmentActivity {
    static String URL_LIST = "http://johnleger.free.fr/spaceApp/";
    static String URL_GET_LIST = "liste";

    View mScreen_1;
    View mScreen_2;

    Button mNavBarBut_1;
    Button mPlayButton;
    Button mButDragNDrop;
    Float mButDragNDrop_x;
    Float mButDragNDrop_y;

    View butPlayAnim1;
    View butPlayAnim2;
    View mRootView;

    MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mRootView =this.findViewById(android.R.id.content);


        mPlayButton =(Button) mRootView.findViewById(R.id.playbutton);

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDownloadManagerAvailable(getApplicationContext()))
                downloadSoundFromTheWeb("https://sites.google.com/site/sakethrajan/Internationale-Hindi.mp3?attredirects=0");
                MediaPlayer mPlayer = new MediaPlayer();

                Uri myUri = Uri.parse(Environment.DIRECTORY_DOWNLOADS+"music.mp3");
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mPlayer.setDataSource(getApplicationContext(), myUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    mPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mPlayer.start();

            }
        });







        // screens
        mScreen_1 = findViewById(R.id.screen_1);
        mScreen_2 = findViewById(R.id.screen_2);


        // first but play
        findViewById(R.id.butPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScreen_1.setVisibility(View.INVISIBLE);
                mScreen_2.setVisibility(View.VISIBLE);

                startRandomMusic();
            }
        });
        butPlayAnim1 = findViewById(R.id.butPlayAnim1);
        butPlayAnim2 = findViewById(R.id.butPlayAnim2);
        Animation logoMoveAnimation = AnimationUtils.loadAnimation(this, R.anim.animation_scale);
        butPlayAnim1.startAnimation(logoMoveAnimation);



        // Nav bar interactions
        mNavBarBut_1 = (Button)findViewById(R.id.nav_bar_but_1);
        mNavBarBut_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScreen_1.setVisibility(View.VISIBLE);
                mScreen_2.setVisibility(View.INVISIBLE);

                mMediaPlayer.stop();
            }
        });


        // button drag n drop
        mButDragNDrop = (Button)findViewById(R.id.butDragNDrop);
        mButDragNDrop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent me) {
                /*if (me.getAction() == MotionEvent.ACTION_DOWN){
                    mButDragNDrop_x = me.getX();
                    mButDragNDrop_y = me.getY();
                    Log.i("DRAG N DROP", "Action Down " + mButDragNDrop_x + "," + mButDragNDrop_y);
                }else if (me.getAction() == MotionEvent.ACTION_MOVE  ){
                    AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(v.getWidth(), v.getHeight(),(int)(me.getRawX() - (v.getWidth() / 2)), (int)(me.getRawY() - (v.getHeight())));
                    v.setLayoutParams(params);
                }*/
                return true;
            }
        });
    }

    private void downloadSoundFromTheWeb(String url){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Some descrition");
        request.setTitle("music.mp3");
// in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "name-of-the-file.ext");

// get download service and enqueue file
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    /**
     * @param context used to check the device version and DownloadManager information
     * @return true if the download manager is available
     */
    public static boolean isDownloadManagerAvailable(Context context) {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setClassName("com.android.providers.downloads.ui", "com.android.providers.downloads.ui.DownloadList");
            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        } catch (Exception e) {
            return false;
        }
    }









    public void startRandomMusic() {
        mMediaPlayer = new MediaPlayer();
        new HttpAsyncTask().execute(URL_LIST + URL_GET_LIST);



    }


    public void startThisStream(String music){
        try {
            String musicToPlay = URL_LIST+music;
            Log.e("Start this music ",musicToPlay);
            mMediaPlayer.setDataSource(musicToPlay);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(prepareListener);
        mMediaPlayer.prepareAsync();
    }

    private MediaPlayer.OnPreparedListener prepareListener = new MediaPlayer.OnPreparedListener(){
        public void onPrepared(MediaPlayer mp){
             if(!mp.isPlaying())
                mp.start();
        }
    };






    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result +=  line + "\n";

        inputStream.close();
        return result;

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();

            String[] separated = result.split("\n");
            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(separated.length-1);
            String sMusic = separated[randomInt];
            Log.e("Start Music : ", sMusic);
            startThisStream(sMusic);
        }
    }
}
