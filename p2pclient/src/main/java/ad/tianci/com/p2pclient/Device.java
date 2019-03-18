package ad.tianci.com.p2pclient;

public class Device {
    public Device() {

    }

    public Device(String name, String ip, int port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String toString() {
        return name + " - " + ip + ":" + port;
    }

    public String name;
    public String ip;
    public int port;
}
