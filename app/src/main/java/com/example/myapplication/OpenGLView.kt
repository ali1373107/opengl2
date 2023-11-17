package com.example.myapplication

import android.opengl.GLSurfaceView
import android.content.Context
import android.opengl.GLES20
import android.util.Log
import freemap.openglwrapper.GPUInterface
import freemap.openglwrapper.OpenGLUtils
import java.io.IOException
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class OpenGLView(ctx:Context)   :GLSurfaceView(ctx),GLSurfaceView.Renderer{
    init {
        setEGLContextClientVersion(2)
        setRenderer(this)
         }

    val gpu = GPUInterface("default shader")
    var fbuf: FloatBuffer?=null
    val blue = floatArrayOf(0f,0f,1f,1f)
    val yellow = floatArrayOf(1f,1f,0f,1f)
// setup code to run when the OpenGL view is first created.
    override fun onSurfaceCreated(unused: GL10, config: EGLConfig){


        try {
            GLES20.glClearColor(0.0f,0.0f,0.0f,1.0f)
            GLES20.glClearDepthf(1.0f)
            GLES20.glEnable(GLES20.GL_DEPTH_TEST)

             gpu.loadShaders(context.assets, "vertex.glsl", "fragment.glsl")
            val vertices = floatArrayOf(
                0f, 0f, 0f,
                1f, 0f, 0f,
                0f, 1f, 0f,
                0f,0f,0f,
                -1f,0f,0f,
                0f,-1f,0f
            )
            fbuf = OpenGLUtils.makeFloatBuffer(vertices)
            gpu.select()
        } catch (e: IOException) {
            Log.e("OpenGLBasic", e.stackTraceToString())
        }

    }

    //draws the first frame
    // its called multyple time per second
//will be run contentiously
    //actual scene drawing shoild go here
    override fun onDrawFrame(unused: GL10){
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        //only run code below if buffer is not null
        fbuf?.apply {


            val refAttrib = gpu.getAttribLocation("aVertex")
            val refUColour = gpu.getUniformLocation("uColour")


            gpu.setUniform4FloatArray(refUColour,blue)
            gpu.specifyBufferedDataFormat(refAttrib,this,0)
            gpu.drawBufferedTriangles(0,3)

            gpu.setUniform4FloatArray(refUColour,yellow)
            gpu.drawBufferedTriangles(3,3)
        }
    }
    //its called whenever the resolution changes (on a mobile device this ill occur when the device i rotated
    override fun onSurfaceChanged(unused: GL10, w: Int, h:Int){
        GLES20.glViewport(0,0,w,h)

    }
}