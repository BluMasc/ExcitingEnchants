#version 150

uniform sampler2D DiffuseSampler;
uniform vec2 InSize;
uniform float GameTime;

out vec4 fragColor;

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
    vec2 uv = gl_FragCoord.xy / InSize;
    float time = GameTime /100.0;

    // Gentle warp
    float warpX = sin(uv.y * 12.0 + time * 2.1) * 0.004
                + sin(uv.y * 5.0  - time * 1.3) * 0.003;
    float warpY = sin(uv.x * 10.0 + time * 1.7) * 0.004
                + sin(uv.x * 7.0  - time * 2.5) * 0.002;

    vec2 warpedUV = uv + vec2(warpX, warpY);
    vec4 baseColor = texture(DiffuseSampler, warpedUV);

    // Waveform ripple
    float wave1 = sin(uv.x * 20.0 + uv.y * 15.0 + time * 3.0);
    float wave2 = sin(uv.x * -13.0 + uv.y * 22.0 + time * 2.2);
    float wave3 = sin(uv.x * 17.0  - uv.y * 9.0  - time * 1.8);
    float waveNorm = ((wave1 + wave2 + wave3) / 3.0) * 0.5 + 0.5;

    // Rainbow
    float hue = waveNorm + time * 0.02;
    vec3 rainbow = hsv2rgb(vec3(hue, 0.9, 1.0));

    // Oil-slick blend
    float luma = dot(baseColor.rgb, vec3(0.299, 0.587, 0.114));
    float shimmerStrength = 0.18 + luma * 0.10;

    vec3 finalColor = mix(baseColor.rgb, rainbow, shimmerStrength * (waveNorm * 0.6 + 0.4));

    fragColor = vec4(finalColor, 1.0);
}
