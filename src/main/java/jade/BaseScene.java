package jade;

import components.SpriteRenderer;
import components.Spritesheet;
import eventHandlers.KeyListener;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

import static org.lwjgl.glfw.GLFW.*;

public class BaseScene extends Scene {

    public BaseScene() {
        //
    }

    @Override
    public void init() {
        loadResources();

        this.camera = new Camera(new Vector2f(0, 0));

        Spritesheet sprites = AssetPool.getSpritesheet("assets/images/spritesheet.png");

        GameObject background = new GameObject("Background", new Transform(new Vector2f(), new Vector2f(1920, 1080)));
        background.addComponent(new SpriteRenderer(new Vector4f(0.53f, 0.91f, 0.91f, 1)));
        this.addGameObjectToScene(background);

        GameObject object1 = new GameObject("Object 1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)));
        object1.addComponent(new SpriteRenderer(sprites.getSprite(0)));
        this.addGameObjectToScene(object1);

        GameObject object2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)));
        object2.addComponent(new SpriteRenderer(sprites.getSprite(7)));
        this.addGameObjectToScene(object2);
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpritesheet("assets/images/spritesheet.png",
                new Spritesheet(AssetPool.getTexture("assets/images/spritesheet.png"),
                        16, 16, 26, 0));
    }

    @Override
    public void update(float dt) {
        if (KeyListener.isKeyPressed(GLFW_KEY_RIGHT)) {
            camera.position.x += 100f * dt;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT)) {
            camera.position.x -= 100f * dt;
        }
        
        if (KeyListener.isKeyPressed(GLFW_KEY_UP)) {
            camera.position.y += 100f * dt;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_DOWN)) {
            camera.position.y -= 100f * dt;
        }

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
    }
}
