package org.example;

public class HttpServer {
    private final int tcpPort;

    public HttpServer(int tcpPort) {
        this.tcpPort = tcpPort;
    }

    public void startServer() {
        System.out.println("Server accepting requests on port " + tcpPort);
        while (true) {
            var connectionHandler = new ConnectionHandler(this);
            connectionHandler.handle();
        }
    }

    public int getTcpPort() {
        return tcpPort;
    }
}
