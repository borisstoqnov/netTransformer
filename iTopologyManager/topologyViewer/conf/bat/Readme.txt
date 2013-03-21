Prerequirements:

Discovery needs JRE  "1.6.0_25" or later.
In order to work properly you have to have connectivity to all network IP addresses in your network.
Will be good (but not a must) if the station that is used for running the discovery is able to resolve device hostname through DNS.
All the devices in your network have to have the same SNMP community string.

So if all those are met:

How to start iTransformer?

!On Windows
cd iTransformer\bin

iTransformer.bat


! On Linux/Unix
cd iTransformer/bin
./iTransformer.sh

Once the GUI starts go the file menu and create a new project.
Then go to the Discovery menu item and click on the Resource tab. The resource settings will be used by the device discovery process.
First go to the SNMP resource and edit the SNMP communities. Then go to CISCO resource if you have CISCO devices, to JUNIPER if you have juniper and so on.
You will have to enter the correct SNMP community settings for your particular device types.
Once you are ready go to Start Discovery enter your initial IP address (e.g the address of one of the devices in your network) and click Start!.
If the discovery process starts to produce tons of data in the login window that's it you got it right.
The discovery process will create a new version of your network.

How to reveal the discovered network topology?
Go to File open graph and navigate to the version folder and once there choose undirected.graphmls. Instantly you should be able to see the discovered devices.


Since the fact that the discovery process might continue quite a while (currnet estimate is 6 hours 500 devices) note that iTopoManager supports dynamic topology
 update through the Reload button.

Once you did this you can run a new discovery or just play with the GUI. Rightclick on some of the devices and you will se a number of RightClick methods.

For any further information about iTransformer please do not hesitate to consult with our web-site or drop us an email on info{AT}itransformers.net
