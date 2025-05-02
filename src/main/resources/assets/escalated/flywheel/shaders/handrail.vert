#define PI 3.1415926538

#use "flywheel:core/quaternion.glsl"
#use "flywheel:core/matutils.glsl"

struct Handrail {
    vec2 light;
    vec4 color;
    vec3 pos;
    vec3 pivot;
    vec4 rotation;
    vec2 sourceTexture;
    vec4 scrollTexture;
    float scroll;
};

void vertex(inout Vertex v, Handrail instance) {
    v.pos = rotateVertexByQuat(v.pos - instance.pivot, instance.rotation) + instance.pivot + instance.pos;
    v.normal = rotateVertexByQuat(v.normal, instance.rotation);
    v.color = instance.color;
    v.light = instance.light;

    v.texCoords = v.texCoords - instance.sourceTexture + instance.scrollTexture.xy + vec2(0, instance.scroll);
}
