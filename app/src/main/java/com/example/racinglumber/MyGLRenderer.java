//package com.example.racinglumber;
//
//import android.opengl.EGLConfig;
//import android.opengl.GLES20;
//import android.opengl.GLSurfaceView;
//
//import javax.microedition.khronos.opengles.GL10;
//
//public class MyGLRenderer implements GLSurfaceView.Renderer {
//    //@Override
//    public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig config)
//    {
//        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//    }
//
//    public void onDrawFrame(GL10 unused) {
//        // Redraw background color
//        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
//    }
//
//    public void onSurfaceChanged(GL10 unused, int width, int height) {
//        GLES20.glViewport(0, 0, width, height);
//    }
//}
