package com.intellias.osm.ndslive.display

import com.intellias.osm.model.display.DisplayFeatureType
import com.intellias.osm.model.display.DisplayFeatureType._
import nds.display.types.{DisplayAreaType => NdsAreaType, DisplayLineType => NdsLineType, DisplayPointType => NdsPointType}

object NdsDisplayTypeMap {
  val AreaTypeMap: Map[DisplayFeatureType, NdsAreaType] = Map(
    BuildingFootprint -> NdsAreaType.AREA_BUILDING,
    AreaIsland -> NdsAreaType.AREA_ISLAND,
    AreaSeaOcean -> NdsAreaType.AREA_SEA_OCEAN,
    AreaForestry -> NdsAreaType.AREA_FOREST,
    AreaUrban -> NdsAreaType.AREA_URBAN,
    DefaultDisplayArea -> NdsAreaType.DISPLAY_AREA
  )
  val LineTypeMap: Map[DisplayFeatureType, NdsLineType] = Map(
    LineRailway -> NdsLineType.LINE_RAILWAY,
    LineRiver -> NdsLineType.LINE_RIVER,
    DefaultDisplayLine -> NdsLineType.DISPLAY_LINE
  )
  val PointTypeMap: Map[DisplayFeatureType, NdsPointType] = Map(
    PointCityCenter -> NdsPointType.POINT_MUNICIPALITY_CENTER,
    PointMountainPeak -> NdsPointType.POINT_MOUNTAIN_PEAK,
    DefaultDisplayPoint -> NdsPointType.DISPLAY_POINT
  )
}
