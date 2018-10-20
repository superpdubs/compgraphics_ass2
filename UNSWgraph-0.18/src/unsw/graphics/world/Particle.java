package unsw.graphics.world;

import java.util.Random;

public class Particle {
        public float life; // how alive it is
        public float r, g, b; // color
        public float x, y, z; // position
        public float speedX, speedY, speedZ; // speed in the direction

        private static float speedYGlobal = 0.1f;
        private final float[][] colors = { // rainbow of 12 colors
                { 1.0f, 0.5f, 0.5f }, { 1.0f, 0.75f, 0.5f },
                { 1.0f, 1.0f, 0.5f }, { 0.75f, 1.0f, 0.5f },
                { 0.5f, 1.0f, 0.5f }, { 0.5f, 1.0f, 0.75f },
                { 0.5f, 1.0f, 1.0f }, { 0.5f, 0.75f, 1.0f },
                { 0.5f, 0.5f, 1.0f }, { 0.75f, 0.5f, 1.0f },
                { 1.0f, 0.5f, 1.0f }, { 1.0f, 0.5f, 0.75f } };

        private Random rand = new Random();

        // Constructor
        public Particle() {
            burst();
        }

        public void burst() {
            // Set the initial position
            x = y = z = 0.0f;

            // Generate a random speed and direction in polar coordinate, then
            // resolve
            // them into x and y.
            float maxSpeed = 0.1f;
            float speed = 0.02f + (rand.nextFloat() - 0.5f) * maxSpeed;
            float angle = (float) Math.toRadians(rand.nextInt(360));

            speedX = speed * (float) Math.cos(angle);
            speedY = speed * (float) Math.sin(angle) + speedYGlobal;
            speedZ = (rand.nextFloat() - 0.5f) * maxSpeed;

            int colorIndex = (int) (((speed - 0.02f) + maxSpeed)
                    / (maxSpeed * 2) * colors.length) % colors.length;
            // Pick a random color
            r = colors[colorIndex][0];
            g = colors[colorIndex][1];
            b = colors[colorIndex][2];

            // Initially it's fully alive
            life = 1.0f;
        }
}
