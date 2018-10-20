package unsw.graphics.world;

import java.awt.Color;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL3;

import unsw.graphics.geometry.Point3D;

public class Sun implements KeyListener {  
	private Light light;
	private boolean useSun;
	
    public Sun() {
    	this.useSun = true;
    }
    
    public void setLight(Light light) {
    	if (useSun) {
    		//Point3D lightPos, Color lightInt, Color AmbInt, Color AmbCoeff, Color DiffCoeff, Color SpecCoeff, float PhongExp
    		light.setLight(light.getLightPos(), Color.WHITE, Color.WHITE, Color.RED, Color.RED, Color.RED, 8f);
    	}
    }
    
    public boolean getToggle() {
    	return useSun;
    }

	@Override
	public void keyPressed(KeyEvent e) {		
        switch(e.getKeyCode()) {
        //Night and Day toggle
        case KeyEvent.VK_V:
        	useSun ^= true;
            break;
	        
	    default:
	    	break;
	    }
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
