package Engine;

import Engine.graph.Render;
import Engine.scene.Scene;

import java.util.Arrays;

public class Engine {

    public static final int TARGET_UPS = 30;
    private final IAppLogic appLogic;
    private final Window window;
    private Render render;
    private boolean running;
    private Scene scene;
    private int targetFps;
    private int targetUps;
    private String[] engineArgs;

    // Config:
    boolean devMode = false;
    boolean serverMode = false;

    public Engine(String windowTitle, Window.WindowOptions opts, String[] args, IAppLogic appLogic)
    {
        this.engineArgs = args;
        System.out.println("INFO: Engine; running with args: " + Arrays.toString(this.engineArgs));

        window = new Window(windowTitle, opts, () -> {
            resize();
            return null;
        });
        this.targetFps = opts.fps;
        this.targetUps = opts.ups;
        this.appLogic = appLogic;

        // Args, TODO: maybe use a switch statement?
        // Developer mode.
        if(Arrays.asList(this.engineArgs).contains("-dev"))
        {
            this.devMode = true;
            System.out.println("Running in dev mode...");
            // TODO: perhaps change the window title if dev mode is on.
        }
        // Server mode.
        else if(Arrays.asList(this.engineArgs).contains("-server"))
        {
            this.serverMode = true;
            System.out.println("Running in server mode...");
        }

        render = new Render(window);
        scene = new Scene(window.getWidth(), window.getHeight());
        appLogic.init(window, scene, render);
        running = true;
    }

    private void cleanup() {
        appLogic.cleanup();
        render.cleanup();
        scene.cleanup();
        window.cleanup();
    }

    private void resize() {
        int width = window.getWidth();
        int height = window.getHeight();
        scene.resize(width, height);
        render.resize(width, height);
    }

    private void run() {
        long initialTime = System.currentTimeMillis();
        float timeU = 1000.0f / targetUps;
        float timeR = targetFps > 0 ? 1000.0f / targetFps : 0;
        float deltaUpdate = 0;
        float deltaFps = 0;

        long updateTime = initialTime;
        IGuiInstance iGuiInstance = scene.getGuiInstance();
        while (running && !window.windowShouldClose()) {
            window.pollEvents();

            long now = System.currentTimeMillis();
            deltaUpdate += (now - initialTime) / timeU;
            deltaFps += (now - initialTime) / timeR;

            if (targetFps <= 0 || deltaFps >= 1) {
                window.getMouseInput().input();
                boolean inputConsumed = iGuiInstance != null ? iGuiInstance.handleGuiInput(scene, window) : false;
                appLogic.input(window, scene, now - initialTime, inputConsumed);
            }

            if (deltaUpdate >= 1) {
                long diffTimeMillis = now - updateTime;
                appLogic.update(window, scene, diffTimeMillis);
                updateTime = now;
                deltaUpdate--;
            }

            if (targetFps <= 0 || deltaFps >= 1) {
                render.render(window, scene);
                deltaFps--;
                window.update();
            }
            initialTime = now;
        }

        cleanup();
    }

    public void start() {
        running = true;
        run();
    }

    public void stop() {
        running = false;
    }

    public boolean isDevMode()
    {
        return devMode;
    }

    public boolean isServerMode()
    {
        return serverMode;
    }
}
