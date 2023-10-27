package com.aspl.task;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.aspl.Utils.NetworkUtil;
import com.aspl.mbs.R;
import com.aspl.mbsmodel.OrderSummary;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class TaskCancelOrder extends AsyncTask<String, Void, String> {

    OrderSummary orderSummary;
    ProgressDialog loading = null;
    Context context;

    TaskCancelOrderEvent mytaskCancelOrderEvent;


    public interface TaskCancelOrderEvent {
        void onTaskCancelOrderResult(OrderSummary orderSummary);
    }

    public TaskCancelOrder(Context context, TaskCancelOrderEvent mytaskCancelOrderEvent) {
        this.mytaskCancelOrderEvent = mytaskCancelOrderEvent;
        this.context = context;
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(context != null) {
            loading = new ProgressDialog(context, R.style.MyprogressDTheme);
            loading.setCancelable(false);
//        loading.setMessage(Constant.Message.AuthenticatingUser);
            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loading.show();
        }
    }

    @Override
    protected String doInBackground(String... strings) {

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
                orderSummary = objectMapper.readValue(response, OrderSummary.class);
                /*Constant.liCardModel = objectMapper.readValue(response, new TypeReference<List<ShoppingCardModel>>() {
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
        /*if (Constant.liCardModel != null){
            MainActivity.onUpdateCartItem(Constant.liCardModel);
        }*/

        if (mytaskCancelOrderEvent != null && orderSummary != null) {
            mytaskCancelOrderEvent.onTaskCancelOrderResult(orderSummary);
        }

        if(loading != null){
            loading.dismiss();
        }
    }
}
