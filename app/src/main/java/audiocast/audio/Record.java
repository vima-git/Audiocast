package audiocast.audio;

/**
 * @author (C) E/11/174 & E/11/311
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import android.net.wifi.WifiManager;
import android.net.DhcpInfo;
//import android.net.NetworkStateTracker;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public final class Record extends Thread {

	private static final int MAXLEN = 1024;
	final AudioRecord stream;
	final BlockingQueue<byte[]> queue;
    SocketAddress address;
    DatagramSocket socket;
    int port;

	public Record(int sampleHz, BlockingQueue<byte[]> queue) {
		this.queue = queue;

		int bufsize = AudioRecord.getMinBufferSize(
				sampleHz, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
		Log.i("Audiocast","initialised recorder with buffer length "+ bufsize);
		
		stream = new AudioRecord(
					MediaRecorder.AudioSource.MIC,
					sampleHz,
					AudioFormat.CHANNEL_IN_MONO, 
					AudioFormat.ENCODING_PCM_16BIT , 
					bufsize);



	}

//    InetAddress getBroadcastAddress() throws IOException {
//        WifiManager wifi = mContext.getSystemService(Context.WIFI_SERVICE);
//        DhcpInfo dhcp = wifi.getDhcpInfo();
//        // handle null somehow
//
//        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
//        byte[] quads = new byte[4];
//        for (int k = 0; k < 4; k++)
//            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
//        return InetAddress.getByAddress(quads);
//    }

	@Override
	public void run() {
		try {
			byte[] pkt = new byte[MAXLEN];

            //socket = new DatagramSocket();
			
			while (!Thread.interrupted()) {
				int len = stream.read(pkt, 0, pkt.length);
				queue.put(pkt);
				Log.d("Audiocast", "recorded "+len+" bytes");
               //DatagramPacket packet = new DatagramPacket(pkt, 0, pkt.length, address);
                //socket.send(packet);
			}
		} catch (InterruptedException e) {
		//}catch (SocketException e) {
            //e.printStackTrace();
        //}catch (IOException e){
         //e.printStackTrace();
        }finally {
			stream.stop();
			stream.release();
		}

	}
	
	public void pause(boolean pause) {
		if (pause) stream.stop(); 
		else stream.startRecording();
		
		Log.i("Audiocast", "record stream state=" + stream.getState());	
	}
}
