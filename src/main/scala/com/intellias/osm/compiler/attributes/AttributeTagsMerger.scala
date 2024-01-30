package com.intellias.osm.compiler.attributes

import com.intellias.osm.compiler.attributes.AttributeTagsMerger.MergedWith.tag
import com.intellias.osm.compiler.attributes.AttributeTagsMerger.MergedWithOps
import play.api.libs.json.{Format, Json}

class AttributeTagsMerger(mergerList: Seq[AttributeMergerLike]) extends Serializable {
  val mergers: Map[String, AttributeMergerLike] = mergerList.map(m => m.tag -> m).toMap

  def merge(from: Map[String, String], to: Map[String, String], fromId: String): Map[String, String] = {
    from
      .foldLeft(to) {
        case (acc, (k, v)) if mergers.contains(k) =>
          acc + (k -> acc.get(k).map(aJson => mergeAttributes(mergers(k), aJson, v)).getOrElse(v))
        case (acc, (k, v)) if !acc.contains(k) => acc + (k -> v)
        case (acc, _)                          => acc
      }
      .addMergedWith(fromId)
  }

  private def mergeAttributes(merger: AttributeMergerLike, jsonA: String, jsonB: String): String = {
    val merged = merger.merge(Json.parse(jsonA).as(merger.reads), Json.parse(jsonB).as(merger.reads))
    Json.toJson(merged)(merger.writes).toString()
  }
}

object AttributeTagsMerger {
  def apply(mergerList: AttributeMergerLike*): AttributeTagsMerger = new AttributeTagsMerger(mergerList)

  case class MergedWith(ids: Seq[String])

  object MergedWith {
    implicit val format: Format[MergedWith] = Json.format[MergedWith]
    val tag: String                         = "NDS:MergedWith"
  }

  implicit class MergedWithOps(tags: Map[String, String]) {
    def addMergedWith(fromId: String): Map[String, String] = {
      val newJson = tags.get(tag)
        .map(json => Json.parse(json).as[MergedWith])
        .map(mergedWith => mergedWith.copy(ids = mergedWith.ids :+ fromId))
        .getOrElse(MergedWith(Seq(fromId)))

      tags + (tag -> Json.toJson(newJson).toString())
    }
  }
}
