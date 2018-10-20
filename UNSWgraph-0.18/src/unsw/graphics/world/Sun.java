package unsw.graphics.world;

import java.awt.Color;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

import unsw.graphics.geometry.Point3D;

public class Sun implements KeyListener {  
	private float radius;
	private float xPosition;
	private float envAmbInt, envAmbCoeff;
	private float ambValue, specValue;
	private int cycle, daySpeed;
	
	private boolean dayTime, passTime;
	private boolean useSun;
	
    public Sun(float width, int daySpeed) {
    	this.radius = width / 2;
    	this.daySpeed = daySpeed;
    	xPosition = 0f;
    	resetCycle();
    	
    	this.useSun = false;
    	this.dayTime = true;
    	this.passTime = false;
    }
    
    public void setLight(Light light) {
    	if (useSun) {
    		if (dayTime && !passTime) {
    			light.setSunlight();
				resetCycle();
    			passTime = true;
    			xPosition = light.getLightPos().getX();
    			envAmbInt = 0.3f;
    			envAmbCoeff = 1f;
    			ambValue = 0.6f;
    			specValue = 0.6f;
    			
    		} else if (!dayTime && !passTime) {
				resetCycle();
    			passTime = true;
    			xPosition = light.getLightPos().getX() - radius;
    			envAmbInt = 0.1f;
    			envAmbCoeff = 0f;
    			ambValue = 0f;
    			specValue = 0f;
    			
    		} else if (dayTime && passTime) {
    			light.setSunlightShader();
    			light.setLight(new Point3D(xPosition, light.getLightPos().getY(), light.getLightPos().getZ()), 
    					Color.WHITE, new Color(envAmbInt, envAmbInt, envAmbInt), 
    					new Color(envAmbCoeff, envAmbCoeff, 1), new Color(ambValue, ambValue, ambValue), 
    					new Color(specValue, specValue, specValue), 8f);
    			
    			xPosition += getChange(light.getLightPos().getX() + radius, light.getLightPos().getX());
    			
    			envAmbInt = age(envAmbInt, 0.3f, 0.1f, -1);
    			envAmbCoeff = age(envAmbCoeff, 1f, 0f, -1);
    			ambValue = age(ambValue, 0.6f, 0f, -1);
    			specValue = age(specValue, 0.6f, 0f, -1);
    			
    			cycle --;
    			if (cycle <= 0) {
    				passTime = false;
    				dayTime = false;
    			}
    			
    		} else if (!dayTime && passTime) {
    			light.setNightLightShader();
    			light.setLight(new Point3D(xPosition, light.getLightPos().getY(), light.getLightPos().getZ()), 
    					Color.WHITE, new Color(envAmbInt, envAmbInt, envAmbInt), 
    					new Color(envAmbCoeff, envAmbCoeff, 1), new Color(ambValue, ambValue, ambValue), 
    					new Color(specValue, specValue, specValue), 8f);
    			
    			xPosition += getChange(light.getLightPos().getX(), light.getLightPos().getX() - radius);
    			
    			envAmbInt = age(envAmbInt, 0.3f, 0.1f, 1);
    			envAmbCoeff = age(envAmbCoeff, 1f, 0f, 1);
    			ambValue = age(ambValue, 0.6f, 0f, 1);
    			specValue = age(specValue, 0.6f, 0f, 1);
    			cycle --;
    			if (cycle <= 0) {
    				passTime = false;
    				dayTime = true;
    			}
    		}
    	}
    }
    
    private float getChange(float uX, float lX) {
    	return ((uX - lX) / daySpeed);
    }
    
    private float age(float subject, float uX, float lX, float direction) {
    	float agedValue = subject + getChange(uX, lX) * direction;
    	if (agedValue > 1f) {
    		return 1f;
    	} else if (agedValue < 0f) {
    		return 0f;
    	} else {
    		return agedValue;
    	}
    }
    
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
        	if (useSun) {
        		System.out.println("DayNight Cycle activated");
        	} else {
        		System.out.println("DayNight Cycle deactivated");
        	}
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
