package com.example.racinglumber;

public class rotationMath {
    public static float[] normalizeRotation(float []input, float[]gravity)
    {
        float[] resultFlt = new float[2];

        //only get i and j from quaternions because that's all we need for rotation
        //i = gravity[0]input[1]+gravity[1]input[0]+gravity[3]input[2]-gravity[2]input[3]
        resultFlt[0] = (gravity[0]*input[1])+(gravity[1]*input[0])+(gravity[3]*input[2])-(gravity[2]*input[3]);
        //j = gravity[0]input[2]+gravity[1]input[3]+gravity[2]input[0]-gravity[3]input[1]
        resultFlt[1] = (gravity[0]*input[2])+(gravity[1]*input[3])+(gravity[2]*input[0])-(gravity[3]*input[1]);

        return resultFlt;//i(number)+i(complexplane)j
    }
}
