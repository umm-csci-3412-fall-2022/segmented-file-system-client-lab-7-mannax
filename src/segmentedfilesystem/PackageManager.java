package segmentedfilesystem;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class PackageManager {
    static int totalPackets = 256;
    ArrayList<packet> packets = new ArrayList<packet>();
    

    public void insertPacket(DatagramPacket input){
        // System.out.println(input.getData()[10]);
        if((2&input.getData()[0]) == 2){
            packets.add(new headerPacket(input));
        } else {
            packets.add(new dataPacket(input));
        }
    }
 ArrayList<ArrayList<packet>> orgPacket = new ArrayList<ArrayList<packet>>();
    public void fileOrganizer(){
            for (packet pack : packets){
                //System.out.println(pack.data);

                for (int i = 0; i < 256; i++) {
                    // If the current index is within the size of the ArrayList of ArrayLists of packets
                    // and the FileID of the current packet is equal to the FileID of the first packet of the
                    // current packet list, then we insert the current packet into the current packet list
                    if(orgPacket.size() > i && orgPacket.get(i).get(0).FileID == pack.FileID){
                        // If previous condition is met and it's a header packet, put it at position 0
                        // Otherwise add it to the end
                        if(pack.getClass() == headerPacket.class){
                            orgPacket.get(i).add(0, pack);
                            break;
                        } else {
                            orgPacket.get(i).add(pack);
                            break;
                        }
                        // In the case that the index is outside the size of the ArrayList
                        // Then we assume we are at the end of the list of ArrayLists and
                        // We can add a new one and put the current pack in that new ArrayList
                    } else if (orgPacket.size() <= i ){
                        orgPacket.add(new ArrayList<packet>());
                        if(pack.getClass() == headerPacket.class){
                            orgPacket.get(i).add(0, pack);
                        } else orgPacket.get(i).add(pack);
                        break;
                    }
                }
            }
    }


    public void packetOrganizer(){
        for (ArrayList<packet> arrayList : orgPacket) {
            arrayList.sort(new packetComparator());
        }
    }
}

class packet{
    byte[] data;
    int FileID;
    int packetNumber;
}

class packetComparator implements Comparator<packet> {
    public int compare(packet p1, packet p2){

        if(p1.packetNumber > p2.packetNumber || p2.getClass() == headerPacket.class) return 1;
        if(p1.packetNumber < p2.packetNumber || p1.getClass() == headerPacket.class) return -1;
        return 0;
    }
}

class headerPacket extends packet{
    String fileName;

    headerPacket(DatagramPacket packet){
        data = new byte[packet.getLength() - 2];
        int index = 0;
        for (int i = 2; i < packet.getLength(); ++i){
            data[index++] = packet.getData()[i];
        }
        FileID = Byte.toUnsignedInt(data[1]);

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
        FileID = Byte.toUnsignedInt(data[1]);
        packetNumber = Byte.toUnsignedInt(data[2])*256 + Byte.toUnsignedInt(data[3]);
        if((3&packet.getData()[0]) == 3) PackageManager.totalPackets = packetNumber;
    }


}