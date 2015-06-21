#!/usr/bin/python

import sys
import os.path
import binascii

#print 'Number of arguments:', len(sys.argv), 'arguments.'

#print 'Argument List:', str(sys.argv)

if len(sys.argv) != 3:
	print "usage: encrypt.py <tobeencryptedstring>"
	exit()

filename = "/dev/assd"

#if not os.path.isfile(filename):
#	print "ASSD device not found!"
#	exit()

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
senderid   = '1000000000000001'

#getidcommand = bytearray([0x90, 0x10, 0x00, 0x00, 0x00])
#with open(filename, 'w') as f:
#        f.write("APDU#"+getidcommand+"\n")
#f.closed
#with open(filename, 'r') as f:
#        response = f.read()
#f.closed
#senderid = response [5:len(response)-2] 


recieverid = sys.argv[1]
mypayload  = sys.argv[2]

padlength = 16 - ((len(mypayload)+16)%16)

pad = bytearray(padlength)

nonce = os.urandom(16)

encryptcommand = bytearray([0x90, 0x21, 0x00, 0x00, 0x00])
encryptcommand[4] = len(nonce) + len(senderid) + len(recieverid) + len(mypayload) + padlength

encryptcommand += nonce + senderid + recieverid + mypayload + pad

#print binascii.hexlify(encryptcommand)
with open(filename, 'w') as f:
	f.write("APDU#"+encryptcommand+"\n")
f.closed
with open(filename, 'r') as f:
        response = f.read()
f.closed
#print binascii.hexlify(response[len(response)-2])
#if response.charAt(len(response)-2) == 0x90:
response = response [5:len(response)-2]

print binascii.hexlify(response)

#else:
#	print "-1"
exit()


