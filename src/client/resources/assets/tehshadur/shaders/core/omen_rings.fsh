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

const float SCROLL_SPEED = 100000.0;
const float VERTICAL_SCALE = 250.0;
const float WAVE_FREQUENCY = 0.025;
const float WAVE_AMPLITUDE = 30.0;
const float WAVE_OFFSET = 25;
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
    vec4 color = texture(Sampler0, (normal.y != 0.0) ? texCoord0 * 2.0 : texCoord0 * repeat);
    if (color.a == 0.0 || scroll == 0) discard;
    vec4 fc = color * ColorModulator;
    fragColor = vec4(
    lerp(scroll, fc.r, 0),
    lerp(scroll, fc.g, 0.75),
    lerp(scroll, fc.b, 0.75),
    lerp(scroll, 0.0, 1.0)
    );
}
