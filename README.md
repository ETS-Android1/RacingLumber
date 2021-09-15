<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Thanks again! Now go create something AMAZING! :D
-->



<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]



<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/othneildrew/Best-README-Template">
    <img src="images/logo.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">Racing Lumber</h3>

  <p align="center">
    Open source mobile racing telemetry
  </p>
</p>



<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgements">Acknowledgements</a></li>
  </ol>
</details>



<!-- Motivation -->
## About The Project

THIS PROJECT IS IN PROGRESS, ALONG WITH THE README.  IT IS NOT READY TO USE, STAY TUNED

This project is aimed at providing flexible race telemetry.  Racing Lumber does not require prior knowledge of the mounting orientation of the mobile device running this application.  This means that Racing Lumber can be used while in a sealed pocket for go-karting, mounted to an RC car/plane, among other potential applications.

I originally created this app as a means to provide go-karting or amateur racing telemetry, where a user cannot or does not want to mount telemetry equipment.  Instead, press play, put the phone in your sealed pocket, and you are ready to race.

This project provides lateral/longitudal acceleration data graphically, and can import/export raw accelerometer and GPS data. Racing Lumber is open source so that users can modify and improve it as they see fit.

### Built With

This section should list any major frameworks that you built your project using. Leave any add-ons/plugins for the acknowledgements section. Here are a few examples.
* [GraphView](https://github.com/jjoe64/GraphView)
* [Readme Template](https://github.com/othneildrew/Best-README-Template)

### Prerequisites

To build this project, you need Android Studio with CMAKE support, and Git.
* android studio with CMAKE support: https://developer.android.com/studio/projects/install-ndk
* git: https://git-scm.com/

<!-- USAGE EXAMPLES -->
## Usage

This project, being a solo development effort, follows a specific workflow.

Written instructions for the Record(default) View:
* Choose a recording length
* Select "Correct tilt in recorded data" if you are recording for lateral/longitudal data.  For raw data recording, leave it unselected
* Choose a GPS polling rate in milliseconds.  Actual GPS polling rate will be limited by your device.  The lowest supported value is 10ms
* Press record.  It is valid to press record again to end the recording, or you can let the full record period elapse
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

<!-- ROADMAP -->
## Remaining Development

* App currently only runs when it is open (in focus).  Recording needs to run as a background task when screen is off.

<!-- LICENSE -->
## License

[Apache 2.0] (https://www.apache.org/licenses/LICENSE-2.0)
