package clients;

import SimServer.Agent;
import SimServer.Main;
import edu.wpi.rail.jrosbridge.Ros;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by the following student at the University of Antwerp
 * Faculty of Applied Engineering: Electronics and ICT
 * Manu Pepermans
 **/
abstract public class Client {

    protected InetAddress ip; // IP address of the client
    protected int port; // Port on which the client has connected
    protected Ros ros;
    public String clientTime;

    public final List<Agent> ownedAgents = Collections.synchronizedList(new ArrayList<Agent>());
    public final List<Agent> externalAgents = Collections.synchronizedList(new ArrayList<Agent>());


    /**
     *
     * @param ip
     * @param port
     */
    public Client(String ip, int port){
        try {
            this.ip = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.port = port;
        ros = new Ros(ip, port);
    }

    /**
     * initialize a client.
     * Register client to the handler
     * Connects to ROSbridge of the client
     */
    public void init(){
        ros.connect();

        //Register client List<SimServer.Agent> in agentHandler
        Main.agentHandler.addClient(this);

        //Callback threads
       lidarScan();
       agentPose();
       agentClock();

    }

    /**
     * Close connection to ROSbridge
     */
    public void closeRos(){
        ros.disconnect();
    }

    abstract public void lidarScan();
    abstract public void agentPose();
    abstract public void agentClock();


}
