package com.intellias.osm.model.road

case class Relation(relationId: Long,
                    relationType: String,
                    relationRole: String,
                    relationTags: Map[String, String])