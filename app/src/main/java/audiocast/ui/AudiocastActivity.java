package audiocast.ui;


/**
 * @author (C) E/11/174 & E/11/311
 */

import java.net.InetAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;
import java.net.InetSocketAddress;

import audiocast.audio.Play;
import audiocast.audio.Record;
import audiocast.audio.Client;
import audiocast.audio.Server;
import co324.audiocast.R;





public class AudiocastActivity extends Activity {
	final static int SAMPLE_HZ = 11025, BACKLOG = 8;	
	final static InetSocketAddress multicast = new InetSocketAddress("224.0.0.1", 3210);
    final int port = 3210;
    //final static InetSocketAddress address = new InetSocketAddress("224.0.0.1") ;
	Record rec; 
	Play play;
    Server server;
    Client client;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_audiocast);



		
		WifiManager wifi = (WifiManager)getSystemService( Context.WIFI_SERVICE );
		if(wifi != null){
		    WifiManager.MulticastLock lock = 
		    		wifi.createMulticastLock("Audiocast");
		    lock.setReferenceCounted(true);
		    lock.acquire();
		} else {
			Log.e("Audiocast", "Unable to acquire multicast lock");
			finish();
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		BlockingQueue<byte[]> rec_que = new ArrayBlockingQueue<byte[]>(BACKLOG);
        BlockingQueue<byte[]> play_que = new ArrayBlockingQueue<byte[]>(BACKLOG);

		rec = new Record(SAMPLE_HZ,rec_que);
		play = new Play(SAMPLE_HZ, play_que);
        server = new Server(rec_que);
        client = new Client(play_que);

        findViewById(R.id.Record).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                rec.pause(!((ToggleButton)v).isChecked());
                if(((ToggleButton)v).isChecked()){
                    Server.broadcast = true;
                }else{
                    Server.broadcast = false;
                }

                play.pause(((ToggleButton)v).isChecked());
                if(!((ToggleButton)v).isChecked()){
                    Client.received = true;
                }else{
                    Client.received = false;
                }
            }
        });
		
		Log.i("Audiocast", "Starting recording/playback threads");
		rec.start();
		play.start();
        server.start();
        client.start();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		Log.i("Audiocast", "Stopping recording/playback threads");
		rec.interrupt();
		play.interrupt();
        client.interrupt();
        server.interrupt();
	}
}
