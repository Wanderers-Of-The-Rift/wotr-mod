#version 150

#moj_import <wotr:simplex3d.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float GameTime;
uniform vec2 ScreenSize;

in vec2 texCoord0;
in vec4 vertexColor;
in float effects;

out vec4 fragColor;

vec4 grid(vec2 uv, vec2 grid_size, vec2 screenUV) {
    float x = round(abs(sin(uv.x * grid_size.x)));
    float y = round(abs(sin(uv.y * grid_size.y)));
    float gridValue = floor(clamp(1.0 - (x + y) / 2.0, 0.0, 1.0) * 1.2)/1.2;
    float gridAlpha = (sin(GameTime * 1000.0 + screenUV.x*100.0) + 1.0) / 2.0 * 0.6 + 0.1;
    return vec4(vec3(gridValue), gridAlpha);
}

vec4 edgeHighlight(vec2 uv) {
    float distanceToMid = distance(vec2(0.5, 0.5), uv);
    distanceToMid += clamp(simplex3d(vec3(uv*10.0, GameTime*600.0)) + 0.5, 0.0, 1.0) * 0.1;
    distanceToMid = clamp(1.0 - distanceToMid, 0.0, 1.0);
    return vec4(1.0, 1.0, 1.0, distanceToMid);
}

float average(vec3 vector) {
    return (vector.r + vector.g + vector.b) / 3.0;
}

void main() {
    vec2 screenUV = gl_FragCoord.xy/ScreenSize;
    vec2 screenUVSquare = gl_FragCoord.xy/ScreenSize.y;
    vec2 uv = texCoord0;

    if (vertexColor.a < 0.1) {
        discard;
    }

    int effect = int(effects);

    float darkenMiddle = distance(vec2(0.5), uv) * 2.0 + 0.2;
    darkenMiddle -= clamp(simplex3d(vec3(uv*3.0, GameTime*600.0)) + 0.5, 0.0, 1.0) * 0.1;
    darkenMiddle = round(darkenMiddle * 10.0) / 10.0;

    vec2 gridUV = vec2(uv.x, uv.y + GameTime * 70.0);
    vec4 gridOutput = grid(gridUV, vec2(ScreenSize.y/50.0), screenUV);

    vec4 edgeHighlightOutput = edgeHighlight(uv);

    vec4 final = vertexColor;
    // Adding the dark middle
    final = mix(vec4(vec3(0.0), vertexColor.a/2.0 + 1.0 * floor(vertexColor.a)), final, darkenMiddle);
    // Adding the spots
    final = mix(final, vec4(final.rgb + gridOutput.rgb * gridOutput.a, final.a + average(gridOutput.rgb) * gridOutput.a), effect & 1);
    // Adding the edge hightlights
    final = mix(final, mix(vec4(edgeHighlightOutput.rgb, 1.0), final, edgeHighlightOutput.a), (effect & 2) / 2.0);

    fragColor = final;
}
