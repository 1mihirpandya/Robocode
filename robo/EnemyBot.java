package robo;

import robocode.*;


/**
 * Record the state of an enemy bot.
 * 
 * @author Mihir Pandya
 * @version 5/12/14
 * 
 * @author Period - 6
 * @author Assignment - EnemyBot
 * 
 * @author Sources - None
 */
public class EnemyBot
{
    /**
     * Bearing of Robot
     */
    private double bearing;

    /**
     * Distance from robot
     */
    private double distance;

    /**
     * Robot energy
     */
    private double energy;

    /**
     * Direction of robot.
     */
    private double heading;

    /**
     * Speed of robot
     */
    private double velocity;

    /**
     * Name of robot
     */
    private String name;


    /**
     * Constructor
     */
    public EnemyBot()
    {
        reset();
    }


    /**
     * Get Bearing of Robot
     * 
     * @return double bearing
     */
    public double getBearing()
    {

        return bearing; // Fix this!!
    }


    /**
     * Get Distance from other robot
     * 
     * @return double distance
     */
    public double getDistance()
    {

        return distance; // Fix this!!
    }


    /**
     * Get Energy of Robot
     * 
     * @return double energy
     */
    public double getEnergy()
    {

        return energy; // Fix this!!
    }


    /**
     * Get Heading of Robot
     * 
     * @return double heading
     */
    public double getHeading()
    {

        return heading; // Fix this!!
    }


    /**
     * Get Velocity of Robot
     * 
     * @return double velocity
     */
    public double getVelocity()
    {

        return velocity; // Fix this!!
    }


    /**
     * Get Name of Robot
     * 
     * @return String name
     */
    public String getName()
    {

        return name;
    }


    /**
     * Update info
     * 
     * @param srEvt
     *            when scanned robot
     */
    public void update( ScannedRobotEvent srEvt )
    {
        bearing = srEvt.getBearing();
        distance = srEvt.getDistance();
        energy = srEvt.getEnergy();
        heading = srEvt.getHeading();
        velocity = srEvt.getVelocity();
        name = srEvt.getName();
    }


    /**
     * Reset info
     */
    public void reset()
    {
        bearing = 0.0;
        distance = 0.0;
        energy = 0.0;
        heading = 0.0;
        velocity = 0.0;
        name = "";

    }


    /**
     * No Name
     * 
     * @return no name
     */
    public boolean none()
    {
        return ( name.equals( "" ) );
    }
}