package com.viewlift.ccavenue.screens;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
    String rsa_key = "" ;
    TextView id_tv_text_payment ;
    RelativeLayout id_rl_parent_layout ;
    RecyclerView id_rv_payment_options ;
    RecyclerView.LayoutManager mLayoutManager ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_options);
        id_rl_parent_layout = (RelativeLayout) findViewById(R.id.id_rl_parent_layout) ;
        id_rv_payment_options = (RecyclerView) findViewById(R.id.id_rv_payment_options) ;
        mLayoutManager = new LinearLayoutManager(this) ;
        id_rv_payment_options.setLayoutManager(mLayoutManager);
        id_rl_parent_layout.setVisibility(View.GONE);
        initialScreen = getIntent();
        id_tv_text_payment = findViewById(R.id.id_tv_text_payment);
        id_tv_text_payment.setText("First Payment Rs. " + initialScreen.getStringExtra(AvenuesParams.AMOUNT).toString().trim() +
                " (+tax if req'd) on MM/DD");
        getDataAsyncTask = new GetData() ;
        getDataAsyncTask.execute() ;
        Log.v("apibaseurl",initialScreen.getStringExtra("api_base_url")) ;
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
            rsa_key = getRSAKey() ;
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            List<NameValuePair> vParams = new ArrayList<NameValuePair>();
            vParams.add(new BasicNameValuePair(AvenuesParams.COMMAND,"getJsonDataVault"));
            try {
                //vParams.add(new BasicNameValuePair(AvenuesParams.ACCESS_CODE, accessCode));
                vParams.add(new BasicNameValuePair(AvenuesParams.ACCESS_CODE, "AVFQ72EH40AR04QFRA"));
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
               // vParams.add(new BasicNameValuePair("payment_option","OPTCRDC"));
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

            if (paymentOptions.size()>0) {
                id_rl_parent_layout.setVisibility(View.VISIBLE);
                id_rv_payment_options.setAdapter(new PaymentOptionsAdapter(payOptionList, new OnItemClickListener() {
                    @Override
                    public void onItemClick(PaymentOptionDTO item) {
                        Intent intent = new Intent(PaymentOptionsActivity.this, WebViewActivity.class);
                        intent.putExtra("payment_option",item.getPayOptId()) ;
                        intent.putExtra("orderId",orderID) ;
                        intent.putExtra("accessCode",accessCode) ;
                        intent.putExtra("merchantID",merchantID) ;
                        intent.putExtra("cancelRedirectURL",cancelRedirectURL) ;
                        intent.putExtra("rsa_key",rsa_key) ;
                        intent.putExtras(getIntent()) ;
                        startActivity(intent);
                    }
                }));
            }
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
            URL url = new URL (initialScreen.getStringExtra("api_base_url")+"/ccavenue/ccavenue/rsakey") ;
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

    public class PaymentOptionsAdapter extends RecyclerView.Adapter<PaymentOptionsAdapter.ViewHolder> {
        private ArrayList<PaymentOptionDTO> adapterPaymentOptionsList = new ArrayList <PaymentOptionDTO>();
        private final OnItemClickListener listener;

        // Provide a suitable constructor (depends on the kind of dataset)
        public PaymentOptionsAdapter (ArrayList<PaymentOptionDTO> _payOptionsList, OnItemClickListener listener) {
            this.adapterPaymentOptionsList = _payOptionsList;
            this.listener = listener ;
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView id_tv_payment_options;
            public ViewHolder(View v) {
                super(v);
                id_tv_payment_options = v.findViewById(R.id.id_tv_payment_options);
            }

            public void bind(final PaymentOptionDTO item, final OnItemClickListener listener) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        listener.onItemClick(item);
                    }
                });
            }
        }


        // Create new views (invoked by the layout manager)
        @Override
        public PaymentOptionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.payment_options_row, parent, false);
            // set the view's size, margins, paddings and layout parameters
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.bind(adapterPaymentOptionsList.get(position), listener);
            holder.id_tv_payment_options.setText(adapterPaymentOptionsList.get(position).getPayOptName());
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return adapterPaymentOptionsList.size();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(PaymentOptionDTO item);
    }
}
