out vec4 outputColor;

uniform vec4 input_color;

uniform mat4 view_matrix;

uniform mat4 model_matrix;
// Light properties
uniform vec3 envLightPos;
uniform vec3 envLightIntensity;
uniform vec3 envAmbientIntensity;

// Material properties
uniform vec3 envAmbientCoeff;
uniform vec3 envDiffuseCoeff;
uniform vec3 envSpecularCoeff;
uniform float envPhongExp;

// Torch properties
uniform vec3 torchLightPos;
uniform vec3 torchLightIntensity;
uniform vec3 torchAmbientIntensity;

uniform vec3 torchAmbientCoeff;
uniform vec3 torchDiffuseCoeff;

uniform float k;


uniform sampler2D tex;

in vec4 viewPosition;
in vec3 m;

in vec2 texCoordFrag;

vec4 envLight( vec4 input_color, mat4 view_matrix, vec4 viewPosition, vec3 m, sampler2D tex, vec2 texCoordFrag,
	vec3 lightPos, vec3 lightIntensity, vec3 ambientIntensity, vec3 ambientCoeff, vec3 diffuseCoeff,
	vec3 specularCoeff, float phongExp );

vec4 torch( vec4 input_color, mat4 view_matrix, vec4 viewPosition, float k, 
	vec3 lightPos, vec3 lightIntensity, vec3 ambientIntensity, vec3 ambientCoeff, vec3 diffuseCoeff);

void main() {

	//outputColor = envLight( input_color, view_matrix, viewPosition, m, tex, texCoordFrag,
	//	envLightPos, envLightIntensity, envAmbientIntensity, envAmbientCoeff, envDiffuseCoeff,
//		envSpecularCoeff, envPhongExp );

	outputColor += torch( input_color, view_matrix, viewPosition, k, torchLightPos, torchLightIntensity,
		torchAmbientIntensity, torchAmbientCoeff, torchDiffuseCoeff);

}

vec4 envLight( vec4 input_color, mat4 view_matrix, vec4 viewPosition, vec3 m, sampler2D tex, vec2 texCoordFrag,
	vec3 lightPos, vec3 lightIntensity, vec3 ambientIntensity, vec3 ambientCoeff, vec3 diffuseCoeff,
	vec3 specularCoeff, float phongExp ) {

    // Compute the s, v and r vectors
    vec3 s = normalize(view_matrix*vec4(lightPos,0)).xyz;
    vec3 v = normalize(-viewPosition.xyz);
    vec3 r = normalize(reflect(-s,m));

    vec3 ambient = ambientIntensity*ambientCoeff;
    vec3 diffuse = max(lightIntensity*diffuseCoeff*dot(m,s), 0.0);
    vec3 specular;

    // Only show specular reflections for the front face
    if (dot(m,s) > 0)
        specular = max(lightIntensity*specularCoeff*pow(dot(r,v),phongExp), 0.0);
    else
        specular = vec3(0);

    vec4 ambientAndDiffuse = vec4(ambient + diffuse, 1);

    return ambientAndDiffuse*input_color*texture(tex, texCoordFrag) + vec4(specular, 1);
    //return input_color*texture(tex, texCoordFrag);
}

vec4 torch( vec4 input_color, mat4 view_matrix, vec4 viewPosition, float k, 
	vec3 lightPos, vec3 lightIntensity, vec3 ambientIntensity, vec3 ambientCoeff, vec3 diffuseCoeff) {

    //vec3 s = normalize(vec4(lightPos,1) - view).xyz;
    vec3 s = normalize(view_matrix*vec4(lightPos, 1) - viewPosition).xyz;
    //The 3 values below should all be vec3 as they are RGB values
    vec3 ambient = ambientIntensity*ambientCoeff;
    vec3 diffuse = max(lightIntensity*diffuseCoeff*dot(m,s), 0.0);

    //float d = length((view_matrix*vec4(lightPos,1) - viewPosition).xyz);
    float d = length(100000000);
    //float d = 5;

    
    float spotlightConeAngle = 0.2f;
    vec3 conedirection = normalize(-viewPosition.xyz);
    vec3 raydirection = -s;
    
    float lightToSurfaceAngle = acos(dot(raydirection, vec3(0, 0, -1)));
    
    float attenuation = 0.0;
    
    if (lightToSurfaceAngle > spotlightConeAngle) {
        attenuation = 1.0 / (1.0 + 1 * pow(100, 2));
        ambient *= attenuation;
        diffuse *= attenuation;

        // attenuation = 1.0 / (1.0 + k * pow(d, 2));
    }

    vec4 ambientAndDiffuse = vec4(ambient + diffuse, 1);
    vec3 intensity = attenuation*diffuse;
    
    return ambientAndDiffuse*input_color*texture(tex, texCoordFrag);

}
