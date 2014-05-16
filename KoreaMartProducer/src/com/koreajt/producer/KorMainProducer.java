package com.koreajt.producer;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.koreajt.producer.gcm.RegisterApp;
import com.koreajt.producer.parsing.XmlParser;
import com.koreajt.producer.util.CustomDialogAlert;
import com.koreajt.producer.util.CustomDialogEnd;
import com.koreajt.producer.util.CustomDialogMovie;
import com.koreajt.producer.util.CustomDialogPhoto;
import com.koreajt.producer.util.HorizontalSlider;
import com.koreajt.producer.util.TagValuse;
import com.navdrawer.SimpleSideDrawer;

/**
 * @author 권영보 140321
 */

public class KorMainProducer extends Activity {
	boolean menuOut = false;
	TextView mUserLevel, mUserName, mMenuNum01, mMenuNum02, mMenuNum03,
			mMenuNum04, mMenuNum05, mMenuNum06, mMenuNum07;
	LinearLayout mMenu01, mMenu02, mMenu03, mMenu04, mMenu05, mMenu06, mMenu07,
			mMenuNotice, mMainView, mMenuLoding;
	FrameLayout mWebViewContainer = null;
	LinearLayout mGongsa;
	Button mGongsaBtn;
	// LinearLayout mHelpMain;
	LinearLayout mHelpLayout;
	Button mBtnHelp;
	ImageView mHelpImageView;
	boolean mHelpBtnOnOver = false;

	LinearLayout mBliendWebview, mPageError, mRecLatout, mHelpCancle;
	Button mChangeBuy, mLogout;
	FrameLayout mHeader;
	XmlParser mXmlP;
	Handler mHandler;
	int mPageErrorCode = 0;
	CustomDialogPhoto mCustomDialogPhoto;
	CustomDialogMovie mCustomDialogMovie;
	CustomDialogAlert mCustomDialogAlert;
	CustomDialogEnd mCustomDialogEnd;
	boolean mLoginFT = false;
	String mSaveInsertUrl = "";
	AnimationDrawable mAnimationBtn;
	boolean mAppLogin = false;
	String[] mAppLoginList = null;
	int mDeviceWidth;
	// ImageView mMyMenuHelp01, mMainHelp;

	public static WebView mWebview = null;
	public int mUploadMode = 0;
	private String mPostParmFname = "";
	private String mPostParmIdx = "";
	private String mPostParmType = "";
	private String mPostParmNum = "";
	private String mPostRecNum = "";

	Uri mImageCaptureUri = null;
	Uri mMoveCaptureUri = null;

	Uri mImgUrl = null;
	Uri mMoveUrl = null;

	private boolean mLeftSlide = false;

	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	GoogleCloudMessaging mGcm;
	AtomicInteger mMsgId = new AtomicInteger();
	String mRegid;
	String mNotificationUrl = null;

	ProgressBar mProgressBar;
	UrlSharedPreference mPref = new UrlSharedPreference(this);
	SimpleSideDrawer mSlide_me;

	private int mServerResponseCode = 0;
	private ProgressDialog mDialog = null;

	private String mFilePath = null;

	/*
	 * 20140403 권영보 녹음 부분 추가되는 변수들
	 */
	private MediaRecorder mRecorder = null;
	private MediaPlayer mPlayer = null;
	private int mRecState = TagValuse.REC_STOP;
	private int mPlayerState = TagValuse.PLAY_STOP;
	private ProgressBar mRecProgressBar;
	private LinearLayout mBtnStartRec, mBtnStartPlay, mBtnSend;
	private String mFileName = null;
	private TextView mTvRecMaxPoint, mTvRecPoint, mTvBtnRec, mTvBtnPlay;
	private ImageView mImgRec, mImgPlay;
	private int mCurRecTimeMs = 0;
	private int mCurProgressTimeDisplay = 0;

	int mPlayCount = 0;
	private String mPlayValuse, mRecValuse;

	private ValueCallback<Uri> mUploadMessage;
	private final static int FILECHOOSER_RESULTCODE = 5;

	private Animation mTextAnim;

	String mGetCookie = null;

	HttpURLConnection mConn = null;

	// 위치정보
	LocationManager mLocationManager;
	Criteria mCriteria;
	String mProvider;
	Location mLocation;
	double mLat;
	double mLng;

	// BasicClientCookie mBcc = new BasicClientCookie(name, value)

	// 녹음시 SeekBar처리
	Handler mProgressHandler = new Handler() {
		public void handleMessage(Message msg) {
			mRecProgressBar.setMax(30000);
			mTvRecMaxPoint.setText("00:30");
			mCurRecTimeMs = mCurRecTimeMs + 100;
			mCurProgressTimeDisplay = mCurProgressTimeDisplay + 100;
			if (mCurRecTimeMs / 1000 < 10) {
				mRecValuse = "00:0" + Integer.toString(mCurRecTimeMs / 1000);
			} else {
				mRecValuse = "00:" + Integer.toString(mCurRecTimeMs / 1000);
			}
			mTvRecPoint.setText(mRecValuse);
			// 녹음시간이 음수이면 정지버튼을 눌러 정지시켰음을 의미하므로
			// SeekBar는 그대로 정지시키고 레코더를 정지시킨다.
			if (mCurRecTimeMs < 0) {
			}
			// 녹음시간이 아직 최대녹음제한시간보다 작으면 녹음중이라는 의미이므로
			// SeekBar의 위치를 옮겨주고 0.1초 후에 다시 체크하도록 한다.
			else if (mCurRecTimeMs < 30000) {
				mRecProgressBar.setProgress(mCurProgressTimeDisplay);
				mProgressHandler.sendEmptyMessageDelayed(0, 100);
			}
			// 녹음시간이 최대 녹음제한 시간보다 크면 녹음을 정지 시킨다.
			else {
				mBtnStartRecOnClick();
			}

		}
	};

	// 재생시 SeekBar 처리
	Handler mProgressHandler2 = new Handler() {
		public void handleMessage(Message msg) {
			mPlayCount++;
			if (mPlayCount % 10 == 0 && mPlayCount / 10 > 0) {
				if (mPlayCount / 10 < 10) {
					mPlayValuse = "00:0" + mPlayCount / 10;
				} else {
					mPlayValuse = "00:" + mPlayCount / 10;
				}
				mTvRecPoint.setText(mPlayValuse);
			}

			if (mPlayer == null)
				return;

			try {
				if (mPlayer.isPlaying()) {
					mRecProgressBar.setProgress(mPlayer.getCurrentPosition());
					mProgressHandler2.sendEmptyMessageDelayed(0, 100);
				}
			} catch (IllegalStateException e) {
			} catch (Exception e) {
			}
		}
	};

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		android.util.Log.d("TAG", "TOTAL MEMORY : "
				+ (Runtime.getRuntime().totalMemory() / (1024 * 1024)) + "MB");
		android.util.Log.d("TAG", "MAX MEMORY : "
				+ (Runtime.getRuntime().maxMemory() / (1024 * 1024)) + "MB");
		android.util.Log.d("TAG", "FREE MEMORY : "
				+ (Runtime.getRuntime().freeMemory() / (1024 * 1024)) + "MB");
		android.util.Log.d("TAG", "ALLOCATION MEMORY : "
				+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
						.freeMemory()) / (1024 * 1024)) + "MB");
		
		mDeviceWidth = getLcdSIzeWidth();

		setContentView(R.layout.main_activity);

		final int sdkVersion = android.os.Build.VERSION.SDK_INT;
		mSlide_me = new SimpleSideDrawer(this);
		mSlide_me.setLeftBehindContentView(R.layout.menu_activity);

		mTextAnim = AnimationUtils.loadAnimation(KorMainProducer.this,R.anim.text_alpha);
		mAnimationBtn = (AnimationDrawable) getResources().getDrawable(
				R.anim.btn_anim);
		mCustomDialogPhoto = new CustomDialogPhoto(KorMainProducer.this);
		mCustomDialogMovie = new CustomDialogMovie(KorMainProducer.this);
		mCustomDialogAlert = new CustomDialogAlert(KorMainProducer.this);
		mCustomDialogEnd = new CustomDialogEnd(KorMainProducer.this);

		// google-play-service가 가능한지 체크
		if (checkPlayServices()) {
			mGcm = GoogleCloudMessaging.getInstance(KorMainProducer.this);
			mRegid = getRegistrationId(KorMainProducer.this);
		}

		setLayout();

		webInit();

		// setHelpFlash("on");

		if (getIntent() != null) {
			Uri uri = getIntent().getData();
			if (uri != null) {
				String info = uri.getQueryParameter("r");
				mAppLogin = true;
				mAppLoginList = new String[2];
				mAppLoginList = info.split("/");
			}
		}

		Log.e("asdfasdfa", "onCreate");

		if (mAppLogin) {
			mWebview.loadUrl(getString(R.string.mainServerUrl)
					+ getString(R.string.loginUrl) + "?id=" + mAppLoginList[0]
					+ "&pw=" + mAppLoginList[1]);
		} else if (!mPref.getValue(TagValuse.USERID, "").equals("")) {
			mWebview.loadUrl(getString(R.string.mainServerUrl)
					+ getString(R.string.loginUrl) + "?id="
					+ mPref.getValue(TagValuse.USERID, "") + "&pw="
					+ mPref.getValue(TagValuse.USERPW, ""));
		} else {
			mWebview.loadUrl(getString(R.string.mainServerUrl)
					+ getString(R.string.loginUrl));
		}

		// GcmStart();

		mProgressBar = (HorizontalSlider) findViewById(R.id.progress_horizontal);
		findViewById(R.id.BtnSlide).setOnClickListener(new ClickListener());
		findViewById(R.id.BtnHome).setOnClickListener(new ClickListener());
		findViewById(R.id.netErrorData).setOnClickListener(new ClickListener());
		findViewById(R.id.netErrorWifi).setOnClickListener(new ClickListener());
		findViewById(R.id.webReload).setOnClickListener(new ClickListener());

		mMenu01.setOnClickListener(new ClickListener());
		mMenu02.setOnClickListener(new ClickListener());
		mMenu03.setOnClickListener(new ClickListener());
		mMenu04.setOnClickListener(new ClickListener());
		mMenu05.setOnClickListener(new ClickListener());
		mMenu06.setOnClickListener(new ClickListener());
		mMenu07.setOnClickListener(new ClickListener());
		mMenuNotice.setOnClickListener(new ClickListener());
		mChangeBuy.setOnClickListener(new ClickListener());
		mLogout.setOnClickListener(new ClickListener());

		mHelpLayout.setOnClickListener(null);
		mBliendWebview.setOnClickListener(null);
		mBtnHelp.setOnClickListener(new ClickListener());
		mHeader.setOnClickListener(null);

		mGongsaBtn.setOnClickListener(new ClickListener());
		mGongsa.setOnClickListener(null);
		// 녹음부분 버튼 리스터
		mRecLatout.setOnClickListener(null);
		mBtnStartRec.setOnClickListener(new ClickListenerRec());
		mBtnStartPlay.setOnClickListener(new ClickListenerRec());
		mBtnSend.setOnClickListener(new ClickListenerRec());

		// setCategoryList();

	}

	private String findAddress(double lat, double lng) {
		String addStr = "error";
		Geocoder geocoder = new Geocoder(this, Locale.getDefault());
		List<Address> list = null;

		try {
			list = geocoder.getFromLocation(lat, lng, 1);

		} catch (Exception e) {
			// TODO: handle exception
		}
		if (list == null) {
			Log.e("Location", "주소값 가져오기 실패");
		} else if (list.size() > 0) {
			Address address = list.get(0);
			addStr = address.getAdminArea() + " " + address.getLocality() + " "
					+ address.getThoroughfare();

		}
		return addStr;

	}

	public void webInit() {
		mWebview.setWebViewClient(new WebViewClientClass());
		mWebview.setWebChromeClient(new WebViewChromClinetClass());
		mWebview.getSettings().setSavePassword(false);
		mWebview.getSettings().setSaveFormData(true);
		mWebview.getSettings().setJavaScriptEnabled(true);
		mWebview.getSettings().setBuiltInZoomControls(false);
		mWebview.getSettings().setAllowFileAccess(true);
		mWebview.addJavascriptInterface(new AndroidBridge(), "kormart");
	}

	private void setLayout() {
		mUserLevel = (TextView) findViewById(R.id.userLevel);
		mUserName = (TextView) findViewById(R.id.userName);
		mBliendWebview = (LinearLayout) findViewById(R.id.blindLayout);
		mPageError = (LinearLayout) findViewById(R.id.pageError);
		mWebViewContainer = (FrameLayout) findViewById(R.id.webViewContainer);
		mWebview = new WebView(this);
		mWebViewContainer.addView(mWebview);
		mMenu01 = (LinearLayout) findViewById(R.id.btnSubMenu01); // 신규주문
		mMenu02 = (LinearLayout) findViewById(R.id.btnSubMenu02); // 발송요청
		mMenu03 = (LinearLayout) findViewById(R.id.btnSubMenu03); // 반품/교환/환불요청
		mMenu04 = (LinearLayout) findViewById(R.id.btnSubMenu04); // 취소요청
		mMenu05 = (LinearLayout) findViewById(R.id.btnSubMenu05); // 신규상품문의
		mMenu06 = (LinearLayout) findViewById(R.id.btnSubMenu06); // 정산알림
		mMenu07 = (LinearLayout) findViewById(R.id.btnSubMenu07); // 흥정내역
		mMenuNotice = (LinearLayout) findViewById(R.id.btnNotice);
		mRecLatout = (LinearLayout) findViewById(R.id.recLayout);
		mMenuNum01 = (TextView) findViewById(R.id.SubMenuText01); // 신규주문 num
		mMenuNum02 = (TextView) findViewById(R.id.SubMenuText02); // 발송요청 num
		mMenuNum03 = (TextView) findViewById(R.id.SubMenuText03); // 반품/교환/환불요청
																	// num
		mMenuNum04 = (TextView) findViewById(R.id.SubMenuText04); // 취소요청 num
		mMenuNum05 = (TextView) findViewById(R.id.SubMenuText05); // 신규상품문의 num
		mMenuNum06 = (TextView) findViewById(R.id.SubMenuText06); // 정산알림 num
		mMenuNum07 = (TextView) findViewById(R.id.SubMenuText07); // 흥정내역 num
		mHeader = (FrameLayout) findViewById(R.id.header);
		mMainView = (LinearLayout) findViewById(R.id.app);
		mMenuLoding = (LinearLayout) findViewById(R.id.menuLoding);
		mChangeBuy = (Button) findViewById(R.id.buyChange);
		mLogout = (Button) findViewById(R.id.logout);

		mHelpLayout = (LinearLayout) findViewById(R.id.helpLayout);
		mBtnHelp = (Button) findViewById(R.id.helpBtn);
		mHelpImageView = (ImageView) findViewById(R.id.imgv_help);

		mGongsa = (LinearLayout) findViewById(R.id.gongsa);
		mGongsaBtn = (Button) findViewById(R.id.gongsaCancle);

		// mMyMenuHelp01 = (ImageView) findViewById(R.id.myMenuHelp01);
		// mMainHelp = (ImageView) findViewById(R.id.mainHelp);
		// mFirstStartLayout = (LinearLayout) findViewById(R.id.help);
		// mHelpCancle = (LinearLayout) findViewById(R.id.helpCancle);

		// 녹음부분 버튼들
		mBtnStartRec = (LinearLayout) findViewById(R.id.btnStartRec);
		mBtnStartPlay = (LinearLayout) findViewById(R.id.btnStartPlay);
		mBtnSend = (LinearLayout) findViewById(R.id.btnSend);
		mRecProgressBar = (ProgressBar) findViewById(R.id.recProgressBar);
		mTvRecPoint = (TextView) findViewById(R.id.tvRecStartPoint);
		mTvRecMaxPoint = (TextView) findViewById(R.id.tvRecMaxPoint);
		mTvBtnPlay = (TextView) findViewById(R.id.btnStartText);
		mTvBtnRec = (TextView) findViewById(R.id.btnRecText);
		mImgPlay = (ImageView) findViewById(R.id.btnStartImg);
		mImgRec = (ImageView) findViewById(R.id.btnRecImg);

	}

	public int getLcdSIzeWidth() {
		return ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay().getWidth();
	}

	public void setSlidingUI() {
		// mSlide_me.isClosed() true 면 열린상태
		if (mSlide_me.isClosed()) {
			mLeftSlide = true;
		} else {
			mLeftSlide = false;

		}
		mSlide_me.toggleLeftDrawer();
	}

	public void helpLayout(int type) {

		/*
		 * ViewFlipper 번호 별 화면 0 : 메인화면 도움말 1 : 상품목록 도움말 2 : 상품등록/수정 첫번째 화면 도움말
		 * 3 : 상품등록/수정 두번째 화면 도움말 4 : 상품등록/수정 세번째 화면 도움말 5 : 상품등록/수정 네번째 화면 도움말
		 */

		if (type == 0) {
			mBtnHelp.setBackgroundResource(R.drawable.btn_help_over);
			mHelpBtnOnOver = false;
			mHelpLayout.setVisibility(View.VISIBLE);
			if (mWebview.getUrl().equals(
					getString(R.string.mainServerUrl)
							+ getString(R.string.mainUrl))) {
				helpImageSet(R.drawable.help_main, mDeviceWidth, 1.4);
			} else if (mWebview.getUrl().indexOf(
					getString(R.string.mainServerUrl)
							+ getString(R.string.mainMenuUrl01)) > -1) {
				helpImageSet(R.drawable.help_sub01, mDeviceWidth, 2.4);
			} else if (mWebview.getUrl().equals(
					getString(R.string.mainServerUrl)
							+ getString(R.string.mainMenuUrl01_01_01))) {
				helpImageSet(R.drawable.help_input01, mDeviceWidth, 1.8);
			} else if (mWebview.getUrl().indexOf(
					getString(R.string.mainMenuUrl01_01_02)) > -1) {
				helpImageSet(R.drawable.help_input01, mDeviceWidth, 1.8);
			} else if (mWebview.getUrl().indexOf(
					getString(R.string.mainMenuUrl01_02)) > -1) {
				helpImageSet(R.drawable.help_input02, mDeviceWidth, 2.7);
			} else if (mWebview.getUrl().indexOf(
					getString(R.string.mainMenuUrl01_03)) > -1) {
				helpImageSet(R.drawable.help_input03, mDeviceWidth, 1.8);
			} else if (mWebview.getUrl().indexOf(
					getString(R.string.mainMenuUrl01_04)) > -1) {
				helpImageSet(R.drawable.help_input04, mDeviceWidth, 1.4);
			}
		} else {
			mBtnHelp.setBackgroundResource(R.drawable.btn_help_on);
			mHelpBtnOnOver = true;
			mHelpLayout.setVisibility(View.GONE);
			if (mWebview.getUrl().equals(
					getString(R.string.mainServerUrl)
							+ getString(R.string.mainUrl))) {
				mPref.put(getString(R.string.mainUrl), false);
			} else if (mWebview.getUrl().indexOf(
					getString(R.string.mainServerUrl)
							+ getString(R.string.mainMenuUrl01)) > -1) {
				mPref.put(getString(R.string.mainMenuUrl01), false);
			} else if (mWebview.getUrl().equals(
					getString(R.string.mainServerUrl)
							+ getString(R.string.mainMenuUrl01_01_01))) {
				mPref.put(getString(R.string.mainMenuUrl01_01_02), false);
			} else if (mWebview.getUrl().indexOf(
					getString(R.string.mainMenuUrl01_01_02)) > -1) {
				mPref.put(getString(R.string.mainMenuUrl01_01_02), false);
			} else if (mWebview.getUrl().indexOf(
					getString(R.string.mainMenuUrl01_02)) > -1) {
				mPref.put(getString(R.string.mainMenuUrl01_02), false);
			} else if (mWebview.getUrl().indexOf(
					getString(R.string.mainMenuUrl01_03)) > -1) {
				mPref.put(getString(R.string.mainMenuUrl01_03), false);
			} else if (mWebview.getUrl().indexOf(
					getString(R.string.mainMenuUrl01_04)) > -1) {
				mPref.put(getString(R.string.mainMenuUrl01_04), false);
			}
		}
	}

	public void helpImageSet(int resource, int width, double height) {
		mHelpImageView.setImageResource(resource);
		mHelpImageView.getLayoutParams().width = width;
		mHelpImageView.getLayoutParams().height = (int) (width * height);
	}

	/*
	 * Gcm 서비스 시작
	 */
	@SuppressLint("NewApi")
	private void GcmStart() {
		if (checkPlayServices()) {
			mGcm = GoogleCloudMessaging.getInstance(KorMainProducer.this);
			// GCM 서버에 등록된 regId를 가지고 옴
			mRegid = getRegistrationId(KorMainProducer.this);
			// 없을경우 생성
			if (mRegid.isEmpty()) {
				new RegisterApp(KorMainProducer.this, mGcm,
						getAppVersion(KorMainProducer.this)).execute();
				Log.i("GcmStart", "RegId create");
			} else {
				mWebview.loadUrl("javascript:callReg('" + mRegid + "')");
				Log.i("GcmStart", "RegId Call = " + mRegid);
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
		int currentVersion = getAppVersion(KorMainProducer.this);
		if (registeredVersion != currentVersion) {
			Log.e(TagValuse.TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	private SharedPreferences getGCMPreferences(Context context) {
		return getSharedPreferences(KorMainProducer.class.getSimpleName(),
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

	private View.OnClickListener galClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.gal:
				selectGallery();
				mCustomDialogPhoto.dismiss();
				break;
			case R.id.m_gal:
				selectMovieGallery();
				mCustomDialogMovie.dismiss();
				break;
			default:
				break;
			}
		}
	};

	private View.OnClickListener camClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.cam:
				selectCamera();
				mCustomDialogPhoto.dismiss();
				break;
			case R.id.m_cam:
				selectMovie();
				mCustomDialogMovie.dismiss();
				break;
			default:
				break;
			}
		}
	};

	// 웹 페이지에서 호출하는 함수들

	// 갤러리 선택했을 경우 실행 되는 부분
	public void selectGallery() {
		Intent pickerIntent = new Intent(Intent.ACTION_PICK);
		pickerIntent.setType("image/*");
		startActivityForResult(pickerIntent, TagValuse.SELECT_GALLERY);
//		Intent i = new Intent(getApplicationContext(), GalleryView.class);
//		startActivityForResult(i, TagValuse.SELECT_GALLERY);
	}

	// 카메라 선택 했을경우 실행 되는 부분
	public void selectCamera() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		File file = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DCIM).toString()
				+ "/NaraJT");
		if (!file.exists()) {
			file.mkdir();
		}
		String url = "tmp_" + String.valueOf(System.currentTimeMillis())
				+ ".jpg";
		mImageCaptureUri = Uri.fromFile(new File(file, url));
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
				mImageCaptureUri);
		intent.putExtra(android.provider.MediaStore.EXTRA_SCREEN_ORIENTATION, 2);
		startActivityForResult(intent, TagValuse.SELECT_CAMERA);

	}

	public void selectMovieGallery() {
		Intent pickerIntent = new Intent(Intent.ACTION_PICK);
		pickerIntent.setType("video/*");
		startActivityForResult(pickerIntent, TagValuse.SELECT_MOVIE_GALLERY);
	}

	// 동영상 선택 했을경우 실행되는 부분
	public void selectMovie() {
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		File file = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DCIM).toString()
				+ "/NaraJTMove");
		if (!file.exists()) {
			file.mkdir();
		}
		String url = "tmp_" + String.valueOf(System.currentTimeMillis())
				+ ".mp4";
		// mMoveCaptureUri = file.getPath() + url;
		mMoveCaptureUri = Uri.fromFile(new File(file, url));

		// Log.e("url", mMoveCaptureUri);

		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
				mMoveCaptureUri);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
		intent.putExtra(android.provider.MediaStore.EXTRA_SCREEN_ORIENTATION, 0);
		intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT,
				(long) (1024 * 1024 * 19.9));
		startActivityForResult(intent, TagValuse.SELECT_MOVIE);
	}

	/*
	 * 카메라, 갤러리 실행 후 이미지를 리턴 받는 부분
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case TagValuse.SELECT_GALLERY:

			try {
				if (data != null) {
					Uri currImageURI = data.getData();
					mFilePath = getRealPathFromURI(currImageURI);
//					mFilePath = data.getStringExtra("key");
					FileUpload("이미지 업로드중...", mFilePath);
				}
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				Toast.makeText(KorMainProducer.this, "갤러리를 이용하여 증록하여 주세요",
						Toast.LENGTH_LONG).show();
			}
			break;
		case TagValuse.SELECT_CAMERA:
			if (mImageCaptureUri != null) {
				try {
					mImgUrl = mImageCaptureUri;
					mFilePath = mImgUrl.toString().replace("file://", "");
					FileUpload("이미지 업로드중...", mFilePath);
				} catch (Exception e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					String exceptionAsStrting = sw.toString();
					Log.e("ImageUploadError", exceptionAsStrting);
				}
			}
			break;
		case TagValuse.SELECT_MOVIE_GALLERY:
			try {
				if (data != null) {
					Uri currImageURI = data.getData();
					mFilePath = getRealPathFromURI(currImageURI);
					File sourceFile = new File(mFilePath);
					long fileSize = sourceFile.length();
					double fileSigeM = fileSize / (1024 * 1024);
					if (fileSigeM <= 20.0) {
						FileUpload("동영상 업로드중...", mFilePath);
					} else {
						Toast.makeText(KorMainProducer.this,
								"20M가 넘는 동영상 파일은 등록하실수 없습니다.",
								Toast.LENGTH_LONG).show();
					}
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				Toast.makeText(KorMainProducer.this, "갤러리를 이용하여 증록하여 주세요",
						Toast.LENGTH_LONG).show();
			}
			break;
		case TagValuse.SELECT_MOVIE:
			if (mMoveCaptureUri != null) {
				try {
					mMoveUrl = mMoveCaptureUri;
					mFilePath = mMoveUrl.toString().replace("file://", "");
					FileUpload("동영상 업로드중...", mFilePath);
				} catch (Exception e) {
					StringWriter sw = new StringWriter();
					e.printStackTrace(new PrintWriter(sw));
					String exceptionAsStrting = sw.toString();
					Log.e("MoveUploadError", exceptionAsStrting);
				}
			}
			break;
		case FILECHOOSER_RESULTCODE:
			if (null == mUploadMessage)
				return;
			Uri result = data == null || resultCode != RESULT_OK ? null : data
					.getData();
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;

			break;
		}
		System.gc();
	}

	public int exifOrientationToDegrees(int exifOrientation) {
		if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
			return 90;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
			return 180;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
			return 270;
		}
		return 0;
	}

	// 파일 업로드 시작
	public void FileUpload(String msg, final String path) {
		mDialog = ProgressDialog.show(KorMainProducer.this, "", msg, true);
		Log.e("파일업로드 시작", "시작");
		new Thread(new Runnable() {
			public void run() {
				uploadFile(path);
			}
		}).start();
	}

	// 파일 이름 가지고 오는 메소드
	public String getRealPathFromURI(Uri contentUri) {
		Cursor cursor = null;
		int column_index = 0;
		try {
			String[] proj = { MediaStore.Images.Media.DATA };
			cursor = managedQuery(contentUri, proj, null, null, null);
			column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cursor.getString(column_index);
	}

	public void userDataDisplay(ArrayList<String> userInfo) {
		mUserLevel.setText(userInfo.get(1) + "등급");
		mUserName.setText(userInfo.get(0) + " 님 반갑습니다");
		mMenuNum01.setText(userInfo.get(2));
		mMenuNum02.setText(userInfo.get(3));
		mMenuNum03.setText(userInfo.get(4));
		mMenuNum04.setText(userInfo.get(5));
		mMenuNum05.setText(userInfo.get(6));
		mMenuNum06.setText(userInfo.get(7));
		mMenuNum07.setText(userInfo.get(8));

		setFlash(mMenuNum01);
		setFlash(mMenuNum02);
		setFlash(mMenuNum03);
		setFlash(mMenuNum04);
		setFlash(mMenuNum05);
		setFlash(mMenuNum06);
		setFlash(mMenuNum07);
		int total = Integer.parseInt(userInfo.get(2))
				+ Integer.parseInt(userInfo.get(3))
				+ Integer.parseInt(userInfo.get(4))
				+ Integer.parseInt(userInfo.get(5))
				+ Integer.parseInt(userInfo.get(6))
				+ Integer.parseInt(userInfo.get(7))
				+ Integer.parseInt(userInfo.get(8));
	}

	/*
	 * 20140331 파일 업로드 변경 스마트폰의 파일 이름을 그대로 서버에 저장 하도록 변경
	 */

	public int uploadFile(String sourceFileUri) {
		if (sourceFileUri != null) {
			String fileName = sourceFileUri;
			DataOutputStream dos = null;
			FileInputStream fileInputStream = null;
			int bytesRead, bytesAvailable, bufferSize;
			byte[] buffer;
			int maxBufferSize = 1 * 1024 * 1024;
			File sourceFile = null;
			try {
				sourceFile = new File(fileName);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				StringWriter sw = new StringWriter();
				e1.printStackTrace(new PrintWriter(sw));
				String exceptionAsStrting = sw.toString();
				Log.e("ImageUploadError", exceptionAsStrting);

				mDialog.dismiss();
				Log.e("uploadFile", "Source File not exist :" + mFilePath);
			}

			if (!sourceFile.isFile()) {
				mDialog.dismiss();
				Log.e("uploadFile", "Source File not exist :" + mFilePath);
				return 0;
			} else {
				try {
					// open a URL connection to the Servlet
					fileInputStream = new FileInputStream(sourceFile);
					URL url = new URL(getString(R.string.mainServerUrl)
							+ getString(R.string.uploadServerUrl) + "?fname="
							+ mPostParmFname + "&idx=" + mPostParmIdx
							+ "&type=" + mPostParmType + "&num=" + mPostParmNum);

					// Open a HTTP connection to the URL
					mConn = (HttpURLConnection) url.openConnection();
					if (mGetCookie != null) {
						mConn.setRequestProperty("Cookie", mGetCookie);
					}
					mConn.setDoInput(true); // Allow Inputs
					mConn.setDoOutput(true); // Allow Outputs
					mConn.setUseCaches(false); // Don't use a Cached Copy
					mConn.setRequestMethod("POST");
					mConn.setRequestProperty("Connection", "Keep-Alive");
					mConn.setRequestProperty("ENCTYPE", "multipart/form-data");
					mConn.setRequestProperty("Content-Type",
							"multipart/form-data;boundary="
									+ TagValuse.BOUNDARY);
					mConn.setRequestProperty("uploaded_file", fileName);

					dos = new DataOutputStream(mConn.getOutputStream());

					dos.writeBytes(TagValuse.TWOHYPHENS + TagValuse.BOUNDARY
							+ TagValuse.LINEEND);
					dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
							+ fileName + "\"" + TagValuse.LINEEND);

					dos.writeBytes(TagValuse.LINEEND);

					// create a buffer of maximum size
					bytesAvailable = fileInputStream.available();

					bufferSize = Math.min(bytesAvailable, maxBufferSize);
					buffer = new byte[bufferSize];

					// read file and write it into form...
					bytesRead = fileInputStream.read(buffer, 0, bufferSize);

					while (bytesRead > 0) {
						Log.e("파일업로드", "업로드중");
						dos.write(buffer, 0, bufferSize);
						bytesAvailable = fileInputStream.available();
						bufferSize = Math.min(bytesAvailable, maxBufferSize);
						bytesRead = fileInputStream.read(buffer, 0, bufferSize);

					}

					// send multipart form data necesssary after file data...
					dos.writeBytes(TagValuse.LINEEND);
					dos.writeBytes(TagValuse.TWOHYPHENS + TagValuse.BOUNDARY
							+ TagValuse.TWOHYPHENS + TagValuse.LINEEND);

					// Responses from the server (code and message)
					mServerResponseCode = mConn.getResponseCode();

					String serverResponseMessage = mConn.getResponseMessage();

					Log.i("uploadFile", "HTTP Response is : "
							+ serverResponseMessage + ": "
							+ mServerResponseCode);

					if (mServerResponseCode == 200
							|| mServerResponseCode == 413) {
						runOnUiThread(new Runnable() {
							public void run() {
								// messageText.setText(msg);
								Log.e("파일업로드", "업로드완료");
								Toast.makeText(KorMainProducer.this,
										"파일 업로드가 완료 되었습니다.", Toast.LENGTH_SHORT)
										.show();

							}
						});
					}
					// mConn.setDoInput(true);
					// close the streams //
					fileInputStream.close();
					sourceFile = null;
					buffer = null;
					dos.flush();
					dos.close();

				} catch (MalformedURLException ex) {

					mDialog.dismiss();
					ex.printStackTrace();

					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(KorMainProducer.this,
									"MalformedURLException", Toast.LENGTH_SHORT)
									.show();
						}
					});
				} catch (Exception e) {

					mDialog.dismiss();
					StringWriter sW = new StringWriter();
					e.printStackTrace(new PrintWriter(sW));
					Log.e("UpLoad Error", sW.toString());

				} finally {
					mDialog.dismiss();
					runOnUiThread(new Runnable() {
						public void run() {
							if (!mPostRecNum.equals("")) {
								mWebview.loadUrl("javascript:get_upimg('"
										+ mPostParmNum + "','" + mPostParmIdx
										+ "','" + mPostParmType + "','"
										+ mPostRecNum + "')");
								mPostRecNum = "";
							} else {
								mWebview.loadUrl("javascript:get_upimg('"
										+ mPostParmNum + "','" + mPostParmIdx
										+ "','" + mPostParmType + "')");
							}
						}
					});

				}
			}
		} else {
			runOnUiThread(new Runnable() {
				public void run() {
					mDialog.dismiss();
					Toast.makeText(KorMainProducer.this,
							"파일 전송을 실패 하였습니다.\n갤러리를 이용해 주세요",
							Toast.LENGTH_SHORT).show();
				}
			});
		}
		return mCurProgressTimeDisplay;
	}

	private void clearApplicationCache(java.io.File dir) {
		if (dir == null)
			dir = getCacheDir();
		else
			;
		if (dir == null)
			return;
		else
			;
		java.io.File[] children = dir.listFiles();
		try {
			for (int i = 0; i < children.length; i++)
				if (children[i].isDirectory())
					clearApplicationCache(children[i]);
				else
					children[i].delete();
		} catch (Exception e) {
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		if (mWebview != null) {
			long timeout = ViewConfiguration.getZoomControlsTimeout();
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					mWebview.clearView();
					mWebview.clearCache(true);
					mWebview.clearHistory();
					mWebview.destroy();
					mWebview = null;
				}
			}, timeout);
		}
		super.onDestroy();
		clearApplicationCache(null);

	}

	private class WebViewChromClinetClass extends WebChromeClient {

		final Activity activity = KorMainProducer.this;

		public boolean onJsAlert(WebView view, String url, String message,
				final android.webkit.JsResult result) {
			mCustomDialogAlert = new CustomDialogAlert(KorMainProducer.this,
					new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							mCustomDialogAlert.dismiss();
							result.confirm();
						}
					}, "코리아JT", message);

			mCustomDialogAlert.show();
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
		//
		// /* 4.4버전 버그로 인해 사용 불가능한 소스 */
		// public void openFileChooser(ValueCallback<Uri> uploadMsg) {
		//
		// mUploadMessage = uploadMsg;
		// Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		// i.addCategory(Intent.CATEGORY_OPENABLE);
		// i.setType("image/*");
		// KorMainProducer.this.startActivityForResult(
		// Intent.createChooser(i, "File Chooser"),
		// FILECHOOSER_RESULTCODE);
		// }
		//
		// // For Android 3.0+
		// public void openFileChooser(ValueCallback uploadMsg, String
		// acceptType) {
		// mUploadMessage = uploadMsg;
		// Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		// i.addCategory(Intent.CATEGORY_OPENABLE);
		// i.setType("*/*");
		// KorMainProducer.this.startActivityForResult(
		// Intent.createChooser(i, "File Browser"),
		// FILECHOOSER_RESULTCODE);
		// }
		//
		// // For Android 4.1
		// public void openFileChooser(ValueCallback<Uri> uploadMsg,
		// String acceptType, String capture) {
		// mUploadMessage = uploadMsg;
		// Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		// i.addCategory(Intent.CATEGORY_OPENABLE);
		// i.setType("image/*");
		// KorMainProducer.this.startActivityForResult(
		// Intent.createChooser(i, "File Chooser"),
		// KorMainProducer.FILECHOOSER_RESULTCODE);
		// }
	}

	private void EndDialog() {
		mCustomDialogEnd = new CustomDialogEnd(KorMainProducer.this,
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
		// AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
		// alt_bld.setMessage("어플리케이션을 종료 하시겠습니까?")
		// .setCancelable(false)
		// .setPositiveButton("예", new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		//
		// // CookieSyncManager cookieSyncManager =
		// // CookieSyncManager
		// // .createInstance(KorMainProducer.this);
		// // CookieManager cookieManager = CookieManager
		// // .getInstance();
		// // cookieManager.setAcceptCookie(true);
		// // cookieManager.removeSessionCookie();
		// // cookieSyncManager.sync();
		// finish();
		// }
		// })
		// .setNegativeButton("아니오",
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		// dialog.cancel();
		// }
		// });
		// AlertDialog alert = alt_bld.create();
		// alert.setTitle(getString(R.string.app_name));
		// alert.show();
	}

	public void setFlash(TextView tv) {
		int tvNum = Integer.parseInt(tv.getText().toString());
		tv.clearAnimation();
		if (tvNum > 0) {
			tv.setTextColor(Color.rgb(56, 144, 66));
			tv.setAnimation(mTextAnim);
		}
	}

	public void gongsaActivity(int i) {
		if (i == 0) {
			mGongsa.setVisibility(View.GONE);
		} else {
			mGongsa.setVisibility(View.VISIBLE);
		}
	}

	// 뒤로가기 하드웨어 버튼 눌렀을경우 처리
	@SuppressLint("NewApi")
	public void onBackPressed() {
		if (mRecLatout.getVisibility() == View.VISIBLE) {
			mRecLatout.setVisibility(View.GONE);
		} else if (mHelpLayout.getVisibility() == View.VISIBLE) {
			helpLayout(1);
		} else if (!mSlide_me.isClosed()) {
			setSlidingUI();
		} else if (mWebview.getUrl()
				.equals(getString(R.string.mainServerUrl)
						+ getString(R.string.mainUrl))
				|| mWebview.getUrl().equals(
						getString(R.string.mainServerUrl)
								+ getString(R.string.loginUrl))) {
			EndDialog();
		} else if (!mWebview.canGoBack()
				&& !mWebview.getUrl().equals(
						getString(R.string.mainServerUrl)
								+ getString(R.string.mainUrl))) {
			mWebview.loadUrl(getString(R.string.mainUrl));
		} else if (mWebview.getUrl().equals(
				getString(R.string.mainServerUrl)
						+ getString(R.string.checkMainUrl01))
				|| mWebview.getUrl().equals(
						getString(R.string.mainServerUrl)
								+ getString(R.string.checkMainUrl02))
				|| mWebview.getUrl().equals(
						getString(R.string.mainServerUrl)
								+ getString(R.string.checkMainUrl03))
				|| mWebview.getUrl().equals(
						getString(R.string.mainServerUrl)
								+ getString(R.string.checkMainUrl04))
				|| mWebview.getUrl().equals(
						getString(R.string.mainServerUrl)
								+ getString(R.string.checkMainUrl05))) {
			mWebview.loadUrl(getString(R.string.mainServerUrl)
					+ getString(R.string.mainUrl));
		} else {
			mWebview.goBack();
		}
		if (mCustomDialogMovie.isShowing()) {
			mCustomDialogMovie.dismiss();
		}
		if (mCustomDialogPhoto.isShowing()) {
			mCustomDialogPhoto.dismiss();
		}
	}

	public void appChange() {

		if (checkApp()) {
			// Intent intent =
			// this.getPackageManager().getLaunchIntentForPackage(
			// TagValuse.CHECK_PACKAGE_NAME);
			// startActivity(intent);

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.addCategory(Intent.CATEGORY_BROWSABLE);
			intent.setData(Uri.parse("koreajt://consumer?r="
					+ mPref.getValue(TagValuse.USERID, "") + "/"
					+ mPref.getValue(TagValuse.USERPW, "")));

			startActivity(intent);
		} else {
			Toast.makeText(KorMainProducer.this,
					"코리아JT어플리케이션이 없습니다\n마켓으로 이동 합니다.", Toast.LENGTH_SHORT)
					.show();
			Intent marketLaunch = new Intent(Intent.ACTION_VIEW);
			marketLaunch.setData(Uri
					.parse("market://details?id=com.koreajt.consumer"));
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
			return true;
			// 설치된 패키지의 APP_ID 체크
			// if (appId.equals(TagValuse.CHECK_APP_NAME)) {
			//
			// }else{
			// return false;
			// }
		}

		catch (NameNotFoundException e) {
			return false;
		}
	}

	// 메인화면 소스 끝

	// 녹음부분 소스 시작

	public void callRecLayout() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mRecLatout.setVisibility(View.VISIBLE);
					}
				});
			}
		}).start();
	}

	public void setRecFile() {

		File file = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DCIM).toString()
				+ "/NaraJTRec");
		if (!file.exists()) {
			file.mkdir();
		}
		mFilePath = file.getPath();
		SimpleDateFormat timeStampFormat = new SimpleDateFormat(
				"yyyyMMddHHmmss");

		// 파일명 위에서 정한 파일명을 WJ 폴더에 저장
		mFileName = "/tmp_" + getDate() + "Rec.mp4";
	}

	private String getDate() {
		int curYear, curMonth, curDay, curHour, curMinute, curNoon, curSecond;

		String dateStr;

		Calendar calendar = null;

		calendar = Calendar.getInstance();

		curYear = calendar.get(Calendar.YEAR);
		curMonth = calendar.get(Calendar.MONTH) + 1;
		curDay = calendar.get(Calendar.DAY_OF_MONTH);
		curHour = calendar.get(Calendar.HOUR_OF_DAY);
		curMinute = calendar.get(Calendar.MINUTE);
		curSecond = calendar.get(Calendar.SECOND);

		dateStr = Integer.toString(curYear) + Integer.toString(curMonth)
				+ Integer.toString(curDay) + Integer.toString(curHour)
				+ Integer.toString(curMinute) + Integer.toString(curSecond);

		return dateStr;
	}

	private void mBtnStartRecOnClick() {
		if (mRecState == TagValuse.REC_STOP) {
			mRecState = TagValuse.RECORDING;
			startRec();
			updateUI();
		} else if (mRecState == TagValuse.RECORDING) {
			mRecState = TagValuse.REC_STOP;
			stopRec();
			updateUI();
		}
	}

	// 녹음시작
	private void startRec() {
		mCurRecTimeMs = 0;
		mCurProgressTimeDisplay = 0;

		setRecFile();

		// SeekBar의 상태를 0.1초후 체크 시작
		mProgressHandler.sendEmptyMessageDelayed(0, 100);

		if (mRecorder == null) {
			mRecorder = new MediaRecorder();
			mRecorder.reset();
		} else {
			mRecorder.reset();
		}

		try {
			// 오디오 파일 생성
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
			mRecorder.setOutputFile(mFilePath + mFileName);
			mRecorder.prepare();
			mRecorder.start();
		} catch (IllegalStateException e) {
			Log.e("IllegalStateException", e.getMessage());
		} catch (IOException e) {
			Log.e("IOException", e.getMessage());
		}
	}

	// 녹음정지
	private void stopRec() {
		try {
			mRecorder.stop();
		} catch (Exception e) {

		} finally {
			mRecorder.release();
			mRecorder = null;
		}

		mCurRecTimeMs = -999;
		// SeekBar의 상태를 즉시 체크
		mProgressHandler.sendEmptyMessageDelayed(0, 0);
	}

	private void mBtnStartPlayOnClick() {
		if (mPlayerState == TagValuse.PLAY_STOP) {
			mPlayerState = TagValuse.PLAYING;
			initMediaPlayer();
			startPlay();
			updateUI();
		} else if (mPlayerState == TagValuse.PLAYING) {
			mPlayerState = TagValuse.PLAY_STOP;
			pausePlay();
			updateUI();
		}
	}

	private void mBtnStopPlayOnClick() {
		if (mPlayerState == TagValuse.PLAYING) {
			mPlayerState = TagValuse.PLAY_STOP;
			stopPlay();
			releaseMediaPlayer();
			updateUI();
		}
	}

	private void initMediaPlayer() {
		// 미디어 플레이어 생성
		if (mPlayer == null)
			mPlayer = new MediaPlayer();
		else
			mPlayer.reset();

		mPlayer.setOnCompletionListener(new Completionlisnear());
		String fullFilePath = mFilePath + mFileName;

		try {
			mPlayer.setDataSource(fullFilePath);
			mPlayer.prepare();
			int point = mPlayer.getDuration();
			mRecProgressBar.setMax(point);

			int maxMinPoint = point / 1000 / 60;
			int maxSecPoint = (point / 1000) % 60;
			String maxMinPointStr = "";
			String maxSecPointStr = "";

			mTvRecMaxPoint.setText(TimeInsert(maxMinPoint, maxSecPoint));

			mRecProgressBar.setProgress(0);
		} catch (Exception e) {
			Log.v("ProgressRecorder", "미디어 플레이어 Prepare Error ==========> " + e);
		}
	}

	public String TimeInsert(int maxMinPoint, int maxSecPoint) {
		String str = null;
		String mPoint, sPoint = null;
		if (maxMinPoint < 10)
			mPoint = "0" + maxMinPoint + ":";
		else
			mPoint = maxMinPoint + ":";

		if (maxSecPoint < 10)
			sPoint = "0" + maxSecPoint;
		else
			sPoint = String.valueOf(maxSecPoint);

		return mPoint + sPoint;
	}

	// 재생 시작
	private void startPlay() {
		Log.v("ProgressRecorder", "startPlay().....");

		try {
			mPlayer.start();

			// SeekBar의 상태를 0.1초마다 체크
			mProgressHandler2.sendEmptyMessageDelayed(0, 100);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("startPlay", e.getMessage());
		}
	}

	private void pausePlay() {
		Log.v("ProgressRecorder", "pausePlay().....");

		// 재생을 일시 정지하고
		mPlayer.pause();

		// 재생이 일시정지되면 즉시 SeekBar 메세지 핸들러를 호출한다.
		mProgressHandler2.sendEmptyMessageDelayed(0, 0);
	}

	private void stopPlay() {
		Log.v("ProgressRecorder", "stopPlay().....");

		// 재생을 중지하고
		mPlayer.stop();

		// 즉시 SeekBar 메세지 핸들러를 호출한다.
		mProgressHandler2.sendEmptyMessageDelayed(0, 0);
	}

	private void releaseMediaPlayer() {
		Log.v("ProgressRecorder", "releaseMediaPlayer().....");
		mPlayer.release();
		mPlayer = null;
		mRecProgressBar.setProgress(0);
	}

	// 재생버튼의 OnCompletionListener (반복되는 부분)
	class Completionlisnear implements OnCompletionListener {

		@Override
		public void onCompletion(MediaPlayer mp) {
			mPlayerState = TagValuse.PLAY_STOP; // 재생이 종료됨
			// 재생이 종료되면 즉시 SeekBar 메세지 핸들러를 호출한다.
			mProgressHandler2.sendEmptyMessageDelayed(0, 0);

			updateUI();
		}

	}

	// 버튼의 OnClick 이벤트 리스너
	class ClickListenerRec implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btnStartRec:
				mBtnStartRecOnClick();
				break;
			case R.id.btnStartPlay:
				mBtnStartPlayOnClick();
				break;
			case R.id.btnSend:
				File file = new File(mFilePath + mFileName);
				if (file.exists()) {
					FileUpload("녹음파일 업로드중...", mFilePath + mFileName);
					mRecLatout.setVisibility(View.GONE);

				} else {
					Toast.makeText(KorMainProducer.this, "녹음된 파일이 없습니다",
							Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
			}
		}
	}

	private void updateUI() {
		if (mRecState == TagValuse.REC_STOP) {
			mTvBtnRec.setText("녹음");
			mImgRec.setBackgroundResource(R.drawable.rec_icon);
			mRecProgressBar.setProgress(0);
		} else if (mRecState == TagValuse.RECORDING) {
			mTvBtnRec.setText("녹음정지");
			mImgRec.setBackgroundResource(R.drawable.stop_icon);
		}
		if (mPlayerState == TagValuse.PLAY_STOP) {
			mTvBtnPlay.setText("재생");
			mImgPlay.setBackgroundResource(R.drawable.play_icon);
			mPlayCount = 0;
			mRecProgressBar.setProgress(0);
		} else if (mPlayerState == TagValuse.PLAYING) {
			mTvBtnPlay.setText("정지");
			mImgPlay.setBackgroundResource(R.drawable.stop_icon);
		}
	}

	// 녹음부분 소스 끝

	// 내부클래스
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
				String tagName = "", name = "", level = "", total = "", mNew = "", exchange = "", cancle = "", send = "", new_q = "", bar = "";
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
						if (isItemTag && tagName.equals("new")) {
							mNew += xpp.getText();
						}
						if (isItemTag && tagName.equals("send")) {
							send += xpp.getText();
						}
						if (isItemTag && tagName.equals("exchange")) {
							exchange += xpp.getText();
						}
						if (isItemTag && tagName.equals("cancle")) {
							cancle += xpp.getText();
						}
						if (isItemTag && tagName.equals("new_q")) {
							new_q += xpp.getText();
						}
						if (isItemTag && tagName.equals("total")) {
							total += xpp.getText();
						}
						if (isItemTag && tagName.equals("bar")) {
							bar += xpp.getText();
						}

					} else if (eventType == XmlPullParser.END_TAG) {
						// 태그명을 저장
						tagName = xpp.getName();

						if (tagName.equals("data")) {

							mMenuText.add(name);
							mMenuText.add(level);
							mMenuText.add(mNew);
							mMenuText.add(send);
							mMenuText.add(exchange);
							mMenuText.add(cancle);
							mMenuText.add(new_q);
							mMenuText.add(total);
							mMenuText.add(bar);

							name = "";
							level = "";
							mNew = "";
							exchange = "";
							send = "";
							cancle = "";
							new_q = "";
							total = "";
							bar = "";

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
			mMenuLoding.setVisibility(View.GONE);
		}
	}

	private class AndroidBridge {
		// 갤러리 호출
		public void callPhoto(String fname, String idx, String type, String num) {
			// TODO Auto-generated method stub
			mCustomDialogPhoto = new CustomDialogPhoto(KorMainProducer.this,
					galClickListener, camClickListener);
			mPostParmFname = fname;
			mPostParmIdx = idx;
			mPostParmType = type;
			mPostParmNum = num;
			mCustomDialogPhoto.show();
		}

		public void callMovie(String fname, String idx, String type, String num) {
			mCustomDialogMovie = new CustomDialogMovie(KorMainProducer.this,
					galClickListener, camClickListener);
			mPostParmFname = fname;
			mPostParmIdx = idx;
			mPostParmType = type;
			mPostParmNum = num;
			mCustomDialogMovie.show();

		}

		public void callRec(String fname, String idx, String type, String num,
				String recNum) {
			mPostParmFname = fname;
			mPostParmIdx = idx;
			mPostParmType = type;
			mPostParmNum = num;
			mPostRecNum = recNum;
			callRecLayout();
		}

		// 사용자 계정 저장
		public void getUserInfo(String id, String pw) {
			mPref.put(TagValuse.USERID, id);
			mPref.put(TagValuse.USERPW, pw);
			mLoginFT = true;
		}

		public void getGcmRegId(String str) {
			GcmStart();
		}

		public void logout(String str) {
			mPref.delValue(TagValuse.USERID);
			mPref.delValue(TagValuse.USERPW);
			mLoginFT = false;
			Log.e("logout", str);
			mWebview.loadUrl(getString(R.string.mainServerUrl)
					+ getString(R.string.loginUrl));
		}

		public void callAddr(String str) {
			String addr = "";
			mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			mCriteria = new Criteria();
			mCriteria.setAccuracy(Criteria.NO_REQUIREMENT);
			mCriteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
			mProvider = mLocationManager.getBestProvider(mCriteria, true);
			mLocation = mLocationManager.getLastKnownLocation(mProvider);

			mLat = mLocation.getLatitude();
			mLng = mLocation.getLongitude();

			// Log.e("위치정보", Double.toString(mLat) + " / " +
			// Double.toString(mLng));
			//
			// Log.e("주소", findAddress(mLat, mLng));

			addr = findAddress(mLat, mLng);
			mWebview.loadUrl("javascript:get_addr('" + addr + "')");

		}

		public void callHp(String str) {
			TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String phoneNum = telManager.getLine1Number();
			Log.e("전화번호", phoneNum);
			mWebview.loadUrl("javascript:get_hp('" + phoneNum + "')");
		}

		public void callModel(String str) {
			String model = Build.MODEL;
			// Log.e("모델명", model);
			mWebview.loadUrl("javascript:get_model('" + model + "')");
		}

		public void callEmail(String str) {
			AccountManager mgr = AccountManager.get(KorMainProducer.this);
			Account[] accts = mgr.getAccounts();
			final int count = accts.length;
			Account acct = null;

			for (int i = 0; i < count; i++) {
				acct = accts[i];
				if (acct.type.equals("com.google")) {
					Log.i("ANDROES", "Account - name=" + acct.name);
					mWebview.loadUrl("javascript:get_email('" + acct.name
							+ "')");
				}
			}
		}

		public void gongsa(String str) {
			gongsaActivity(1);
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
					setSlidingUI();
				} else {
					Toast.makeText(KorMainProducer.this,
							"로그인을 하셔야 개인메뉴를 보실수 있습니다.", Toast.LENGTH_SHORT)
							.show();
				}
			} else if (btnId == R.id.helpBtn) {
				if (mHelpBtnOnOver) {
					helpLayout(0);
				} else {
					helpLayout(1);
				}
			} else if (btnId == R.id.gongsaCancle) {
				gongsaActivity(0);
			} else if (btnId == R.id.buyChange) {
				appChange();
				setSlidingUI();
			} else if (btnId == R.id.logout) {
				mWebview.loadUrl(getString(R.string.mainServerUrl)
						+ getString(R.string.logoutUrl));
				setSlidingUI();
			} else if (btnId == R.id.BtnHome) {
				if (mWebview.getUrl().indexOf(getString(R.string.loginUrl)) > -1) {
					mWebview.loadUrl(getString(R.string.mainServerUrl)
							+ getString(R.string.loginUrl));
				} else {
					mWebview.loadUrl(getString(R.string.mainServerUrl)
							+ getString(R.string.mainUrl));
				}
				// Intent testActivity = new Intent(KorMainProducer.this,
				// TestInputActivity.class);
				// startActivity(testActivity);
			} else if (btnId == R.id.btnSubMenu01) { // 신규주문
				mWebview.loadUrl(getString(R.string.mainServerUrl)
						+ getString(R.string.menuUrl01));
				// gongsaActivity(1);
				setSlidingUI();
			} else if (btnId == R.id.btnSubMenu02) { // 발송요청
				mWebview.loadUrl(getString(R.string.mainServerUrl)
						+ getString(R.string.menuUrl02));
				// gongsaActivity(1);
				setSlidingUI();
			} else if (btnId == R.id.btnSubMenu03) { // 반품/교환/환불요청
				mWebview.loadUrl(getString(R.string.mainServerUrl)
						+ getString(R.string.menuUrl03));
				// gongsaActivity(1);
				setSlidingUI();
			} else if (btnId == R.id.btnSubMenu04) { // 취소요청
				mWebview.loadUrl(getString(R.string.mainServerUrl)
						+ getString(R.string.menuUrl04));
				// gongsaActivity(1);
				setSlidingUI();
			} else if (btnId == R.id.btnSubMenu05) { // 신규상품문의
				mWebview.loadUrl(getString(R.string.mainServerUrl)
						+ getString(R.string.menuUrl05));
				// gongsaActivity(1);
				setSlidingUI();
			} else if (btnId == R.id.btnSubMenu06) { // 정산알림
				// Toast.makeText(KorMainProducer.this, "준비중 입니다",
				// Toast.LENGTH_SHORT).show();
				Intent testActivity = new Intent(KorMainProducer.this, GalleryView.class);
						startActivity(testActivity);
				gongsaActivity(1);
				setSlidingUI();
			} else if (btnId == R.id.btnSubMenu07) { // 흥정내역
				mWebview.loadUrl(getString(R.string.mainServerUrl)
						+ getString(R.string.menuUrl07));
				// gongsaActivity(1);
				setSlidingUI();
			} else if (btnId == R.id.btnNotice) {
				mWebview.loadUrl(getString(R.string.mainServerUrl)

				+ getString(R.string.menuUrlNotice));
				// gongsaActivity(1);
				setSlidingUI();
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

	// 웹뷰 설정하는 클레스
	private class WebViewClientClass extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			// Log.e("url", url);

			if ((url.indexOf("product_list") > -1)
					|| (url.indexOf("product_nform") > -1)
					|| (url.equals(getString(R.string.mainServerUrl)
							+ getString(R.string.mainUrl)))) {
				findViewById(R.id.help_btnLayout).setVisibility(View.VISIBLE);
			} else {
				findViewById(R.id.help_btnLayout).setVisibility(View.GONE);
			}
			if (url.equals(getString(R.string.mainServerUrl)
					+ getString(R.string.mainUrl))) {
				if (mPref.getValue(getString(R.string.mainUrl), true)) {
					mHelpLayout.setVisibility(View.VISIBLE);
					helpImageSet(R.drawable.help_main, mDeviceWidth, 1.4);
					mBtnHelp.setBackgroundResource(R.drawable.btn_help_over);
				}
			} else if (url.indexOf(getString(R.string.mainServerUrl)
					+ getString(R.string.mainMenuUrl01)) > -1) {
				if (mPref.getValue(getString(R.string.mainMenuUrl01), true)) {
					mHelpLayout.setVisibility(View.VISIBLE);
					helpImageSet(R.drawable.help_sub01, mDeviceWidth, 2.4);
					mHelpBtnOnOver = false;
					mBtnHelp.setBackgroundResource(R.drawable.btn_help_over);
				}
			} else if (url.equals(getString(R.string.mainServerUrl)
					+ getString(R.string.mainMenuUrl01_01_01))) {
				if (mPref.getValue(getString(R.string.mainMenuUrl01_01_02),
						true)) {
					mHelpLayout.setVisibility(View.VISIBLE);
					helpImageSet(R.drawable.help_input01, mDeviceWidth, 1.8);
					mHelpBtnOnOver = false;
					mBtnHelp.setBackgroundResource(R.drawable.btn_help_over);
				}
			} else if (url.indexOf(getString(R.string.mainMenuUrl01_01_02)) > -1) {
				if (mPref.getValue(getString(R.string.mainMenuUrl01_01_02),
						true)) {
					mHelpLayout.setVisibility(View.VISIBLE);
					helpImageSet(R.drawable.help_input01, mDeviceWidth, 1.8);
					mHelpBtnOnOver = false;
					mBtnHelp.setBackgroundResource(R.drawable.btn_help_over);
				}
			} else if (url.indexOf(getString(R.string.mainMenuUrl01_02)) > -1) {
				if (mPref.getValue(getString(R.string.mainMenuUrl01_02), true)) {
					mHelpLayout.setVisibility(View.VISIBLE);
					helpImageSet(R.drawable.help_input02, mDeviceWidth, 3.3);
					mHelpBtnOnOver = false;
					mBtnHelp.setBackgroundResource(R.drawable.btn_help_over);
				}
			} else if (url.indexOf(getString(R.string.mainMenuUrl01_03)) > -1) {
				if (mPref.getValue(getString(R.string.mainMenuUrl01_03), true)) {
					mHelpLayout.setVisibility(View.VISIBLE);
					helpImageSet(R.drawable.help_input03, mDeviceWidth, 1.8);
					mHelpBtnOnOver = false;
					mBtnHelp.setBackgroundResource(R.drawable.btn_help_over);
				}
			} else if (url.indexOf(getString(R.string.mainMenuUrl01_04)) > -1) {
				if (mPref.getValue(getString(R.string.mainMenuUrl01_04), true)) {
					mHelpLayout.setVisibility(View.VISIBLE);
					helpImageSet(R.drawable.help_input04, mDeviceWidth, 1.4);
					mHelpBtnOnOver = false;
					mBtnHelp.setBackgroundResource(R.drawable.btn_help_over);
				}
			}

			// if (url.equals(getString(R.string.mainServerUrl)
			// + getString(R.string.mainUrl))) {
			// if (mFirstStart) {
			// mFirstStartLayout.setVisibility(View.VISIBLE);
			// setHelpFlash("on");
			// }
			// }
			mWebview.clearCache(true);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			return super.shouldOverrideUrlLoading(view, url);
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
			if (url.equals(getString(R.string.mainServerUrl)
					+ getString(R.string.mainUrl))) {
				mGetCookie = CookieManager.getInstance().getCookie(url);
			}

			if (url.indexOf(getString(R.string.joinGcmSend)) > -1) {
				GcmStart();
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
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private static void recycleBitmap(ImageView iv) {

		Drawable d = iv.getDrawable();
		if (d != null) {
			if (d instanceof BitmapDrawable) {
				Bitmap b = ((BitmapDrawable) d).getBitmap();
				b.recycle();
			} // 현재로서는 BitmapDrawable 이외의 drawable 들에 대한 직접적인 메모리 해제는 불가능하다.

			d.setCallback(null);
		}
	}
}
