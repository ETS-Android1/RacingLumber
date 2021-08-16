package com.example.racinglumber;

import static com.example.racinglumber.dataStorage.dataArrayLen;

//This class contains a set of synthesized latitudal, longitudal, etc. data using sensor fusion
//builds based on current data in dataStorage
public class synthesizedData {

    public int dataLen = 0;
    public float[] lateralDataArray; //left (negative) right (positive) acceleration
    public float[] longitudalDataArray; //backward (negative) forward (positive) acceleration
    public double[] latitudeArray;
    public double[] longitudeArray;
    public long[] GPSEventTime;
    public static int GPSIndex; //index of GPS data

    public void generateSynthDataFromDataStorage()
    {
        if (dataArrayLen > 0)
        {
            computeLateralLongitudalArrays();

            /*Instantiate gps arrays*/
            dataLen = dataStorage.getDataArrayLen();
            latitudeArray = new double[dataStorage.getDataArrayLen()];
            longitudeArray = new double[dataStorage.getDataArrayLen()];
            GPSEventTime = new long[dataArrayLen];

            /*Copy latitude and longitude array*/
            for (int index = 0; index < dataStorage.getDataArrayLen(); index++)
            {
                latitudeArray[index] = dataStorage.latitudeArray[index];
                longitudeArray[index] = dataStorage.longitudeArray[index];
                GPSEventTime[index] = dataStorage.GPSEventTime[index];
                GPSIndex = dataStorage.GPSIndex;
            }
        }
    }

    public void computeLateralLongitudalArrays()
    {
        float xVal, yVal, xyMag, xyDotProduct, cosTheta, theta;

        lateralDataArray = new float[dataStorage.getDataArrayLen()];
        longitudalDataArray = new float[dataStorage.getDataArrayLen()];

        //Instantiate lateral data array
        for (int index = 0; index < dataStorage.getDataArrayLen(); index++)
        {
            //use dot product to get angle:

            //new vector, get magnitude
            xVal = dataStorage.getSensorValue(dataStorage.Axis.X, dataStorage.RecordType.acceleration, index);
            yVal = dataStorage.getSensorValue(dataStorage.Axis.Y, dataStorage.RecordType.acceleration, index);
            xyMag = (float) Math.sqrt((xVal*xVal)+(yVal*yVal));
            //get dot product
            xyDotProduct = (xVal*dataStorage.forwardVectorX) + (yVal*dataStorage.forwardVectorX);
            //get cosine theta
            cosTheta = xyDotProduct/xyMag;
            //get angle between vectors
            theta = (float) Math.acos(cosTheta);

            //todo some of these signs might need to flip
            //Get the lateral component
            lateralDataArray[index] = xyMag*((float)Math.sin(theta));
            //get the longitudal component
            longitudalDataArray[index] = xyMag*cosTheta;
        }
    }

}

