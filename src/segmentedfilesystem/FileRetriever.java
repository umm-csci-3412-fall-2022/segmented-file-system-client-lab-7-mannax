package segmentedfilesystem;
import java.io.File;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;


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
        // We make a DatagramSocket that is set as specified information that was declared before we made it.
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length, server, port);
        socket.send(packet);
        // we make potato our instance of PackageManager that will be used to call the methods and information form that class.
        PackageManager potato = new PackageManager();
        //we have the client loop through receiving info from the server until the package manager declares that we have all packets
        while(PackageManager.totalPackets == 0 || potato.packets.size() < PackageManager.totalPackets){ 
        // create a DatagramPacket to act as our packets that we receive from the server
        packet = new DatagramPacket(receiveBuf, receiveBuf.length);
        socket.receive(packet);
        // inserts the new created packet into an arrayList 
        potato.insertPacket(packet);
        receiveBuf = new byte[1028];
        }
        socket.close();
        //organizes the arraylist into a 2-dimensional arraylist where they are sorted by fileID
        potato.fileOrganizer();
        //this organizes the 2-dimensional arraylist in the individual arrayLists by packet number
        potato.packetOrganizer();
        for (int i = 0; i < potato.orgPacket.size(); i++) {
                // creates an arrayList of packets from a particular FileID
                ArrayList<packet> dPackets=potato.orgPacket.get(i);
                // Grabs that FileID
                String file = dPackets.get(0).fileName;
                // makes a new file based on the FileID
                File newFile = new File(file);
                // Sets the standard output to write out onto the file
                System.setOut(new PrintStream(newFile));
                for (int j = 1; j < potato.orgPacket.get(i).size(); j++) {
                        //loops through the arrayList and writes the data to the file
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
