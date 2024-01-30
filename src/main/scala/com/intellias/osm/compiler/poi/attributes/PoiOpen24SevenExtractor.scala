package com.intellias.osm.compiler.poi.attributes
import com.intellias.osm.compiler.attributes.OsmTagsOps._
import com.intellias.osm.model.poi.{Open24SevenFlag, POI}
import play.api.libs.json.Writes


object PoiOpen24SevenExtractor extends PoiAttributeExtractor[Open24SevenFlag] {
  val tag: String = "NDS:PoiOpen24/7"
  override implicit def writes: Writes[Seq[Open24SevenFlag]] =  Writes.seq

  def decodeFromOsm(poi: POI): Seq[Open24SevenFlag] = {
    if(poi.tags.tagValue("opening_hours", "24/7")) Seq(Open24SevenFlag()) else Seq.empty
  }
}
