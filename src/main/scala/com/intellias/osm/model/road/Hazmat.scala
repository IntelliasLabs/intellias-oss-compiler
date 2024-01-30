package com.intellias.osm.model.road

import com.intellias.osm.compiler.road.rules.values.HazmatType
import com.intellias.osm.model.common.DirectionType

case class Hazmat(value: String, hazmatType: HazmatType, condition: Option[String], direction: DirectionType) {}
