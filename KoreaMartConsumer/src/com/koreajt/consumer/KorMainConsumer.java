package com.koreajt.consumer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Browser;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.koreajt.consumer.gcm.RegisterApp;
import com.koreajt.consumer.parsing.XmlParser;
import com.koreajt.consumer.util.CustomDialogAlert;
import com.koreajt.consumer.util.CustomDialogEnd;
import com.koreajt.consumer.util.HorizontalSlider;
import com.koreajt.consumer.util.TagValuse;
import com.navdrawer.SimpleSideDrawer;

/**
 * @author 권영보 140321
 */

public class KorMainConsumer extends Activity {
	// View menu;
	// View app;
	boolean menuOut = false;
	/*
	 * AnimParams animParams = new AnimParams(); AnimParams animParamsMenu = new
	 * AnimParams();
	 */
	Button mSearch, mSell, mLogout;
	EditText mSearchText;
	TextView mUserLevel, mUserName;
	LinearLayout mMenuBuylist, mMenubag, mMenuBookmark, mMenuQna, mMenuHunJung,
			mMenuNotice, mMenuLoding;
	TextView mMenuTotalSendNum, mMenuCancleNum, mMenuChangeNum, mMenuReNum;
	LinearLayout mBliendWebview, mPageError, mRecLatout;
	XmlParser mXmlP;
	Handler mHandler;
	boolean mLoginFT = false;
	int mPageErrorCode = 0;
	private boolean mLeftSlide = false;
	private boolean mRightSlide = false;
	String mType = "0";
	boolean mAppLogin = false;
	String[] mAppLoginList = null;
	InputMethodManager mImm;

	// 생산자 데이터 파싱 주소(임시로 파일 이름으로 설정)
	public static WebView mWebview;
	public int mUploadMode = 0;

	/* 갤러리 실행, 카메라 실행 flag */
	protected final int SELECT_GALLERY = 1;
	protected final int SELECT_CAMERA = 2;
	protected final int SELECT_CROP = 3;
	protected final int SELECT_MOVIE = 4;

	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	GoogleCloudMessaging mGcm;
	AtomicInteger mMsgId = new AtomicInteger();
	String mRegid;
	String mNotificationUrl = null;

	ProgressBar mProgressBar;
	private Context mContextChange;
	UrlSharedPreferenceUChange mPrefUChange;
	UrlSharedPreference mPref = new UrlSharedPreference(this);
	SimpleSideDrawer mLeftSlide_me;
	SimpleSideDrawer mRightSlide_me;

	private int mServerResponseCode = 0;
	private ProgressDialog mDialog = null;

	private String mFilePath = null;
	private ArrayList<String> mCategoryTitle = new ArrayList<String>();
	private ArrayList<String> mCategoryDept = new ArrayList<String>();
	private ArrayList<String> mCategoryDeptId = new ArrayList<String>();
	private ArrayList<String> mCategoryDeptnum = new ArrayList<String>();
	private ArrayList<String> mCategoryP_num = new ArrayList<String>();

	/*
	 * 20130401 권영보 expandableListView 관련 변수들
	 */
	ExpandableListAdapters mListAdapter;
	ExpandableListView mExpListView;
	List<String> mListDataHeader;
	HashMap<String, List<String>> mListDataChild;

	private Animation mTextAnim;
	
	CustomDialogAlert mCustomDialogAlert;
	CustomDialogEnd mCustomDialogEnd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);

		final int sdkVersion = android.os.Build.VERSION.SDK_INT;

		/*
		 * 20140324 SlideLeft 다른 방법으로 변경
		 */
		// menu = findViewById(R.id.menu);
		// app = findViewById(R.id.app);

		mLeftSlide_me = new SimpleSideDrawer(this);
		
		mRightSlide_me = new SimpleSideDrawer(this);
		mLeftSlide_me.setLeftBehindContentView(R.layout.menu_activity);
		mRightSlide_me.setRightBehindContentView(R.layout.category_activity);
		mTextAnim = AnimationUtils.loadAnimation(this, R.anim.text_alpha);
		mImm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

		setLayout();

		webInit();

		if (getIntent() != null) {
			Uri uri = getIntent().getData();
			if (uri != null) {
				String info = uri.getQueryParameter("r");
				mAppLogin = true;
				mAppLoginList = new String[2];
				mAppLoginList = info.split("/");
			}
		}

		if (mAppLogin) {
			mWebview.loadUrl(getString(R.string.mainServerUrl)
					+ getString(R.string.loginUrl) + "?id=" + mAppLoginList[0]
					+ "&pw=" + mAppLoginList[1] + "&is_chg=1");
		} else if (!mPref.getValue(TagValuse.USERID, "").equals("")) {
			mWebview.loadUrl(getString(R.string.mainServerUrl)
					+ getString(R.string.loginUrl) + "?id="
					+ mPref.getValue(TagValuse.USERID, "") + "&pw="
					+ mPref.getValue(TagValuse.USERPW, ""));
		} else {
			mWebview.loadUrl(getString(R.string.mainServerUrl)
					+ getString(R.string.mainUrl));
		}
		
		mCustomDialogAlert = new CustomDialogAlert(KorMainConsumer.this);
		mCustomDialogEnd = new CustomDialogEnd(KorMainConsumer.this);

		mProgressBar = (HorizontalSlider) findViewById(R.id.progress_horizontal);
		findViewById(R.id.BtnSlide).setOnClickListener(new ClickListener());
		findViewById(R.id.BtnHome).setOnClickListener(new ClickListener());
		findViewById(R.id.BtnCategroy).setOnClickListener(new ClickListener());
		findViewById(R.id.netErrorData).setOnClickListener(new ClickListener());
		findViewById(R.id.netErrorWifi).setOnClickListener(new ClickListener());
		findViewById(R.id.webReload).setOnClickListener(new ClickListener());
		findViewById(R.id.bleak).setOnClickListener(null);

		mMenuBuylist.setOnClickListener(new ClickListener());
		mMenubag.setOnClickListener(new ClickListener());
		mMenuBookmark.setOnClickListener(new ClickListener());
		mMenuQna.setOnClickListener(new ClickListener());
		mMenuHunJung.setOnClickListener(new ClickListener());
		mMenuNotice.setOnClickListener(new ClickListener());
		mBliendWebview.setOnClickListener(null);
		mSearch.setOnClickListener(new ClickListener());
		mSell.setOnClickListener(new ClickListener());
		mLogout.setOnClickListener(new ClickListener());
		mSearchText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				switch (actionId) {
				
				case EditorInfo.IME_ACTION_SEARCH:
					String searchStr = mSearchText.getText().toString();
					if (!searchStr.equals("")) {
						searchItem(searchStr);
						setRightSlidingUI();
						mSearchText.setText("");
						
					} else {
						Toast.makeText(getApplicationContext(), "상품명을 입력 하세요",
								Toast.LENGTH_SHORT).show();
					}
					break;
				default:
					return false;
				}
				return true;
			}
		});

		prepareListData();
	}

	public void webInit() {
		mWebview.setWebViewClient(new WebViewClientClass());
		mWebview.setWebChromeClient(new WebViewChromClinetClass());
		mWebview.getSettings().setSavePassword(false);
		mWebview.getSettings().setSaveFormData(true);
		mWebview.getSettings().setJavaScriptEnabled(true);
		mWebview.getSettings().setBuiltInZoomControls(false);
		mWebview.getSettings().setUseWideViewPort(false);
		mWebview.addJavascriptInterface(new AndroidBridge(), "kormart");
	}

	private void setLayout() {
		mUserLevel = (TextView) findViewById(R.id.userLevel);
		mUserName = (TextView) findViewById(R.id.userName);
		mBliendWebview = (LinearLayout) findViewById(R.id.blindLayout);
		mPageError = (LinearLayout) findViewById(R.id.pageError);
		mWebview = (WebView) findViewById(R.id.webview);
		mMenuBuylist = (LinearLayout) findViewById(R.id.btnSubMenu01);
		mMenubag = (LinearLayout) findViewById(R.id.btnSubMenu02);
		mMenuBookmark = (LinearLayout) findViewById(R.id.btnSubMenu03);
		mMenuQna = (LinearLayout) findViewById(R.id.btnSubMenu04);
		mMenuHunJung = (LinearLayout) findViewById(R.id.btnSubMenu05);
		mMenuNotice = (LinearLayout) findViewById(R.id.btnSubMenu06);
		mExpListView = (ExpandableListView) findViewById(R.id.elv_list);
		mMenuTotalSendNum = (TextView) findViewById(R.id.SubMenuText01);
		mMenuCancleNum = (TextView) findViewById(R.id.SubMenuText02);
		mMenuChangeNum = (TextView) findViewById(R.id.SubMenuText03);
		mMenuReNum = (TextView) findViewById(R.id.SubMenuText04);
		mMenuLoding = (LinearLayout) findViewById(R.id.menuLoding);
		mSearch = (Button) findViewById(R.id.searchBtn);
		mSearchText = (EditText) findViewById(R.id.searchEdit);
		mSell = (Button) findViewById(R.id.sellChange);
		mLogout = (Button) findViewById(R.id.logout);
		mSell.setVisibility(View.INVISIBLE);
	}

	public void setLeftSlidingUI() {
		mLeftSlide_me.toggleLeftDrawer();
		if (mLeftSlide) {
			mLeftSlide = false;
		} else {
			mLeftSlide = true;
		}
	}

	public void setRightSlidingUI() {
		mRightSlide_me.toggleRightDrawer();
		if (mRightSlide) {
			mRightSlide = false;
		} else {
			mRightSlide = true;
		}
	}

	/*
	 * Gcm 서비스 시작
	 */
	@SuppressLint("NewApi")
	private void GcmStart() {
		if (checkPlayServices()) {
			mGcm = GoogleCloudMessaging.getInstance(getApplicationContext());
			// GCM 서버에 등록된 regId를 가지고 옴
			mRegid = getRegistrationId(getApplicationContext());
			// 없을경우 생성
			if (mRegid.isEmpty()) {
				new RegisterApp(getApplicationContext(), mGcm,
						getAppVersion(getApplicationContext())).execute();
				Log.i("GcmStart", "RegId create");
			} else {
				KorMainConsumer.mWebview.loadUrl("javascript:callReg('"
						+ mRegid + "')");
				Log.i("GcmStart", "RegId Call");
			}
		} else {
			Log.e(TagValuse.TAG, "No valid Google Play Services APK found.");
		}
	}

	/*
	 * regId를 가지고 오는 메소드
	 */
	@SuppressLint("NewApi")
	private String getRegistrationId(Context context) {
		// preferences 에 저장된 regId를 가지고 옴
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(TagValuse.PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.e(TagValuse.TAG, "Registration not found.");
			return "";
		}
		int registeredVersion = prefs.getInt(TagValuse.PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(getApplicationContext());
		if (registeredVersion != currentVersion) {
			Log.e(TagValuse.TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	private SharedPreferences getGCMPreferences(Context context) {
		return getSharedPreferences(KorMainConsumer.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	private int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/*
	 * google-play-service 가능 여부 체크
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.e(TagValuse.TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	public void userDataDisplay(ArrayList<String> userInfo) {
		mUserLevel.setText(userInfo.get(1) + "등급");
		mUserName.setText(userInfo.get(0) + "님 반갑습니다.");
		mMenuTotalSendNum.setText(userInfo.get(2));
		mMenuCancleNum.setText(userInfo.get(3));
		mMenuChangeNum.setText(userInfo.get(4));
		mMenuReNum.setText(userInfo.get(5));
		setFlash(mMenuTotalSendNum);
		setFlash(mMenuCancleNum);
		setFlash(mMenuChangeNum);
		setFlash(mMenuReNum);
		mMenuLoding.setVisibility(View.GONE);
		if (mType.equals("3")) {
			mSell.setVisibility(View.VISIBLE);
		} else {
			mSell.setVisibility(View.INVISIBLE);
		}
	}

	public void setCategoryList() {
		int category01Size = mCategoryTitle.size();
		int cateforyNumSize = mCategoryDeptnum.size();

		final ArrayList<ArrayList<String>> cartgoryNumPar = new ArrayList<ArrayList<String>>();

		for (int i = 0; i < category01Size; i++) {
			mListDataHeader.add(mCategoryTitle.get(i));
			mListDataChild.put(mListDataHeader.get(i),
					CategorySplit(mCategoryDept.get(i)));
			
		}
		for (int i = 0; i < cateforyNumSize; i++) {
			cartgoryNumPar.add(CategorySplit(mCategoryDeptnum.get(i)));
		}

		mListAdapter = new ExpandableListAdapters(this, mListDataHeader,
				mListDataChild);

		// setting list adapter
		mExpListView.setAdapter(mListAdapter);

		// Listview Group click listener
		mExpListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// Toast.makeText(getApplicationContext(),
				// "Group Clicked " + listDataHeader.get(groupPosition),
				// Toast.LENGTH_SHORT).show();
				return false;
			}
		});

		// Listview Group expanded listener
		mExpListView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				// Toast.makeText(getApplicationContext(),
				// listDataHeader.get(groupPosition) + " Expanded",
				// Toast.LENGTH_SHORT).show();
			}
		});

		// Listview Group collasped listener
		mExpListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				// Toast.makeText(getApplicationContext(),
				// listDataHeader.get(groupPosition) + " Collapsed",
				// Toast.LENGTH_SHORT).show();

			}
		});

		// Listview on child click listener
		mExpListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub

				setRightSlidingUI();
				String url = getString(R.string.mainServerUrl)
						+ getString(R.string.categoryUrl)
						+ mCategoryP_num.get(groupPosition) + "&"
						+ getString(R.string.categoryUrlPar)
						+ cartgoryNumPar.get(groupPosition).get(childPosition);
				mWebview.loadUrl(url);
				return false;
			}
		});
	}

	private void prepareListData() {
		mListDataHeader = new ArrayList<String>();
		mListDataChild = new HashMap<String, List<String>>();

		new CategoryParser().execute(getString(R.string.mainServerUrl)
				+ getString(R.string.categoryXmlUrl));
	}

	private ArrayList<String> CategorySplit(String categoryStr) {

		ArrayList<String> categoryList = new ArrayList<String>();

		String[] category = categoryStr.split(",");

		int categoryLength = category.length;

		for (int i = 0; i < categoryLength; i++) {
			categoryList.add(category[i]);
		}

		return categoryList;
	}

	public static boolean isPackageInstalled(Context ctx, String pkgName) {
		try {
			ctx.getPackageManager().getPackageInfo(pkgName,
					PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void showAlert(String message, String positiveButton,
			DialogInterface.OnClickListener positiveListener,
			String negativeButton,
			DialogInterface.OnClickListener negativeListener) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setMessage(message);
		alert.setPositiveButton(positiveButton, positiveListener);
		alert.setNegativeButton(negativeButton, negativeListener);
		alert.show();
	}

	private void downloadFile(String mUrl) {
		new DownloadFileTask().execute(mUrl);
	}

	// AsyncTask<Params,Progress,Result>
	private class DownloadFileTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... urls) {
			URL myFileUrl = null;
			try {
				myFileUrl = new URL(urls[0]);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			try {
				HttpURLConnection conn = (HttpURLConnection) myFileUrl
						.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();

				// 다운 받는 파일의 경로는 sdcard/ 에 저장되며 sdcard에 접근하려면 uses-permission에
				// android.permission.WRITE_EXTERNAL_STORAGE을 추가해야만 가능.
				String mPath = "sdcard/v3mobile.apk";
				FileOutputStream fos;
				File f = new File(mPath);
				if (f.createNewFile()) {
					fos = new FileOutputStream(mPath);
					int read;
					while ((read = is.read()) != -1) {
						fos.write(read);
					}
					fos.close();
				}

				return "v3mobile.apk";
			} catch (IOException e) {
				e.printStackTrace();
				return "";
			}
		}

		@Override
		protected void onPostExecute(String filename) {
			if (!"".equals(filename)) {
				Toast.makeText(getApplicationContext(), "download complete", 0)
						.show();
				// 안드로이드 패키지 매니저를 사용한 어플리케이션 설치.
				File apkFile = new File(
						Environment.getExternalStorageDirectory() + "/"
								+ filename);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(apkFile),
						"application/vnd.android.package-archive");
				startActivity(intent);
			}
		}
	}

	// 웹뷰 설정하는 클레스
	private class WebViewClientClass extends WebViewClient {

		boolean scaleChangedRunnablePending = false;

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}

		// @Override
		// public void onScaleChanged(WebView view, float oldScale, float
		// newScale) {
		// // TODO Auto-generated method stub
		// super.onScaleChanged(view, oldScale, newScale);
		//
		// }

		@Override
		public void onScaleChanged(final WebView webView, float oldScale,
				float newScale) {
			// webView.zoomOut();
		}

		@Override
		public boolean shouldOverrideUrlLoading(final WebView view,
				final String url) {
			// TODO Auto-generated method stub
			boolean mReturn = false;
			/*
			 * 유플러스 백신 APK다운로드 처리
			 */
			if (url.indexOf(getString(R.string.joinGcmSend)) > -1) {
				GcmStart();
			}

			if (url.startsWith("http://") || url.startsWith("https://")) {
				view.loadUrl(url);
				mReturn = true;
			} else if ((url.startsWith("http://") || url.startsWith("https://"))
					&& url.endsWith(".apk")) {
				downloadFile(url);
				mReturn = super.shouldOverrideUrlLoading(view, url);
			} else if ((url.startsWith("http://") || url.startsWith("https://"))
					&& (url.contains("market.android.com") || url
							.contains("m.ahnlab.com/kr/site/download"))) {
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				try {
					startActivity(intent);
					mReturn = true;
				} catch (ActivityNotFoundException e) {
					mReturn = false;
				}
			} else if (url != null
					&& (url.contains("vguard")
							|| url.contains("droidxantivirus")
							|| url.contains("smhyundaiansimclick://")
							|| url.contains("smshinhanansimclick://")
							|| url.contains("smshinhancardusim://")
							|| url.contains("smartwall://")
							|| url.contains("appfree://")
							|| url.contains("v3mobile")
							|| url.endsWith(".apk")
							|| url.contains("market://")
							|| url.contains("ansimclick")
							|| url.contains("market://details?id=com.shcard.smartpay") || url
								.contains("shinhan-sr-ansimclick://"))) {
				Intent intent = null;
				// 인텐트 정합성 체크 : 2014 .01추가
				try {
					intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
					Log.e("intent getScheme     +++===>", intent.getScheme());
					Log.e("intent getDataString +++===>",
							intent.getDataString());
				} catch (URISyntaxException ex) {
					Log.e("Browser", "Bad URI " + url + ":" + ex.getMessage());
					mReturn = false;
				}
				try {
					boolean retval = true;
					// chrome 버젼 방식 : 2014.01 추가
					if (url.startsWith("intent")) { // chrome 버젼 방식
						// 앱설치 체크를 합니다.
						if (getPackageManager().resolveActivity(intent, 0) == null) {
							String packagename = intent.getPackage();
							if (packagename != null) {
								Uri uri = Uri.parse("market://search?q=pname:"
										+ packagename);
								intent = new Intent(Intent.ACTION_VIEW, uri);
								startActivity(intent);
								retval = true;
							}
						} else {
							intent.addCategory(Intent.CATEGORY_BROWSABLE);
							intent.setComponent(null);
							try {
								if (startActivityIfNeeded(intent, -1)) {
									retval = true;
								}
							} catch (ActivityNotFoundException ex) {
								retval = false;
							}
						}
					} else { // 구 방식
						Uri uri = Uri.parse(url);
						intent = new Intent(Intent.ACTION_VIEW, uri);
						startActivity(intent);
						retval = true;
					}
					return retval;
				} catch (ActivityNotFoundException e) {
					Log.e("error ===>", e.getMessage());
					e.printStackTrace();
					mReturn = false;
				}
			} else if (url.startsWith("smartxpay-transfer://")) {
				boolean isatallFlag = isPackageInstalled(
						getApplicationContext(), "kr.co.uplus.ecredit");
				if (isatallFlag) {
					boolean override = false;
					Intent intent = new Intent(Intent.ACTION_VIEW,
							Uri.parse(url));
					intent.addCategory(Intent.CATEGORY_BROWSABLE);
					intent.putExtra(Browser.EXTRA_APPLICATION_ID,
							getPackageName());

					try {
						startActivity(intent);
						override = true;
					} catch (ActivityNotFoundException ex) {
					}
					return override;
				} else {
					showAlert("확인버튼을 누르시면 구글플레이로 이동합니다.", "확인",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											Intent.ACTION_VIEW,
											Uri.parse(("market://details?id=kr.co.uplus.ecredit")));
									intent.addCategory(Intent.CATEGORY_BROWSABLE);
									intent.putExtra(
											Browser.EXTRA_APPLICATION_ID,
											getPackageName());
									startActivity(intent);
									overridePendingTransition(0, 0);
								}
							}, "취소", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});
					return true;
				}
			} else if (url.startsWith("ispmobile://")) {
				boolean isatallFlag = isPackageInstalled(
						getApplicationContext(), "kvp.jjy.MispAndroid320");
				if (isatallFlag) {
					boolean override = false;
					Intent intent = new Intent(Intent.ACTION_VIEW,
							Uri.parse(url));
					intent.addCategory(Intent.CATEGORY_BROWSABLE);
					intent.putExtra(Browser.EXTRA_APPLICATION_ID,
							getPackageName());

					try {
						startActivity(intent);
						override = true;
					} catch (ActivityNotFoundException ex) {
					}
					return override;
				} else {
					showAlert("확인버튼을 누르시면 구글플레이로 이동합니다.", "확인",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									view.loadUrl("http://mobile.vpay.co.kr/jsp/MISP/andown.jsp");
								}
							}, "취소", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});
					return true;
				}
			}
			return mReturn;
		}

		@Override
		public void onLoadResource(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onLoadResource(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
			if(mAppLogin && url.equals(getString(R.string.mainServerUrl) + getString(R.string.mainUrl))){
				mWebview.loadUrl("javascript:chg_mb_type('" + mAppLoginList[0] + "')");
				mAppLogin = false;
			}

			if (view.getUrl().indexOf(
					getString(R.string.mainServerUrl)
							+ getString(R.string.loginUrl)) > -1) {
				GcmStart();
			}

			if (mPageErrorCode == 1) {
				mPageError.setVisibility(View.VISIBLE);
				mPageErrorCode = 0;
			}
			if (mPageErrorCode == 2) {
				mPageError.setVisibility(View.VISIBLE);
				mPageErrorCode = 0;
			} else {
				mPageError.setVisibility(View.GONE);
			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			// TODO Auto-generated method stub
			super.onReceivedError(view, errorCode, description, failingUrl);
			Log.e("errorCode", Integer.toString(errorCode));
			switch (errorCode) {
			case ERROR_AUTHENTICATION: // 서버에서 사용자 인증 실패
			case ERROR_BAD_URL: // 잘못된 URL
			case ERROR_FAILED_SSL_HANDSHAKE: // SSL handshake 수행 실패
			case ERROR_FILE: // 일반 파일 오류
			case ERROR_FILE_NOT_FOUND: // 파일을 찾을 수 없습니다
			case ERROR_PROXY_AUTHENTICATION: // 프록시에서 사용자 인증 실패
			case ERROR_REDIRECT_LOOP: // 너무 많은 리디렉션
			case ERROR_TIMEOUT: // 연결 시간 초과
			case ERROR_TOO_MANY_REQUESTS: // 페이지 로드중 너무 많은 요청 발생
			case ERROR_UNKNOWN: // 일반 오류
			case ERROR_UNSUPPORTED_AUTH_SCHEME: // 지원되지 않는 인증 체계
			case ERROR_UNSUPPORTED_SCHEME:// URI가 지원되지 않는 방식
				mPageErrorCode = 1;
				break;
			case ERROR_CONNECT: // 서버로 연결 실패
			case ERROR_HOST_LOOKUP: // 서버 또는 프록시 호스트 이름 조회 실패
			case ERROR_IO: // 서버에서 읽거나 서버로 쓰기 실패
				mPageErrorCode = 2;
				break;
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (intent != null) {
			Uri uri = intent.getData();
			if (Intent.ACTION_VIEW.equals(intent.getAction())) {
				if (String.valueOf(uri).startsWith(
						"<ISP Return Custom Scheme> ")) {
					String result = uri.getQueryParameter("result");
					if ("success".equals(result)) {
						mWebview.loadUrl("javascript:doPostProcess();");
					} else if ("cancel".equals(result)) {
						mWebview.loadUrl("javascript:doCancelProcess();");
					} else {
						mWebview.loadUrl("javascript:doNoteProcess();");
					}
				} else if (String.valueOf(uri).startsWith(
						"<계좌이체 Return Custom Scheme>")) {
					// 계좌이체는 WebView가 아무일을 하지 않아도 됨
				}
			} else if (String.valueOf(uri).startsWith(
					"<paypin Return Custom Scheme>")) {
				mWebview.loadUrl("javascript:doPostProcess();");
			} else if (String.valueOf(uri).startsWith(
					"<paynow Return Custom Scheme>")) {
				// paynow는 WebView가 아무일을 하지 않아도 됨
			}
		}
	}

	private class WebViewChromClinetClass extends WebChromeClient {

		final Activity activity = KorMainConsumer.this;

		public boolean onJsAlert(WebView view, String url, String message,
				final android.webkit.JsResult result) {
			mCustomDialogAlert = new CustomDialogAlert(KorMainConsumer.this,
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							mCustomDialogAlert.dismiss();
							result.confirm();
						}
					}, "코리아JT", message);

			mCustomDialogAlert.show();
//			new AlertDialog.Builder(KorMainConsumer.this)
//					.setTitle(getString(R.string.app_name))
//					.setMessage(message)
//					.setPositiveButton(android.R.string.ok,
//							new AlertDialog.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int which) {
//									result.confirm();
//								}
//							}).setCancelable(false).create().show();
			return true;

		};

		public void onProgressChanged(WebView view, int progress) {
			super.onProgressChanged(view, progress);
			activity.setProgress(0);
			activity.setProgress(progress * 100);
			mProgressBar.setProgress(0);

			if (progress == mProgressBar.getMax()) {
				mProgressBar.setVisibility(View.GONE);
			} else {
				mProgressBar.setProgress(progress);
				mProgressBar.setVisibility(View.VISIBLE);
			}
		}
	}

	private void EndDialog() {
		mCustomDialogEnd = new CustomDialogEnd(KorMainConsumer.this,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mCustomDialogEnd.dismiss();
						finish();

					}
				}, new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mCustomDialogEnd.dismiss();
					}
				});

		mCustomDialogEnd.show();
//		AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
//		alt_bld.setMessage("어플리케이션을 종료 하시겠습니까?")
//				.setCancelable(false)
//				.setPositiveButton("예", new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int id) {
//
//						CookieSyncManager cookieSyncManager = CookieSyncManager
//								.createInstance(KorMainConsumer.this);
//						CookieManager cookieManager = CookieManager
//								.getInstance();
//						cookieManager.setAcceptCookie(true);
//						cookieManager.removeSessionCookie();
//						cookieSyncManager.sync();
//						finish();
//					}
//				})
//				.setNegativeButton("아니오",
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int id) {
//								dialog.cancel();
//							}
//						});
//		AlertDialog alert = alt_bld.create();
//		alert.setTitle(getString(R.string.app_name));
//		alert.show();
	}

	// 뒤로가기 하드웨어 버튼 눌렀을경우 처리
	public void onBackPressed() {
		if (!mLeftSlide_me.isClosed()) {
			setLeftSlidingUI();
		} else if (!mRightSlide_me.isClosed()) {
			setRightSlidingUI();
		} else if (mWebview.getUrl()
				.equals(getString(R.string.mainServerUrl)
						+ getString(R.string.mainUrl))) {
			EndDialog();
		} else {
			mWebview.goBack();
		}
	}

	public void setFlash(TextView tv) {
		int tvNum = Integer.parseInt(tv.getText().toString());
		tv.clearAnimation();
		if (tvNum > 0) {
			tv.setTextColor(Color.rgb(56, 144, 66));
			tv.setAnimation(mTextAnim);
		}
	}

	public void appChange() {
		if (checkApp()) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.addCategory(Intent.CATEGORY_BROWSABLE);
			intent.setData(Uri.parse("koreajt://producer?r="
					+ mPref.getValue(TagValuse.USERID, "") + "/"
					+ mPref.getValue(TagValuse.USERPW, "")));
			startActivity(intent);
		} else {
			Toast.makeText(getApplicationContext(),
					"상샌자판매(코리아JT)어플리케이션이 없습니다\n마켓으로 이동 합니다.",
					Toast.LENGTH_SHORT).show();
			Intent marketLaunch = new Intent(Intent.ACTION_VIEW); 
			marketLaunch.setData(Uri.parse("market://details?id=com.koreajt.producer"));
			startActivity(marketLaunch);
		}
	}

	public boolean checkApp() {
		try {

			PackageManager pm = getPackageManager();
			PackageInfo pi = pm.getPackageInfo(
					TagValuse.CHECK_PACKAGE_NAME.trim(),
					PackageManager.GET_META_DATA);
			ApplicationInfo appInfo = pi.applicationInfo;

			String appId = (String) appInfo.loadDescription(pm) + "";

			// // 설치된 패키지의 APP_ID 체크
			// if (appId.equals(`TagValuse.CHECK_APP_NAME)) {
			// return true;
			// }else{
			// return false;
			// }
			return true;
		}

		catch (NameNotFoundException e) {
			return false;
		}
	}

	// 더미액티비티 에서 저장한 url값을 받아오는 부분
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		String str = mPref.getValue(TagValuse.NOTIFIURL, null);
		if (str != null) {
			mWebview.loadUrl(str);
			mPref.delValue("url");
		}
	}
	
	public void searchItem(String str){
		mWebview.loadUrl(getString(R.string.mainServerUrl) + getString(R.string.searchUrl) + str);
		mImm.hideSoftInputFromWindow(mSearchText.getWindowToken(),0);
	}

	// 메인화면 소스 끝

	private class UserInfo extends AsyncTask<String, Void, Void> {

		ArrayList<String> mMenuText = new ArrayList<String>();

		@Override
		protected Void doInBackground(String... params) {

			try {
				URL xml = new URL(params[0]);
				InputStream is = xml.openStream();
				boolean[] initem = { false, false, false, false, false, false };
				XmlPullParserFactory factory = XmlPullParserFactory
						.newInstance();
				factory.setNamespaceAware(true);
				XmlPullParser xpp = factory.newPullParser();
				xpp.setInput(is, "UTF-8");
				int eventType = xpp.getEventType();
				String tagName = "", name = "", level = "", total = "", mNew = "", cancle = "", mReturn = "";
				boolean isItemTag = false;
				while (eventType != XmlPullParser.END_DOCUMENT) {

					if (eventType == XmlPullParser.START_TAG) {
						tagName = xpp.getName();
						if (tagName.equals("data"))
							isItemTag = true;
					} else if (eventType == XmlPullParser.TEXT) {

						if (isItemTag && tagName.equals("name")) {
							name += xpp.getText();
						}
						if (isItemTag && tagName.equals("level")) {
							level += xpp.getText();
						}
						if (isItemTag && tagName.equals("total")) {
							total += xpp.getText();
						}
						if (isItemTag && tagName.equals("new")) {
							mNew += xpp.getText();
						}
						if (isItemTag && tagName.equals("cancle")) {
							cancle += xpp.getText();
						}
						if (isItemTag && tagName.equals("return")) {
							mReturn += xpp.getText();
						}

					} else if (eventType == XmlPullParser.END_TAG) {
						// 태그명을 저장
						tagName = xpp.getName();

						if (tagName.equals("data")) {

							mMenuText.add(name);
							mMenuText.add(level);
							mMenuText.add(total);
							mMenuText.add(mNew);
							mMenuText.add(cancle);
							mMenuText.add(mReturn);

							name = "";
							level = "";
							total = "";
							mNew = "";
							cancle = "";
							mReturn = "";

							isItemTag = false; // 초기화
						}
					}
					eventType = xpp.next(); // 다음 이벤트 타입
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			userDataDisplay(mMenuText);
		}
	}

	private class CategoryParser extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			try {
				URL xml = new URL(params[0]);
				InputStream is = xml.openStream();
				XmlPullParserFactory factory = XmlPullParserFactory
						.newInstance();
				XmlPullParser xpp = factory.newPullParser();
				xpp.setInput(is, "UTF-8");

				int eventType = xpp.getEventType();
				boolean isItemTag = false;

				String tagName = "", title = "", dept = "", num = "", p_num = "";

				while (eventType != XmlPullParser.END_DOCUMENT) {

					if (eventType == XmlPullParser.START_TAG) {
						tagName = xpp.getName();
						if (tagName.equals("data"))
							isItemTag = true;
					} else if (eventType == XmlPullParser.TEXT) {

						if (isItemTag && tagName.equals("title")) {
							title += xpp.getText();
						}
						if (isItemTag && tagName.equals("depth")) {
							dept += xpp.getText();
						}
						if (isItemTag && tagName.equals("p_num")) {
							p_num += xpp.getText();
						}
						if (isItemTag && tagName.equals("num")) {
							num += xpp.getText();
						}

					} else if (eventType == XmlPullParser.END_TAG) {
						// 태그명을 저장
						tagName = xpp.getName();

						if (tagName.equals("data")) {
							mCategoryTitle.add(title);
							mCategoryDept.add(dept);
							mCategoryDeptnum.add(num);
							mCategoryP_num.add(p_num);

							title = ""; // 초기화
							dept = ""; // 초기화
							num = "";
							p_num = "";
							isItemTag = false; // 초기화
						}
					}
					eventType = xpp.next(); // 다음 이벤트 타입
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			setCategoryList();
		}

	}

	// 웹 페이지에서 호출하는 함수들
	private class AndroidBridge {
		// 사용자 계정 저장
		public void getUeerInfo(String id, String pw, String type) {
			mPref.put(TagValuse.USERID, id);
			mPref.put(TagValuse.USERPW, pw);
			mPref.put(TagValuse.USERTYPE, type);
			mType = type;
			mLoginFT = true;
		}

		public void getGcmRegId(String str) {
			GcmStart();
		}

		public void logout(String str) {
			mPref.delValue(TagValuse.USERID);
			mPref.delValue(TagValuse.USERPW);
			mLoginFT = false;
			mWebview.loadUrl(getString(R.string.mainServerUrl) + getString(R.string.mainUrl));
			Log.e("logout", str);
		}
	}

	/* 클릭 이밴트 설정 */
	class ClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			int btnId = v.getId();
			if (btnId == R.id.BtnSlide) {
				if (mLoginFT) {
					new UserInfo().execute(getString(R.string.mainServerUrl)
							+ getString(R.string.xmlUrl)
							+ mPref.getValue(TagValuse.USERID, ""));
					mMenuLoding.setVisibility(View.VISIBLE);
					setLeftSlidingUI();
				} else {
					Toast.makeText(getApplicationContext(),
							"로그인을 하셔야 개인메뉴를 보실수 있습니다.", Toast.LENGTH_SHORT)
							.show();
					mWebview.loadUrl(getString(R.string.mainServerUrl)
							+ getString(R.string.loginUrl));
				}
			} else if (btnId == R.id.sellChange) {
				appChange();
				setLeftSlidingUI();
			} else if (btnId == R.id.logout) {
				mWebview.loadUrl(getString(R.string.mainServerUrl)
						+ getString(R.string.logoutUrl));
				setLeftSlidingUI();
			} else if (btnId == R.id.BtnHome) {
				mWebview.loadUrl(getString(R.string.mainServerUrl)
						+ getString(R.string.mainUrl));
			} else if (btnId == R.id.BtnCategroy) {
				setRightSlidingUI();
			} else if (btnId == R.id.btnSubMenu01) {
				mWebview.loadUrl(getString(R.string.mainServerUrl)
						+ getString(R.string.menuUrl01));
				setLeftSlidingUI();
			} else if (btnId == R.id.btnSubMenu02) {
				mWebview.loadUrl(getString(R.string.mainServerUrl)
						+ getString(R.string.menuUrl02));
				setLeftSlidingUI();
			} else if (btnId == R.id.btnSubMenu03) {
				mWebview.loadUrl(getString(R.string.mainServerUrl)
						+ getString(R.string.menuUrl03));
				setLeftSlidingUI();
			} else if (btnId == R.id.btnSubMenu04) {
				Toast.makeText(KorMainConsumer.this, "준비중 입니다",
						Toast.LENGTH_SHORT).show();
				setLeftSlidingUI();
			} else if (btnId == R.id.btnSubMenu05) {
				Toast.makeText(KorMainConsumer.this, "준비중 입니다",
						Toast.LENGTH_SHORT).show();
				setLeftSlidingUI();
			} else if (btnId == R.id.btnSubMenu06) {
				mWebview.loadUrl(getString(R.string.mainServerUrl)
						+ getString(R.string.menuUrl06));
				setLeftSlidingUI();
			} else if (btnId == R.id.searchBtn) {
				String searchStr = mSearchText.getText().toString();
				if (!searchStr.equals("")) {
					searchItem(searchStr);
					setRightSlidingUI();
					mSearchText.setText("");
				} else {
					Toast.makeText(getApplicationContext(), "상품명을 입력 하세요",
							Toast.LENGTH_SHORT).show();
				}

			} else if (btnId == R.id.netErrorData) {
				Intent netIntent = new Intent(
						android.provider.Settings.ACTION_WIRELESS_SETTINGS);
				startActivity(netIntent);
			} else if (btnId == R.id.netErrorWifi) {
				Intent netIntent = new Intent(
						android.provider.Settings.ACTION_WIFI_SETTINGS);
				startActivity(netIntent);
			} else if (btnId == R.id.webReload) {
				mWebview.reload();
			}
		}
	}

}
