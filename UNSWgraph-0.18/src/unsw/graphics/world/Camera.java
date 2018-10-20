package unsw.graphics.world;


import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.geometry.Point3D;

/**
 * Camera object which moves around the plane. Controls
 * player character.
 *
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

    public CoordFrame3D getViewFrame() {
        CoordFrame3D viewFrame = CoordFrame3D.identity()
                .scale(1/myScale, 1/myScale, 1/myScale)
                .rotateY(-myAngle)
                .translate(-myPos.getX(), -myPos.getY(), -myPos.getZ());

        return viewFrame;
    }
    
    public Point3D getPosition() {
    	return this.myPos;
    }

    /**
     * Set the view transform. Take a step back if Third Person Camera is active.
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
    	
    	//Draw character only if third person view.
    	if (!FirstPersonCam) player.drawCharacter(gl);
    }
    
    public void destroy(GL3 gl) {
    	player.destroy(gl);
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
    	float newX, newY, newZ;
    	
        switch(e.getKeyCode()) {
        case KeyEvent.VK_A:
        case KeyEvent.VK_LEFT:
            myAngle += 5;
            player.addRotation(5);
            player.isMoving(0); // Notify player is moving (no direction change).
            break;
            
        case KeyEvent.VK_D:
        case KeyEvent.VK_RIGHT:
            myAngle -= 5;
            player.addRotation(-5);
            player.isMoving(0); // Notify player is moving (no direction change).
            break;

        case KeyEvent.VK_S:
        case KeyEvent.VK_DOWN:
        	newX = myPos.getX() + (float) Math.sin(Math.toRadians(myAngle));
        	newZ = myPos.getZ() + (float) Math.cos(Math.toRadians(myAngle));
        	newY = 1 + myWorld.getTerrain().altitude(newX, newZ);	// Make it so camera isn't clipping into ground.
        	
            myPos = new Point3D(newX, newY, newZ);
            player.setPosition(new Point3D(newX, newY - 1, newZ));
            player.isMoving(-1);	// Notify player moving backwards.
            break;

        case KeyEvent.VK_W:
        case KeyEvent.VK_UP:
        	newX = myPos.getX() - (float) Math.sin(Math.toRadians(myAngle));
        	newZ = myPos.getZ() - (float) Math.cos(Math.toRadians(myAngle));
        	newY = 1 + myWorld.getTerrain().altitude(newX, newZ);	// Make it so camera isn't clipping into ground.
        	
            myPos = new Point3D(newX, newY, newZ);
            player.setPosition(new Point3D(newX, newY - 1, newZ));
            player.isMoving(1);	// Notify player moving forwards.
            break;
        
       case KeyEvent.VK_F:
        	player.isSlashing();	// Notify player to trigger animation.
        	break;        
           
        case KeyEvent.VK_SPACE:
        	FirstPersonCam ^= true;
        	player.resetAnimation();
        	break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

}