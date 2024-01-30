package com.intellias.osm.compiler.display.conf

import com.intellias.osm.AppConfig
import com.intellias.osm.compiler.display.conf.DisplayPropertiesService.Service
import pureconfig.generic.auto._

trait DisplayPropertiesService {
  val displayPropertiesService: Service
}

object DisplayPropertiesService {
  trait Service extends Serializable {
    def displayPropertiesList: DisplayFeaturePropertiesList
  }

  def apply(config: DisplayFeatureConfig): DisplayPropertiesService = new DisplayPropertiesService {
    override val displayPropertiesService: Service = DisplayPropertiesServiceImpl(config)
  }
}

class DisplayPropertiesServiceImpl(featurePropertiesList: DisplayFeaturePropertiesList) extends Service {
  override def displayPropertiesList: DisplayFeaturePropertiesList = featurePropertiesList
}

object DisplayPropertiesServiceImpl {
  def apply(displayFeatureConf: DisplayFeatureConfig): DisplayPropertiesServiceImpl = {
    new DisplayPropertiesServiceImpl(
      AppConfig.read[DisplayFeaturePropertiesList](Some(displayFeatureConf.configPath))
    )
  }
}