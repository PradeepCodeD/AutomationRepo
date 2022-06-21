/*
1. Subscribe to a Standard fund from RM
2. Check orders tab for the details given during subscription
3. Approve from internal the subscription
4. Check the orders tab after subscription and check if the values are correct
5. Check kristals tab after approval
6. Check if Contract notes are generated
7. Unsubscribe for all units of subscribed units

 */
package com.kristal.api.workflows.PooledSubscriptions;

import com.kristal.core.api.BaseClass;
import com.kristal.core.api.modules.DashboardAPIs;
import com.kristal.core.api.modules.RMAppSubscriptionAPIs;
import com.kristal.core.api.modules.ResponsibleOfficer;
import com.kristal.core.api.modules.SignupAndLogin;
import com.kristal.core.utils.*;
import com.kristal.core.utils.Enums.AccountTypes;
import com.kristal.core.utils.Enums.KristalTypes;
import com.kristal.core.utils.Enums.SearchFilterNames;
import com.kristal.core.utils.response_POJO.AccountDetailsBean;
import com.relevantcodes.extentreports.LogStatus;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Buy_Sell_Std_Fund_RM extends BaseClass {

    private String ip;
    private String token;
    private String kristalId;
    private String email;
    private String password;
    private String roEmail;
    private String tokenRO;
    private String roPassword;
    private Response resp,placementFeeResp;
    private Float noOfUnits;
    private Double amountToSubscribe;
    private double unitNav;
    private String accountId;
    private Float placementFeePercent;
    private Float placementFeeAbsolute;
    private float investmentAmount;
    private float amountCreditedToBank;
    private  float purchasePrice;
    private String emailRM;
    private String passwordRM;
    private String rmToken;
    private List<Integer> tempGoalIdList;
    private int tempGoalId;
    private String tempStringStore;
    private List<String> tempStringListStore;
    public List<Float> tempFloatListStore;
    public float tempFloatStore;
    public List<Integer> tempIntListStore;
    public int tempIntStore;
  //  private Double tempDoubleStore;
  //  private List<Double> tempDoubleListStore;
    private final PooledSubscripiton pooled = new PooledSubscripiton();
    private String OTP;
    private final Map<String, Object> portalInputMap = new HashMap<>();
    private final RMSubscriptions rmSubs = new RMSubscriptions();
    private final APISpecificUtils apiSpecificUtils = new APISpecificUtils();
    private final DashboardAPIs dashboardAPIs = new DashboardAPIs();
    private final ResponsibleOfficer ro = new ResponsibleOfficer();
    private final SignupAndLogin signupAndLogin = new SignupAndLogin();
    private final Map<String, Object> inputMap = new HashMap<>();
    private final RMAppSubscriptionAPIs rmAppSubs= new RMAppSubscriptionAPIs();

    public void initData() {
        ip = CommonUtils.getPublicIp();
        kristalId = methodTestData.getAsJsonObject().get("kristalId").getAsString();
        if (isProduction) {
            email = "vineeth+2@kristal.ai";
            password = new Base64Converter().encodeStringToBase64("Admin@123");
        } else {
            email = methodTestData.getAsJsonObject().get("emailId").getAsString();
            password = new Base64Converter().encodeStringToBase64(methodTestData.getAsJsonObject().get("password").getAsString());
            roEmail= configData.get("RO_USER");
            roPassword=new Base64Converter().encodeStringToBase64(configData.get("RO_PASSWORD"));
            emailRM = configData.get("HHRmEmailId");
            OTP = configData.get("HHRmOTP");
            passwordRM = new Base64Converter().encodeStringToBase64(configData.get("HHRmPassword"));
        }
    }

    @Test(groups = {"ALL","SUBSCRIPTION"}, description = "TC3.9_Buy_Sell_VC_FundKristal_Using_Pooled_AC")
    public void stdFundBuyAndSellFromRM() throws Exception{
        initData();
        // STANDARD FUND create a sub from rm

        if(isProduction) {
            nodeTest.log(LogStatus.INFO,"This test case contains subscription, so skipped in production.");
        }
        else {


          //  portalInputMap.put("ip",ip);
          //  portalInputMap.put("key","PORTAL");

            inputMap.put("kristalId",kristalId);

            //login to portal and get the external user id
            rmSubs.loginUserandGetExternalUId(inputMap, email, password, ip);
            tokenRO=  ro.loginRO(roEmail,roPassword,ip);
            Assert.assertNotNull(tokenRO, "Token not generated");

            //set advisory mode
            inputMap.put("token",tokenRO);
            inputMap.put("advisoryMode","FULLY_DISCRETIONARY");
            ro.updateAdvisoryMode(inputMap);

            //login to rm and set input map for rm headers
            rmToken = signupAndLogin.authenticateUserAndGetToken(emailRM, passwordRM, ip, inputMap);
            Assert.assertNotNull(rmToken, "Token not generated");
            inputMap.put("token", rmToken);
            //Get Approved Kristals
            inputMap.put("key", "RMAPP");
            inputMap.put("APPLICATION", "RMAPP");

            // Get appropriate broker for subscription
            resp = dashboardAPIs.getAllUserAccounts(inputMap);
            HashMap<String, AccountDetailsBean> hashMap = apiSpecificUtils.getAccounts(resp);
            AccountDetailsBean accountDetailsBean = hashMap.get(AccountTypes.Pooled.toString());
            int accountId = accountDetailsBean.getUserAccountId();
            inputMap.put("User-Account", accountId);
            inputMap.put("accountId", accountId);

            inputMap.put("kristalSubscriptionType", "SUBSCRIBE");
            inputMap.put("type", "AMOUNT");

            inputMap.put("token",tokenRO);


            resp = ro.internalKristalDetails(inputMap);

            // Get dynamic values for subscription approval
            noOfUnits = apiSpecificUtils.minimumUnitsOfSubscriptionInDecimals(resp);
            amountToSubscribe = apiSpecificUtils.minimumInvestmentValueForTheKristal(resp);
            unitNav = apiSpecificUtils.getUnitNavOfKristal(resp);
            purchasePrice =  CommonUtils.roundOffToDecimals(unitNav,2);
            investmentAmount = CommonUtils.roundOffToDecimals(noOfUnits*unitNav,2);



            placementFeeResp = ro.getPlacementFeeInInternal(inputMap);

            placementFeePercent = apiSpecificUtils.getProcessedPlacementFee(placementFeeResp,amountToSubscribe,true);
            placementFeeAbsolute = apiSpecificUtils.getProcessedPlacementFee(placementFeeResp,amountToSubscribe,false);
            placementFeeAbsolute = (placementFeeAbsolute *  (amountToSubscribe.floatValue()) ) / 100;




            amountCreditedToBank = CommonUtils.roundOffToDecimals(investmentAmount+placementFeeAbsolute+10,2);

            //Populate input map with dynamic values
            inputMap.put("quantum",amountToSubscribe);
            inputMap.put("subscriptionDate",CommonUtils.getCurrentTimeInMs());

            inputMap.put("noOfUnits",noOfUnits);
            inputMap.put("purchasePrice",purchasePrice);
            inputMap.put("investmentAmount", investmentAmount);
            //fees default to 0
            inputMap.put("fees",placementFeeAbsolute+10);
            inputMap.put("bankCharges",1);
            inputMap.put("kristalFees",2);
            inputMap.put("externalManagerFees",3);
            inputMap.put("otherCharges",4);
            inputMap.put("placementFees",placementFeePercent);
            inputMap.put("placementFeesAbsolute",placementFeeAbsolute);
            inputMap.put("amountCreditedToBank",amountCreditedToBank);
            inputMap.put("eqCreditDebitType","CREDIT");
            inputMap.put("eqAmount",2000.25);
            inputMap.put("otp",OTP);
            inputMap.put("TCTYPE","POSITIVE");
            inputMap.replace("token",rmToken);


            rmAppSubs.generateOtpForSubscriptionOnetimeinRM(inputMap);
            rmAppSubs.oneTimeSubscribeToKristal(inputMap);

            System.out.println("Testing values here ");


            Thread.sleep(20000);

            resp = rmAppSubs.subscriptionOrderHistory(inputMap);
            Assert.assertNotNull(resp.then().extract().response().asString());
            tempStringListStore = (List<String>) CommonUtils.getNodeValueFromRespAsObject(resp, "subList.finalDisplayState");
            tempStringStore = tempStringListStore.stream().findFirst().get();


            //Checking in orders tab if the values are correct
            nodeTest.log(LogStatus.INFO, "state====" + tempStringStore);
            Assert.assertEquals(tempStringStore, "Processing", "state doesnt match");

            tempFloatListStore = (List<Float>) CommonUtils.getNodeValueFromRespAsObject(resp, "subList.placementFeePercentage");
            tempFloatStore = tempFloatListStore.stream().findFirst().get();


            nodeTest.log(LogStatus.INFO, "Placement Fee percent given====" + tempFloatStore);
            Assert.assertEquals(String.valueOf(tempFloatStore), String.valueOf(placementFeePercent), "Placement Fee is not present or does not match");



            tempFloatListStore = (List<Float>) CommonUtils.getNodeValueFromRespAsObject(resp, "subList.requestedAmount");
            tempFloatStore = tempFloatListStore.stream().findFirst().get();

            nodeTest.log(LogStatus.INFO, "Amount to Subscribe====" + tempFloatStore);
            Assert.assertEquals(String.valueOf(tempFloatStore), String.valueOf(amountToSubscribe), "The amount to subscribe does not match");

            tempGoalIdList = (List<Integer>) CommonUtils.getNodeValueFromRespAsObject(resp, "subList.subscriptionGoalId");
            tempGoalId = tempGoalIdList.stream().findFirst().get();

            inputMap.replace("token",tokenRO);
            inputMap.put("subTempGoalId", tempGoalId);

           // pooled.approvePooledSubsfromInternalWithDynamicValues(inputMap);
            resp = ro.approveHedgeFundsubwithEqualisation(inputMap);
            Thread.sleep(20000);

            inputMap.replace("token",rmToken);

            resp = rmAppSubs.subscriptionOrderHistory(inputMap);
            Assert.assertNotNull(resp.then().extract().response().asString());

            //Checking in orders tab after order approval
            tempFloatListStore = (List<Float>) CommonUtils.getNodeValueFromRespAsObject(resp, "subList.approvedUnits");
            tempFloatStore = tempFloatListStore.stream().findFirst().get();

            nodeTest.log(LogStatus.INFO, "Quantity Subscribed====" + tempFloatStore);
            Assert.assertEquals(String.valueOf(tempFloatStore), String.valueOf(noOfUnits), "The Quantity subscribed does not match");

            tempStringListStore = (List<String>) CommonUtils.getNodeValueFromRespAsObject(resp, "subList.finalDisplayState");
            tempStringStore = tempStringListStore.stream().findFirst().get();

            nodeTest.log(LogStatus.INFO, "state====" + tempStringStore);
            Assert.assertEquals(tempStringStore, "Completed", "state doesnt match");

            tempFloatListStore = (List<Float>) CommonUtils.getNodeValueFromRespAsObject(resp, "subList.subscriptionAmount");
            tempFloatStore = tempFloatListStore.stream().findFirst().get();

            nodeTest.log(LogStatus.INFO, "Approved amount for subscription====" + tempFloatStore);
            Assert.assertEquals(String.valueOf(tempFloatStore), String.valueOf(amountCreditedToBank), "The Amount Approved does not match");

            //Checking contract notes
            tempIntListStore = (List<Integer>) CommonUtils.getNodeValueFromRespAsObject(resp, "subList.subscriptionId");
            tempIntStore = tempIntListStore.stream().findFirst().get();
            inputMap.put("subscriptionId",tempIntStore);

          //   resp = dashboardAPIs.getKristalSpecificSubOrders(inputMap);
          //  tempIntListStore = (List<Integer>) CommonUtils.getNodeValueFromRespAsObject(resp, "subList.contractNoteId");
          //  tempIntStore = tempIntListStore.stream().findFirst().get();

        //    Assert.assertNotNull(tempFloatStore,"The contract Notes are not generated");


            inputMap.put("type", "UNITS");
            inputMap.put("quantum",noOfUnits);
            inputMap.put("kristalSubscriptionType", "UNSUBSCRIBE");
            rmAppSubs.generateOtpForSubscriptionOnetimeinRM(inputMap);
            rmAppSubs.oneTimeSubscribeToKristal(inputMap);

            inputMap.replace("token",rmToken);
            inputMap.replace("investmentAmount",CommonUtils.roundOffToDecimals(noOfUnits*unitNav,2));
            resp = resp = rmAppSubs.subscriptionOrderHistory(inputMap);
            tempGoalIdList = (List<Integer>) CommonUtils.getNodeValueFromRespAsObject(resp, "subList.subscriptionGoalId");
            tempGoalId = tempGoalIdList.stream().findFirst().get();
            inputMap.put("subTempGoalId", tempGoalId);
            inputMap.replace("token",tokenRO);

            System.out.println("Going to unsubscribe");

           //  pooled.unsubscribePooledKristalWithDynamicValues(inputMap);
            inputMap.put("fees",0.00);
            ro.approveHedgeFundSubscription(inputMap);





            //check orders tab
            // internal approve --> RM la check available cash --> check in rm kristals tab — the status
            // create unsub request full redeem-->
            // approve from internal,
            // go to rm --> check orders tab - status and available cash -->
            // wait for 2 min and then check for contract note, if it found or not


        }
    }
    @AfterMethod(alwaysRun = true)
    public void tearDownMethod(ITestResult result) {
        super.tearDownMethod(result);
    }
}
