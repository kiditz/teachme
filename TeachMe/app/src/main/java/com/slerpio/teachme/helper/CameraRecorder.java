package com.slerpio.teachme.helper;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;
import android.view.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CameraRecorder implements SurfaceHolder.Callback {
    private static final String TAG = CameraRecorder.class.getName();
    private static final int DEGREES_0 = 0;
    private static final int DEGREES_90 = 90;
    private static final int DEGREES_180 = 180;
    private static final int DEGREES_270 = 270;
    private static final int DEGREES_360 = 360;
    private final SurfaceView view;
    private final Activity activity;
    private MediaRecorder recorder;
    private boolean isRecording;
    private int cameraId = 0;
    private Camera camera;
    private File outputFile;
    private boolean inPreview;
    private CamcorderProfile profile;
    private SurfaceHolder holder;

    public CameraRecorder(final Activity activity, SurfaceView view) {
        this.activity = activity;
        this.view = view;
        this.holder = this.view.getHolder();
        this.holder.addCallback(this);
        this.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void startRecording() throws IOException {
        if (!isRecording) {
            stopPreview();
            releaseCamera();
            releaseMediaRecorder();
            //new MediaPrepareTask().execute(null, null, null);
            if (prepareCamera() && prepareRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                recorder.start();
                isRecording = true;
            } else {
                releaseCamera();
                releaseMediaRecorder();
            }
            inPreview = false;
        }
    }

    public void stopRecording() {
        if (isRecording) {
            try {
                recorder.stop();
            } catch (Exception e) {
                outputFile.delete();
            }
            releaseMediaRecorder();
            isRecording = false;
            releaseCamera();
        }
    }

    public void releaseMediaRecorder() {
        if (recorder != null) {
            // clear recorder configuration
            recorder.reset();
            // release the recorder object
            recorder.release();
            recorder = null;
            // Lock camera for later use i.e taking it back from MediaRecorder.
            // MediaRecorder doesn't need it anymore and we will release it if the activity pauses.
            camera.lock();
        }
    }

    public void releaseCamera() {
        if (camera != null) {
            // release the camera for other applications
            camera.release();
            camera = null;
        }
    }

    public void changeCamera(int cameraId) {
        this.cameraId = cameraId;
        if (camera != null) {
            stopPreview();
            releaseCamera();
            startPreview();
        }
    }

    private boolean prepareCamera() {

        // BEGIN_INCLUDE (configure_preview)
        if (camera == null)
            camera = CameraHelper.getDefaultCameraInstance(this.cameraId);

        try {
            // Requires API level 11+, For backward compatibility use {@link setPreviewDisplay}
            // with {@link SurfaceView}
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            Log.e(TAG, "Surface texture is unavailable or unsuitable" + e.getMessage());
            return false;
        }

        Camera.Parameters parameters = camera.getParameters();
        parameters.setExposureCompensation(parameters.getMaxExposureCompensation());
        parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_TWILIGHT);
        List<Camera.Size> mSupportedPreviewSizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> mSupportedVideoSizes = parameters.getSupportedVideoSizes();
        Camera.Size optimalSize = CameraHelper.getOptimalVideoSize(mSupportedVideoSizes,
                mSupportedPreviewSizes, view.getWidth(), view.getHeight());

        // Use the same size for recording profile.
        profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);

        profile.videoFrameWidth = optimalSize.width;
        profile.videoFrameHeight = optimalSize.height;

        // likewise for the camera object itself.
        parameters.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
        //parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
        camera.setParameters(parameters);
        setOrientation(false);
        return true;
    }

    private boolean prepareRecorder() {

        // BEGIN_INCLUDE (configure_media_recorder)
        recorder = new MediaRecorder();
        setOrientation(true);
        // Step 1: Unlock and set camera to MediaRecorder
        camera.unlock();
        recorder.setCamera(camera);

        // Step 2: Set sources
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //
        recorder.setVideoFrameRate(15);
        recorder.setPreviewDisplay(holder.getSurface());
        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        //recorder.setProfile(profile);

        // Step 4: Set output outputFile
        outputFile = CameraHelper.getOutputMediaFile(activity, CameraHelper.MEDIA_TYPE_VIDEO);
        if (outputFile == null) {
            return false;
        }
        recorder.setOutputFile(outputFile.getPath());
        // END_INCLUDE (configure_media_recorder)

        // Step 5: Prepare configured MediaRecorder
        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    private void startPreview() {
        if (camera == null) {
            prepareCamera();
        }
        camera.startPreview();

        inPreview = true;
    }

    private void stopPreview() {
        if (camera != null) {
            camera.stopPreview();
            inPreview = false;
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            startPreview();
        } catch (Exception e) {

        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (inPreview) {
            stopPreview();
            releaseCamera();
            releaseMediaRecorder();
        }

    }

    class MediaPrepareTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            // initialize video camera
            if (prepareCamera() && prepareRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                recorder.start();

                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                activity.finish();
            }
            // inform the user that recording has started

        }
    }

    private void setOrientation(boolean isRecorder) {
        Display display = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);

        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        int recOrientation;
        boolean isFrontCamera = info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT;
        if (isFrontCamera) {
            result = (info.orientation + degrees) % 360;
            recOrientation = result;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
            recOrientation = result;
        }
        Log.d(TAG, "setOrientation: " + result);
        Log.d(TAG, "recsetOrientation: " + result);
        if(isRecorder)
            recorder.setOrientationHint(recOrientation);
        else
            camera.setDisplayOrientation(result);

    }

    public static int mirror(int orientation) {
        switch (orientation) {
            case DEGREES_0:
            case DEGREES_360:
                return DEGREES_180;
            case DEGREES_90:
                return DEGREES_270;
            case DEGREES_180:
                return DEGREES_0;
            case DEGREES_270:
                return DEGREES_90;
        }
        return DEGREES_0;
    }
}
