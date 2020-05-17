package eventHandlers;

import static org.lwjgl.opengl.GL11.glViewport;

public class WindowResizeListener {

    public static void resizeCallback(long glfwWindow, int width, int height) {
        glViewport(0, 0, width, height);
    }
}