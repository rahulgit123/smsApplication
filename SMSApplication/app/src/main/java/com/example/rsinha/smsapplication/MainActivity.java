package com.example.rsinha.smsapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    public static String phoneNo;
    public static MainActivity ins;
    SharedPreferences sP;
    public double longt,latt;
    TextView tv;
    EditText et;

    public static MainActivity instant() {
        return ins;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ins = this;
        sP = getApplicationContext().getSharedPreferences("SMSApp", 0);
        phoneNo = sP.getString("Phone", "7870786678");

        EditText txt = (EditText) findViewById(R.id.editText);
        txt.setText(phoneNo);

        tv=(TextView) findViewById(R.id.textView3);
        et=(EditText) findViewById(R.id.editText);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        MyCurrentLoctionListener locationListener = new MyCurrentLoctionListener();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        Log.i("Check", "Its initialised");
    }
    public class MyCurrentLoctionListener implements android.location.LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            //Log.i("Check", "Its here");
            latt=location.getLatitude();
            longt=location.getLongitude();

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i("Check","Status Changed");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i("Check","Provider enabled");
            tv.setText("Your GPS is turned ON");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i("Check","Provider Disabled");
            tv.setText("Switch on your GPS.");
            Toast.makeText(MainActivity.this , "Please enable GPS for location Access", Toast.LENGTH_LONG).show();
        }
    }

    public void test(View v){
        String str = et.getText().toString();
        phoneNo = valNo(str);
        SharedPreferences.Editor edit = sP.edit();
        edit.putString("Phone",phoneNo);
        edit.apply();
        Toast.makeText(this, "The number is updated", Toast.LENGTH_SHORT).show();
    }
    public String getLoc(){
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(latt, longt, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if(addresses.size()>0) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                Log.i("Check",address+", "+city+", "+state+", "+country+", "+postalCode+", "+knownName);
                return address+", "+city+", "+state+", "+country+", "+postalCode+", "+knownName;
            }else{
                Log.i("Check","Nothing Found");
            }

        }catch (Exception ex){
            Log.i("Check",ex.getMessage());
        }
        return "Cannot access the location";
    }
    public void sendSMS(String msg) {
        String fstr="";
        boolean f=false;
        for(int i=0;i<msg.length();i++){
            char ch =msg.charAt(i);
            if(Character.isDigit(ch)){
                f=true;
                fstr=fstr+ch;
            }else{
                if(f){
                    f=false;
                    fstr=fstr+", ";
                }
                if(i==msg.length()-1 && fstr.length()>2){
                    fstr=fstr.substring(0,fstr.length()-2);
                }
            }
        }
        if(fstr.equals("")){
            finalSend(getLoc());
        }else{
            finalSend(fstr);
        }
    }

    public void finalSend(String message){
        Log.i("Check", "Sending msg : "+message);

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS faild, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public static String valNo(String str){
        int l=str.length();
        String s=str.substring(l-10,l);
        return s;
    }
}
