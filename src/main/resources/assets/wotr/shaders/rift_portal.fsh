#version 150

#moj_import <minecraft:matrix.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform float InnerScale;

in vec2 texCoord0;
in vec4 texProj0;
in vec4 texProj1;

out vec4 fragColor;

void main() {
    vec4 color = texture(Sampler0, texCoord0);
#ifdef ALPHA_CUTOUT
    if (color.a < ALPHA_CUTOUT) {
        discard;
    }
#endif

    if (color.x < 0.1) {
        vec2 centeredCoords = (texProj0.xy / texProj0.w) - 0.5;
        vec2 texCoord = InnerScale * vec2(centeredCoords.x, centeredCoords.y / texProj0.z) + 0.5;
        float w = texProj0.w;
        if (texCoord.x > 1.0 || texCoord.x < 0.0) {
            centeredCoords = (texProj1.xy / texProj1.w) - 0.5;
            texCoord = InnerScale * vec2(centeredCoords.x, centeredCoords.y / texProj1.z) + 0.5;
            w = texProj1.w;
        }
        vec2 texCoord1 = clamp(texCoord, 0.0, 1.0);
        texCoord1.y = 1.0 - sign(w) * texCoord1.y;
        color = texture(Sampler1, texCoord1);
    }

    fragColor = color;
}