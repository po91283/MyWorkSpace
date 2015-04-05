package com.example.gracelu.myproject1;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class MainActivity extends Activity {
    private ArrayList<String> mAl;
    private ArrayAdapter<String> mAdapter;
    private int mCurrentBudget = 300;
    private static Integer mNewDayBudget = 300;
    private static Integer mNewRecordStartIndex;
    private static final String PREFERENCE_SETTING_NAME = "BALANCE_DATA";
    private static final String SPEND_HIS_FILE_NAME = "SpendRecord";
    private static final String PREF_BALANCE_KEY = "Balance";
    private static final String PREF_DAY_BUDGET = "NewDayBudget";
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get previous budget value
        SharedPreferences myPreferences = getSharedPreferences(PREFERENCE_SETTING_NAME,
                0);
        if (myPreferences.contains(PREF_BALANCE_KEY)) {
            mCurrentBudget = myPreferences.getInt(PREF_BALANCE_KEY, -9999);
        }

        if(myPreferences.contains(PREF_DAY_BUDGET)){
            mNewDayBudget = myPreferences.getInt(PREF_DAY_BUDGET, 300);
        }
        // Init data in list view
        mAl = new ArrayList<String>();
        mAl.add("Balance: " + Integer.toString(mCurrentBudget));
        getPreviousSpendList();
        mAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mAl);
        mNewRecordStartIndex = mAl.size();
        ListView lView = (ListView) findViewById(R.id.lt_spendDetail);
        lView.setAdapter(mAdapter);
        // set button listener
        Button btnSubmit = (Button) findViewById(R.id.BtnSubmitMoney);
        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText spend = (EditText) findViewById(R.id.EdTextSpendMoney);
                if (spend.getText().toString().isEmpty()) {
                    return;
                }
                mAl.add(spend.getText().toString());
                deductBalance(spend.getText().toString());
                updateCurrentBudgetShowInList();
                mAdapter.notifyDataSetChanged();
                spend.setText("");
            }
        });

        Button btnNewDay = (Button) findViewById(R.id.BtnNewDay);
        btnNewDay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mAl.clear();
                mAl.add("temp");  //create one value for edit.
                addBalance();
                updateCurrentBudgetShowInList();
                mAdapter.notifyDataSetChanged();
                clearPreviousSpendList();
            }
        });

        Button btnReset=(Button)findViewById(R.id.BtnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mAl.clear();
                mAl.add("temp");  //create one value for edit.
                mCurrentBudget = mNewDayBudget;
                updateCurrentBudgetShowInList();
                mAdapter.notifyDataSetChanged();
                clearPreviousSpendList();
            }
        });

        mDialog = new Dialog(MainActivity.this);
        mDialog.setTitle("Setting");
        mDialog.setCancelable(false);
        mDialog.setContentView(R.layout.budget_dlg);
    }

    @Override
    protected void onStop() {
        super.onStop();
        setPreviousSpendList();
        saveBalance();
        saveNewDayBudget();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mDialog.findViewById(R.id.btnSetBudgetOK).setOnClickListener(DialogBtnOKOnclick);
            mDialog.findViewById(R.id.btnSetBudgetCancel).setOnClickListener(DialogBtnCancelOnclick);
            setDiagCurrentBudget();
            mDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private Button.OnClickListener DialogBtnOKOnclick = new Button.OnClickListener(){
        public void onClick(View v){
            try {
                EditText dt = (EditText) mDialog.findViewById(R.id.edtUserBudget);
                Integer bng = Integer.parseInt(dt.getText().toString());
                mCurrentBudget += (bng - mNewDayBudget);
                Log.d("grace", bng.toString());
                Log.d("grace", mNewDayBudget.toString());
                mNewDayBudget = bng;
                updateCurrentBudgetShowInList();
                dt.setText(""); //clear ui text
                mDialog.cancel();
            }
            catch(Exception ex){
                Log.d("grace",ex.toString());
            }
        }
    };

    private Button.OnClickListener DialogBtnCancelOnclick = new Button.OnClickListener(){
        public void onClick(View v){
            mDialog.cancel();
        }
    };
    private void saveBalance()
    {
        SharedPreferences.Editor myPreferences = getSharedPreferences(PREFERENCE_SETTING_NAME,
                0).edit();
        myPreferences.putInt(PREF_BALANCE_KEY, mCurrentBudget);
        myPreferences.commit();
    }

    private void saveNewDayBudget()
    {
        SharedPreferences.Editor myPreferences = getSharedPreferences(PREFERENCE_SETTING_NAME,
                0).edit();
        myPreferences.putInt(PREF_DAY_BUDGET, mNewDayBudget);
        myPreferences.commit();
    }

    private void clearPreviousSpendList() {
        try {
            FileOutputStream fis = openFileOutput(SPEND_HIS_FILE_NAME, Context.MODE_PRIVATE);
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(fis));
            br.write(""); // clear data in file
        } catch (Exception ex) {
            Log.d("grace", "file delete fail.");
        }
    }

    private void setPreviousSpendList()
    {
        try {
            if(mAl.size() == 0) {
                return;
            }
            FileOutputStream fis = openFileOutput(SPEND_HIS_FILE_NAME, Context.MODE_APPEND);
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(fis));
            for(int i = mNewRecordStartIndex;i < mAl.size(); i++) {
                br.write(mAl.get(i));
                br.newLine();
            }
            br.close();
            fis.close();
        }catch(Exception ex){
            Log.d("grace", ex.getMessage());
        }
    }
    private void getPreviousSpendList() {
        try {
            FileInputStream fis = openFileInput(SPEND_HIS_FILE_NAME);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String oneRecord;
            while ((oneRecord = br.readLine()) != null) {
                mAl.add(oneRecord);
            }
            br.close();
            fis.close();
        } catch (Exception ex) {
            Log.d("grace", ex.getMessage());
        }
    }

    private String deductBalance(String spend) {
        int spendint = Integer.parseInt(spend);
        mCurrentBudget = mCurrentBudget - spendint;
        return String.valueOf(mCurrentBudget);
    }

    private void setDiagCurrentBudget(){
        TextView tv = (TextView) mDialog.findViewById(R.id.txvCurrentBudget);
        tv.setText(mNewDayBudget.toString());
    }

    private String addBalance() {
        mCurrentBudget += mNewDayBudget;
        return String.valueOf(mCurrentBudget);
    }

    private void updateCurrentBudgetShowInList() {
        mAl.set(0, "Balance: " + String.valueOf(mCurrentBudget));
    }
}
