package com.example.ex_02_opengl_02;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyGLRenderer implements GLSurfaceView.Renderer {
    
    Square myBox;
    
    float [] mMVPMatrix = new float[16]; // 통합, 3D 사각형이라 16개 지정
    float [] mProjectionMatrix = new float[16]; // 2D 뷰 포트. 사물 속성
    float [] mViewMatrix = new float[16]; // 카메라 시점
    
    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.0f,1.0f,1.0f,1.0f);
        
        myBox = new Square();
    }

    // 화면 갱신되면서 화면에서 배치
    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {

        GLES20.glViewport(0,0,width,height);

        // 해상도 변화, 가로세로 화면 바뀔 수 있다. (포트라이트, 랜드스케이프)
        float ratio = (float) width / height;

        // offset - 배열의 0번부터 시작
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1,1,3,7);

    }

    @Override
    public void onDrawFrame(GL10 gl10) {

        // 색상 버퍼 삭제 | 깊이 버퍼 삭제
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // offset - 배열의 0번부터 시작, 카메라 위치, 카메라 시선, 카메라 윗방향
        Matrix.setLookAtM(mViewMatrix,0,
                0,0,6, // 카메라 위치
                0,0,0, // 카메라 시선
                0,1,0); // 카메라 윗방향

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0,mViewMatrix,0);
        
        //정사각형 그리기
        myBox.draw(mMVPMatrix);
    }


    // GPU를 이용하여 그리기를 연산한다.
    static int loadShader(int type, String shaderCode){

        int res = GLES20.glCreateShader(type);

        GLES20.glShaderSource(res, shaderCode);
        GLES20.glCompileShader(res);

        return res;
    }

}
