uniform vec4 uColor;
uniform mat4 uMVPMatrix;
uniform mat4 uMVMatrix;
uniform bool uUseTexture;
uniform bool uUseNormal;

attribute vec2 aTextureCoordinate;
attribute vec4 aPosition;
attribute vec3 aNormal;

varying vec3 vPosition;
varying vec4 vColor;
varying vec3 vNormal;
varying vec2 vTextureCoordinate;

void main() {
    vPosition = vec3(uMVMatrix * aPosition);
    vColor = uColor;
    if (uUseNormal){
        vNormal = vec3(uMVMatrix * vec4(aNormal, 0.0));
    }
    gl_Position = uMVPMatrix * aPosition;
    if (uUseTexture){
        vTextureCoordinate = aTextureCoordinate;
    }
    gl_PointSize = 30.0;
}