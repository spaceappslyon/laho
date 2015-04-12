import SocketServer

class MyTCPHandler(SocketServer.BaseRequestHandler):
    summary = {}	
    liste = [
    "ear-mhop.mp3_best.mp3",
    "earthakr.mp3_best.mp3",
    "Launch-Sound_Female-Voice-Countdown.mp3_best.mp3",
    "Launch-Sound_Jupiter-Launch.mp3_best.mp3",
    "Launch-Sound_Shuttle-Landing.mp3_best.mp3",
    "Sound-Bite_Apollo-11_One-Small-Step_Quote.mp3",
    "VanA-2014-08-28_0313.mp3"
    ]
    liste2 = [
    "STS-41-Launch-Onboard-Nat-Sound_part0.mp3",
    "STS-41-Launch-Onboard-Nat-Sound_part1.mp3",
    "STS-41-Launch-Onboard-Nat-Sound_part2.mp3",
    "STS-41-Launch-Onboard-Nat-Sound_part3.mp3"
    ]

    def most_common(self,lst):
        return max(set(lst), key=lst.count)    

    def write_stat(self):
        f = open("data","w")
	for i in self.liste:
	    try:
	        f.write(i+" "+ str(self.most_common(self.summary[i])))
	    except:
		f.write(i+" -1")
	    f.write("\n")
	f.write("STS-41-Launch-Onboard-Nat-Sound.mp3 ")
	for i,w in enumerate(self.liste2):
            try:
                f.write(str(self.most_common(self.summary[w])))
            except:
                f.write("-1")
            if i < len(self.liste2)-1:
                f.write(" ")
	f.close()

    def handle(self):
        # self.request is the TCP socket connected to the client
        self.data = self.request.recv(1024).strip()
        data = self.data.split(" ")
	title = data[0]
	typem = int(data[1])
	print "Music:",title,"Type",typem
        if title in self.summary:
            self.summary[title].append(typem)
	else:
	    self.summary[title] = [typem]
        self.write_stat()
	

if __name__ == "__main__":
    HOST, PORT = "195.154.15.21", 3000
    # Create the server, binding to localhost on port 9999
    server = SocketServer.TCPServer((HOST, PORT), MyTCPHandler)
    # Activate the server; this will keep running until you
    # interrupt the program with Ctrl-C
    server.serve_forever()
