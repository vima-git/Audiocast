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
import java.net.MulticastSocket;



public class Client extends Thread {


    final BlockingQueue<byte[]> buff ;
    private static InetAddress spread ; //host address
    private static MulticastSocket socket = null ;
    final static  int MAXSIZE = 1024;// data size
    public static boolean received = false ; // received ?
    InetAddress source ;
    String broadcast_IP = "224.0.0.1" ; // broadcast ip

    public Client(BlockingQueue<byte[]> buff){
        this.buff = buff ;
        try{

            socket = new MulticastSocket(12000);
            spread = InetAddress.getByName(broadcast_IP);
            socket.joinGroup(spread);


        }catch (IOException e){

        }


    }


    @Override

    public void run(){
    try {
        DatagramPacket packet;
        byte[] receive = new byte[MAXSIZE];

        while (!Thread.interrupted()) {
            packet = new DatagramPacket(receive, MAXSIZE);

            try {
                if (received) socket.receive(packet);
                receive = packet.getData();
                buff.put(receive);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }catch (InterruptedException e){
            e.printStackTrace();
     }finally{
        try {
            socket.leaveGroup(spread);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    }










}
