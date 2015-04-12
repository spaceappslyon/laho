package com.laho.roger2space;


import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
        import java.net.Socket;
import java.net.URL;
import java.util.Random;


public class SendStats {

    String hostName;
    int port;

    public SendStats(String hostname, int port) {
        this.hostName = hostname;
        this.port = port;
    }

    public void send(String music, int type){

        Socket echoSocket;
        PrintWriter out = null;
        try {
            Log.e("SendStats", "Connecting to " + hostName + ":" + port);
            echoSocket = new Socket(hostName, port);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            out.write(music+" "+type);
            Log.e("SendStats", "Done");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }


    }


    public void sendAsync(String music, int type){
        String line = music+" "+type;
        new HttpAsyncTask().execute(line);
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... line) {
            Socket echoSocket;
            PrintWriter out = null;

            try {
                Log.e("SendStats", "Connecting to " + hostName + ":" + port);
                echoSocket = new Socket(hostName, port);
                out = new PrintWriter(echoSocket.getOutputStream(), true);
                out.write(line[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    out.close();
                }
            }


            return line[0];
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.e("SendStats", "Done, "+result+" send.");
        }
    }
}
