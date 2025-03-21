#version 150

in vec3 Position;
in vec2 UV0;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float GameTime;
uniform float Frames0;
uniform float FrameRate0;

out vec4 texProj0;
out vec4 texProj1;
out vec2 texCoord0;

vec4 getPortalProjection(vec3 position) {
    vec4 portalViewPos = ProjMat * vec4(position, 1.0);
    vec4 portalProjection = 0.5 * portalViewPos;
    portalProjection.xy = 0.5 * vec2(portalViewPos.x + portalViewPos.w, portalViewPos.y + portalViewPos.w);
    portalProjection.zw = portalViewPos.zw;
    return portalProjection;
}

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
    float frameSize0 = 1.0 / Frames0;
    float frame = floor(FrameRate0 * GameTime * 1200.0);
    texCoord0 = vec2(UV0.x, frameSize0 * (frame + UV0.y));

    float aspect = ProjMat[1][1]/ProjMat[0][0];
    texProj0 = getPortalProjection(Position);
    texProj0.z = aspect;
    texProj1 = getPortalProjection(vec3(Position.z, Position.y, -Position.x));
    texProj1.z = aspect;
}