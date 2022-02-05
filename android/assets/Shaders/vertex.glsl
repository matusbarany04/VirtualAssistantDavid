attribute vec3 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;

uniform mat4 u_worldTrans;
uniform mat4 u_projViewTrans;

varying vec2 v_texCoords;
varying vec4 v_color;
varying vec4 v_pos;


void main()
{
    v_texCoords = a_texCoord0;
    v_color = a_color;
    v_pos = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);

    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);
}