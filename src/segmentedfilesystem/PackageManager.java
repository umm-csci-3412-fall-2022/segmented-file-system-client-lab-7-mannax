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
        //System.out.println(input.getData()[1]);
        if((1&input.getData()[0]) == 1){ // This is more accurate than 2, odd numbers can have two in it as well
            packets.add(new dataPacket(input));
        } else {
            packets.add(new headerPacket(input));
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
                    //if(orgPacket.size() > i) System.out.println("Check " + orgPacket.get(i).get(0).FileID + " vs " + pack.FileID);
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


    public void packetOrganizer(){
        for (ArrayList<packet> arrayList : orgPacket) {
            arrayList.sort(new packetComparator());
        }

        for (ArrayList<packet> arrayList : orgPacket) {
            System.out.println("Start of a packet...");
            for (int i = 0; i < arrayList.size(); i++) {
                System.out.println(arrayList.get(i).packetNumber);
            }
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
    

    headerPacket(DatagramPacket packet){
        byte[] temp = packet.getData();
        data = new byte[packet.getLength() - 2];
        int index = 0;
        for (int i = 2; i < packet.getLength(); ++i){
            data[index++] = temp[i];
        }
        FileID = temp[1];

        fileName = new String(data, 0, data.length);
        System.out.println(fileName);
    }
}

class dataPacket extends packet{
    dataPacket(DatagramPacket packet){
        byte[] temp = packet.getData();
        data = new byte[packet.getLength() - 4];
        int index = 0;
        for (int i = 4; i < packet.getLength(); ++i){
            data[index++] = temp[i];
        }
        //data = packet.getData();
        FileID = temp[1];
        packetNumber = Byte.toUnsignedInt(temp[2]) + Byte.toUnsignedInt(temp[3]);
        //System.out.println(packetNumber);
        if((3&packet.getData()[0]) == 3) PackageManager.totalPackets = packetNumber;
    }


}