package Game;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import org.joml.Math;
import org.lwjglx.debug.Log;
import org.tinylog.Logger;
import org.tinylog.core.LogEntry;
import Engine.*;
import Engine.Window;
import Engine.graph.*;
import Engine.scene.*;
import Engine.scene.lights.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import org.joml.*;

import static org.lwjgl.glfw.GLFW.*;

public class Main implements IAppLogic {
    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVEMENT_SPEED = 0.005f;
    private static final int NUM_CHUNKS = 4;

    private Entity[][] terrainEntities;
    private Entity cubeEntity;
    private Entity pointEntity;
    private Entity spotEntity;
    private Entity dirEntity;

    private Vector4f displInc = new Vector4f();
    private float rotation;
    private LightControls lightControls;

    public Main() {
    }

    public static void main(String[] args) {
        Logger.info("Hello, world!");
        Logger.info( "\n-----LOGGER SETTINGS-----" +
                "\nisDebugEnabled: " + Logger.isDebugEnabled() +
                        "\nisErrorEnabled: " + Logger.isErrorEnabled() +
                "\nisInfoEnabled: " + Logger.isInfoEnabled() +
                        "\nisTraceEnabled: " + Logger.isTraceEnabled() +
                        "\nisWarnEnabled: " + Logger.isWarnEnabled() +
                    "\n--------------------------"
        );
        Main main = new Main();
        Window.WindowOptions windowOptions = new Window.WindowOptions();
        windowOptions.height = 600;
        windowOptions.width = 800;
        Logger.info("Starting engine...");
        Engine gameEng = new Engine("game engine thingy >:3", windowOptions, main);
        gameEng.start();
    }
/*
    @Override
    public void drawGui() {
        ImGui.newFrame();
        ImGui.setNextWindowPos(0, 0, ImGuiCond.Always);
        ImGui.showDemoWindow();
        ImGui.endFrame();
        ImGui.render();
    }
 */

    /*
    @Override
    public boolean handleGuiInput(Scene scene, Window window) {
        ImGuiIO imGuiIO = ImGui.getIO();
        MouseInput mouseInput = window.getMouseInput();
        Vector2f mousePos = mouseInput.getCurrentPos();
        imGuiIO.setMousePos(mousePos.x, mousePos.y);
        imGuiIO.setMouseDown(0, mouseInput.isLeftButtonPressed());
        imGuiIO.setMouseDown(1, mouseInput.isRightButtonPressed());

        return imGuiIO.getWantCaptureMouse() || imGuiIO.getWantCaptureKeyboard();
    }
     */

    @Override
    public void cleanup() {
        // Nothing to be done yet
        Logger.debug("Game; cleanup.");
    }

    public void init(Window window, Scene scene, Render render) {
        Logger.info("Starting game init...");

        String quadModelId = "quad-model";
        Model quadModel = ModelLoader.loadModel("quad-model", "resources/models/quad/quad.obj",
                scene.getTextureCache());
        scene.addModel(quadModel);

        Model cubeModel = ModelLoader.loadModel("cube-model", "resources/models/cube/cube.obj",
                scene.getTextureCache());
        scene.addModel(cubeModel);

        Model coneModel = ModelLoader.loadModel("cone-model", "resources/models/cone/cone.obj",
                scene.getTextureCache());
        scene.addModel(coneModel);

        cubeEntity = new Entity("cube-entity", cubeModel.getId());
        cubeEntity.setPosition(0, 0, -2);
        scene.addEntity(cubeEntity);
//
//        pointEntity = new Entity("pointEntity-entity", coneModel.getId());
//        pointEntity.setPosition(0, 0, -2);
//        scene.addEntity(pointEntity);
//        spotEntity = new Entity("spotEntity-entity", coneModel.getId());
//        spotEntity.setPosition(0, 0, -2);
//        scene.addEntity(spotEntity);
//        dirEntity = new Entity("dirEntity-entity", coneModel.getId());
//        dirEntity.setPosition(0, 0, -2);
//        scene.addEntity(dirEntity);


        int numRows = NUM_CHUNKS * 2 + 1;
        int numCols = numRows;
        terrainEntities = new Entity[numRows][numCols];
        for (int j = 0; j < numRows; j++) {
            for (int i = 0; i < numCols; i++) {
                Entity entity = new Entity("TERRAIN_" + j + "_" + i, quadModelId);
                terrainEntities[j][i] = entity;
                scene.addEntity(entity);
            }
        }


        SceneLights sceneLights = new SceneLights();
        sceneLights.getAmbientLight().setIntensity(0.2f);
        scene.setSceneLights(sceneLights);
        sceneLights.getPointLights().add(new PointLight(new Vector3f(1, 1, 1),
                new Vector3f(0, 0, -1.4f), 1.0f));
//
//        Vector3f coneDir = new Vector3f(0, 0, -1);
//        sceneLights.getSpotLights().add(new SpotLight(new PointLight(new Vector3f(1, 1, 1),
//                new Vector3f(0, 0, -1.4f), 0.0f), coneDir, 140.0f));
//
//        lightControls = new LightControls(scene);
//        scene.setGuiInstance(lightControls);

        if (true){ // skybox toggle
            SkyBox skyBox = new SkyBox("resources/models/skybox/skybox.obj", scene.getTextureCache());
            skyBox.getSkyBoxEntity().setScale(100);
            scene.setSkyBox(skyBox);
        }

        //scene.setFog(new Fog(true, new Vector3f(0.5f, 0.5f, 0.5f), 0.95f)); // make sure fog is the same color as clear color, or else without a skybox it will look weird.

        scene.getCamera().moveUp(0.1f);

        // move model for easy viewing.
        //cubeEntity.setRotation(0, 1, 0, 80);
        cubeEntity.setPosition(0, 0.0f, -0.7f);
        cubeEntity.updateModelMatrix(); // idk why we need this, but we do :3

        String wallNoNormalsModelId = "quad-no-normals-model";
        Model quadModelNoNormals = ModelLoader.loadModel(wallNoNormalsModelId, "resources/models/wall/wall_nonormals.obj",
                scene.getTextureCache());
        scene.addModel(quadModelNoNormals);

        Entity wallLeftEntity = new Entity("wallLeftEntity", wallNoNormalsModelId);
        wallLeftEntity.setPosition(-3f, 0, 0);
        wallLeftEntity.setScale(2.0f);
        wallLeftEntity.updateModelMatrix();
        scene.addEntity(wallLeftEntity);

        String wallModelId = "quad-model-normals";
        Model quadModel2 = ModelLoader.loadModel(wallModelId, "resources/models/wall/wall.obj",
                scene.getTextureCache());
        scene.addModel(quadModel2);

        Entity wallRightEntity = new Entity("wallRightEntity", wallModelId);
        wallRightEntity.setPosition(3f, 0, 0);
        wallRightEntity.setScale(2.0f);
        wallRightEntity.updateModelMatrix();
        scene.addEntity(wallRightEntity);

        DirLight dirLight = sceneLights.getDirLight();
        dirLight.setPosition(1, 1, 0);
        dirLight.setIntensity(1.0f);
        scene.setSceneLights(sceneLights);
    }

    public void input(Window window, Scene scene, long diffTimeMillis, boolean inputConsumed) {
        if (inputConsumed) {
            return;
        }
        float move = diffTimeMillis * MOVEMENT_SPEED;
        Camera camera = scene.getCamera();
        if (window.isKeyPressed(GLFW_KEY_W)) {
            camera.moveForward(move);
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            camera.moveBackwards(move);
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            camera.moveLeft(move);
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            camera.moveRight(move);
        }
        if (window.isKeyPressed(GLFW_KEY_UP)) {
            camera.moveUp(move);
        } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
            camera.moveDown(move);
        }

        MouseInput mouseInput = window.getMouseInput();
        if (mouseInput.isRightButtonPressed()) {
            Vector2f displVec = mouseInput.getDisplVec();
            camera.addRotation((float) Math.toRadians(-displVec.x * MOUSE_SENSITIVITY),
                    (float) Math.toRadians(-displVec.y * MOUSE_SENSITIVITY));
        }
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {
        /*
        rotation += 1.5;
        if (rotation > 360) {
            rotation = 0;
        }
        cubeEntity.setRotation(1, 1, 1, (float) Math.toRadians(rotation));
         */
        //cubeEntity.updateModelMatrix();

        //TODO: There are better ways to do this >w<
//        SceneLights sceneLights = scene.getSceneLights();
//        SpotLight spotLight = sceneLights.getSpotLights().get(0);
//        DirLight dirLight = sceneLights.getDirLight();
//        PointLight pointLight = sceneLights.getPointLights().get(0);
//        spotEntity.setPosition(spotLight.getPointLight().getPosition().x, spotLight.getPointLight().getPosition().y, spotLight.getPointLight().getPosition().z);
//        dirEntity.setPosition(dirLight.getDirection().x, dirLight.getDirection().y, dirLight.getDirection().z);
//        pointEntity.setPosition(pointLight.getPosition().x, pointLight.getPosition().y, pointLight.getPosition().z);
//        //spotEntity.setScale(0.2f);
//        //dirEntity.setScale(0.2f);
//        //pointEntity.setScale(0.2f);
//        //spotLight.getPointLight().getPosition()
//        spotEntity.updateModelMatrix();
//        dirEntity.updateModelMatrix();
//        pointEntity.updateModelMatrix();
    }
}
