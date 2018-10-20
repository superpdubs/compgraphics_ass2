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
import unsw.graphics.geometry.Point3D;

import static com.jogamp.opengl.GL.GL_BLEND;

/**
 * Rain System (Extension Part)
 * Uses a particle system, and produces rain with a given particle texture
 * Use t to produce rain.
 *
 */
public class Rain implements KeyListener {

    private int MAX_PARTICLES; // max number of particles
    private Particle[] particles;

    private boolean rain = false;

    private static float gravityY = -0.0008f; // gravity

    // Texture applied over the shape
    private Texture texture;
    private String textureFileName = "res/textures/rain.png";
    private String textureExt = "png";

    private Point3DBuffer positions;
    private ColorBuffer colors;

    private int positionsName;
    private int colorsName;

    private int width;
    private int depth;

    private Shader shader;

    public Rain(int width, int depth) {
        this.width = width;
        this.depth = depth;
        MAX_PARTICLES = width * depth * 10;
        if (MAX_PARTICLES > 1000) MAX_PARTICLES = 1000;
        particles = new Particle[MAX_PARTICLES];
    }


    public void init(GL3 gl) {
        shader = new Shader(gl, "shaders/vertex_particle.glsl",
                "shaders/fragment_particle.glsl");
        shader.use(gl);

        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

        // Load the texture image
        texture = new Texture(gl, textureFileName, textureExt, false);

        // Initialize the particles
        for (int i = 0; i < MAX_PARTICLES; i++) {
            particles[i] = new Particle(width, depth);
        }

        // Allocate the buffers
        positions = new Point3DBuffer(MAX_PARTICLES);
        colors = new ColorBuffer(MAX_PARTICLES);

        int[] names = new int[3];
        gl.glGenBuffers(3, names, 0);

        positionsName = names[0];
        colorsName = names[1];

        // Set the point size
        gl.glPointSize(50);
    }


    public void display(GL3 gl, CoordFrame3D frames) {
        // Setup the particle shader
        if (rain == false) return;
        gl.glEnable(GL_BLEND);

        // Disable depth testing to get a nice composition
        gl.glDisable(GL.GL_DEPTH_TEST);

        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        shader.use(gl);

        Shader.setPenColor(gl, Color.WHITE);

        //Enable Dynamic drawing for all particles
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, positionsName);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, MAX_PARTICLES * 3 * Float.BYTES,
                null, GL.GL_DYNAMIC_DRAW);
        gl.glVertexAttribPointer(Shader.POSITION, 3, GL.GL_FLOAT, false, 0, 0);

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, colorsName);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, MAX_PARTICLES * 4 * Float.BYTES,
                null, GL.GL_DYNAMIC_DRAW);
        gl.glVertexAttribPointer(Shader.COLOR, 4, GL.GL_FLOAT, false, 0, 0);

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

        CoordFrame3D frame = CoordFrame3D.identity().translate(0, 10, 0);
        Shader.setModelMatrix(gl, frame.getMatrix());
        Shader.setViewMatrix(gl, frames.getMatrix());
        gl.glDrawArrays(GL.GL_POINTS, 0, particles.length);

        // Update the particles
        for (int i = 0; i < MAX_PARTICLES; i++) {
            // Move the particle
            particles[i].y += particles[i].speedY;

            // Apply the gravity force on y-axis
            particles[i].speedY += gravityY;

            // Slowly kill it
            particles[i].life -= 0.0002;
            Point3D p = frame.transform(new Point3D(particles[i].x, particles[i].y, particles[i].z));

            //Deal with particle life and reset particle based on position
            if (p.getY() < 0) particles[i].life = 0;
            if (particles[i].life == 0) {
                particles[i].life = 1.0f;
                particles[i].y = 0;
                particles[i].speedY = -0.04f;
            }

        }
        gl.glDisable(GL_BLEND);
        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    public void destroy(GL3 gl) {
        gl.glDeleteBuffers(2, new int[] { positionsName, colorsName }, 0);
        texture.destroy(gl);
        shader.destroy(gl);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_BACK_SLASH:
                if (!rain) {
                    rain = true;
                } else {
                    rain = false;
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
