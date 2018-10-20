package unsw.graphics.world;

import java.awt.Color;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

import unsw.graphics.geometry.Point3D;

public class Sun implements KeyListener {  
	private float radius;					// The sun travels -radius to +radius in X-axis.
	private float xPosition;
	private float envAmbInt, envAmbCoeff;	//Variables to work with shader.
	private float ambValue, specValue;		//		Will change per iteration.
	private int cycle, daySpeed;			//Speed of the day-night cycle.
	
	private boolean dayTime, passTime;		//Day || Night || Transition Flag
	private boolean useSun;
	
	/**
	 * Create a "moving sun" which generates a day night cycle.
	 * 
	 * @param width	The width of the map. Affects sun travel distance.
	 * @param daySpeed Speed at which the sun moves and light increases / dims.
	 */
    public Sun(float width, int daySpeed) {
    	this.radius = width / 2;
    	this.daySpeed = daySpeed;
    	xPosition = 0f;
    	resetCycle();
    	
    	this.useSun = false;	
    	this.dayTime = true;	//start at day
    	this.passTime = false;	
    }
    
    public void setLight(Light light) {
    	if (useSun) {
    		if (dayTime && !passTime) {		// If day and not transition
    			//Set up light shader variables
    			light.setSunlightShader();
				resetCycle();
    			passTime = true;
    			xPosition = light.getLightPos().getX();
    			envAmbInt = 0.3f;
    			envAmbCoeff = 1f;
    			ambValue = 0.6f;
    			specValue = 0.6f;
    			
    		} else if (!dayTime && !passTime) {	// If night and not transition
    			//Set up light shader variables
    			light.setLightShader();
				resetCycle();
    			passTime = true;
    			xPosition = light.getLightPos().getX() - radius;
    			envAmbInt = 0.1f;
    			envAmbCoeff = 0f;
    			ambValue = 0f;
    			specValue = 0f;
    			
    		} else if (dayTime && passTime) {
    			// set light shader and pass shader variables to shader.
    			light.setLightShader();
    			light.setLight(new Point3D(xPosition, light.getLightPos().getY(), light.getLightPos().getZ()), 
    					Color.WHITE, new Color(envAmbInt, envAmbInt, envAmbInt), 
    					new Color(envAmbCoeff, envAmbCoeff, 1), new Color(ambValue, ambValue, ambValue), 
    					new Color(specValue, specValue, specValue), 8f);
    			
    			//age the variables
    			xPosition += getChange(light.getLightPos().getX() + radius, light.getLightPos().getX());
    			
    			envAmbInt = age(envAmbInt, 0.3f, 0.1f, -1);
    			envAmbCoeff = age(envAmbCoeff, 1f, 0f, -1);
    			ambValue = age(ambValue, 0.6f, 0f, -1);
    			specValue = age(specValue, 0.6f, 0f, -1);
    			
    			//reduce cycle counter by 1. When it reaches 0, a day to night (vice versa) cycle has
    			//been complete, so transition to next phase.
    			cycle --;
    			if (cycle <= 0) {
    				passTime = false;
    				dayTime = false;
    			}
    			
    		} else if (!dayTime && passTime) {
    			// set light shader and pass shader variables to shader.
    			light.setLightShader();
    			light.setLight(new Point3D(xPosition, light.getLightPos().getY(), light.getLightPos().getZ()), 
    					Color.WHITE, new Color(envAmbInt, envAmbInt, envAmbInt), 
    					new Color(envAmbCoeff, envAmbCoeff, 1), new Color(ambValue, ambValue, ambValue), 
    					new Color(specValue, specValue, specValue), 8f);
    			
    			//age the variables
    			xPosition += getChange(light.getLightPos().getX(), light.getLightPos().getX() - radius);
    			
    			envAmbInt = age(envAmbInt, 0.3f, 0.1f, 1);
    			envAmbCoeff = age(envAmbCoeff, 1f, 0f, 1);
    			ambValue = age(ambValue, 0.6f, 0f, 1);
    			specValue = age(specValue, 0.6f, 0f, 1);
    			
    			//reduce cycle counter by 1. When it reaches 0, a day to night (vice versa) cycle has
    			//been complete, so transition to next phase.
    			cycle --;
    			if (cycle <= 0) {
    				passTime = false;
    				dayTime = true;
    			}
    		}
    	}
    }
    
    /**
     * Find the increment value from uX to lX per draw, based on daySpeed.
     * @param uX The destination (higher) value
     * @param lX The source (lower) value
     * @return The increment / draw
     */
    private float getChange(float uX, float lX) {
    	return ((uX - lX) / daySpeed);
    }
    
    /**
     * Age the variables by 1 draw and return the aged variable.
     * 
     * @param subject The variable to be aged
     * @param uX The destination the variable is moving to
     * @param lX The source the variable came from
     * @param direction	The direction the variable is moving.
     * @return
     */
    private float age(float subject, float uX, float lX, float direction) {
    	float agedValue = subject + getChange(uX, lX) * direction;
    	
    	//Normalise the aged value so it stays between 0 <= value <= 1;
    	if (agedValue > 1f) {
    		return 1f;
    	} else if (agedValue < 0f) {
    		return 0f;
    	} else {
    		return agedValue;
    	}
    }
    
    /**
     * Reset counter
     */
    private void resetCycle() {
    	cycle = daySpeed;
    }

    public boolean getToggle() {
    	return useSun;
    }

	@Override
	public void keyPressed(KeyEvent e) {
        switch(e.getKeyCode()) {
        //Night and Day toggle
        case KeyEvent.VK_CLOSE_BRACKET:
        	useSun ^= true;
            break;
            
        case KeyEvent.VK_OPEN_BRACKET:
        	dayTime ^= true;
        	passTime = false;
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
