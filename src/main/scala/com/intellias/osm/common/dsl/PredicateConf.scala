package com.intellias.osm.common.dsl

sealed trait PredicateConf
case class OsmSimpleConf(tags: Tags) extends PredicateConf
case class OsmExpressionConf(expr: String) extends PredicateConf