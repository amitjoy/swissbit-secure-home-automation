# README #

This is the repository for the development of a complete end-to-end secure home automation solution by Swissbit AG, Germany Munich. 

### What is this repository for? ###

* This repository contains all the **Home Automation** Related Source Codes for the complete subsystem
* 1.0.0-SNAPSHOT

### How do I get set up for RPi-Gateway Development? ###

## How do I get my Eclipse set up for RPi-Gateway Development? ##

* Install **Maven 3** in your local machine
* Checkout the complete Eclipse Kura Source from Github. Use Develop Branch.
* Checkout the complete source of Swissbit Secure Home Automation's RPi-Gateway
* Now build the complete **Eclipse Kura** Source
* The complete Successful Building of **Eclipse Kura** is an integral part
* If the Eclipse Kura Build is successful, you can now proceed building the Swissbit Secure Home Automation Solution Source.
* You need **Eclipse IDE** to import the Source. Don't use NetBeans or IntelliJ or any other IDE to import as the source has a dependency on IDE itself.
* In Eclipse IDE, you have to install **mToolkit** from **http://mtoolkit.tigris.org**
* Import the complete Swissbit Home Automation Solution's RPi-Gatway Source in Eclipse as Maven Projects (Import -> Existing Maven Projects)
* After you successfully imported the source, you have to open **com.swissbit.dp.commons.dpp** file (found under **com.swissbit.dp.commons.dp** project)
* In Bundles Tab, change the **"/Users/AMIT/swissbit/"** locations to your current source location
* After you change the locations, you have to right click on the same file from Package Explorer Menu and then select Export and it will open a pop up box.
* Then Select Ant Script under Deployment Package Deployment
* It will ask you to replace and go ahead wit that.
* You have to follow these steps with **com.swissbit.dp.zwave.dpp** (found under **com.swissbit.dp.zwave**)
* Now open the shell script (found under com.swissbit.dp.part)
* Change the **home_dir** and **cp_dir**
* Now build the **com.swissbit.parent** project
* You will get 3 files in your directory which you have changed in the shell script
* Now have to build the **org.eclipse.kura.web**

## How do I get my Raspberry Pi set up for RPi-Gateway Development? ##

* Install Raspbian OS to your SD Card depending on the the architecture of your Raspberry Pi (ARMv6 or ARMv7)
* Install Eclipse Kura (No Net with Web UI) depending on the the architecture of your Raspberry Pi (ARMv6 or ARMv7)
* You can access Eclipse Kura's Web UI from the Web Broswer
* You have to use kura as username and admins as password to access the Web UI
* Now go to Packages section and install the files which have been generated after you built the Swissbit Sources (the .dp files)
* Install the dependencies package first and then commons package and lastly the zwave package
* Copy the org.eclipse.kura.web-1.0.4-SNAPSHOT.jar to Raspberry Pi
* Rename it to org.eclipse.kura.web_1.0.4.jar
* And copy this file to /opt/eclipse/kura/kura/plugins
* _ Now Restart the Raspberry Pi
* After the restart, you have to configure the Swissbit Configurations according to your need
* First of all, you have to specifiy the MQTT Configuration Parameters from the MQTTDataTransport
* 


### Contribution guidelines ###

* Before you commit your code to this project, please talk to the repo owner first
* Code review
* Other guidelines

### Who do I talk to? ###

* Amit Kumar Mondal (admin@amitinside.com) for RPi-Gateway
* Swissbit AG