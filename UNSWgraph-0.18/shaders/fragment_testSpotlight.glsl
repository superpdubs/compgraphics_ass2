//Fragment Shader
out vec4 outputColor;

uniform vec4 input_color;

uniform mat4 view_matrix;

uniform vec3 lightPos;
uniform vec3 lightIntensity;
uniform vec3 ambientIntensity;

uniform vec3 ambientCoeff;
uniform vec3 diffuseCoeff;

uniform float k;

in vec4 viewPosition;
in vec3 m;

void main()
{
    vec3 s = normalize(view_matrix*vec4(lightPos,1) - viewPosition).xyz;

    //The 3 values below should all be vec3 as they are RGB values
    vec3 ambient = ambientIntensity*ambientCoeff;
    vec3 diffuse = max(lightIntensity*diffuseCoeff*dot(m,s), 0.0);

    float d = length((view_matrix*vec4(lightPos,1) - viewPosition).xyz);


    float attenuation = 1.0 / (1.0 + k *pow(d, 2));

    vec3 intensity = ambient + attenuation*diffuse;
    
    outputColor = vec4(intensity,1)*input_color;
}
