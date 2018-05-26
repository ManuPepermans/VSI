package clients;

import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.messages.std.Header;
import msgs.LaserScan;
import raytrace.RayTracer;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 * Created by the following students at the University of Antwerp
 * Faculty of Applied Engineering: Electronics and ICT
 * Based on Janssens Arthur, De Laet Jan and Verhoeven Peter their work
 * Adaptations by Manu Pepermans
 *
 * Used by the agents to update the laserScan with the external robots
 **/

public class MyLaserCallback implements TopicCallback {
    RealClient client;

    public MyLaserCallback(RealClient client){
        this.client = client;
    }

    /**
     *
     * @param message
     */
    @Override
    public void handleMessage(Message message) {
        //Get laserscan
        LaserScan laserScan = LaserScan.fromMessage(message);
        Topic updatedLaserScan = new Topic(client.ros, "/updatedScan", "sensor_msgs/LaserScan", 300);
        edu.wpi.rail.jrosbridge.primitives.Time time;
        Header h;
        //if more than one external robot && at least one Owned Agent
        synchronized (client.externalAgents) {
            if (client.externalAgents.size() > 0 && client.ownedAgents.size() > 0) {
                //Raytrace, modify laserscan
                float[] updatedRanges = new float[0];
                updatedRanges = RayTracer.rayTrace(client, laserScan, laserScan.getRanges().length);
                // time from ROS
                synchronized (client.clientTime) {
                    time = edu.wpi.rail.jrosbridge.primitives.Time.fromJsonString(client.clientTime);
                }
                h = new Header(laserScan.getHeader().getSeq(), time, new String("laser"));
                //publish updated laserscan
                updatedLaserScan.publish(new LaserScan(h, laserScan.getAngle_min(), laserScan.getAngle_max(), laserScan.getAngle_increment(), laserScan.getTime_increment(), laserScan.getScan_time(), laserScan.getRange_min(), laserScan.getRange_max(), updatedRanges, getJsonArrayBuilder(updatedRanges), laserScan.getIntensities(), getJsonArrayBuilder(laserScan.getIntensities())));
            } else {
                // time from ROS
                synchronized (client.clientTime) {
                    time = edu.wpi.rail.jrosbridge.primitives.Time.fromJsonString(client.clientTime);
                }
                h = new Header(laserScan.getHeader().getSeq(), time, new String("laser"));

                //publish unmodified laserscan
                updatedLaserScan.publish(new LaserScan(h, laserScan.getAngle_min(), laserScan.getAngle_max(), laserScan.getAngle_increment(), laserScan.getTime_increment(), laserScan.getScan_time(), laserScan.getRange_min(), laserScan.getRange_max(), laserScan.getRanges(), getJsonArrayBuilder(laserScan.getRanges()), laserScan.getIntensities(), getJsonArrayBuilder(laserScan.getIntensities())));
            }
        }
    }

    /**
     *
     * @param ranges
     * @return
     */
    private JsonArrayBuilder getJsonArrayBuilder(float[] ranges){
        JsonArrayBuilder jsonRangeBuilder = Json.createArrayBuilder();
        for (float f : ranges){
            jsonRangeBuilder.add(f);
        }
        return jsonRangeBuilder;
    }
}
