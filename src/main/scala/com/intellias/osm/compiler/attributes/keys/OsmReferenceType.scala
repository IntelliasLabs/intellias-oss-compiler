package com.intellias.osm.compiler.attributes.keys

import com.intellias.osm.compiler.schema.model.common.ReferenceType

abstract class OsmReferenceType (val values: String, val refType: ReferenceType) {
  override def toString: String = values
}

object OsmReferenceType extends OsmCompositeKey[OsmReferenceType] {
  override val values: Seq[OsmReferenceType] = Seq(
    Reference,
    International,
    National,
    Regional,
    Local,
    UnSigned,
    Authority,
    Prow,
    Alternate
  )

  override def groupName: String = "referenceType"

  override def default: OsmReferenceType = Reference

  case object Reference extends OsmReferenceType("ref",  ReferenceType.Reference)
  case object International extends OsmReferenceType("int_ref",  ReferenceType.International)
  case object National extends OsmReferenceType("nat_ref",  ReferenceType.National)
  case object Regional extends OsmReferenceType("reg_ref",  ReferenceType.Regional)
  case object Local extends OsmReferenceType("loc_ref",  ReferenceType.Local)
  case object UnSigned extends OsmReferenceType("unsigned_ref",  ReferenceType.UnSigned)
  case object Authority extends OsmReferenceType("highway_authority_ref",  ReferenceType.Authority)
  case object Prow extends OsmReferenceType("prow_ref",  ReferenceType.Prow)
  case object Alternate extends OsmReferenceType("alt_ref",  ReferenceType.Alternate)
}