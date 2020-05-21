package com.example.racinglumber;

import android.hardware.Sensor;
import android.os.SystemClock;

public class dataStorage {
    // Used to load the 'native-lib' library on application startup.
//    static {
//        System.loadLibrary("native-lib");
//    }

    enum Axis
    {
        X, Y, Z;
    }

    enum RecordType
    {
        acceleration,
        rotation,
        gravity
    }

    public static int dataArrayLen = 500;

    private static float[] xDataArray;//todo this is public for now, update with graphActivity
    private static float[] yDataArray;//todo check that these don't break record
    private static float[] zDataArray;
    private static int accelIndex; //index of x/y/zDataArray
    private static long[] accelEventTime;

    private static float[] xRotationArray;
    private static float[] yRotationArray;
    private static float[] zRotationArray;
    private static int rotationIndex = 0; //index of x/y/zRotationArray
    private static long[] rotationEventTime;

    private static float[] xGravityArray;
    private static float[] yGravityArray;
    private static float[] zGravityArray;
    private static int gravityIndex = 0; //index of x/y/zGravityArray
    private static long[] gravityEventTime;

    public void clearStorage()
    {
        xDataArray = null;
        xDataArray = new float[dataArrayLen];
        yDataArray = null;
        yDataArray = new float[dataArrayLen];
        zDataArray = null;
        zDataArray = new float[dataArrayLen];
        accelEventTime = null;
        accelEventTime = new long[dataArrayLen];
        accelIndex = 0;

        xRotationArray = null;
        xRotationArray = new float[dataArrayLen];
        yRotationArray = null;
        yRotationArray = new float[dataArrayLen];
        zRotationArray = null;
        zRotationArray = new float[dataArrayLen];
        rotationEventTime = null;
        rotationEventTime = new long[dataArrayLen];
        rotationIndex = 0;

        xGravityArray = null;
        xGravityArray = new float[dataArrayLen];
        yGravityArray = null;
        yGravityArray = new float[dataArrayLen];
        zGravityArray = null;
        zGravityArray = new float[dataArrayLen];
        gravityEventTime = null;
        gravityEventTime = new long[dataArrayLen];
        gravityIndex = 0;
    }

    public void setDataArrayLen(int inputDataLen)
    {
        dataArrayLen = inputDataLen;
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
                gravityEventTime[gravityIndex] = SystemClock.elapsedRealtime();
                gravityIndex = gravityIndex + 1;
            }
        }
        else
        {
            //do nothing
        }

        return bufferFull;
    }

    public void correctedDataPoints()
    {
        correctDataOrientation (xDataArray, yDataArray, zDataArray);
        correctDataOrientation (xRotationArray, yRotationArray, zRotationArray);
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

    public void correctDataOrientation (float [] xInputArray, float [] yInputArray, float [] zInputArray) {
        double absValGPRIMEcrossG;
        double absValG;
        double angleWinRadians;
        double inputX;
        double inputY;
        double inputZ;
        double outputX; double outputY; double outputZ;
        double q0; double q1; double q2;
        double dPrimeXcompX; double dPrimeXcompY; double dPrimeXcompZ;
        double dPrimeYcompX; double dPrimeYcompY; double dPrimeYcompZ;
        double dPrimeZcompX; double dPrimeZcompY; double dPrimeZcompZ;
        double DEBUGVAR;//todo remove
        double DEBUGVARTWO;//todo remove
        double DEBUGVARTHREE;//todo remove

        for (int index = 0; index < xInputArray.length; index++)
        {
            inputX = xInputArray[index];
            inputY = yInputArray[index];
            inputZ = zInputArray[index];

            /*Calculate abs|G'xG| and abs|G|*/
            if (xGravityArray.length < (index - 1))
            {
                absValGPRIMEcrossG = Math.pow(xGravityArray[xGravityArray.length - 1], 2); //abs|G| and abs|G'xG| have equivalent x component
                absValGPRIMEcrossG += Math.pow(yGravityArray[xGravityArray.length - 1], 2); //abs|G| and abs|G'xG| have equivalent y component
                absValG = absValGPRIMEcrossG + Math.pow(xGravityArray[xGravityArray.length - 1], 2); //abs|G| has z component while abs|G'xG| doesn't
            }
            else
            {
                absValGPRIMEcrossG = Math.pow(xGravityArray[index], 2); //abs|G| and abs|G'xG| have equivalent x component
                absValGPRIMEcrossG += Math.pow(yGravityArray[index], 2); //abs|G| and abs|G'xG| have equivalent y component
                absValG = absValGPRIMEcrossG + Math.pow(zGravityArray[index], 2); //abs|G| has z component while abs|G'xG| doesn't
            }

            absValG = Math.sqrt(absValG);
            absValGPRIMEcrossG = Math.sqrt(absValGPRIMEcrossG);

            /*Calculate angle W*/
            angleWinRadians = Math.asin(absValGPRIMEcrossG/absValG);
            DEBUGVARTHREE = Math.toDegrees(angleWinRadians);
//////////////////////////TODO DEBUG MESSAGE////////REMOVE ABOVE DEBUG VAR, AND ANGLE IS NOW CORRECT
            //Pout = q * Pin * conj(q)
            // Conjugate D by R: D' = RDR'
            //R = |G'xG|
            //D is data input
            //q = cos(W/2) + i ( Rx * sin(W/2)) + j (Ry * sin(W/2)) + k ( Rz * sin(W/2))
            //    W=angle of rotation.
            //    x,y,z = vector representing axis of rotation.

            //so for me, q is the rotation quaternion and needs absolute value to be 1.  Need to normalize |G'xG| = [-Gy,Gx,0]
            double normRx = (-1)*(yGravityArray[index] / absValGPRIMEcrossG);//normalized Rx
            double normRy = (xGravityArray[index] / absValGPRIMEcrossG);//normalized Ry
            //Normalized Rz = 0

            q0 = Math.cos(angleWinRadians/2); //cos(W/2)
            q1 = normRx * Math.sin(angleWinRadians/2); //( Rx * sin(W/2))i
            q2 = normRy * Math.sin(angleWinRadians/2); //j (Ry * sin(W/2))
            //q3 = 0, k ( Rz * sin(W/2)

            //x component of x output value of rotation
            dPrimeXcompX = Math.pow(q0,2) + Math.pow(q1, 2) - Math.pow(q2, 2);//https://www.weizmann.ac.il/sci-tea/benari/sites/sci-tea.benari/files/uploads/softwareAndLearningMaterials/quaternion-tutorial-2-0-1.pdf
            dPrimeXcompX *= inputX;
            //y component of x output value of rotation
            dPrimeXcompY = (q1*q2);
            dPrimeXcompY *= (2*inputY);
            //z component of x output value of rotation
            dPrimeXcompZ = 2*inputZ*q0*q2;
            outputX = dPrimeXcompX + dPrimeXcompY + dPrimeXcompZ;

            dPrimeYcompX = 2*inputX*q1*q2;
            dPrimeYcompY = inputY*(Math.pow(q0,2)+Math.pow(q2,2)-Math.pow(q1,2));
            dPrimeYcompZ = (-1)*2*inputZ*q0*q1;
            outputY = dPrimeYcompX + dPrimeYcompY + dPrimeYcompZ;

            dPrimeZcompX = (-2)*inputX*q0*q2;
            dPrimeZcompY = 2*inputY*q0*q1;
            dPrimeZcompZ = inputY*(Math.pow(q0,2)-(Math.pow(q1,2)+Math.pow(q2,2)));
            outputZ = dPrimeZcompX + dPrimeZcompY + dPrimeZcompZ;

            xInputArray[index] = (float)outputX;
            yInputArray[index] = (float)outputY;
            zInputArray[index] = (float)outputZ;
            ////////////////////////////////////////////////TODO VERIFY THAT ROTATION WORKS BY ROTATING GRAVITY VECTOR BY ABOVE ALGORITHM AND CHECK THAT RESULT IS [0,0,9.8]
            ///////////////////////////////////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..
            ///////////////////////////////////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..//todo it's wrong still?
            // TODO Confirmed that magnitude of output is approx 9.8, so rotation is not causing a change in magnitude of the output vector.  But I don't know if output vector is correct
            inputX = xGravityArray[index];
            inputY = yGravityArray[index];
            inputZ = zGravityArray[index];
            //x component of x output value of rotation
            dPrimeXcompX = Math.pow(q0,2) + Math.pow(q1, 2) - Math.pow(q2, 2);//https://www.weizmann.ac.il/sci-tea/benari/sites/sci-tea.benari/files/uploads/softwareAndLearningMaterials/quaternion-tutorial-2-0-1.pdf
            dPrimeXcompX *= inputX;
            //y component of x output value of rotation
            dPrimeXcompY = (q1*q2);
            dPrimeXcompY *= (2*inputY);
            //z component of x output value of rotation
            dPrimeXcompZ = 2*inputZ*q0*q2;
            outputX = dPrimeXcompX + dPrimeXcompY + dPrimeXcompZ;

            dPrimeYcompX = 2*inputX*q1*q2;
            dPrimeYcompY = inputY*(Math.pow(q0,2)+Math.pow(q2,2)-Math.pow(q1,2));
            dPrimeYcompZ = (-1)*2*inputZ*q0*q1;
            outputY = dPrimeYcompX + dPrimeYcompY + dPrimeYcompZ;

            dPrimeZcompX = (-2)*inputX*q0*q2;
            dPrimeZcompY = 2*inputY*q0*q1;
            dPrimeZcompZ = inputY*(Math.pow(q0,2)-(Math.pow(q1,2)+Math.pow(q2,2)));
            outputZ = dPrimeZcompX + dPrimeZcompY + dPrimeZcompZ;

            xInputArray[index] = (float)outputX;
            yInputArray[index] = (float)outputY;
            zInputArray[index] = (float)outputZ;
            //////////////////////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            //////////////////////////<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        }
    }

    public float getValue(Axis axis, RecordType recordType, int index)
    {
        float returnVal;

        switch (axis)
        {
            case X:
                switch (recordType)
                {
                    case acceleration:
                        returnVal = xDataArray[index];
                        break;
                    case rotation:
                        returnVal = xRotationArray[index];
                        break;
                    case gravity:
                        returnVal = xGravityArray[index];
                        break;
                    default:
                        returnVal = 0.0F;
                }
                break;
            case Y:
                switch (recordType)
                {
                    case acceleration:
                        returnVal = yDataArray[index];
                        break;
                    case rotation:
                        returnVal = yRotationArray[index];
                        break;
                    case gravity:
                        returnVal = yGravityArray[index];
                        break;
                    default:
                        returnVal = 0.0F;
                }
                break;
            case Z:
                switch (recordType)
                {
                    case acceleration:
                        returnVal = zDataArray[index];
                        break;
                    case rotation:
                        returnVal = zRotationArray[index];
                        break;
                    case gravity:
                        returnVal = zGravityArray[index];
                        break;
                    default:
                        returnVal = 0.0F;
                }
                break;
            default:
                returnVal = 0.0F;
                break;
        }
        return returnVal;
    }

//    public float getMaxOfAbsValue(Axis axis, RecordType recordType)
//    {
//
//    }

        //stringFromJNI();
        //long testVar = sumIntegers(1,2);
    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
//    public native void stringFromJNI();
//    private native long sumIntegers(int first, int second);
}
