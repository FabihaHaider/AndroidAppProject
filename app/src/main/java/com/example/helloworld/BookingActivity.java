package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BookingActivity extends AppCompatActivity {
    private Place place;
    private TextView placeName, location, duration, cost;
    private EditText fromDate, fromTime, toDate, toTime, purpose, guestNum;
    private Button sendRequest;
    private int year, month,day;
    private DatePickerDialog datePicker;
    private TimePickerDialog timePicker;
    private Calendar gCalendar;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        if (getIntent().getExtras() != null) {
            Object place = getIntent().getExtras().get("place");
            if (place != null) {
                this.place = (Place) place;
            }
        }

        bindUI();


        gCalendar=Calendar.getInstance();
        fromDate.setOnClickListener(new View.OnClickListener() {
            Calendar cldr = Calendar.getInstance();

            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            @Override
            public void onClick(View v) {
                // date picker dialog
                datePicker = new DatePickerDialog(BookingActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                fromDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                gCalendar.set(year,monthOfYear,dayOfMonth);
                            }
                        }, year, month, day);
                datePicker.getDatePicker().setMinDate(cldr.getTimeInMillis());
                datePicker.show();
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            Calendar cldr = Calendar.getInstance();

            int day = cldr.get(Calendar.DAY_OF_MONTH);
            int month = cldr.get(Calendar.MONTH);
            int year = cldr.get(Calendar.YEAR);
            @Override
            public void onClick(View v) {
                // date picker dialog
                datePicker = new DatePickerDialog(BookingActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                toDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, year, month, day);
                datePicker.getDatePicker().setMinDate(gCalendar.getTime().getTime());
                datePicker.show();
            }
        });

        fromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                timePicker = new TimePickerDialog(BookingActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                fromTime.setText(sHour + "-" + sMinute);
                            }
                        }, hour, minutes, true);

                timePicker.show();
            }
        });

        toTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                timePicker = new TimePickerDialog(BookingActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                toTime.setText(sHour + "-" + sMinute);
                            }
                        }, hour, minutes, true);

                timePicker.show();
            }
        });


      sendRequest.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              //insertRequest();
              try {
                  if(validate()){
                      insertRequest();
                      Toast.makeText(BookingActivity.this, "request has been sent", Toast.LENGTH_LONG).show();

                  }
              } catch (ParseException e) {
                  e.printStackTrace();
              }
          }
      });


    }

    public boolean validate() throws ParseException {
        String sFromDate= fromDate.getText().toString().trim();
        String sToDate= toDate.getText().toString().trim();
        String sFromTime= fromTime.getText().toString().trim();
        String sToTime= toTime.getText().toString().trim();
        String sPurpose= purpose.getText().toString().trim();
        String sGuestNum= guestNum.getText().toString().trim();

        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat stFormat = new SimpleDateFormat("HH-mm");
        Date dFromDate= sdFormat.parse(sFromDate);
        Date dToDate = sdFormat.parse(sToDate);
        Date dFromTime = stFormat.parse(sFromTime);
        Date dToTime = stFormat.parse(sToTime);

        Log.i("date",sFromDate+" "+sToDate);

        if(sFromDate.isEmpty() || sToDate.isEmpty() || sFromTime.isEmpty() || sToTime.isEmpty() ||sPurpose.isEmpty() || sGuestNum.isEmpty()){
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(dFromDate.compareTo(dToDate)>0 || (dFromDate.compareTo(dToDate)==0 && dFromTime.compareTo(dToTime)>0)){
            Toast.makeText(this, "Please select valid date time", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void insertRequest(){


        String sFromDate= fromDate.getText().toString().trim();
        String sToDate= toDate.getText().toString().trim();
        String sFromTime= fromTime.getText().toString().trim();
        String sToTime= toTime.getText().toString().trim();
        String sPurpose= purpose.getText().toString().trim();
        String sGuestNum= guestNum.getText().toString().trim();
        int state = 0;//state 0= not reviewed, state 1= confirmed, state 2 = declined;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String sUserEmail = user.getEmail();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Request");
        Request request = new Request(place.getName(), place.getOwner_email(), sUserEmail, sFromDate, sToDate, sFromTime, sToTime, sPurpose, sGuestNum, state);
        String key = mDatabase.push().getKey();
        mDatabase.child(key).setValue(request);



    }

    public void bindUI(){
        placeName= (TextView) findViewById(R.id.tvBookingPlaceName);
        location= findViewById(R.id.tvBookingPlaceLocation);
        duration= findViewById(R.id.tvBookingDuration);
        cost= findViewById(R.id.tvBookingCost);
        purpose= findViewById(R.id.etBookingPurpose);
        guestNum= findViewById(R.id.etBookingGuest);
        sendRequest= findViewById(R.id.btSendRequest);
        fromDate=findViewById(R.id.etBookingStartDate);
        toDate=findViewById(R.id.etBookingEndDate);
        fromTime=findViewById(R.id.etBookingStartTime);
        toTime=findViewById(R.id.etBookingEndTime);

        placeName.setText(place.getName());
        location.setText(place.getAddress());
        duration.setText("");
        cost.setText("");

        fromDate.setInputType(InputType.TYPE_NULL);
        toDate.setInputType(InputType.TYPE_NULL);
        fromTime.setInputType(InputType.TYPE_NULL);
        toTime.setInputType(InputType.TYPE_NULL);

    }
}