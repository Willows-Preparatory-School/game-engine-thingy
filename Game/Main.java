package Game;

import Engine.sound.SoundBuffer;
import Engine.sound.SoundListener;
import Engine.sound.SoundManager;
import Engine.sound.SoundSource;
import org.joml.Math;
import org.lwjgl.openal.AL11;
import org.tinylog.Logger;
import Engine.*;
import Engine.Window;
import Engine.graph.*;
import Engine.scene.*;
import Engine.scene.lights.*;

import java.util.Arrays;

import org.joml.*;

import static org.lwjgl.glfw.GLFW.*;

public class Main implements IAppLogic
{
    private static final float MOUSE_SENSITIVITY = 0.1f;
    private static final float MOVEMENT_SPEED = 0.005f;
    private static final int NUM_CHUNKS = 4;

    private Entity[][] terrainEntities;
    private Entity cubeEntity;
    private Entity ospreyEntity;
    private Entity computerEntity;
    private Entity pointEntity;
    private Entity spotEntity;
    private Entity dirEntity;
    private AnimationData animationData;

    private Vector4f displInc = new Vector4f();
    private float rotation;
    private float lightAngle;
    private LightControls lightControls;
    private SoundSource playerSoundSource;
    private SoundManager soundMgr;

    // Config
    private static boolean noSound = false;

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
        //windowOptions.fps = 60;
        windowOptions.antiAliasing = false;

        System.out.println("INFO: Game; running with args: " + Arrays.toString(args));

        // No sound mode.
        if(Arrays.asList(args).contains("-nosound"))
        {
            noSound = true;
            System.out.println("Running in nosound mode... No sound will be played.");
        }

        // Force disable sound.
        noSound = true;

        Logger.info("Starting engine...");
        Engine gameEng = new Engine("game engine thingy >:3", windowOptions, args, main);
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
    public void cleanup()
    {
        Logger.debug("Game; cleanup.");
        if(!noSound)
            soundMgr.cleanup(); // Soundmgr cleanup.
    }

    public void init(Window window, Scene scene, Render render)
    {
        Logger.info("Starting game init...");

        /*
        String terrainModelId = "terrain";
        Model terrainModel = ModelLoader.loadModel(terrainModelId, "resources/models/terrain/terrain.obj",
                scene.getTextureCache(), scene.getMaterialCache(), false);
        scene.addModel(terrainModel);
        Entity terrainEntity = new Entity("terrainEntity", terrainModelId);
        terrainEntity.setScale(100.0f);
        terrainEntity.updateModelMatrix();
        scene.addEntity(terrainEntity);

        Model cubeModel = ModelLoader.loadModel("cube-model", "resources/models/computer/computer.obj",
                scene.getTextureCache(), scene.getMaterialCache(), false);
        //scene.addModel(cubeModel);

        //Model coneModel = ModelLoader.loadModel("cone-model", "resources/models/cone/cone.obj",
        //        scene.getTextureCache(), scene.getMaterialCache(), false);
        //scene.addModel(coneModel);

        Model ospreyModel = ModelLoader.loadModel("osprey-model",
                "resources/models/osprey/ospreyt.mdl",
                scene.getTextureCache(), scene.getMaterialCache(), false);
        //scene.addModel(ospreyModel);

        //Model computerModel = ModelLoader.loadModel("computer-model",
                //"resources/models/box/box2.obj",
                //scene.getTextureCache(), false);
        //scene.addModel(computerModel);

        cubeEntity = new Entity("cube-entity", cubeModel.getId());
        cubeEntity.setPosition(0, 0, -2);
        //scene.addEntity(cubeEntity);

        // move model for easy viewing.
        //cubeEntity.setRotation(0, 1, 0, 80);
        cubeEntity.setPosition(0, 0.0f, -0.7f);
        cubeEntity.updateModelMatrix(); // idk why we need this, but we do :3

        ospreyEntity = new Entity("osprey-entity", ospreyModel.getId());
        ospreyEntity.setPosition(0, 3, 0); //-8, 3, 2
        //animationData = new AnimationData(ospreyModel.getAnimationList().get(0));
        //testEntity.setAnimationData(animationData);
        //testEntity.updateModelMatrix();
        //scene.addEntity(ospreyEntity);


        //! IMPORTANT
        render.setupData(scene);

        //computerEntity = new Entity("computer-entity", computerModel.getId());
        //computerEntity.setPosition(-3f, 0.5f, 0);
        //scene.addEntity(computerEntity);
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

        if (false){ // skybox toggle
            SkyBox skyBox = new SkyBox("resources/models/skybox/skybox.obj",
                    scene.getTextureCache(), scene.getMaterialCache());
            skyBox.getSkyBoxEntity().setScale(100);
            scene.setSkyBox(skyBox);
        }

        //scene.setFog(new Fog(true, new Vector3f(0.5f, 0.5f, 0.5f), 0.95f)); // make sure fog is the same color as clear color, or else without a skybox it will look weird.

        scene.getCamera().moveUp(0.1f);

        // move model for easy viewing.
        //cubeEntity.setRotation(0, 1, 0, 80);
        cubeEntity.setPosition(0, 0.0f, -0.7f);
        cubeEntity.updateModelMatrix(); // idk why we need this, but we do :3

        ospreyEntity.setScale(0.01f);
        ospreyEntity.updateModelMatrix();

        String wallNoNormalsModelId = "quad-no-normals-model";
        Model quadModelNoNormals = ModelLoader.loadModel(wallNoNormalsModelId, "resources/models/wall/wall_nonormals.obj",
                scene.getTextureCache(), scene.getMaterialCache(), false);
        scene.addModel(quadModelNoNormals);

        Entity wallLeftEntity = new Entity("wallLeftEntity", wallNoNormalsModelId);
        wallLeftEntity.setPosition(-3f, 0, 0);
        wallLeftEntity.setScale(2.0f);
        wallLeftEntity.updateModelMatrix();
        scene.addEntity(wallLeftEntity);

        String wallModelId = "quad-model-normals";
        Model quadModel2 = ModelLoader.loadModel(wallModelId, "resources/models/wall/wall.obj",
                scene.getTextureCache(), scene.getMaterialCache(), false);
        scene.addModel(quadModel2);

        Entity wallRightEntity = new Entity("wallRightEntity", wallModelId);
        wallRightEntity.setPosition(3f, 0, 0);
        wallRightEntity.setScale(2.0f);
        wallRightEntity.updateModelMatrix();
        scene.addEntity(wallRightEntity);

//        String bobModelId = "bobModel";
//        Model bobModel = ModelLoader.loadModel(bobModelId, "resources/models/bob/boblamp.md5mesh",
//                scene.getTextureCache(), true);
//        scene.addModel(bobModel);
//        Entity bobEntity = new Entity("bobEntity", bobModelId);
//        bobEntity.setScale(0.05f);
//        bobEntity.updateModelMatrix();
//        animationData = new AnimationData(bobModel.getAnimationList().get(0));
//        bobEntity.setAnimationData(animationData);
//        scene.addEntity(bobEntity);

        DirLight dirLight = sceneLights.getDirLight();
        dirLight.setPosition(1, 1, 0);
        dirLight.setIntensity(1.0f);
        scene.setSceneLights(sceneLights);
     */

        String terrainModelId = "terrain";
        Model terrainModel = ModelLoader.loadModel(terrainModelId, "resources/models/terrain/terrain.obj",
                scene.getTextureCache(), scene.getMaterialCache(), false);
        scene.addModel(terrainModel);
        Entity terrainEntity = new Entity("terrainEntity", terrainModelId);
        terrainEntity.setScale(100.0f);
        terrainEntity.updateModelMatrix();
        scene.addEntity(terrainEntity);

        /*
        String bobModelId = "bobModel";
        Model bobModel = ModelLoader.loadModel(bobModelId, "resources/models/bob/boblamp.md5mesh",
                scene.getTextureCache(), scene.getMaterialCache(), true);
        scene.addModel(bobModel);
        Entity bobEntity = new Entity("bobEntity-1", bobModelId);
        bobEntity.setScale(0.05f);
        bobEntity.updateModelMatrix();
        animationData1 = new AnimationData(bobModel.getAnimationList().get(0));
        bobEntity.setAnimationData(animationData1);
        scene.addEntity(bobEntity);

        Entity bobEntity2 = new Entity("bobEntity-2", bobModelId);
        bobEntity2.setPosition(2, 0, 0);
        bobEntity2.setScale(0.025f);
        bobEntity2.updateModelMatrix();
        animationData2 = new AnimationData(bobModel.getAnimationList().get(0));
        bobEntity2.setAnimationData(animationData2);
        scene.addEntity(bobEntity2);
         */

        Model cubeModel = ModelLoader.loadModel("cube-model", "resources/models/cube/cube.obj",
                scene.getTextureCache(), scene.getMaterialCache(), false);
        scene.addModel(cubeModel);
        cubeEntity = new Entity("cube-entity", cubeModel.getId());
        cubeEntity.setPosition(0, 2, -1);
        cubeEntity.updateModelMatrix();
        scene.addEntity(cubeEntity);

        Model ospreyModel = ModelLoader.loadModel("osprey-model",
                "resources/models/box/box2.obj", // resources/models/osprey/osprey.mdl
                scene.getTextureCache(), scene.getMaterialCache(), false);
        scene.addModel(ospreyModel);
        ospreyEntity = new Entity("osprey-entity", ospreyModel.getId());
        ospreyEntity.setPosition(0, 5, 5); //-8, 3, 2
        ospreyEntity.setScale(50.0f); // 0.01f
        ospreyEntity.updateModelMatrix();
        scene.addEntity(ospreyEntity);

        Model computerModel = ModelLoader.loadModel("computer-model",
                "resources/models/computer/computer.obj",
                scene.getTextureCache(), scene.getMaterialCache(), false);
        scene.addModel(computerModel);
        computerEntity = new Entity("computer-entity", computerModel.getId());
        computerEntity.setPosition(0, 2, 1); //-8, 3, 2
        //computerEntity.setScale(0.01f);
        computerEntity.updateModelMatrix();
        scene.addEntity(computerEntity);

        String wallNoNormalsModelId = "quad-no-normals-model";
        Model quadModelNoNormals = ModelLoader.loadModel(wallNoNormalsModelId, "resources/models/wall/wall_nonormals.obj",
                scene.getTextureCache(), scene.getMaterialCache(), false);
        scene.addModel(quadModelNoNormals);

        Entity wallLeftEntity = new Entity("wallLeftEntity", wallNoNormalsModelId);
        wallLeftEntity.setPosition(-3f, 1, 0);
        wallLeftEntity.setScale(2.0f);
        wallLeftEntity.updateModelMatrix();
        scene.addEntity(wallLeftEntity);

        String wallModelId = "quad-model-normals";
        Model quadModel2 = ModelLoader.loadModel(wallModelId, "resources/models/wall/wall.obj",
                scene.getTextureCache(), scene.getMaterialCache(), false);
        scene.addModel(quadModel2);

        Entity wallRightEntity = new Entity("wallRightEntity", wallModelId);
        wallRightEntity.setPosition(3f, 1, 0);
        wallRightEntity.setScale(2.0f);
        wallRightEntity.updateModelMatrix();
        scene.addEntity(wallRightEntity);

        /*
        cubeEntity2 = new Entity("cube-entity-2", cubeModel.getId());
        cubeEntity2.setPosition(-2, 2, -1);
        cubeEntity2.updateModelMatrix();
        scene.addEntity(cubeEntity2);
         */


        render.setupData(scene); // NO MORE MODEL LOADING AFTER!!!

        SceneLights sceneLights = new SceneLights();
        AmbientLight ambientLight = sceneLights.getAmbientLight();
        ambientLight.setIntensity(0.5f);
        ambientLight.setColor(0.3f, 0.3f, 0.3f);

        DirLight dirLight = sceneLights.getDirLight();
        //dirLight.setPosition(0, 1, 0);
        dirLight.setPosition(1, 1, 0);
        dirLight.setIntensity(1.0f);
        scene.setSceneLights(sceneLights);

        if(false)
        {
            SkyBox skyBox = new SkyBox("resources/models/skybox/skybox.obj", scene.getTextureCache(),
                    scene.getMaterialCache());
            skyBox.getSkyBoxEntity().setScale(100);
            skyBox.getSkyBoxEntity().updateModelMatrix();
            scene.setSkyBox(skyBox);
        }

        scene.setFog(new Fog(true, new Vector3f(0.5f, 0.5f, 0.5f), 0.02f));

        Camera camera = scene.getCamera();
        camera.setPosition(-1.5f, 3.0f, 4.5f);
        camera.addRotation((float) Math.toRadians(15.0f), (float) Math.toRadians(390.f));

        lightAngle = 45.001f;

        if(!noSound)
            initSounds(scene.getCamera().getPosition(), scene.getCamera());
    }

    private void initSounds(Vector3f position, Camera camera) {
        soundMgr = new SoundManager();
        soundMgr.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);
        soundMgr.setListener(new SoundListener(camera.getPosition()));

        SoundBuffer buffer = new SoundBuffer("resources/sounds/creak1.ogg");
        soundMgr.addSoundBuffer(buffer);
        playerSoundSource = new SoundSource(false, false);
        playerSoundSource.setPosition(position);
        playerSoundSource.setBuffer(buffer.getBufferId());
        soundMgr.addSoundSource("CREAK", playerSoundSource);

        buffer = new SoundBuffer("resources/sounds/woo_scary.ogg");
        soundMgr.addSoundBuffer(buffer);
        SoundSource source = new SoundSource(true, true);
        source.setBuffer(buffer.getBufferId());
        soundMgr.addSoundSource("MUSIC", source);
        source.play();
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

        SceneLights sceneLights = scene.getSceneLights();
        DirLight dirLight = sceneLights.getDirLight();
        double angRad = Math.toRadians(lightAngle);
        dirLight.getDirection().z = (float) Math.sin(angRad);
        dirLight.getDirection().y = (float) Math.cos(angRad);

        if(!noSound)
            soundMgr.updateListenerPosition(camera);
    }

    @Override
    public void update(Window window, Scene scene, long diffTimeMillis) {

        rotation += 1.5;
        if (rotation > 360) {
            rotation = 0;
        }
        ospreyEntity.setRotation(1, 1, 1, (float) Math.toRadians(rotation));


        ospreyEntity.updateModelMatrix();

        // Calculate the angle for each color component (red, green, blue)
        double redAngle = Math.toRadians(0); // Starting with red
        double greenAngle = Math.toRadians(120); // Moving to green
        double blueAngle = Math.toRadians(240); // Moving to blue

        // Calculate the hue based on the current time
        long currentTime = System.currentTimeMillis();
        double hue = (currentTime % 10000L) / 10000.0; // Normalize to [0, 1]

        // Calculate the final RGB values
        int red = (int) (255 * Math.sin(hue + redAngle));
        int green = (int) (255 * Math.sin(hue + greenAngle));
        int blue = (int) (255 * Math.sin(hue + blueAngle));

        //scene.getSceneLights().getPointLights().get(0).setColor(new Vector3f(red, green, blue));

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
        //animationData.nextFrame();

        // playerSoundSource.play(); // How to trigger sound.
    }
}
