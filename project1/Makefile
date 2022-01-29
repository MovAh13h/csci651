.PHONY: build run

run: clean build
	@java pktanalyzer/pktanalyzer pkt/new_icmp_packet2.bin

build:
	@javac pktanalyzer/pktanalyzer.java

clean:
	@rm -rf pktanalyzer/*.class