precision mediump float;

void main() {
	
	
	gl_FragColor =  new vec4(gl_FragCoord.x/1280.0, gl_FragCoord.y/800.0, 0.0, 1.0);
}