package com.intellias.osm.compiler.display

import com.intellias.osm.common.StorageService
import com.intellias.osm.compiler.admin.AdminPlaceService
import com.intellias.osm.compiler.display.conf.DisplayPropertiesService
import com.intellias.osm.compiler.language.LanguageService
import com.intellias.osm.compiler.scale.ScaleService

trait DisplayEnvironment extends StorageService
  with ScaleService with DisplayPropertiesService with AdminPlaceService with LanguageService with Serializable
