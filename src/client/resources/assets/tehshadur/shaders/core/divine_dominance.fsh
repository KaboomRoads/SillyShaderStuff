#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;

uniform float GameTime;

in vec2 texCoord0;
in vec2 repeat;
in vec3 normal;

out vec4 fragColor;

float lerp(float delta, float start, float end) {
    return start + delta * (end - start);
}

const float SCROLL_SPEED = -1500.0;
const float VERTICAL_SCALE = 25.0;
const float WAVE_FREQUENCY = 0.75;
const float WAVE_AMPLITUDE = 2.0;
const float WAVE_OFFSET = 0.75;
const vec2 CENTER = vec2(0.5);

float getScrollOffset(vec2 texCoord, vec3 normal) {
    float timeComponent = GameTime * SCROLL_SPEED;
    float scrollInput = (normal.y != 0.0)
    ? max(abs(texCoord.x - CENTER.x), abs(texCoord.y - CENTER.y)) * VERTICAL_SCALE +
    timeComponent * sign(normal.y) +
    (normal.y < 0.0 ? VERTICAL_SCALE : VERTICAL_SCALE * 0.5)
    : texCoord.y * VERTICAL_SCALE + timeComponent;
    return max(sin(scrollInput * WAVE_FREQUENCY) * WAVE_AMPLITUDE - WAVE_OFFSET, 0.0);
}

void main() {
    float scroll = getScrollOffset(texCoord0, normal);
    vec4 color = texture(Sampler0, texCoord0 * repeat);
    if (color.a == 0.0) discard;
    vec4 fc = color * ColorModulator * vec4(0.25, 0.25, 0.25, 1.0);
    fragColor = vec4(
    lerp(scroll, fc.r, 1.0),
    lerp(scroll, fc.g, 0.85),
    lerp(scroll, fc.b, 0.6),
    lerp(scroll, 0.75, 1.0)
    );
}
