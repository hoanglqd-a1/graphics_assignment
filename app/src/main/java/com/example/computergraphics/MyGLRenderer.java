package com.example.computergraphics;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import java.nio.*;
import java.util.*;
import java.io.*;
import android.content.res.AssetManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.opengl.GLUtils;

public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Context mContext;

    private Shape shape;
    private Cube cube;
    private Shape model;
    
    private float [] vpMatrix = new float[16];
    private float [] projectionMatrix = new float[16];
    private float [] viewMatrix = new float [16];
    private float [] rotationMatrix = new float[16];
    private float [] lightModelMatrix = new float[16];

    MyGLRenderer(Context context) {
        // Constructor
        mContext = context;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
        // GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        shape = new Shape(this.mContext);
        cube = new Cube(this.mContext);
        try {
            AssetManager assetManager = this.mContext.getAssets();
            InputStream objInputStream = assetManager.open("teapot.obj");
            InputStream mtlInputStream = null;
            try {
                mtlInputStream = assetManager.open("teapot.mtl");
            } catch (FileNotFoundException e) {
                // If the mtl file is not found, we can still create the model without it
                mtlInputStream = null; // No material file
            }
            model = new Shape(objInputStream, mtlInputStream, this.mContext);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        Matrix.setLookAtM(viewMatrix, 0, 3.0f, 3.0f, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        // Calculate the projection and view transformation
        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.setRotateM(rotationMatrix, 0, mAngle, 0f, 1.0f, 0.0f);

        // shape.draw(viewMatrix, projectionMatrix, rotationMatrix);
        cube.draw(viewMatrix, projectionMatrix, rotationMatrix);
        // model.draw(viewMatrix, projectionMatrix, rotationMatrix);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 8);
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
    public volatile float mAngle;
    public float getAngle() {
        return this.mAngle;
    }
    public void setAngle(float angle) {
        this.mAngle = angle;
    }
}

class Shape {
    protected int program;
    protected Context context;
    static final public int COORDS_PER_VERTEX = 3;
    protected float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
    protected float modelCoords [] = {   // in counterclockwise order:
        0.0f,  0.622008459f, 0.0f, // top
        -0.5f, -0.311004243f, 0.0f, // bottom left
        0.5f, -0.311004243f, 0.0f,  // bottom right
    };
    protected short drawOrder [] = { 0, 1, 2 }; // order to draw vertices
    protected float vertexData [];
    protected float normalData [];
    protected float textureCoordinateData [];

    protected FloatBuffer vertexBuffer;
    protected FloatBuffer normalBuffer;
    protected FloatBuffer textureCoordinateBuffer;

    protected float translation [] = { 0.0f, 0.0f, 0.0f};
    protected float rotation [] = { 0.0f, 0.0f, 0.0f};
    protected float scale [] = { 1.0f, 1.0f, 1.0f };
    protected float modelMatrix [] = new float[16];

    protected int textureHandle = -1; // Texture handle, -1 means no texture

    protected final int textureCoordinateDataSize = 2;
    protected int textureCode = R.drawable.bumpy_bricks_public_domain;
    // protected int textureCode = -1; // No texture by default

    private final String vertexShaderCode =
        "uniform vec4 aColor;" +
        "uniform mat4 uMVPMatrix;" +
        "uniform mat4 uMVMatrix;" +

        "attribute vec2 aTextureCoordinate;" +
        "attribute vec4 aPosition;" +
        "attribute vec3 aNormal;" +

        "varying vec2 vTextureCoordinate;" +
        "varying vec4 vColor;" +

        "void main() {" +
        "   vColor = aColor;" +
        "   gl_Position = uMVPMatrix * aPosition;" +
        "   vTextureCoordinate = aTextureCoordinate;" +
        "}";

    private final String fragmentShaderCode =
        "precision mediump float;" +

        "uniform sampler2D uTexture;" +

        "varying vec4 vColor;" +
        "varying vec2 vTextureCoordinate;" +

        "void main() {" +
        "  gl_FragColor = vColor * texture2D(uTexture, vTextureCoordinate);" +
        "}";

    // Set color with red, green, blue and alpha (opacity) values

    private int positionHandle;
    private int colorHandle;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public Shape(Context context) {
        this.context = context;
        this.program = GLES20.glCreateProgram();
        // initialize vertex byte buffer for shape coordinates
        this.vertexData = this.createVertexData(this.modelCoords, this.drawOrder);
        this.createBuffer();
        this.initProgram();
    }
    public Shape(InputStream objInputStream, InputStream mtlInputStream, Context mContext) {
        this.context = mContext;
        this.program = GLES20.glCreateProgram();
        // BufferedReader mtlReader = null;
        // if (mtlInputStream != null) {
        //     mtlReader = new BufferedReader(new InputStreamReader(mtlInputStream));
        // }
        BufferedReader reader = new BufferedReader(new InputStreamReader(objInputStream));
        List<Float> vertices = new ArrayList<>();
        List<Short> faces = new ArrayList<>();
        String line;
        // while (true){
        //     try {
        //         if (mtlReader == null || (line = mtlReader.readLine()) == null) break;
        //     } catch (IOException e) {
        //         throw new RuntimeException(e);
        //     }
        //     String [] tokens = line.split(" ");
        //     if (tokens[0].equals("newmtl")){
        //         // ignore material names
        //         continue;
        //     }
        //     else if (tokens[0].equals("map_Kd")){
        //         // ignore texture map names
        //         continue;
        //     }
        //     else if (tokens[0].equals("#")){
        //         // ignore comments
        //         continue;
        //     }
        //     else if (tokens[0].equals("map_Kd")){
        //         String textureName = tokens[1].split(".")[0];
        //         this.texture = this.mContext.getResources().getIdentifier(textureName, "drawable", this.mContext.getPackageName());
        //     }
        //     else {
        //         // ignore other lines
        //         continue;
        //     }
        // }
        while (true){
            try {
                if ((line = reader.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String[] tokens = line.split(" ");
            if (tokens[0].equals("v")){
                vertices.add(Float.parseFloat(tokens[1]));
                vertices.add(Float.parseFloat(tokens[2]));
                vertices.add(Float.parseFloat(tokens[3]));
            }
            else if (tokens[0].equals("f")){
                int verticesCount = tokens.length;
                short fv = (short) (Integer.parseInt(tokens[1].split("/")[0]) - 1); // vertex index
                for (int i = 2; i < verticesCount - 1; i++){
                    short sv = (short) (Integer.parseInt(tokens[i].split("/")[0]) - 1); // vertex index
                    short tv = (short) (Integer.parseInt(tokens[i + 1].split("/")[0]) - 1); // vertex index
                    faces.add(fv);
                    faces.add(sv);
                    faces.add(tv);
                }
            }
            else if (tokens[0].equals("#")){
                // ignore comments
                continue;
            }
            else {
                // ignore other lines
                continue;
            }
        }
        this.modelCoords = new float[vertices.size()];
        for (int i=0; i<vertices.size(); ++i){
            this.modelCoords[i] = vertices.get(i);
        }
        this.drawOrder = new short[faces.size()];
        for (int i=0; i<faces.size(); ++i){
            this.drawOrder[i] = faces.get(i);
        }
        this.vertexData = this.createVertexData(this.modelCoords, this.drawOrder);
        this.createBuffer();
        this.initProgram();
    }
    protected void createBuffer() {
        if (this.vertexData != null) {
            this.vertexBuffer = toBuffer(this.vertexData);
        }
        if (this.textureCoordinateData != null) {
            this.textureCoordinateBuffer = toBuffer(this.textureCoordinateData);
        }
        if (this.normalData != null) {
            // Create a buffer for the normal data if it exists
            this.normalBuffer = toBuffer(this.normalData);
        }
    }
    public static FloatBuffer toBuffer(float[] data){
        // Initialize a ByteBuffer for the data
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        // Create a FloatBuffer from the ByteBuffer
        FloatBuffer buffer = byteBuffer.asFloatBuffer();
        // Put the data into the FloatBuffer
        buffer.put(data);
        // Set the position to the beginning
        buffer.position(0);
        return buffer;
    }
    protected float [] createVertexData(float[] geometryCoords, short[] drawOrder) {
        float [] vertexData = new float[drawOrder.length * COORDS_PER_VERTEX];
        for (int i = 0; i < drawOrder.length; i++){
            short index = drawOrder[i];
            vertexData[i * COORDS_PER_VERTEX] = geometryCoords[index * COORDS_PER_VERTEX];
            vertexData[i * COORDS_PER_VERTEX + 1] = geometryCoords[index * COORDS_PER_VERTEX + 1];
            vertexData[i * COORDS_PER_VERTEX + 2] = geometryCoords[index * COORDS_PER_VERTEX + 2];
        }
        return vertexData;
    }
    protected void initProgram() {
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
            vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
            fragmentShaderCode);

        // create empty OpenGL ES Program
        GLES20.glAttachShader(this.program, vertexShader);
        GLES20.glAttachShader(this.program, fragmentShader);        
        
        // if (this.textureCode != -1) {
        //     // Load texture if shader code is provided
        //     this.mTextureHandle = MyGLRenderer.loadTexture(this.mContext, this.textureCode);
        // }

        GLES20.glLinkProgram(this.program);
    }
    public float[] getModelMatrix(){
        // Reset the model matrix to identity
        Matrix.setIdentityM(modelMatrix, 0);
        // Apply translation
        Matrix.translateM(modelMatrix, 0, translation[0], translation[1], translation[2]);
        // Apply rotation
        Matrix.rotateM(modelMatrix, 0, rotation[0], 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, rotation[1], 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, rotation[2], 0.0f, 0.0f, 1.0f);
        // Apply scaling
        Matrix.scaleM(modelMatrix, 0, scale[0], scale[1], scale[2]);
        return modelMatrix;
    }
    public static int loadTexture(final Context context, final int resourceId) {
        final int[] textureHandle = new int[1];
        GLES20.glGenTextures(1, textureHandle, 0);
        if (textureHandle[0] != 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false; // No pre-scaling
            // Load the bitmap from resources
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
            if (bitmap == null) {
                throw new RuntimeException("Error loading texture.");
            }
            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }
        else {
            throw new RuntimeException("Error loading texture.");
        }
        return textureHandle[0];
    }
    public void draw(float [] vMatrix, float[] pMatrix, float[] rotationMatrix) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(program);

        int positionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        int colorHandle = GLES20.glGetUniformLocation(program, "aColor");
        int textureUniformHandle = GLES20.glGetUniformLocation(program, "uTexture");
        int textureCoordinateHandle = GLES20.glGetAttribLocation(program, "aTextureCoordinate");
        int mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                                    GLES20.GL_FLOAT, false,
                                    vertexStride, vertexBuffer);

        // Set color for drawing the triangle
        GLES20.glUniform4fv(colorHandle, 1, color, 0);
        
        modelMatrix = this.getModelMatrix();
        Matrix.multiplyMM(modelMatrix, 0, rotationMatrix, 0, modelMatrix, 0);
        float [] vpMatrix = new float[16];
        Matrix.multiplyMM(vpMatrix, 0, pMatrix, 0, vMatrix, 0);
        float [] mvpMatrix = new float[16];
        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0);

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);
        if (this.textureCoordinateBuffer != null && textureCoordinateHandle >= 0) {
            GLES20.glEnableVertexAttribArray(textureCoordinateHandle);
            GLES20.glVertexAttribPointer(
                textureCoordinateHandle,
                textureCoordinateDataSize,
                GLES20.GL_FLOAT,
                false,
                0,
                textureCoordinateBuffer
            );
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
            GLES20.glUniform1i(textureUniformHandle, 0);
        }

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexData.length / COORDS_PER_VERTEX);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}

class Cube extends Shape {
    static float defaultCubeCoords[] = {
        -0.5f,  0.5f, 0.5f,   // top left front
        -0.5f, -0.5f, 0.5f,   // bottom left front
        0.5f, -0.5f, 0.5f,   // bottom right front
        0.5f,  0.5f, 0.5f,   // top right front
        -0.5f,  0.5f,  -0.5f,   // top left back
        -0.5f, -0.5f,  -0.5f,   // bottom left back
        0.5f, -0.5f,  -0.5f,   // bottom right back
        0.5f,  0.5f,  -0.5f }; // top right back
    static short defaultDrawOrder[] = {
        0, 1, 2, 0, 2, 3, // front face
        4, 7, 6, 4, 6, 5, // back face
        0, 4, 5, 0, 5, 1, // left face
        2, 6, 7, 2, 7, 3, // right face
        0, 3, 7, 0, 7, 4, // top face
        1, 5, 6, 1, 6, 2, // bottom face
    };
    static float defaultNormalData [] = {
        0.0f, 0.0f, 1.0f,				
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,				
        0.0f, 0.0f, 1.0f,
        0.0f, 0.0f, 1.0f,
        
        // Right face 
        1.0f, 0.0f, 0.0f,				
        1.0f, 0.0f, 0.0f,
        1.0f, 0.0f, 0.0f,
        1.0f, 0.0f, 0.0f,				
        1.0f, 0.0f, 0.0f,
        1.0f, 0.0f, 0.0f,
        
        // Back face 
        0.0f, 0.0f, -1.0f,				
        0.0f, 0.0f, -1.0f,
        0.0f, 0.0f, -1.0f,
        0.0f, 0.0f, -1.0f,				
        0.0f, 0.0f, -1.0f,
        0.0f, 0.0f, -1.0f,
        
        // Left face 
        -1.0f, 0.0f, 0.0f,				
        -1.0f, 0.0f, 0.0f,
        -1.0f, 0.0f, 0.0f,
        -1.0f, 0.0f, 0.0f,				
        -1.0f, 0.0f, 0.0f,
        -1.0f, 0.0f, 0.0f,
        
        // Top face 
        0.0f, 1.0f, 0.0f,			
        0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f,				
        0.0f, 1.0f, 0.0f,
        0.0f, 1.0f, 0.0f,
        
        // Bottom face 
        0.0f, -1.0f, 0.0f,			
        0.0f, -1.0f, 0.0f,
        0.0f, -1.0f, 0.0f,
        0.0f, -1.0f, 0.0f,				
        0.0f, -1.0f, 0.0f,
        0.0f, -1.0f, 0.0f,
    };
    static float defaultTextureCoordinateData[] = {
        // Front face
        0.0f, 0.0f, 				
        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 1.0f,
        1.0f, 0.0f,				
        
        // Back face 
        0.0f, 0.0f, 				
        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 1.0f,
        1.0f, 0.0f,	
        
        // Left face 
        0.0f, 0.0f, 				
        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 1.0f,
        1.0f, 0.0f,	
        
        // Right face 
        0.0f, 0.0f, 				
        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 1.0f,
        1.0f, 0.0f,	
        
        // Top face 
        0.0f, 0.0f, 				
        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 1.0f,
        1.0f, 0.0f,	
        
        // Bottom face 
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
        0.0f, 0.0f,
        1.0f, 1.0f,
        1.0f, 0.0f,
    };
    public Cube(Context context) {
        super(context);
        this.vertexData = this.createVertexData(defaultCubeCoords, defaultDrawOrder);
        this.textureCoordinateData = defaultTextureCoordinateData;
        this.normalData = defaultNormalData;
        this.textureHandle = this.loadTexture(context, R.drawable.bumpy_bricks_public_domain);
        this.createBuffer();
    }
    public Cube(float[] cubeCoords, Context context) {
        super(context);
        this.vertexData = this.createVertexData(cubeCoords, defaultDrawOrder);
        this.textureCoordinateData = defaultTextureCoordinateData;
        this.normalData = defaultNormalData;
        this.textureHandle = this.loadTexture(context, R.drawable.bumpy_bricks_public_domain);
        this.createBuffer();
    }
}