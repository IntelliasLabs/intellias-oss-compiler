package com.intellias.osm.compiler.road.common.range

import com.intellias.osm.tools.JsonMapperProvider

@deprecated
trait RoadRangeTagBuilder extends JsonMapperProvider {
  val TagSuffixRange: String = ":Range"

  def createRangeTag(key: String, roadRanges: RoadRange): (String, String) =
    (key + TagSuffixRange) -> toJson(roadRanges)

  def createArrayRangeTag(key: String, ranges: Seq[RoadRange]): (String, String) =
    (key + TagSuffixRange) -> toJson(ranges.toArray)

  def toRangeExtractor(key: String): Map[String, String] => RoadRange = tags => {
    tags
      .get(key + TagSuffixRange)
      .map{rangeStr =>
        fromJson[RoadRange](rangeStr)
      }
      .getOrElse(RoadRangeComplete())
  }

  def toRangeArrayExtractor(key: String): Map[String, String] => Array[RoadRange] = tags =>
    tags
      .get(key + TagSuffixRange)
      .map { rangeStr =>
        fromJson[Array[RoadRange]](rangeStr)
      }
      .getOrElse(Array.empty[RoadRange])

}
