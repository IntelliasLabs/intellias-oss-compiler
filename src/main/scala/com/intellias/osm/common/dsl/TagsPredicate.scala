package com.intellias.osm.common.dsl

trait TagsPredicate extends Serializable {
  def isMatched(tags: Tags): Boolean
}

object TagsPredicate {
  def apply(conf: PredicateConf): TagsPredicate = conf match {
    case OsmSimpleConf(tags) => OsmSimple(tags)
    case OsmExpressionConf(expression) => OsmExpressionPredicate(TagsCondition.Parser(expression))
    case _ => throw new IllegalArgumentException(s"unsupported predicate configuration: ${conf.getClass.getName}")
  }
}

case class OsmSimple(lookupTags: Tags) extends TagsPredicate {
  override def isMatched(tags: Tags): Boolean = lookupTags.exists {
    case (k, v) =>
      tags.exists {
        case (k2, v2) => k == k2 && (v == v2 || v.isBlank)
      }
  }
}

case class OsmExpressionPredicate(condition: TagsCondition.Condition) extends TagsPredicate {
  override def isMatched(tags: Tags): Boolean = condition.evaluate(tags)
}