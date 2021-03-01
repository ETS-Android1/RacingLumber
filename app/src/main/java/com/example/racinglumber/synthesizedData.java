package com.example.racinglumber;

import static com.example.racinglumber.dataStorage.dataArrayLen;

//This class contains a set of synthesized latitudal, longitudal, etc. data using sensor fusion
public class synthesizedData {
    private static float[] lateralDataArray; //left (negative) right (positive acceleration)
    private dataStorage recordedVars; //todo this should just be static?

    //custom constructor
    public synthesizedData()
    {
        lateralDataArray = new float[dataArrayLen];
        recordedVars = new dataStorage();
        computeLateral();
    }


    public void computeLateral()
    {
        int i;
        double latitude;
        double latitudeNext;
        double longitude;
        double longitudeNext;
        double latDelta, longDelta;


        for (i = 0; i < dataArrayLen-1; i++)
        {
            //find GPS vector using current and next points
            latitude = recordedVars.getGPSValueFromAccelDataIndex(true, i);
            latitudeNext = recordedVars.getGPSValueFromAccelDataIndex(true, i+1);
            longitude = recordedVars.getGPSValueFromAccelDataIndex(false, i);
            longitudeNext = recordedVars.getGPSValueFromAccelDataIndex(false, i+1);

            latDelta = latitudeNext - latitude;
            longDelta = longitudeNext - longitude;

            //find angle between XY acceleration vectors and GPS vector

            //split latitude and longitude parts
        }
    }

}
