package com.example.racinglumber;

import android.hardware.Sensor;
import android.os.SystemClock;

public class dataStorage {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private static int dataArrayLen = 500;

    public static float[] xDataArray = new float[dataArrayLen];//todo this is public for now, update with graphActivity
    private static float[] yDataArray;// = new float[dataArrayLen];//todo check that these don't break record
    private static float[] zDataArray;// = new float[dataArrayLen];
    private static int accelIndex;// = 0; //index of x/y/zDataArray
    private static long[] accelEventTime = new long[dataArrayLen];

    private static float[] xRotationArray;// = new float[dataArrayLen];
    private static float[] yRotationArray;// = new float[dataArrayLen];
    private static float[] zRotationArray;// = new float[dataArrayLen];
    private static int rotationIndex = 0; //index of x/y/zRotationArray
    private static long[] rotationEventTime;// = new long[dataArrayLen];

    private static float[] xGravityArray;// = new float[dataArrayLen];
    private static float[] yGravityArray;// = new float[dataArrayLen];
    private static float[] zGravityArray;// = new float[dataArrayLen];
    private static int gravityIndex = 0; //index of x/y/zGravityArray
    private static long[] gravityEventTime;// = new long[dataArrayLen];

    public void clearStorage()
    {
        xDataArray = null;
        xDataArray = new float[dataArrayLen];
        yDataArray = null;
        yDataArray = new float[dataArrayLen];
        zDataArray = null;
        zDataArray = new float[dataArrayLen];
        accelIndex = 0;

        xRotationArray = null;
        xRotationArray = new float[dataArrayLen];
        yRotationArray = null;
        yRotationArray = new float[dataArrayLen];
        zRotationArray = null;
        zRotationArray = new float[dataArrayLen];
        rotationIndex = 0;

        xGravityArray = null;
        xGravityArray = new float[dataArrayLen];
        yGravityArray = null;
        yGravityArray = new float[dataArrayLen];
        zGravityArray = null;
        zGravityArray = new float[dataArrayLen];
        gravityIndex = 0;
    }

    public boolean writeToStorage(float xInput, float yInput, float zInput, int sensorType)
    {
        boolean bufferFull = false;

        if (sensorType == Sensor.TYPE_LINEAR_ACCELERATION)
        {
            if (accelIndex >= dataArrayLen)
            {
                bufferFull = true;
            }
            else
            {
                xDataArray[accelIndex] = xInput;
                yDataArray[accelIndex] = yInput;
                zDataArray[accelIndex] = zInput;
                accelEventTime[accelIndex] = SystemClock.elapsedRealtime();
                accelIndex = accelIndex + 1;
            }
        }
        else if (sensorType == Sensor.TYPE_ROTATION_VECTOR)
        {
            if (rotationIndex >= dataArrayLen)
            {
                bufferFull = true;
            }
            else
            {
                xRotationArray[rotationIndex] = xInput;//x is pitch
                yRotationArray[rotationIndex] = yInput;//y is roll
                zRotationArray[rotationIndex] = zInput;//z is yaw
                rotationEventTime[rotationIndex] = SystemClock.elapsedRealtime();
                rotationIndex = rotationIndex + 1;
            }
        }
        else if (sensorType == Sensor.TYPE_GRAVITY)
        {
            if (gravityIndex >= dataArrayLen)
            {
                bufferFull = true;
            }
            else
            {
                xGravityArray[gravityIndex] = xInput;
                yGravityArray[gravityIndex] = yInput;
                zGravityArray[gravityIndex] = zInput;
                gravityEventTime[rotationIndex] = SystemClock.elapsedRealtime();
                gravityIndex = gravityIndex + 1;
            }
        }
        else
        {
            //do nothing
        }

        return bufferFull;
    }

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

    //todo call this for every value asked for in graph activity.  That way we don't operate on unused datasets
    //todo needs bounds checking
    public void correctDataOrientation () {
        int index = 0; //todo debug var, will be inputted parameter
        double absValGPRIMEcrossG;
        double absValG;
        double angleW;

        /*Calculate abs|G'xG| and abs|G|*/
        absValGPRIMEcrossG = Math.pow(xGravityArray[index], 2); //abs|G| and abs|G'xG| have equivalent x component
        absValGPRIMEcrossG += Math.pow(yGravityArray[index], 2); //abs|G| and abs|G'xG| have equivalent y component
        absValG = absValGPRIMEcrossG + Math.pow(xGravityArray[index], 2); //abs|G| has z component while abs|G'xG| doesn't

        absValG = Math.sqrt(absValG);
        absValGPRIMEcrossG = Math.sqrt(absValGPRIMEcrossG);

        /*Calculate angle W*/
        angleW = Math.asin((absValGPRIMEcrossG/absValG));


    }

        //stringFromJNI();
        //long testVar = sumIntegers(1,2);
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native void stringFromJNI();
    private native long sumIntegers(int first, int second);
}
