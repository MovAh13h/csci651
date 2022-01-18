.PHONY: build run

run: clean build
	@java pktanalyzer/pktanalyzer pkt/new_tcp_packet1.bin

build:
	@javac pktanalyzer/pktanalyzer.java

clean:
	@rm -rf pktanalyzer/*.class