package com.koreajt.producer;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.koreajt.producer.util.LRUCache;
import com.koreajt.producer.util.TagValuse;
import com.koreajt.producer.util.ThumbImageInfo;

public class GalleryView extends Activity implements ListView.OnScrollListener,
		GridView.OnItemClickListener {
	boolean mScoll = false;
	ProgressDialog mLodingProg;
	Button mCancle;
	GridView mGalleryGv;
	ImageAdapter mListAdapter;
	ArrayList<ThumbImageInfo> mThumbImageInfoList;
	Cursor mImageCursor = null, mImageInfoCursor = null;
	DoFindImageList mDoFindImageList = new DoFindImageList();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_list_view);

		mThumbImageInfoList = new ArrayList<ThumbImageInfo>();
		mGalleryGv = (GridView) findViewById(R.id.gvImageList);
		mGalleryGv.setOnScrollListener(this);
		mGalleryGv.setOnItemClickListener(this);
		mCancle = (Button) findViewById(R.id.btnGalleryCancel);
		mCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		mDoFindImageList.execute();
	}

	private long findThumbList() {
		long returnValue = 0;

		String[] projection = { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DATA };
		mImageCursor = getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
				null, MediaStore.Images.Media.DATE_ADDED + " desc ");

		if (mImageCursor != null && mImageCursor.getCount() > 0) {
			int imageIDCol = mImageCursor
					.getColumnIndex(MediaStore.Images.Media._ID);
			int imageDataCol = mImageCursor
					.getColumnIndex(MediaStore.Images.Media.DATA);

			while (mImageCursor.moveToNext()) {
				ThumbImageInfo thumbInfo = new ThumbImageInfo();

				thumbInfo.setId(mImageCursor.getString(imageIDCol));
				thumbInfo.setData(mImageCursor.getString(imageDataCol));

				mThumbImageInfoList.add(thumbInfo);
				returnValue++;
			}
		}
		mImageCursor.close();
		return returnValue;
	}

	private void updateUI() {
		mListAdapter = new ImageAdapter(this, R.layout.image_cell,
				mThumbImageInfoList);
		mGalleryGv.setAdapter(mListAdapter);

	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_IDLE:
			mScoll = false;
			mListAdapter.notifyDataSetChanged();
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			mScoll = true;
			break;
		case OnScrollListener.SCROLL_STATE_FLING:
			mScoll = true;
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		ImageAdapter adapter = (ImageAdapter) arg0.getAdapter();
		ThumbImageInfo rowData = (ThumbImageInfo) adapter.getItem(position);
		String filePath = getImageInfo(mThumbImageInfoList.get(position)
				.getId());
		Intent intent = new Intent();
		intent.putExtra("key", filePath);
		setResult(TagValuse.SELECT_GALLERY, intent);
		mDoFindImageList.cancel(false);
		finish();
	}

	static class ImageViewHolder {
		ImageView ivImage;
		CheckBox chkImage;
	}

	private class ImageAdapter extends BaseAdapter {
		static final int VISIBLE = 0x00000000;
		static final int INVISIBLE = 0x00000004;
		private Context mContext;
		private int mCellLayout;
		private LayoutInflater mLiInflater;
		private ArrayList<ThumbImageInfo> mThumbImageInfoList;
		@SuppressWarnings("unchecked")
		private LRUCache mCache;

		public ImageAdapter(Context c, int cellLayout,
				ArrayList<ThumbImageInfo> thumbImageInfoList) {
			mContext = c;
			mCellLayout = cellLayout;
			mThumbImageInfoList = thumbImageInfoList;

			mLiInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			mCache = new LRUCache<String, Bitmap>(30);
		}

		public int getCount() {
			return mThumbImageInfoList.size();
		}

		public Object getItem(int position) {
			return mThumbImageInfoList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		@SuppressWarnings("unchecked")
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			if (convertView == null) {
				convertView = mLiInflater.inflate(mCellLayout, parent, false);
				ImageViewHolder holder = new ImageViewHolder();

				holder.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);

				convertView.setTag(holder);
			}

			final ImageViewHolder holder = (ImageViewHolder) convertView
					.getTag();

			if (!mScoll) {
				try {
					String path = ((ThumbImageInfo) mThumbImageInfoList
							.get(position)).getData();
					Bitmap bmp = (Bitmap) mCache.get(path);

					if (bmp != null) {
						holder.ivImage.setImageBitmap(bmp);
					} else {
						BitmapFactory.Options option = new BitmapFactory.Options();

						if (new File(path).length() > 100000)
							option.inSampleSize = 10;
						else
							option.inSampleSize = 2;

						bmp = BitmapFactory.decodeFile(path, option);
						holder.ivImage.setImageBitmap(bmp);

						mCache.put(path, bmp); // 캐쉬에 넣어준다.
					}

					holder.ivImage.setVisibility(VISIBLE);
					setProgressBarIndeterminateVisibility(false);
				} catch (Exception e) {
					e.printStackTrace();
					setProgressBarIndeterminateVisibility(false);
				}
			} else {
				setProgressBarIndeterminateVisibility(true);
				holder.ivImage.setVisibility(INVISIBLE);
			}

			return convertView;
		}
	}

	private class DoFindImageList extends AsyncTask<String, Integer, Long> {
		@Override
		protected void onPreExecute() {
			mLodingProg = ProgressDialog.show(GalleryView.this, null, "로딩중...",
					true, true);
			super.onPreExecute();
		}

		@Override
		protected Long doInBackground(String... arg0) {
			long returnValue = 0;
			returnValue = findThumbList();
			return returnValue;
		}

		@Override
		protected void onPostExecute(Long result) {
			updateUI();
			mLodingProg.dismiss();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}
	}

	private String getImageInfo(String thumbID) {
		String imageDataPath = null;
		String[] proj = { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DATA,
				MediaStore.Images.Media.DISPLAY_NAME,
				MediaStore.Images.Media.SIZE };
		mImageInfoCursor = getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj,
				"_ID='" + thumbID + "'", null, null);

		if (mImageInfoCursor != null && mImageInfoCursor.moveToFirst()) {
			if (mImageInfoCursor.getCount() > 0) {
				int imgData = mImageInfoCursor
						.getColumnIndex(MediaStore.Images.Media.DATA);
				imageDataPath = mImageInfoCursor.getString(imgData);
			}
		}
		mImageInfoCursor.close();
		return imageDataPath;
	}
}