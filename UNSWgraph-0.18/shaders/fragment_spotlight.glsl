#define MAX_LIGHTS 2
uniform int numLights

uniform struct Light {
	vec4 lightPos;
	vec3 lightIntensity;
	float attenuation;
	float ambientCoeff;
	float diffuseCoeff;
	float specularCoeff;
	float coneAngle;
	vec3 coneDirection;
} allLights[MAX_LIGHTS];


vec3 linearColor = vec3(0);
for(int i = 0; i < numLights; ++i){
    linearColor += ApplyLight(allLights[i], surfaceColor.rgb, normal, surfacePos, surfaceToCamera);
}

vec3 ApplyLight(Light light, vec3 surfaceColor, vec3 normal, vec3 surfacePos, vec3 surfaceToCamera) {
    vec3 surfaceToLight;
    float attenuation = 1.0;
    if(light.position.w == 0.0) {
        //directional light
        vec3 s = normalize(view_matrix*vec4(lightPos,0)).xyz;
        //surfaceToLight = normalize(light.position.xyz);
        attenuation = 1.0; //no attenuation for directional lights
    } else {
        //point light
        surfaceToLight = normalize(light.position.xyz - surfacePos);
        float distanceToLight = length(light.position.xyz - surfacePos);
        attenuation = 1.0 / (1.0 + light.attenuation * pow(distanceToLight, 2));

        //cone restrictions (affects attenuation)
        float lightToSurfaceAngle = degrees(acos(dot(-surfaceToLight, normalize(light.coneDirection))));
        if(lightToSurfaceAngle > light.coneAngle){
            attenuation = 0.0;
        }
    }

    //ambient
    vec3 ambient = light.ambientCoeff * surfaceColor.rgb * lightIntensity;

    //diffuse
    vec3 diffuse = diffuseCoeff * surfaceColor.rgb * lightIntensity;
    
    //specular
    vec3 specular = specularCoefficient * materialSpecularColor * lightIntensity;

    //linear color (color before gamma correction)
    return ambient + attenuation*(diffuse + specular);
}