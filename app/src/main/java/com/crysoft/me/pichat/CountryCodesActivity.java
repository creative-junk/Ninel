package com.crysoft.me.pichat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.crysoft.me.pichat.helpers.Constants;
import com.crysoft.me.pichat.helpers.Countries;
import com.crysoft.me.pichat.helpers.ReadFiles;
import com.crysoft.me.pichat.models.CountryCodeData;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class CountryCodesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private static final String TAG = "CountryCodesActivity";
    private Toolbar mToolBar;

    private Context context = CountryCodesActivity.this;

    private static final String NAME = "name";
    private static final String PHONE_CODE = "phone-code";
    private static final String ALPHA_2 = "alpha-2";

    private CountryCodeAdapter countryCodeAdapter;
    private ListView codeListView;
    private EditText countryCodeEditText;

    private ArrayList<CountryCodeData> allCountriesList;

    private JSONArray responseArray = null;

    private int position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_codes);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);

        codeListView = (ListView) findViewById(R.id.code_listview);
        countryCodeEditText = (EditText) findViewById(R.id.country_code_editText);

        responseArray = new JSONArray();

        try {
            String response = ReadFiles.readRawFileAsString(context, R.raw.countrycodes);
            responseArray = new JSONArray(response);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        allCountriesList = new ArrayList<CountryCodeData>();
        for (int i = 0; i < responseArray.length(); i++) {

            try {
                CountryCodeData data = new CountryCodeData();
                data.setItem1(responseArray.getJSONObject(i).getString(NAME));
                data.setItem2(responseArray.getJSONObject(i).getString(PHONE_CODE));
                data.setItem3(responseArray.getJSONObject(i).getString(ALPHA_2));
                allCountriesList.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String countryCode = Countries.getCurrentCountryCode(context);
        for (int i = 0; i < allCountriesList.size(); i++) {
            if (countryCode.equalsIgnoreCase(allCountriesList.get(i).getItem3())) {
                position = i;
                break;
            }
        }

        countryCodeAdapter = new CountryCodeAdapter(context, allCountriesList);
        codeListView.setAdapter(countryCodeAdapter);
        codeListView.setOnItemClickListener(this);
        codeListView.setSmoothScrollbarEnabled(true);

        codeListView.post(new Runnable() {
            @Override
            public void run() {
                codeListView.setSelection(position);
            }
        });
        countryCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString().trim();
                search(text);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void search(String text) {
        ArrayList<CountryCodeData> selectedCountriesList = new ArrayList<CountryCodeData>();

        for (CountryCodeData country : allCountriesList) {
            if (country.getItem1().toLowerCase(Locale.ENGLISH).contains(text.toLowerCase(Locale.ENGLISH))) {
                selectedCountriesList.add(country);
            }
        }
        countryCodeAdapter.refreshList(selectedCountriesList);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_country_codes, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView textView = (TextView) view.findViewById(R.id.country_code_text);

        String code = textView.getText().toString();
        Intent data = new Intent();
        data.putExtra(Constants.Extra.CODES,code);
        setResult(RESULT_OK, data);
        finish();

    }

    private class CountryCodeAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private ViewHolder holder;
        private ArrayList<CountryCodeData> list;

        public CountryCodeAdapter(Context context, ArrayList<CountryCodeData> arrayList) {
            layoutInflater = layoutInflater.from(context);
            this.list = arrayList;

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View hView = convertView;

            if (convertView == null) {
                hView = layoutInflater.inflate(R.layout.country_codes_items, null);
                holder = new ViewHolder();
                holder.country = (TextView) hView.findViewById(R.id.country_text);
                holder.code = (TextView) hView.findViewById(R.id.country_code_text);
                hView.setTag(holder);

            }

            holder = (ViewHolder) hView.getTag();

            if (position % 2 == 0) {
                hView.setBackgroundResource(R.drawable.list_selector_white);
            } else {
                hView.setBackgroundResource(R.drawable.list_selector_gray);
            }

            try {
                holder.country.setText("" + list.get(position).getItem1());
                holder.code.setText("" + list.get(position).getItem2());
            }catch (Exception e){
                e.printStackTrace();
            }
            return hView;

        }

        public void refreshList(ArrayList<CountryCodeData> codeList) {
            this.list = codeList;
            notifyDataSetChanged();
        }
    }

    private class ViewHolder {
        TextView country,code;
    }
}
