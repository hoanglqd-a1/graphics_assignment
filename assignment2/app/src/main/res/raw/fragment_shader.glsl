precision mediump float;

uniform vec3 uLightPosition;
uniform vec3 uViewPosition;
uniform sampler2D uTexture;
uniform vec3 Ka;
uniform vec3 Kd;
uniform vec3 Ks;
uniform float Ns;

varying vec3 vPosition;
varying vec4 vColor;
varying vec3 vNormal;
varying vec2 vTextureCoordinate;

void main() {
    float distance = length(uLightPosition - vPosition);
    vec3 lightVector = normalize(uLightPosition - vPosition);
    vec3 viewVector = normalize(uViewPosition - vPosition);
    vec3 reflectVector = reflect(-lightVector, vNormal);

    vec3 ambient = Ka;
    float diff = max(dot(vNormal, lightVector), 0.1);
    vec3 diffuse = Kd * diff;
    float spec = pow(max(dot(viewVector, reflectVector), 0.0), 32.0);
    vec3 specular = Ks * spec;
    vec4 lightning = vec4(ambient + diffuse + specular, 0);
    gl_FragColor = vColor * lightning;
//   gl_FragColor = vColor;
}