# Build Shop from source
# then configure and run this to install to local maven repo

JAR_PATH=../Shop/target/Shop-1.11.3.jar
VERSION=1.11.3
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
	-DgeneratePom=true
