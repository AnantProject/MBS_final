package com.aspl.task;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.aspl.Utils.NetworkUtil;
import com.aspl.mbs.R;
import com.aspl.mbsmodel.StoreHour;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.json.JSONException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

public class TaskStoreDeliveryHours extends AsyncTask<String, Void, String> {
    private List<StoreHour> storeHourList;

    StoreDeliveryHoursEvent storeDeliveryHoursEvent;
    Context context;
    String s;
    ProgressDialog loading = null;

//    public TaskStoreDeliveryHours(StoreDeliveryHoursEvent storeDeliveryHoursEvent, Context context) {
//        this.storeDeliveryHoursEvent = storeDeliveryHoursEvent;
//        this.context = context;
//    }

    public TaskStoreDeliveryHours(StoreDeliveryHoursEvent storeDeliveryHoursEvent, Context context, String deliveryOption) {
        this.storeDeliveryHoursEvent = storeDeliveryHoursEvent;
        this.context = context;
        s = deliveryOption;
    }

    public interface StoreDeliveryHoursEvent {
        void onGetStoreDeliveryHoursResult(List<StoreHour> storeHourList);
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if(!s.isEmpty() && s.equals("deliveryOption")) {
            loading = new ProgressDialog(context, R.style.MyprogressDTheme);
            loading.setCancelable(false);
//        loading.setMessage(Constant.Message.AuthenticatingUser);
            loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            loading.show();
        }
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
                Log.i("web service", "Response : " + response);
                ObjectMapper objectMapper = new ObjectMapper();
                TypeFactory typeFactory = objectMapper.getTypeFactory();
                CollectionType collectionType = typeFactory.constructCollectionType(
                        List.class, StoreHour.class);

                storeHourList = objectMapper.readValue(response, collectionType);

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
        if(storeDeliveryHoursEvent != null){
            storeDeliveryHoursEvent.onGetStoreDeliveryHoursResult(storeHourList);
        }
//
        if(loading != null && loading.isShowing()){
            loading.dismiss();
        }

    }
}
