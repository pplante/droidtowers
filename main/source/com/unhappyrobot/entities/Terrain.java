package com.unhappyrobot.entities;

import com.badlogic.gdx.graphics.*;

import java.util.Random;

public class Terrain {
  Mesh mesh;
  private float[][] heightData;
  private float[] verticies;
  private TerrainChunk chunk;

  public Terrain() {
  }

  public void loadImage(String filename) {
    TextureRef heightMapTextureRef = TextureDict.loadTexture(filename);
    Texture heightMapTexture = heightMapTextureRef.get();
    heightMapTexture.getTextureData().prepare();
    Pixmap pixmap = heightMapTexture.getTextureData().consumePixmap();

    int vertIndex = 3;
    int mapWidth = pixmap.getWidth();
    int mapHeight = pixmap.getHeight();
    chunk = new TerrainChunk(mapWidth, mapHeight, 4);
    int len = chunk.vertices.length;

    int bi = 0;
    for(int x = 0; x < mapWidth; x++) {
      for(int y = 0; y < mapHeight; y++) {
        chunk.heightMap[bi++] = (byte) (pixmap.getPixel(x, y) & 0xFF00FFFF);
      }
    }

    Random rand = new Random();
    len = chunk.vertices.length;
    for (int i = 3; i < len; i += 4)
      chunk.vertices[i] = Color.toFloatBits(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255), 255);

    chunk.buildVertices();


    mesh = new Mesh(true, chunk.vertices.length / 3, chunk.indices.length, new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"), new VertexAttribute(VertexAttributes.Usage.ColorPacked, 4, "a_color"));
    mesh.setVertices(chunk.vertices);
    mesh.setIndices(chunk.indices);
  }

  public void render(GL10 gl) {
    gl.glColor4f(1, 1, 1, 1);
    mesh.render(GL10.GL_TRIANGLES);
  }

  final static class TerrainChunk {
    public byte[] heightMap;
    public final short width;
    public final short height;
    public final float[] vertices;
    public final short[] indices;
    public final int vertexSize;

    public TerrainChunk(int width, int height, int vertexSize) {
      if ((width + 1) * (height + 1) > Short.MAX_VALUE)
        throw new IllegalArgumentException("Chunk size too big, (width + 1)*(height+1) must be <= 32767");

      this.heightMap = new byte[(width + 1) * (height + 1)];
      this.width = (short) width;
      this.height = (short) height;
      this.vertices = new float[heightMap.length * vertexSize];
      this.indices = new short[width * height * 6];
      this.vertexSize = vertexSize;

      buildIndices();
      buildVertices();
    }

    public void buildVertices() {
      int heightPitch = height + 1;
      int widthPitch = width + 1;

      int idx = 0;
      int hIdx = 0;
      int inc = vertexSize - 3;

      for (int z = 0; z < heightPitch; z++) {
        for (int x = 0; x < widthPitch; x++) {
          vertices[idx++] = x;
          vertices[idx++] = heightMap[hIdx++];
          vertices[idx++] = z;
          idx += inc;
        }
      }
    }

    private void buildIndices() {
      int idx = 0;
      short pitch = (short) (width + 1);
      short i1 = 0;
      short i2 = 1;
      short i3 = (short) (1 + pitch);
      short i4 = pitch;

      short row = 0;

      for (int z = 0; z < height; z++) {
        for (int x = 0; x < width; x++) {
          indices[idx++] = i1;
          indices[idx++] = i2;
          indices[idx++] = i3;

          indices[idx++] = i3;
          indices[idx++] = i4;
          indices[idx++] = i1;

          i1++;
          i2++;
          i3++;
          i4++;
        }

        row += pitch;
        i1 = row;
        i2 = (short) (row + 1);
        i3 = (short) (i2 + pitch);
        i4 = (short) (row + pitch);
      }
    }
  }
}
