package msgs;

import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.messages.std.Header;
import javax.json.*;

/**
 * Created by the following students at the University of Antwerp
 * Faculty of Applied Engineering: Electronics and ICT
 * Janssens Arthur, De Laet Jan and Verhoeven Peter.
 **/
public class LaserScan extends Message{

    public static final String HEADER = "header";

    public static final String ANGLE_MIN = "angle_min";
    public static final String ANGLE_MAX = "angle_max";
    public static final String ANGLE_INCREMENT = "angle_increment";
    public static final String TIME_INCREMENT = "time_increment";
    public static final String SCAN_TIME = "scan_time";

    public static final String RANGE_MIN = "range_min";
    public static final String RANGE_MAX = "range_max";

    public static final String RANGES = "ranges";
    public static final String INTENSITIES = "intensities";

    public static final String TYPE = "sensor_msgs/LaserScan";

    private final Header header;
    private final float angle_min, angle_max, angle_increment, time_increment, scan_time, range_min, range_max;
    private final float[] ranges, intensities;
    private JsonArrayBuilder rangeBuilder, intensityBuilder;

    /**
     * New Blank Laserscan message
     */
    public LaserScan(){
        this(new Header(), 0,0,0,0,0,0,0, new float[]{1,2,3}, Json.createArrayBuilder(), new float[]{4,5,6}, Json.createArrayBuilder());
    }

    /**
     * New lasercan message with updated data
     * @param header ROS Header message
     * @param angle_min
     * @param angle_max
     * @param angle_increment
     * @param time_increment
     * @param scan_time
     * @param range_min
     * @param range_max
     * @param ranges array of raytraced data
     * @param rangeBuilder used to update the Super's message string correctly
     * @param intensities array of intensities
     * @param intensityBuilder used to update the Super's message string correctly
     */
    public LaserScan(Header header, float angle_min, float angle_max, float angle_increment, float time_increment, float scan_time, float range_min, float range_max,float[] ranges, JsonArrayBuilder rangeBuilder, float[] intensities, JsonArrayBuilder intensityBuilder){
        super(Json.createObjectBuilder()
                .add(LaserScan.HEADER, header.toJsonObject())
                .add(LaserScan.ANGLE_MIN, angle_min)
                .add(LaserScan.ANGLE_MAX, angle_max)
                .add(LaserScan.ANGLE_INCREMENT, angle_increment)
                .add(LaserScan.TIME_INCREMENT, time_increment)
                .add(LaserScan.SCAN_TIME, scan_time)
                .add(LaserScan.RANGE_MIN, range_min)
                .add(LaserScan.RANGE_MAX, range_max)
                .add(LaserScan.RANGES, rangeBuilder)
                .add(LaserScan.INTENSITIES, intensityBuilder)
                .build(),LaserScan.TYPE);
        this.header=header;
        this.angle_min=angle_min;
        this.angle_max=angle_max;
        this.angle_increment=angle_increment;
        this.time_increment=time_increment;
        this.scan_time=scan_time;
        this.range_min=range_min;
        this.range_max=range_max;
        this.ranges=ranges;
        this.intensities=intensities;
        this.rangeBuilder=rangeBuilder;
        this.intensityBuilder=intensityBuilder;
    }

    public Header getHeader() {
        return header;
    }

    public float getAngle_min() {
        return angle_min;
    }

    public float getAngle_max() {
        return angle_max;
    }

    public float getAngle_increment() {
        return angle_increment;
    }

    public float getTime_increment() {
        return time_increment;
    }

    public float getScan_time() {
        return scan_time;
    }

    public float getRange_min() {
        return range_min;
    }

    public float getRange_max() {
        return range_max;
    }

    public float[] getRanges() {
        return ranges;
    }

    public float[] getIntensities() {
        return intensities;
    }

    @Override
    public LaserScan clone() {
        return new LaserScan(header,  angle_min,  angle_max,  angle_increment,  time_increment,  scan_time,  range_min,  range_max, ranges, rangeBuilder, intensities, intensityBuilder);
    }

    public static LaserScan fromJsonString(String jsonString) {
        // convert to a message
        return LaserScan.fromMessage(new Message(jsonString));
    }

    public static LaserScan fromMessage(Message m) {
        // get it from the JSON object
        return LaserScan.fromJsonObject(m.toJsonObject());
    }

    /**
     * jsonObject to new Laserscan
     * @param jsonObject
     * @return
     */
    public static LaserScan fromJsonObject(JsonObject jsonObject) {
        // check the fields
        try {
            //Gson gson = new Gson();
            //String s = jsonObject.toString();
            //LaserScan laserScan = gson.fromJson(jsonObject.toString(), LaserScan.class);

            Header header = jsonObject.containsKey(LaserScan.HEADER) ? Header.fromJsonObject(jsonObject.getJsonObject(LaserScan.HEADER)) : new Header();
            double angle_min = jsonObject.containsKey(LaserScan.ANGLE_MIN) ? jsonObject.getJsonNumber(LaserScan.ANGLE_MIN).doubleValue() : -3;
            double angle_max = jsonObject.containsKey(LaserScan.ANGLE_MAX) ? jsonObject.getJsonNumber(LaserScan.ANGLE_MAX).doubleValue() : 3;
            double angle_increment = jsonObject.containsKey(LaserScan.ANGLE_INCREMENT) ? jsonObject.getJsonNumber(LaserScan.ANGLE_INCREMENT).doubleValue() : 0.0043;
            double time_increment = jsonObject.containsKey(LaserScan.TIME_INCREMENT) ? jsonObject.getJsonNumber(LaserScan.TIME_INCREMENT).doubleValue() : 0;
            double scan_time = jsonObject.containsKey(LaserScan.SCAN_TIME) ? jsonObject.getJsonNumber(LaserScan.SCAN_TIME).doubleValue() : 0;
            double range_min = jsonObject.containsKey(LaserScan.RANGE_MIN) ? jsonObject.getJsonNumber(LaserScan.RANGE_MIN).doubleValue() : 0;
            double range_max = jsonObject.containsKey(LaserScan.RANGE_MAX) ? jsonObject.getJsonNumber(LaserScan.RANGE_MAX).doubleValue() : 30;

            JsonArrayBuilder jsonRangeBuilder = Json.createArrayBuilder();
            JsonArray rangeArray = jsonObject.getJsonArray(LaserScan.RANGES);
            float[] ranges = new float[rangeArray.size()];
            for(int i = 0; i<rangeArray.size(); i++){
                try{
                    JsonNumber jsonNumber = rangeArray.getJsonNumber(i);
                    ranges[i] = (float) jsonNumber.doubleValue();
                    jsonRangeBuilder.add(jsonNumber);
                }catch (Exception e){
                    ranges[i] = 0;
                    jsonRangeBuilder.add(0f);
                }
            }

            JsonArrayBuilder jsonIntensityBuilder = Json.createArrayBuilder();
            JsonArray intensityArray = jsonObject.getJsonArray(LaserScan.INTENSITIES);
            float[] intensities = new float[intensityArray.size()];
            for(int i = 0; i<intensityArray.size(); i++){
                try{
                    JsonNumber jsonNumber = intensityArray.getJsonNumber(i);
                    intensities[i] = (float) 1000;
                    jsonIntensityBuilder.add(jsonNumber);
                }catch (Exception e){
                    intensities[i] = 0;
                    jsonIntensityBuilder.add(0f);
                }
            }

            
            return new LaserScan(header, (float) angle_min, (float) angle_max, (float) angle_increment, (float) time_increment, (float) scan_time, (float) range_min, (float) range_max, ranges, jsonRangeBuilder, intensities, jsonIntensityBuilder);
        }catch (Exception e){
            e.printStackTrace();
            return new LaserScan();
        }
    }
}