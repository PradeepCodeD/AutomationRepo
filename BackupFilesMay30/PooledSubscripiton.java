package com.kristal.core.utils;

import com.kristal.core.api.BaseClass;
import com.kristal.core.api.modules.DashboardAPIs;
import com.kristal.core.api.modules.ResponsibleOfficer;
import com.kristal.core.utils.Enums.AccountTypes;
import com.kristal.core.utils.response_POJO.AccountDetailsBean;
import com.relevantcodes.extentreports.LogStatus;
import io.restassured.response.Response;
import org.testng.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.path.json.JsonPath.with;

public class PooledSubscripiton extends BaseClass {
    private DashboardAPIs dashboardAPIs = new DashboardAPIs();
    private Response resp;
    private String kristalId;
    private String token_r;
    private APISpecificUtils apiSpecificUtils = new APISpecificUtils();
    private ResponsibleOfficer ro = new ResponsibleOfficer();
    private String accountId;
    private List<Integer> subscriptionId;
    private String val;
    private Integer val1;

    public void validateSubscriptioninDashboard(Map<String, Object> inputMap){
        kristalId= String.valueOf(inputMap.get("kristalId"));

        resp = dashboardAPIs.getAllInvestedKristalsInAccount(inputMap);
        int resplist = (int) CommonUtils.getNodeValueFromRespAsObject(resp, "kristalList[0].kristal.kristalId");
        nodeTest.log(LogStatus.INFO,"resplist:" + resplist);
        float units = (float) CommonUtils.getNodeValueFromRespAsObject(resp, "kristalList[0].unitsSubscribedApproved");
        nodeTest.log(LogStatus.INFO,String.valueOf(units));
        Assert.assertEquals(kristalId, String.valueOf(resplist));
        int unitCheck = Math.round(units);
        Assert.assertEquals(inputMap.get("noofunits"), unitCheck);

        float unitNav = (float) CommonUtils.getNodeValueFromRespAsObject(resp, "kristalList[0].kristal.unitNav");
        nodeTest.log(LogStatus.INFO,String.valueOf(unitNav));

        float currentValue = unitNav * units;
        float cv = (float) CommonUtils.getNodeValueFromRespAsObject(resp, "kristalList[0].subscriptionValue");
        nodeTest.log(LogStatus.INFO,String.valueOf(cv));
        Assert.assertEquals(cv, currentValue);
        int totalAmount = (int) inputMap.get("amount");
        nodeTest.log(LogStatus.INFO,String.valueOf(totalAmount));
        float pnlCalculated = currentValue - Float.valueOf(totalAmount);
        nodeTest.log(LogStatus.INFO,String.valueOf(pnlCalculated));

        float pnl = (float) CommonUtils.getNodeValueFromRespAsObject(resp, "kristalList[0].pnl");
        nodeTest.log(LogStatus.INFO,String.valueOf(pnl));
        Assert.assertEquals(Math.ceil(Double.valueOf(pnl)), Math.ceil(Double.valueOf(pnlCalculated)));
}


    public void unsubscribePooledKristal(Map<String, Object> inputMap) throws InterruptedException {

        inputMap.put("kristalSubscriptionType", "UNSUBSCRIBE");
        inputMap.put("quantum", 300.00);
        inputMap.put("type", "UNITS");
        ro.subscribeFromInternal(inputMap);

        Thread.sleep(15000);
        resp = ro.getAllPendingHedgeFundSubscriptions(inputMap);
        val = resp.then().extract().response().asString();
        subscriptionId = with(val).getList("tempGoalId");
        val1 = subscriptionId.stream().max(Integer::compare).get();

        nodeTest.log(LogStatus.INFO,"tempGoalId====" + subscriptionId);
        Assert.assertNotNull(subscriptionId);
        inputMap.put("subTempGoalId",val1);

        //Approving Un-Subscribe from RO
        inputMap.put("noOfUnits",300.00); //quantum value itself
        inputMap.put("purchasePrice",500.00); //purchase price is NAV of the kristal
        inputMap.put("fees",0.00);

        double camount = (double) inputMap.get("noOfUnits") * (double)inputMap.get("purchasePrice");
        nodeTest.log(LogStatus.INFO,String.valueOf(camount));
        inputMap.put("investmentAmount",camount);
        ro.approveHedgeFundSubscription(inputMap);

    }

    public void approvePooledSubsfromInternal(Map<String, Object> inputMap) throws InterruptedException {
        Thread.sleep(20000);
        inputMap.put("pageNo", "1");
        //Get Pending Subscription From RO
        resp = ro.getAllPendingHedgeFundSubscriptions(inputMap);

        val = resp.then().extract().response().asString();
        List<Integer> subscriptionId = with(val).getList("tempGoalId");
        Integer val1 = subscriptionId.stream().max(Integer::compare).get();
        nodeTest.log(LogStatus.INFO,"subTempGoalId====" + subscriptionId);
        nodeTest.log(LogStatus.INFO,val1.toString());
        Assert.assertNotNull(subscriptionId);
        inputMap.put("subTempGoalId", val1);

        //get the no of units and purchase price and investment amount -- dynamically
        //Approving Subscription from RO
        inputMap.put("noOfUnits", 300.00);
        inputMap.put("purchasePrice",500.00);
        inputMap.put("investmentAmount",150000.00);
        //fees default to 0
        inputMap.put("fees",0.00);
        inputMap.put("bankCharges",0.00);
        inputMap.put("kristalFees",0.00);
        inputMap.put("externalManagerFees",0.00);
        inputMap.put("otherCharges",0.00);
        inputMap.put("placementFees",0.00);

        resp = ro.approveHedgeFundSubscription(inputMap);

    }
    public void approvePooledSubsfromInternalWithDynamicValues(Map<String, Object> inputMap) throws InterruptedException {
        Thread.sleep(20000);
        inputMap.put("pageNo", "1");
        //Get Pending Subscription From RO
        resp = ro.getAllPendingHedgeFundSubscriptions(inputMap);

        val = resp.then().extract().response().asString();
        List<Integer> subscriptionId = with(val).getList("tempGoalId");
        Integer val1 = subscriptionId.stream().max(Integer::compare).get();
        nodeTest.log(LogStatus.INFO,"subTempGoalId====" + subscriptionId);
        nodeTest.log(LogStatus.INFO,val1.toString());
        Assert.assertNotNull(subscriptionId);
        inputMap.put("subTempGoalId", val1);

        //get the no of units and purchase price and investment amount -- dynamically
        //Approving Subscription from RO
        //fees default to 0


        resp = ro.approveHedgeFundsubwithEqualisation(inputMap);

    }

    public String getInternalAccessToken(Map<String, Object> inputMap) throws InterruptedException {
        token_r = ro.loginRO(String.valueOf(inputMap.get("roemail")), String.valueOf(inputMap.get("ropassword")), String.valueOf(inputMap.get("ip")));
        Assert.assertNotNull(token_r, "Token not generated");
        inputMap.put("token",token_r);
        return  token_r;
    }

    public float getAccountDetails(Map<String, Object> inputMap){
        //Get Pooled Account Details
        resp = dashboardAPIs.getAllUserAccounts(inputMap);
        HashMap<String, AccountDetailsBean> hashMap = apiSpecificUtils.getAccounts(resp);
        AccountDetailsBean accountDetailsBean = hashMap.get(AccountTypes.Pooled.toString());
        accountId=String.valueOf(accountDetailsBean.getUserAccountId());
        nodeTest.log(LogStatus.INFO,String.valueOf(accountDetailsBean.getUserAccountId()));

        float u = accountDetailsBean.getKristalAccountNav();
        nodeTest.log(LogStatus.INFO,String.valueOf(u));

        String externalUserId = new Base64Converter().getUserID(String.valueOf(inputMap.get("token")));
        inputMap.put("ExternalUser-Id", externalUserId);
        inputMap.put("accountId", accountId);
        inputMap.put("User-Account",accountId);
        inputMap.put("userAccountId",accountId);

        return u;
    }
    //check on other flows

}
