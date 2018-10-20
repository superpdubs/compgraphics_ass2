package unsw.graphics.world;

import java.awt.Color;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

import unsw.graphics.geometry.Point3D;

public class Sun implements KeyListener {  
	private boolean useSun;
	private float radius;
	private float xPosition;
	private float increment;
	private float envAmbInt, envAmbCoeff;
	private float ambValue, specValue;
	private int cycle;
	private int daySpeed;
	
	private boolean dayTime, passTime;
	
    public Sun(float width, int daySpeed) {
    	this.useSun = true;
    	this.radius = width / 2;
    	this.daySpeed = daySpeed;
    	xPosition = 0f;
    	increment = 0.001f;
    	resetCycle();
    	
    	this.dayTime = false;
    	this.passTime = false;
    }
    
    public void setLight(Light light) {
    	if (useSun) {
    		if (dayTime && !passTime) {
    			light.setSunlight();
				resetCycle();
    			passTime = true;
    			xPosition = light.getLightPos().getX();
    			envAmbInt = 0.2f;
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
    				
    			light.setLight(new Point3D(xPosition, light.getLightPos().getY(), light.getLightPos().getZ()), 
    					Color.WHITE, new Color(envAmbInt, envAmbInt, envAmbInt), 
    					new Color(envAmbCoeff, envAmbCoeff, 1), new Color(ambValue, ambValue, ambValue), 
    					new Color(specValue, specValue, specValue), 8f);
    			
    			xPosition += getChange(light.getLightPos().getX() + radius, light.getLightPos().getX());
    			
    			envAmbInt -= getChange(0.2f, 0.1f);
    			envAmbCoeff -= getChange(1f, 0f);
    			ambValue -= getChange(0.6f, 0f);
    			specValue -= getChange(0.6f, 0f);
    			cycle --;
    			if (cycle <= 0) {
    				passTime = false;
    				dayTime = false;
    			}
    			
    		} else if (!dayTime && passTime) {

    			light.setLight(new Point3D(xPosition, light.getLightPos().getY(), light.getLightPos().getZ()), 
    					Color.WHITE, new Color(envAmbInt, envAmbInt, envAmbInt), 
    					new Color(envAmbCoeff, envAmbCoeff, 1), new Color(ambValue, ambValue, ambValue), 
    					new Color(specValue, specValue, specValue), 8f);
    			
    			xPosition += getChange(light.getLightPos().getX(), light.getLightPos().getX() - radius);
    			
    			envAmbInt += getChange(0.2f, 0.1f);
    			envAmbCoeff += getChange(1f, 0f);
    			ambValue += getChange(0.6f, 0f);
    			specValue += getChange(0.6f, 0f);
    			cycle --;
    			if (cycle <= 0) {
    				passTime = false;
    				dayTime = true;
    			}
    		}
    	}
    }
    
    public boolean getToggle() {
    	return useSun;
    }
    
    private void resetCycle() {
    	cycle = daySpeed;
    }
    
    private float getChange(float uX, float lX) {
    	return ((uX - lX) / increment / daySpeed) * increment;
    }

	@Override
	public void keyPressed(KeyEvent e) {		
        switch(e.getKeyCode()) {
        //Night and Day toggle
        case KeyEvent.VK_V:
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
