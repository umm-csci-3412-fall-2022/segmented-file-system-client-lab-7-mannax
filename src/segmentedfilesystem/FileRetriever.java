package segmentedfilesystem;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class FileRetriever {

        InetAddress server;
        int port;
        DatagramSocket socket = null;
        DatagramPacket packet;
        byte[] sendBuf = new byte[256];
        byte[] receiveBuf = new byte[1028];

	public FileRetriever(String server, int port) {
        // Save the server and port for use in `downloadFiles()`
        //...
        try{
        this.server = InetAddress.getByName(server);
        System.out.println("Sever name : " + server);
        System.out.println("Port Number: " + port);
        this.port = port;
        } catch (Exception e){
                System.err.println("Exception: " + e);
        }
	}

	public void downloadFiles() {
        // Do all the heavy lifting here.
        // This should
        //   * Connect to the server
        //   * Download packets in some sort of loop
        //   * Handle the packets as they come in by, e.g.,
        //     handing them to some PacketManager class
        // Your loop will need to be able to ask someone
        // if you've received all the packets, and can thus
        // terminate. You might have a method like
        // PacketManager.allPacketsReceived() that you could
        // call for that, but there are a bunch of possible
        // ways.

        try {
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length, server, port);
        System.out.println("Sending packet....");
        socket.send(packet);
        PackageManager potato = new PackageManager();

        while(potato.index < PackageManager.totalPackets){
        packet = new DatagramPacket(receiveBuf, receiveBuf.length);
        socket.receive(packet);
        potato.insertPacket(packet);
        //System.out.println("Something received!");
        }
        System.out.println("Organization start");
        potato.packetOrginizer();
        System.out.println("Organization end");
        for (int i = 0; i < 256; i++) {
                for (int j = 0; j < potato.orgPacket[i].size(); j++) {
                        System.out.println(potato.orgPacket[i].get(j));
                }
        }

        //String received = new String(packet.getData(), 0, packet.getLength());
        
        
        //System.out.println("The big bean: " + );
        } catch (Exception e){
                System.err.println("Exception: " + e);
        }

	}

}
