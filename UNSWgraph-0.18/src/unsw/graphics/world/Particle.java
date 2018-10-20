package unsw.graphics.world;

import java.util.Random;

public class Particle {
        public float life; // how alive it is
        public float r, g, b;
        public float x, y, z; // position
        public float speedY; // speed in the direction
        private int width;
        private int depth;


        private Random rand = new Random();

        // Constructor
        public Particle(int width, int depth) {
            this.width = width;
            this.depth = depth;
            initiate();
        }

        public void initiate() {
            // Set the initial position
            x = y = z = 0.0f;
            x = rand.nextFloat() * (width - 1);
            z = rand.nextFloat() * (depth - 1);
            // Generate a random speed and direction
            float maxSpeed = 0.1f;
            float speed = 0.02f + (rand.nextFloat() - 0.5f) * maxSpeed;
            float angle = (float) Math.toRadians(rand.nextInt(360));

            speedY = speed * (float) Math.sin(angle);

            r = 0.8f;
            g = 0.8f;
            b = 0.8f;

            // Initially it's fully alive
            life = 1.0f;
        }
}
