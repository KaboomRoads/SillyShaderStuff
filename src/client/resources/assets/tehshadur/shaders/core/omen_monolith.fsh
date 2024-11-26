#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;

uniform float GameTime;

in vec2 texCoord0;
in vec2 repeat;

out vec4 fragColor;

float lerp(float delta, float start, float end) {
    return start + delta * (end - start);
}

const float SCROLL_SPEED = -1500.0;
const float VERTICAL_SCALE = 25.0;
const float WAVE_FREQUENCY = 1.0;
const float WAVE_AMPLITUDE = 2.0;
const float WAVE_OFFSET = 0.75;

float getScrollOffset(vec2 texCoord) {
    float timeComponent = GameTime * SCROLL_SPEED;
    float cx = 0.0;
    float cy = 0.0;
    if (texCoord0.x > 0.5)cx = 1.0;
    if (texCoord0.y > 0.5)cy = 1.0;
    float scrollInput = max(abs(texCoord.x - cx), abs(texCoord.y - cy)) * VERTICAL_SCALE + timeComponent + VERTICAL_SCALE;
    return max(sin(scrollInput * WAVE_FREQUENCY) * WAVE_AMPLITUDE - WAVE_OFFSET, 0.0);
}

void main() {
    float scroll = getScrollOffset(texCoord0);
    vec4 color = texture(Sampler0, texCoord0 * repeat);
    if (color.a == 0.0) discard;
    vec4 fc = color * ColorModulator * vec4(0.25, 0.25, 0.25, 1.0);
    fragColor = vec4(
    lerp(scroll, fc.r, 0),
    lerp(scroll, fc.g, 0.75),
    lerp(scroll, fc.b, 0.75),
    lerp(scroll, 0.75, 1.0)
    );
}
