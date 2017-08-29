package com.viewlift.ccavenue.screens;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.viewlift.R;
import com.viewlift.ccavenue.dto.CardTypeDTO;
import com.viewlift.ccavenue.dto.EMIOptionDTO;
import com.viewlift.ccavenue.dto.PaymentOptionDTO;
import com.viewlift.ccavenue.utility.AvenuesParams;
import com.viewlift.ccavenue.utility.Constants;
import com.viewlift.ccavenue.utility.ServiceHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PaymentOptionsActivity extends AppCompatActivity {
    Intent initialScreen;
    Map<String,ArrayList<CardTypeDTO>> cardsList = new LinkedHashMap<String,ArrayList<CardTypeDTO>>();
    ArrayList<PaymentOptionDTO> payOptionList = new ArrayList<PaymentOptionDTO>();
    ArrayList<EMIOptionDTO> emiOptionList = new ArrayList<EMIOptionDTO>();
    private JSONObject jsonRespObj;
    private ProgressDialog pDialog;
    private Map<String,String> paymentOptions = new LinkedHashMap<String,String>();
    String selectedPaymentOption;
    CardTypeDTO selectedCardType;
    GetData getDataAsyncTask = null ;
    String orderID = "" ;
    String accessCode = "" ;
    String cancelRedirectURL = "" ;
    String merchantID = "" ;
    TextView id_tv_text_payment ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_options);
        initialScreen = getIntent();
        id_tv_text_payment = (TextView) findViewById(R.id.id_tv_text_payment) ;
        id_tv_text_payment.setText("First Payment Rs. " + initialScreen.getStringExtra(AvenuesParams.AMOUNT).toString().trim() +
                " (+tax if req'd) on MM/DD");
        getDataAsyncTask = new GetData() ;
        getDataAsyncTask.execute() ;
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(PaymentOptionsActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            getRSAKey() ;
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            List<NameValuePair> vParams = new ArrayList<NameValuePair>();
            vParams.add(new BasicNameValuePair(AvenuesParams.COMMAND,"getJsonDataVault"));
            try {
                vParams.add(new BasicNameValuePair(AvenuesParams.ACCESS_CODE, accessCode));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            try {
                vParams.add(new BasicNameValuePair(AvenuesParams.CURRENCY,initialScreen.getStringExtra(AvenuesParams.CURRENCY).toString().trim()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            try {
                vParams.add(new BasicNameValuePair(AvenuesParams.AMOUNT,initialScreen.getStringExtra(AvenuesParams.AMOUNT).toString().trim()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            //if (getIntent().getBooleanExtra("renewable",false)) {
                vParams.add(new BasicNameValuePair("payment_option","OPTCRDC"));
                //params.append(ServiceUtility.addToPostParams("payment_option","OPTCRDC")) ;
            //}

            //vParams.add(new BasicNameValuePair(AvenuesParams.CUSTOMER_IDENTIFIER,initialScreen.getStringExtra(AvenuesParams.CUSTOMER_IDENTIFIER).toString().trim()));

            String vJsonStr = sh.makeServiceCall(Constants.CCAVENUE_JSON_URL, ServiceHandler.POST, vParams);

            Log.d("Response: ", "> " + vJsonStr);

            if (vJsonStr!=null && !vJsonStr.equals("")) {
                try {
                    jsonRespObj = new JSONObject(vJsonStr);
                    if(jsonRespObj!=null){
                        if(jsonRespObj.getString("payOptions")!=null){
                            JSONArray vPayOptsArr = new JSONArray(jsonRespObj.getString("payOptions"));
                            for(int i=0;i<vPayOptsArr.length();i++){
                                JSONObject vPaymentOption = vPayOptsArr.getJSONObject(i);
                                if(vPaymentOption.getString("payOpt").equals("OPTIVRS")) continue;
                                payOptionList.add(new PaymentOptionDTO(vPaymentOption.getString("payOpt"),vPaymentOption.getString("payOptDesc").toString()));//Add payment option only if it includes any card
                                paymentOptions.put(vPaymentOption.getString("payOpt"),vPaymentOption.getString("payOptDesc"));
                                try{
                                    JSONArray vCardArr = new JSONArray(vPaymentOption.getString("cardsList"));
                                    if(vCardArr.length()>0){
                                        cardsList.put(vPaymentOption.getString("payOpt"), new ArrayList<CardTypeDTO>()); //Add a new Arraylist
                                        for(int j=0;j<vCardArr.length();j++){
                                            JSONObject card = vCardArr.getJSONObject(j);
                                            try{
                                                CardTypeDTO cardTypeDTO = new CardTypeDTO();
                                                cardTypeDTO.setCardName(card.getString("cardName"));
                                                cardTypeDTO.setCardType(card.getString("cardType"));
                                                cardTypeDTO.setPayOptType(card.getString("payOptType"));
                                                cardTypeDTO.setDataAcceptedAt(card.getString("dataAcceptedAt"));
                                                cardTypeDTO.setStatus(card.getString("status"));
                                                cardsList.get(vPaymentOption.getString("payOpt")).add(cardTypeDTO);
                                            }catch (Exception e) { Log.e("ServiceHandler", "Error parsing cardType",e); }
                                        }
                                    }
                                }catch (Exception e) { Log.e("ServiceHandler", "Error parsing payment option",e); }
                            }
                        }
                        if((jsonRespObj.getString("EmiBanks")!=null && jsonRespObj.getString("EmiBanks").length()>0) &&
                                (jsonRespObj.getString("EmiPlans")!=null && jsonRespObj.getString("EmiPlans").length()>0)){
                            paymentOptions.put("OPTEMI","Credit Card EMI");
                            payOptionList.add(new PaymentOptionDTO("OPTEMI", "Credit Card EMI"));
                        }
                    }
                } catch (JSONException e) {
                    Log.e("ServiceHandler", "Error fetching data from server",e);
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

//            try{
//                // bind adapter to spinner
//                final Spinner payOpt = (Spinner) findViewById(R.id.payopt);
//                PayOptAdapter payOptAdapter = new PayOptAdapter(BillingShippingActivity.this, android.R.layout.simple_spinner_item, payOptionList);
//                payOpt.setAdapter(payOptAdapter);
//
//                //set a listener for selected items in the spinner
//                payOpt.setOnItemSelectedListener(new OnItemSelectedListener(){
//                    @Override
//                    public void onItemSelected(AdapterView parent, View view, int position, long id) {
//                        ((LinearLayout) findViewById(R.id.vCardCVVCont)).setVisibility(View.GONE);
//
//                        selectedPaymentOption = payOptionList.get(position).getPayOptId();
//                        String vCustPayments = null;
//                        try{
//                            vCustPayments = jsonRespObj.getString("CustPayments");
//                        }catch (Exception e) {}
//
//                        if(counter!=0 || vCustPayments==null){
//                            LinearLayout ll = (LinearLayout) findViewById(R.id.cardDetails);
//                            if(selectedPaymentOption.equals("OPTDBCRD") ||
//                                    selectedPaymentOption.equals("OPTCRDC")){
//                                ll.setVisibility(View.VISIBLE);
//                            }else{
//                                ll.setVisibility(View.GONE);
//                            }
//                        }
//
//
//                        if(selectedPaymentOption.equals("OPTEMI")){
//                            ((LinearLayout) findViewById(R.id.cardDetails)).setVisibility(View.VISIBLE);
//                            ((CheckBox) findViewById(R.id.saveCard)).setVisibility(View.GONE);
//                            if(((LinearLayout) findViewById(R.id.vaultCont))!=null)
//                                ((LinearLayout) findViewById(R.id.vaultCont)).setVisibility(View.GONE);
//
//                            ((Spinner) findViewById(R.id.cardtype)).setVisibility(View.GONE);
//                            ((TextView) findViewById(R.id.cardtypetv)).setVisibility(View.GONE);
//
//                            ((LinearLayout) findViewById(R.id.emiDetails)).removeAllViews();
//
//                            ((LinearLayout) findViewById(R.id.emiOptions)).setVisibility(View.VISIBLE);
//                            try{
//                                JSONArray vEmiBankArr = new JSONArray(jsonRespObj.getString("EmiBanks"));
//                                for(int i=0;i<vEmiBankArr.length();i++){
//                                    JSONObject vEmiBank = vEmiBankArr.getJSONObject(i);
//
//                                    EMIOptionDTO vEmiOptionDTO = new EMIOptionDTO();
//                                    vEmiOptionDTO.setGtwId(vEmiBank.getString("gtwId"));
//                                    vEmiOptionDTO.setGtwName(vEmiBank.getString("gtwName"));
//                                    vEmiOptionDTO.setSubventionPaidBy(vEmiBank.getString("subventionPaidBy"));
//                                    vEmiOptionDTO.setTenureMonths(vEmiBank.getString("tenureMonths"));
//                                    vEmiOptionDTO.setProcessingFeeFlat(vEmiBank.getString("processingFeeFlat"));
//                                    vEmiOptionDTO.setProcessingFeePercent(vEmiBank.getString("processingFeePercent"));
//                                    vEmiOptionDTO.setCcAvenueFeeFlat(vEmiBank.getString("ccAvenueFeeFlat"));
//                                    vEmiOptionDTO.setCcAvenueFeePercent(vEmiBank.getString("ccAvenueFeePercent"));
//                                    vEmiOptionDTO.setTenureData(vEmiBank.getString("tenureData"));
//                                    vEmiOptionDTO.setPlanId(vEmiBank.getString("planId"));
//                                    vEmiOptionDTO.setAccountCurrName(vEmiBank.getString("accountCurrName"));
//                                    vEmiOptionDTO.setEmiPlanId(vEmiBank.getString("emiPlanId"));
//                                    vEmiOptionDTO.setMidProcesses(vEmiBank.getString("midProcesses"));
//                                    vEmiOptionDTO.setBins(vEmiBank.getString("BINs"));
//
//                                    JSONArray vEmiPlanArr = new JSONArray(jsonRespObj.getString("EmiPlans"));
//                                    for(int j=0;j<vEmiPlanArr.length();j++){
//                                        JSONObject vEmiPlan = vEmiPlanArr.getJSONObject(j);
//
//                                        if(vEmiBank.getString("planId").equals(vEmiPlan.getString("planId"))){
//                                            EMIPlansDTO vEmiPlansDTO = new EMIPlansDTO();
//                                            vEmiPlansDTO.setGtwId(vEmiPlan.getString("gtwId"));
//                                            vEmiPlansDTO.setGtwName(vEmiPlan.getString("gtwName"));
//                                            vEmiPlansDTO.setSubventionPaidBy(vEmiBank.getString("subventionPaidBy"));
//                                            vEmiPlansDTO.setTenureMonths(vEmiPlan.getString("tenureMonths"));
//                                            vEmiPlansDTO.setProcessingFeeFlat(vEmiPlan.getString("processingFeeFlat"));
//                                            vEmiPlansDTO.setProcessingFeePercent(vEmiPlan.getString("processingFeePercent"));
//                                            vEmiPlansDTO.setCcAvenueFeeFlat(vEmiPlan.getString("ccAvenueFeeFlat"));
//                                            vEmiPlansDTO.setCcAvenueFeePercent(vEmiPlan.getString("ccAvenueFeePercent"));
//                                            vEmiPlansDTO.setTenureData(vEmiPlan.getString("tenureData"));
//                                            vEmiPlansDTO.setPlanId(vEmiPlan.getString("planId"));
//                                            vEmiPlansDTO.setAccountCurrName(vEmiPlan.getString("accountCurrName"));
//                                            vEmiPlansDTO.setEmiPlanId(vEmiPlan.getString("emiPlanId"));
//                                            vEmiPlansDTO.setTenureId(vEmiPlan.getString("tenureId"));
//                                            vEmiPlansDTO.setMidProcesses(vEmiPlan.getString("midProcesses"));
//                                            vEmiPlansDTO.setEmiAmount(vEmiPlan.getString("emiAmount"));
//                                            vEmiPlansDTO.setTotal(vEmiPlan.getString("total"));
//                                            vEmiPlansDTO.setEmiProcessingFee(vEmiPlan.getString("emiProcessingFee"));
//                                            vEmiPlansDTO.setTenureAmtGreaterThan(vEmiPlan.getString("tenureAmtGreaterThan"));
//                                            vEmiPlansDTO.setCurrency(vEmiPlan.getString("currency"));
//
//                                            vEmiOptionDTO.getEmiPlansDTO().add(vEmiPlansDTO);
//                                        }
//                                    }
//                                    emiOptionList.add(vEmiOptionDTO);
//                                }
//                            }catch (Exception e) {
//                                e.printStackTrace();
//                            }
//
//                            Spinner emiOption = (Spinner) findViewById(R.id.emiBanks);
//                            EMIAdapter emiAdapter = new EMIAdapter(BillingShippingActivity.this, android.R.layout.simple_spinner_item, emiOptionList);
//                            emiOption.setAdapter(emiAdapter);
//
//                            emiOption.setOnItemSelectedListener(new OnItemSelectedListener(){
//                                @Override
//                                public void onItemSelected(AdapterView parent, View view, int position, long id) {
//                                    EMIOptionDTO vEmiOptionDTO = (EMIOptionDTO)emiOptionList.get(position);
//
//                                    emiPlanId = vEmiOptionDTO.getPlanId();
//                                    allowedBins = vEmiOptionDTO.getBins();
//
//                                    String[] midProcessCards = vEmiOptionDTO.getMidProcesses().split("\\|");
//                                    final ArrayList<String> cardNameList = new ArrayList<String>();
//                                    for(int i=0;i<midProcessCards.length;i++)
//                                        cardNameList.add(midProcessCards[i]);
//                                    Spinner emiCardName = (Spinner) findViewById(R.id.emiCardName);
//                                    CardNameAdapter cardNameAdapter = new CardNameAdapter(BillingShippingActivity.this, android.R.layout.simple_spinner_item, cardNameList);
//                                    emiCardName.setAdapter(cardNameAdapter);
//
//                                    emiCardName.setOnItemSelectedListener(new OnItemSelectedListener(){
//                                        @Override
//                                        public void onItemSelected(AdapterView parent, View view, int position, long id) {
//                                            cardName = cardNameList.get(position);
//                                        }
//
//                                        @Override
//                                        public void onNothingSelected(AdapterView<?> parent) {}
//                                    });
//
//                                    final LinearLayout vEmiDetailsCont = (LinearLayout) findViewById(R.id.emiDetails);
//                                    vEmiDetailsCont.removeAllViews();
//
//                                    RadioGroup rg = new RadioGroup(BillingShippingActivity.this);
//                                    rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
//                                        public void onCheckedChanged(RadioGroup group, int checkedId) {
//                                            try{
//                                                RadioButton rb = (RadioButton) findViewById(checkedId);
//
//                                                EMIPlansDTO vEmiPlanDTO = (EMIPlansDTO)rb.getTag();
//
//                                                emiTenureId = vEmiPlanDTO.getTenureId();
//                                                amount = vEmiPlanDTO.getEmiAmount();
//                                                currency = vEmiPlanDTO.getCurrency();
//
//                                                TextView vProcFee = new TextView(BillingShippingActivity.this);
//                                                vProcFee.setId(R.id.procFee);
//                                                if(ServiceUtility.chkNull(vEmiPlanDTO.getSubventionPaidBy()).equals("Customer")){
//                                                    if((TextView)findViewById(R.id.procFee)!=null)
//                                                        vEmiDetailsCont.removeView((TextView)findViewById(R.id.procFee));
//
//                                                    vProcFee.setText("Processing Fee: "+vEmiPlanDTO.getCurrency()+" "+vEmiPlanDTO.getEmiProcessingFee()+"(Processing fee will be charged only on the first EMI.)");
//                                                    vEmiDetailsCont.addView(vProcFee);
//                                                }else{
//                                                    vEmiDetailsCont.removeView((TextView)findViewById(R.id.procFee));
//                                                }
//                                            }catch (Exception e) { e.printStackTrace(); }
//                                        }
//                                    });
//
//                                    Iterator<EMIPlansDTO> vEmiPlanIt = vEmiOptionDTO.getEmiPlansDTO().iterator();
//                                    while(vEmiPlanIt.hasNext()){
//                                        EMIPlansDTO vEmiPlansDTO = vEmiPlanIt.next();
//
//                                        RadioButton rb = new RadioButton(BillingShippingActivity.this);
//
//                                        String processingFee = !ServiceUtility.chkNull(vEmiPlansDTO.getProcessingFeePercent()).equals("")?
//                                                (vEmiPlansDTO.getProcessingFeePercent()+"% p.a."):(vEmiPlansDTO.getProcessingFeeFlat()+" flat p.a.");
//                                        rb.setText(vEmiPlansDTO.getTenureMonths()+" EMIs.@ "+processingFee+" - "+vEmiPlansDTO.getCurrency()
//                                                +" "+(Math.round(Double.parseDouble(vEmiPlansDTO.getEmiAmount())*100.0)/100.0)+" (Total: "+
//                                                vEmiPlansDTO.getCurrency()+" "+(Math.round(Double.parseDouble(vEmiPlansDTO.getTotal())*100.0)/100.0)+")");
//                                        rb.setTag(vEmiPlansDTO);
//                                        rg.addView(rb);
//                                    }
//                                    vEmiDetailsCont.addView(rg);
//                                }
//                                @Override
//                                public void onNothingSelected(AdapterView<?> parent) {}
//                            });
//                        }else{
//                            ((Spinner) findViewById(R.id.cardtype)).setVisibility(View.VISIBLE);
//                            ((TextView) findViewById(R.id.cardtypetv)).setVisibility(View.VISIBLE);
//                            ((CheckBox) findViewById(R.id.saveCard)).setVisibility(View.VISIBLE);
//                            ((LinearLayout) findViewById(R.id.emiOptions)).setVisibility(View.GONE);
//
//                            Spinner cardType = (Spinner) findViewById(R.id.cardtype);
//                            CardAdapter cardTypeAdapter = new CardAdapter(BillingShippingActivity.this, android.R.layout.simple_spinner_item, cardsList.get(selectedPaymentOption));
//                            cardType.setAdapter(cardTypeAdapter);
//
//                            cardType.setOnItemSelectedListener(new OnItemSelectedListener(){
//                                @Override
//                                public void onItemSelected(AdapterView parent, View view, int position, long id) {
//                                    ((LinearLayout) findViewById(R.id.vCardCVVCont)).setVisibility(View.GONE);
//                                    selectedCardType = cardsList.get(selectedPaymentOption).get(position);
//                                    if(ServiceUtility.chkNull(selectedPaymentOption).equals("OPTCRDC")
//                                            || ServiceUtility.chkNull(selectedPaymentOption).equals("OPTDBCRD")){
//                                        if(!ServiceUtility.chkNull(selectedCardType.getDataAcceptedAt()).equals("CCAvenue")){
//                                            ((LinearLayout) findViewById(R.id.cardDetails)).setVisibility(View.GONE);
//                                            cardNumber.setText("");
//                                            expiryMonth.setText("");
//                                            expiryYear.setText("");
//                                            cardCvv.setText("");
//                                            issuingBank.setText("");
//                                        }
//                                        else{
//                                            //Setting default values here
//                                            cardNumber.setText("4111111111111111");
//                                            expiryMonth.setText("07");
//                                            expiryYear.setText("2027");
//                                            cardCvv.setText("328");
//                                            issuingBank.setText("State Bank of India");
//                                            ((LinearLayout) findViewById(R.id.cardDetails)).setVisibility(View.VISIBLE);
//                                        }
//                                    }
//                                }
//                                @Override
//                                public void onNothingSelected(AdapterView<?> parent) {}
//                            });
//                        }
//                        counter++;
//                    }
//                    @Override
//                    public void onNothingSelected(AdapterView<?> parent) {}
//                });
//                try{
//                    if(jsonRespObj!=null){
//                        if(jsonRespObj.getString("CustPayments")!=null){
//                            final JSONArray vJsonArr = new JSONArray(jsonRespObj.getString("CustPayments"));
//                            if(vJsonArr.length()>0){
//                                ((LinearLayout) findViewById(R.id.payOptions)).setVisibility(View.GONE);
//                                ((LinearLayout) findViewById(R.id.cardDetails)).setVisibility(View.GONE);
//
//                                LinearLayout vDataContainer = (LinearLayout)findViewById(R.id.linDataCont);
//
//                                final LinearLayout vVaultOptionsCont = new LinearLayout(BillingShippingActivity.this);
//                                vVaultOptionsCont.setId(R.id.vaultCont);
//                                vVaultOptionsCont.setOrientation(LinearLayout.VERTICAL);
//                                TextView tv = new TextView(BillingShippingActivity.this);
//                                tv.setText("Vault Options");
//                                vVaultOptionsCont.addView(tv);
//
//                                RadioGroup rg = new RadioGroup(BillingShippingActivity.this);
//                                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
//                                    public void onCheckedChanged(RadioGroup group, int checkedId) {
//                                        try{
//                                            for(int i=0;i<vJsonArr.length();i++){
//                                                JSONObject vVaultOpt = vJsonArr.getJSONObject(i);
//
//                                                if(checkedId==Integer.parseInt(vVaultOpt.getString("payOptId"))){
//                                                    selectedCardType = new CardTypeDTO();
//                                                    selectedCardType.setCardName(vVaultOpt.getString("payCardName"));
//                                                    selectedCardType.setCardType(vVaultOpt.getString("payCardType"));
//                                                    selectedCardType.setPayOptType(vVaultOpt.getString("payOption"));
//
//                                                    selectedPaymentOption = vVaultOpt.getString("payOption");
//
//                                                    if(selectedPaymentOption.equals("OPTCRDC") || selectedPaymentOption.equals("OPTDBCRD"))
//                                                        ((LinearLayout) findViewById(R.id.vCardCVVCont)).setVisibility(View.VISIBLE);
//                                                    else
//                                                        ((LinearLayout) findViewById(R.id.vCardCVVCont)).setVisibility(View.GONE);
//
//                                                    String vCardStr = "";
//                                                    try{
//                                                        vCardStr = vVaultOpt.getString("payCardNo")!=null?vVaultOpt.getString("payCardNo"):cardNumber.getText().toString();
//                                                    }catch(Exception e){}
//
//                                                    cardNumber.setText(vCardStr);
//                                                }
//                                            }
//                                        }catch (Exception e) {}
//                                    }
//                                });
//                                for(int i=0;i<vJsonArr.length();i++){
//                                    JSONObject vVaultOpt = vJsonArr.getJSONObject(i);
//
//                                    String vCardStr = "";
//                                    try{
//                                        vCardStr = vVaultOpt.getString("payCardNo")!=null?" - XXXX XXXX XXXX " + vVaultOpt.getString("payCardNo"):"";
//                                    }catch(Exception e){}
//
//                                    //Radio Button
//                                    String vLblText = paymentOptions.get(vVaultOpt.getString("payOption"))
//                                            + " - " + vVaultOpt.getString("payCardName") + vCardStr;
//                                    RadioButton rb = new RadioButton(BillingShippingActivity.this);
//                                    rb.setId(Integer.parseInt(vVaultOpt.getString("payOptId")));
//                                    rb.setText(vLblText);
//                                    rb.setTextSize(11);
//
//                                    rg.addView(rb);
//                                }
//                                vVaultOptionsCont.addView(rg);
//
//                                vDataContainer.addView(vVaultOptionsCont);
//
//                                final CheckBox vChb = new CheckBox(BillingShippingActivity.this);
//                                vChb.setText("Pay using other payment option");
//                                vChb.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        if(vChb.isChecked()){
//                                            ((LinearLayout) findViewById(R.id.vCardCVVCont)).setVisibility(View.GONE);
//                                            selectedPaymentOption = ((PaymentOptionDTO)payOpt.getItemAtPosition(payOpt.getSelectedItemPosition())).getPayOptId();
//                                            ((LinearLayout) findViewById(R.id.payOptions)).setVisibility(View.VISIBLE);
//                                            if(selectedPaymentOption.equals("OPTDBCRD")
//                                                    || selectedPaymentOption.equals("OPTCRDC"))
//                                                ((LinearLayout) findViewById(R.id.cardDetails)).setVisibility(View.VISIBLE);
//                                            else if(selectedPaymentOption.equals("OPTEMI")){
//                                                ((LinearLayout) findViewById(R.id.emiOptions)).setVisibility(View.VISIBLE);
//                                                ((LinearLayout) findViewById(R.id.emiDetails)).setVisibility(View.VISIBLE);
//                                                ((LinearLayout) findViewById(R.id.cardDetails)).setVisibility(View.VISIBLE);
//                                            }
//                                            else
//                                                ((LinearLayout) findViewById(R.id.cardDetails)).setVisibility(View.GONE);
//                                            ((CheckBox) findViewById(R.id.saveCard)).setVisibility(View.VISIBLE);
//                                            vVaultOptionsCont.setVisibility(View.GONE);
//                                        }
//                                        else{
//                                            ((LinearLayout) findViewById(R.id.payOptions)).setVisibility(View.GONE);
//                                            ((LinearLayout) findViewById(R.id.cardDetails)).setVisibility(View.GONE);
//                                            ((LinearLayout) findViewById(R.id.emiOptions)).setVisibility(View.GONE);
//                                            ((CheckBox) findViewById(R.id.saveCard)).setVisibility(View.GONE);
//                                            vVaultOptionsCont.setVisibility(View.VISIBLE);
//                                        }
//                                    }
//                                });
//                                vDataContainer.addView(vChb);
//                            }else{
//                                ((LinearLayout) findViewById(R.id.payOptions)).setVisibility(View.VISIBLE);
//                            }
//                        }else{
//                            LinearLayout ll = (LinearLayout) findViewById(R.id.cardDetails);
//                            if(selectedPaymentOption.equals("OPTDBCRD") ||
//                                    selectedPaymentOption.equals("OPTCRDC")){
//                                ll.setVisibility(View.VISIBLE);
//                            }else{
//                                ll.setVisibility(View.GONE);
//                            }
//                            counter++;
//                        }
//                    }
//                }catch (Exception e) {}
//            }catch (Exception e) { showToast("Error loading payment options"); }
        }
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        if (getDataAsyncTask!=null) {
            try {
                if (getDataAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
                    getDataAsyncTask.cancel(true) ;
                    getDataAsyncTask = null ;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        super.onDestroy();
    }


    public String getRSAKey () {
        String JsonResponse = null;
        String JsonDATA = "";
        String rsaToken = "" ;
        JSONObject post_dict = new JSONObject();

        try {
            post_dict.put(getString(R.string.app_cms_site_name), getIntent().getStringExtra(getString(R.string.app_cms_site_name)));
            post_dict.put(getString(R.string.app_cms_user_id), getIntent().getStringExtra(getString(R.string.app_cms_user_id)));
            post_dict.put(getString(R.string.app_cms_device), getString(R.string.app_cms_subscription_key));
            JsonDATA = String.valueOf(post_dict);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            //URL url = new URL(getString(R.string.app_cms_baseurl)+"/ccavenue/ccavenue/rsakey");
            URL url = new URL ("http://release-api.viewlift.com/ccavenue/ccavenue/rsakey") ;
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            // is output buffer writter
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty ("Authorization", getIntent().getStringExtra("auth_token"));
            urlConnection.setRequestProperty("x-api-token",getIntent().getStringExtra("x-api-token"));
            //set headers and method
            Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
            writer.write(JsonDATA);
            // json data
            writer.close();
            InputStream inputStream = urlConnection.getInputStream();
            //input stream
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String inputLine;
            while ((inputLine = reader.readLine()) != null)
                buffer.append(inputLine + "\n");
            if (buffer.length() == 0) {
                // Stream was empty. No point in parsing.
                return null;
            }
            JsonResponse = buffer.toString();
            //response data
            Log.i("TAG", JsonResponse);
            try {
                JSONObject jsonObj = new JSONObject(JsonResponse);
                rsaToken = jsonObj.getString("rsaToken");
                orderID = jsonObj.getString("orderId") ;
                accessCode = jsonObj.getString("accessCode") ;
                cancelRedirectURL = jsonObj.getString("redirectUrl") ;
                merchantID = jsonObj.getString("merchantId") ;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("TAG", "Error closing stream", e);
                }
            }
        }
        return rsaToken ;
    }
}
