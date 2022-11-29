package segmentedfilesystem;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.event.SwingPropertyChangeSupport;
import javax.xml.transform.Templates;

public class PackageManager {
    static int totalPackets = 0;
    static int tempPackets = 0;
    static int numend = 0;
    ArrayList<packet> packets = new ArrayList<packet>();
    

    public void insertPacket(DatagramPacket input){
        //System.out.println(input.getData()[1]);
        byte[] packet = input.getData();
        if((1&packet[0]) == 1){ // This is more accurate than 2, odd numbers can have two in it as well  
            packets.add(new dataPacket(packet));
        } else {
            //System.out.println(data[0]);
            packets.add(new headerPacket(packet));
        }
        // System.out.println("number of end packets : " + numend);
        // System.out.println("Number of temp packets : " + tempPackets);
        // System.out.println("Variable totalPackets : " + totalPackets);
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
    

    headerPacket(byte[] packet){
        data = new byte[packet.length - 2];
        int index = 0;
        for (int i = 2; i < packet.length; ++i){
            data[index++] = packet[i];
        }
        FileID = packet[1];

        fileName = (new String(data, 0, data.length)).replaceAll("\0", "");

        System.out.println("Name : |" + fileName + "| length : " + fileName.length() + " --------------------------------");
    }
}

class dataPacket extends packet{
    dataPacket(byte[] packet){
        int index = 0;
        data = new byte[packet.length - 4];
        for (int i = 4; i < packet.length; ++i){
            data[index++] = packet[i];
        }
        //data = packet.getData();
        FileID = packet[1];
        packetNumber = 256*Byte.toUnsignedInt(packet[2]) + Byte.toUnsignedInt(packet[3]);
        //System.out.println(packetNumber);
        if((3&packet[0]) == 3) {
         //   PackageManager.totalPackets = packetNumber;
         System.out.println(packetNumber);
         PackageManager.tempPackets += packetNumber + 2;
         System.out.println("tempPackets : " + PackageManager.tempPackets);
         System.out.println("totalPackets : " + PackageManager.totalPackets);
            ++PackageManager.numend;
            if(PackageManager.numend == 3){
               PackageManager.totalPackets = PackageManager.tempPackets;
               System.out.println("totalPackets : " + PackageManager.totalPackets);
            }
        }
    }


}