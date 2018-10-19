package unsw.graphics.world;

import java.awt.Color;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;

import unsw.graphics.ColorBuffer;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Point3DBuffer;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.world.Particle;

/**
 * Displays fireworks using a particle system. Taken from NeHe Lesson #19a:
 * Fireworks
 *
 * @author Robert Clifton-Everest
 */
public class Rain implements KeyListener {

    private static final int MAX_PARTICLES = 1000; // max number of particles
    private Particle[] particles = new Particle[MAX_PARTICLES];

    // Set when the particles first burst
    private boolean burst = false;

    // Pull forces in each direction
    private static float gravityY = -0.0008f; // gravity

    // Initial speed for all the particles
    private static float speedYGlobal = 0.1f;

    // Texture applied over the shape
    private Texture texture;
    private String textureFileName = "res/textures/star.png";
    private String textureExt = "png";

    private Point3DBuffer positions;
    private ColorBuffer colors;

    private int positionsName;
    private int colorsName;

    private Shader shader;


    public void init(GL3 gl) {
        //getWindow().addKeyListener(this);

        // Setup the particle shader


        // Creates an additive blend, which looks spectacular on a black
        // background
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);

        // Disable depth testing to get a nice composition
        gl.glDisable(GL.GL_DEPTH_TEST);

        // Load the texture image
        texture = new Texture(gl, textureFileName, textureExt, false);

        // Initialize the particles
        for (int i = 0; i < MAX_PARTICLES; i++) {
            particles[i] = new Particle();
        }

        // Allocate the buffers
        positions = new Point3DBuffer(MAX_PARTICLES);
        colors = new ColorBuffer(MAX_PARTICLES);

        int[] names = new int[3];
        gl.glGenBuffers(3, names, 0);

        positionsName = names[0];
        colorsName = names[1];

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, positionsName);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, MAX_PARTICLES * 3 * Float.BYTES,
                null, GL.GL_DYNAMIC_DRAW);
        gl.glVertexAttribPointer(Shader.POSITION, 3, GL.GL_FLOAT, false, 0, 0);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, colorsName);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, MAX_PARTICLES * 4 * Float.BYTES,
                null, GL.GL_DYNAMIC_DRAW);
        gl.glVertexAttribPointer(Shader.COLOR, 4, GL.GL_FLOAT, false, 0, 0);

        // Set the point size
        gl.glPointSize(50);
    }

    public void display(GL3 gl) {
        Shader.setPenColor(gl, Color.WHITE);

        // Update the buffers
        for (int i = 0; i < MAX_PARTICLES; i++) {
            positions.put(i, particles[i].x, particles[i].y, particles[i].z);
            colors.put(i, particles[i].r, particles[i].g, particles[i].b,
                    particles[i].life);
        }

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, positionsName);
        gl.glBufferSubData(GL.GL_ARRAY_BUFFER, 0,
                MAX_PARTICLES * 3 * Float.BYTES, positions.getBuffer());

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, colorsName);
        gl.glBufferSubData(GL.GL_ARRAY_BUFFER, 0,
                MAX_PARTICLES * 4 * Float.BYTES, colors.getBuffer());

        // Draw the particles
        Shader.setInt(gl, "tex", 0);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getId());

        CoordFrame3D frame = CoordFrame3D.identity().translate(0, 1, -40);
        Shader.setModelMatrix(gl, frame.getMatrix());
        gl.glDrawArrays(GL.GL_POINTS, 0, particles.length);

        // Update the particles
        for (int i = 0; i < MAX_PARTICLES; i++) {
            // Move the particle
            particles[i].x += particles[i].speedX;
            particles[i].y += particles[i].speedY;
            particles[i].z += particles[i].speedZ;

            // Apply the gravity force on y-axis
            particles[i].speedY += gravityY;

            // Slowly kill it
            particles[i].life -= 0.002;

            if (burst) {
                particles[i].burst();
            }

        }
        if (burst)
            burst = false;

    }

    public void destroy(GL3 gl) {
        gl.glDeleteBuffers(2, new int[] { positionsName, colorsName }, 0);
        texture.destroy(gl);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_T:
                if (!burst)
                    burst = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
