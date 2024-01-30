package com.intellias.osm.model.display

sealed abstract class DisplayFeatureType(val name: String)
object DisplayFeatureType {
  val areas: Set[DisplayFeatureType] = Set(BuildingFootprint, AreaIsland, AreaSeaOcean, AreaForestry, AreaUrban, DefaultDisplayArea)
  val lines: Set[DisplayFeatureType] = Set(LineRailway, LineRiver, DefaultDisplayLine)
  val points: Set[DisplayFeatureType] = Set(PointCityCenter, PointMountainPeak, DefaultDisplayPoint)
  val values: Set[DisplayFeatureType] = areas ++ lines ++ points + Unknown
  case object BuildingFootprint extends DisplayFeatureType("building")
  case object AreaIsland extends DisplayFeatureType("island")
  case object AreaSeaOcean extends DisplayFeatureType("sea-ocean")
  case object AreaForestry extends DisplayFeatureType("forestry")
  case object AreaUrban extends DisplayFeatureType("urban")
  case object DefaultDisplayArea extends DisplayFeatureType("default-display-area")

  case object LineRailway extends DisplayFeatureType("railway")
  case object LineRiver extends DisplayFeatureType("river")
  case object DefaultDisplayLine extends DisplayFeatureType("default-display-line")

  case object PointCityCenter extends DisplayFeatureType("city-center")
  case object PointMountainPeak extends DisplayFeatureType("mountain-peak")
  case object DefaultDisplayPoint extends DisplayFeatureType("default-display-point")

  case object Unknown extends DisplayFeatureType("unknown")

  def lookup(name: String): DisplayFeatureType = values.find(_.name.equalsIgnoreCase(name)).getOrElse(Unknown)
}