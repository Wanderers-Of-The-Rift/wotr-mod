#version 150

in vec3 Position;
in vec2 UV0;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;

out vec2 texCoord0;
out float dist;

void main() {
    vec4 cameraPos = ModelViewMat * vec4(Position, 1.0);
    dist = -cameraPos.z;
    gl_Position = ProjMat * cameraPos;     
    texCoord0 = UV0.xy;
}