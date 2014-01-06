uniform mat4 u_MVPMatrix;

attribute vec4 a_Position;

void main() {
	
	gl_PointSize = 10.0;
	gl_Position = u_MVPMatrix * a_Position;
}
