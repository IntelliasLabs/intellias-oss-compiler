package com.intellias.osm.compiler.poi.attributes.keys

import com.intellias.osm.compiler.attributes.keys.OsmCompositeKey
import com.intellias.osm.model.poi.PaymentName
import play.api.libs.json._

abstract class PaymentType(val values: Set[String]) {
  override def toString: String = values.mkString("|")
  def names: Seq[PaymentName]
}

//TODO: complete list of payments => https://wiki.openstreetmap.org/wiki/Key:payment:*#Electronic_Purses_.2F_Intersector_Electronic_Purses_.28IEP.29_.2F_Stored-value_cards
case object PaymentType extends OsmCompositeKey[PaymentType] {
  override val values: Seq[PaymentType] = Seq(
    Cash,
    Coins,
    Notes,
    Cheque,
    CreditCard,
    DebitCard,
    Contactless,
    MasterCard,
    Visa,
    AmericanExpress,
    Paypass,
    Paywave,
    LocalPayment("")
  )

  override def groupName: String = "paymentType"

  override def default: PaymentType = LocalPayment("")

  override def apply(value: String): PaymentType = {
    values.find {
      _.values(value)
    }.getOrElse(
      LocalPayment(value)
    )
  }

  case class LocalPayment(localPaymentName: String) extends PaymentType(Set("[^:]+")) {
    override def names: Seq[PaymentName] = Seq.empty
  }

  object Cash extends PaymentType(Set("cash")) {
    override def names: Seq[PaymentName] = Seq(
      PaymentName("Cash", "eng"),
      PaymentName("Готівка", "ukr")
    )
  }

  case object Coins extends PaymentType(Set("coins")) {
    override def names: Seq[PaymentName] = Seq(PaymentName("Coins", "eng"))
  }

  case object Notes extends PaymentType(Set("notes")) {
    override def names: Seq[PaymentName] = Seq(PaymentName("Notes", "eng"))
  }

  case object Cheque extends PaymentType(Set("cheque")) {
    override def names: Seq[PaymentName] = Seq(PaymentName("Cheque", "eng"))
  }

  case object CreditCard extends PaymentType(Set("credit_cards")) {
    override def names: Seq[PaymentName] = Seq(
      PaymentName("Credit card", "eng"),
      PaymentName("Кредитна картка", "ukr"),
    )
  }

  case object DebitCard extends PaymentType(Set("debit_cards")) {
    override def names: Seq[PaymentName] = Seq(PaymentName("Debit card", "eng"))
  }

  case object Contactless extends PaymentType(Set("contactless")) {
    override def names: Seq[PaymentName] = Seq(
      PaymentName("Contactless", "eng"),
      PaymentName("Без контактна оплата", "ukr"),
    )
  }

  case object MasterCard extends PaymentType(Set("mastercard")) {
    override def names: Seq[PaymentName] = Seq(PaymentName("MasterCard", "eng"))
  }

  case object Visa extends PaymentType(Set("visa")) {
    override def names: Seq[PaymentName] = Seq(PaymentName("Visa", "eng"))
  }

  case object AmericanExpress extends PaymentType(Set("american_express")) {
    override def names: Seq[PaymentName] = Seq(PaymentName("AmericanExpress", "eng"))
  }

  case object Paypass extends PaymentType(Set("mastercard_contactless", "paypass", "mastercard_paypass")) {
    override def names: Seq[PaymentName] = Seq(PaymentName("Paypass", "eng"))
  }

  case object Paywave extends PaymentType(Set("visa_contactless", "visa_paywave", "paywave")) {
    override def names: Seq[PaymentName] = Seq(PaymentName("Paywave", "eng"))
  }

  implicit val osmPaymentTypeWrites: Writes[PaymentType] = Writes[PaymentType] {
    case p: PaymentType.LocalPayment => Json.obj(
      "type" -> "LocalPayment",
      "localPaymentName" -> p.localPaymentName
    )
    case p: PaymentType => Json.obj(
      "type" -> p.getClass.getSimpleName
    )
  }

  implicit val osmPaymentTypeReads: Reads[PaymentType] = Reads[PaymentType] { json =>
    val paymentType = (json \ "type").as[String]
    paymentType match {
      case "LocalPayment" =>
        JsSuccess(PaymentType.LocalPayment((json \ "localPaymentName").as[String]))
      case _ =>
        PaymentType.values.find(_.getClass.getSimpleName == paymentType) match {
          case Some(paymentType) => JsSuccess(paymentType)
          case None => JsError(s"Unknown payment type: $paymentType")
        }
    }
  }
}
