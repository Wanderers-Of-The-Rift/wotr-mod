#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform float GameTime;
uniform vec2 View;
uniform vec2 ScreenSize;
uniform float RotationSpeed;

in vec2 texCoord0;
in float dist;

out vec4 fragColor;

const float PIXELATE = 64.0;
const float OPEN_DISTANCE = 3;
const float CLOSE_DISTANCE = 16;
const float PI = 3.14159265359;

// #########################################################
//     INNER CODE TAKEN FROM MY (WH4I3'S) SKYBOX SHADER
// #########################################################
vec2 getInnerUV(vec3 t3) {
    vec2 t2;
    t3=normalize(t3)/sqrt(2.0);
    vec3 q3=abs(t3);
    if ((q3.x>=q3.y)&&(q3.x>=q3.z)) {
        t2.x=0.5-t3.z/t3.x;
        t2.y=0.5-t3.y/q3.x;
    }
    else if ((q3.y>=q3.x)&&(q3.y>=q3.z)) {
        t2.x=0.5+t3.x/q3.y;
        t2.y=0.5+t3.z/t3.y;
    }
    else {
        t2.x=0.5+t3.x/t3.z;
        t2.y=0.5-t3.y/q3.z;
    }
    return t2;
}

vec2 getInnerUVOffset(vec3 dir1) {
    dir1=normalize(dir1)/sqrt(2.0);
    vec3 dir=abs(dir1);
    // EAST
    if ((dir.x>=dir.y)&&(dir.x>=dir.z)&&(dir1.x > 0.0)) {
        return vec2(2.0, 1.0);
    }
    // WEST
    if ((dir.x>=dir.y)&&(dir.x>=dir.z)&&(dir1.x < 0.0)) {
        return vec2(0.0, 1.0);
    }
    // UP
    if ((dir.y>=dir.x)&&(dir.y>=dir.z)&&(dir1.y > 0.0)) {
        return vec2(1.0, 0.0);
    }
    // DOWN
    if ((dir.y>=dir.x)&&(dir.y>=dir.z)&&(dir1.y < 0.0)) {
        return vec2(1.0, 2.0);
    }
    // SOUTH
    if (dir1.z > 0.0) {
        return vec2(1.0, 1.0);
    }
    // NORTH
    return vec2(3.0, 1.0);
}

mat2 rotate2d(float theta) {
    float s = sin(theta), c = cos(theta);
    return mat2(c, -s, s, c);
}

mat3 camera(vec3 cameraPos, vec3 lookAtPoint) {
    vec3 cd = normalize(lookAtPoint - cameraPos);
    vec3 cr = normalize(cross(vec3(0, 1, 0), cd));
    vec3 cu = normalize(cross(cd, cr));

    return mat3(-cr, cu, -cd);
}

vec4 getInner() {
    float iTime = GameTime * 1000.0;
    vec2 uv = (gl_FragCoord.xy - 0.5 * ScreenSize.xy) / ScreenSize.yy;

    vec3 lp = vec3(0);
    vec3 ro = vec3(0, 0, 10);
    ro.yz *= rotate2d(mix(-PI/2., PI/2., 0.5 - View.x / 180.1));
    ro.xz *= rotate2d(mix(-PI, PI, (View.y + RotationSpeed * iTime) / 360.0));

    vec3 rd = camera(ro, lp) * normalize(vec3(uv, -0.5));

    return texture(Sampler1, (getInnerUV(rd)/2.0+0.25+getInnerUVOffset(rd))/4.0);
}


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

    if (color.x <= 0.01) {
        fragColor = getInner();
        return;
    }
    
    vec3 lines = vec3(0.0);
    lines += mix(-1.5, 1.0, texture(Sampler0, (uv + vec2(0.0, iTime * 3.0)) / vec2(0.2, 10.0)).x);
    lines = clamp(lines, vec3(0.0), vec3(1.0));
    lines = vec3(round(lines.x));
    lines *= mask;

    fragColor = vec4(color + lines, mask);
}