#!/usr/bin/python
#https://github.com/jiaaro/pydub

from pydub import AudioSegment
import sys

sec = 1000
begin = 0
end = 10*sec

if len(sys.argv) < 2:
    print("I need at least one arg")
    sys.exit(1)

files = sys.argv[1]
filem = files.split("/")[-1]
if filem[-3:] == "mp3":
    song = AudioSegment.from_mp3(files)
elif filem[-3:] == "wav":
    song = AudioSegment.from_wav(files)
else:
    print("Unknow format")
    sys.exit(2)
if len(sys.argv) >= 3:
    begin = int(sys.argv[2])*sec
    end = begin + 10*sec
    if len(sys.argv) == 4:
        end = int(sys.argv[3])*sec
print("spliting from",str(begin),"to",str(end))
awesome = song[begin:end]
awesome.export(filem+"_short.mp3", format="mp3")




