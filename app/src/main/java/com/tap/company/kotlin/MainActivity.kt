package com.tap.company.kotlin

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import company.tap.gosellapi.GoSellSDK
import company.tap.gosellapi.internal.api.callbacks.GoSellError
import company.tap.gosellapi.internal.api.models.Authorize
import company.tap.gosellapi.internal.api.models.Charge
import company.tap.gosellapi.internal.api.models.PhoneNumber
import company.tap.gosellapi.internal.api.models.SaveCard
import company.tap.gosellapi.internal.api.models.Token
import company.tap.gosellapi.open.buttons.PayButtonView
import company.tap.gosellapi.open.controllers.SDKSession
import company.tap.gosellapi.open.controllers.ThemeObject
import company.tap.gosellapi.open.delegate.SessionDelegate
import company.tap.gosellapi.open.enums.TransactionMode
import company.tap.gosellapi.open.models.CardsList
import company.tap.gosellapi.open.models.Customer
import company.tap.gosellapi.open.models.TapCurrency
import java.math.BigDecimal

class MainActivity : AppCompatActivity() , SessionDelegate {

    var sdkSession :SDKSession =SDKSession()
   lateinit var payButtonView :PayButtonView
    private val SDK_REQUEST_CODE = 1001
    private  val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startSDK()
    }

    private fun startSDK() {
        /**
         * Required step.
         * start the sdk.
         */
        configureApp()


        /**
         * Required step.
         * Configure SDK Session with all required data.
         */
        configureSDKSession()


        /**
         * If you included Tap Pay Button then configure it first, if not then ignore this step.
         */
        initPayButton()
    }

    private fun initPayButton() {
        payButtonView = findViewById(R.id.payButtonId)

        payButtonView.setupFontTypeFace(ThemeObject.getInstance().payButtonFont)

        payButtonView.payButton.textSize = ThemeObject.getInstance().payButtonTextSize.toFloat()
        //
        payButtonView.securityIconView.visibility = View.VISIBLE

        payButtonView.setBackgroundSelector(ThemeObject.getInstance().payButtonResourceId)

        sdkSession.setButtonView(payButtonView, this, SDK_REQUEST_CODE)
    }

    private fun configureSDKSession() {
        if (sdkSession == null) sdkSession = SDKSession() //** Required **

        // pass your activity as a session delegate to listen to SDK internal payment process follow
        sdkSession.addSessionDelegate(this) //** Required **

        // initiate PaymentDataSource
        sdkSession.instantiatePaymentDataSource() //** Required **

        // set transaction currency associated to your account
        sdkSession.setTransactionCurrency(TapCurrency("KWD")) //** Required **

        // Using static CustomerBuilder method available inside TAP Customer Class you can populate TAP Customer object and pass it to SDK
        sdkSession.setCustomer(customer) //** Required **

        // Set Total Amount. The Total amount will be recalculated according to provided Taxes and Shipping
        sdkSession.setAmount(BigDecimal(1)) //** Required **

        // Enable or Disable Saving Card
        sdkSession.isUserAllowedToSaveCard(true) //  ** Required ** you can pass boolean

        // Enable or Disable 3DSecure
        sdkSession.isRequires3DSecure(true)

        sdkSession.transactionMode = TransactionMode.SAVE_CARD



    }

    /**
     * Required step.
     * Configure SDK with your Secret API key and App Bundle name registered with tap company.
     */
    private fun configureApp(){
        GoSellSDK.init(this@MainActivity, "sk_test_kovrMB0mupFJXfNZWx6Etg5y","company.tap.goSellSDKExample")  // to be replaced by merchant, you can contact tap support team to get you credentials
        GoSellSDK.setLocale("en")//  if you dont pass locale then default locale EN will be used
    }


    override fun paymentSucceed(charge: Charge) {
        println("Payment Succeeded : charge status : " + charge.status)
        println("Payment Succeeded : description : " + charge.description)
        println("Payment Succeeded : message : " + charge.response.message)
        println("##############################################################################")
        if (charge.card != null) {
            println("Payment Succeeded : first six : " + charge.card?.firstSix)
            println("Payment Succeeded : last four: " + charge.card?.last4)
            println("Payment Succeeded : card object : " + charge.card?.getObject())
            println("Payment Succeeded : brand : " + charge.card?.brand)
        }

        println("##############################################################################");
        if (charge.acquirer != null) {
            println("Payment Succeeded : acquirer id : " + charge.acquirer!!.getId())
            println("Payment Succeeded : acquirer response code : " + charge.acquirer!!.getResponse().getCode())
            println("Payment Succeeded : acquirer response message: " + charge?.acquirer!!.getResponse().getMessage())
        }
        println("##############################################################################")
        if (charge.source != null) {
            println("Payment Succeeded : source id: " + charge.source.id);
            println("Payment Succeeded : source channel: " + charge.source.channel);
            println("Payment Succeeded : source object: " + charge.source.getObject())
            println("Payment Succeeded : source payment method: " + charge.source.paymentMethodStringValue)
            println("Payment Succeeded : source payment type: " + charge.source.paymentType)
            println("Payment Succeeded : source type: " + charge.source.type)
        }
        println("##############################################################################")
       

        println("##############################################################################")
        if (charge.expiry != null) {
            println("Payment Succeeded : expiry type :" + charge?.expiry!!.getType())
            println("Payment Succeeded : expiry period :" + charge?.expiry!!.getPeriod())
        }
       
      
    }

    override fun paymentFailed(charge: Charge?) {
        println("Payment Failed : " + charge?.status)
        println("Payment Failed : " + charge?.id)
        println("Payment Failed : " + charge?.description)
        println("Payment Failed : " + charge?.response?.message)

    }

    override fun authorizationSucceed(authorize: Authorize) {
        println("Authorize Succeeded : " + authorize.status)
        println("Authorize Succeeded : " + authorize.response.message)

        if (authorize.card != null) {
            println("Payment Authorized Succeeded : first six : " + authorize.card!!.firstSix)
            println("Payment Authorized Succeeded : last four: " + authorize.card!!.last4)
            println("Payment Authorized Succeeded : card object : " + authorize.card!!.getObject())
        }

        println("##############################################################################")
        if (authorize.acquirer != null) {
            println("Payment Authorized Succeeded : acquirer id : " + authorize.acquirer!!.id)
            println(
                "Payment Authorized Succeeded : acquirer response code : " + authorize.acquirer!!
                    .response.code
            )
            println(
                "Payment Authorized Succeeded : acquirer response message: " + authorize.acquirer!!
                    .response.message
            )
        }
        println("##############################################################################")
        if (authorize.source != null) {
            println("Payment Authorized Succeeded : source id: " + authorize.source.id)
            println("Payment Authorized Succeeded : source channel: " + authorize.source.channel)
            println("Payment Authorized Succeeded : source object: " + authorize.source.getObject())
            println("Payment Authorized Succeeded : source payment method: " + authorize.source.paymentMethodStringValue)
            println("Payment Authorized Succeeded : source payment type: " + authorize.source.paymentType)
            println("Payment Authorized Succeeded : source type: " + authorize.source.type)
        }

        println("##############################################################################")
        if (authorize.expiry != null) {
            println("Payment Authorized Succeeded : expiry type :" + authorize.expiry!!.type)
            println("Payment Authorized Succeeded : expiry period :" + authorize.expiry!!.period)
        }
    }

    override fun authorizationFailed(authorize: Authorize?) {
        println("Authorize Failed : " + authorize?.status)
        println("Authorize Failed : " + authorize?.description)
        println("Authorize Failed : " + authorize?.response?.message)
    }

    override fun cardSaved(charge: Charge) {

        // Cast charge object to SaveCard first to get all the Card info.
        if (charge is SaveCard) {
            println(
                "Card Saved Succeeded : first six digits : " + (charge as SaveCard).card!!
                    .firstSix + "  last four :" + (charge as SaveCard).card!!.last4
            )

            println("Card Saved Succeeded : " + charge.status)
            println("Card Saved Succeeded : " + charge.card!!.brand)
            println("Card Saved Succeeded : " + charge.description)
            println("Card Saved Succeeded : " + charge.response.message)
          //  println("Card Saved Succeeded ID: " + (charge as SaveCard).paymentAgreement?.id)
          //  println("Card Saved Succeeded ID: " + (charge as SaveCard).paymentAgreement?.contract?.id)
        }
    }

    override fun cardSavingFailed(charge: Charge) {
        println("Card Saved Failed : " + charge.status)
        println("Card Saved Failed : " + charge.description)
        println("Card Saved Failed : " + charge.response.message)
    }

    override fun cardTokenizedSuccessfully(token: Token) {
        println("Card Tokenized Succeeded : ")
        println("Tokenized Response : $token")
        println("Card Tokenized Response : " + token.card)

        println("Token card : " + token.card.firstSix + " **** " + token.card.lastFour)
        println("Token card : " + token.card.fingerprint + " **** " + token.card.funding)
        println("Token card : " + token.card.id + " ****** " + token.card.name)
        println("Token card : " + token.card.address + " ****** " + token.card.getObject())
        println("Token card : " + token.card.expirationMonth + " ****** " + token.card.expirationYear)
        println("Token card brand : " + token.card.brand)
    }

    override fun cardTokenizedSuccessfully(token: Token, saveCardEnabled: Boolean) {
        println("userEnabledSaveCardOption :  $saveCardEnabled")
        println("cardTokenizedSuccessfully Succeeded : ")
        println("Token card : " + token.card.firstSix + " **** " + token.card.lastFour)
        println("Token card : " + token.card.fingerprint + " **** " + token.card.funding)
        println("Token card : " + token.card.id + " ****** " + token.card.name)
        println("Token card : " + token.card.address + " ****** " + token.card.getObject())

    }

    override fun savedCardsList(cardsList: CardsList) {
        if (cardsList != null && cardsList.cards != null) {
            println(" Card List Response Code : " + cardsList.responseCode)
            println(" Card List Top 10 : " + cardsList.cards.size)
            println(" Card List Has More : " + cardsList.isHas_more)
            println("----------------------------------------------")
            for (sc in cardsList.cards) {
                println(sc.brandName)
            }
            println("----------------------------------------------")

        }
    }

    override fun sdkError(goSellError: GoSellError?) {
        if (goSellError != null) {
            println("SDK Process Error : " + goSellError.errorBody)
            println("SDK Process Error : " + goSellError.errorMessage)
            println("SDK Process Error : " + goSellError.errorCode)

        }
    }

    override fun sessionIsStarting() {
        Log.e(TAG, "sessionIsStarting: " )
    }

    override fun sessionHasStarted() {
        Log.e(TAG, "sessionHasStarted: " )
    }

    override fun sessionCancelled() {
        Log.e(TAG, "sessionCancelled: " )
    }

    override fun sessionFailedToStart() {
        Log.e(TAG, "sessionFailedToStart: " )
    }

    override fun invalidCardDetails() {
        Log.e(TAG, "invalidCardDetails: " )
    }

    override fun backendUnknownError(message: String?) {
        Log.e(TAG, "backendUnknownError: " )
    }

    override fun invalidTransactionMode() {
        Log.e(TAG, "invalidTransactionMode: " )

    }

    override fun invalidCustomerID() {
        Log.e(TAG, "invalidCustomerID: " )

    }

    override fun userEnabledSaveCardOption(saveCardEnabled: Boolean) {
        Log.e(TAG, "userEnabledSaveCardOption: "+saveCardEnabled )

    }

    override fun asyncPaymentStarted(charge: Charge) {
        println("asyncPaymentStarted :  ")
        println("Charge id:" + charge.id)
        println("Fawry reference:" + charge.transaction.order?.reference)
    }


     override fun paymentInitiated(charge: Charge?) {
        println("paymentInitiated CallBack :  ")
        println("Charge id:" + charge?.id)
        println("charge status:" + charge?.status)

    }

  /*  override fun googlePayFailed(error: String?) {

    }*/
}


private val customer: Customer
     get() {
        return Customer.CustomerBuilder(null).email("abc@abc.com").firstName("firstname")
            .lastName("lastname").metadata("").phone(PhoneNumber("965", "6509030"))
            .middleName("middlename").build()
    }

