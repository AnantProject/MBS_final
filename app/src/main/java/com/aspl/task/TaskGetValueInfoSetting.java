package com.aspl.task;

import android.os.AsyncTask;
import android.util.Log;

import com.aspl.Utils.NetworkUtil;
import com.aspl.mbsmodel.GetValueInfoSetting;
import com.aspl.mbsmodel.InsertOrderDetailed;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

/**
 * Created by Mic on 1/10/2018.
 */

public class TaskGetValueInfoSetting extends AsyncTask<String, Void, String> {

    GetValueInfoSetting getValueInfoSetting;
    //List<InsertOrderDetailed> insertOrderDetailedList;

    /*OrderDetailEvent myOrderDetailEvent;

    public interface OrderDetailEvent{
        void OrderDetailedResult(InsertOrderDetailed insertOrderDetailed);
    }

    public TaskGetValueInfoSetting(OrderDetailEvent myOrderDetailEvent){
        this.myOrderDetailEvent = myOrderDetailEvent;
    }*/

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

                Log.i("web service", "Response : " + response);
                ObjectMapper objectMapper = new ObjectMapper();

                getValueInfoSetting = objectMapper.readValue(response, GetValueInfoSetting.class);

                /*liLoyaltyInfo = objectMapper.readValue(response, new TypeReference<List<LoyaltyInfo>>() {
                });*/
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
       /* if (myOrderDetailEvent!=null)
            myOrderDetailEvent.OrderDetailedResult(insertOrderDetailed);*/
    }
}
