package Engine;

import com.jme3.bullet.collision.shapes.*;
import com.jme3.bullet.objects.*;
import com.jme3.math.*;
import com.jme3.system.NativeLibraryLoader;
import com.jme3.bullet.*;

import java.io.File;

public class PhysicsManager
{
    public static float PHYSICS_TIMESTEP = 0.02f;

    private static PhysicsSpace physicsSpace;

    //! DEMO!!!
    public static PhysicsRigidBody ball;

    public static PhysicsSpace getPhysicsSpace() {
        return physicsSpace;
    }

    public static void initPhysics()
    {
        // Load a native library from Natives directory.
        boolean dist = true; // use distribution filenames
        File downloadDirectory = new File("natives");
        String buildType = "Debug";
        String flavor = "Sp";
        NativeLibraryLoader.loadLibbulletjme(
                dist, downloadDirectory, buildType, flavor);

        physicsSpace = createSpace();
        populateSpace();

        //Vector3f location = new Vector3f();
        //for (int iteration = 0; iteration < 50; ++iteration) {
            //updatePhysics(PHYSICS_TIMESTEP);

            //ball.getPhysicsLocation(location);
            //System.out.println(location);
        //}
    }

    /**
     * Populate the PhysicsSpace. Invoked once during initialization.
     */
    //! DEMO!!!
    private static void populateSpace() {
        // Add a static horizontal plane at y=-1.
        float groundY = -1f;
        Plane plane = new Plane(Vector3f.UNIT_Y, groundY);
        CollisionShape planeShape = new PlaneCollisionShape(plane);
        float mass = PhysicsBody.massForStatic;
        PhysicsRigidBody floor = new PhysicsRigidBody(planeShape, mass);
        physicsSpace.addCollisionObject(floor);

        // Add a sphere-shaped, dynamic, rigid body at the origin.
        float radius = 0.f;
        CollisionShape ballShape = new SphereCollisionShape(radius);
        mass = 1f;
        ball = new PhysicsRigidBody(ballShape, mass);
        physicsSpace.addCollisionObject(ball);
    }

    /**
     * Create the PhysicsSpace. Invoked once during initialization.
     *
     * @return a new instance
     */
    private static PhysicsSpace createSpace() {
        PhysicsSpace result
                = new PhysicsSpace(PhysicsSpace.BroadphaseType.DBVT);
        return result;
    }

    /**
     * Advance the physics simulation by the specified amount.
     *
     * @param intervalSeconds the amount of time to simulate (in seconds, &ge;0)
     */
    public static void updatePhysics(float intervalSeconds) {
        int maxSteps = 0; // for a single step of the specified duration
        physicsSpace.update(intervalSeconds, maxSteps);
    }
}
