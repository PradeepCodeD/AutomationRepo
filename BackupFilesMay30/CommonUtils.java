package com.kristal.core.utils;

import com.kristal.core.api.BaseClass;
import com.relevantcodes.extentreports.LogStatus;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.Assert;

import java.io.*;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

import static io.restassured.path.json.JsonPath.from;

public class CommonUtils extends BaseClass {
    private String str;
    static boolean status;
    static float tolerance;
    private static DecimalFormat decimalPlaces;

    /**
     * Method to get public ip using api
     * @return public ip
     */
    public static String getPublicIp() {
        String ip = "Was unable to get the IP of the executing system";
        //String ipRes = given().get("https://api.ipify.org/?format=json").then().assertThat().statusCode(200).and().extract().response().asString();
        try {
            InetAddress ipAddr = InetAddress.getLocalHost();
            ip=ipAddr.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        /*return from(ipRes).get("ip");*/
        if (ip.equalsIgnoreCase("Was unable to get the IP of the executing system"))
            Assert.assertNotEquals(ip,"Was unable to get the IP of the executing system");
        return ip;
    }


    public static double createRandomInteger(int n){
        double randomNumber = Math.floor(Math.random() * (9 * Math.pow(10, n - 1))) + Math.pow(10, (n - 1));

        return randomNumber;

    }

    public static String getNodeValueFromResp(Response resp, String nodeName){
        return  from(resp.then().extract().response().asString()).get(nodeName);
    }

    public static Long convertStringToTimestamp(String strDate) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate date = LocalDate.parse(strDate, df);
        ZonedDateTime parsed = date.atStartOfDay(ZoneId.systemDefault());
        LocalDateTime dateTime = parsed.toLocalDateTime();
        Instant ts = dateTime.atZone(ZoneId.systemDefault()).toInstant();
        long timeStampDate = ts.toEpochMilli();
        return timeStampDate;
    }

    public static void clearErrorLogFile(String fileName)  {
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
            bw.write("");
            bw.flush();
            bw.close();
        }catch(IOException ioe){
            // You should really do something more appropriate here
            ioe.printStackTrace();
        }
    }

    public static Object getNodeValueFromRespAsObject(Response resp,String nodeName){
        return  from(resp.then().extract().response().asString()).get(nodeName);
    }

    public static String readFileContents(String fileName){
        StringBuffer str=new StringBuffer("");
        try{
            FileInputStream fstream = new FileInputStream(fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                str.append(strLine+"\n");
            }

            fstream.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        return  str.toString();
    }

    public static boolean containsRequiredLang(String s1, String s) {

        if (s1.toLowerCase().equals("english")) {
            for (int i = 0; i < s.length(); ) {
                int codepoint = s.codePointAt(i);
                i += Character.charCount(codepoint);
                if (Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN) {
                    return true;
                }
            }

        } else if (s1.toLowerCase().equals("chinese")) {
            for (int i = 0; i < s.length(); ) {
                int codepoint = s.codePointAt(i);                i += Character.charCount(codepoint);
                if (Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void appendUsingFileWriter(String fileName,String text) {
        File file = new File(fileName);
        FileWriter fr = null;
        try {
            // Below constructor argument decides whether to append or override
            fr = new FileWriter(file, true);
            fr.write("\n"+text+"\n");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getCurrentDate(String format){
        String date = null;
        DateFormat dateFormat = new SimpleDateFormat(format);
        date = dateFormat.format(new Date());

        return  date;
    }

    public static String getLastDayDate(String format,int dateLag){
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.DATE,-dateLag);
        String dateRequired=new SimpleDateFormat(format).format(cal.getTime());
        return  dateRequired;
    }

    public static long getCurrentTimeInMs(){
        Date now = new Date();
        long ut3 = now.getTime() / 1000L;
        return ut3 * 1000;
    }

    public static long getLastDayofTheMonth(){
        LocalDate date = LocalDate.now();
        LocalDate lastOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());
        nodeTest.log(LogStatus.INFO,lastOfMonth.toString());
        Instant instant = lastOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant();
        long timeInMillis = instant.toEpochMilli();
        nodeTest.log(LogStatus.INFO,String.valueOf(timeInMillis));
        return  timeInMillis;
    }

    public static long getCurrentTimewithYearInMs(int yrs){
        LocalDate nextYear = LocalDate.now().plusYears(yrs);
        nodeTest.log(LogStatus.INFO,nextYear.toString());
        Instant instant = nextYear.atStartOfDay(ZoneId.systemDefault()).toInstant();
        long timeInMillis = instant.toEpochMilli();
        nodeTest.log(LogStatus.INFO,String.valueOf(timeInMillis));
        return  timeInMillis;
    }

    public static long getPastTimewithYearInMs(int yrs){
        LocalDate previousYear = LocalDate.now().minusYears(yrs);
        nodeTest.log(LogStatus.INFO,previousYear.toString());
        Instant instant = previousYear.atStartOfDay(ZoneId.systemDefault()).toInstant();
        long timeInMillis = instant.toEpochMilli();
        nodeTest.log(LogStatus.INFO,String.valueOf(timeInMillis));
        return  timeInMillis;
    }

    public static String dateFormatter(String pattern, String format,String dateFromResp) throws ParseException {

        DateFormat formatter = new SimpleDateFormat(pattern);
        Date date = formatter.parse(dateFromResp);
        formatter = new SimpleDateFormat(format);
        String formatedDate = formatter.format(date);

        return  formatedDate;
    }

    public static Date formatLongDate(long dateInLong){
         return  new Date(dateInLong*1000);
    }

    public static Date getCurrentMonthReportGenerationDate(int dayOfReportGeneration){
        Date date=new Date();
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);

        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        calendar.set(year,month,dayOfReportGeneration);

        date=calendar.getTime();

        return  getZeroTimeDate(date);
    }

    public static Date getZeroTimeDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        date = calendar.getTime();
        return date;
    }

    public static Date formatLongDateExcludingMS(long dateInLong){
        return  new Date(dateInLong);
    }

    public static String timeDiffernceCalculator(Date endDate,Date startDate){
        long diffence=endDate.getTime()-startDate.getTime();
        String str=DurationFormatUtils.formatDuration(diffence, "HH:mm:ss");
        return str;
    }

    public static long dateAfterThreeYears(){

        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        instance.add(Calendar.YEAR, +3);
        long millisSinceEpoch = instance.getTimeInMillis();
        return millisSinceEpoch;
    }

//    public static long datePastThreeDays(){
//
//        Calendar instance = Calendar.getInstance();
//        instance.setTime(new Date());
//        if((new Date()).getDate()<4){
//            instance.add(Calendar.DATE, 0);
//        }else{
//            instance.add(Calendar.DATE, -3);
//        }
//        long millisSinceEpoch = instance.getTimeInMillis();
//        return millisSinceEpoch;
//    }

    public static DateTime getDateInGivenTimeZone(Date date,String timeZone){

        DateTime dt = new DateTime(date);
        DateTime dtInGivenTimezone = dt.withZone(DateTimeZone.forTimeZone(TimeZone.getTimeZone(timeZone)));

        return dtInGivenTimezone;
    }



    public static void main(String[] args) throws IOException {
/*        Date date=formatLongDate(1573678891);
        nodeTest.log(LogStatus.INFO,date);

        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);

        int day = calendar.get(Calendar.DAY_OF_MONTH); // 6
        int Month = calendar.get(Calendar.MONTH)+1; // 17
        int Year = calendar.get(Calendar.YEAR); //169
        nodeTest.log(LogStatus.INFO,day);
        nodeTest.log(LogStatus.INFO,Month);
        nodeTest.log(LogStatus.INFO,Year);*/

        //nodeTest.log(LogStatus.INFO,getCurrentMonthReportGenerationDate(14));
        //dateAfterThreeYears();
//        CommonUtils c=new CommonUtils();
//     nodeTest.log(LogStatus.INFO, c.getCeilValue(116.6448,1.0,1.015));
//        writeCSV();

        nodeTest.log(LogStatus.INFO,getPublicIp());
    }
    public static int getCeilValue(Double ...a)
    {
        Double c=1.0;
        for (Double i: a){
            c=c*i;
        }
        return Integer.valueOf((int) Math.ceil(c));
    }

    /**
     *
     * @param resp
     * @param keyForSearching
     * @param attributeToGet
     * @param keyForSearchValue
     * @return
     */
    public static String getNodeValueFromRespRespectiveOfOtherNode(Response resp,String attributeToGet,String keyForSearching,int keyForSearchValue){
        List<HashMap> userList= from(resp.then().extract().response().asString()).get("$");
        String key1Value = "";
        for(int i=0; i<userList.size(); i++){
            if(Integer.valueOf((String) userList.get(i).get(keyForSearching)).equals(keyForSearchValue)){
                key1Value=String.valueOf(userList.get(i).get(attributeToGet));
            }
        }
        return key1Value;
    }

    public static List<Object> getArrayFromResponse(Response resp, String path){
        List<Object> jsonResponse = resp.jsonPath().getList(path);
        return  jsonResponse;
    }

    public static <T> List<T> getTypedListFromResponse(Response resp, String path, Class<T> aClass){
        List<T> jsonResponse = resp.jsonPath().getList(path,aClass);
        return  jsonResponse;
    }

    public static String getNodeValueFromRespRespectiveOfOtherNodestring(Response resp,String attributeToGet,String keyForSearching,String keyForSearchValue){
        List<HashMap> userList= from(resp.then().extract().response().asString()).get("$");
        String key1Value = "";
        for(int i=0; i<userList.size(); i++){
            if(Integer.valueOf((String) userList.get(i).get(keyForSearching)).equals(keyForSearchValue)){
                key1Value=String.valueOf(userList.get(i).get(attributeToGet));
            }
        }
        return key1Value;
    }

    public static Date getZeroTimeOfATimezone(Date date,String timezone){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

        //Here you say to java the initial timezone. This is the secret
        sdf.setTimeZone(TimeZone.getTimeZone(timezone));
        //Will print in UTC
        return getZeroTimeDate(calendar.getTime());

        /*//Here you set to your timezone
        sdf.setTimeZone(TimeZone.getDefault());
        //Will print on your default Timezone
        nodeTest.log(LogStatus.INFO,sdf.format(calendar.getTime()));*/
    }

    public static void writeCSV(List<String> rows , String filePath) {

        File file = new File(filePath);
        try (PrintWriter writer = new PrintWriter(file)) {

            StringBuilder sb = new StringBuilder();
            sb.append("client_id");
            for (String rowData : rows)
        {
            sb.append("\n");
            sb.append(rowData);
        }
            writer.write(sb.toString());
            writer.flush();

        } catch (FileNotFoundException e) {
            nodeTest.log(LogStatus.INFO,e.getMessage());
        }
    }

    public static String randomSequence(){
        return Integer.toString(new Random().nextInt(10000))+randomChar()+randomChar();
    }
    public static char randomChar(){
        String alphabet = "0123456789abcdefghijklmnopqrstuvwxyz";
        Random rnd = new Random();
        char c = alphabet.charAt(rnd.nextInt(alphabet.length()));
        return c;

    }

    public static boolean  changeVerificationInPercentage(float referenceValue,float actualvalue, Map inputMap){

        if (inputMap.get("tolerance") == null) {
            tolerance = Float.valueOf(configData.get("TOLERANCE"));
        }else{
            tolerance = Float.valueOf((Float) inputMap.get("tolerance"));
        }

        float percentageDiff;
        String comparedField = String.valueOf(inputMap.get("comparedField"));
        String msg=null;
        if(referenceValue!= 0){
            percentageDiff = (((actualvalue-referenceValue)/referenceValue)*100);
            if (percentageDiff > -tolerance && percentageDiff < tolerance){
                msg = new TableUtils().
                        createRow("Message",comparedField +" change is with in range. ChangeInPercentage: "+percentageDiff+"%").
                        createRow("actualvalue UI ",String.valueOf(referenceValue)).
                        createRow("actualvalue API", String.valueOf(actualvalue)).
                        createTable();
                nodeTest.log(LogStatus.PASS,msg);
                return true;
            }else {
                msg = new TableUtils().
                        createRow("Message",comparedField+ " change is out of range.").
                        createRow("Field Reference Value",String.valueOf(referenceValue)).
                        createRow("Field Actual Value",String.valueOf(actualvalue)).
                        createRow("AcceptedChange",-tolerance+"% to +"+tolerance+"%").
                        createRow("ChangeInValue",percentageDiff+"%").
                        createTable();
                nodeTest.log(LogStatus.FAIL,msg);
                return false;
// Assert.fail(comparedField +" change is out of range. ChangeInPercentage: "+percentageDiff+"%");
            }
        }else if(referenceValue == 0 && actualvalue == 0) {
            percentageDiff = 0;
            msg = new TableUtils().
                    createRow("Message",comparedField +" change is with in range. ChangeInValue: "+percentageDiff).
                    createTable();
            nodeTest.log(LogStatus.PASS,msg);
            return true;
        }else {
            msg = new TableUtils().
                    createRow("Message",comparedField +" change is not in range").
                    createTable();
            nodeTest.log(LogStatus.FAIL,msg);
            return false;
// Assert.fail("The change in value is out of range.");
        }
    }

    public static int  toleranceBasedOnSophistication(int sophistication){
        int tolerance = 3;
        if(sophistication <=3) {
            tolerance=2;
        }else if(sophistication == 4 ||sophistication == 5 ){
            tolerance=5;
        }else if(sophistication == 6 || sophistication == 7){
            tolerance=8;
        }else if(sophistication >=8){
            tolerance=16;
        }

        return tolerance;
    }

    public static float roundOffToDecimals(Double number, int decimalPlacesKey)
    {
        String formatSetter = "0.0";
        if(decimalPlacesKey > 1)
        {
            formatSetter = formatSetter.concat("0".repeat(decimalPlacesKey-1));
        }
        decimalPlaces = new DecimalFormat(formatSetter);
        decimalPlaces.setRoundingMode(RoundingMode.UP);
        return Float.parseFloat(decimalPlaces.format(number));

    }


    public static String convertFiletoBase64String(String filePath) throws IOException {

        byte[] fileContent = FileUtils.readFileToByteArray(new File(filePath));
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        return encodedString;
    }

    public static String convertBase64toFile(String encodedString, String outputFileName) throws IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        FileUtils.writeByteArrayToFile(new File(outputFileName), decodedBytes);
        return outputFileName;
    }
}
