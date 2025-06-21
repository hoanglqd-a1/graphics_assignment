uniform vec4 uColor;
uniform mat4 uViewMatrix;
uniform mat4 uProjectionMatrix;
uniform mat4 uRotationMatrix;

attribute vec4 aModelMatrixRow1;
attribute vec4 aModelMatrixRow2;
attribute vec4 aModelMatrixRow3;
attribute vec4 aModelMatrixRow4;
attribute vec2 aTextureCoordinate;
attribute vec4 aPosition;
attribute vec3 aNormal;

varying vec3 vPosition;
varying vec4 vColor;
varying vec3 vNormal;
varying vec2 vTextureCoordinate;

void main() {
//    vPosition = vec3(uMVMatrix * aPosition);
   mat4 aModelMatrix = mat4(aModelMatrixRow1,
                           aModelMatrixRow2,
                           aModelMatrixRow3,
                           aModelMatrixRow4);
   aModelMatrix = aModelMatrix * uRotationMatrix;
   vColor = uColor;
//    vNormal = vec3(uMVMatrix * vec4(aNormal, 0.0));
   gl_Position = uProjectionMatrix * uViewMatrix * aModelMatrix * aPosition;
//    vTextureCoordinate = aTextureCoordinate;
   gl_PointSize = 30.0;
}