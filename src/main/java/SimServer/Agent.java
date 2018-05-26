package SimServer;

import edu.wpi.rail.jrosbridge.messages.geometry.*;
import extras.Quat;
import raytrace.Segment;

/**
 * Created by the following students at the University of Antwerp
 * Faculty of Applied Engineering: Electronics and ICT
 * Based on Janssens Arthur, De Laet Jan and Verhoeven Peter their work
 * Adaptations by Manu Pepermans
 **/
public class Agent {

    public long id;
    public String model_name;
    public Pose pose;
    public Twist twist;
    private AgentHandler agentHandler;

    public boolean created = false;

    /**
     * new Agent
     * @param id
     * @param model_name
     * @param pose
     * @param twist
     * @param agentHandler
     */
    public Agent(long id, String model_name, Pose pose, Twist twist, AgentHandler agentHandler){
        this.id=id;
        this.model_name=model_name;
        this.pose=pose;
        this.twist=twist;
        this.agentHandler = agentHandler;
    }

    public Agent clone(){
        return new Agent(id, model_name, pose, twist, null);
    }

    public Agent(String str){
        this.model_name = str;
    }

    /**
     *
     * @param pose
     * @param twist
     */
    public void updateAgent(Pose pose, Twist twist){
        this.pose=pose;
        this.twist=twist;
    }

    public void setCreated(boolean status){
        this.created = status;
    }

    public void updateAgent(Pose pose){
        this.pose=pose;
    }

    /**
     * This method refreshes the Message string created in the Super. If the values are modified,
     * the string will not be updated without this function. Thus the wrong data would be sent if published
     */
    public void refreshStrings(){
        pose = new Pose(new Point(pose.getPosition().getX(), pose.getPosition().getY(), pose.getPosition().getZ()), new Quaternion(pose.getOrientation().getX(), pose.getOrientation().getY(), pose.getOrientation().getZ(), pose.getOrientation().getW()));
        twist = new Twist(new Vector3(twist.getLinear().getX(), twist.getLinear().getY(), twist.getLinear().getZ()), new Vector3(twist.getAngular().getX(), twist.getAngular().getY(), twist.getAngular().getZ()));
    }

    @Override
    public boolean equals(Object o){
        if(o.getClass() == this.getClass()){
            if(((Agent) o).model_name.equals(this.model_name))
                return true;
            else
                return false;
        }else if(o.getClass() == String.class){
            if(((String) o) == this.model_name)
                return true;
            else
                return false;
        }
        return false;
    }

    /**
     * Get four corners of the agent in a 2D environment
     *
     * @return
     */
    public Segment[] getSegments(){
        //Four corners
        double[] corner = new double[]{this.pose.getPosition().getX()-0.125, this.pose.getPosition().getY()-0.25, this.pose.getPosition().getZ()};
        double[] corner1 = new double[]{this.pose.getPosition().getX()-0.125, this.pose.getPosition().getY()+0.25, this.pose.getPosition().getZ()};
        double[] corner2 = new double[]{this.pose.getPosition().getX()+0.125, this.pose.getPosition().getY()-0.25, this.pose.getPosition().getZ()};
        double[] corner3 = new double[]{this.pose.getPosition().getX()+0.125, this.pose.getPosition().getY()+0.25, this.pose.getPosition().getZ()};

        //Translate corners
        double[] newcorner = rotationAround3DEuler(pose.getPosition(), corner, Quat.toEulerianAngle(this.pose.getOrientation()));
        double[] newcorner1 = rotationAround3DEuler(pose.getPosition(), corner1, Quat.toEulerianAngle(this.pose.getOrientation()));
        double[] newcorner2 = rotationAround3DEuler(pose.getPosition(), corner2, Quat.toEulerianAngle(this.pose.getOrientation()));
        double[] newcorner3 = rotationAround3DEuler(pose.getPosition(), corner3, Quat.toEulerianAngle(this.pose.getOrientation()));

        /*
        double[] newcorner = rotation3D(corner, this.pose.getOrientation());
        double[] newcorner1 = rotation3D(corner1, this.pose.getOrientation());
        double[] newcorner2 = rotation3D(corner2, this.pose.getOrientation());
        double[] newcorner3 = rotation3D(corner3, this.pose.getOrientation());
        */

        //New segments from translated corners
        Segment s = new Segment(new double[]{newcorner[0], newcorner[1]}, new double[]{newcorner1[0], newcorner1[1]});
        Segment s1 = new Segment(new double[]{newcorner1[0], newcorner1[1]}, new double[]{newcorner3[0], newcorner3[1]});
        Segment s2 = new Segment(new double[]{newcorner3[0], newcorner3[1]}, new double[]{newcorner2[0], newcorner2[1]});
        Segment s3 = new Segment(new double[]{newcorner2[0], newcorner2[1]}, new double[]{newcorner[0], newcorner[1]});

        return new Segment[]{s,s1,s2,s3};
    }

    /**
     * Rotates a point around a different center point in 2D (yaw in 3D)
     * @param center
     * @param point
     * @param angles
     * @return
     */
    private double[] rotationAround3DEuler(Point center, double[] point, double[] angles){

        double[] newPoints = new double[3];

        //Translate point to center, rotate, and transform back
        newPoints[0] = center.getX() + (point[0]-center.getX())*Math.cos(angles[2]) - (point[1]-center.getY())*Math.sin(angles[2]);

        newPoints[1] = center.getY() + (point[0]-center.getX())*Math.sin(angles[2]) + (point[1]-center.getY())*Math.cos(angles[2]);
        newPoints[2] = point[2];

        return newPoints;
    }


}
