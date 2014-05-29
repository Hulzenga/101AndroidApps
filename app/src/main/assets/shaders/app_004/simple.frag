precision mediump float;

uniform vec3 u_LightPosition;

varying vec3 v_Position;
varying vec4 v_Color;
varying vec3 v_Normal;

void main() {

	vec3 lightVector = normalize(u_LightPosition-v_Position);
	float diffuse = max(sqrt(dot(v_Normal, lightVector)), 0.4);	
	
	gl_FragColor = v_Color*diffuse;

	//gl_FragColor = vec4(v_Normal, 1.0);
	//gl_FragColor = vec4(lightVector, 1.0);
}
