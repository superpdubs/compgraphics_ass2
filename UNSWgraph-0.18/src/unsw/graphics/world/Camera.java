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
    private Character player;
    private boolean FirstPersonCam;

    public Camera(World parentWorld) {
        myPos = new Point3D(3, 1, 10);
    	myAngle = 0;
        myScale = 1;
        myWorld = parentWorld;
        player = new Character(new Point3D(3, 0, 10), 180, 3);
        
        FirstPersonCam = false;
    }
    
    public void init(GL3 gl) {
    	player.init(gl);
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
    	if (FirstPersonCam) {
	        CoordFrame3D viewFrame = CoordFrame3D.identity()
	                .scale(1/myScale, 1/myScale, 1/myScale)
	                .rotateY(-myAngle)
	                .translate(-myPos.getX(), -myPos.getY(), -myPos.getZ());
	        Shader.setViewMatrix(gl, viewFrame.getMatrix());
    	} else {
        	float backX = myPos.getX() + ((float) Math.sin(Math.toRadians(myAngle)) * 2);
        	float backZ = myPos.getZ() + ((float) Math.cos(Math.toRadians(myAngle)) * 2);
        	float backY = myPos.getY();
        	
	        CoordFrame3D viewFrame = CoordFrame3D.identity()
	                .scale(1/myScale, 1/myScale, 1/myScale)
	                .rotateY(-myAngle)
	                .translate(-backX, -backY, -backZ);
	        Shader.setViewMatrix(gl, viewFrame.getMatrix());
    		
    	}
    	
    	if (!FirstPersonCam) player.drawCharacter(gl);
    }

    public Point3D getPosition() {
    	return this.myPos;
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
    	float newX, newY, newZ;
    	
        switch(e.getKeyCode()) {
        case KeyEvent.VK_A:
        case KeyEvent.VK_LEFT:
            myAngle += 5;
            player.addRotation(5);
            player.isMoving(0);
            break;
            
        case KeyEvent.VK_D:
        case KeyEvent.VK_RIGHT:
            myAngle -= 5;
            player.addRotation(-5);
            player.isMoving(0);
            break;

        case KeyEvent.VK_S:
        case KeyEvent.VK_DOWN:
        	newX = myPos.getX() + (float) Math.sin(Math.toRadians(myAngle));
        	newZ = myPos.getZ() + (float) Math.cos(Math.toRadians(myAngle));
        	newY = 1 + myWorld.getTerrain().altitude(newX, newZ);
        	
            myPos = new Point3D(newX, newY, newZ);
            player.setPosition(new Point3D(newX, newY - 1, newZ));
            player.isMoving(-1);
            
            System.out.println("Moving Back");
            break;

        case KeyEvent.VK_W:
        case KeyEvent.VK_UP:
        	newX = myPos.getX() - (float) Math.sin(Math.toRadians(myAngle));
        	newZ = myPos.getZ() - (float) Math.cos(Math.toRadians(myAngle));
        	newY = 1 + myWorld.getTerrain().altitude(newX, newZ);
        	
            myPos = new Point3D(newX, newY, newZ);
            player.setPosition(new Point3D(newX, newY - 1, newZ));
            player.isMoving(1);
            
            System.out.println("Moving Forward");
            break;
           
        case KeyEvent.VK_SPACE:
        	FirstPersonCam ^= true;
        	System.out.println(FirstPersonCam);
        	break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

}