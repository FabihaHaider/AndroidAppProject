package com.example.helloworld;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.Math.ceil;
import static java.lang.Math.round;

public class BookingActivity extends AppCompatActivity implements View.OnClickListener {
    private Place place;
    private TextView placeName, location, duration, cost, alert, alert2;
    private EditText fromDate, fromTime, toDate, toTime, purpose, guestNum;
    private Button sendRequest;
    private int year, month,day, hour, minutes;
    private DatePickerDialog datePicker;
    private TimePickerDialog timePicker;
    private Calendar calendar;
    private Calendar gCalendar;
    private DatabaseReference databaseReference;
    private String sFromDate, sToDate, sFromTime, sToTime, sPurpose, sGuestNum;
    private Date thisFromDateTime, thisToDateTime;
    private boolean ret;
    private ArrayList<Request> arrayList;

    SimpleDateFormat sdFormat = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat stFormat = new SimpleDateFormat("HH:mm");
    SimpleDateFormat sdtFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");



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

        fromDate.addTextChangedListener(textWatcher);
        toDate.addTextChangedListener(textWatcher);
        fromTime.addTextChangedListener(textWatcher);
        toTime.addTextChangedListener(textWatcher);


        fromDate.setOnClickListener(this);
        toDate.setOnClickListener(this);
        fromTime.setOnClickListener(this);
        toTime.setOnClickListener(this);

        sendRequest.setOnClickListener(this);


    }


    public void validateAndInsert() throws ParseException {
        sFromDate= fromDate.getText().toString().trim();
        sToDate= toDate.getText().toString().trim();
        sFromTime= fromTime.getText().toString().trim();
        sToTime= toTime.getText().toString().trim();
        sPurpose= purpose.getText().toString().trim();
        sGuestNum= guestNum.getText().toString().trim();

        if(sFromDate.isEmpty() || sToDate.isEmpty() || sFromTime.isEmpty() || sToTime.isEmpty() ||sPurpose.isEmpty() || sGuestNum.isEmpty()){
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
            return;
        }

        Date dFromDate= sdFormat.parse(sFromDate);
        Date dToDate = sdFormat.parse(sToDate);
        Date dFromTime = stFormat.parse(sFromTime);
        Date dToTime = stFormat.parse(sToTime);

        if(dFromDate.compareTo(dToDate)>0 || (dFromDate.compareTo(dToDate)==0 && dFromTime.compareTo(dToTime)>=0) || thisToDateTime.compareTo(new Date())<=0){
            Toast.makeText(this, "Please select valid date time", Toast.LENGTH_SHORT).show();
            return;
        }

        //ret=true;
        retrieveAndInsert();

    }

    public void retrieveAndInsert() {
        arrayList = new ArrayList<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserMail = user.getEmail().trim();

        readData(new FirebaseCallback() {
            @Override
            public void onCallback(List<Request> list){
                Log.i("tuba3" , String.valueOf(list.size()));
                int flag=1;
                for (int i=0;i<list.size();i++){
                    Request req = list.get(i);
                    String senderMail= req.getSenderMail().trim();
                    String startDate = req.getStartDate().trim();
                    String endDate = req.getEndDate().trim();
                    String startTime = req.getStartTime().trim();
                    String endTime = req.getEndTime().trim();
                    String placeName= req.getPlaceName().trim();
                    String state = req.getState().trim();

                    if(senderMail.equals(currentUserMail) && placeName.equals(place.getName().trim()) && startDate.equals(sFromDate) && endDate.equals(sToDate)
                            && startTime.equals(sFromTime) && endTime.equals(sToTime)){
                        Log.i("tuba", "YES1");
                        alert2.setText("! This request has been sent");
                        alert2.setTextColor(Color.RED);
                        flag=0;
                        break;
                    }

                    Date dbFromDateTime = null,dbToDateTime = null;

                    try {
                        dbFromDateTime= sdtFormat.parse(startDate+" "+startTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                         dbToDateTime= sdtFormat.parse(endDate+" "+endTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(placeName.equals(place.getName().trim()) && state.equals("1") ){
                        if(dbFromDateTime.compareTo(thisFromDateTime)<=0 && dbToDateTime.compareTo(thisFromDateTime)>0){
                            alert2.setText("! This timeslot is already booked. Please try another.");
                            alert2.setTextColor(Color.RED);
                            flag=0;
                            break;

                        }
                        else if(dbFromDateTime.compareTo(thisFromDateTime)>0 && dbFromDateTime.compareTo(thisToDateTime)<=0){
                            alert2.setText("! This timeslot is already booked. Please try another.");
                            alert2.setTextColor(Color.RED);
                            flag=0;
                            break;

                        }
                        else {
                            flag=1;
                        }


                    }
                    else flag=1;

                }
                if(flag==1){
                    insertRequest();
                }

            }
        });


    }


    private void readData(FirebaseCallback firebaseCallback){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            Request request;
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(!arrayList.isEmpty())
                    arrayList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (snapshot.exists()) {
                        String senderMail = dataSnapshot.child("senderMail").getValue().toString();
                        String placeName = dataSnapshot.child("placeName").getValue().toString();
                        String location = dataSnapshot.child("location").getValue().toString();
                        String ownerMail = dataSnapshot.child("ownerMail").getValue().toString();
                        String startDate = dataSnapshot.child("startDate").getValue().toString();
                        String endDate = dataSnapshot.child("endDate").getValue().toString();
                        String startTime = dataSnapshot.child("startTime").getValue().toString();
                        String endTime = dataSnapshot.child("endTime").getValue().toString();
                        String purpose = dataSnapshot.child("bookingPurpose").getValue().toString();
                        String guestNum = dataSnapshot.child("guestNum").getValue().toString();
                        String state = dataSnapshot.child("state").getValue().toString();

                        request= new Request(placeName,location, ownerMail, senderMail, startDate, endDate, startTime, endTime,purpose,guestNum, state);
                        arrayList.add(request);

                    }
                }

                firebaseCallback.onCallback(arrayList);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private interface FirebaseCallback{
        void onCallback(List<Request> list);
    }



    public void insertRequest(){

        String state="0";//state 0= not reviewed, state 1= confirmed, state 2 = declined;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String sUserEmail = user.getEmail();

        Request request = new Request(place.getName(),place.getAddress(), place.getOwner_email(), sUserEmail, sFromDate, sToDate, sFromTime, sToTime, sPurpose, sGuestNum, state);
        String key = databaseReference.push().getKey();
        databaseReference.child(key).setValue(request);

        Toast.makeText(this, "Request has been sent",Toast.LENGTH_SHORT).show();
        finish();


    }

    public void bindUI(){
        placeName= (TextView) findViewById(R.id.tvBookingPlaceName);
        location= findViewById(R.id.tvBookingPlaceLocation);
        duration= findViewById(R.id.tvBookingDuration);
        cost= findViewById(R.id.tvBookingCost);
        alert = findViewById(R.id.tvBookingAlert);
        alert2 = findViewById(R.id.tvBookingAlert2);
        purpose= findViewById(R.id.etBookingPurpose);
        guestNum= findViewById(R.id.etBookingGuest);
        sendRequest= findViewById(R.id.btSendRequest);
        fromDate=findViewById(R.id.etBookingStartDate);
        toDate=findViewById(R.id.etBookingEndDate);
        fromTime=findViewById(R.id.etBookingStartTime);
        toTime=findViewById(R.id.etBookingEndTime);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Request");


        placeName.setText(place.getName());
        location.setText(place.getAddress());
        duration.setText("");
        cost.setText("");
        alert.setText("");
        alert2.setText("");

        if(place.getCharge_unit().trim().equals("Daily")){
            fromTime.setVisibility(View.GONE);
            toTime.setVisibility(View.GONE);
            fromTime.setText("00:00");
            toTime.setText("23:59");
        }

        fromDate.setInputType(InputType.TYPE_NULL);
        toDate.setInputType(InputType.TYPE_NULL);
        fromTime.setInputType(InputType.TYPE_NULL);
        toTime.setInputType(InputType.TYPE_NULL);


        calendar = Calendar.getInstance();
        gCalendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minutes = calendar.get(Calendar.MINUTE);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.etBookingStartDate:
                datePicker = new DatePickerDialog(BookingActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String zDate="", zMonth="";
                                if(dayOfMonth<10) zDate="0";
                                if(monthOfYear<10) zMonth="0";
                                fromDate.setText(zDate+dayOfMonth + "/" +zMonth+(monthOfYear + 1) + "/" + year);
                                gCalendar.set(year,monthOfYear,dayOfMonth);
                            }
                        }, year, month, day);
                datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis());
                datePicker.show();
                break;

            case R.id.etBookingEndDate:
                datePicker = new DatePickerDialog(BookingActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                String zDate="", zMonth="";
                                if(dayOfMonth<10) zDate="0";
                                if(monthOfYear<10) zMonth="0";
                                toDate.setText(zDate+dayOfMonth + "/" +zMonth+(monthOfYear + 1) + "/" + year);

                            }
                        }, year, month, day);
                datePicker.getDatePicker().setMinDate(gCalendar.getTime().getTime());
                datePicker.show();
                break;

            case R.id.etBookingStartTime:
                timePicker = new TimePickerDialog(BookingActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                String zhour="", zMin="";
                                if(sHour<10) zhour="0";
                                if(sMinute<10) zMin="0";
                                fromTime.setText(zhour+sHour + ":" +zMin+ sMinute);
                            }
                        }, hour, minutes, true);

                timePicker.show();
                break;

            case R.id.etBookingEndTime:
                timePicker = new TimePickerDialog(BookingActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                String zhour="", zMin="";
                                if(sHour<10) zhour="0";
                                if(sMinute<10) zMin="0";
                                toTime.setText(zhour+sHour + ":" + zMin+sMinute);
                            }
                        }, hour, minutes, true);

                timePicker.show();
                break;

            case R.id.btSendRequest:
                try {
                   validateAndInsert();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                break;

        }

    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                alert2.setText("");
                calculateCostDuration();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private void calculateCostDuration() throws ParseException {
        sFromDate= fromDate.getText().toString().trim();
        sToDate= toDate.getText().toString().trim();
        sFromTime= fromTime.getText().toString().trim();
        sToTime= toTime.getText().toString().trim();

        if(!sFromDate.isEmpty() && !sToDate.isEmpty() && !sFromTime.isEmpty() && !sToTime.isEmpty()){

            Date dFromDate= sdFormat.parse(sFromDate);
            Date dToDate = sdFormat.parse(sToDate);
            Date dFromTime = stFormat.parse(sFromTime);
            Date dToTime = stFormat.parse(sToTime);
            thisFromDateTime = sdtFormat.parse(sFromDate + " "+ sFromTime);
            thisToDateTime = sdtFormat.parse(sToDate + " "+ sToTime);

            if(dFromDate.compareTo(dToDate)>0 || (dFromDate.compareTo(dToDate)==0 && dFromTime.compareTo(dToTime)>=0) || thisToDateTime.compareTo(new Date())<=0){
                //invalid
                alert.setText("! invalid date and time");
                alert.setTextColor(Color.RED);
                duration.setText("");
                cost.setText("");

            }

            else{
                //calculate
                if(place.getCharge_unit().equals("Daily")){
                    long diff= dToDate.getTime() - dFromDate.getTime();
                    diff= diff/(24*60*60*1000);
                    diff++;
                    long amount = diff * place.getAmount_of_charge();
                    duration.setText("Duration: "+diff + " day/s");
                    cost.setText("Cost: BDT "+amount);
                    alert.setText("");


                }
                else if(place.getCharge_unit().equals("Hourly")){
                    //calculate
                    long diff= thisToDateTime.getTime() - thisFromDateTime.getTime();
                    double fdiff = (double) diff/(60*60*1000);
                    fdiff= (double) round(fdiff*100)/100;
                    double ceilFdiff= ceil(fdiff);
                    long amount = (long) (ceilFdiff * place.getAmount_of_charge());
                    duration.setText("Duration: "+fdiff + " hour/s (considered as "+ceilFdiff+" hour/s)");
                    cost.setText("Cost: BDT "+amount);
                    alert.setText("");

                }
            }

        }

    }

}