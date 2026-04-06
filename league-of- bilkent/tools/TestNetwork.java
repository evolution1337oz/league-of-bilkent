package tools;

// quick test to check if network discovery works
public class TestNetwork {
    public static void main(String[] args) throws Exception {
        System.out.println("my ip: " + NetworkManager.myIp);
        System.out.println("my uuid: " + NetworkManager.myUUID);

        // start as host first to broadcast
        System.out.println("starting broadcast...");
        NetworkManager.isClientMode = false;
        NetworkManager.startBroadcasting();

        Thread.sleep(3000);
        System.out.println("broadcast is running");

        // switch to client and try to discover
        NetworkManager.isBroadcasting = false;
        Thread.sleep(500);

        NetworkManager.isClientMode = true;
        System.out.println("starting discovery...");
        NetworkManager.startDiscovery();

        Thread.sleep(5000);
        System.out.println("found " + NetworkManager.discoveredHosts.size() + " hosts");
        for (NetworkManager.DiscoveredHost h : NetworkManager.discoveredHosts) {
            System.out.println("  host: " + h.ip);
        }
        NetworkManager.stopDiscovery();
        System.out.println("done");
    }
}
