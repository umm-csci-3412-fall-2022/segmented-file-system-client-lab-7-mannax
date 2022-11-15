public class PackageManager {
    Byte[][] packets = new Byte[256][];
    int totalPackets = 256;

    public void insertPacket(int fileID, int packetNumber Byte status, Byte[] data){
        packets[packetNumber] = data;
        if((3&status) == 3) totalPackets = fileID;
    }
}
