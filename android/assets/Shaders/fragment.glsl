#extension GL_EXT_shader_texture_lod : enable
#extension GL_OES_standard_derivatives : enable


#ifdef GL_ES
precision mediump float;
#endif


varying vec4 v_color;
varying vec2 v_texCoords;
varying vec4 v_pos;

uniform sampler2D u_texture;

float Light()
{
    vec3 ndc_pos = v_pos .xyz / v_pos.w;
    vec3 dx      = dFdx(ndc_pos);
    vec3 dy      = dFdy(ndc_pos);

    vec3 N = normalize(cross(dx, dy));
    N *= sign(N.z);

    vec3 L = vec3(0.0, 0.0, 1.0);
    float NdotL = dot(N, L);

    return NdotL;
}


void main()
{
    float l = Light();
    gl_FragColor = l * texture2D(u_texture, v_texCoords);
}