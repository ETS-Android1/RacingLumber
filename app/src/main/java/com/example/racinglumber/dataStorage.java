package com.example.racinglumber;

public class dataStorage {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    public static int dataArrayLen = 500;

    public static float[] xDataArray = new float[dataArrayLen];
    public static float[] yDataArray = new float[dataArrayLen];
    public static float[] zDataArray = new float[dataArrayLen];
    public static long[] accelEventTime = new long[dataArrayLen];

    public static float[] xRotationArray = new float[dataArrayLen];
    public static float[] yRotationArray = new float[dataArrayLen];
    public static float[] zRotationArray = new float[dataArrayLen];
    public static long[] rotationEventTime = new long[dataArrayLen];

    public static float[] xGravityArray = new float[dataStorage.dataArrayLen];
    public static float[] yGravityArray = new float[dataStorage.dataArrayLen];
    public static float[] zGravityArray = new float[dataStorage.dataArrayLen];

    /* Below is a prototype of data correction using gravity data in Java.  This will
     * hopefully be handled in C++/JNI after prototyping
     * */

    /* Math for correcting the orientation of collected data.
    *
    * Data collected by the phone will be affected by the orientation of the phone. We cannot guarantee
    * that the phone will be still during the entire recording session.  So, we must adjust our data by
    * the recorded gravity vector.  Data will be rotated such that the gravity vector is a positive Z-axis
    * value with no X or Y component.  The math for this follows:
    * : Recorded gravity vector is [Gx,Gy,Gz], assuming a positive Gz. The goal is to achieve [0,0,Gz']
    * (gravity prime) by rotating the recorded gravity vector to the orientation of gravity prime.
    * Set |G'| to 1. (we only care about the orientation of the vector)
    * : |G'xG| = |G'||G|sin(W), where W is the angle between the two vectors
    * : |G'xG| = [-Gy,Gx,0], which is a vector on the XY plane to rotate the G to G'
    * Now we calculate W
    * : sin(W) = abs(|G'xG|)/|G'||G|
    * : sin(W) = abs([-Gy,Gx,0])/abs([Gx,Gy,Gz])
    *
    * We now define the accelerometer data recorded when G was recorded as [Dx,Dy,Dz].  This data's
    * orientation is corrected such that the adjusted data D' would be the equivalent of recording when
    * the phone was flat on the XY axis with its face up (positive Z axis).
    * To do this we rotate D by angle W about vector |G'xG| (define as R from now on to ease notation)
    * using quaternions.  This rotation would also make G and G' parallel, but we only need the output D'.
    * : Conjugate D by R: D' = RDR'
    * */

    public void correctDataOrientation ()
    {
        String testVar = stringFromJNI();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
