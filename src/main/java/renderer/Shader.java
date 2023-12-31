package renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import javax.print.DocFlavor;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {
    private int shaderProgramID;
    private boolean beingUsed = false;

    private String vertexSource;
    private String fragmentSource;
    private String filepath;

    public Shader(String filepath) {
        this.filepath = filepath;
        
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            // Find the first pattern after #type 'pattern'
            int index = source.indexOf("#type") + 6;
            int eol = source.indexOf("\r\n", index);
            String pattern1 = source.substring(index, eol).trim();

            // Find the second pattern after #type 'pattern'
            index = source.indexOf("#type", eol) + 6;
            eol = source.indexOf("\r\n", index);
            String pattern2 = source.substring(index, eol).trim();

            if (firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            } else if (firstPattern.equals("fragment")) {
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected Token: '" + pattern1 + "'");
            }

            if (secondPattern.equals("vertex")) {
                vertexSource = splitString[2];
            } else if (secondPattern.equals("fragment")) {
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected Token: '" + pattern2 + "'");
            }
        } catch(IOException e) {
            e.printStackTrace();
            assert false : "Error: Could not open file for Shader: '" + filepath + "'";
        }
    }

    public void compile() {
        // ============================================================
        // Compile and link shaders
        // ============================================================
        int vertexID, fragmentID;

        // First load and compile the vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);
        
        // Pass the shader source to the GPU
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        // Check for errors in compilation
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        
        if (success == GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            
            System.out.println("ERROR: '" + filepath + "'\n\tVertex Shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, len));
            
            assert false : "";
        }

        // First load and compile the vertex shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
        
        // Pass the shader source to the GPU
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);

        // Check for errors in compilation
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        
        if (success == GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            
            System.out.println("ERROR: '" + filepath + "'\n\tFragment Shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, len));
            
            assert false : "";
        }

        // Link shaders and check for errors
        shaderProgramID = glCreateProgram();
        
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        // Check for linking errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        
        if (success == GL_FALSE) {
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            
            System.out.println("ERROR: '" + filepath + "'\n\tLinking of Shaders failed.");
            System.out.println(glGetProgramInfoLog(shaderProgramID, len));
            
            assert false : "";
        }
    }

    public void use() {
        if (!beingUsed) {
            // Bind shader program
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }
    }
    
    public void detach() {
        glUseProgram(0);
        beingUsed = false;
    }

    public void uploadMatrix4f(String variableName, Matrix4f matrix4) {
        int variableLocation = glGetUniformLocation(shaderProgramID, variableName);
        
        use();
        
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        matrix4.get(matrixBuffer);
        
        glUniformMatrix4fv(variableLocation, false, matrixBuffer);
    }

    public void uploadMatrix3f(String variableName, Matrix3f matrix3) {
        int variableLocation = glGetUniformLocation(shaderProgramID, variableName);
        
        use();
        
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(9);
        matrix3.get(matrixBuffer);
        
        glUniformMatrix3fv(variableLocation, false, matrixBuffer);
    }

    public void uploadVec4f(String variableName, Vector4f vector) {
        int variableLocation = glGetUniformLocation(shaderProgramID, variableName);
        
        use();
        
        glUniform4f(variableLocation, vector.x, vector.y, vector.z, vector.w);
    }

    public void uploadVec3f(String varName, Vector3f vector) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        
        use();
        
        glUniform3f(variableLocation, vector.x, vector.y, vector.z);
    }

    public void uploadVec2f(String variableName, Vector2f vector) {
        int variableLocation = glGetUniformLocation(shaderProgramID, variableName);
        
        use();
        
        glUniform2f(variableLocation, vector.x, vector.y);
    }

    public void uploadFloat(String variableName, float value) {
        int variableLocation = glGetUniformLocation(shaderProgramID, variableName);
        
        use();
        
        glUniform1f(variableLocation, value);
    }

    public void uploadInt(String variableName, int value) {
        int variableLocation = glGetUniformLocation(shaderProgramID, variableName);
        
        use();
        
        glUniform1i(variableLocation, value);
    }

    public void uploadTexture(String variableName, int slot) {
        int variableLocation = glGetUniformLocation(shaderProgramID, variableName);
        
        use();
        
        glUniform1i(variableLocation, slot);
    }

    public void uploadIntArray(String variableName, int[] array) {
        int variableLocation = glGetUniformLocation(shaderProgramID, variableName);
        
        use();
        
        glUniform1iv(variableLocation, array);
    }
}
