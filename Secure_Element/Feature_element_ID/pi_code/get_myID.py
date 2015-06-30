#!/usr/bin/python

import sys
import os.path
import binascii

#This program needs no parameter

filename = "/dev/assd"

with open(filename, 'r') as f:
	response = f.read()
f.closed

if response.startswith("TERM#PRESENT"):
	#print "Request SE required..."
	with open(filename, 'w') as f:
        	f.write("TERM#REQUEST")
	f.closed
	with open(filename, 'r') as f:
        	response = f.read()
	f.closed

	selectcommand = bytearray([0x00, 0xA4, 0x04, 0x00, 0x0B, 0xD2, 0x76, 0x00, 0x01, 0x62, 0x44, 0x65, 0x6D, 0x6F, 0x01, 0x01])
	with open(filename, 'w') as f:
        	f.write("APDU#"+selectcommand+"\n")
	f.closed
	with open(filename, 'r') as f:
        	response = f.read()
	f.closed
	#print binascii.hexlify(response)

#Senderid will be obtained by another APDU command by new applet.

getidcommand = bytearray([0x90, 0x10, 0x00, 0x00, 0x00])

with open(filename, 'w') as f:
        f.write("APDU#"+getidcommand+"\n")
f.closed

with open(filename, 'r') as f:
        response = f.read()
f.closed

senderid = response [5:len(response)-2] 

print binascii.hexlify(senderid)

exit()


