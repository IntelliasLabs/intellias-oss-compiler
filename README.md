# Intellias OSM-to-NDS.Live Compiler

This project aims to provide a compiler from [Open Street Map (OSM)](https://www.openstreetmap.org) data into [NDS.Live](https://nds.live/) - a modern format for navigational
map services. Compiler is created by Intellias and distributed as an open source software to help you get familiar with
both OSM and NDS.Live map data format.

To get a list of licenses for the libraries that project uses as dependencies execute:
```shell
mvn site
```
in the Project's root directory and collect the results in /${projectDir}/site/ directory.

<br/>

## Setup
You can either set up an execution environment from scratch by installing and configuring all the required tools by
yourself or utilize a pre-built Docker image.

### Setup from scratch
#### Required software
You will need the following tooling to be installed on your machine:
* JDK 17
* Scala 2.13
* Maven 3.x
#### Maven configuration
Compiler relies on some Intellias libraries that simplify working with map features geometries, coordinates conversion,
tiling etc. To be able to access these libraries, the Maven installation should be configured. Just add the below `server`
configuration to the `servers` section in your `~/.m2/settings.xml` Maven configuration file:
```xml
<server>
  <id>intellias-oss-maven</id>
  <configuration>
	<httpHeaders>
	  <property>
		<name>Private-Token</name>
		<value>...</value>
	  </property>
	</httpHeaders>
  </configuration>
</server>
```

> [!IMPORTANT]
> Access token needs to be requested from Intellias

When the above requirements are met, the Compiler sources can be downloaded from GitHub:
```shell
git clone https://github.com/IntelliasLabs/mapping-and-location.git
```
and the project can be built by executing
```shell
mvn clean install
```
<br/>

### Docker image utilization
#### Required software
Only Docker installation is required.

To pull the Compiler Docker image, you need to authorize to registry.gitlab.com.
```shell
docker login registry.gitlab.com -u oss-packages
```

> [!IMPORTANT]
> Credentials need to be requested from Intellias

> [!NOTE]
> In case if you've already requested an access token for Maven configuration, you can re-use it for pulling Docker images

<br/>

## Input Data Preparation

#### OSM Source Data
While OSM limits scope of the data that can be directly exported from their website, they encourage to download map
data dumps refreshed on regular basis and partitioned by regions, countries and sub-countries (if applicable).
So navigate to the [OSM Data Extracts](https://download.geofabrik.de/) web page and download the data you're interested in.

To be fed to the Compiler, OSM data downloaded in Protobuf format need to be pre-processed. This is where **OSM Parquetizer**
tool may come in handy as it brings OSM data exactly in format expected by the Compiler - converted to Parquet format
and split by OSM feature types, i.e. nodes, ways and relations. Just clone the [OSM Parquetizer Repo](https://github.com/adrianulbona/osm-parquetizer.git)
and follow instructions there to prepare the downloaded OSM data.

<br/>

#### Administrative Hierarchies Data
Compilation requires pre-processed Administrative Hierarchies data to be provided. This data comprises information about
administrative areas of different levels along with the boundaries built from the OSM data.
Intellias provides this data in Parquet format for two countries - Luxembourg and Malta (see the `admin-hierarchy` directory).
If you need to build it for other countries - contact Intellias for assistance.

<br/>

## Workflow Execution

### Run custom build
After project is built with Maven, you can either import it into your favourite IDE and set up a run configuration there
or run it directly from the command line. The main class and an entry point for compilation is `com.intellias.osm.Main`.

Before you run the compilation workflows, you need to amend the `application.conf` file which is available in
`src/main/resources/` directory. There a path to the directory where 'parquetized' OSM data is stored needs to be provided,
as well as a path to the output directory where compiled data will be stored. It may be convenient to have several different
versions of the `application.conf` with distinct configurations. You can instruct the Compiler which one to take by the JVM flag:
```
-Dconfig.resource=my-application.conf
```

When running from an IDE, you need to provide the below additional JVM flags to address problems that Apache Spark has while running
with Java 17:
```
--add-exports=java.base/sun.nio.ch=ALL-UNNAMED
--add-exports=java.base/java.nio=ALL-UNNAMED
--add-opens=java.base/java.lang=ALL-UNNAMED
--add-opens=java.base/java.nio=ALL-UNNAMED
--add-opens=java.base/java.util=ALL-UNNAMED
--add-opens=java.base/java.lang.reflect=ALL-UNNAMED
--add-opens=java.base/java.lang.invoke=ALL-UNNAMED
--add-opens=java.base/sun.security.action=ALL-UNNAMED
--add-opens=java.base/java.io=ALL-UNNAMED
--add-exports=jdk.unsupported/sun.misc=ALL-UNNAMED
```
These flags are not needed when you run compilation using JAR with dependencies as they are already included into the MANIFEST file.

So running a compilation with the JAR file is as simple as:
```shell
# specify the correct path to the edited 'application.conf' file
# Java max heap size is about to be changed depending on you compilation needs
java -Dconfig.file=my-application.conf -Xmx16G -jar osm-nds-live-compiler-1.0-SNAPSHOT-jar-with-dependencies.jar
```

> [!NOTE]
> When compilation is started you can use Spark console to track the progress. By default, it is available at port 4040. So if you run a compilation on your local machine, just navigate to `localhost:4040` in your browser.

> [!NOTE]
> List of the workflows to be executed can be found in `com.intellias.osm.Main` class. You can keep only some of those to speed up the compilation and save computational resources.


### Run Docker container
Edit `docker-compose-compiler.yml` file: you need to specify paths to the directories on your machine where the pre-processed
OSM data and pre-compiled administrative hierarchies are stored as well as a path to the directory with
compilation results.

After that, just execute
```shell
docker-compose -f docker-compose-compiler.yml up
```
and compilation will start. 

You can still browse the Spark console by navigating to `localhost:4040` in a browser on your local machine.

<br/>

## Results inspection
NDS.Live standardize the way how compiled map data should be accessed. For this it declares so called 'Smart Layer' services -
Web services that expose the NDS.Live map data.

Intellias offers a Smart Layer Tile server Docker image that you can use to access and expose the compiled data.

In case if you haven't yet authorized into the Intellias Container Registry, please refer the `Docker image utilization`
section to see how to do this.

When authorized, you can start the dockerized Smart Layer Tile service with a docker-compose command, but before some
amendments to the `docker-compose-smart-service.yml` need to be done. The only thing that needs to be provided there - is
the path to a directory where compiled NDS.Live map data is stored (see comments inside the docker-compose file). Optionally
you can also disable some of the compiled layers, change port to which the RESTful service is mapped etc.

When the mentioned modifications to the docker-compose file are done, you can start the service by executing:
```shell
docker-compose -f docker-compose-smart-service.yml up
```

To persuade that service is up and running, open your browser and navigate to `http://localhost:8080/swagger-ui/index.html` - 
you should be able to see the Swagger UI page with the list of endpoints exposed by the RESTful service.

With the Smart Layer Tile service running, you can use tooling provided by NDS Association to work with the compiled map
data. For example, you may use [Live.Lab](https://developer.nds.live/tools/livelab) tool to inspect the compiled data and 
[Map Viewer](https://developer.nds.live/tools/mapviewer) to render it, or you can validate and certify the compiled data 
with [Certification Kit](https://developer.nds.live/certification-and-validation).