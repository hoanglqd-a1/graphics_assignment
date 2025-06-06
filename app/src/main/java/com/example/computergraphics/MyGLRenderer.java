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

// public class MyGLRenderer implements GLSurfaceView.Renderer {
//     private Context mContext;
//     private Triangle mTriangle;
//     private Square mSquare;
//     private Cube mCube;
//     public MyGLRenderer(Context context) {
//         this.mContext = context;
//         // Initialize the rotation angle
//         this.mAngle = 0.0f;
//     }
//     public void onSurfaceCreated(GL10 unused, EGLConfig config) {
//         // Set the background frame color
//         GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//         GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//         // Use culling to remove back faces.
// 		GLES20.glEnable(GLES20.GL_CULL_FACE);
//         // Clear the depth buffer
//         GLES20.glEnable(GLES20.GL_DEPTH_TEST);
//         this.mTriangle = new Triangle(this.mContext);
//         this.mSquare = new Square(this.mContext);
//         this.mCube = new Cube(this.mContext);
//     }
//     private float [] vPMatrix = new float[16];
//     private float [] projectionMatrix = new float[16];
//     private float [] viewMatrix = new float [16];
//     private float [] rotationMatrix = new float[16];
//     private float [] lightModelMatrix = new float[16];
//     public void onDrawFrame(GL10 unused) {
//         // Redraw background color
//         GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//         // Set the camera position (View matrix)
//         Matrix.setLookAtM(viewMatrix, 0, 3.0f, 3.0f, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
//         // Calculate the projection and view transformation
//         Matrix.multiplyMM(vPMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
//         Matrix.setRotateM(rotationMatrix, 0, mAngle, 0f, 1.0f, 0.0f);
//         // Combine the rotation matrix with the projection and camera view
//         // Note that the vPMatrix factor *must be first* in order
//         // for the matrix multiplication product to be correct.
//         // Draw shape
//         Model model = null;
//         try {
//             AssetManager assetManager = this.mContext.getAssets();
//             InputStream objInputStream = assetManager.open("teapot.obj");
//             InputStream mtlInputStream = null;
//             try {
//                 mtlInputStream = assetManager.open("teapot.mtl");
//             } catch (FileNotFoundException e) {
//                 // If the mtl file is not found, we can still create the model without it
//                 mtlInputStream = null; // No material file
//             }
//             model = new Model(objInputStream, mtlInputStream, this.mContext);
//         } catch (IOException e) {
//             throw new RuntimeException(e);
//         }
//         // model.draw(scratch);
//         // mCube.draw(viewMatrix, projectionMatrix, rotationMatrix);
//         // mSquare.draw(scratch);
//         mTriangle.draw(viewMatrix, projectionMatrix, rotationMatrix);
//     }
//     @Override
//     public void onSurfaceChanged(GL10 unused, int width, int height) {
//         GLES20.glViewport(0, 0, width, height);

//         float ratio = (float) width / height;

//         // this projection matrix is applied to object coordinates
//         // in the onDrawFrame() method
//         Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
//     }

//     public static int loadShader(int type, String shaderCode){
//         // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
//         // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
//         int shader = GLES20.glCreateShader(type);

//         // add the source code to the shader and compile it
//         GLES20.glShaderSource(shader, shaderCode);
//         GLES20.glCompileShader(shader);

//         return shader;
//     }
//     public volatile float mAngle;
//     public float getAngle() {
//         return this.mAngle;
//     }
//     public void setAngle(float angle) {
//         this.mAngle = angle;
//     }
//     public static int loadTexture(final Context context, final int resourceId) {
//         final int[] textureHandle = new int[1];
//         GLES20.glGenTextures(1, textureHandle, 0);
//         if (textureHandle[0] != 0) {
//             final BitmapFactory.Options options = new BitmapFactory.Options();
//             options.inScaled = false; // No pre-scaling
//             // Load the bitmap from resources
//             final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
//             if (bitmap == null) {
//                 throw new RuntimeException("Error loading texture.");
//             }
//             // Bind to the texture in OpenGL
//             GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
//             // Set filtering
//             GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//             GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//             // Load the bitmap into the bound texture.
//             GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
//             // Recycle the bitmap, since its data has been loaded into OpenGL.
//             bitmap.recycle();
//         }
//         else {
//             throw new RuntimeException("Error loading texture.");
//         }
//         return textureHandle[0];
//     }
// }

// class Model {
//     protected int mProgram;
//     protected Context mContext;
//     static public int COORDS_PER_VERTEX = 3;
//     protected float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
//     protected float modelCoords [];
//     protected short drawOrder [];
//     protected float vertexCoords [];
//     protected float normalData [];
//     protected FloatBuffer vertexBuffer;
//     protected FloatBuffer normalBuffer;
//     protected FloatBuffer mTextureCoordinates;
//     protected float TextureCoordinateData [];
//     protected float translation [] = { 0.0f, 0.0f, 0.0f};
//     protected float rotation [] = { 0.0f, 0.0f, 0.0f};
//     protected float scale [] = { 1.0f, 1.0f, 1.0f };
//     protected float modelMatrix [] = new float[16];
//     protected int mTextureHandle;
//     protected final int mTextureCoordinateDataSize = 2;
//     protected int textureCode = R.drawable.bumpy_bricks_public_domain;
//     // protected int textureCode = -1; // No texture by default
//     protected final String vertexShaderCode =
//         // "uniform mat4 uMVPMatrix;" +
//         // "uniform mat4 uMVMatrix;" +

//         // "attribute vec2 aTextureCoordinate;" +
//         // "attribute vec4 vPosition;" +
//         // "attribute vec4 aColor;" +
//         // "attribute vec3 aNormal;" +

//         // "varying vec2 vTextureCoordinate;" +
//         // "varying vec3 aPosition;" +
//         // "varying vec4 vColor;" +
//         // "varying vec3 vNormal;" +
//         // "void main() {" +
//         // // "   vPosition = vec3(uMVMatrix * aPosition);" +
//         // // "   vNormal = vec3(uMVMatrix * vec4(aNormal, 0.0));" +
//         // "   gl_Position = uMVPMatrix * vPosition;" +
//         // "   vColor = aColor;" +
//         // "   vTextureCoordinate = aTextureCoordinate;" +
//         // "}";


//         // This matrix member variable provides a hook to manipulate
//         // the coordinates of the objects that use this vertex shader
//         "uniform mat4 uMVPMatrix;" +
//         "attribute vec4 vPosition;" +
//         "void main() {" +
//         // the matrix must be included as a modifier of gl_Position
//         // Note that the uMVPMatrix factor *must be first* in order
//         // for the matrix multiplication product to be correct.
//         "  gl_Position = uMPVMatrix * vPosition;" +
//         "}";
//     protected final String fragmentShaderCode =
//         // "precision mediump float;" +

//         // "uniform vec3 uLightPosition;" +
//         // "uniform sampler2D uTexture;" +
//         // "uniform vec4 vColor;" +

//         // "varying vec3 aPosition;" +
//         // "varying vec3 vNormal;" +

//         // "varying vec2 vTextureCoordinate;" +
//         // "void main() {" +
//         // // "    float distance = length(uLightPosition - vPosition);" +
//         // // "    vec3 lightVector = normalize(uLightPosition - vPosition);" +
//         // // "    float diffuse = max(dot(vNormal, lightVector), 0.1);" +
//         // // "    diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));" +
//         // // "    gl_FragColor = vColor * diffuse * texture2D(uTexture, vTextureCoordinate);" +
//         // "    gl_FragColor = vColor * texture2D(uTexture, vTextureCoordinate);" +
//         // "}";

//         "precision mediump float;" +
//         "uniform vec4 vColor;" +
//         "void main() {" +
//         "  gl_FragColor = vColor;" +
//         "}";
//     public Model(int mProgram, Context mContext){
//         this.mContext = mContext;
//         this.mProgram = mProgram;
//         this.createBuffer();
//         this.initProgram();
//     }
//     public Model(float modelCoords[], short drawOrder[], int mProgram, Context mContext) {
//         this.mContext = mContext;
//         this.modelCoords = modelCoords;
//         this.drawOrder = drawOrder;
//         this.mProgram = mProgram;
//         this.vertexCoords = this.createVertexData(modelCoords, drawOrder);
//         this.createBuffer();
//         this.initProgram();
//     }
//     public Model(float modelCoords[], short drawOrder[], Context mContext) {
//         this(modelCoords, drawOrder, GLES20.glCreateProgram(), mContext);
//     }
//     public Model(InputStream objInputStream, InputStream mtlInputStream, Context mContext) throws FileNotFoundException {
//         this.mContext = mContext;
//         this.mProgram = GLES20.glCreateProgram();
//         // BufferedReader mtlReader = null;
//         // if (mtlInputStream != null) {
//         //     mtlReader = new BufferedReader(new InputStreamReader(mtlInputStream));
//         // }
//         BufferedReader reader = new BufferedReader(new InputStreamReader(objInputStream));
//         List<Float> vertices = new ArrayList<>();
//         List<Short> faces = new ArrayList<>();
//         String line;
//         // while (true){
//         //     try {
//         //         if (mtlReader == null || (line = mtlReader.readLine()) == null) break;
//         //     } catch (IOException e) {
//         //         throw new RuntimeException(e);
//         //     }
//         //     String [] tokens = line.split(" ");
//         //     if (tokens[0].equals("newmtl")){
//         //         // ignore material names
//         //         continue;
//         //     }
//         //     else if (tokens[0].equals("map_Kd")){
//         //         // ignore texture map names
//         //         continue;
//         //     }
//         //     else if (tokens[0].equals("#")){
//         //         // ignore comments
//         //         continue;
//         //     }
//         //     else if (tokens[0].equals("map_Kd")){
//         //         String textureName = tokens[1].split(".")[0];
//         //         this.texture = this.mContext.getResources().getIdentifier(textureName, "drawable", this.mContext.getPackageName());
//         //     }
//         //     else {
//         //         // ignore other lines
//         //         continue;
//         //     }
//         // }
//         while (true){
//             try {
//                 if ((line = reader.readLine()) == null) break;
//             } catch (IOException e) {
//                 throw new RuntimeException(e);
//             }
//             String[] tokens = line.split(" ");
//             if (tokens[0].equals("v")){
//                 vertices.add(Float.parseFloat(tokens[1]));
//                 vertices.add(Float.parseFloat(tokens[2]));
//                 vertices.add(Float.parseFloat(tokens[3]));
//             }
//             else if (tokens[0].equals("f")){
//                 int verticesCount = tokens.length;
//                 short fv = (short) (Integer.parseInt(tokens[1].split("/")[0]) - 1); // vertex index
//                 for (int i = 2; i < verticesCount - 1; i++){
//                     short sv = (short) (Integer.parseInt(tokens[i].split("/")[0]) - 1); // vertex index
//                     short tv = (short) (Integer.parseInt(tokens[i + 1].split("/")[0]) - 1); // vertex index
//                     faces.add(fv);
//                     faces.add(sv);
//                     faces.add(tv);
//                 }
//             }
//             else if (tokens[0].equals("#")){
//                 // ignore comments
//                 continue;
//             }
//             else {
//                 // ignore other lines
//                 continue;
//             }
//         }
//         this.modelCoords = new float[vertices.size()];
//         for (int i=0; i<vertices.size(); ++i){
//             this.modelCoords[i] = vertices.get(i);
//         }
//         this.drawOrder = new short[faces.size()];
//         for (int i=0; i<faces.size(); ++i){
//             this.drawOrder[i] = faces.get(i);
//         }
//         this.vertexCoords = this.createVertexData(this.modelCoords, this.drawOrder);
//         this.createBuffer();
//         this.initProgram();
//     }
//     protected void createBuffer() {
//         if (this.vertexCoords != null && this.vertexBuffer == null) {
//             this.vertexBuffer = toBuffer(this.vertexCoords);
//         }
//         if (this.TextureCoordinateData != null && this.mTextureCoordinates == null) {
//             this.mTextureCoordinates = toBuffer(this.TextureCoordinateData);
//         }
//         if (this.normalData != null && this.normalData.length > 0) {
//             // Create a buffer for the normal data if it exists
//             this.normalBuffer = toBuffer(this.normalData);
//         }
//     }
//     protected float [] createVertexData(float[] geometryCoords, short[] drawOrder) {
//         float [] vertexData = new float[drawOrder.length * COORDS_PER_VERTEX];
//         for (int i = 0; i < drawOrder.length; i++){
//             short index = drawOrder[i];
//             vertexData[i * COORDS_PER_VERTEX] = geometryCoords[index * COORDS_PER_VERTEX];
//             vertexData[i * COORDS_PER_VERTEX + 1] = geometryCoords[index * COORDS_PER_VERTEX + 1];
//             vertexData[i * COORDS_PER_VERTEX + 2] = geometryCoords[index * COORDS_PER_VERTEX + 2];
//         }
//         return vertexData;
//     }
//     protected void initProgram() {
//         int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
//             vertexShaderCode);
//         int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
//             fragmentShaderCode);

//         // create empty OpenGL ES Program
//         GLES20.glAttachShader(this.mProgram, vertexShader);
//         GLES20.glAttachShader(this.mProgram, fragmentShader);        
        
//         // if (this.textureCode != -1) {
//         //     // Load texture if shader code is provided
//         //     this.mTextureHandle = MyGLRenderer.loadTexture(this.mContext, this.textureCode);
//         // }

//         GLES20.glLinkProgram(this.mProgram);
//     }
//     protected static FloatBuffer toBuffer(float[] data){
//         // Initialize a ByteBuffer for the data
//         ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.length * 4);
//         byteBuffer.order(ByteOrder.nativeOrder());
//         // Create a FloatBuffer from the ByteBuffer
//         FloatBuffer buffer = byteBuffer.asFloatBuffer();
//         // Put the data into the FloatBuffer
//         buffer.put(data);
//         // Set the position to the beginning
//         buffer.position(0);
//         return buffer;
//     }
//     public float[] getModelMatrix(){
//         // Reset the model matrix to identity
//         Matrix.setIdentityM(modelMatrix, 0);
//         // Apply translation
//         Matrix.translateM(modelMatrix, 0, translation[0], translation[1], translation[2]);
//         // Apply rotation
//         Matrix.rotateM(modelMatrix, 0, rotation[0], 1.0f, 0.0f, 0.0f);
//         Matrix.rotateM(modelMatrix, 0, rotation[1], 0.0f, 1.0f, 0.0f);
//         Matrix.rotateM(modelMatrix, 0, rotation[2], 0.0f, 0.0f, 1.0f);
//         // Apply scaling
//         Matrix.scaleM(modelMatrix, 0, scale[0], scale[1], scale[2]);
//         return modelMatrix;
//     }
//     public void setTranslation(float x, float y, float z) {
//         this.translation[0] = x;
//         this.translation[1] = y;
//         this.translation[2] = z;
//     }
//     public void setRotation(float x, float y, float z) {
//         this.rotation[0] = x;
//         this.rotation[1] = y;
//         this.rotation[2] = z;
//     }
//     public void setScale(float x, float y, float z) {
//         this.scale[0] = x;
//         this.scale[1] = y;
//         this.scale[2] = z;
//     }
//     public void draw(float[] vMatrix, float[] pMatrix, float[] rotationMatrix) {
//         // Use the shader program
//         // GLES20.glUseProgram(this.mProgram);

//         // // Get handles
//         // int mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
//         // int mMVMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVMatrix");
//         // // int positionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
//         // int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
//         // int colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
//         // int lightPositionHandle = GLES20.glGetUniformLocation(mProgram, "uLightPosition");
//         // int normalHandle = GLES20.glGetAttribLocation(mProgram, "aNormal");
//         // int mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "uTexture");
//         // int mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoordinate");

//         // // Enable and set vertex attribute
//         // GLES20.glEnableVertexAttribArray(positionHandle);
//         // GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
//         //         GLES20.GL_FLOAT, false, COORDS_PER_VERTEX * 4, vertexBuffer);

//         // // Set color uniform
//         // GLES20.glUniform4fv(colorHandle, 1, this.color, 0);

//         // // Prepare the model matrix
//         // modelMatrix = getModelMatrix();

//         // Model-View matrix
//         float[] mvMatrix = new float[16];
//         Matrix.multiplyMM(modelMatrix, 0, rotationMatrix, 0, modelMatrix, 0);
//         Matrix.multiplyMM(mvMatrix, 0, vMatrix, 0, modelMatrix, 0);

//         // MVP matrix
//         float[] mvpMatrix = new float[16];
//         Matrix.multiplyMM(mvpMatrix, 0, mvMatrix, 0, pMatrix, 0);

//         // // Pass matrices to shader
//         // // GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvMatrix, 0);
//         // GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

//         // // Set normal vector
//         // // if (this.normalData != null && normalHandle >= 0) {
//         // //     // Enable and set normal attribute
//         // //     normalBuffer.position(0);
//         // //     GLES20.glEnableVertexAttribArray(normalHandle);
//         // //     GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT, false, 0, normalBuffer);
//         // // }

//         // // Enable and set texture coordinates if available
//         // // if (this.mTextureCoordinates != null && mTextureCoordinateHandle >= 0) {
//         // //     GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
//         // //     GLES20.glVertexAttribPointer(
//         // //         mTextureCoordinateHandle,
//         // //         mTextureCoordinateDataSize,
//         // //         GLES20.GL_FLOAT,
//         // //         false,
//         // //         0,
//         // //         mTextureCoordinates
//         // //     );
//         // //     // Set active texture and bind
//         // //     GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//         // //     GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureHandle);
//         // //     GLES20.glUniform1i(mTextureUniformHandle, 0);
//         // // }

//         // // Draw
//         // GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCoords.length / COORDS_PER_VERTEX);

//         // // Disable attribute arrays
//         // GLES20.glDisableVertexAttribArray(positionHandle);
//         // if (this.mTextureCoordinates != null && mTextureCoordinateHandle >= 0) {
//         //     GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle);
//         // }

//         // Add program to OpenGL ES environment
//         GLES20.glUseProgram(this.mProgram);

//         // get handle to vertex shader's vPosition member
//         int positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
//         int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

//         // Enable a handle to the triangle vertices
//         GLES20.glEnableVertexAttribArray(positionHandle);

//         // Prepare the triangle coordinate data
//         GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX,
//                                     GLES20.GL_FLOAT, false,
//                                     vertexStride, vertexBuffer);

//         // get handle to fragment shader's vColor member
//         int colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

//         // Set color for drawing the triangle
//         GLES20.glUniform4fv(colorHandle, 1, color, 0);
        
//         // get handle to shape's transformation matrix
//         int vPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

//         // Pass the projection and view transformation to the shader
//         GLES20.glUniformMatrix4fv(vPMatrixHandle, 1, false, mvpMatrix, 0);

//         // Draw the triangle
//         GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCoords.length / COORDS_PER_VERTEX);

//         // Disable vertex array
//         GLES20.glDisableVertexAttribArray(positionHandle);
//     }
// }

// class Triangle extends Model {
//     static float defaultTriangleCoords[] = {
//             0.0f,  0.622008459f, 0.0f,   // top
//             -0.5f, -0.311004243f, 0.0f,   // bottom left
//             0.5f, -0.311004243f, 0.0f }; // bottom right
//     static short defaultDrawOrder[] = { 0, 1, 2 }; // order to draw vertices
//     public Triangle(Context context) {
//         super(defaultTriangleCoords, defaultDrawOrder, GLES20.glCreateProgram(), context);
//     }
//     public Triangle(float[] triangleCoords, Context context) {
//         super(triangleCoords, defaultDrawOrder, GLES20.glCreateProgram(), context);
//     }
// }

// class Square extends Model {
//     static float defaultSquareCoords[] = {
//         -0.5f,  0.5f, 0.0f,   // top left
//         -0.5f, -0.5f, 0.0f,   // bottom left
//         0.5f, -0.5f, 0.0f,   // bottom right
//         0.5f,  0.5f, 0.0f, }; // top right
//     static short defaultDrawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices
//     static float defaultTextureCoordinateData[] = {
//         0.0f, 0.0f, // top left
//         0.0f, 1.0f, // bottom left
//         1.0f, 1.0f, // bottom right
//         0.0f, 0.0f, // top left
//         1.0f, 1.0f, // bottom right
//         1.0f, 0.0f  // top right
//     };
    
//     public Square(Context context) {
//         super(defaultSquareCoords, defaultDrawOrder, GLES20.glCreateProgram(), context);
//         this.TextureCoordinateData = defaultTextureCoordinateData;
//         this.createBuffer();
//     }
//     public Square(float[] squareCoords, Context context) {
//         super(squareCoords, defaultDrawOrder, GLES20.glCreateProgram(), context);
//         this.TextureCoordinateData = defaultTextureCoordinateData;
//         this.createBuffer();
//     }
// }

// class Cube extends Model {
//     static float defaultCubeCoords[] = {
//         -0.5f,  0.5f, 0.5f,   // top left front
//         -0.5f, -0.5f, 0.5f,   // bottom left front
//         0.5f, -0.5f, 0.5f,   // bottom right front
//         0.5f,  0.5f, 0.5f,   // top right front
//         -0.5f,  0.5f,  -0.5f,   // top left back
//         -0.5f, -0.5f,  -0.5f,   // bottom left back
//         0.5f, -0.5f,  -0.5f,   // bottom right back
//         0.5f,  0.5f,  -0.5f }; // top right back
//     static short defaultDrawOrder[] = {
//         0, 1, 2, 0, 2, 3, // front face
//         4, 7, 6, 4, 6, 5, // back face
//         0, 4, 5, 0, 5, 1, // left face
//         2, 6, 7, 2, 7, 3, // right face
//         0, 3, 7, 0, 7, 4, // top face
//         1, 5, 6, 1, 6, 2, // bottom face
//     };
//     static float defaultNormalData [] = {
//         0.0f, 0.0f, 1.0f,				
//         0.0f, 0.0f, 1.0f,
//         0.0f, 0.0f, 1.0f,
//         0.0f, 0.0f, 1.0f,				
//         0.0f, 0.0f, 1.0f,
//         0.0f, 0.0f, 1.0f,
        
//         // Right face 
//         1.0f, 0.0f, 0.0f,				
//         1.0f, 0.0f, 0.0f,
//         1.0f, 0.0f, 0.0f,
//         1.0f, 0.0f, 0.0f,				
//         1.0f, 0.0f, 0.0f,
//         1.0f, 0.0f, 0.0f,
        
//         // Back face 
//         0.0f, 0.0f, -1.0f,				
//         0.0f, 0.0f, -1.0f,
//         0.0f, 0.0f, -1.0f,
//         0.0f, 0.0f, -1.0f,				
//         0.0f, 0.0f, -1.0f,
//         0.0f, 0.0f, -1.0f,
        
//         // Left face 
//         -1.0f, 0.0f, 0.0f,				
//         -1.0f, 0.0f, 0.0f,
//         -1.0f, 0.0f, 0.0f,
//         -1.0f, 0.0f, 0.0f,				
//         -1.0f, 0.0f, 0.0f,
//         -1.0f, 0.0f, 0.0f,
        
//         // Top face 
//         0.0f, 1.0f, 0.0f,			
//         0.0f, 1.0f, 0.0f,
//         0.0f, 1.0f, 0.0f,
//         0.0f, 1.0f, 0.0f,				
//         0.0f, 1.0f, 0.0f,
//         0.0f, 1.0f, 0.0f,
        
//         // Bottom face 
//         0.0f, -1.0f, 0.0f,			
//         0.0f, -1.0f, 0.0f,
//         0.0f, -1.0f, 0.0f,
//         0.0f, -1.0f, 0.0f,				
//         0.0f, -1.0f, 0.0f,
//         0.0f, -1.0f, 0.0f,
//     };
//     static float defaultTextureCoordinateData[] = {
//         // Front face
//         0.0f, 0.0f, 				
//         0.0f, 1.0f,
//         1.0f, 1.0f,
//         0.0f, 0.0f,
//         1.0f, 1.0f,
//         1.0f, 0.0f,				
        
//         // Back face 
//         0.0f, 0.0f, 				
//         0.0f, 1.0f,
//         1.0f, 1.0f,
//         0.0f, 0.0f,
//         1.0f, 1.0f,
//         1.0f, 0.0f,	
        
//         // Left face 
//         0.0f, 0.0f, 				
//         0.0f, 1.0f,
//         1.0f, 1.0f,
//         0.0f, 0.0f,
//         1.0f, 1.0f,
//         1.0f, 0.0f,	
        
//         // Right face 
//         0.0f, 0.0f, 				
//         0.0f, 1.0f,
//         1.0f, 1.0f,
//         0.0f, 0.0f,
//         1.0f, 1.0f,
//         1.0f, 0.0f,	
        
//         // Top face 
//         0.0f, 0.0f, 				
//         0.0f, 1.0f,
//         1.0f, 1.0f,
//         0.0f, 0.0f,
//         1.0f, 1.0f,
//         1.0f, 0.0f,	
        
//         // Bottom face 
//         0.0f, 0.0f,
//         0.0f, 1.0f,
//         1.0f, 1.0f,
//         0.0f, 0.0f,
//         1.0f, 1.0f,
//         1.0f, 0.0f,
//     };
//     public Cube(Context context) {
//         super(GLES20.glCreateProgram(),context);
//         this.vertexCoords = this.createVertexData(defaultCubeCoords, defaultDrawOrder);
//         this.TextureCoordinateData = defaultTextureCoordinateData;
//         this.normalData = defaultNormalData;
//         this.createBuffer();
//     }
//     public Cube(float[] cubeCoords, Context context) {
//         super(GLES20.glCreateProgram(),context);
//         this.vertexCoords = this.createVertexData(cubeCoords, defaultDrawOrder);
//         this.TextureCoordinateData = defaultTextureCoordinateData;
//         this.normalData = defaultNormalData;
//         this.createBuffer();
//     }
// }


public class MyGLRenderer implements GLSurfaceView.Renderer {

    private Context mContext;

    private Shape mShape;
    private Cube mCube;
    
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

        mShape = new Shape(this.mContext);
        mCube = new Cube(this.mContext);
    }

    public void onDrawFrame(GL10 unused) {
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        Matrix.setLookAtM(viewMatrix, 0, 3.0f, 3.0f, 5.0f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        // Calculate the projection and view transformation
        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        Matrix.setRotateM(rotationMatrix, 0, mAngle, 0f, 1.0f, 0.0f);
//         // Combine the rotation matrix with the projection and camera view
//         // Note that the vPMatrix factor *must be first* in order
//         // for the matrix multiplication product to be correct.
//         // Draw shape
//         Model model = null;
//         try {
//             AssetManager assetManager = this.mContext.getAssets();
//             InputStream objInputStream = assetManager.open("teapot.obj");
//             InputStream mtlInputStream = null;
//             try {
//                 mtlInputStream = assetManager.open("teapot.mtl");
//             } catch (FileNotFoundException e) {
//                 // If the mtl file is not found, we can still create the model without it
//                 mtlInputStream = null; // No material file
//             }
//             model = new Model(objInputStream, mtlInputStream, this.mContext);
//         } catch (IOException e) {
//             throw new RuntimeException(e);
//         }
        //  mShape.draw(viewMatrix, projectionMatrix, rotationMatrix);
       mCube.draw(viewMatrix, projectionMatrix, rotationMatrix);    
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
    protected int mProgram;
    protected Context mContext;
    static public int COORDS_PER_VERTEX = 3;
    protected float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
    protected float modelCoords [] = {   // in counterclockwise order:
        0.0f,  0.622008459f, 0.0f, // top
        -0.5f, -0.311004243f, 0.0f, // bottom left
        0.5f, -0.311004243f, 0.0f,  // bottom right
    };
    protected short drawOrder [] = { 0, 1, 2 }; // order to draw vertices
    protected float vertexCoords [];
    protected float normalData [];
    protected FloatBuffer vertexBuffer;
    protected FloatBuffer normalBuffer;
    protected FloatBuffer mTextureCoordinatesBuffer;
    protected float TextureCoordinateData [];
    protected float translation [] = { 0.0f, 0.0f, 0.0f};
    protected float rotation [] = { 0.0f, 0.0f, 0.0f};
    protected float scale [] = { 1.0f, 1.0f, 1.0f };
    protected float modelMatrix [] = new float[16];
    protected int mTextureHandle;
    protected final int mTextureCoordinateDataSize = 2;
    protected int textureCode = R.drawable.bumpy_bricks_public_domain;
    // protected int textureCode = -1; // No texture by default

    private final String vertexShaderCode =
        "attribute vec4 vPosition;" +
        "uniform mat4 uMVPMatrix;" +
        "void main() {" +
        "   gl_Position = uMVPMatrix * vPosition;" +
        "}";

    private final String fragmentShaderCode =
        "precision mediump float;" +
        "uniform vec4 vColor;" +
        "void main() {" +
        "  gl_FragColor = vColor;" +
        "}";

    // Set color with red, green, blue and alpha (opacity) values

    private int positionHandle;
    private int colorHandle;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public Shape(Context context) {
        this.mContext = context;
        this.mProgram = GLES20.glCreateProgram();
        // initialize vertex byte buffer for shape coordinates
        this.vertexCoords = this.createVertexData(this.modelCoords, this.drawOrder);
        this.createBuffer();
        this.initProgram();
    }
    protected void createBuffer() {
        if (this.vertexCoords != null) {
            this.vertexBuffer = toBuffer(this.vertexCoords);
        }
        if (this.TextureCoordinateData != null) {
            this.mTextureCoordinatesBuffer = toBuffer(this.TextureCoordinateData);
        }
        if (this.normalData != null) {
            // Create a buffer for the normal data if it exists
            this.normalBuffer = toBuffer(this.normalData);
        }
    }
    protected static FloatBuffer toBuffer(float[] data){
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
        GLES20.glAttachShader(this.mProgram, vertexShader);
        GLES20.glAttachShader(this.mProgram, fragmentShader);        
        
        // if (this.textureCode != -1) {
        //     // Load texture if shader code is provided
        //     this.mTextureHandle = MyGLRenderer.loadTexture(this.mContext, this.textureCode);
        // }

        GLES20.glLinkProgram(this.mProgram);
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

    public void draw(float [] vMatrix, float[] pMatrix, float[] rotationMatrix) {
        // Add program to OpenGL ES environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        // get handle to fragment shader's vColor member
        colorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

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

        // get handle to shape's transformation matrix
        int mvpMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0);

        // Draw the triangle
        // GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCoords.length / COORDS_PER_VERTEX);

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
        this.vertexCoords = this.createVertexData(defaultCubeCoords, defaultDrawOrder);
        this.TextureCoordinateData = defaultTextureCoordinateData;
        this.normalData = defaultNormalData;
        this.createBuffer();
    }
    public Cube(float[] cubeCoords, Context context) {
        super(context);
        this.vertexCoords = this.createVertexData(cubeCoords, defaultDrawOrder);
        this.TextureCoordinateData = defaultTextureCoordinateData;
        this.normalData = defaultNormalData;
        this.createBuffer();
    }
}