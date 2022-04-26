package com.example.ex_02_opengl_02;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

public class Square {

    // 점. 고정되어있으므로 그대로 써야한다
    // GPU 를 이용하여 고속 계산하여 화면 처리하기 위한 코드
    String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" //(4X4 형태의 상수로 지정)
                    + "attribute vec4 vPosition;" // vec4 -> 3차원 좌표
                    + "void main () {"
                    + "gl_Position = uMVPMatrix * vPosition;"
                    // gl_Position : OpenGL에 있는 변수 이용 > 계산식 uMVPMatrix * vPosition
                    + "}";

    // 화면에 어떻게 그려지는지
    String fragmentShaderCode =
            // 정밀도 중간
            "precision mediump float;"
                    + "uniform vec4 vColor;" // 4 개 (점들) 컬러를 받겠다
                    + "void main() {"
                    + "gl_FragColor = vColor;"
                    +"}";

    // 직사각형 점들의 좌표
//    static float [] squareCoords = {
//            // x , y   , z
//            -0.5f, 0.5f, 0.0f, // 왼쪽 위
//            -0.5f, -0.5f, 0.0f, // 왼쪽 아래
//            0.5f, -0.5f, 0.0f, // 오른쪽 아래
//            0.5f, 0.5f, 0.0f // 오른쪽 위
//
//    };

    static float [] squareCoords;

    public void circle(){

        ArrayList<Float> al = new ArrayList<>();

        al.add(0.0f);
        al.add(0.0f);
        al.add(0.0f);
        al.add(1.0f);
        al.add(0.0f);
        al.add(0.0f);


        for(float i =0; i <= 360; i++){
            al.add((float) (1.0f*Math.cos(Math.toRadians(i))));
            al.add((float) (1.0f*Math.sin(Math.toRadians(i))));
            al.add((float) (0.0f));
        }

        squareCoords = new float[al.size()];
        for(int i=0; i < al.size(); i++){
            squareCoords[i] = al.get(i);
        }


        int a = 0;
        int b = 1;
        int c = 2;

        drawOrder = new short[al.size()];
        for(int i = 0; i < al.size(); i = i + 3){

            drawOrder[i] = (short) a;
            drawOrder[i+1] = (short) b;
            drawOrder[i+2] = (short) c;
            b++;
            c++;
        }
    }




    // 색깔 (빨간색에 가까운 색)
    float [] color = {1.0f, 0.5f, 0.3f, 1.0f };

    // 그리는 순서. 점의 위치를 반시계방향을 맞춰야한다
    // squareCoords 의 위에서부터 0,1,2 순서
//    short [] drawOrder =  {
//            0,1,2,
//            0,2,3
//    };
    short [] drawOrder;


    FloatBuffer vertexBuffer;
    ShortBuffer drawBuffer;
    int mProgram;

    // 버퍼로 만들어서 쪼개 보낸다
    public Square(){

        circle();

        // float 가 4byte 이므로 * 4 를 해준다
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        // 정렬 . c 나 c++은 전송할때 반드시 순서를 정해줘야한다. java는 자동으로 해준다
        bb.order(ByteOrder.nativeOrder());

        // vertexBuffer 에 넣어라
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);

        // 위치를 맨 앞으로
        vertexBuffer.position(0);

        // short 이 2byte 이므로 * 2 를 해준다
        bb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        // 정렬 . c 나 c++은 전송할때 반드시 순서를 정해줘야한다. java는 자동으로 해준다
        bb.order(ByteOrder.nativeOrder());

        // drawBuffer 에 넣어라
        drawBuffer = bb.asShortBuffer();
        drawBuffer.put(drawOrder);

        // 위치를 맨 앞으로
        drawBuffer.position(0);

        // shading 입체감
        // 점위치 계산식
        // vertexShaderCode -> vertexShader
        int vertexShader = com.example.ex_02_opengl_02.MyGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode
        );

        // 점색상 계산식
        // fragmentShaderCode -> fragmentShader
        int fragmentShader = com.example.ex_02_opengl_02.MyGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode
        );


        // mProgram = vertexShader + fragmentShader
        mProgram = GLES20.glCreateProgram();
        // 점위치 계산식 합치기
        GLES20.glAttachShader(mProgram,vertexShader);
        // 색상 계산식 합치기
        GLES20.glAttachShader(mProgram,fragmentShader);

        GLES20.glLinkProgram(mProgram); // 도형 렌더링 계산식 정보를 넣는다.
    }


    int mPositionHandle, mColorHandle, mMVPMatrixHandle;

    // 도형 그리기 --> MyGLRenderer.onDrawFrame() 에서 호출하여 그리기
    void draw(float [] mMVPMatrix){

        //계산된 렌더링 정보 사용한다.
        GLES20.glUseProgram(mProgram);


        // 핸들러

        // vPosition
        // mProgram == > vertexShader
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glVertexAttribPointer(
                mPositionHandle,        // 정점 속성의 인덱스 지정
                3,                // 점속성 - 좌표계
                GLES20.GL_FLOAT,        // 점의 자료형 float
                false,      // 노멀라이즈. 정규화 true, 직접변환 false
                3 * 4,          // 점 속성의 stride(간격)
                vertexBuffer            // 점 정보
        );

        // vColor
        // mProgram == > fragmentShader
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        GLES20.glUniform4fv(mColorHandle,1,color,0);

        // matrix의 현재 값을 받아 온다
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram,"uMVPMatrix");

        // 그려지는 곳에 위치, 보이는 정보를 적용한다.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        //직사각형 그린다
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES,
                drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT,
                drawBuffer
        );

        // 닫는다
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
