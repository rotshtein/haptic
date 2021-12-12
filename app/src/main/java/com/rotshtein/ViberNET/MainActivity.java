package com.rotshtein.ViberNET;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class MainActivity extends AppCompatActivity {

    private static final int MAX_UDP_DATAGRAM_LEN = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ServerThread serverThread = new ServerThread();
        serverThread.start();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private class ServerThread extends Thread {
        @Override
        public void run() {
            //Log.i("VibraNET", "Setup start");
            byte[] msg = new byte[MAX_UDP_DATAGRAM_LEN];
            DatagramSocket sock = null;
            try {
                sock = new DatagramSocket(new InetSocketAddress("0.0.0.0", 12345));
            } catch (SocketException e) {
                //e.printStackTrace();
                //Log.e("VibraNET", "no socket", e);
                return;
            }
            DatagramPacket pkt = new DatagramPacket(msg, msg.length);
            Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);

            Log.i("VibraNET", "Setup complete");

            while (true) {
                //Log.i("VibraNET", "iter");
                try {
                    sock.receive(pkt);
                    //Log.i("VibraNET", "before vibrate");
                    //byte[] time_length_array = new byte[4];
                    //System.arraycopy(msg,0, time_length_array,0,time_length_array.length);
                    //String s = new String(time_length_array, StandardCharsets.US_ASCII);
                    int time_array = (short)(msg[0] + ((msg[1] << 8) & 0xFF00));
                    v.vibrate(time_array);
                    //Log.i("VibraNET", "after vibrate");
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }

            //Log.i("VibraNET", "end loop");
        }
    }
}