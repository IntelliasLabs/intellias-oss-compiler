package com.intellias.osm.ndslive.name

import nds.core.attributemap.{Condition, ConditionList, Validity}

trait AttributeMapBuilder extends Serializable {
  def emptyCondition: ConditionList = new ConditionList(0.toShort, Array.empty[Condition])

  def fillValidities(size: Int): Array[Validity] = Array.fill(size)(new Validity(0.toByte))

}
