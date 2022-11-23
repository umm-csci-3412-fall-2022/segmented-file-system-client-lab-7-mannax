package segmentedfilesystem;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
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
        socket.send(packet);
        PackageManager potato = new PackageManager();

        while(potato.packets.size() < PackageManager.totalPackets){ // I think this is still inaccurate
        packet = new DatagramPacket(receiveBuf, receiveBuf.length);
        socket.receive(packet);
        potato.insertPacket(packet);
        }
        socket.close();
        potato.fileOrganizer();
        potato.packetOrganizer();
        for (int i = 0; i < potato.orgPacket.size(); i++) {
                File newFile = new File(new String(potato.orgPacket.get(i).get(0).data, 0, potato.orgPacket.get(i).get(0).data.length));
                //File newFile = new File(potato.orgPacket.get(i).get(0).fileName);
                System.setOut(new PrintStream(newFile));
                for (int j = 1; j < potato.orgPacket.get(i).size(); j++) {
                        System.out.write(potato.orgPacket.get(i).get(j).data);
                        System.out.flush();
                        }
                System.out.flush();
        }
        } catch (Exception e){
                System.err.println("Exception: " + e);
        }

	}

}
