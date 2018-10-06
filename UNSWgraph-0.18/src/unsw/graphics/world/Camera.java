package unsw.graphics.world;


import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame2D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.geometry.LineStrip2D;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;



/**
 * The camera for the person demo
 *
 * @author malcolmr
 * @author Robert Clifton-Everest
 */
public class Camera implements KeyListener {

    private Point3D myPos;
    private float myAngle;
    private float myScale;
    private World myWorld;

    public Camera(World parentWorld) {
        myPos = new Point3D(3, 0, 10);
    	myAngle = 0;
        myScale = 1;
        myWorld = parentWorld;
    }
    
    public void draw(GL3 gl, CoordFrame3D frame) {
        CoordFrame3D cameraFrame = frame.translate(myPos)
                .rotateY(myAngle)
                .scale(myScale, myScale, myScale);
    }

    /**
     * Set the view transform
     * 
     * Note: this is the inverse of the model transform above
     * 
     * @param gl
     */
    public void setView(GL3 gl) {
        CoordFrame3D viewFrame = CoordFrame3D.identity()
                .scale(1/myScale, 1/myScale, 1/myScale)
                .rotateY(-myAngle)
                .translate(-myPos.getX(), -myPos.getY(), -myPos.getZ());
        Shader.setViewMatrix(gl, viewFrame.getMatrix());
    }

    @Override
    public void keyPressed(KeyEvent e) {
    	float newX, newY, newZ;
    	
        switch(e.getKeyCode()) {
        case KeyEvent.VK_LEFT:
            myAngle += 5;               
            break;
            
        case KeyEvent.VK_RIGHT:
            myAngle -= 5;              
            break;

        case KeyEvent.VK_DOWN:
        	newX = myPos.getX() + (float) Math.sin(Math.toRadians(myAngle));
        	newZ = myPos.getZ() + (float) Math.cos(Math.toRadians(myAngle));
        	newY = myWorld.altitude(newX, newZ);
        	
            myPos = new Point3D(newX, newY, newZ);
            System.out.println("Moving Back");
            break;

        case KeyEvent.VK_UP:
        	newX = myPos.getX() - (float) Math.sin(Math.toRadians(myAngle));
        	newZ = myPos.getZ() - (float) Math.cos(Math.toRadians(myAngle));
        	newY = myWorld.altitude(newX, newZ);
        	
            myPos = new Point3D(newX, newY, newZ);
            System.out.println("Moving Forward");
            break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

}