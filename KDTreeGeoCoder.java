package com.vedamic.mymap.library.map.geocode.offline;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Computes the reverse geo coding of the latitude and the longitude
 * provided by the user in the background thread.
 *
 * Created by yogesh on 10/9/16.
 */
public final class KDTreeGeoCoder extends AsyncTask<Double,Void,GeoName> {

    private static final String TAG = "==> KDTreeGeoCoder";

    private boolean enableDebugging = true;
    private Context context;
    private int requestCode;
    private boolean hasDialog = false;
    private OnGeoCodeCompleteListener mListener;
    private double latitude, longitude;
    private ProgressDialog mDialog;

    public KDTreeGeoCoder(int requestCode, @Nullable OnGeoCodeCompleteListener callback) {
        this.requestCode = requestCode;
        this.mListener = callback;
    }

    public KDTreeGeoCoder(@NonNull Context context, int requestCode, boolean hasDialog, OnGeoCodeCompleteListener callback) {
        this(requestCode,callback);
        this.context = context;
        this.hasDialog = hasDialog;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(this.hasDialog){
            this.mDialog = new ProgressDialog(this.context);
            this.mDialog.setMessage("Decoding location...");
            this.mDialog.setCancelable(false);
            this.mDialog.setCanceledOnTouchOutside(false);
            this.mDialog.show();
        }
    }

    @Override
    protected GeoName doInBackground(Double... latLongs) {
        try{

            this.latitude = latLongs[0];
            this.longitude = latLongs[1];
            return ReverseGeoCoder.getInstance().nearestPlace(this.latitude,this.longitude);

        }catch (ArrayIndexOutOfBoundsException e){
            if(this.enableDebugging) {
                e.printStackTrace();
                Log.e(TAG, "doInBackground: ArrayIndexOutOfBounds : " + e.getMessage());
            }
            return null;
        }catch (NullPointerException e){
            if(this.enableDebugging) {
                e.printStackTrace();
                Log.e(TAG, "doInBackground: NullPointer : " + e.getMessage());
            }
            return null;
        }
    }

    @Override
    protected void onPostExecute(GeoName geoName) {
        super.onPostExecute(geoName);
        if(this.mDialog != null && this.mDialog.isShowing()){
            this.mDialog.dismiss();
        }
        String geoStr;
        if(geoName != null){
            if(this.enableDebugging){
                Log.d(TAG, "onPostExecute: "+geoName.toString());
            }
            geoStr = geoName.name+", "+geoName.country;
        }
        else{
            if(this.enableDebugging){
                Log.d(TAG, "onPostExecute: Reverse Geo Coder returned null!");
            }
            geoStr = "Nepal";
        }

        if(this.mListener != null){
            this.mListener.onGeoCodeComplete(this.requestCode,geoStr);
        }

    }

    public void debugging(boolean enable){
        this.enableDebugging = enable;
    }

}
