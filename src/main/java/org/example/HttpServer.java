package org.example;

public class HttpServer {
    private final int tcpPort;
    private boolean flag;

    public HttpServer(int tcpPort) {
        this.tcpPort = tcpPort;
        this.flag = false;
    }

    public void startServer() {
        System.out.println("Server accepting requests on port " + tcpPort);
        while (true) {
            var connectionHandler = new ConnectionHandler(this);
            if (flag) {
                connectionHandler.handleTxt();
            } else  {
                connectionHandler.handleJson();
            }
            flag = !flag;
        }
    }

    public int getTcpPort() {
        return tcpPort;
    }
}
