package Engine.graph;

import org.lwjgl.opengl.GL;
import Engine.Window;
import Engine.scene.Scene;

import static org.lwjgl.opengl.GL11.*;

public class Render {

    private GuiRender guiRender;
    private SceneRender sceneRender;
    private SkyBoxRender skyBoxRender;

    public Render(Window window) {
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        sceneRender = new SceneRender();
        guiRender = new GuiRender(window);
        skyBoxRender = new SkyBoxRender();
    }

    public void cleanup() {
        sceneRender.cleanup();
        guiRender.cleanup();
    }

    public void render(Window window, Scene scene) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glViewport(0, 0, window.getWidth(), window.getHeight());

        skyBoxRender.render(scene); // Render the sky box first. This is due to the fact that if we have 3D models in the scene with transparencies we want them to be blended with the skybox (not with a black background).
        sceneRender.render(scene);
        guiRender.render(scene);
    }

    public void resize(int width, int height) {
        guiRender.resize(width, height);
    }
}
