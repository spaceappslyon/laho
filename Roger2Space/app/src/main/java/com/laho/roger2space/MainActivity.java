package com.laho.roger2space;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
    View mScreen_3_profile;

    View mNavBarBut_1;
    View mNavBarBut_1_border;
    View mNavBarBut_2;
    View mNavBarBut_2_border;
    Button mPlayButton;
    Button mButDragNDrop;
    Float mButDragNDrop_x;
    Float mButDragNDrop_y;

    View butPlayAnim1;
    View butPlayAnim2;
    View mRootView;

    RelativeLayout mLayout_content;

    MediaPlayer mMediaPlayer;


    String profile_urlPicture;
    ImageView mImgProfilePicture;
    String profile_name;


    ImageView[] mButChoix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            final Activity that = this;
            this.mRootView = this.findViewById(android.R.id.content);

            mLayout_content = (RelativeLayout) findViewById(R.id.layout_content);


            // get googleplus profile infos
                // picture
            profile_urlPicture = getIntent().getStringExtra("pictureUrl");
            mImgProfilePicture = (ImageView)findViewById(R.id.profilePicture);
            String modifiedUrl = profile_urlPicture.substring(0, profile_urlPicture.length() - 2) + mImgProfilePicture.getMeasuredWidth();
            new LoadProfileImage(mImgProfilePicture).execute(modifiedUrl);
                // name
            profile_name = getIntent().getStringExtra("name");
            ((TextView)findViewById(R.id.profileName)).setText(profile_name);



            mPlayButton = (Button) mRootView.findViewById(R.id.playbutton);

            mPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isDownloadManagerAvailable(getApplicationContext()))
                        downloadSoundFromTheWeb("https://sites.google.com/site/sakethrajan/Internationale-Hindi.mp3?attredirects=0");
                    MediaPlayer mPlayer = new MediaPlayer();

                    Uri myUri = Uri.parse(Environment.DIRECTORY_DOWNLOADS + "music.mp3");
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
            mScreen_3_profile = findViewById(R.id.screen_3_profile);


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

            final Animation logoMoveAnimation2 = AnimationUtils.loadAnimation(this, R.anim.animation_scale);
            final Handler handler = new Handler();
            final Runnable r = new Runnable() {
                public void run() {
                    butPlayAnim2.startAnimation(logoMoveAnimation2);
                }
            };
            handler.postDelayed(r, 1200);


            // Nav bar interactions
            mNavBarBut_1 =  findViewById(R.id.nav_bar_but_1);
            mNavBarBut_2 =  findViewById(R.id.nav_bar_but_2);
            mNavBarBut_1_border =  findViewById(R.id.nav_bar_but_1_border);
            mNavBarBut_2_border =  findViewById(R.id.nav_bar_but_2_border);
            mNavBarBut_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mScreen_1.setVisibility(View.VISIBLE);
                        mNavBarBut_1_border.setVisibility(View.VISIBLE);
                        mScreen_2.setVisibility(View.INVISIBLE);
                        mScreen_3_profile.setVisibility(View.INVISIBLE);
                        mNavBarBut_2_border.setVisibility(View.INVISIBLE);

                        if(mMediaPlayer != null && mMediaPlayer.isPlaying())
                            mMediaPlayer.stop();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            mNavBarBut_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mScreen_3_profile.setVisibility(View.VISIBLE);
                        mNavBarBut_2_border.setVisibility(View.VISIBLE);
                        mScreen_1.setVisibility(View.INVISIBLE);
                        mScreen_2.setVisibility(View.INVISIBLE);
                        mNavBarBut_1_border.setVisibility(View.INVISIBLE);

                        if(mMediaPlayer != null && mMediaPlayer.isPlaying())
                            mMediaPlayer.stop();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });


            // button drag n drop
            mButDragNDrop = (Button) findViewById(R.id.butDragNDrop);
            mButDragNDrop.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View v, MotionEvent me) {
                    try {
                        if (me.getAction() == MotionEvent.ACTION_DOWN) {
                            mButDragNDrop_x = me.getX();
                            mButDragNDrop_y = me.getY();
                            Log.i("DRAG N DROP", "Action Down " + mButDragNDrop_x + "," + mButDragNDrop_y);
                        } else if (me.getAction() == MotionEvent.ACTION_MOVE) {
                            v.setX(((int) (me.getRawX() - (v.getWidth() / 2))));
                            v.setY(((int) (me.getRawY() - (v.getHeight()))));
                        } else if (me.getAction() == MotionEvent.ACTION_UP) {
                            Animation anim = AnimationUtils.loadAnimation(that, R.anim.translate_anim);
                            v.startAnimation(anim);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });





            // Get choice buttons
            mButChoix = new ImageView[8];
            for(int i=0; i<8; i++){
                mButChoix[i] = (ImageView)findViewById(getResources().getIdentifier("choix_"+(i+1), "id", getPackageName()));
                final int num = i+1;
                mButChoix[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getBaseContext(), "HEYY you did the "+(num)+" choice !", Toast.LENGTH_LONG).show();
                        startRandomMusic();
                    }
                });
            }

        }catch (Exception e){
            e.printStackTrace();
        }
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

            String[] separated = result.split("\n");
            Random randomGenerator = new Random();
            int randomInt = randomGenerator.nextInt(separated.length);
            String sMusic = separated[randomInt];
            Log.e("Start Music : ", sMusic);
            startThisStream(sMusic);

            mButDragNDrop.setText(sMusic);
        }
    }





    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }



    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap icon = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                icon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return icon;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
