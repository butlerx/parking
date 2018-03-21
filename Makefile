JFLAGS = -g
JC = javac
.SUFFIXES: .java .class

.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = $(wildcard *.java)

default: classes

classes: $(CLASSES:.java=.class)

.PHONY: clean

clean:
	$(RM) *.class

run:
	$(classes)
	java Parking
