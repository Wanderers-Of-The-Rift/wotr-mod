#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform float GameTime;
uniform float InnerScale;

in vec2 texCoord0;
in vec4 texProj0;
in vec4 texProj1;
in float dist;

out vec4 fragColor;

const float PIXELATE = 64.0;
const float OPEN_DISTANCE = 3;
const float CLOSE_DISTANCE = 16;

void main() {
    float iTime = GameTime * 1000.0;
    vec2 UV0 = round(texCoord0 * PIXELATE) / PIXELATE;

    float distance0 = clamp(1.0 - ((dist - OPEN_DISTANCE) / (CLOSE_DISTANCE - OPEN_DISTANCE)), 0.0, 1.0);

    float sharpness = mix(4.0, 0.3, distance0);
    float width = mix(0.5, 1.25, distance0);
    float edgeThickness = mix(0.9, 0.99, distance0);
    
    vec2 uv = vec2(
        ((UV0.x * 2.0) - 1.0) / width + 0.5,
        UV0.y
    );
    
    float noise = texture(Sampler0, UV0 + vec2(iTime / 10.0, iTime / 2.0)).x;
    float noiseStrength = 0.05;
    float innerStrength = 0.1;

    float dist1 = distance(uv, vec2(0.5));
    vec2 dist2 = vec2(
        distance(uv.x, 0.5),
        distance(uv.y, 0.5)
    );
    
    float mixValue1 = round(
        mix(dist1, dist2.x + dist2.y, sharpness) + noise * noiseStrength
    );
    float mixValue2 = round(
        mix(dist1 / edgeThickness, dist2.x / edgeThickness * 1.4 + dist2.y / edgeThickness, sharpness) + noise * innerStrength
    );
    
    vec3 color1 = mix(vec3(1.0), vec3(0.0), mixValue1);
    float edge = mix(vec3(0.0), vec3(1.0), mixValue2).x;
    
    vec3 color = mix(vec3(0.0, 0.0, 0.0), color1, edge);
    float mask = mix(vec3(1.0), color1, edge).x;

    if (mask <= 0.0) {
        discard;
    }
    
    vec3 lines = vec3(0.0);
    lines += mix(-1.5, 1.0, texture(Sampler0, (uv + vec2(0.0, iTime * 3.0)) / vec2(0.2, 10.0)).x);
    lines = clamp(lines, vec3(0.0), vec3(1.0));
    lines = vec3(round(lines.x));
    lines *= mask;

    if (color.x <= 0.01) {
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
        color = texture(Sampler1, texCoord1).rgb;
    }

    fragColor = vec4(color + lines, mask);
}