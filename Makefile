SHELL=/bin/zsh
SRV_LOC=../../../_test_server/srv
PKG_NAME=ShopAddon
JAR=target/${PKG_NAME}*.jar
SRV_JAR=purpur-1.21.4-2394.jar

all: jar

jar: ${JAR}

${JAR}: $(shell find src -type f)
	mvn clean package

test: jar
	rm -rf ${SRV_LOC}/plugins/${PKG_NAME}*.jar && \
	cp -f target/${PKG_NAME}*.jar ${SRV_LOC}/plugins/ && \
	cd ${SRV_LOC} && \
	java -jar ${SRV_JAR}
clean:
	mvn clean