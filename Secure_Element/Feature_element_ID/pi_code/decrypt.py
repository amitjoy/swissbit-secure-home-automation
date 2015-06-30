#!/usr/bin/python

import sys
import os.path
import binascii
import array

#print 'Number of arguments:', len(sys.argv), 'arguments.'

#print 'Argument List:', str(sys.argv)

if len(sys.argv) != 2:
	print "usage: encrypt.py <tobedecryptedstring>"
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


mypayload = sys.argv[1]
hexdata = mypayload.decode("hex")
cryptogram = bytearray(hexdata)
decryptcommand = bytearray([0x90, 0x22, 0x00, 0x00, 0x00]) + cryptogram
decryptcommand[4] = len(cryptogram)
with open(filename, 'w') as f:
	f.write("APDU#"+decryptcommand+"\n")
f.closed
with open(filename, 'r') as f:
        response = f.read()
f.closed

senderid = response [5:5+16]
print binascii.hexlify(senderid)

plaintext = response [5+16+16:len(response)-2]
print plaintext

exit()

