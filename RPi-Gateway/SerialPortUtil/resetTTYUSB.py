#*******************************************************************************
# Copyright (C) 2015 - Amit Kumar Mondal <admin@amitinside.com>
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#*******************************************************************************
#!/usr/bin/env python
import os
import sys
from subprocess import Popen, PIPE
import fcntl
driver = sys.argv[-1]
print "resetting driver:", driver
USBDEVFS_RESET= 21780

try:
    lsusb_out = Popen("lsusb | grep -i %s"%driver, shell=True, bufsize=64, stdin=PIPE, stdout=PIPE, close_fds=True).stdout.read().strip().split()
    bus = lsusb_out[1]
    device = lsusb_out[3][:-1]
    f = open("/dev/bus/usb/%s/%s"%(bus, device), 'w', os.O_WRONLY)
    fcntl.ioctl(f, USBDEVFS_RESET, 0)
except Exception, msg:
    print "failed to reset device:", msg
