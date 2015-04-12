package com.laho.roger2space;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.Image;
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
import android.view.ViewPropertyAnimator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.glomadrian.dashedcircularprogress.DashedCircularProgress;

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
    Button mButDragNDrop;
    TextView mNumberOfClicksView;
    int mNumberOfClicks = 154;
    DashedCircularProgress mProgressMusicBar;


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

    Handler handlerProgressBar;
    ImageView[] mButChoix;
    String[] mbuttonChoixtitle = {"","Launching Spacecraft",
                                "Space sounds",
                                "Piece of History",
                                "Space Alien",
                                "blublu4",
                                "Space alien",
                                "Unidentified sound",
                                "Radio Transmission"};

    DownloadManager mDm ;
    long mEnqueue;

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



            handlerProgressBar = new Handler();

            // screens
            mScreen_1 = findViewById(R.id.screen_1);
            mScreen_2 = findViewById(R.id.screen_2);
            mScreen_3_profile = findViewById(R.id.screen_3_profile);

            mNumberOfClicksView = (TextView) findViewById(R.id.numberOfClicks);

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
                        mNumberOfClicksView.setText("Number of analyzed sounds : "+mNumberOfClicks);
                        mNavBarBut_2_border.setVisibility(View.INVISIBLE);
                        if(mProgressMusicBar != null)
                            mProgressMusicBar.reset();

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

            mProgressMusicBar = (DashedCircularProgress) findViewById(R.id.progressMusicBar);
            mProgressMusicBar.setOnValueChangeListener(new DashedCircularProgress.OnValueChangeListener() {
                @Override
                public void onValueChange(float value) {
                    try {
                        if (value >= mProgressMusicBar.getMax() && mScreen_2.getVisibility() == View.VISIBLE)
                            startRandomMusic();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            mButDragNDrop.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(final View v, MotionEvent me) {
                    try {
                        int rawX = (int)me.getRawX();
                        int rawY = (int)me.getRawY();

                        if (me.getAction() == MotionEvent.ACTION_DOWN) {
                            mButDragNDrop_x = me.getX();
                            mButDragNDrop_y = me.getY();
                            Log.i("DRAG N DROP", "Action Down " + mButDragNDrop_x + "," + mButDragNDrop_y);
                        } else if (me.getAction() == MotionEvent.ACTION_MOVE) {


                            v.setX(((int) (rawX - (v.getWidth() / 2))));
                            v.setY(((int) (rawY - (v.getHeight()))));
                        } else if (me.getAction() == MotionEvent.ACTION_UP) {
                            if(!selectButtonFromSwipe(me.getRawX(), me.getRawY()))
                                animateButtonToInitialPlace();
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

                        onSelectChoice(num);
                    }
                });
            }





            // receiver for download

            BroadcastReceiver receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                        long downloadId = intent.getLongExtra(
                                DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                        DownloadManager.Query query = new DownloadManager.Query();
                        query.setFilterById(mEnqueue);
                        Cursor c = mDm.query(query);
                        if (c.moveToFirst()) {
                            int columnIndex = c
                                    .getColumnIndex(DownloadManager.COLUMN_STATUS);
                            if (DownloadManager.STATUS_SUCCESSFUL == c
                                    .getInt(columnIndex)) {

                                String uriString = c
                                        .getString(c
                                                .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                                Log.e("DOWNLOAD down",uriString);

                            }
                        }
                    }
                }
            };

            registerReceiver(receiver, new IntentFilter(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

private void animateButtonToInitialPlace(){
    ViewPropertyAnimator anim =   mButDragNDrop.animate()
            .translationX(0)
            .translationY(0)
            .setDuration(200);
}
   private void onSelectChoice(int num){
       Toast.makeText(getBaseContext(), "HEYY you chose "+mbuttonChoixtitle[num] + "!", Toast.LENGTH_SHORT).show();
       final String music = mButDragNDrop.getText().toString();

        animateButtonToInitialPlace();
       // random new music
       startRandomMusic();

        mNumberOfClicks++;
       //send music to stats!

       // send music to stats!
       SendStats a = new SendStats("195.154.15.21", 3000);
       a.sendAsync(music, num);// title, choice


       // download
       if(num == 4){
           downloadThisSound(music);
           /*Intent sendIntent = new Intent(Intent.ACTION_SEND);
           sendIntent.setClassName("com.android.mms", "com.android.mms.ui.ComposeMessageActivity");
           sendIntent.putExtra("sms_body", "Hey, listen this music");
           sendIntent.putExtra("address", send_to);
           sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/Test.vcf"));
           sendIntent.setType("text/x-vcard");
           startActivity(sendIntent);*/
       }
   }


    private boolean selectButtonFromSwipe(float rawX, float rawY) {
        int choixImage = 0;
        for(ImageView choix : mButChoix){
            View parent = (View)choix.getParent();
            int x,xPlusWidth,y,yPlusWidth;
            x = (int) choix.getX();
            xPlusWidth = x+choix.getWidth();
            y = (int) choix.getY()+(int)parent.getY();
            yPlusWidth = y + choix.getHeight();

            if(rawX > x && rawX < xPlusWidth && rawY > y && rawY < yPlusWidth){
              onSelectChoice(choixImage+1);
                return true;
            }
            choixImage++;
        }

    return false;
    }




    public void startRandomMusic() {
        mProgressMusicBar.reset();

        if(handlerProgressBar != null){
            handlerProgressBar.removeCallbacksAndMessages(null);
        }

        if(mMediaPlayer != null){
            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
            }
        }


        new HttpAsyncTask().execute(URL_LIST + URL_GET_LIST);
    }


    public void startThisStream(String music){
        try {
            String musicToPlay = URL_LIST+music;
            Log.e("Start this music ",musicToPlay);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(musicToPlay);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnPreparedListener(prepareListener);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private MediaPlayer.OnPreparedListener prepareListener = new MediaPlayer.OnPreparedListener(){
        public void onPrepared(MediaPlayer mp){
            try {
                if (!mp.isPlaying()) {
                    mp.start();
                    mProgressMusicBar.reset();
                    mProgressMusicBar.setValue(10000);
/*
                 final Runnable r = new Runnable() {
                     public void run() {
                         int progress = mProgressMusicBar.getV
                         progress ++;
                         mProgressMusicBar.setValue(progress);
                         if(mScreen_2.getVisibility() == View.VISIBLE) {
                             if (progress < mProgressMusicBar.getMax()) {
                                 handlerProgressBar.postDelayed(this, 1);
                             } else {
                                 startRandomMusic();
                             }
                         }
                     }
                 };

                 handlerProgressBar.postDelayed(r, 0);*/
                }
            }catch (Exception e){
                e.printStackTrace();
            }
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



    private class LoadProfileImage extends AsyncTask<String, Bitmap, Bitmap> {
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
            mProgressMusicBar.setIconBitmap( cutCircle(result) );
        }
    }


    public Bitmap cutCircle(Bitmap bitmapimg){
        Bitmap output = Bitmap.createBitmap(bitmapimg.getWidth(),
                bitmapimg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmapimg.getWidth(),
                bitmapimg.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmapimg.getWidth() / 2,
                bitmapimg.getHeight() / 2, bitmapimg.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmapimg, rect, rect, paint);
        return output;
    }



    public String downloadThisSound(String music){
        String filepath;
        filepath = "";

        String url = URL_LIST+music;
        try {
            mDm= (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(
                    Uri.parse(url));
            mEnqueue = mDm.enqueue(request);


        }catch (Exception e){
            e.printStackTrace();
        }


        return filepath;
    }
}
