# intellias-osm

### Tools

#### Osmium Tool
this tool can convert OSM files from one format into another (supports all XML and PBF formats and several more)
https://osmcode.org/osmium-tool/
SvallFor Windows Osmium can be launch using Conda

```shell
# convert osm to osm.pbf
osmium cat syhiv.osm -o syhiv.osm.pbf
```

#### OpenStreetMap Parquetizer
The project intends to provide a way to get the OpenStreetMap data available in a Big Data friendly format as Parquet.
for Mac M1 users the next versions have to be updated to:
```xml
<parquet.version>1.12.3</parquet.version>
<hadoop.version>3.3.5</hadoop.version>
```
```shell
git clone https://github.com/adrianulbona/osm-parquetizer.git
cd osm-parquetizer
mvn clean package
java -jar target/osm-parquetizer-1.0.1-SNAPSHOT.jar ~/intellias/osm-nds/osm-src-files/syhiv.osm.pbf
```

## How to run:

The main class is com.intellias.osm.Main. To run it locally in an IDE, you have to add this option to the JVM arguments.
```shell
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
Main accept application.conf as CLI argument 


## List of licenses
To get list of licenses run following command:
```shell
mvn site
```
the result will be under /${projectDir}/site/