package com.example.computergraphics.shape;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

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
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MultipleShape {
    protected int program;
    protected Context context;
    static final public int COORDS_PER_VERTEX = 3;
    static final public int COORDS_PER_NORMAL = 3;
    public float [] color = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
    protected float modelCoords [] = {  // in counterclockwise order:
            0.0f,  0.622008459f, 0.0f, // top
            -0.5f, -0.311004243f, 0.0f, // bottom left
            0.5f, -0.311004243f, 0.0f,  // bottom right
    };
    protected short drawOrder [] = { 0, 1, 2 }; // order to draw vertices
    public static float defaultModelCoords [] = {   // in counterclockwise order:
            0.0f,  0.622008459f, 0.0f, // top
            -0.5f, -0.311004243f, 0.0f, // bottom left
            0.5f, -0.311004243f, 0.0f,  // bottom right
    };
    public static short defaultDrawOrder [] = {0, 1, 2};
    public static float defaultNormalCoords [] = {
        0f, 0f, 1f,
        0f, 0f, 1f,
        0f, 0f, 1f,
    };
    public float vertexData [];
    public float normalData [];
    public float modelMatrixRowData [][];
    protected float lightPositionData [] = { 0.0f, 0.0f, 0.75f, 1.0f };
    protected float textureCoordinateData [];

    protected FloatBuffer vertexBuffer;
    protected FloatBuffer normalBuffer;
    protected FloatBuffer textureCoordinateBuffer;
    protected FloatBuffer [] modelMatrixRowBuffer = new FloatBuffer[4];

    protected Material mtl;
    protected int textureHandle = -1; // Texture handle, -1 means no texture
    protected final int textureCoordinateDataSize = 2;
    // Set color with red, green, blue and alpha (opacity) values
    private int positionHandle;
    private int colorHandle;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private final int normalStride = COORDS_PER_VERTEX * 4;
    protected int typ = 2;
    public int numberOfCopies;
    public MultipleShape(float [] vertexData, float [] normalData, float [] textureCoordinateData, int numberOfCopies, Context context){
        this.vertexData = vertexData;
        this.normalData = normalData;
        this.textureCoordinateData = textureCoordinateData;
        this.numberOfCopies = numberOfCopies;
        this.context = context;

        this.mtl = new Material("default");
        this.program = GLES20.glCreateProgram();
        float [][] randomModelMatrices = generateRandomModelMatrix();
        float [][] modelMatrix4EachVertex = generateModelMatrix4EachVertex(randomModelMatrices);
        this.modelMatrixRowData = convertMatrices2RowVectors(modelMatrix4EachVertex);

        this.createBuffer();
        this.initProgram();
    }
    public MultipleShape(int numberOfCopies, Context context) {
        this(createData(defaultModelCoords, defaultDrawOrder, COORDS_PER_VERTEX), defaultNormalCoords, null, numberOfCopies, context);
    }
    public MultipleShape(InputStream objInputStream, int numberOfCopies, Context context) {
        this.context = context;
        this.program = GLES20.glCreateProgram();
        this.numberOfCopies = numberOfCopies;
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
        float [][] randomModelMatrices = generateRandomModelMatrix();
        float [][] modelMatrix4EachVertex = generateModelMatrix4EachVertex(randomModelMatrices);
        this.modelMatrixRowData = convertMatrices2RowVectors(modelMatrix4EachVertex);

        this.createBuffer();
        this.initProgram();
    }
    protected void createBuffer() {
        if (this.vertexData != null) {
            this.vertexBuffer = toBuffer(this.vertexData, numberOfCopies);
        }
        if (this.textureCoordinateData != null) {
            this.textureCoordinateBuffer = toBuffer(this.textureCoordinateData, numberOfCopies);
        }
        if (this.normalData != null) {
            this.normalBuffer = toBuffer(this.normalData, numberOfCopies);
        }
        if (this.modelMatrixRowData != null){
            for(int i=0; i<4; i++){
                this.modelMatrixRowBuffer[i] = toBuffer(this.modelMatrixRowData[i]);
            }
        }
    }
    public static  FloatBuffer toBuffer(float[] data){
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
    public static FloatBuffer toBuffer(float[] data, int numberOfRepeat){
        float [] newData = new float[data.length * numberOfRepeat];
        for(int i=0; i<numberOfRepeat; i++){
            System.arraycopy(data, 0, newData, i * data.length, data.length);
        }
        // Initialize a ByteBuffer for the data
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(newData.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        // Create a FloatBuffer from the ByteBuffer
        FloatBuffer buffer = byteBuffer.asFloatBuffer();
        // Put the data into the FloatBuffer
        buffer.put(newData);
        // Set the position to the beginning
        buffer.position(0);
        return buffer;
    }
    public static float [] createData (float[] coords, short[] order, int stride) {
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
//        for(int i=0; i<modelMatrixData.length; i++){
//            Log.d("Tag1", "V " + Arrays.toString(modelMatrixData[i]));
//        }
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
    public float [][] generateRandomModelMatrix(){
        float [][] modelMatrices = new float [numberOfCopies][16];
        Random random = new Random();
        for (int i=0; i<numberOfCopies; i++){
            float [] translation = new float [] {
                -1f + random.nextFloat() *  2f,
                -1f + random.nextFloat() *  2f,
                -1f + random.nextFloat() *  2f,
            };
            float [] rotation = new float [] {
                random.nextFloat() * 360f,
                random.nextFloat() * 360f,
                random.nextFloat() * 360f,
            };
            float [] scaling = new float [] {0.2f, 0.2f, 0.2f};
            modelMatrices[i] = Utility.getModelMatrix(translation, rotation, scaling);
        }
        return modelMatrices;
    }
    public float [][] generateModelMatrix4EachVertex(float [][] modelMatrices){
        int numberOfCoordinates = (int) vertexData.length / COORDS_PER_VERTEX;
        float [][] modelMatrix4EachVertex = new float[numberOfCoordinates * modelMatrices.length][16];
        for(int i=0; i<modelMatrices.length; i++){
            for (int j=0; j<numberOfCoordinates; j++){
                int length = modelMatrices[i].length;
                System.arraycopy(modelMatrices[i], 0, modelMatrix4EachVertex[i * numberOfCoordinates + j], 0, length);
            }
        }
        return modelMatrix4EachVertex;
    }
    public float [][] convertMatrices2RowVectors(float [][] matrices){
        float [][] vectors = new float [4][matrices.length * 4];
        for(int i=0; i<matrices.length; i++){
            System.arraycopy(matrices[i], 0, vectors[0], i * 4, 4);
            System.arraycopy(matrices[i], 4, vectors[1], i * 4, 4);
            System.arraycopy(matrices[i], 8, vectors[2], i * 4, 4);
            System.arraycopy(matrices[i], 12, vectors[3], i * 4, 4);
        }
        return vectors;
    }
    public void draw(float [] vMatrix, float[] pMatrix, float[] rotationMatrix, float [] eye){
        GLES20.glUseProgram(program);

        int projectionMatrixHandle = GLES20.glGetUniformLocation(program, "uProjectionMatrix");
        int viewMatrixHandle = GLES20.glGetUniformLocation(program, "uViewMatrix");
        int rotationMatrixHandle = GLES20.glGetUniformLocation(program, "uRotationMatrix");
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
        int modelMatrixRow1Handle = GLES20.glGetAttribLocation(program, "aModelMatrixRow1");
        int modelMatrixRow2Handle = GLES20.glGetAttribLocation(program, "aModelMatrixRow2");
        int modelMatrixRow3Handle = GLES20.glGetAttribLocation(program, "aModelMatrixRow3");
        int modelMatrixRow4Handle = GLES20.glGetAttribLocation(program, "aModelMatrixRow4");

        GLES20.glUniformMatrix4fv(projectionMatrixHandle, 1, false, pMatrix, 0);
        GLES20.glUniformMatrix4fv(viewMatrixHandle, 1, false, vMatrix, 0);
        GLES20.glUniformMatrix4fv(rotationMatrixHandle, 1, false, rotationMatrix, 0);
        textureHandle = mtl.textureHandle;
        if (this.textureCoordinateBuffer != null && textureCoordinateHandle >= 0 && textureHandle >=0) {
            GLES20.glEnableVertexAttribArray(textureCoordinateHandle);
            GLES20.glVertexAttribPointer(textureCoordinateHandle, textureCoordinateDataSize, GLES20.GL_FLOAT, false, 0, textureCoordinateBuffer);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
            GLES20.glUniform1i(textureUniformHandle, 0);
        }
        GLES20.glUniform4fv(colorHandle, 1, color, 0);
        if (this.lightPositionData != null && lightPositionHandle >= 0){
            GLES20.glUniform3fv(lightPositionHandle, 1, this.lightPositionData, 0);
        }
        GLES20.glUniform3fv(viewPositionHandle, 1, eye, 0);
        GLES20.glUniform3fv(ambientHandle, 1, mtl.Ka, 0);
        GLES20.glUniform3fv(diffuseHandle, 1, mtl.Kd, 0);
        GLES20.glUniform3fv(specularHandle, 1, mtl.Ks, 0);

        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        if (this.normalBuffer != null && normalHandle >= 0){
            GLES20.glEnableVertexAttribArray(normalHandle);
            GLES20.glVertexAttribPointer(normalHandle, COORDS_PER_NORMAL,
                    GLES20.GL_FLOAT, false,
                    normalStride, normalBuffer);
        }
        int COORDS_PER_ROW = 4;
        int rowStride = COORDS_PER_ROW * 4;
        GLES20.glEnableVertexAttribArray(modelMatrixRow1Handle);
        GLES20.glVertexAttribPointer(modelMatrixRow1Handle, COORDS_PER_ROW,
                GLES20.GL_FLOAT, false,
                rowStride, modelMatrixRowBuffer[0]);
        GLES20.glEnableVertexAttribArray(modelMatrixRow2Handle);
        GLES20.glVertexAttribPointer(modelMatrixRow2Handle, COORDS_PER_ROW,
                GLES20.GL_FLOAT, false,
                rowStride, modelMatrixRowBuffer[1]);
        GLES20.glEnableVertexAttribArray(modelMatrixRow3Handle);
        GLES20.glVertexAttribPointer(modelMatrixRow3Handle, COORDS_PER_ROW,
                GLES20.GL_FLOAT, false,
                rowStride, modelMatrixRowBuffer[2]);
        GLES20.glEnableVertexAttribArray(modelMatrixRow4Handle);
        GLES20.glVertexAttribPointer(modelMatrixRow4Handle, COORDS_PER_ROW,
                GLES20.GL_FLOAT, false,
                rowStride, modelMatrixRowBuffer[3]);
        if (typ == 2){
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, numberOfCopies * vertexData.length / COORDS_PER_VERTEX);
        } else if (typ == 1) {
            GLES20.glDrawArrays(GLES20.GL_LINES, 0, numberOfCopies * vertexData.length / COORDS_PER_VERTEX);
        } else {
            GLES20.glDrawArrays(GLES20.GL_POINTS, 0, numberOfCopies * vertexData.length / COORDS_PER_VERTEX);
        }
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(normalHandle);
        GLES20.glDisableVertexAttribArray(textureCoordinateHandle);
        GLES20.glDisableVertexAttribArray(modelMatrixRow1Handle);
        GLES20.glDisableVertexAttribArray(modelMatrixRow2Handle);
        GLES20.glDisableVertexAttribArray(modelMatrixRow3Handle);
        GLES20.glDisableVertexAttribArray(modelMatrixRow4Handle);
    }
}
