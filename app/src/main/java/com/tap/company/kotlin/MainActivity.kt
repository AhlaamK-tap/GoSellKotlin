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

        sdkSession.transactionMode = TransactionMode.PURCHASE



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
        if (charge.getAcquirer() != null) {
            println("Payment Succeeded : acquirer id : " + charge.getAcquirer()!!.getId())
            println("Payment Succeeded : acquirer response code : " + charge.getAcquirer()!!.getResponse().getCode())
            println("Payment Succeeded : acquirer response message: " + charge?.getAcquirer()!!.getResponse().getMessage())
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
        println("Payment Failed : " + charge?.description)
        println("Payment Failed : " + charge?.response?.message)

    }

    override fun authorizationSucceed(authorize: Authorize) {

    }

    override fun authorizationFailed(authorize: Authorize?) {

    }

    override fun cardSaved(charge: Charge) {

    }

    override fun cardSavingFailed(charge: Charge) {

    }

    override fun cardTokenizedSuccessfully(token: Token) {

    }

    override fun cardTokenizedSuccessfully(token: Token, saveCardEnabled: Boolean) {

    }

    override fun savedCardsList(cardsList: CardsList) {

    }

    override fun sdkError(goSellError: GoSellError?) {

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


    }

    override fun paymentInitiated(charge: Charge?) {


    }

    override fun googlePayFailed(error: String?) {

    }
}


private val customer: Customer
     get() {
        return Customer.CustomerBuilder(null).email("abc@abc.com").firstName("firstname")
            .lastName("lastname").metadata("").phone(PhoneNumber("965", "6509030"))
            .middleName("middlename").build()
    }

