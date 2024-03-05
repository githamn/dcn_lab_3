package AS;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

public class AS {
    private static final int PORT = 53533;
    private static final Map<String, String> asDatabase = new HashMap<>();

    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket(PORT);
            byte[] receiveBuffer = new byte[1024];

            while (true) {
                DatagramPacket packet = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(packet);
                String receivedData = new String(packet.getData(), 0, packet.getLength());

                if (receivedData.contains("VALUE=")) {
                    // Store the data
                    String[] parts = receivedData.split("\n");
                    String name = parts[1].split("=")[1];
                    String value = parts[2].split("=")[1];
                    asDatabase.put(name, value);
                } else {
                    // Retrieve and send data
                    String name = receivedData.split("\n")[1].split("=")[1];
                    String ip = asDatabase.getOrDefault(name, null);
                    if (ip != null) {
                        String response = "TYPE=A\nNAME=" + name + "\nVALUE=" + ip + "\nTTL=10\n";
                        byte[] sendData = response.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                        socket.send(sendPacket);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
