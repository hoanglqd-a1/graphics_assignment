package com.example.computergraphics.shape;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.example.computergraphics.R;
import com.example.computergraphics.utils.Material;
import com.example.computergraphics.MyGLRenderer;
import com.example.computergraphics.utils.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class Shape {
    protected int program;
    protected Context context;
    static final public int COORDS_PER_VERTEX = 3;
    static final public int COORDS_PER_NORMAL = 3;
    public float [] color = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
    protected float modelCoords [] = {   // in counterclockwise order:
            0.0f,  0.622008459f, 0.0f, // top
            -0.5f, -0.311004243f, 0.0f, // bottom left
            0.5f, -0.311004243f, 0.0f,  // bottom right
    };
    protected short drawOrder [] = { 0, 1, 2 }; // order to draw vertices
    public float vertexData [];
    public float normalData [] = {
            0f, 0f, 1f,
            0f, 0f, 1f,
            0f, 0f, 1f,
    };
    protected float lightPositionData [] = { 0.0f, 0.0f, 0.75f, 1.0f };
    protected float textureCoordinateData [];
    protected FloatBuffer vertexBuffer;
    protected FloatBuffer normalBuffer;
    protected FloatBuffer textureCoordinateBuffer;

    protected float translation [] = { 0.0f, 0.0f, 0.0f};
    protected float rotation [] = { 0.0f, 0.0f, 0.0f};
    protected float scale [] = { 1.0f, 1.0f, 1.0f };
    protected float modelMatrix [] = new float[16];

    protected Material mtl;
    protected int textureHandle = -1; // Texture handle, -1 means no texture
    protected final int textureCoordinateDataSize = 2;
    // Set color with red, green, blue and alpha (opacity) values

    private int positionHandle;
    private int colorHandle;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private final int normalStride = COORDS_PER_VERTEX * 4;
    protected int typ = 2;

    public Shape(Context context) {
        this.context = context;
        this.mtl = new Material("default");
        this.program = GLES20.glCreateProgram();
        // initialize vertex byte buffer for shape coordinates
        this.vertexData = this.createData(this.modelCoords, this.drawOrder, COORDS_PER_VERTEX);
        this.createBuffer();
        this.initProgram();
    }
    public Shape(InputStream objInputStream, Context context) {
        this.context = context;
        this.program = GLES20.glCreateProgram();
        BufferedReader reader = new BufferedReader(new InputStreamReader(objInputStream));
        List<Float> vertices = new ArrayList<>();
        List<Float> textures = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Short> vfaces = new ArrayList<>();
        List<Short> tfaces = new ArrayList<>();
        List<Short> nfaces = new ArrayList<>();
        String line;
        while (true){
            try {
                if ((line = reader.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String[] tokens = line.split(" ");
            if (tokens[0].equals("mtllib")){
                AssetManager assetManager = this.context.getAssets();
                InputStream mtlfile;
                try {
                    mtlfile = assetManager.open(tokens[1]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                this.mtl = this.readMtl(mtlfile);
            }
            if (tokens[0].equals("v")){
                vertices.add(Float.parseFloat(tokens[1]));
                vertices.add(Float.parseFloat(tokens[2]));
                vertices.add(Float.parseFloat(tokens[3]));
            }
            else if (tokens[0].equals("vt")){
                textures.add(Float.parseFloat(tokens[1]));
                textures.add(Float.parseFloat(tokens[2]));
            }
            else if (tokens[0].equals("vn")){
                normals.add(Float.parseFloat(tokens[1]));
                normals.add(Float.parseFloat(tokens[2]));
                normals.add(Float.parseFloat(tokens[3]));
            }
            else if (tokens[0].equals("f")){
                int verticesCount = tokens.length;
                String [] token1 = tokens[1].split("/");
                for (int i = 2; i < verticesCount - 1; i++){
                    String [] token2 = tokens[i].split("/");
                    String [] token3 = tokens[i+1].split("/");
                    vfaces.add((short) (Integer.parseInt(token1[0]) - 1));
                    vfaces.add((short) (Integer.parseInt(token2[0]) - 1));
                    vfaces.add((short) (Integer.parseInt(token3[0]) - 1));
                    if (token1.length >= 2 && token1[1] != ""){
                        tfaces.add((short) (Integer.parseInt(token1[1]) - 1));
                        tfaces.add((short) (Integer.parseInt(token2[1]) - 1));
                        tfaces.add((short) (Integer.parseInt(token3[1]) - 1));
                    }
                    if (token1.length == 3 && token1[1] != ""){
                        nfaces.add((short) (Integer.parseInt(token1[2]) - 1));
                        nfaces.add((short) (Integer.parseInt(token2[2]) - 1));
                        nfaces.add((short) (Integer.parseInt(token3[2]) - 1));
                    }
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
        this.drawOrder = new short[vfaces.size()];
        float textureCoords [] = new float[textures.size()];
        short textureOrder [] = new short[tfaces.size()];
        float normalCoords [] = new float [normals.size()];
        short normalOrder [] = new short[nfaces.size()];
        for (int i=0; i<vertices.size(); ++i){
            this.modelCoords[i] = vertices.get(i);
        }
        for (int i=0; i<vfaces.size(); ++i){
            this.drawOrder[i] = vfaces.get(i);
        }
        for (int i=0; i<textures.size(); ++i){
            textureCoords[i] = textures.get(i);
        }
        for (int i=0; i<tfaces.size(); ++i){
            textureOrder[i] = tfaces.get(i);
        }
        for (int i=0; i<normals.size(); ++i){
            normalCoords[i] = normals.get(i);
        }
        for (int i=0; i<nfaces.size(); ++i){
            normalOrder[i] = nfaces.get(i);
        }
        this.vertexData = this.createData(this.modelCoords, this.drawOrder, COORDS_PER_VERTEX);
        this.textureCoordinateData = this.createData(textureCoords, textureOrder, 2);
        this.normalData = this.createData(normalCoords, normalOrder, 3);
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
    protected float [] createData (float[] coords, short[] order, int stride) {
        float [] data = new float[order.length * stride];
        for (int i = 0; i < order.length; i++){
            short index = order[i];
            for (int j = 0; j < stride; j++){
                data[i * stride + j] = coords[index * stride + j];
            }
        }
        return data;
    }
    protected void initProgram() {
        String vertexShaderCode = Utility.readRawTextFile(context, R.raw.vertex_shader);
        String fragmentShaderCode = Utility.readRawTextFile(context, R.raw.fragment_shader);
        int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        GLES20.glAttachShader(this.program, vertexShader);
        GLES20.glAttachShader(this.program, fragmentShader);

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
    protected Material readMtl (InputStream mtlFile){
        BufferedReader reader = new BufferedReader(new InputStreamReader(mtlFile));
        // HashMap<String, Material> mtls = new HashMap<>();
        Material curr = null;
        String line;
        while (true) {
            try {
                if((line = reader.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String [] tokens = line.split(" ");
            if (tokens[0].equals("newmtl")){
                curr = new Material(tokens[1]);
            }
            else if (tokens[0].equals("Ka")){
                curr.Ka = new float [] {
                        Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])
                };
            }
            else if (tokens[0].equals("Kd")){
                curr.Kd = new float [] {
                        Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])
                };
            }
            else if (tokens[0].equals("Ks")){
                curr.Ks = new float [] {
                        Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3])
                };
            }
            else if (tokens[0].equals("Ns")){
                curr.Ns = Float.parseFloat(tokens[1]);
            }
            else if (tokens[0].equals("map_Kd")){
                String texName = tokens[1];
                int dotIndex = texName.lastIndexOf('.');
                if (dotIndex > 0){
                    texName = texName.substring(0, dotIndex);
                }
                curr.resourceId = this.context.getResources().getIdentifier(texName, "drawable", "com.example.computergraphics");
                curr.textureHandle = this.loadTexture(this.context, curr.resourceId);
            }
            else if (tokens[0].equals("#")){
                continue;
            }
            else{
                continue;
            }
        }
        return curr;
    }
    public void draw(float [] vMatrix, float[] pMatrix, float[] rotationMatrix, float [] eye) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(program);
        Material mtl = this.mtl;

        int mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");
        int mvMatrixHandle = GLES20.glGetUniformLocation(program, "uMVMatrix");
        int textureUniformHandle = GLES20.glGetUniformLocation(program, "uTexture");
        int colorHandle = GLES20.glGetUniformLocation(program, "uColor");
        int lightPositionHandle = GLES20.glGetUniformLocation(program, "uLightPosition");
        int viewPositionHandle = GLES20.glGetUniformLocation(program, "uViewPosition");
        int ambientHandle = GLES20.glGetUniformLocation(program, "Ka");
        int diffuseHandle = GLES20.glGetUniformLocation(program, "Kd");
        int specularHandle = GLES20.glGetUniformLocation(program, "Ks");

        int positionHandle = GLES20.glGetAttribLocation(program, "aPosition");
        int normalHandle = GLES20.glGetAttribLocation(program, "aNormal");
        int textureCoordinateHandle = GLES20.glGetAttribLocation(program, "aTextureCoordinate");

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

        float [] mvMatrix = new float[16];
        Matrix.multiplyMM(mvMatrix, 0, vMatrix, 0, modelMatrix, 0);
        GLES20.glUniformMatrix4fv(mvMatrixHandle, 1, false, mvMatrix, 0);

        this.textureHandle = mtl.textureHandle;
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

        if (this.lightPositionData != null && lightPositionHandle >= 0){
            GLES20.glUniform3fv(lightPositionHandle, 1, this.lightPositionData, 0);
        }

        GLES20.glUniform3fv(viewPositionHandle, 1, eye, 0);

        GLES20.glUniform3fv(ambientHandle, 1, mtl.Ka, 0);
        GLES20.glUniform3fv(diffuseHandle, 1, mtl.Kd, 0);
        GLES20.glUniform3fv(specularHandle, 1, mtl.Ks, 0);

        if (this.normalBuffer != null && normalHandle >= 0){
            GLES20.glEnableVertexAttribArray(normalHandle);
            GLES20.glVertexAttribPointer(normalHandle, COORDS_PER_NORMAL,
                    GLES20.GL_FLOAT, false,
                    normalStride, normalBuffer);
        }

        if (typ == 2){
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexData.length / COORDS_PER_VERTEX);
        } else if (typ == 1) {
            GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertexData.length / COORDS_PER_VERTEX);
        } else {
            GLES20.glDrawArrays(GLES20.GL_POINTS, 0, vertexData.length / COORDS_PER_VERTEX);
        }
        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }
}
