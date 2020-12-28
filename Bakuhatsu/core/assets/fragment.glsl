#ifdef GL_ES
precision mediump float;
#endif

uniform float v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;

void main() {
    gl_FragColor = texture2D(u_texture, v_texCoords);
    gl_FragColor.r += v_color;
}
