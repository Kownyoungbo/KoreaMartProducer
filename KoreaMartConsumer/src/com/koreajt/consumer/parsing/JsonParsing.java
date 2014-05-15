package com.koreajt.consumer.parsing;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.koreajt.consumer.R;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class JsonParsing extends ListActivity implements OnScrollListener {
	private static final String TAG_CONTACTS = "row";
	private static final String TAG_PUNAME = "PUMNAME";
	private static final String TAG_GRANDENAME = "GRADENAME";
	private static final String TAG_UNITQTY = "UNITQTY";
	private static final String TAG_UNITNAME = "UNITNAME";
	private static final String TAG_MAXPRICE = "MAXPRICE";
	private static final String TAG_MINPRICE = "MINPRICE";
	private static final String TAG_AVGPRICE = "AVGPRICE";
	private static final String TAG_INVEST_DT = "INVEST_DT";

	// contacts JSONArray
	JSONArray contacts = null;

	// Hashmap for ListView
	ArrayList<HashMap<String, String>> contactList;
	// DataAdapter adapter;

	private ProgressDialog pDialog;

	Button parsingBtn;

	EditText parsingText;
	
	ListAdapter adapter;
	
	private LayoutInflater mInflater;
	
	ListView lv;

	private static String url = "http://openapi.seoul.go.kr:8088/476e775651726e6a3732417a6f4f51/json/GarakGradePrice/1/1000";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.json_parsing_layout);

		contactList = new ArrayList<HashMap<String, String>>();

		lv = getListView();

		parsingText = (EditText) findViewById(R.id.parsingText);
		parsingText.setText("");

		parsingBtn = (Button) findViewById(R.id.parsingStart);
		parsingBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				contactList.clear();
				JsonParsingStart();
			}
		});
	}

	public void JsonParsingStart() {
		new GetContacts().execute();
	}

	/**
	 * Async task class to get json by making HTTP call
	 * */
	private class GetContacts extends AsyncTask<Void, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(JsonParsing.this);
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();

			// Making a request to url and getting response
			String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

			jsonStr = jsonStr.replace(jsonStr.substring(
					jsonStr.indexOf("GarakGradePrice") - 1,
					jsonStr.indexOf("row") - 1), "");

			Log.d("Response: ", "> " + jsonStr);

			if (jsonStr != null) {
				try {
					JSONObject jsonObj = new JSONObject(jsonStr);

					// Getting JSON Array node
					contacts = jsonObj.getJSONArray(TAG_CONTACTS);
					Log.e("", Integer.toString(contacts.length()));
					// looping through All Contacts
					for (int i = 0; i < contacts.length(); i++) {
						JSONObject c = contacts.getJSONObject(i);
						if (parsingText.getText().toString().equals("")) {
							String puName = c.getString(TAG_PUNAME);
							String grandeName = c.getString(TAG_GRANDENAME);
							String unitqty = c.getString(TAG_UNITQTY);
							String unitName = c.getString(TAG_UNITNAME);
							String mixPrice = c.getString(TAG_MAXPRICE);
							String minPrice = c.getString(TAG_MINPRICE);
							String avgPrice = c.getString(TAG_AVGPRICE);
							String insData = c.getString(TAG_INVEST_DT);

							// tmp hashmap for single contact
							HashMap<String, String> contact = new HashMap<String, String>();

							// adding each child node to HashMap key => value
							contact.put(TAG_PUNAME, puName);
							contact.put(TAG_GRANDENAME, grandeName);
							contact.put(TAG_UNITQTY, unitqty);
							contact.put(TAG_UNITNAME, unitName);
							contact.put(TAG_MAXPRICE, mixPrice);
							contact.put(TAG_MINPRICE, minPrice);
							contact.put(TAG_AVGPRICE, avgPrice);
							contact.put(TAG_INVEST_DT, insData);

							// adding contact to contact list
							contactList.add(contact);
						} else {
							if (c.getString(TAG_PUNAME).indexOf(
									parsingText.getText().toString()) >= 0) {
								String puName = c.getString(TAG_PUNAME);
								String grandeName = c.getString(TAG_GRANDENAME);
								String unitqty = c.getString(TAG_UNITQTY);
								String unitName = c.getString(TAG_UNITNAME);
								String mixPrice = c.getString(TAG_MAXPRICE);
								String minPrice = c.getString(TAG_MINPRICE);
								String avgPrice = c.getString(TAG_AVGPRICE);
								String insData = c.getString(TAG_INVEST_DT);

								// tmp hashmap for single contact
								HashMap<String, String> contact = new HashMap<String, String>();

								// adding each child node to HashMap key =>
								// value
								contact.put(TAG_PUNAME, puName);
								contact.put(TAG_GRANDENAME, grandeName);
								contact.put(TAG_UNITQTY, unitqty);
								contact.put(TAG_UNITNAME, unitName);
								contact.put(TAG_MAXPRICE, mixPrice);
								contact.put(TAG_MINPRICE, minPrice);
								contact.put(TAG_AVGPRICE, avgPrice);
								contact.put(TAG_INVEST_DT, insData);

								// adding contact to contact list
								contactList.add(contact);
							}

						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.e("ServiceHandler", "Couldn't get any data from the url");
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			if (pDialog.isShowing())
				pDialog.dismiss();
			/**
			 * Updating parsed JSON data into ListView
			 * */
			adapter = new SimpleAdapter(JsonParsing.this,
					contactList, R.layout.row, new String[] { TAG_PUNAME,
							TAG_GRANDENAME, TAG_UNITQTY, TAG_UNITNAME,
							TAG_MAXPRICE, TAG_MINPRICE, TAG_AVGPRICE,
							TAG_INVEST_DT }, new int[] { R.id.rowTextName,
							R.id.rowTextLevel, R.id.rowTextNum,
							R.id.rowTextDanwe, R.id.rowTextMax,
							R.id.rowTextMin, R.id.rowTextAvg, R.id.rowTextDay });

			setListAdapter(adapter);
			

		}

	}

	// private class DataAdapter extends ArrayAdapter<CustomAdapter> {
	// private LayoutInflater mInflater;
	//
	// public DataAdapter(Context context, ArrayList<CustomAdapter> object) {
	//
	// super(context, 0, object);
	// mInflater = (LayoutInflater) context
	// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	//
	// }
	//
	// // 보여지는 스타일을 자신이 만든 xml로 보이기 위한 구문
	// @Override
	// public View getView(int position, View v, ViewGroup parent) {
	// View view = null;
	//
	// // 현재 리스트의 하나의 항목에 보일 컨트롤 얻기
	//
	// if (v == null) {
	// // XML 레이아웃을 직접 읽어서 리스트뷰에 넣음
	// view = mInflater.inflate(R.layout.row, null);
	// } else {
	// view = v;
	// }
	//
	// // 자료를 받는다.
	// final CustomAdapter data = this.getItem(position);
	//
	// if (data != null) {
	//
	//
	// // 화면 출력
	// TextView puName = (TextView) view.findViewById(R.id.rowTextName);
	// TextView grandeName = (TextView) view.findViewById(R.id.rowTextLevel);
	// TextView unitqty = (TextView) view.findViewById(R.id.rowTextName);
	// TextView unitName = (TextView) view.findViewById(R.id.rowTextDanwe);
	// TextView maxPrice = (TextView) view.findViewById(R.id.rowTextMax);
	// TextView minPrice = (TextView) view.findViewById(R.id.rowTextMin);
	// TextView avgPrice = (TextView) view.findViewById(R.id.rowTextAvg);
	// TextView insDate = (TextView) view.findViewById(R.id.rowTextDay);
	// // 텍스트뷰1에 getLabel()을 출력 즉 첫번째 인수값
	// puName.setText(data.getPuName());
	// grandeName.setText(data.getGrandeName());
	// unitqty.setText(data.getUnitqty());
	// unitName.setText(data.getUnitName());
	// maxPrice.setText(data.getMaxPrice());
	// minPrice.setText(data.getMinPrice());
	// avgPrice.setText(data.getAvgPrice());
	// insDate.setText(data.getInsDate());
	// }
	//
	// return view;
	//
	// }
	//
	// }

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}
}