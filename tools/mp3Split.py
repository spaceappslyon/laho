#!/usr/bin/python
##https://github.com/jiaaro/pydub

from pydub import AudioSegment
import sys

sec = 1000
begin = 0
end = 10*sec

if len(sys.argv) < 2:
    print("I need at least one arg")
    sys.exit(1)
for files in sys.argv[1:]:
    filem = files.split("/")[-1]
    if filem[-3:] == "mp3":
        song = AudioSegment.from_mp3(files)
    elif filem[-3:] == "wav":
        song = AudioSegment.from_wav(files)
    else:
        print("Unknow format")
        sys.exit(2)
    nb_part = int(len(song)/(10*sec))
    for i in range(nb_part+1):
        awesome = song[(10*sec*i):(10*sec*(i+1))]
        awesome.export(filem+"_short"+str(i)+".mp3", format="mp3")
    print(filem,"was split in",str(nb_part),"parts")




