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
}

void main() {
	
	vec3 linearColor = vec3(0);
	for(int i = 0; i < numLights; ++i){
	    linearColor += buildLights(allLights[i], surfaceColor.rgb, normal, surfacePos, surfaceToCamera);
	}

}

vec3 buildLights(Light light, vec3 surfaceColor, vec3 normal, vec3 surfacePos, vec3 surfaceToCamera) {
	vec3 s;
	float attenuation = 1.0;
	if(light.lightPos.w)

}