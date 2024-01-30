package com.intellias.osm

import com.intellias.osm.compiler.display.conf.DisplayFeatureConfig
import com.intellias.osm.compiler.language.LanguageConfig
import com.intellias.osm.compiler.scale.ScaleConfig
import org.apache.spark.storage.StorageLevel
import pureconfig._

import java.io.File
import scala.reflect.ClassTag

case class CommonConfig(tileLevel: Int = 13, defaultStorageLevel: String, tileList: Option[String], tileBoundingBoxes: Option[String]) {
  val sparkStorageLevel: StorageLevel = StorageLevel.fromString(defaultStorageLevel)
}

case class StorageAPI(typeApi: String, savePath: String, failurePath: String, credentialsPath: Option[String])
case class StorageConfig(api: Array[StorageAPI])

case class OsmConfig(osmInPath: String)

case class NdsLiveConfig(roadLayerFilePrefix: String = "RoadLayer_",
                         roadGeometryLayerPrefix: String = "RoadGeometryLayer_",
                         roadCharacteristicsLayerPrefix: String = "RoadCharacteristicsLayer_",
                         roadCharacteristicsMetadata: String = "RoadCharacteristicsMetadata",
                         regionRulesMetadata: String = "RegionRulesMetadata",
                         roadRulesLayerPrefix: String = "RoadRulesLayer_",
                         roadRulesLayerMeta: String = "RoadRulesLayerMeta",
                         roadStatistics: String = "RoadStatistics",
                         poiPrefix: String = "Poi_",
                         poiMetadata: String = "PoiMetadata",
                         poiAttrPrefix: String = "PoiAttribute_",
                         poiAttrMeta: String = "PoiAttributeMeta",
                         poiNamePrefix: String = "PoiName_",
                         poiNameMetadata: String = "PoiNameMetadata",
                         roadNamePrefix: String = "RoadName_",
                         roadNameMetadata: String = "RoadName_",
                         icon: String = "IconLayer",
                         iconMetadata: String = "IconMetadata",
                         displayPrefix: String = "Display_",
                         displayMetadata: String = "DisplayMeta",
                         displayAttrPrefix: String = "DisplayAttribute_",
                         displayAttrMetaMeta: String = "DisplayAttributeMeta",
                         displayNamePrefix: String = "DisplayName_",
                         displayNameMetadata: String = "DisplayNameMeta",
                        )


case class PoiConfig(categoryPath: String, outPath: String)

case class RoadConfig(outPath: String, statisticPath: String)

case class AdminHierarchyConfig(osmDisputedAreasMapping: String,
                                countriesHierarchyPath: String,
                                adminPlaceOut: String,
                                adminPlaceGroundOut: String)

case class AdminPlaceConfig(disputedViewPath: String,
                            adminPlaceOut: String,
                            adminPlaceGroundOut: String)

case class DisplayConfig(interimStoragePath: String)

case class DisplayWorkflowConfig(common: CommonConfig,
                                 osm: OsmConfig,
                                 adminPlace: AdminPlaceConfig,
                                 scaleConfig: ScaleConfig,
                                 displayFeatureConfig: DisplayFeatureConfig,
                                 ndsStorage: StorageConfig,
                                 nds: NdsLiveConfig,
                                 displayConfig: DisplayConfig,
                                 langConfig: LanguageConfig)


object AppConfig {
  def read[A](configFile: Option[String] = None)(implicit tt: ClassTag[A], reader: ConfigReader[A]): A = {
    configFile.map(new File(_)).map(ConfigSource.file).getOrElse(ConfigSource.default).loadOrThrow[A]
  }

  implicit val storageLevelConfigReader: ConfigReader[StorageLevel] =
    ConfigReader.fromStringOpt(level => Some(StorageLevel.fromString(level)))
}
