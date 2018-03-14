CLASSES = ReadAndReplace.java CheckedIOStream.java WriteProg.java ReadProg.java

default:
	javac -d .  $(CLASSES)
