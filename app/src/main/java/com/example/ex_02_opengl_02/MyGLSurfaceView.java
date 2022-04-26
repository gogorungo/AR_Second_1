package com.example.ex_02_opengl_02;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class MyGLSurfaceView extends GLSurfaceView {



    public MyGLSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);
        
        setRenderer(new com.example.ex_02_opengl_02.MyGLRenderer());
        
        // 화면이 바뀔때
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }


}
