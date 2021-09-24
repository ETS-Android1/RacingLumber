## About The Project

This project is aimed at providing flexible race telemetry.  Racing Lumber does not require prior knowledge of the mounting orientation of the mobile device running this application.  This means that Racing Lumber can be used while in a sealed pocket for go-karting, mounted to an RC car/plane, among other potential applications.

I originally created this app as a means to provide go-karting or amateur racing telemetry, where a user cannot or does not want to mount telemetry equipment.  Instead, press play, put the phone in your sealed pocket, and you are ready to race.

This project provides lateral/longitudal acceleration data graphically, and can import/export raw accelerometer and GPS data. Racing Lumber is open source so that users can modify and improve it as they see fit.

## Theory

Lateral and longitudal acceleration are found without orientation using the phone's gravity vector and an inputted forward vector.  Recorded XYZ accelerometer data is rotated such that the Z vector points directly up.  Then the XY data (ignoring Z since it's approximately zero) is split into lateral and longitudal acceleration using the forward vector.  See usage for instructions for more details.

### Built With

* [GraphView](https://github.com/jjoe64/GraphView)

### Prerequisites

To build this project you need Android Studio with CMAKE support, and Git.
* android studio with CMAKE support: https://developer.android.com/studio/projects/install-ndk
* git: https://git-scm.com/

Alternatively, download the app from Google play: https://play.google.com/store/apps/details?id=com.app.racinglumber

The Google play download is a few dollars.  Feel free to use this download method if you would like a slightly easier install process or if you would like to tip the developer.

## Known issues

* During the first recording, the app will ask for GPS permissions.  The recording will be affected by this authentication, so it is recommended to run a quick recording initially to give the app its permissions
* Required memory for a recording is not calculated by the app prior to recording.  A recording may fail if it runs for an extended time or the app is run on a phone with little memory
* GPS accuracy/availability is dependent on the mobile phone speed and the availability of GPS data from the Google API.  This will vary by device and location

## Usage

This project, being a solo development effort, follows a specific workflow.

Written instructions for the Record(default) View:
* Choose a recording length
* Select "Correct tilt in recorded data" if you are recording for lateral/longitudal data.  For raw data recording, leave it unselected
* Choose a GPS polling rate in milliseconds.  Actual GPS polling rate will be limited by your device.  The lowest supported value is 10ms
* If you are using this app in a pocket where buttons can be inadvertantly pressed, press the "lock" button.  This will disable button input during the recording interval
* Press record.  It is valid to press record again to end the recording, or you can let the full record period elapse.  Do not press the power button on your phone (it will pause recording)
* Use the arrow keys to select a GPS location in the recording.  Choose a location where you were accelerating in a straight line
* Press the Set Forward Vector to set the forward vector.  This is used to split acceleration into lateral and longitudal vectors
* Select graph view using the bottom navigation to view recorded data, or save view to save the recorded data

Written instructions for the Graph View:
* Select data that you would like to using the spinner at the top of the view.  By default after recording, data is stored in set one.  Set one and two can be populated/overwritten in the save view
* Scroll/resize the data as desired 
* Select set one, two, or one and two buttons to choose which data to offset.  By default this is set to set one
* Use the arrow keys to set offsets for set one/two/onetwo based on the selected button.  This can be used to align/compare datasets
* The mapview will show the closest GPS data for the dataset selected.  Specifically, this GPS location is the closest measurement to the accelerometer measurement on the leftmost side of the screen for the selected dataset

Written instructions for the Save View:
* Android has restrictions on how files are stored.  To get around this to save datasets, datasets are stored as local to the app
* After recording a dataset, press "save to external" and choose a filename.  Depending on the recording length, the file size may be large and the screen may freeze for a while during the save.  This is expected
* If you want to export this dataset, send it via gmail/google drive from the phone to yourself.  As the dataset file is local to the app, it cannot be seen over PC USB connection
* Load a dataset to set one or two for viewing in the graph view.  You can load the same dataset into dataset one and two to compare the same dataset at different points in time
* Use "delete external save" to delete recorded datasets as required

## Contributing

This app codebase is available for modification and use in other projects under the Apache 2.0 license.  I will not be maintaining this project as I am moving on to other projects.  I have pushed a release-spencer branch for all releases/updates by myself.

## License and Legal

This project is published under the Apache 2.0 license: https://www.apache.org/licenses/LICENSE-2.0

I accept no responsibility for any damage or injury resulting from this software.  This software is provided free of use, the use of which is at the user's responsibility.