package robo;

import robocode.*;


/**
 * Record the advanced state of an enemy bot.
 * 
 * @author Mihir Pandya
 * @version 5/14/14
 * 
 * @author Period - 6
 * @author Assignment - AdvancedEnemyBot
 * 
 * @author Sources - None
 */
public class AdvancedEnemyBot extends EnemyBot
{
    /**
     * x coordinate
     */
    private double x;

    /**
     * y coordinate
     */
    private double y;


    /**
     * Advanced Enemy Bot constructer
     */
    public AdvancedEnemyBot()
    {
        reset();
    }


    /**
     * Get current x coordinate
     * 
     * @return x coordinate
     */
    public double getX()
    {

        return x;
    }


    /**
     * Gets current y coordinate
     * 
     * @return y coordinate
     */
    public double getY()
    {
        return y;
    }


    /**
     * Updates bearing, x, y
     * 
     * @param e
     *            scanned robot event
     * @param robot
     *            actual robot
     */
    public void update( ScannedRobotEvent e, Robot robot )
    {
        super.update( e );
        double absBearingDeg = ( robot.getHeading() + e.getBearing() );
        if ( absBearingDeg < 0 )
        {
            absBearingDeg += 360;
        }
        x = robot.getX() + Math.sin( Math.toRadians( absBearingDeg ) )
            * e.getDistance();
        y = robot.getY() + Math.cos( Math.toRadians( absBearingDeg ) )
            * e.getDistance();

    }


    /**
     * Gets future x coordinate
     * 
     * @param when
     *            long time
     * @return future x coordinate
     */
    public double getFutureX( long when )
    {
        return x + Math.sin( Math.toRadians( getHeading() ) ) * getVelocity()
            * when;
    }


    /**
     * Gets future y coordinate.
     * 
     * @param when
     *            long time
     * @return future y coordinate
     */
    public double getFutureY( long when )
    {
        return y + Math.cos( Math.toRadians( getHeading() ) ) * getVelocity()
            * when;
    }


    /**
     * Resets
     * 
     */
    public void reset()
    {
        super.reset();
        x = 0.0;
        y = 0.0;
    }

}