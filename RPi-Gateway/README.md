# README #

This is the repository for the development of a complete end-to-end secure home automation solution by Swissbit AG, Germany Munich. 

### What is this repository for? ###

* This repository contains all the **Home Automation** Related Sources for the complete subsystem
* 1.0.0-SNAPSHOT

### How do I get set up for RPi-Gateway Development? ###

## How do I get my Eclipse set up for RPi-Gateway Development? ##

* Install **Maven 3** in your local machine
* Checkout the complete Eclipse Kura Source from Github. Use **Develop Branch**.
* Checkout the complete source of Swissbit Secure Home Automation's RPi-Gateway
* Now build the complete **Eclipse Kura** Source
* The complete Successful Build of **Eclipse Kura** is an integral part before you proceed with the next steps
* If the Eclipse Kura Build is successful, you can now proceed building the Swissbit Secure Home Automation Solution Source.
* You need **Eclipse IDE** to import the Source. Don't use NetBeans or IntelliJ or any other IDE to import as the source has a dependency on IDE itself.
* In Eclipse IDE, you have to install **mToolkit** from **http://mtoolkit.tigris.org**
* Import the complete Swissbit Home Automation Solution's RPi-Gatway Source in Eclipse as Maven Projects (Import -> Existing Maven Projects)
* After you successfully imported the source, you have to open **com.swissbit.dp.commons.dpp** file (found under **com.swissbit.dp.commons.dp** project)
* In **Bundles Tab**, change the **"/Users/AMIT/swissbit/"** locations to your current source location
* After you change the locations, you have to right click on the same file from Package Explorer Menu and then select Export and it will open a pop up box.
* Then Select Ant Script under Deployment Package Deployment
* It will ask you to replace and go ahead wit that.
* You have to follow these steps with **com.swissbit.dp.zwave.dpp** (found under **com.swissbit.dp.zwave**)
* Now open the shell script (found under **com.swissbit.dp.part**)
* Change the **home_dir** and **cp_dir**
* Now build the **com.swissbit.parent** project
* You will get 3 files in your directory which you have mentioned in the shell script
* Now have to build the **org.eclipse.kura.web**

## How do I get my Raspberry Pi set up for RPi-Gateway Development? ##

* You need Swissbit Secure Element SD Card
* Install Raspbian OS to the Swissbit's Secure SD Card depending on the the architecture of your Raspberry Pi (ARMv6 or ARMv7)
* Replace OpenJDK with **Oracle Java 8 Hotspot JDK**
* Install **Eclipse Kura (No Net with Web UI)** depending on the the architecture of your **Raspberry Pi (ARMv6 or ARMv7)**
* You can access Eclipse Kura's Web UI from the Web Broswer
* You have to use **kura** as username and **admin** as password to access the Web UI
* Now go to Packages section and install the files which have been generated after you built the Swissbit Sources (the **.dp** files)
* Install the **dependencies** package first and then **commons** package and lastly the **zwave** package
* Copy the **org.eclipse.kura.web-1.0.4-SNAPSHOT.jar** to Raspberry Pi
* Rename it to **org.eclipse.kura.web_1.0.4.jar**
* And copy this file to **/opt/eclipse/kura/kura/plugins**
* Now Restart the Raspberry Pi
* After the restart, you have to configure the Swissbit Configurations according to your need
* First of all, you have to specifiy the **MQTT Configuration Parameters** from the **MQTTDataTransport**
* Make Sure you need to change only the **broker.url, account.name, username, password, keep-alive and timeout parameters**.
* For convenience, make sure to set **Kepp-Alive** and **timeout** to **10** and **account.name** to **swissbit**.
* Now go to **DataService** configuration section, and make sure to set **auto.starup** to **true** and **interval** to **10**
* You need to create a folder named **swissbit** under **/home/pi**
* Create a file named **clients-revoked.perm** in Swissbit folder
* Another file named **all-clients-connected.perm** in Swissbit folder
* Copy the **resetTTYUSB.py** from Swissbit Source (found under **SerialPortUtil**) to Swissbit folder
* Create a folder named **logs** under Swissbit folder
* Create a file named **swisbsit.log** under logs folder
* You have to create another folder named **assd**
* This folder comprises the python scripts to encrypt and decrypt messages using Swissbit Secure Element
* You will find these python scripts under **Secure_Element** folder in **default branch**
* You need the proper **assd.ko** module that matches your Raspbian architecture (ARMv6 or ARMv7)
* Make sure to change the newly created and copies files and folders permissions to 777

## How do I get my Z-Wave Device set up? ##

* You have to use **AeonLabs Z-Stick Series 2 PC Controller** to use Z-Wave Device Management
* You can buy any Z-Wave Device that can communicate with **AeonLabs PC Controller** (I have used **AeonLabs Smart Energy Switch**)
* Make sure to use Z-Wave Devices that are made for Europe
* First pair the device with PC Controller and then connect the PC Controller to Raspberry Pi
* The PC Controller communicates with the Raspberry Pi using Serial Port **/dev/TTYUSB0**
* Make sure it's same or else you have to change it in **com.swissbit.device.zwave.operation**
* You have to export **com.swissbit.device.zwave.operation** as a Runnable Jar and copy the runnable jar to Swissbit folder in Raspberry Pi
* Change the permission of the runnable jar to 777

## How do I get my IFTTT set up? ##

* You can sign up in IFTTT using your email address
* You can setup your own recipe here
* The primary constraint for Swissbit IFTTT Module is that you have to use Tagged Email trigger
* Once you set up your own recipe, you can go to IFTTT section in the raspberry Pi Web UI to configure your email address smtp confguration
* Make sure to use the correct hashtags for your recipes

### Learning Objective for Contribution ###

* Java 8
* OSGi R5
* Eclipse Kura
* Eclipse Coding Standards
* Open Source Standard
* Z-Wave Protocol Standard

### Contribution guidelines ###

* Before you commit your code to this project, please talk to the repo owner first
* If the communication with the repo owner is getting delayed, you can fork the branch
* Code review

### Who do I talk to? ###

* Amit Kumar Mondal (admin@amitinside.com) for RPi-Gateway
* Swissbit AG