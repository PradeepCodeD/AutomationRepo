package com.kristal.api.workflows.PooledSubscriptions;

import com.kristal.core.api.BaseClass;
import com.kristal.core.api.modules.DashboardAPIs;
import com.kristal.core.api.modules.KristalExplorePage;
import com.kristal.core.api.modules.ResponsibleOfficer;
import com.kristal.core.api.modules.SignupAndLogin;
import com.kristal.core.utils.APISpecificUtils;
import com.kristal.core.utils.Base64Converter;
import com.kristal.core.utils.CommonUtils;
import com.kristal.core.utils.Enums.AccountTypes;
import com.kristal.core.utils.Enums.TradeCatogory;
import com.kristal.core.utils.PooledSubscripiton;
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

import static io.restassured.path.json.JsonPath.with;

public class Buy_Sell_VC_FUND_Kristal_Using_Pooled_AC extends BaseClass {

    private String token;
    private String token_r;
    private String ro_email;
    private String ro_password;
    private String email;
    private String password;
    private String ip;
    private Float noOfUnits;
    private Double amountToSubscribe;
    private double unitNav;
    private SignupAndLogin signupAndLogin = new SignupAndLogin();
    private Map<String, Object> inputMap = new HashMap<>();
    private Map<String, Object> portalInputMap = new HashMap<>();
    private Response resp,placementFeeResp;
    private APISpecificUtils apiSpecificUtils = new APISpecificUtils();
    private DashboardAPIs dashboardAPIs = new DashboardAPIs();
    private ResponsibleOfficer ro = new ResponsibleOfficer();
    private PooledSubscripiton pooled = new PooledSubscripiton();
    private KristalExplorePage kristalExplorePage=new KristalExplorePage();
    private String kristalId;
    private String accountId;
    private Double placementFee;
    private float investmentAmount;
    private float amountCreditedToBank;
    private  float purchasePrice;
    private List<Integer> subscriptionId;
    private String val;
    private Integer val1;
    private float minInv;

    public void initData() {
        ip = CommonUtils.getPublicIp();
        kristalId = methodTestData.getAsJsonObject().get("kristalId").getAsString();
        if (isProduction) {
            email = "vineeth+2@kristal.ai";
            password = new Base64Converter().encodeStringToBase64("Admin@123");
        } else {
            email = methodTestData.getAsJsonObject().get("emailId").getAsString();
            password = new Base64Converter().encodeStringToBase64(methodTestData.getAsJsonObject().get("password").getAsString());
            ro_email= configData.get("RO_USER");
            ro_password=new Base64Converter().encodeStringToBase64(configData.get("RO_PASSWORD"));
        }
    }

    @Test(groups = {"ALL","SUBSCRIPTION"}, description = "TC3.9_Buy_Sell_VC_FundKristal_Using_Pooled_AC")
    public void kristalBuyAndSellVCFundInternal() throws Exception {
        initData();

        inputMap.put("ip", ip);
        if (isProduction) {
            nodeTest.log(LogStatus.SKIP, "The Test is Skipped in production");

        } else {
            inputMap.put("email", email);
            inputMap.put("password", password);
            inputMap.put("ip", ip);
            portalInputMap.put("ip",ip);


            portalInputMap.put("key","PORTAL");

            //Authenticate given user for staging
            token = signupAndLogin.authenticateUserAndGetToken(email, password, ip, inputMap);
            Assert.assertNotNull(token, "Token not generated");
            portalInputMap.put("token", token);

            //Kristal Private Investment account , wmid:521

            //accountList[1].navCurrent
            resp = dashboardAPIs.getAllUserAccounts(portalInputMap);
            HashMap<String, AccountDetailsBean> hashMap = apiSpecificUtils.getAccounts(resp);
            AccountDetailsBean accountDetailsBean = hashMap.get(AccountTypes.Pooled.toString());
            accountId = String.valueOf(accountDetailsBean.getUserAccountId());
            float knav = accountDetailsBean.getKristalAccountNav();
            nodeTest.log(LogStatus.INFO, String.valueOf(knav));
            nodeTest.log(LogStatus.INFO, String.valueOf(accountDetailsBean.getUserAccountId()));

            String externalUserId = new Base64Converter().getUserID(token);
            inputMap.put("ExternalUser-Id", externalUserId);

            token_r = ro.loginRO(ro_email, ro_password, ip);
            Assert.assertNotNull(token_r, "Token not generated");



            inputMap.put("kristalId", kristalId);
            inputMap.put("accountId", accountId);
            inputMap.put("kristalSubscriptionType", "SUBSCRIBE");
            inputMap.put("type", "AMOUNT");

            inputMap.put("token",token_r);


            resp = ro.internalKristalDetails(inputMap);

            noOfUnits = apiSpecificUtils.minimumUnitsOfSubscriptionInDecimals(resp);
            amountToSubscribe = apiSpecificUtils.minimumInvestmentValueForTheKristal(resp);
            unitNav = apiSpecificUtils.getUnitNavOfKristal(resp);
            purchasePrice =  CommonUtils.roundOffToDecimals(unitNav,2);
            investmentAmount = CommonUtils.roundOffToDecimals(noOfUnits*unitNav,2);


            placementFeeResp = ro.getPlacementFeeInInternal(inputMap);

            placementFee = apiSpecificUtils.getProcessedPlacementFee(placementFeeResp,amountToSubscribe);
            amountCreditedToBank = CommonUtils.roundOffToDecimals(investmentAmount+placementFee+10,2);

            inputMap.put("quantum",amountToSubscribe);
            ro.subscribeFromInternal(inputMap);

            inputMap.put("noOfUnits",noOfUnits);
            inputMap.put("purchasePrice",purchasePrice);
            inputMap.put("investmentAmount", investmentAmount);
            //fees default to 0
            inputMap.put("fees",placementFee+10);
            inputMap.put("bankCharges",1);
            inputMap.put("kristalFees",2);
            inputMap.put("externalManagerFees",3);
            inputMap.put("otherCharges",4);
            inputMap.put("placementFees",placementFee);
            inputMap.put("amountCreditedToBank",amountCreditedToBank);
            inputMap.put("eqCreditDebitType","CREDIT");
            inputMap.put("eqAmount",2000.25);



            //Approval Flow
            pooled.approvePooledSubsfromInternalWithDynamicValues(inputMap);




        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownMethod(ITestResult result) {
        super.tearDownMethod(result);
    }

}
