package segmentedfilesystem;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Comparator;

public class PackageManager {
    static int totalPackets = 0;
    static int tempPackets = 0;
    static int numend = 0;
    ArrayList<packet> packets = new ArrayList<packet>();
    

    /**
     * insertPacket takes a datagrampacket from the server and puts it into the array list of packets
     * as well as putting it into a class, headerPacket or dataPacket depending on the status bytes.
     * We assume this is thrown into a while loop in the FileRetriever, which the dataPacket constructor
     * will update the number of total expected packets when it receives a 3 packets with status 3.
     * @param input Datapacket received from the server
     */
    public void insertPacket(DatagramPacket input){
        int dataLength = input.getLength();
        byte[] packet = input.getData();
        if((1&packet[0]) == 1){
            packets.add(new dataPacket(packet, dataLength));
        } else {
            packets.add(new headerPacket(packet, dataLength));
        }
    }
 ArrayList<ArrayList<packet>> orgPacket = new ArrayList<ArrayList<packet>>();

 /**
  * This uses the array list of packets generated from calling insertPacket, and sorts it into 
  *  a 2-dimensional arrayList, each individual arrayList is responsible for holding a file with a specific
  * fileID. 
  */
    public void fileOrganizer(){
            for (packet pack : packets){

                for (int i = 0; i < 256; i++) {
                    // If the current index is within the size of the ArrayList of ArrayLists of packets
                    // and the FileID of the current packet is equal to the FileID of the first packet of the
                    // current packet list, then we insert the current packet into the current packet list
                    if(orgPacket.size() > i && orgPacket.get(i).get(0).FileID == pack.FileID){
                        orgPacket.get(i).add(pack);
                        break;
                    } else if (orgPacket.size() <= i ){
                        orgPacket.add(new ArrayList<packet>());
                        orgPacket.get(i).add(pack);
                        break;
                    }
                }
            }
    }

    /**
     * This uses the 2-dimensional arrayList made from fileOrganizer and organizes the contents
     * of each individual file. It uses the packetComparator defined below, which just compares packetNumbers
     * with the exception that headerPackets are always smaller than dataPackets.
     */
    public void packetOrganizer(){
        for (ArrayList<packet> arrayList : orgPacket) {
            arrayList.sort(new packetComparator());
        }
    }
}

class packet{
    byte[] data;
    int FileID;
    int packetNumber = 0;
    String fileName;
} 

class packetComparator implements Comparator<packet> {
    public int compare(packet p1, packet p2){

        if(p1.packetNumber > p2.packetNumber || p2.getClass() == headerPacket.class) return 1;
        if(p1.packetNumber < p2.packetNumber || p1.getClass() == headerPacket.class) return -1;
        return 0;
    }
}

class headerPacket extends packet{
    

    headerPacket(byte[] packet, int dataLength){
        data = new byte[dataLength - 2];
        int index = 0;
        for (int i = 2; i < dataLength; ++i){
            data[index++] = packet[i];
        }
        FileID = packet[1];
        fileName = (new String(data, 0, data.length)).replaceAll("\0", "");
    }
}

class dataPacket extends packet{
    dataPacket(byte[] packet, int dataLength){
        int index = 0;
        data = new byte[dataLength - 4];
        for (int i = 4; i < dataLength; ++i){
            data[index++] = packet[i];
        }
        FileID = packet[1];
        packetNumber = 256*Byte.toUnsignedInt(packet[2]) + Byte.toUnsignedInt(packet[3]);
        if((3&packet[0]) == 3) {
         PackageManager.tempPackets += packetNumber + 2;
            ++PackageManager.numend;
            if(PackageManager.numend == 3){
               PackageManager.totalPackets = PackageManager.tempPackets;
            }
        }
    }


}