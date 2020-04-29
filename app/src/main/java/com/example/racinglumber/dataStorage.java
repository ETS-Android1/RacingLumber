package com.example.racinglumber;

public class dataStorage {
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
}
