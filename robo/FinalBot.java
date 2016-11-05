package robo;

import robocode.*;
import robocode.util.Utils;

import java.awt.Color;
import java.awt.geom.Point2D;


/**
 * A modular bot adhering to the RoboPart Interface.
 * 
 * @author Mihir Pandya
 * 
 * @author Period - 6
 * @author Assignment - PartsBot
 * 
 * @author Sources - None
 */
public class FinalBot extends AdvancedRobot
{
    private AdvancedEnemyBot enemy = new AdvancedEnemyBot();

    private RobotPart[] parts = new RobotPart[3]; // make three parts

    private final static int RADAR = 0;

    private final static int GUN = 1;

    private final static int TANK = 2;

    private int tooCloseToWall = 0;

    private int wallMargin = 70;

    private byte radarDirection = 1;

    private byte moveDirection = 1;

    int turnDirection = 1;


    public void run()
    {
        parts[RADAR] = new Radar();
        parts[GUN] = new Gun();
        parts[TANK] = new Tank();

        // initialize each part
        for ( int i = 0; i < parts.length; i++ )
        {
            // behold, the magic of polymorphism
            parts[i].init();
        }

        // iterate through each part, moving them as we go
        for ( int i = 0; true; i = ( i + 1 ) % parts.length )
        {
            // polymorphism galore!
            parts[i].move();
            if ( i == 0 )
                execute();
        }
    }


    public void onScannedRobot( ScannedRobotEvent e )
    {
        Radar radar = (Radar)parts[RADAR];
        if ( radar.shouldTrack( e ) )
            enemy.update( e, this );

        // track if we have no enemy, the one we found is significantly
        // closer, or we scanned the one we've been tracking.
        if ( enemy.none() || e.getDistance() < enemy.getDistance() - 70
            || e.getName().equals( enemy.getName() ) )
        {

            // track him using the NEW update method
            enemy.update( e, this );
        }

    }


    public void onCustomEvent( CustomEvent e )
    {
        if ( e.getCondition().getName().equals( "too_close_to_walls" ) )
        {
            if ( tooCloseToWall <= 0 )
            {
                // if we weren't already dealing with the walls, we are now
                tooCloseToWall += wallMargin;
                /*
                 * -- don't do it this way // switch directions and move away
                 * moveDirection *= -1; setAhead(10000 * moveDirection);
                 */
                setMaxVelocity( 0 ); // stop!!!
            }
        }
    }


    public void onRobotDeath( RobotDeathEvent e )
    {
        Radar radar = (Radar)parts[RADAR];
        if ( radar.wasTracking( e ) )
            enemy.reset();

    }


    public void onHitByBullet( HitByBulletEvent e )
    {
        if ( tooCloseToWall > 0 )
            tooCloseToWall--;

        setMaxVelocity( 600 );
        setAhead( Math.random() * 4000 );
        setMaxVelocity( 0 );

    }


    public void onHitWall( HitWallEvent e )
    {
        out.println( "OUCH! I hit a wall anyway!" );

    }


    public void onHitRobot( HitRobotEvent e )
    {
        tooCloseToWall = 0;
        setAhead( enemy.getDistance() + 5 );
    }


    // ... put normalizeBearing and absoluteBearing methods here
    // computes the absolute bearing between two points
    double absoluteBearing( double x1, double y1, double x2, double y2 )
    {
        double xo = x2 - x1;
        double yo = y2 - y1;
        double hyp = Point2D.distance( x1, y1, x2, y2 );
        double arcSin = Math.toDegrees( Math.asin( xo / hyp ) );
        double bearing = 0;

        if ( xo > 0 && yo > 0 )
        { // both pos: lower-Left
            bearing = arcSin;
        }
        else if ( xo < 0 && yo > 0 )
        { // x neg, y pos: lower-right
            bearing = 360 + arcSin; // arcsin is negative here, actually 360 -
                                    // ang
        }
        else if ( xo > 0 && yo < 0 )
        { // x pos, y neg: upper-left
            bearing = 180 - arcSin;
        }
        else if ( xo < 0 && yo < 0 )
        { // both neg: upper-right
            bearing = 180 - arcSin; // arcsin is negative here, actually 180 +
                                    // ang
        }

        return bearing;
    }


    // normalizes a bearing to between +180 and -180
    double normalizeBearing( double angle )
    {
        while ( angle > 180 )
            angle -= 360;
        while ( angle < -180 )
            angle += 360;
        return angle;
    }


    // ... declare the RobotPart interface and classes that implement it here
    // They will be _inner_ classes.
    public interface RobotPart
    {
        public void init();


        public void move();
    }


    private class Radar implements RobotPart

    {

        public void init()
        {
            setAdjustRadarForGunTurn( true );
        }


        /**
         * Finds, and continuosly tracks a robot (narrow-beam)
         */
        public void move()
        {

            if ( enemy.none() )
            {
                turnRadarRight( 360 );
            }

            // Absolute angle towards target
            double angleToEnemy = getHeadingRadians()
                + Math.toRadians( enemy.getBearing() );

            // Subtract current radar heading to get the turn required to face
            // the enemy, be sure it is normalized
            double radarTurn = Utils.normalRelativeAngle( angleToEnemy
                - getRadarHeadingRadians() );

            // Distance we want to scan from middle of enemy to either side
            // The 36.0 is how many units from the center of the enemy robot it
            // scans.
            double extraTurn = Math.min( Math.atan( 36.0 / enemy.getDistance() ),
                Rules.RADAR_TURN_RATE_RADIANS );

            // Adjust the radar turn so it goes that much further in the
            // direction it is going to turn
            // Basically if we were going to turn it left, turn it even more
            // left, if right, turn more right.
            // This allows us to overshoot our enemy so that we get a good sweep
            // that will not slip.
            radarTurn += ( radarTurn < 0 ? -extraTurn : extraTurn );

            // Turn the radar
            setTurnRadarRightRadians( radarTurn );

        }


        public boolean shouldTrack( ScannedRobotEvent e )

        {

            // track if we have no enemy, the one we found is significantly

            // closer, or we scanned the one we've been tracking.

            return ( enemy.none() || e.getDistance() < enemy.getDistance() - 70 || e.getName()

                .equals( enemy.getName() ) );

        }


        public boolean wasTracking( RobotDeathEvent e )

        {

            return e.getName().equals( enemy.getName() );

        }

    }


    private class Gun implements RobotPart
    {

        public void init()
        {
            setAdjustGunForRobotTurn( true );
            // setAdjustGunForScannerTurn( true );
        }


        public void move()

        {

            if ( enemy.none() )
                return;
            // only does circular if it is close by
            if ( enemy.getDistance() < 200 )
            {

                double oldEnemyHeading = Math.toRadians( enemy.getHeading() );
                // uses 3 unless its energy is lower
                double bulletPower = Math.min( 3.0, getEnergy() );
                // gets THIS robots current values
                double myX = getX();
                double myY = getY();
                // finds how much left to turn to reach the enemy
                double absoluteBearing = getHeadingRadians()
                    + Math.toRadians( enemy.getBearing() );
                // estimates where the enemy robot will be
                double enemyX = getX() + enemy.getDistance()
                    * Math.sin( absoluteBearing );
                double enemyY = getY() + enemy.getDistance()
                    * Math.cos( absoluteBearing );
                double enemyHeading = Math.toRadians( enemy.getHeading() );
                double enemyHeadingChange = enemyHeading - oldEnemyHeading;
                double enemyVelocity = enemy.getVelocity();
                oldEnemyHeading = enemyHeading;

                double deltaTime = 0;
                double battleFieldHeight = getBattleFieldHeight(), battleFieldWidth = getBattleFieldWidth();
                // updates predicted coordinates
                double predictedX = enemyX, predictedY = enemyY;
                while ( ( ++deltaTime ) * ( 20.0 - 3.0 * bulletPower ) < Point2D.Double.distance( myX,
                    myY,
                    predictedX,
                    predictedY ) )
                {
                    // fine edits the predicted coordinates
                    predictedX += Math.sin( enemyHeading ) * enemyVelocity;
                    predictedY += Math.cos( enemyHeading ) * enemyVelocity;
                    enemyHeading += enemyHeadingChange;
                    if ( predictedX < 18.0 || predictedY < 18.0
                        || predictedX > battleFieldWidth - 18.0
                        || predictedY > battleFieldHeight - 18.0 )
                    {

                        predictedX = Math.min( Math.max( 18.0, predictedX ),
                            battleFieldWidth - 18.0 );
                        predictedY = Math.min( Math.max( 18.0, predictedY ),
                            battleFieldHeight - 18.0 );
                        break;
                    }
                }
                // angle to enemy
                double theta = Utils.normalAbsoluteAngle( Math.atan2( predictedX
                    - getX(),
                    predictedY - getY() ) );

                setTurnRadarRightRadians( Utils.normalRelativeAngle( absoluteBearing
                    - getRadarHeadingRadians() ) );
                setTurnGunRightRadians( Utils.normalRelativeAngle( theta
                    - getGunHeadingRadians() ) );
                // determines bullet power
                double firePower = Math.min( 500 / enemy.getDistance(), 3 );
                fire( firePower );
            }

            else
            {
                double bulletPower = Math.min( 3.0, getEnergy() );
                double myX = getX();
                double myY = getY();
                double absoluteBearing = getHeadingRadians()
                    + Math.toRadians( enemy.getBearing() );
                double enemyX = getX() + enemy.getDistance()
                    * Math.sin( absoluteBearing );
                double enemyY = getY() + enemy.getDistance()
                    * Math.cos( absoluteBearing );
                double enemyHeading = Math.toRadians( enemy.getHeading() );
                double enemyVelocity = enemy.getVelocity();

                double deltaTime = 0;
                double battleFieldHeight = getBattleFieldHeight(), battleFieldWidth = getBattleFieldWidth();
                double predictedX = enemyX, predictedY = enemyY;
                while ( ( ++deltaTime ) * ( 20.0 - 3.0 * bulletPower ) < Point2D.Double.distance( myX,
                    myY,
                    predictedX,
                    predictedY ) )
                {
                    predictedX += Math.sin( enemyHeading ) * enemyVelocity;
                    predictedY += Math.cos( enemyHeading ) * enemyVelocity;
                    if ( predictedX < 18.0 || predictedY < 18.0
                        || predictedX > battleFieldWidth - 18.0
                        || predictedY > battleFieldHeight - 18.0 )
                    {
                        predictedX = Math.min( Math.max( 18.0, predictedX ),
                            battleFieldWidth - 18.0 );
                        predictedY = Math.min( Math.max( 18.0, predictedY ),
                            battleFieldHeight - 18.0 );
                        break;
                    }
                }
                double theta = Utils.normalAbsoluteAngle( Math.atan2( predictedX
                    - getX(),
                    predictedY - getY() ) );

                setTurnRadarRightRadians( Utils.normalRelativeAngle( absoluteBearing
                    - getRadarHeadingRadians() ) );
                setTurnGunRightRadians( Utils.normalRelativeAngle( theta
                    - getGunHeadingRadians() ) );
                fire( bulletPower );
            }

        }
    }


    private class Tank implements RobotPart
    {

        public void init()
        {
            setColors( Color.red, Color.yellow, Color.blue );

            addCustomEvent( new Condition( "too_close_to_walls" )
            {
                public boolean test()
                {
                    return (
                    // we're too close to the left wall
                    ( getX() <= wallMargin ||
                    // or we're too close to the right wall
                        getX() >= getBattleFieldWidth() - wallMargin ||
                        // or we're too close to the bottom wall
                        getY() <= wallMargin ||
                    // or we're too close to the top wall
                    getY() >= getBattleFieldHeight() - wallMargin ) );
                }
            } );

            execute();
        }


        public void move()
        {
            /*
             * // always square off our enemy, turning slightly toward him
             * setTurnRight( normalizeBearing( enemy.getBearing() + 90));
             * 
             * // if we're close to the wall, eventually, we'll move away if (
             * tooCloseToWall > 0 ) tooCloseToWall--;
             * 
             * 
             * 
             * if (enemy.getDistance() <= 350) { if ( tooCloseToWall > 0 )
             * tooCloseToWall--;
             * 
             * 
             * 
             * setTurnRight(130); setAhead(2000);
             * 
             * }
             * 
             * if ( getTime() % 75 == 0) { if ( tooCloseToWall > 0 )
             * tooCloseToWall--; setAhead(1500); setMaxVelocity(0);
             * 
             * }
             * 
             * if ( getTime() % Math.random() == 0 ) { if ( tooCloseToWall > 0 )
             * tooCloseToWall--; setMaxVelocity(500);
             * setAhead(1000*Math.random());
             * 
             * 
             * }
             * 
             * if ( getTime() % 30 == 0) { if ( tooCloseToWall > 0 )
             * tooCloseToWall--;
             * 
             * setMaxVelocity(0); }
             * 
             * if ( getTime() % Math.random() == 0 ) { if ( tooCloseToWall > 0 )
             * tooCloseToWall--; setMaxVelocity(0);
             * 
             * 
             * }
             * 
             * // switch directions if we've stopped // (also handles moving
             * away from the wall if too close) if ( getVelocity() == 0 ) {
             * setMaxVelocity( Math.random()*100 ); // 200 moveDirection *= -1;
             * setAhead( 20000 * Math.random() * moveDirection ); }
             * 
             * 
             * }
             */
            // always square off our enemy, turning slightly toward him
            setTurnRight( normalizeBearing( enemy.getBearing() + 90
                - ( 15 * moveDirection ) ) );

            // if we're close to the wall, eventually, we'll move away
            if ( tooCloseToWall > 0 )
                tooCloseToWall--;

            // switch directions if we've stopped
            // (also handles moving away from the wall if too close)
            if ( getVelocity() == 0 )
            {
                setMaxVelocity( 8 );
                moveDirection *= -1;
                setAhead( 10000 * moveDirection );
            }

        }
    }
}