package tools;

import java.net.*;
import java.util.*;
import javax.swing.SwingUtilities;

/*
 *   NetworkManager
 * 
 *  whole network system
 *  client listens for hosts, host broadcasts
 * 
 *      startBroadcasting  startDiscovery  stopDiscovery
 *      restartNetwork  setClientMode
 * 
 */
public class NetworkManager {
    public static final int DISCOVERY_PORT = 50055; // udp port for discovering hosts
    public static String myIp = "127.0.0.1"; // local ip
    public static final String myUUID = UUID.randomUUID().toString(); // an id to distinguish this client from others
    private static DatagramSocket discoverySocket; // socket for discovering
    private static boolean isDiscovering = false;
    private static boolean isBroadcasting = false;

    public static class DiscoveredHost {
        public String ip; // ip address of the discovered host (we will use this to connect hosts database)
        public long lastSeen; // to change if host is not seen for 8 seconds

        public DiscoveredHost(String _ip) {
            this.ip = _ip;
            this.lastSeen = System.currentTimeMillis();
        }
    }

    public static List<DiscoveredHost> discoveredHosts = new ArrayList<>(); // list of discovered hosts
    public static Runnable onHostFound; // when we find a new host this will trigger MainFile.java script will catch and get ip from here (discoveredHosts)
    public static boolean isClientMode = true; // true if client mode, false if host mode

    // Get local ip so we know who we are on the network
    static {
        try {
            try (final DatagramSocket tempSocket = new DatagramSocket()) {
                tempSocket.connect(InetAddress.getByName("8.8.8.8"), 10002); // this sends a fake packet to google dns to find local ip
                myIp = tempSocket.getLocalAddress().getHostAddress(); // this gets the local ip
            }
        } catch (Exception e) {
            try {
                myIp = InetAddress.getLocalHost().getHostAddress(); // this gets the local ip
            } catch (Exception e2) {
                System.out.println("error ip : " + e2);
            }
        }
    }

    // this sets the client mode
    // true = client
    // false = host
    public static void setClientMode(boolean _client) {
        isClientMode = _client;
    }

    // this restarts the network when we change mode
    public static void restartNetwork() {
        stopDiscovery();
        isBroadcasting = false;
        try {
            Thread.sleep(200);
        } catch (Exception e) {
        }

        discoveredHosts.clear();
        if (isClientMode) {
            startDiscovery();
        } else {
            startBroadcasting();
        }
        // TODO reconnect database after mode change
    }

    // this starts broadcasting for host mode
    public static void startBroadcasting() {
        if (isBroadcasting) {
            return;
        }
        if (isClientMode) {
            return;
        }
        isBroadcasting = true;

        // this should run in a new thread so it doesnt freeze prohram
        new Thread(new Runnable() {

            @Override
            public void run() {

                try (DatagramSocket socket = new DatagramSocket()) {

                    socket.setBroadcast(true);

                    while (isBroadcasting) {

                        String message = "LOB_HOST:" + myIp + ":" + myUUID; // format => leauge of bilkent host : ip : uuid

                        byte[] byteMessage = message.getBytes();

                        try {
                            socket.send(new DatagramPacket(byteMessage, byteMessage.length,
                                    InetAddress.getByName("255.255.255.255"), DISCOVERY_PORT)); // this sends the message to all devices that are connected to DISCOVERY_PORT
                        } catch (Exception e) {
                        }

                        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

                        while (interfaces.hasMoreElements()) {
                            NetworkInterface networkInterface = interfaces.nextElement();

                            if (networkInterface.isLoopback() || !networkInterface.isUp()) { // if it comes from localgost or disabled skip it
                                continue;
                            }

                            for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                                InetAddress broadcast = interfaceAddress.getBroadcast();
                                if (broadcast == null) { // if it doesnt have a broadcast address, skip it
                                    continue;
                                }
                                try {
                                    socket.send(new DatagramPacket(byteMessage, byteMessage.length, broadcast,
                                            DISCOVERY_PORT)); // send the message to all devices that are connected to DISCOVERY_PORT
                                } catch (Exception e) {
                                }
                            }
                        }
                        Thread.sleep(2000); // wait for 2 seconds before broadcasting again for not get flagged as spam
                    }
                } catch (Exception e) {
                    System.out.println("broadcast error: " + e.getMessage());
                }
                isBroadcasting = false; // stops broadcasting when thread ends
            }
        }).start();
    }

    // this starts discovery for client mode
    public static void startDiscovery() {
        if (isDiscovering) {
            return;
        }
        if (!isClientMode) {
            return;
        }

        isDiscovering = true;
        discoveredHosts.clear(); // clears previous hosts

        // this should run in a new thread so it doesnt freeze program
        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    discoverySocket = new DatagramSocket(DISCOVERY_PORT);
                    byte[] byteMessage = new byte[1024];

                    while (isDiscovering) {

                        DatagramPacket packet = new DatagramPacket(byteMessage, byteMessage.length);
                        discoverySocket.receive(packet); // waits to receive a packet from host

                        String incomingMessage = new String(packet.getData(), 0, packet.getLength()); // converts the byte to String

                        if (incomingMessage.startsWith("LOB_HOST:")) { // check if this is our programs packet

                            String[] messageParts = incomingMessage.split(":"); // split message to get ip and uuid
                            int partCount = messageParts.length;

                            if (partCount >= 3) {

                                String hostIp = messageParts[1].trim();

                                if (hostIp.equals("127.0.0.1") || hostIp.startsWith("0:0:0")) {
                                    hostIp = "localhost"; // if host is on the same pc set it as localhost
                                }

                                String hostUUID = messageParts[2].trim();

                                if (!hostUUID.equals(myUUID)) { // if the received uuid is not ours

                                    boolean hostExists = false;

                                    for (DiscoveredHost activeHost : discoveredHosts) {
                                        if (activeHost.ip.equals(hostIp)) {
                                            hostExists = true;
                                            activeHost.lastSeen = System.currentTimeMillis(); // update the last time we saw this host
                                            break;
                                        }
                                    }

                                    if (!hostExists) { // if this is a newly discovered host
                                        discoveredHosts.add(new DiscoveredHost(hostIp));

                                        if (onHostFound != null) {
                                            SwingUtilities.invokeLater(onHostFound); // host is found notify others
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("discovery failed: " + e.getMessage());
                }

                isDiscovering = false; // stops discovery when thread ends
            }
        }).start();

        // TODO remove unactive hosts
        
    }

    public static void stopDiscovery() {
        isDiscovering = false;
        boolean wasClosed = false;
        if (discoverySocket != null && !discoverySocket.isClosed()) {
            discoverySocket.close();
            wasClosed = true;
        }
    }
}
