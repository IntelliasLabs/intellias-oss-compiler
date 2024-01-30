package com.intellias.osm.model.common.wrapper

case class WayLineWrapper(wayLineId: Long, coordinates: Seq[OrderedCoordinateWrapper], tags: Map[String, String])
