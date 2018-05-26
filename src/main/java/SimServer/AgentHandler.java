package SimServer;

import clients.Client;
import edu.wpi.rail.jrosbridge.messages.geometry.Pose;
import edu.wpi.rail.jrosbridge.messages.geometry.Twist;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by the following students at the University of Antwerp
 * Faculty of Applied Engineering: Electronics and ICT
 * Manu Pepermans
 *
 * This class manages robots and if every robot is created using this class, no robot can have the same ID.
 **/
public class AgentHandler {
    private long agentCounter = 0;

    private List<Client> clients;

    /**
     * Creates agents.
     */
    public AgentHandler(){
        clients = new ArrayList<Client>();
    }

    public void addClient(Client client){
        clients.add(client);
    }

    /**
     * Create a new agent with a new ID
     * @param model_name
     * @param pose
     * @param twist
     * @return
     */
    public Agent newAgent(String model_name, Pose pose, Twist twist){
        agentCounter++;
        return new Agent(agentCounter, model_name, pose, twist, this);
    }

}
