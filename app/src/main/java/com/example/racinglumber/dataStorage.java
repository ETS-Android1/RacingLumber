package com.example.racinglumber;

import android.hardware.Sensor;
import android.location.Location;
import android.os.SystemClock;

public class dataStorage {

    enum Axis
    {
        X,
        Y,
        Z,
        Magnitude
    }

    enum RecordType
    {
        acceleration,
        rotation,
        gravity
    }

    public static int dataArrayLen = 0; //default to 0, since no data is recorded

    private static float[] xDataArray;
    private static float[] yDataArray;
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

    private static double[] latitudeArray;
    private static double[] longitudeArray;
    private static int GPSIndex = 0; //index of GPS data
    private static long[] GPSEventTime;

    public void clearStorage()
    {
        xDataArray = null;
        yDataArray = null;
        zDataArray = null;
        accelEventTime = null;
        accelIndex = 0;

        xRotationArray = null;
        yRotationArray = null;
        zRotationArray = null;
        rotationEventTime = null;
        rotationIndex = 0;

        xGravityArray = null;
        yGravityArray = null;
        zGravityArray = null;
        gravityEventTime = null;
        gravityIndex = 0;

        latitudeArray = null;
        longitudeArray = null;
        GPSEventTime = null;
        GPSIndex = 0;

        /*If the data length is zero, leave all of the data arrays as null*/
        if (dataArrayLen > 0)
        {
            xDataArray = new float[dataArrayLen];
            yDataArray = new float[dataArrayLen];
            zDataArray = new float[dataArrayLen];
            accelEventTime = new long[dataArrayLen];

            xRotationArray = new float[dataArrayLen];
            yRotationArray = new float[dataArrayLen];
            zRotationArray = new float[dataArrayLen];
            rotationEventTime = new long[dataArrayLen];

            xGravityArray = new float[dataArrayLen];
            yGravityArray = new float[dataArrayLen];
            zGravityArray = new float[dataArrayLen];
            gravityEventTime = new long[dataArrayLen];

            latitudeArray = new double[dataArrayLen];
            longitudeArray = new double[dataArrayLen];
            GPSEventTime = new long[dataArrayLen];
        }

    }

    public void setDataArrayLen(int inputDataLen)
    {
        dataArrayLen = inputDataLen;
    }
    public int getDataArrayLen() { return dataArrayLen; }

    public boolean writeGPSValToStorage(Location location)
    {
        boolean bufferFull = false;

        if ((GPSIndex >= dataArrayLen)
                || (latitudeArray == null)
                || (longitudeArray == null)
                || (GPSEventTime == null))
        {
            /*Out of range or buffer is full*/
            bufferFull = true;
        }
        else
        {
            latitudeArray[GPSIndex] = location.getLatitude();
            longitudeArray[GPSIndex] = location.getLongitude();
            GPSEventTime[GPSIndex] = SystemClock.elapsedRealtime();
            GPSIndex = GPSIndex + 1;
        }

        return bufferFull;
    }

    //true if latitude
    public double getGPSValueFromAccelDataIndex(boolean latOrLong, int accelIndex)
    {
        double returnVal;
        long accelEventTimestamp;
        int i;

        /*Check for null arrays or out of bounds*/
        if ((accelIndex >= dataArrayLen)
                || (accelEventTime == null)
                || (GPSEventTime == null)
                || (latitudeArray == null)
                || (longitudeArray == null))
        {
            returnVal = 0.0;
        }
        else
        {
            accelEventTimestamp = accelEventTime[accelIndex];

            /*Check if the timestamp searched for is before the first gps timeStamp*/
            if ((accelEventTimestamp < GPSEventTime[0]) || (accelIndex == 0))
            {
                i = 0;
            }
            else
            {
                for (i = 0; i < (dataArrayLen-1); i++)
                {
                    if ((i >= GPSIndex) && (i > 0))
                    {
                        /*Check if we are at the end of the GPS data array*/
                        i = GPSIndex - 1;
                        break;
                    }
                    else
                    {
                        if ((GPSEventTime[i] < accelEventTimestamp) && (GPSEventTime[i+1] >= accelEventTimestamp))
                        {
                            break; //matching timestamp found
                        }
                    }
                }
            }

            if (latOrLong)
            {
                returnVal = latitudeArray[i];
            }
            else
            {
                returnVal = longitudeArray[i];
            }
        }

        return returnVal;
    }

    public double getGPSValue(boolean latOrLong, int index) {
        double returnVal;

        if ((index > dataArrayLen)
                || (latitudeArray == null)
                || (longitudeArray == null))
        {
            /*Out of bounds, return zero*/
            returnVal = 0.0;
        }
        else
        {
            if (latOrLong)
            {
                returnVal = latitudeArray[index];
            }
            else
            {
                returnVal = longitudeArray[index];
            }
        }

        return returnVal;
    }

    public boolean writeSensorValToStorage(float xInput, float yInput, float zInput, int sensorType)
    {
        boolean bufferFull = false;

        if (sensorType == Sensor.TYPE_LINEAR_ACCELERATION)
        {
            if ((accelIndex >= dataArrayLen)
                    || (xDataArray == null)
                    || (yDataArray == null)
                    || (zDataArray == null)
                    || (accelEventTime == null))
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
            if ((rotationIndex >= dataArrayLen)
                    || (xRotationArray == null)
                    || (yRotationArray == null)
                    || (zRotationArray == null)
                    || (rotationEventTime == null))
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
            if ((gravityIndex >= dataArrayLen)
                    || (xGravityArray == null)
                    || (yGravityArray == null)
                    || (zGravityArray == null)
                    || (gravityEventTime == null))
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

    public void correctDataSetOrientation()
    {
        if (dataArrayLen > 0)
        {
            correctDataOrientation (xDataArray, yDataArray, zDataArray);
            correctDataOrientation (xRotationArray, yRotationArray, zRotationArray);
        }
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
    * : |GxG'| = |G'||G|sin(W), where W is the angle between the two vectors
    * : |GxG'| = [Gy,-Gx,0], which is a vector on the XY plane to rotate the G to G'
    * Now we calculate W
    * : sin(W) = abs(|GxG'|)/|G'||G|
    * : sin(W) = abs([Gy,-Gx,0])/abs([Gx,Gy,Gz])
    *
    * We now define the accelerometer data recorded when G was recorded as [Dx,Dy,Dz].  This data's
    * orientation is corrected such that the adjusted data D' would be the equivalent of recording when
    * the phone was flat on the XY axis with its face up (positive Z axis).
    * To do this we rotate D by angle W about vector |GxG'| (define as R from now on to ease notation)
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
        double normRx; double normRy;
        double dPrimeXcompX; double dPrimeXcompY; double dPrimeXcompZ;
        double dPrimeYcompX; double dPrimeYcompY; double dPrimeYcompZ;
        double dPrimeZcompX; double dPrimeZcompY; double dPrimeZcompZ;

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

            //Pout = q * Pin * conj(q)
            // Conjugate D by R: D' = RDR'
            //R = |GxG'|
            //D is data input
            //q = cos(W/2) + i ( Rx * sin(W/2)) + j (Ry * sin(W/2)) + k ( Rz * sin(W/2))
            //W=angle of rotation.
            //x,y,z = vector representing axis of rotation.

            //so for me, q is the rotation quaternion and needs absolute value to be 1.  Need to normalize |G'xG| = [-Gy,Gx,0]
            normRx = (yGravityArray[index] / absValGPRIMEcrossG);//normalized Rx
            normRy = (-1)*(xGravityArray[index] / absValGPRIMEcrossG);//normalized Ry
            //Normalized Rz = 0

            q0 = Math.cos(angleWinRadians/2); //cos(W/2)
            q1 = normRx * Math.sin(angleWinRadians/2); //i ( Rx * sin(W/2))
            q2 = normRy * Math.sin(angleWinRadians/2); //j (Ry * sin(W/2))
            //q3 = 0, k ( Rz * sin(W/2))

            /*Parameters for quaternion rotation are now calculated, use on inputX, inputY, and inputZ*/

            //x component of x output value of rotation
            dPrimeXcompX = Math.pow(q0,2) + Math.pow(q1, 2) - Math.pow(q2, 2);//todo remove https://www.weizmann.ac.il/sci-tea/benari/sites/sci-tea.benari/files/uploads/softwareAndLearningMaterials/quaternion-tutorial-2-0-1.pdf
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
            dPrimeZcompZ = inputZ*(Math.pow(q0,2)-(Math.pow(q1,2)+Math.pow(q2,2)));
            outputZ = dPrimeZcompX + dPrimeZcompY + dPrimeZcompZ;

            xInputArray[index] = (float)outputX;
            yInputArray[index] = (float)outputY;
            zInputArray[index] = (float)outputZ;
        }
    }

    public float getSensorValue(Axis axis, RecordType recordType, int index)
    {
        float returnVal;
        double squaredMag;

        if ((dataArrayLen > 0) && (index < dataArrayLen))
        {
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
                            break;
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
                            break;
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
                            break;
                    }
                    break;

                case Magnitude:
                    switch (recordType)
                    {
                        case acceleration:
                            squaredMag = Math.pow(xDataArray[index],2) + Math.pow(yDataArray[index],2) + Math.pow(zDataArray[index],2);
                            returnVal = (float)(Math.sqrt(squaredMag));
                            break;
                        case rotation:
                            squaredMag = Math.pow(xRotationArray[index],2) + Math.pow(yRotationArray[index],2) + Math.pow(zRotationArray[index],2);
                            returnVal = (float)(Math.sqrt(squaredMag));
                            break;
                        case gravity:
                            squaredMag = Math.pow(xGravityArray[index],2) + Math.pow(yGravityArray[index],2) + Math.pow(zGravityArray[index],2);
                            returnVal = (float)(Math.sqrt(squaredMag));
                            break;
                        default:
                            returnVal = 0.0F;
                            break;
                    }
                    break;

                default:
                    returnVal = 0.0F;
                    break;
            }
        }
        else
        {
            returnVal = 0.0F;
        }

        return returnVal;
    }

    public long getTimestampValue(RecordType recordType, int index)
    {
        long returnVal;

        if ((dataArrayLen > 0) && (index < dataArrayLen))
        {
            switch(recordType)
            {
                case acceleration:
                    returnVal = accelEventTime[index];
                    break;

                case rotation:
                    returnVal = rotationEventTime[index];
                    break;

                case gravity:
                    returnVal = gravityEventTime[index];
                    break;

                default:
                    returnVal = 0;
            }
        }
        else
        {
            returnVal = 0;
        }

        return returnVal;
    }

    public static String getName(Axis axis, RecordType recordType)
    {
        String returnVal;

        switch (axis)
        {
            case X:
                switch (recordType)
                {
                    case acceleration:
                        returnVal = "X Acceleration";
                        break;
                    case rotation:
                        returnVal = "X Rotation";
                        break;
                    case gravity:
                        returnVal = "X Gravity";
                        break;
                    default:
                        returnVal = "INVALID REFERENCE";
                        break;
                }
                break;

            case Y:
                switch (recordType)
                {
                    case acceleration:
                        returnVal = "Y Acceleration";
                        break;
                    case rotation:
                        returnVal = "Y Rotation";
                        break;
                    case gravity:
                        returnVal = "Y Gravity";
                        break;
                    default:
                        returnVal = "INVALID REFERENCE";
                        break;
                }
                break;

            case Z:
                switch (recordType)
                {
                    case acceleration:
                        returnVal = "Z Acceleration";
                        break;
                    case rotation:
                        returnVal = "Z Rotation";
                        break;
                    case gravity:
                        returnVal = "Z Gravity";
                        break;
                    default:
                        returnVal = "INVALID REFERENCE";
                        break;
                }
                break;

            case Magnitude:
                switch (recordType)
                {
                    case acceleration:
                        returnVal = "Acceleration Magnitude";
                        break;
                    case rotation:
                        returnVal = "Rotation Magnitude";
                        break;
                    case gravity:
                        returnVal = "Gravity Magnitude";
                        break;
                    default:
                        returnVal = "INVALID REFERENCE";
                        break;
                }
                break;

            default:
                returnVal = "INVALID REFERENCE";
                break;
        }
        return returnVal;
    }

    public float getMaxOfAbsValue(Axis axis, RecordType recordType)
    {
        int index;
        float maxValueFound = 0.0F;
        float newValueFound;

        for (index = 0; index < dataArrayLen; index++)
        {
            newValueFound = getSensorValue(axis, recordType, index);

            if (newValueFound < 0)
            {
                newValueFound *= -1.0F; //get absolute value of the new value
            }

            if (newValueFound > maxValueFound)
            {
                maxValueFound = newValueFound;
            }
        }

        return maxValueFound;
    }

    public long getGPSTimestampValue(int index)
    {
        long returnVal;

        if ((dataArrayLen > 0) && (index < dataArrayLen))
        {
            returnVal = GPSEventTime[index];
        }
        else
        {
            returnVal = 0;
        }

        return returnVal;
    }
}
