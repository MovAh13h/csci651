.PHONY: build run

run: clean build
	@java pktanalyzer/pktanalyzer pkt/icmp2a.bin

build:
	@javac pktanalyzer/pktanalyzer.java

clean:
	@rm -rf pktanalyzer/*.class