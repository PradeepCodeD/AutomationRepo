package com.kristal.core.utils;

import com.kristal.core.api.BaseClass;
import com.kristal.core.api.modules.*;
import com.kristal.core.utils.Enums.BillingTypes;
import com.kristal.core.utils.Enums.CountryTypes;
import com.kristal.core.utils.Enums.SearchFilterNames;
import com.kristal.core.utils.response_POJO.*;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.testng.Assert;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.kristal.core.utils.Enums.SearchFilterNames.*;
import static io.restassured.path.json.JsonPath.from;
import static io.restassured.path.json.JsonPath.with;

import org.json.JSONObject;

public class APISpecificUtils extends BaseClass {

    public HashMap<String,AccountDetailsBean> getAccounts (Response resp){
        HashMap<String,AccountDetailsBean> accounts = new HashMap<>();
        List<HashMap<String,Object>> obj=from(resp.then().extract().response().asString()).get("accountList");
        String res = resp.then().extract().response().asString();
        String wealthManager_name;
        for (int i=0;i<obj.size();i++){
            wealthManager_name=with(res).getList("accountList.account.wealthManager.wealthManagerName").get(i).toString();

            if(wealthManager_name.equalsIgnoreCase("Kristal Freedom")) {
                AccountDetailsBean accountDetailsBean = new AccountDetailsBean();
                accountDetailsBean.setExternalAccountId(with(res).getList("accountList.account.externalAccountId").get(i).toString());
                accountDetailsBean.setUserAccountId((int) with(res).getList("accountList.account.id").get(i));
                accountDetailsBean.setWealthManagerName(with(res).getList("accountList.account.wealthManager.wealthManagerName").get(i).toString());
                accountDetailsBean.setKristalAccountNav((float)obj.get(i).get("navCurrent"));
                accounts.put("Freedom",accountDetailsBean);
            }
            else if (wealthManager_name.equalsIgnoreCase("Kristal Pooled")) {
                AccountDetailsBean accountDetailsBean = new AccountDetailsBean();
                accountDetailsBean.setExternalAccountId(with(res).getList("accountList.account.externalAccountId").get(i).toString());
                accountDetailsBean.setUserAccountId((int) with(res).getList("accountList.account.id").get(i));
                accountDetailsBean.setWealthManagerName(with(res).getList("accountList.account.wealthManager.wealthManagerName").get(i).toString());
                accountDetailsBean.setKristalAccountNav((float)obj.get(i).get("navCurrent"));
                accounts.put("Pooled",accountDetailsBean);

            }else if (wealthManager_name.equalsIgnoreCase("Saxo Bank")) {
                AccountDetailsBean accountDetailsBean = new AccountDetailsBean();
                accountDetailsBean.setExternalAccountId(with(res).getList("accountList.account.externalAccountId").get(i).toString());
                accountDetailsBean.setUserAccountId((int) with(res).getList("accountList.account.id").get(i));
                accountDetailsBean.setWealthManagerName(with(res).getList("accountList.account.wealthManager.wealthManagerName").get(i).toString());
                accountDetailsBean.setKristalAccountNav((float)obj.get(i).get("navCurrent"));
                accounts.put("Saxo",accountDetailsBean);

            }else if (wealthManager_name.equalsIgnoreCase("Interactive Brokers")) {
                AccountDetailsBean accountDetailsBean = new AccountDetailsBean();
                accountDetailsBean.setExternalAccountId(with(res).getList("accountList.account.externalAccountId").get(i).toString());
                accountDetailsBean.setUserAccountId((int) with(res).getList("accountList.account.id").get(i));
                accountDetailsBean.setWealthManagerName(with(res).getList("accountList.account.wealthManager.wealthManagerName").get(i).toString());
                accountDetailsBean.setKristalAccountNav((float)obj.get(i).get("navCurrent"));
                accounts.put("IB",accountDetailsBean);
            }
            else if (wealthManager_name.equalsIgnoreCase("Kristal Private Investment")) {
                AccountDetailsBean accountDetailsBean = new AccountDetailsBean();
                accountDetailsBean.setExternalAccountId(with(res).getList("accountList.account.externalAccountId").get(i).toString());
                accountDetailsBean.setUserAccountId((int) with(res).getList("accountList.account.id").get(i));
                accountDetailsBean.setWealthManagerName(with(res).getList("accountList.account.wealthManager.wealthManagerName").get(i).toString());
                accountDetailsBean.setKristalAccountNav((float)obj.get(i).get("navCurrent"));
                accounts.put("KPI",accountDetailsBean);
            }
            else if (wealthManager_name.equalsIgnoreCase("IB IMA")) {
                AccountDetailsBean accountDetailsBean = new AccountDetailsBean();
                accountDetailsBean.setExternalAccountId(with(res).getList("accountList.account.externalAccountId").get(i).toString());
                accountDetailsBean.setUserAccountId((int) with(res).getList("accountList.account.id").get(i));
                accountDetailsBean.setWealthManagerName(with(res).getList("accountList.account.wealthManager.wealthManagerName").get(i).toString());
                accountDetailsBean.setKristalAccountNav((float)obj.get(i).get("navCurrent"));
                accounts.put("IB_IMA",accountDetailsBean);
            }
        }
        return accounts;
    }

    public List<KristalTypesBean> getKristalList (Response resp, String kristalType){

        List<HashMap<String,Object>> obj=from(resp.then().extract().response().asString()).get("kristalList");
        String name;
        List<KristalTypesBean> arrayList = new ArrayList();
        for (int i=0;i<obj.size();i++){
            name=(String) obj.get(i).get("kristalType");
            if(name.equalsIgnoreCase(kristalType)) {
                    KristalTypesBean kristalTypesBean = new KristalTypesBean();
                    kristalTypesBean.setKristalId(String.valueOf(obj.get(i).get("kristalId")));
                    kristalTypesBean.setName(String.valueOf(obj.get(i).get("name")));
                    kristalTypesBean.setCurrency(String.valueOf(obj.get(i).get("currency")));
                    kristalTypesBean.setUnitNav(String.valueOf(obj.get(i).get("lastClose")));
                    arrayList.add(kristalTypesBean);
            }
        }
        return arrayList;
    }

    public List<MarketOrdersBean> getAllMarketOrders (Response resp, int userid,int kristalId, long accountId){
        List<HashMap<String,Object>> obj=from(resp.then().extract().response().asString()).get("");
        int uid,kid,aid;
        List<MarketOrdersBean> arrayList = new ArrayList();
        for (int i=0;i<obj.size();i++){
            uid = (int) obj.get(i).get("userId");
            kid = (int) obj.get(i).get("kristalId");
            aid = (int) obj.get(i).get("executionAccountId");

            if(uid == userid && kid == kristalId && aid == accountId) {
                obj.get(i).values().removeIf(Objects::isNull);
                MarketOrdersBean marketOrdersBean = new MarketOrdersBean();
                marketOrdersBean.setExecutionAccountAdvisoryMode(String.valueOf(obj.get(i).get("executionAccountAdvisoryMode")));
                marketOrdersBean.setNoOFUnitsSubscribedPending( (float) obj.get(i).get("noOFUnitsSubscribedPending"));
                marketOrdersBean.setQuantumType(String.valueOf(obj.get(i).get("quantumType")));
                marketOrdersBean.setSubscriptionId((int)obj.get(i).get("subscriptionId"));
                marketOrdersBean.setTempGoalId((int)obj.get(i).get("tempGoalId"));
                marketOrdersBean.setSubscribedByUserId((int) obj.get(i).get("subscribedByUserId"));
                arrayList.add(marketOrdersBean);
            }
        }
        return arrayList;
    }


    public List<TranchesStateBean> getTrancheStates (Response resp, int trancheId){
        List<HashMap<String,Object>> obj=from(resp.then().extract().response().asString()).get("");
        int tid;
        List<TranchesStateBean> arrayList = new ArrayList();
        for (int i=0;i<obj.size();i++){
            tid = (int) obj.get(i).get("trancheId");
            if(tid == trancheId) {
                obj.get(i).values().removeIf(Objects::isNull);
                TranchesStateBean tranchesStateBean = new TranchesStateBean();
                tranchesStateBean.setName(String.valueOf(obj.get(i).get("name")));
                tranchesStateBean.setTrancheState(String.valueOf(obj.get(i).get("trancheState")));
                tranchesStateBean.setTrancheName(String.valueOf(obj.get(i).get("trancheName")));
                tranchesStateBean.setTrancheActualType(String.valueOf(obj.get(i).get("trancheActualType")));
                tranchesStateBean.setCurrency(String.valueOf(obj.get(i).get("currency")));
                tranchesStateBean.setTrancheType(String.valueOf(obj.get(i).get("trancheType")));
                tranchesStateBean.setTrancheActualMainType(String.valueOf(obj.get(i).get("trancheActualMainType")));
                tranchesStateBean.setWaitlistState(String.valueOf(obj.get(i).get("waitlistState")));
                tranchesStateBean.setDate((long) obj.get(i).get("date"));

                //if null is obtained when the amount subscribed itself is the amount given in automation, on cancel, it becomes null --  need to handle here
                tranchesStateBean.setNextAwaitedAction(String.valueOf(obj.get(i).get("nextAwaitedAction")));
                float ca = (float) obj.get(i).get("commitedAmount");
                tranchesStateBean.setCommitedAmount(ca);
                tranchesStateBean.setUsersCount((int) obj.get(i).get("usersCount"));

                arrayList.add(tranchesStateBean);
            }
        }
        return arrayList;
    }

    public TreeMap<Integer, String> getTrancheOrderStates(Response resp){
        List<HashMap<String,Object>> obj=from(resp.then().extract().response().asString()).get("tranchOrderStateCycle");
        HashMap<Integer, String> hm=new HashMap<>();
        TrancheOrderStatebean trancheOrderStateBean = new TrancheOrderStatebean();

        for (int i=0;i<obj.size();i++) {
            obj.get(i).values().removeIf(Objects::isNull);
            trancheOrderStateBean.setTrancheOrderState(String.valueOf(obj.get(i).get("trancheOrderState")));
            trancheOrderStateBean.setTrancheOrderStateName(String.valueOf(obj.get(i).get("trancheOrderStateName")));
            trancheOrderStateBean.setOrderNo((Integer) obj.get(i).get("orderNo"));
            trancheOrderStateBean.setCompleted((Boolean) obj.get(i).get("isCompleted"));
            trancheOrderStateBean.setNotificationSent((Boolean) obj.get(i).get("isNotificationSent"));
            hm.put(trancheOrderStateBean.getOrderNo(),trancheOrderStateBean.getTrancheOrderState());
        }

        TreeMap<Integer,String> tm=new  TreeMap<> (hm);
        Iterator itr=tm.keySet().iterator();
        while(itr.hasNext())
        {
            int key=(int)itr.next();
            nodeTest.log(LogStatus.INFO,"order no:  "+key+"State name:   "+tm.get(key));
        }
        return tm;

    }

    public  TrancheOrderStatebean getTrancheOrderDetailsforOrderNo(Response resp, int orderNo){
        List<HashMap<String,Object>> obj=from(resp.then().extract().response().asString()).get("tranchOrderStateCycle");
        TrancheOrderStatebean trancheOrderStateBean = new TrancheOrderStatebean();

        for (int i=0;i<obj.size();i++) {
            obj.get(i).values().removeIf(Objects::isNull);
            int on = (int) obj.get(i).get("orderNo");
            if(on == orderNo) {
                trancheOrderStateBean.setTrancheOrderState(String.valueOf(obj.get(i).get("trancheOrderState")));
                trancheOrderStateBean.setTrancheOrderStateName(String.valueOf(obj.get(i).get("trancheOrderStateName")));
                trancheOrderStateBean.setOrderNo((Integer) obj.get(i).get("orderNo"));
                trancheOrderStateBean.setCompleted((Boolean) obj.get(i).get("isCompleted"));
                trancheOrderStateBean.setNotificationSent((Boolean) obj.get(i).get("isNotificationSent"));
            }
        }
        return trancheOrderStateBean;
    }


    public void setTrancheBean(List<HashMap<String,Object>> obj, int i,List<TranchesStateBean> arrayList ,TranchesStateBean tranchesStateBean ){
        tranchesStateBean.setName(String.valueOf(obj.get(i).get("name")));
        tranchesStateBean.setTrancheName(String.valueOf(obj.get(i).get("trancheName")));
        tranchesStateBean.setTrancheState(String.valueOf(obj.get(i).get("trancheState")));
        tranchesStateBean.setTrancheId((int) obj.get(i).get("trancheId"));
        tranchesStateBean.setTrancheActualType(String.valueOf(obj.get(i).get("trancheActualType")));
        tranchesStateBean.setCurrency(String.valueOf(obj.get(i).get("currency")));
        tranchesStateBean.setTrancheType(String.valueOf(obj.get(i).get("trancheType")));
        tranchesStateBean.setTrancheActualMainType(String.valueOf(obj.get(i).get("trancheActualMainType")));
        tranchesStateBean.setWaitlistState(String.valueOf(obj.get(i).get("waitlistState")));
        tranchesStateBean.setDate((long) obj.get(i).get("date"));
        arrayList.add(tranchesStateBean);
    }

    public List<TranchesStateBean> getAllTrancheStatesforTrancheName (Response resp, String tname , String name){
        List<HashMap<String,Object>> obj=from(resp.then().extract().response().asString()).get("");
        String trancheName , dealName;
        TranchesStateBean tranchesStateBean = new TranchesStateBean();
        List<TranchesStateBean> arrayList = new ArrayList();
        for (int i=0;i<obj.size();i++) {
            trancheName = String.valueOf(obj.get(i).get("trancheName"));
            dealName = String.valueOf(obj.get(i).get("name"));

            if (!tname.isEmpty() || !tname.isBlank()) {
                if (trancheName.equals(tname) && dealName.equals(name)) {
                    setTrancheBean(obj,i,arrayList,tranchesStateBean);
                }
            }else{
                if(dealName.equals(name)){
                   setTrancheBean(obj,i,arrayList,tranchesStateBean);
                }
            }
        }
        return arrayList;
    }


    public List<TranchesStateBean> getAllWaitlistTranches (Response resp){
        List<HashMap<String,Object>> obj=from(resp.then().extract().response().asString()).get("");
        String waitlistState;
        List<TranchesStateBean> arrayList = new ArrayList();
        for (int i=0;i<obj.size();i++){
            waitlistState = String.valueOf(obj.get(i).get("waitlistState"));

            if(waitlistState.equals("ACTIVE")) {
                TranchesStateBean tranchesStateBean = new TranchesStateBean();
                tranchesStateBean.setName(String.valueOf(obj.get(i).get("name")));
                tranchesStateBean.setTrancheState(String.valueOf(obj.get(i).get("trancheState")));
                tranchesStateBean.setTrancheName(String.valueOf(obj.get(i).get("trancheName")));
                tranchesStateBean.setTrancheId((int) obj.get(i).get("trancheId"));
                tranchesStateBean.setTrancheActualType(String.valueOf(obj.get(i).get("trancheActualType")));
                tranchesStateBean.setCurrency(String.valueOf(obj.get(i).get("currency")));
                tranchesStateBean.setTrancheType(String.valueOf(obj.get(i).get("trancheType")));
                tranchesStateBean.setTrancheActualMainType(String.valueOf(obj.get(i).get("trancheActualMainType")));
                tranchesStateBean.setDate((long) obj.get(i).get("date"));
                arrayList.add(tranchesStateBean);
            }
        }
        return arrayList;
    }


    public List<TranchesStateBean> getAllTranchesbasedonState (Response resp, String tstate){
        List<HashMap<String,Object>> obj=from(resp.then().extract().response().asString()).get("");
        String trancheState;
        List<TranchesStateBean> arrayList = new ArrayList();
        for (int i=0;i<obj.size();i++){
            trancheState = String.valueOf(obj.get(i).get("trancheState"));

            if(trancheState.equals(tstate)) {
                TranchesStateBean tranchesStateBean = new TranchesStateBean();
                tranchesStateBean.setName(String.valueOf(obj.get(i).get("name")));
                tranchesStateBean.setTrancheState(String.valueOf(obj.get(i).get("waitlistState")));
                tranchesStateBean.setTrancheName(String.valueOf(obj.get(i).get("trancheName")));
                tranchesStateBean.setTrancheId((int) obj.get(i).get("trancheId"));
                tranchesStateBean.setTrancheActualType(String.valueOf(obj.get(i).get("trancheActualType")));
                tranchesStateBean.setCurrency(String.valueOf(obj.get(i).get("currency")));
                tranchesStateBean.setTrancheType(String.valueOf(obj.get(i).get("trancheType")));
                tranchesStateBean.setTrancheActualMainType(String.valueOf(obj.get(i).get("trancheActualMainType")));
                tranchesStateBean.setDate((long) obj.get(i).get("date"));
                arrayList.add(tranchesStateBean);
            }
        }
        return arrayList;
    }

    public List<ApprovedTemplatesBean> getAllApprovedTemplates (Response resp, int pmTempId){
        List<HashMap<String,Object>> obj=from(resp.then().extract().response().asString()).get("templateList");
        List<ApprovedTemplatesBean> arrayList = new ArrayList();
        int tid;
        for (int i=0;i<obj.size();i++){
            tid = (int) obj.get(i).get("pmTemplateId");
            if(tid == pmTempId) {
                ApprovedTemplatesBean approvedTemplatesBean = new ApprovedTemplatesBean();
                approvedTemplatesBean.setName(String.valueOf(obj.get(i).get("name")));
                approvedTemplatesBean.setCiTranchesCount((int) obj.get(i).get("liveTrancehsCount"));
                approvedTemplatesBean.setClosedTranchesCount((int) obj.get(i).get("closedTranchesCount"));
                approvedTemplatesBean.setLiveTrancehsCount((int) obj.get(i).get("liveTrancehsCount"));
                approvedTemplatesBean.setTemplateState(String.valueOf(obj.get(i).get("templateState")));

                arrayList.add(approvedTemplatesBean);
            }
        }
        return arrayList;
    }

    public List<KristalCategoryBean> getCategoryListforSFName (Response resp, String searchFilterName){

        List<HashMap<String,Object>> obj=from(resp.then().extract().response().asString()).get("elementList");
        String sfName;
        List<KristalCategoryBean> arrayList = new ArrayList();
        for (int i=0;i<obj.size();i++){
            sfName=(String) obj.get(i).get("searchFilterName");
            if(sfName.equalsIgnoreCase(searchFilterName)) {
                List<HashMap<String,Object>> a = (ArrayList)obj.get(i).get("categoryList");
                for(int j=0; j<a.size(); j++) {
                    KristalCategoryBean kristalCategoryBean = new KristalCategoryBean();
                    kristalCategoryBean.setcId((int) (a.get(j).get("categoryId")));
                    kristalCategoryBean.setcName(String.valueOf(a.get(j).get("categoryName")));
                arrayList.add(kristalCategoryBean);
                     }
            }
        }
        return arrayList;
    }

    public Map<String, Integer> getCIdandSFId (Response resp, String searchFilterName, String categryName){

        List<HashMap<String,Object>> obj=from(resp.then().extract().response().asString()).get("elementList");
        String sfName,cname;
        int sfId,cid;
        Map<String,Integer> scIds = new HashMap<>();
        List<KristalCategoryBean> arrayList = new ArrayList();
        for (int i=0;i<obj.size();i++){
            sfName=(String) obj.get(i).get("searchFilterName");
            if(sfName.equalsIgnoreCase(searchFilterName)) {
                 sfId = (int) obj.get(i).get("searchFilterId");
                 scIds.put("searchFilterId",sfId);
                List<HashMap<String,Object>> a = (ArrayList)obj.get(i).get("categoryList");
                for(int j=0; j<a.size(); j++) {
                    cname = (String) a.get(j).get("categoryName");
                    if(cname.equalsIgnoreCase(categryName)){
                      cid=  (int) (a.get(j).get("categoryId"));
                      scIds.put("categoryId",cid);
                    }
                }
            }
        }
        return scIds;
    }


    public KristalTypesBean getKristalDetailsbyID(List<KristalTypesBean> kristalList,int kristalId){
        KristalTypesBean kristalTypesBean=null;
        Iterator iterator=kristalList.iterator();
        while(iterator.hasNext()){
            KristalTypesBean kristalBean=(KristalTypesBean)iterator.next();
            if(kristalBean.getKristalId().equals(String.valueOf(kristalId))){
                kristalTypesBean=kristalBean;
                break;
            }
        }
        return  kristalTypesBean;
    }

    public ArrayList<KristalAssetBean> extractAssetDetails(Response resp, Map<String,Object> inputMap) {
        String res = resp.then().extract().response().asString();
        List<Map> obj = with(res).get("");
        String AssetId;
        ArrayList<KristalAssetBean> assetDetails = new ArrayList<>();
        KristalAssetBean kristalAssetBean = new KristalAssetBean();
        for (int i = 0; i < obj.size(); i++) {
            AssetId = String.valueOf(obj.get(i).get("id"));
            if (AssetId.equalsIgnoreCase(String.valueOf(inputMap.get("asset")))) {
                kristalAssetBean.setAssetId((int) inputMap.get("asset"));
                kristalAssetBean.setCustom((boolean) obj.get(i).get("custom"));
                kristalAssetBean.setAssetName(String.valueOf(obj.get(i).get("name")));
                kristalAssetBean.setExchange(String.valueOf(obj.get(i).get("exchange")));
                kristalAssetBean.setCurrency(String.valueOf(inputMap.get("currency")));
                kristalAssetBean.setAssetType(String.valueOf(obj.get(i).get("secType")));
                kristalAssetBean.setQuantity((int) (inputMap.get("quantity")));
                kristalAssetBean.setNav((float) inputMap.get("lastPrice"));
            }

        }
        assetDetails.add(kristalAssetBean);
        return assetDetails;
    }
    public ArrayList<AccountDetailsBean> getAllAccounts (Response resp){
        ArrayList<AccountDetailsBean> accountsList = new ArrayList<>();
        List<HashMap<String,Object>> obj=from(resp.then().extract().response().asString()).get("accountList");
        String res = resp.then().extract().response().asString();
        AccountDetailsBean accountDetailsBean;
        for (int i=0;i<obj.size();i++){
                accountDetailsBean = new AccountDetailsBean();
                accountDetailsBean.setExternalAccountId(with(res).getList("accountList.account.externalAccountId").get(i).toString());
                accountDetailsBean.setUserAccountId((int) with(res).getList("accountList.account.id").get(i));
                accountDetailsBean.setWealthManagerName(with(res).getList("accountList.account.wealthManager.wealthManagerName").get(i).toString());
                accountDetailsBean.setKristalAccountNav((float)obj.get(i).get("navCurrent"));
                if(accountDetailsBean.getKristalAccountNav() > 0){
                    accountsList.add(accountDetailsBean);
                }
             //   accountsList.add(accountDetailsBean);
        }
        return accountsList;
    }
    public boolean containsDesiredReport(AccountDetailsBean accountDetailsBean, ArrayList<String> reportNameList,ExtentTest nodeTest){
        boolean status=false;
        switch (accountDetailsBean.getWealthManagerName()){
            case "Kristal Pooled":
                if (!reportGenerated("Kristal Pooled",accountDetailsBean.getExternalAccountId(),reportNameList,nodeTest)){
                    nodeTest.log(LogStatus.FAIL,"No Reports generated for 'Kristal Pooled' ("+accountDetailsBean.getExternalAccountId()+")");
                }
                break;
            case "Saxo Bank":
                if (!reportGenerated("Saxo Bank",accountDetailsBean.getExternalAccountId(),reportNameList,nodeTest)){
                    nodeTest.log(LogStatus.FAIL,"No Reports generated for 'Saxo Bank' ("+accountDetailsBean.getExternalAccountId()+")");
                }
                break;
            case "Interactive Brokers":
                if (!reportGenerated("Interactive Brokers",accountDetailsBean.getExternalAccountId(),reportNameList,nodeTest)){
                    nodeTest.log(LogStatus.FAIL,"No Reports generated for 'Interactive Brokers' ("+accountDetailsBean.getExternalAccountId()+")");
                }
                break;
            case "Kristal Freedom":
                if (!reportGenerated("Kristal Freedom",accountDetailsBean.getExternalAccountId(),reportNameList,nodeTest)){
                    nodeTest.log(LogStatus.FAIL,"No Reports generated for 'Kristal Freedom' ("+accountDetailsBean.getExternalAccountId()+")");
                }
                break;
        }
        return status;
    }

    public boolean reportGenerated(String wealthManagerName,String externalAccountId, ArrayList<String> reportNameList, ExtentTest nodeTest){
        boolean flagForSpecificAccount=false;
        for (int i=0;i<reportNameList.size();i++){
            if (reportNameList.get(i).contains(wealthManagerName)&&reportNameList.get(i).contains(externalAccountId.replace("/","-"))){
                nodeTest.log(LogStatus.INFO,reportNameList.get(i));
                flagForSpecificAccount=true;
                break;
            }
        }
        return flagForSpecificAccount;
    }

    public ArrayList<JobDetailsBean> getALLJobDetails (Response resp){
        ArrayList<JobDetailsBean>  jobDetailsBeansList= new ArrayList<>();
        List<HashMap<String,Object>> obj=from(resp.then().extract().response().asString()).get();

        JobDetailsBean jobDetailsBean;
        for (int i=0;i<obj.size();i++){
            jobDetailsBean = new JobDetailsBean();
            jobDetailsBean.setProcessId((int)obj.get(i).get("processId"));
            jobDetailsBean.setRunId((int)obj.get(i).get("runId"));
            jobDetailsBean.setStartTime((long)obj.get(i).get("startTime"));
            jobDetailsBean.setEndTime(obj.get(i).get("endTime"));
            jobDetailsBean.setState((String) obj.get(i).get("state"));
            jobDetailsBean.setMaxAttempts((int)obj.get(i).get("maxAttempts"));
            jobDetailsBean.setHostName((String) obj.get(i).get("hostName"));
            jobDetailsBean.setnAttempts((int)obj.get(i).get("nAttempts"));
            jobDetailsBean.setLastAttemptTime((long)obj.get(i).get("lastAttemptTime"));
            jobDetailsBean.setName((String) obj.get(i).get("name"));
            jobDetailsBean.setLocked((boolean) obj.get(i).get("locked"));
            jobDetailsBeansList.add(jobDetailsBean);
            jobDetailsBean=null;
        }
        return jobDetailsBeansList;
    }

    public JobDetailsBean getJobByProcessID(int processId,Response resp){
        ArrayList<JobDetailsBean> jobDetailsBeanArrayList=getALLJobDetails(resp);
        JobDetailsBean jobDetailsBean=null;
        for (int i=0;i<jobDetailsBeanArrayList.size();i++){
            if (jobDetailsBeanArrayList.get(i).getProcessId()==processId){
                jobDetailsBean=jobDetailsBeanArrayList.get(i);
                break;
            }
        }
        return jobDetailsBean;
    }

    public ArrayList<KristalDetailsFromDB> getALLKristalDetails (Response resp){
        ArrayList<KristalDetailsFromDB>  kristalDetailsFromDBList= new ArrayList<>();
        List<HashMap<String,Object>> obj=from(resp.then().extract().response().asString()).get();
        nodeTest.log(LogStatus.INFO,"List Size:"+obj.size());
        KristalDetailsFromDB kristalDetailsFromDB;
        ArrayList<Object> dateArray;
        for (int i=0;i<obj.size();i++){
            kristalDetailsFromDB = new KristalDetailsFromDB();
            kristalDetailsFromDB.setKristalId((int)obj.get(i).get("kristalId"));
            kristalDetailsFromDB.setKristalName((String) obj.get(i).get("kristalName"));
            kristalDetailsFromDB.setNavWithoutAccrued((float)obj.get(i).get("navWithoutAccrued"));
            dateArray=(ArrayList) obj.get(i).get("lastUpdateDate");
            kristalDetailsFromDB.setLastUpdatedYear((int)dateArray.get(0));
            kristalDetailsFromDB.setLastUpdatedMonth((int)dateArray.get(1));
            kristalDetailsFromDB.setLastUpdatedDay((int)dateArray.get(2));
            dateArray=null;
            kristalDetailsFromDB.setKristalType((String) obj.get(i).get("kristalType"));
            kristalDetailsFromDBList.add(kristalDetailsFromDB);
            kristalDetailsFromDB=null;
        }
        return kristalDetailsFromDBList;
    }


    public HashMap<String, Long> getAllReportValues1 (Response resp){
        HashMap<String ,Long> hlist = new HashMap<>();
        List<HashMap<String,Object>> obj=from(resp.then().extract().response().asString()).get();
        ReportDatesfromDB rdb;
        for (int i=0;i<obj.size();i++) {
            rdb = new ReportDatesfromDB();
            rdb.setReportType((String) obj.get(i).get("reportType"));
            rdb.setLastUpdatedDate((Long) obj.get(i).get("lastUpdatedTime"));
            hlist.put(rdb.getReportType(),rdb.getLastUpdatedDate());
        }
        return hlist;
    }


    public HashMap<String, Integer> getAllMasterAccounts (Response resp){
        HashMap<String , Integer> hlist = new HashMap<>();
        List<HashMap<String,Object>> obj=from(resp.then().extract().response().asString()).get("");
        InternalUserAccounts iur;
        for (int i=0;i<obj.size();i++) {
            iur = new InternalUserAccounts();
            iur.setNickname((String) obj.get(i).get("nickname"));
            iur.setAccountId((int) obj.get(i).get("accountId"));
            hlist.put(iur.getNickname(),iur.getAccountId());
        }
        return hlist;
    }

    public HashMap<Integer, String> getAllSipIdsandStatus (Response resp){
        HashMap<  Integer, String> hlist = new HashMap<>();
        List<HashMap<String,Object>> obj=from(resp.then().extract().response().asString()).get("sipList");
        SipResponse iur;
        for (int i=0;i<obj.size();i++) {
            iur = new SipResponse();
            iur.setSipId((int) obj.get(i).get("sipId"));
            iur.setState((String) obj.get(i).get("status"));
            hlist.put(iur.getSipId(),iur.getState());
        }
        return hlist;
    }

    public String getStatusofCartOrders (Response resp,String cartId,String jsonPath){
        List<HashMap<String,Object>> obj=from(resp.then().extract().response().asString()).get(jsonPath);
       String sts = null;
        List<HashMap<String, Object>> flist = obj.stream().filter(Objects::nonNull).collect(Collectors.toList());
        for (int i=0;i<flist.size();i++) {
            String val = (String) flist.get(i).get("cartId");
            if (val != null) {
                if ((val).equalsIgnoreCase(cartId)) {
                    sts = String.valueOf(flist.get(i).get("finalDisplayState"));
                }
            }
        }
        return sts;
    }


    public String getStatusofProposedTDOrders (Response resp,int orderId){
        List<HashMap<String,Object>> obj=from(resp.then().extract().response().asString()).get("");
        String sts = null;
        for (int i=0;i<obj.size();i++) {
            int val = (int) obj.get(i).get("orderId");
                if (val == orderId) {
                    sts = String.valueOf(obj.get(i).get("status"));
                }
        }
        return sts;
    }
    /**
     * @description These Method verifies the execution of a particular Job
     * @param ib_Job1_details
     * @param nodeTest
     * @param expectedJobRanTime in Miliseconds
     * @return booloean status of the job exection
     */
    public boolean jobLoggerAndVerifier(JobDetailsBean ib_Job1_details,ExtentTest nodeTest,long expectedJobRanTime ){
        Date startDate=CommonUtils.formatLongDateExcludingMS(ib_Job1_details.getStartTime());
        Date endDate=CommonUtils.formatLongDateExcludingMS((long) ib_Job1_details.getEndTime());
        nodeTest.log(LogStatus.INFO,"startDate:"+startDate);
        nodeTest.log(LogStatus.INFO,"endDate:"+endDate);
        String state=ib_Job1_details.getState();
        long difference = endDate.getTime()-startDate.getTime();
        nodeTest.log(LogStatus.INFO,"difference:"+difference);
        nodeTest.log(LogStatus.INFO,"Compare To:"+difference/(1000*60));
        boolean status=true;
        String msg;
        //Verifying Job is executed on Current Date or Not
        if (CommonUtils.getZeroTimeDate(new Date()).compareTo(CommonUtils.getZeroTimeDate(startDate))==0){

            //verifying Time Duration
            if (ib_Job1_details.getEndTime().equals(null)){
                //error
                msg=new TableUtils().
                        jobDetailsLogger(ib_Job1_details).
                        createRow("Job Execution Status","").
                        createRow("Excution Details","We did not get the end time of the Job").
                        createTable();
                nodeTest.log(LogStatus.FAIL,msg);
                //Assert.assertFalse(ib_Job1_details.getEndTime().equals(null),"We did not get the end time of the Job");
                status=false;
            }else if((endDate.getTime()-startDate.getTime())>=expectedJobRanTime) {
                //pass
                if (state.equalsIgnoreCase("done")){
                    //Pass
                    msg=new TableUtils().
                            jobDetailsLogger(ib_Job1_details).
                            createRow("Job Execution Status","The job has been executed today").
                            createRow("Excution Details","It was executed more than the expected time").
                            createRow("Job Status","And the Job Status is done").
                            createTable();
                    nodeTest.log(LogStatus.PASS,msg);
                    //Assert.assertEquals(state.toLowerCase(),"done","The Expected State is not present");
                    status=true;
                }else{
                    //fail
                    msg=new TableUtils().
                            jobDetailsLogger(ib_Job1_details).
                            createRow("Job Execution Status","The job has been executed today").
                            createRow("Excution Details","It was executed more than the expected time").
                            createRow("Job Status","The Expected State is not present").
                            createTable();
                    nodeTest.log(LogStatus.FAIL,msg);
                    //Assert.assertEquals(state.toLowerCase(),"done","The Expected State is not present");
                    status=false;
                }
            }else{
                //fail
                msg=new TableUtils().
                        jobDetailsLogger(ib_Job1_details).
                        createRow("Job Execution Status","The job has been executed today").
                        createRow("Excution Details","It was executed less than the expected time").
                        createTable();
                nodeTest.log(LogStatus.FAIL,msg);
                //Assert.assertTrue((endDate.getTime()-startDate.getTime())>=expectedJobRanTime,"It was executed less than the expected time");
                status=false;
            }
        }else{
            msg=new TableUtils().
                    jobDetailsLogger(ib_Job1_details).
                    createRow("Job Execution Status","The job didn't executed today").
                    createTable();
            nodeTest.log(LogStatus.FAIL,msg);
            //Assert.assertTrue(CommonUtils.getZeroTimeDate(new Date()).compareTo(CommonUtils.getZeroTimeDate(startDate))==0,"The job didn't executed today");
            status=false;
        }
        return status;
    }
    public int getParentGoalId (Response resp,int kristalId,String jsonPath){
        List<HashMap<String,Object>> obj=from(resp.then().extract().response().asString()).get(jsonPath);
        int goalId=0;
        for (int i=0;i<obj.size();i++){
            if((obj.get(i).get("kristalId")).equals(kristalId)){
                goalId = (int)obj.get(i).get("goalId");
            }
        }
        return goalId;
    }

    /**
     * @description These Method verifies the execution of a particular Job
     * @param ib_Job1_details
     * @param nodeTest
     * @param expectedrecurringTime in Miliseconds
     * @return booloean status of the job exection
     */
    public boolean jobLoggerAndVerifierTimeSpan(JobDetailsBean ib_Job1_details,ExtentTest nodeTest,long expectedrecurringTime ){
        Date startDate=CommonUtils.formatLongDateExcludingMS(ib_Job1_details.getStartTime());
        Date endDate=CommonUtils.formatLongDateExcludingMS((long) ib_Job1_details.getEndTime());
        nodeTest.log(LogStatus.INFO,"startDate:"+startDate);
        nodeTest.log(LogStatus.INFO,"endDate:"+endDate);
        String state=ib_Job1_details.getState();
        /*long difference = endDate.getTime()-startDate.getTime();
        nodeTest.log(LogStatus.INFO,"difference:"+difference);
        nodeTest.log(LogStatus.INFO,"Compare To:"+difference/(1000*60));*/
        boolean status=true;
        String msg;

        long diff=((new Date()).getTime()-startDate.getTime());
        nodeTest.log(LogStatus.INFO,"diff:"+diff/(60*1000));
        nodeTest.log(LogStatus.INFO,"Desired Interval:"+ String.valueOf(expectedrecurringTime/(60*1000)));


        //Verifying Job is executed on Current Date or Not
        //if (CommonUtils.getZeroTimeDate(new Date()).compareTo(CommonUtils.getZeroTimeDate(startDate))==0){
        if ((diff/(60*1000))<(expectedrecurringTime/(60*1000))){
            //verifying Time Duration
            if (ib_Job1_details.getEndTime().equals(null)){
                //error
                msg=new TableUtils().
                        jobDetailsLogger(ib_Job1_details).
                        createRow("Job Execution Status","").
                        createRow("Excution Details","We did not get the end time of the Job").
                        createTable();
                nodeTest.log(LogStatus.FAIL,msg);
                //Assert.assertFalse(ib_Job1_details.getEndTime().equals(null),"We did not get the end time of the Job");
                status=false;
            }else/* if((endDate.getTime()-startDate.getTime())>=expectedrecurringTime)*/ {
                //pass
                if (state.equalsIgnoreCase("done")) {
                    //Pass
                    msg = new TableUtils().
                            jobDetailsLogger(ib_Job1_details).
                            createRow("Job Execution Status", "The job has been within last 20 min").
                            createRow("Excution Details", "It was executed " + diff / (60 * 1000) + " min back").
                            createRow("Job Status", "And the Job Status is done").
                            createTable();
                    nodeTest.log(LogStatus.PASS, msg);
                    //Assert.assertEquals(state.toLowerCase(),"done","The Expected State is not present");
                    status = true;
                } else {
                    //fail
                    msg = new TableUtils().
                            jobDetailsLogger(ib_Job1_details).
                            createRow("Job Execution Status", "The job has been within last 20 min").
                            createRow("Excution Details", "It was executed " + diff / (60 * 1000) + " min back").
                            createRow("Job Status", "The Expected State is not present").
                            createTable();
                    nodeTest.log(LogStatus.FAIL, msg);
                    //Assert.assertEquals(state.toLowerCase(),"done","The Expected State is not present");
                    status = false;
                }
              }
        }else{
            msg=new TableUtils().
                    jobDetailsLogger(ib_Job1_details).
                    createRow("Job Execution Status","The job didn't executed within last 20 min").
                    createTable();
            nodeTest.log(LogStatus.FAIL,msg);
            //Assert.assertTrue(CommonUtils.getZeroTimeDate(new Date()).compareTo(CommonUtils.getZeroTimeDate(startDate))==0,"The job didn't executed today");
            status=false;
        }
        return status;
    }

    public String getPasswordAndLoginUser(Map inputMap) throws IOException, InterruptedException {

        nodeTest.log(LogStatus.INFO,"inside getPasswordAndLoginUser ");
        int delay = Integer.valueOf(configData.get("EMAIL_READING_DELAY"));
        String token=null;
        String subject = (String) inputMap.get("subject");
        String userEmail = (String) inputMap.get("userEmail");
        SignupAndLogin signupAndLogin = new SignupAndLogin();
        String ip = (String) inputMap.get("ip");
        int numberOfEmailChecks = 100;
        nodeTest.log(LogStatus.INFO,subject+"::"+userEmail+"::"+ip);
        MailReader mailReader =null;
        try {
            //login to imap server with credentials and establish connection to imap store
            mailReader = new MailReader((String) inputMap.get("userEmailImap"), (String) inputMap.get("passwordImap"), numberOfEmailChecks);

            ArrayList<Message> messageArray = null;
            boolean mailSentReceivedStatus = false;
            int count = 1;
            int delayCount = delay / (1000 * 60);
            while (count <= delayCount) {
                nodeTest.log(LogStatus.INFO,"Trying to get user credential mail. Loop Count: " + count);
                nodeTest.log(LogStatus.INFO, "Trying to get user credential mail. Loop Count: " + count);
                ++count;
                Thread.sleep(60000);
                messageArray = mailReader.getMailsBySubjectCurrentDateAndRecipient(subject, userEmail, "INBOX");
                if (messageArray.size() != 0) {
                    mailSentReceivedStatus = true;
                }
                if (mailSentReceivedStatus) {
                    break;
                }
            }

            //Get created user credentials
            String userPassword = null, msg;
            Part message;

            //To verify whether we are getting mail with given criteria
            if (mailSentReceivedStatus) {
                //Get recent message
                message = mailReader.sortMessageByRecievedDate(messageArray).get(0);
                userPassword = MailReader.fetchRMUserCredentials(message, userEmail);
                nodeTest.log(LogStatus.INFO,userPassword);
                Assert.assertNotNull(userPassword, "userPassword ERROR:" + userPassword);
                msg = new TableUtils()
                        .createRow("Description", "Credentials found of created user. Username: " + userEmail + ",Password : " + userPassword)
                        .createTable();
                nodeTest.log(LogStatus.PASS, msg);
            } else {
                msg = new TableUtils()
                        .createRow("Message", "Searched the credentials of created user.But it was not found. Username: " + userEmail + ",Password : " + userPassword)
                        .createTable();
                nodeTest.log(LogStatus.FAIL, msg);
                Assert.assertTrue(false, "Searched the credentials of created user.But it was not found. Username: " + userEmail + ",Password : " + userPassword);
            }

            String userPasswordEncoded = new Base64Converter().encodeStringToBase64(userPassword);
            nodeTest.log(LogStatus.INFO,"encoded pwd: " + userPasswordEncoded);
            try {
                inputMap.put("APPLICATION","PORTAL");
                token = signupAndLogin.authenticateUserAndGetToken(userEmail, userPasswordEncoded, ip,inputMap);
                nodeTest.log(LogStatus.INFO,"token: " + token);
                if (token != null) {
                    msg = new TableUtils()
                            .createRow("Message", "User successfully logged in with password, fetched from Password generation mail.")
                            .createTable();
                nodeTest.log(LogStatus.PASS, msg);
                } else {
                    msg = new TableUtils()
                            .createRow("Description", "User login fail with password, fetched from Password generation mail.")
                            .createTable();
                    nodeTest.log(LogStatus.FAIL, msg);
                    Assert.assertTrue(false, "User login fail with password, fetched from Password generation mail.");
                }
            } catch (Exception e) {
                nodeTest.log(LogStatus.FAIL, "Exception Occurred while loggin in to get token with user & password.");
                e.printStackTrace();
            }
        } catch (MessagingException m) {
            nodeTest.log(LogStatus.FAIL, "Exception Occurred while getting user password and login.");


        }finally {
            if(mailReader !=null){
                try {
                    mailReader.tearDown();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }

        return token;
    }

    public Double getProcessedPlacementFee(Response resp, double amtToSubscribe)
    {
       return (Double.valueOf((float)CommonUtils.getNodeValueFromRespAsObject(resp,"placementFeePercent"))*amtToSubscribe)/100;
    }

    public int minimumAmountOfSubscription (Response resp){
        int minimumunit;
        Double unitNav=Double.valueOf((float)CommonUtils.getNodeValueFromRespAsObject(resp,"unitNav"));
        Double minInvestment=(Double.valueOf((float)CommonUtils.getNodeValueFromRespAsObject(resp,"minInvestment")));
        Double minvalue=minInvestment/unitNav;
        minimumunit= CommonUtils.getCeilValue(minvalue);
        return minimumunit;
    }

    public double minimumInvestmentValueForTheKristal (Response resp){
        return Double.valueOf((float)CommonUtils.getNodeValueFromRespAsObject(resp,"minInvestMentForSubscription"));
    }

    public double getUnitNavOfKristal (Response resp){
        return Double.valueOf((float)CommonUtils.getNodeValueFromRespAsObject(resp,"nav"));
    }

    public float minimumUnitsOfSubscriptionInDecimals (Response resp) throws JSONException {
        Double minimumunit;
        String val = resp.then().extract().response().asString();
        boolean isPresent = val.contains("noOfDecimalsAllowed");
        Double unitNav=Double.valueOf((float)CommonUtils.getNodeValueFromRespAsObject(resp,"nav"));
        Double minInvestment=(Double.valueOf((float)CommonUtils.getNodeValueFromRespAsObject(resp,"minInvestMentForSubscription")));
        int noOfDecimalAllowed = Integer.valueOf((int)CommonUtils.getNodeValueFromRespAsObject(resp,"kristalMetadata.noOfDecimalsAllowed"));
        Double minvalue=minInvestment/unitNav;
        if(minvalue % 1 == 0)
        {
            minimumunit = minvalue;
            return (float) minimumunit.doubleValue();
        }
        minimumunit= Double.valueOf(CommonUtils.roundOffToDecimals(minvalue, noOfDecimalAllowed));
        return (float)minimumunit.doubleValue();
    }
    public int minimumNumberOfSIPSubscription (Response resp){
        int minimumunit;
        Double unitNav=Double.valueOf((float)CommonUtils.getNodeValueFromRespAsObject(resp,"unitNav"));
        Double residualCashMultiplier= 1.0 + (Double.valueOf((float)CommonUtils.getNodeValueFromRespAsObject(resp,"residualCashMultiplier"))/100);
        Double minimumSubscription=Double.valueOf((float)CommonUtils.getNodeValueFromRespAsObject(resp,"minInvestment"));
        //minimumunit= Integer.valueOf((int) Math.ceil(unitNav * residualCashMultiplier));
        minimumunit =Integer.valueOf((int) Math.ceil(minimumSubscription/unitNav));
        //minimumunit= CommonUtils.getCeilValue(unitNav,residualCashMultiplier,minimumSubscription);
        return minimumunit;
    }

    public boolean jobLoggerAndVerifier_onlyCurrentDate(JobDetailsBean ib_Job1_details, ExtentTest nodeTest) {
        Date startDate=CommonUtils.formatLongDateExcludingMS(ib_Job1_details.getStartTime());
        Date endDate=CommonUtils.formatLongDateExcludingMS((long) ib_Job1_details.getEndTime());
        nodeTest.log(LogStatus.INFO,"startDate:"+startDate);
        nodeTest.log(LogStatus.INFO,"endDate:"+endDate);
        String state=ib_Job1_details.getState();
        long difference = endDate.getTime()-startDate.getTime();
        nodeTest.log(LogStatus.INFO,"difference:"+difference);
        nodeTest.log(LogStatus.INFO,"Compare To:"+difference/(1000*60));
        boolean status=true;
        String msg;
        //Verifying Job is executed on Current Date or Not
        if (CommonUtils.getZeroTimeDate(new Date()).compareTo(CommonUtils.getZeroTimeDate(startDate))==0){
            msg=new TableUtils().
                    jobDetailsLogger(ib_Job1_details).
                    createRow("Job Execution Status","The job has been executed today").
                    createRow("Job Status","And the Job Status is done").
                    createTable();
            nodeTest.log(LogStatus.PASS,msg);
            //Assert.assertEquals(state.toLowerCase(),"done","The Expected State is not present");
            status=true;
        }else{
            msg=new TableUtils().
                    jobDetailsLogger(ib_Job1_details).
                    createRow("Job Execution Status","The job didn't executed today").
                    createTable();
            nodeTest.log(LogStatus.FAIL,msg);

            status=false;
        }
        return status;
    }

    public static String readMailAndGetOTPForMailVerification(Map inputMap) throws MessagingException, IOException, InterruptedException {

        String OTP=null; String msg; Part message;
        String subject = (String) inputMap.get("verifySubject");
        String userEmail = (String) inputMap.get("userEmail");

        MailReader mailReader = new MailReader((String) inputMap.get("userEmailImap"), (String) inputMap.get("passwordImap"),(int) inputMap.get("nuberOfEmailToCheck"));

        ArrayList<Message> messageArray = null;
        boolean mailSentReceivedStatus=false;
        int count=0;
        while(count<=3){
            ++count;
            Thread.sleep(60000);
            messageArray = mailReader.getMailsBySubjectCurrentDateAndRecipient(subject,userEmail,"OTP");
            if(messageArray.size()!=0){
                mailSentReceivedStatus = true;
            }
            if (mailSentReceivedStatus){
                break;
            }
        }

        //Fetching OTP for mail verification
        if(mailSentReceivedStatus){
            message = mailReader.sortMessageByRecievedDate(messageArray).get(0);
            mailReader.isRecentMessage(message);
            HashMap<String,Object> types= mailReader.getMessageMimeTypes(message);
            OTP=mailReader.fetchOtpNew(types);
            Assert.assertNotNull(OTP,"OTP ERROR:"+OTP);
            msg=new TableUtils()
                    .createRow("Description","OTP found for the account verification. OTP : "+OTP)
                    .createTable();
            nodeTest.log(LogStatus.PASS,msg);
        }else{
            msg=new TableUtils()
                    .createRow("Message", "Searched the OTP for the account verification. But it was not found. OTP: "+OTP)
                    .createTable();
            nodeTest.log(LogStatus.FAIL, msg);
            Assert.assertTrue(false,"Searched the OTP for the account verification. But it was not found. OTP: "+OTP);
        }
        return OTP;
    }


    public HashMap<Integer,Integer> getRiskFromGetApprovedResponse(Response resp){
        HashMap<Integer,Integer> risks = new HashMap<>();
        ArrayList<ArrayList> list = from(resp.then().extract().response().asString()).get("category.kristals");
        ArrayList<ArrayList> list1;
        ArrayList<HashMap> list2;
        for(int i=0; i<list.size(); i++) {
            list1 = list.get(i);
            for (int j = 0; j < list1.size(); j++) {
                list2 = list1.get(j);
                for (int k = 0; k < list2.size(); k++) {
                    risks.put((int)list2.get(k).get("kristalId"),(int) list2.get(k).get("sophistication"));
                }
            }
        }
        return risks;
    }

    public ArrayList<KristalDetailsFromDBSophistication> getALLKristalDetailsWithSophistication (Response resp){
        ArrayList<KristalDetailsFromDBSophistication>  kristalDetailsFromDBList= new ArrayList<>();
        List<HashMap<String,Object>> obj=from(resp.then().extract().response().asString()).get();
        nodeTest.log(LogStatus.INFO,"List Size:"+obj.size());
        KristalDetailsFromDBSophistication kristalDetailsFromDB;
        ArrayList<Object> dateArray;
        for (int i=0;i<obj.size();i++){
            kristalDetailsFromDB = new KristalDetailsFromDBSophistication();
            kristalDetailsFromDB.setKristalId((int)obj.get(i).get("kristalId"));
            kristalDetailsFromDB.setKristalName((String) obj.get(i).get("kristalName"));
            kristalDetailsFromDB.setNavWithoutAccrued((float)obj.get(i).get("navWithoutAccrued"));
            dateArray=(ArrayList) obj.get(i).get("lastUpdateDate");
            kristalDetailsFromDB.setLastUpdatedYear((int)dateArray.get(0));
            kristalDetailsFromDB.setLastUpdatedMonth((int)dateArray.get(1));
            kristalDetailsFromDB.setLastUpdatedDay((int)dateArray.get(2));
            dateArray=null;
            kristalDetailsFromDB.setKristalType((String) obj.get(i).get("kristalType"));
            kristalDetailsFromDB.setRisk((String) obj.get(i).get("kristalRiskRating"));
            kristalDetailsFromDB.setSophistication((int) obj.get(i).get("sophistication"));
            kristalDetailsFromDBList.add(kristalDetailsFromDB);
            kristalDetailsFromDB=null;
        }
        return kristalDetailsFromDBList;
    }

    public Map<String, List<String>> UITabsvalidation(String kycstatus){

        Map<String,List<String>> mapp = new HashMap<>();

        //check these tabs with customer properties
        List<String> list=new ArrayList<>();
        list.add("INVEST_NOW");
        list.add("EXPLORE");
//        list.add("IDEAS");
        list.add("BOOKMARKS");
        list.add("PROFILE");

        List<String> list1=new ArrayList<>();
        list1.add("DASHBOARD");
        list1.add("EXPLORE");
//        list1.add("IDEAS");
        list1.add("BOOKMARKS");
        list1.add("PROFILE");
        list1.add("REPORTS");
        list1.add("MESSAGE_CENTER");
        switch (kycstatus){
            case "DRAFT":
                mapp.put("DRAFT",list);
                break;
            case "APPROVED":
                mapp.put("APPROVED",list1);
                break;
            case "USER_APPROVED":
                mapp.put("USER_APPROVED",list1);
                break;
        }
        return mapp;
    }

    public List<SearchFilterNames> listofsfnBasedonCountryandBillingtype(String c, String b){
        List<SearchFilterNames> li = new ArrayList<>();
        if(c.equalsIgnoreCase(CountryTypes.IND.toString()) && b.equalsIgnoreCase(BillingTypes.Digital.toString())){
                li.add(Portfolios);
                li.add(Featured);
                li.add(ETFs);
            return li;
        }else if(c.equalsIgnoreCase(CountryTypes.IND.toString()) && b.equalsIgnoreCase(BillingTypes.PrivateWealth.toString())){
            li.add(Portfolios); li.add(Featured); li.add(ETFs); li.add(Funds);li.add(MutualFunds); li.add(Stocks); li.add(Private_Markets);
            return li;
        }else if(!c.equalsIgnoreCase(CountryTypes.IND.toString()) && b.equalsIgnoreCase(BillingTypes.Digital.toString())){
            li.add(Portfolios); li.add(Featured); li.add(ETFs);
            return li;
        }else{
            li.add(Featured); li.add(ETFs); li.add(Funds); li.add(Stocks); li.add(Private_Markets); li.add(Elon); li.add(MutualFunds); li.add(Portfolios);
            return li;
        }
    }

    public void authenticateAndUpdateTestProfile(Map<String,Object> inputMap) throws Exception {

        String emailRM,passwordRM;
        SignupAndLogin signupAndLogin=new SignupAndLogin();
        RmApp rm = new RmApp();
        KYC kyc =new KYC();
        String email = String.valueOf(inputMap.get("email"));
        String password = String.valueOf(inputMap.get("password"));
        String ip = String.valueOf(inputMap.get("ip"));
        if(isProduction) {
             emailRM = configData.get("RmEmailIdProd");
             passwordRM = new Base64Converter().encodeStringToBase64(configData.get("RmPasswordProd"));
        }else{
            emailRM = configData.get("HHRmEmailId");
            passwordRM = new Base64Converter().encodeStringToBase64(configData.get("HHRmPassword"));
        }
        //Authenticate above created user
        inputMap.put("APPLICATION","PORTAL");
        String token = signupAndLogin.authenticateUserAndGetToken(email, password, ip,inputMap);
        Assert.assertNotNull(token,"Token not generated");
        inputMap.put("token",token);

        int userid = kyc.getUserId_V1(inputMap);
        String euid = Integer.toString(userid);
        inputMap.put("ExternalUser-Id", euid);
        inputMap.put("externalUserId",euid);

        //Authenticate RM and Get Token
        inputMap.put("APPLICATION","RMAPP");
        String token_rm = signupAndLogin.authenticateUserAndGetToken(emailRM, passwordRM, ip,inputMap);
        Assert.assertNotNull(token_rm, "Token not generated");
        inputMap.put("token_rm", token_rm);
        inputMap.put("token", token_rm);

        //can be done to respective partners only --> eg. cannot assign KRIS user to KRISDEMO or CP partners and vice versa

        rm.assingUser(inputMap);
        Thread.sleep(5000);
        inputMap.put("userProfile","TEST");
        kyc.updateUserProfile(inputMap);

        inputMap.put("APPLICATION","PORTAL");
        inputMap.put("token",token);
    }

    public void addAccountwithcash(String currency,int wmid,int pacid, String externalaid,Map<String,Object> inputMap, String token_r) throws InterruptedException {

        ResponsibleOfficer ro= new ResponsibleOfficer();
        DashboardAPIs dashboardAPIs= new DashboardAPIs();

        Thread.sleep(10000);
        inputMap.put("currency",currency);
        inputMap.put("wealthManager",wmid);
        inputMap.put("parentAccountId",pacid);
        inputMap.put("externalAccountId",externalaid);
        Response resp = ro.addAccount(token_r, inputMap);
        resp.then().statusCode(HttpStatus.SC_OK);
        String accountId = String.valueOf(CommonUtils.getNodeValueFromRespAsObject(resp, "accountId"));
        nodeTest.log(LogStatus.INFO,"account id"+accountId);
        inputMap.put("accountId",accountId);
        nodeTest.log(LogStatus.INFO,"adding cash to account of wealth manager: "+wmid);
        dashboardAPIs.addCashStg(inputMap);
    }

}
