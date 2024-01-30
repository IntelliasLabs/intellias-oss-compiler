package com.intellias.osm.model.admin

import play.api.libs.json.{Format, Json}

case class AdminTypeWrapper(typeStr: String) {
  def adminType: AdminType = AdminTypeWrapper.toAdminType(typeStr)
}

object AdminTypeWrapper {
  def apply(adminType: AdminType): AdminTypeWrapper = AdminTypeWrapper(adminType.getClass.getSimpleName.replace("$", ""))
  def toAdminType(typeStr: String): AdminType =
    AdminType.values.find(_.getClass.getSimpleName.replace("$", "") == typeStr) match {
      case Some(at) => at
      case None     => throw new IllegalArgumentException(s"Unknown AdminType string representation: $typeStr")
    }

  implicit val adminPlaceFormat: Format[AdminTypeWrapper] = Json.format[AdminTypeWrapper]
}
