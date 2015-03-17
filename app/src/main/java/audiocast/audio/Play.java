package audiocast.audio;

/**
 * @author (C) E/11/174 & E/11/311
 */

import java.util.concurrent.BlockingQueue;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;


public final class Play extends Thread {

    DatagramSocket socket;
    SocketAddress address;

	final AudioTrack stream;
	final BlockingQueue<byte[]> queue;	
	final int port = 3210;
	public Play(int sampleHz, BlockingQueue<byte[]> queue) {
		this.queue = queue;

		int bufsize = AudioTrack.getMinBufferSize(
				sampleHz, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
		Log.i("Audiocast","initialised player with buffer length "+ bufsize);		
		
		stream = new AudioTrack( 
					AudioManager.STREAM_VOICE_CALL, 
					sampleHz, 
					AudioFormat.CHANNEL_OUT_MONO, 
					AudioFormat.ENCODING_PCM_16BIT,  
					bufsize,
					AudioTrack.MODE_STREAM);
		
	}

	@Override
	public void run() {
		try {


			while (!Thread.interrupted()) {
				byte[] pkt = queue.take();
				int len = stream.write(pkt, 0, pkt.length);
				Log.d("Audiocast", "played "+len+" bytes");
                //DatagramPacket packet = new DatagramPacket(pkt, pkt.length,address);
                //socket.receive(packet);
			}
		} catch (InterruptedException e) {

        }finally {
			stream.stop();
			stream.release();
		}
	}
	
	public void pause(boolean pause) {
		if (pause) stream.stop(); 
		else stream.play();	
		
		Log.i("Audiocast", "playback stream state=" + stream.getState());		
	}
}
