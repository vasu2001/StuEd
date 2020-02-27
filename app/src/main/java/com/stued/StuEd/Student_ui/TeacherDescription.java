package com.stued.StuEd.Student_ui;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.allyants.notifyme.NotifyMe;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.stued.StuEd.Model_Classes.SlotBookingClass;
import com.stued.StuEd.Model_Classes.SlotsClass;
import com.stued.StuEd.Model_Classes.TinyDBorderID;
import com.stued.StuEd.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class TeacherDescription extends Fragment {

    final private DatabaseReference databaseReference;
    private ExpandableListView expandableListView;
    private List<String> listgroup;
    private HashMap<String, List<String>> listitem,listItemVenue,listItemPreference;
    private MyExpandableListAdaptor adapter;
    private TextView estimatedMarks, topicDescription, topicName;
    private String p, price;
    private int rate;
    private String oID;
    private View view;
    private LayoutInflater layoutInflater;
    private String time;
    private String date;
    private Dialog dialog;

    public TeacherDescription(LayoutInflater layoutInflater, DatabaseReference databaseReference) {
        this.layoutInflater = layoutInflater;
        this.databaseReference = databaseReference;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        view = layoutInflater.inflate(R.layout.activity_teacher_description, container, false);
        expandableListView =(ExpandableListView)view.findViewById(R.id.ListOfSlots);
        estimatedMarks = view.findViewById(R.id.Marks);
        topicDescription = view.findViewById(R.id.Topic_Description);
        topicName = view.findViewById(R.id.SlotsInfo);
        listgroup = new ArrayList<>();
        listitem = new HashMap<>();
        listItemVenue=new HashMap<>();
        listItemPreference=new HashMap<>();
        dialog=new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_payment);
        dialog.setCanceledOnTouchOutside(false);
        dialog.dismiss();
        final TextView emptyview=view.findViewById(R.id.empty_view2);
        emptyview.setVisibility(View.INVISIBLE);
        p = "";
        price = "";

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<SlotsClass> slots = new ArrayList<>();
                listitem.clear();

                for (DataSnapshot slotSnapshot : dataSnapshot.getChildren()) {
                    switch (slotSnapshot.getKey()) {
                        case "topicName":
                            topicName.setText(slotSnapshot.getValue(String.class));
                            break;
                        case "topicDescription":
                            topicDescription.setText(slotSnapshot.getValue(String.class));
                            break;
                        case "estimatedMarks":
                            estimatedMarks.setText(slotSnapshot.getValue(String.class));
                            break;
                        case "estimatedTime":break;
                        default:
                            slots.add(slotSnapshot.getValue(SlotsClass.class));
                    }
                }

                //list modification
                List<String> newListItem = new ArrayList<String>();
                List<String> newListItemPreference = new ArrayList<String>();
                List<String> newListItemVenue = new ArrayList<String>();
                for (SlotsClass slot : slots) {
                    if (!listgroup.contains(slot.date)) {
                        listgroup.add(slot.date);
                        listitem.put(slot.date, new ArrayList<String>());
                        listItemPreference.put(slot.date, new ArrayList<String>());
                        listItemVenue.put(slot.date, new ArrayList<String>());
                    }
                    newListItem = listitem.get(slot.date);
                    if (newListItem == null) newListItem = new ArrayList<>();
                    if (newListItemPreference == null) newListItemPreference = new ArrayList<>();
                    if (newListItemVenue == null) newListItemVenue = new ArrayList<>();

                    if(slot.slotStatus && slot.maxStudents > slot.currentStudents) {
                            newListItem.add(slot.time + "    Rs. " + slot.fees);
                            newListItemPreference.add(slot.genderPreference);
                            newListItemVenue.add(slot.venue2);
                    }

                    listitem.put(slot.date, newListItem);
                    listItemVenue.put(slot.date, newListItemVenue);
                    listItemPreference.put(slot.date, newListItemPreference);
                }

                if(listgroup.isEmpty())
                {
                    emptyview.setVisibility(View.VISIBLE);
                    emptyview.setText("No Slots!!");
                    expandableListView.setVisibility(View.INVISIBLE);
                }
                else
                {
                    emptyview.setVisibility(View.INVISIBLE);
                    expandableListView.setVisibility(View.VISIBLE);
                }

                /*for(String listGroup: listgroup){
                    if(listitem.get(listGroup).isEmpty())
                        listgroup.remove(listGroup);
                }*/

                for(int i=listgroup.size()-1;i>=0;i--){
                    if(listitem.get(listgroup.get(i)).isEmpty())
                        listgroup.remove(i);
                }

                if(view.getContext()!=null) {
                    adapter = new MyExpandableListAdaptor(view.getContext(), listgroup, listitem, listItemPreference, listItemVenue);
                    expandableListView.setAdapter(adapter);
                    expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                        @Override
                        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                            Log.d("CLicked", "worked");
                            return false;
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(final ExpandableListView expandableListView, View view, final int groupPosition, final int childPosition, long id) {
                //get price from Expandable List View

                dialog.show();

                final TextView newusername = dialog.findViewById(R.id.textview17);
                final ImageView saveButton = dialog.findViewById(R.id.save22);
                final ImageView cancelButton = dialog.findViewById(R.id.cancel22);

                final String s = expandableListView.getExpandableListAdapter().getChild(groupPosition, childPosition).toString();
                if (s.equals("Slot filled"))
                    return false;
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String[] arr = s.split("Rs. ", 2);
                        p = arr[1];
                        Log.w("fees=", p);
                        rate = Integer.parseInt(p) * 100;
                        price = Integer.toString(rate);
                        time = arr[0].trim();
                        date = expandableListView.getExpandableListAdapter().getGroup(groupPosition).toString();
                        getOrderId();
                        startPayment();
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
                return false;
            }
        });
        return view;
    }

    public void getOrderId()
    {
        oID="";
        String url;
        url = "https://us-central1-startup-d757d.cloudfunctions.net/paymentApi";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        double receiptNumber = Math.floor(Math.random() * 5123 * 43) + 10;
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("amount", price);
        map.put("currency","INR");
        map.put("receipt", (Double.toString(receiptNumber)));
        map.put("payment_capture", false);

        JsonObjectRequest jar1 = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(map), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    oID = jsonObject.getString("id");
                    TinyDBorderID tinyDBorderID=new TinyDBorderID(getActivity());
                    tinyDBorderID.putString("orderId",oID);
                    Log.i("oid=",oID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Json Error Res: ", "" + error);
            }
        }
        );
        requestQueue.add(jar1);
        TinyDBorderID tinyDBorderID=new TinyDBorderID(getActivity());

    }



    public void startPayment() {
        /**
         * Instantiate Checkout
         */
        TinyDBorderID tinyDBorderID=new TinyDBorderID(getActivity());
        Log.i("orderid=",tinyDBorderID.getString("orderId"));
        Checkout checkout = new Checkout();

        /**
         * Set your logo here
         */
        //  checkout.setImage(R.drawable.gg);

        /**
         * Reference to current activity
         */
        final Activity activity = this.getActivity();

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            /**
             * Merchant Name
             * eg: ACME Corp || HasGeek etc.
             */
            options.put("name", "StuEd Solutions");

            /**
             * Description can be anything
             * eg: Reference No. #123123 - This order number is passed by you for your internal reference. This is not the `razorpay_order_id`.
             *     Invoice Payment
             *     etc.
             */
            options.put("description", "Reference No. #123456");
             options.put("order_id", tinyDBorderID.getString("orderId"));
            options.put("currency", "INR");

            /**
             * Amount is always passed in currency subunits
             * Eg: "500" = INR 5.00
             */
            options.put("amount", rate);

            checkout.open(activity, options);
            dialog.dismiss();
        } catch (Exception e) {
            Log.e("ERROR=", "Error in starting Razorpay Checkout", e);
        }

    }

    public void onPaymentSuccessful(final String razorpayPaymentID) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        final String uid = firebaseAuth.getCurrentUser().getUid();
        final DatabaseReference userReference = FirebaseDatabase.getInstance().getReference((new TinyDBorderID(getActivity())).getString("collegeName")).child("Users").child(uid).child("slots");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot slotSnapshot : dataSnapshot.getChildren()) {
                    SlotsClass slot = slotSnapshot.getValue(SlotsClass.class);
                    if (slot != null && date.equals(slot.date) && time.equals(slot.time)) {
                        String slot_uid;
                        ArrayList<String> students = new ArrayList<>();

                        slot_uid = slotSnapshot.getKey();
                        slot.currentStudents++;

                        /*String tempUID = databaseReference.child(slot_uid).child("students").push().getKey();

                        databaseReference.child(slot_uid).child("students").child(tempUID).child("studentUID").setValue(uid);
                        databaseReference.child(slot_uid).child("students").child(tempUID).child("studentOTP").setValue(Math.floor(Math.random()*100000));*/

                        databaseReference.child(slot_uid).child("students").child(uid).child("otp").setValue(Math.floor(Math.random()*100000));
                        databaseReference.child(slot_uid).child("students").child(uid).child("validated").setValue(false);

                        databaseReference.child(slot_uid).child("currentStudents").setValue(slot.currentStudents);
                        //databaseReference.child(slot_uid).setValue(slot);

                        String date44=slot.date.replace('/','-');
                        String time44=slot.time;
                        try {
                            checkTimeForEdit(time44,date44);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        SlotBookingClass bookedSlot = new SlotBookingClass(URLDecoder.decode(databaseReference.child(slot_uid).toString().substring(databaseReference.getRoot().toString().length())),razorpayPaymentID);
                        userReference.child(slot_uid).setValue(bookedSlot);

                        //FirebaseMessaging.getInstance().subscribeToTopic(slot_uid);

                        break;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void checkTimeForEdit(String slotBeginTime, String datenotify) throws ParseException {
        Log.i("dateInput=", slotBeginTime + " " + datenotify);
        String[] arrOfStr = slotBeginTime.split(":", 2);
        String[] arrOfStr2 = arrOfStr[1].split(" ", 2);
        String a = arrOfStr[0];
        int t = Integer.parseInt(a);
        String ampm = arrOfStr2[1];
        String notifytime = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd-MM-yyyy");
        c.setTime(sdf2.parse(datenotify));
        datenotify = sdf2.format(c.getTime());
        //  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if (t != 1 && t != 12 && ampm.equals("PM")) {
            t--;
            notifytime = t + ":" + arrOfStr2[0] + " PM";
        } else if (t != 1 && t != 12 && ampm.equals("AM")) {
            t--;
            notifytime = t + ":" + arrOfStr2[0] + " AM";
        } else if (t == 1 && ampm.equals("AM")) {
            t = 12;
            notifytime = t + ":" + arrOfStr2[0] + " AM";
        } else if (t == 1 && ampm.equals("PM")) {
            t = 12;
            notifytime = t + ":" + arrOfStr2[0] + " PM";
        } else if (t == 12 && ampm.equals("AM")) {
            t--;
            notifytime = t + ":" + arrOfStr2[0] + " PM";
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            c.setTime(sdf.parse(datenotify));
            c.add(Calendar.DATE, -1);  // number of days to add
            datenotify = sdf.format(c.getTime());

        } else if (t == 12 && ampm.equals("PM")) {
            t--;
            notifytime = t + ":" + arrOfStr2[0] + " AM";

        }

        String[] dateParts = datenotify.split("-");
        String day = dateParts[0];
        String month = dateParts[1];
        String year = dateParts[2];

        String[] arrOfStr3 = notifytime.split(":", 2);
        String[] arrOfStr4 = arrOfStr3[1].split(" ", 2);
        String a2 = arrOfStr[0];
        int hour2 = Integer.parseInt(a2);
        int min2 = Integer.parseInt(arrOfStr4[0]);
        String ampm2 = arrOfStr4[1];
        Calendar now = Calendar.getInstance();
        now.set(Calendar.YEAR, Integer.parseInt(year));
        now.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        now.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
        now.set(Calendar.HOUR_OF_DAY, hour2);
        now.set(Calendar.MINUTE, min2);
        now.set(Calendar.SECOND, 0);
        if (ampm2.equals("PM"))
            now.set(Calendar.AM_PM, 1);
        else
            now.set(Calendar.AM_PM, 0);

        Log.i("dataoutput=", notifytime + " " + datenotify);

        NotifyMe notifyMe = new NotifyMe.Builder(getActivity())
                .title("StuEd")
                .content("Hey user,your slot begins within in 1 hour")
                .color(255, 0, 0, 255)
                .led_color(255, 255, 255, 255)
                .time(now)
                .small_icon(R.mipmap.ic_launcher)
                .large_icon(R.mipmap.ic_launcher_round)
                .rrule("FREQ=MINUTELY;INTERVAL=5;COUNT=2")
                .build();
    }


}
