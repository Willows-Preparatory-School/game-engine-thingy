package Engine.scene;

import Engine.IGuiInstance;
import Engine.graph.Mesh;
import Engine.graph.Model;
import Engine.graph.TextureCache;
import Engine.scene.lights.SceneLights;

import java.util.*;

public class Scene {

    private Map<String, Model> modelMap;
    private TextureCache textureCache;
    private Camera camera;
    private SkyBox skyBox;
    private IGuiInstance guiInstance;
    private Projection projection;
    private SceneLights sceneLights;
    private Fog fog;

    public Scene(int width, int height) {
        modelMap = new HashMap<>();
        projection = new Projection(width, height);
        textureCache = new TextureCache();
        camera = new Camera();
        fog = new Fog();
    }

    public Camera getCamera() {
        return camera;
    }

    public TextureCache getTextureCache() {
        return textureCache;
    }

    public SceneLights getSceneLights() {
        return sceneLights;
    }

    public IGuiInstance getGuiInstance() {
        return guiInstance;
    }

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public Fog getFog() {
        return fog;
    }

    public void addEntity(Entity entity) {
        String modelId = entity.getModelId();
        Model model = modelMap.get(modelId);
        if (model == null) {
            throw new RuntimeException("Could not find model [" + modelId + "]");
        }
        model.getEntitiesList().add(entity);
    }

    public void addModel(Model model) {
        modelMap.put(model.getId(), model);
    }

    public void cleanup() {
        modelMap.values().forEach(Model::cleanup);
    }

    public Map<String, Model> getModelMap() {
        return modelMap;
    }

    public Projection getProjection() {
        return projection;
    }

    public void setGuiInstance(IGuiInstance guiInstance) {
        this.guiInstance = guiInstance;
    }

    public void setSceneLights(SceneLights sceneLights) {
        this.sceneLights = sceneLights;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    public void setFog(Fog fog) {
        this.fog = fog;
    }

    public void resize(int width, int height) {
        projection.updateProjMatrix(width, height);
    }
}
