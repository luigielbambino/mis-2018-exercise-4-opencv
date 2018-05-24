package com.example.mis.opencv;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.opencv.objdetect.Objdetect.CASCADE_SCALE_IMAGE;

public class MainActivity extends Activity implements CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";

    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;
    private CascadeClassifier faceDetector;
    private CascadeClassifier eyesDetector;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
            faceDetector = new CascadeClassifier(initAssetFile("haarcascade_frontalface_default.xml"));
            eyesDetector = new CascadeClassifier(initAssetFile("haarcascade_eye.xml"));
        }
    };

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.tutorial1_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);


    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        Mat col = inputFrame.rgba();
        Mat gray = inputFrame.gray();

        int frameWidth = col.width();
        int frameHeight = col.height();

        //Reference: https://docs.opencv.org/java/2.4.9/org/opencv/objdetect/CascadeClassifier.html
        MatOfRect faces = new MatOfRect();
        MatOfRect eyes = new MatOfRect();
        int eyesSize = 0;

        faceDetector.detectMultiScale(gray, faces, 1.3, 5, 2, new org.opencv.core.Size(40, 40), new org.opencv.core.Size(frameWidth, frameHeight));

        for(Rect rectf : faces.toArray()){
            /* Draw rectangles around the faces
            Imgproc.rectangle(col, new Point(rectf.x, rectf.y),
                    new Point(rectf.x + rectf.width, rectf.y + rectf.height),
                    new Scalar(0, 255, 0));*/
            eyesDetector.detectMultiScale(gray, eyes, 1.2, 5, 2, new org.opencv.core.Size(20, 20), new org.opencv.core.Size(frameWidth, frameHeight));
            for(Rect recte : eyes.toArray()){

                /* Draw rectangles around the eyes
                 *Imgproc.rectangle(col, new Point(recte.x, recte.y),
                        new Point(recte.x + recte.width, recte.y + recte.height),
                        new Scalar(0, 0, 255));*/
                eyesSize = recte.height;
            }

            if(eyes.toArray().length >= 1){
                int noseSize = rectf.width/7;
                int nosex = rectf.x + (rectf.width / 2);
                int nosey = rectf.y + (rectf.height / 2) + (eyesSize/2);
                Imgproc.circle(col, new Point(nosex, nosey),
                        noseSize,
                        new Scalar(255, 0, 0),
                        -noseSize);
            }

        }
        return col;
    }


    public String initAssetFile(String filename)  {
        File file = new File(getFilesDir(), filename);
        if (!file.exists()) try {
            InputStream is = getAssets().open(filename);
            OutputStream os = new FileOutputStream(file);
            byte[] data = new byte[is.available()];
            is.read(data); os.write(data); is.close(); os.close();
        } catch (IOException e) { e.printStackTrace(); }
        Log.d(TAG,"prepared local file: "+filename);
        return file.getAbsolutePath();
    }
}