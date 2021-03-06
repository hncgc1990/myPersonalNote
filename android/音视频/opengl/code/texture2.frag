#version 330 core
in vec3 ourColor;
in vec2 TexCoord;

out vec4 color;

uniform sampler2D ourTexture1;//纹理１
uniform sampler2D ourTexture2;//纹理2

void main()
{
   // color = texture(ourTexture2, TexCoord);
    //color = texture(ourTexture1, TexCoord) * vec4(ourColor, 1.0f);
    color = mix(texture(ourTexture1, TexCoord), texture(ourTexture2, TexCoord), 0.5);
}
