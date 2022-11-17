package segmentedfilesystem;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class PackageManager {
    static int totalPackets = 256;
    ArrayList<packet> packets = new ArrayList<packet>();
    int index = 0;

    public void insertPacket(DatagramPacket input){
        System.out.println(input.getData()[10]);
        if((2&input.getData()[0]) == 2){
            packets.add(new headerPacket(input));
            index++;
        } else {
            packets.add(new dataPacket(input));
            index++;
        }
    }
 ArrayList<byte[]>[] orgPacket = new ArrayList[256];
    public void packetOrginizer(){
            for (packet pack : packets){
                System.out.println(pack.data);
                if(pack.getClass() == headerPacket.class) orgPacket[pack.FileID].add(0, pack.data);
                if(pack.getClass() == dataPacket.class) orgPacket[pack.FileID].add(pack.packetNumber+1, pack.data);
            }
    }
}

class packet{
    byte[] data;
    int FileID;
    int packetNumber;
}

class headerPacket extends packet{
    String fileName;

    headerPacket(DatagramPacket packet){
        data = new byte[packet.getLength() - 2];
        int index = 0;
        for (int i = 2; i < packet.getLength(); ++i){
            data[index++] = packet.getData()[i];
        }
        FileID = (int)data[1];

        fileName = new String(data, 0, data.length);
    }
}

class dataPacket extends packet{
    dataPacket(DatagramPacket packet){
        data = new byte[packet.getLength() - 4];
        int index = 0;
        for (int i = 4; i < packet.getLength(); ++i){
            data[index++] = packet.getData()[i];
        }
        //data = packet.getData();
        FileID = (int)data[1];
        packetNumber = Byte.toUnsignedInt(data[2])*256 + Byte.toUnsignedInt(data[3]);
        if((3&packet.getData()[0]) == 3) PackageManager.totalPackets = packetNumber;
    }


}