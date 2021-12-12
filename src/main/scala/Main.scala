import saga2.*
import scala.collection.immutable.Stream.Cons

//DOMAIN MODELS -- START
case class Consent(provided: Boolean)
case class CreditScore(value: Double)
case class CustomerId(id: String)
case class MobileNumber(value: String)
case class CustomerProfile(
    id: CustomerId,
    name: String,
    pan: String,
    creditScore: CreditScore
)
case class Offer(creditLimit: Double, benefits: List[String])
case class DisbursalNumber(value: String)
//DOMAIN MODELS -- END

//DOMAIN SERVICES / AGGREGATES -- START
def isPreApproved(mobileNumber: MobileNumber): Boolean = ???
def getCustomerProfile(
    mobileNumber: MobileNumber
): Either[CustomerProfileFetchError, CustomerProfile] = ???
def getCustomerOffer(customerId: CustomerId): Either[NoOffer, Offer] = ???
def getCreditScore(
    mobileNumber: MobileNumber
): Either[CouldNotFetchCreditScore, CreditScore] = ???
def createOffer(
    creditScore: CreditScore
): Either[InsufficientCreditScore, Offer] = ???
def getConsent(offer: Offer): Either[ConsentNotProvided, Consent] = ???
def disburse(
    offer: Offer
): Either[DisbursalError, DisbursalNumber] = ???
//DOMAIN SERVICES / AGGREGATES -- END

//ERRORs -- START
case class CustomerProfileFetchError(msg: String)
case class DisbursalError(msg: String)
case class NoOffer()
case class CouldNotFetchCreditScore(msg: String)
case class InsufficientCreditScore()
case class ConsentNotProvided()
//ERRORs -- END

@main def hello: Unit =
  val paSaga =
    Saga
      .fromFunction(getCustomerProfile)
      .andThen(Saga.fromFunction(profile => getCustomerOffer(profile.id)))

  val npaSaga =
    Saga
      .fromFunction(getCreditScore)
      .andThen(Saga.fromFunction(createOffer))

  val consentSaga =
    Saga
      .fromFunction(getConsent)
      .andThen(Saga.fromFunction(disburse))      
  
  val saga: Saga[
        MobileNumber, 
        
        CustomerProfileFetchError | NoOffer | ConsentNotProvided | CouldNotFetchCreditScore | InsufficientCreditScore | DisbursalError, 
        
        MobileNumber & CustomerProfile & Offer & Consent & DisbursalNumber |  // PA - Disbursed
        MobileNumber & CreditScore & Offer & Consent & DisbursalNumber // NPA - Disbursed
    ] = Saga
    .requires[MobileNumber]
    .decide(isPreApproved)(paSaga, npaSaga)
    .andThen(consentSaga)
