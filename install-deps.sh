# Build Shop jar from source
# then configure and run this to install to local maven repo

JAR_PATH=../../../shop/Shop/target/Shop-1.10.0.jar
POM_PATH=../../../shop/Shop/pom.xml
VERSION=1.10.0
GROUP_ID=com.snowgears.shop
ARTIFACT_ID=Shop
REPO_PATH=lib

mvn install:install-file \
	-Dfile=$JAR_PATH \
	-DgroupId=$GROUP_ID \
	-DartifactId=$ARTIFACT_ID \
	-Dversion=$VERSION \
	-Dpackaging=jar \
	-DlocalRepositoryPath=$REPO_PATH \
	-DpomFile=$POM_PATH
