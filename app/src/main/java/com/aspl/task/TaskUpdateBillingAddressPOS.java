package com.aspl.task;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.aspl.Utils.NetworkUtil;
import com.aspl.mbs.R;
import com.aspl.mbsmodel.ShippingData;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class TaskUpdateBillingAddressPOS extends AsyncTask<String, Void, String> {

//    ShippingData shippingData;
    String resposeStr;
    Context context;

    UpdateBillingAddressPOSEvent myUpdateBillingAddressPOSEvent;
    ProgressDialog loading = null;

    public interface UpdateBillingAddressPOSEvent{
        void onUpdateBillingAddressOnPosResult(String responseStr);
    }

    public TaskUpdateBillingAddressPOS(Context context, UpdateBillingAddressPOSEvent myUpdateBillingAddressPOSEvent){
        this.myUpdateBillingAddressPOSEvent = myUpdateBillingAddressPOSEvent;
        this.context = context;
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        loading = new ProgressDialog(context, R.style.MyprogressDTheme);
        loading.setCancelable(false);
//        loading.setMessage(Constant.Message.AuthenticatingUser);
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.show();
    }

    @Override
    protected String doInBackground(String... strings) {
        Log.i("web service", "request url : " + strings[0]);
        int count = 0;
        boolean retry = false;
        StringBuilder responseStrBuilder = new StringBuilder();
        do {
            retry = false;
            try {
                NetworkUtil.doNetworkProcessGet(strings[0], responseStrBuilder);
                String response = responseStrBuilder.toString();
                resposeStr = response.toString().trim();
                Log.i("web service", "Response : " + response);
//                ObjectMapper objectMapper = new ObjectMapper();
//                shippingData = objectMapper.readValue(response, ShippingData.class);
                return response;

            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonGenerationException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            retry = true;
            count += 1;
        } while (count < 3 && retry);
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (myUpdateBillingAddressPOSEvent!=null)
            myUpdateBillingAddressPOSEvent.onUpdateBillingAddressOnPosResult(resposeStr);

        if(loading != null && loading.isShowing()){
            loading.dismiss();
        }
    }
}
