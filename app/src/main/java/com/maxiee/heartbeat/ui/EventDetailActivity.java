package com.maxiee.heartbeat.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.FileUtils;
import com.maxiee.heartbeat.common.TimeUtils;
import com.maxiee.heartbeat.common.tagview.Tag;
import com.maxiee.heartbeat.common.tagview.TagView;
import com.maxiee.heartbeat.database.utils.EventUtils;
import com.maxiee.heartbeat.database.utils.ImageUtils;
import com.maxiee.heartbeat.database.utils.LabelUtils;
import com.maxiee.heartbeat.database.utils.ThoughtUtils;
import com.maxiee.heartbeat.model.Event;
import com.maxiee.heartbeat.model.Image;
import com.maxiee.heartbeat.model.Label;
import com.maxiee.heartbeat.model.Thoughts;
import com.maxiee.heartbeat.ui.adapter.ThoughtTimeaxisAdapter;
import com.maxiee.heartbeat.ui.common.BaseActivity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by maxiee on 15-6-13.
 */
public class EventDetailActivity extends BaseActivity {
    private final static String TAG = EventDetailActivity.class.getSimpleName();

    public static final String EXTRA_NAME = "id";
    private static final int ADD_IMAGE = 1127;
    private static final int LONG_IMAGE_IMAGE_MAX_HEIGHT = 300;

    private long mId;
    private Event mEvent;

    @Bind(R.id.tv_event)        TextView mTvEvent;
    @Bind(R.id.recyclerview)    RecyclerView mRecyclerView;
    @Bind(R.id.tagview)         TagView mTagView;
    @Bind(R.id.tv_time)         TextView mTvTime;
    @Bind(R.id.backdrop)        ImageView mImageBackDrop;
    @Bind(R.id.card_event)      View mCardEvent;
    @Bind(R.id.add_imgae)       TextView mAddImageText;
    @Bind(R.id.header)          View mHeaderView;

    private LinearLayoutManager mLayoutManager;
    private ThoughtTimeaxisAdapter mAdapter;

    private Image mImage;
    private Thoughts mThoughts;
    private String mSortingType;
    private boolean mHasImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mId = intent.getLongExtra(EXTRA_NAME, -1);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mSortingType = sp.getString("time_axis_sorting", "0");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("");
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mEvent = EventUtils.getEvent(this, mId);

        mTagView.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag, int position) {
                Intent i = new Intent();
                i.setAction(Intent.ACTION_MAIN);
                i.setClass(EventDetailActivity.this, LabelDetailActivity.class);
                i.putExtra("tag_text", tag.text);
                startActivity(i);
            }
        });

        mTvTime.setText(TimeUtils.parseTime(this, mEvent.getTimestamp()));

        mCardEvent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent i = new Intent(EventDetailActivity.this, AddEventActivity.class);
                i.putExtra(AddEventActivity.ID_EVENT_MODIFY, mId);
                startActivity(i);
                return true;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EventDetailActivity.this, AddEditThoughtActivity.class);
                i.putExtra(AddEditThoughtActivity.MODE, AddEditThoughtActivity.MODE_NEW);
                i.putExtra(AddEditThoughtActivity.EVENT_KEY, mId);
                startActivity(i);
            }
        });

        mThoughts = ThoughtUtils.getThoughtsByEventId(this, mId);
        mAdapter = new ThoughtTimeaxisAdapter(mThoughts);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEvent = EventUtils.getEvent(this, mId);
        mTvEvent.setText(mEvent.getEvent());
        mThoughts = ThoughtUtils.getThoughtsByEventId(this, mEvent.getId());
        mAdapter.setData(mThoughts);
        mAdapter.notifyDataSetChanged();
        initImage();
        updateTagView();
    }

    private void initImage() {
        mImage = ImageUtils.getImageByEventId(this, mEvent.getId());
        if (mImage == null) {
            mAddImageText.setVisibility(View.VISIBLE);
            mAddImageText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT < 19) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, getString(R.string.add_image)), ADD_IMAGE);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        startActivityForResult(Intent.createChooser(intent, getString(R.string.add_image)), ADD_IMAGE);
                    }
                }
            });
        } else {
            changeHeaderToImage();
            mAddImageText.setVisibility(View.INVISIBLE);
            Glide.with(this)
                    .load(mImage.getPath())
                    .into(mImageBackDrop);
            mImageBackDrop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(EventDetailActivity.this, GalleryActivity.class);
                    i.putExtra(GalleryActivity.EVENT_ID, mEvent.getId());
                    startActivity(i);
                }
            });
        }
    }

    private void changeHeaderToImage() {
        mHeaderView.setVisibility(View.GONE);
        mImageBackDrop.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.onBackPressed();
            return true;
        }
        if (id == R.id.long_iamge) {
//            generateLongImage();
            new LongImageTask().execute();
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateTagView() {
        mTagView.clear();
        ArrayList<Label> labels = LabelUtils.getLabelsByEvent(this, mEvent);
        for (Label l : labels) mTagView.addTag(new Tag(l.getLabel()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri mImageUri = data.getData();
            if (Build.VERSION.SDK_INT >= 19) {
                final int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                //noinspection ResourceType
                getContentResolver().takePersistableUriPermission(mImageUri, takeFlags);
            }
            // convert uri to path
            String path = FileUtils.uriToPath(this, mImageUri);
            ImageUtils.addImage(EventDetailActivity.this, mEvent.getId(), path);
            initImage();
        }
    }

    private class LongImageTask extends AsyncTask<Void, Integer, Void> {
        private ProgressDialog progressDialog;
        private Canvas mBitmapHolder;
        private int mWidth;
        private Bitmap mBitmap;
        private View mView;
        private TextView mTv;
        private ImageView mIv;
        private TextView mTvOrder;
        private TextView mTvTime;
        private View mPoint;
        private LinearLayout mLL;
        private int mYPos = 0;
        private int mHeight = 0;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(EventDetailActivity.this);
            progressDialog.setMessage(getString(R.string.generating));
            progressDialog.setCancelable(false);
            progressDialog.show();

//            mCardEvent.setBackgroundColor(ContextCompat.getColor(EventDetailActivity.this, android.support.v7.cardview.R.color.cardview_light_background));

            // create a parent layout of time-axis item
            FrameLayout mFl = new FrameLayout(EventDetailActivity.this);
            mFl.setBackgroundColor(ContextCompat.getColor(EventDetailActivity.this, R.color.window_background));

            // get display width
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            mWidth = displaymetrics.widthPixels;

            // create time-axis item view from layout
            mView = getLayoutInflater().inflate(R.layout.item_thought_timeaxis, mFl);
            mTv = (TextView) mView.findViewById(R.id.tv_thought);
            mIv = (ImageView) mView.findViewById(R.id.image_thought);
            mTvOrder = (TextView) mView.findViewById(R.id.tv_order);
            mTvTime = (TextView) mView.findViewById(R.id.tv_time);
            mPoint = mView.findViewById(R.id.time_point);

            setPointColor();

            createBottomLogoLayout();
        }

        private void setPointColor() {
            final TypedValue typedValue = new TypedValue();
            getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
            int color = typedValue.data;
            Drawable d = ContextCompat.getDrawable(EventDetailActivity.this, R.drawable.circle_timeaxis);
            d.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            mPoint.setBackgroundDrawable(d);
        }

        private void createBottomLogoLayout() {
            mLL = new LinearLayout(EventDetailActivity.this);
            mLL.setBackgroundColor(ContextCompat.getColor(EventDetailActivity.this, R.color.window_background));
            ViewGroup.LayoutParams LLParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
            mLL.setLayoutParams(LLParams);
            mLL.setOrientation(LinearLayout.HORIZONTAL);
            mLL.setPadding(24, 0, 0, 0);
            mLL.setGravity(Gravity.CENTER_VERTICAL);
            ImageView icon = new ImageView(EventDetailActivity.this);
            icon.setScaleType(ImageView.ScaleType.CENTER_CROP);
            icon.setImageDrawable(ContextCompat.getDrawable(EventDetailActivity.this, R.mipmap.ic_launcher));
            mLL.addView(icon);
            TextView hb = new TextView(EventDetailActivity.this);
            hb.setText("@心动小分队");
            mLL.addView(hb);
            mLL.measure(View.MeasureSpec.makeMeasureSpec(mWidth, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            mHeight += mLL.getMeasuredHeight();
        }

        @Override
        protected Void doInBackground(Void... params) {
            mHeight += measureViewsHeight(mWidth);

            Log.d("maxiee", "生成Bitmap:" + String.valueOf(mWidth) + "," + String.valueOf(mHeight));
            mBitmap = Bitmap.createBitmap(
                    mWidth,
                    mHeight,
                    Bitmap.Config.RGB_565);
            mBitmapHolder = new Canvas(mBitmap);

            if (mImage != null) publishProgress(-2);//backdrop
            publishProgress(-1);//event card
            for (int i=0; i<mThoughts.length(); i++) {
                publishProgress(i);
            }
            publishProgress(-3);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progress = values[0];
            if (progress == -2) {
                // draw backdrop
                if (mImage != null) {
                    int backDropHeight = mImageBackDrop.getMeasuredHeight();
                    mImageBackDrop.layout(0, 0, mWidth, backDropHeight);
                    mImageBackDrop.buildDrawingCache();
                    Bitmap backDropBitmap = mImageBackDrop.getDrawingCache();
                    if (backDropBitmap != null) {
                        mBitmapHolder.drawBitmap(backDropBitmap, 0, 0, null);
                    }
                    mYPos += backDropHeight;
                }
            }
            else if (progress == -1) {
                // draw cardview
                int cardEventHeight = mCardEvent.getMeasuredHeight();
                mCardEvent.layout(0, 0, mWidth, cardEventHeight);
                mCardEvent.buildDrawingCache();
                Bitmap eventBitmap = mCardEvent.getDrawingCache();
                if (eventBitmap != null) {
                    mBitmapHolder.drawBitmap(eventBitmap, 0, mYPos, null);
                }
                mYPos += cardEventHeight;
            } else if (progress == -3) {
                mLL.layout(0, 0, mWidth, mLL.getMeasuredHeight());
                Log.d("maxiee", "llMeasureH:" + String.valueOf(mLL.getMeasuredHeight()));
                mLL.buildDrawingCache();
                Bitmap b = mLL.getDrawingCache();
                if (b != null) {
                    Log.d("maxiee", "Logo不为空");
                    mBitmapHolder.drawBitmap(b, 0, mYPos, null);
                }
            } else if (progress >= 0) {
                // draw item
                Bitmap itemBitmap;
                int childHeight;

                mTv.setText(mThoughts.get(progress).getThought());
                mTvOrder.setText(getOrder(progress, mThoughts.length()));
                long time = mThoughts.get(progress).getTimeStamp();
                mTvTime.setText(TimeUtils.parseTime(EventDetailActivity.this, time));
                if (mThoughts.get(progress).hasImage()) {
                    mIv.setVisibility(View.VISIBLE);
                    Bitmap bmp = loadBitmap(mTv.getMeasuredWidth(), LONG_IMAGE_IMAGE_MAX_HEIGHT, mThoughts.get(progress).getPath());
                    mIv.setImageBitmap(bmp);
                } else {
                    mIv.setVisibility(View.GONE);
                }
                mView.measure(View.MeasureSpec.makeMeasureSpec(mWidth, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                childHeight = mView.getMeasuredHeight();
                mView.layout(0, 0, mWidth, childHeight);
                mView.buildDrawingCache();
                itemBitmap = mView.getDrawingCache();
                if (itemBitmap != null) {
                    mBitmapHolder.drawBitmap(itemBitmap, 0, mYPos, null);
                }
                mView.destroyDrawingCache();
                mYPos += childHeight;
        }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            drawViews(mView, mBitmapHolder, mWidth);
            String s = FileUtils.saveLongImage(EventDetailActivity.this, mBitmap);
            progressDialog.cancel();
            if (!s.isEmpty()) {
                try {
                    Log.d("maxiee", "地址:" + s);
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + s)));
                } catch (Exception e) {e.printStackTrace();}
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        EventDetailActivity.this,
                        R.style.AppTheme_Dialog);
                builder.setTitle(getString(R.string.long_image));
                builder.setMessage(getString(R.string.generate_ok) + s);
                builder.setCancelable(false);
                final String path = s;
                builder.setPositiveButton(R.string.view, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse("file://" + path), "image/*");
                        startActivity(intent);
                        dialog.cancel();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(EventDetailActivity.this, getString(R.string.add_failed), Toast.LENGTH_LONG).show();
            }
        }

        private int measureViewsHeight(int width) {
            int height = 0;

            if (mImage != null) height += mImageBackDrop.getMeasuredHeight();

            height += mCardEvent.getMeasuredHeight();

            mView.measure(View.MeasureSpec.makeMeasureSpec(mWidth, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            int screenWidth = mTv.getMeasuredWidth();
            mIv.setVisibility(View.GONE);

            // get Size
            int rvItemNum = mThoughts.length();
            for (int i=0; i<rvItemNum; i++) {
                mTv.setText(mThoughts.get(i).getThought());
                if (mThoughts.get(i).hasImage()) {
                    mIv.setVisibility(View.VISIBLE);
                    Bitmap bmp = loadBitmap(screenWidth, LONG_IMAGE_IMAGE_MAX_HEIGHT, mThoughts.get(i).getPath());
                    mIv.setImageBitmap(bmp);
                } else {
                    mIv.setVisibility(View.GONE);
                }
                mView.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                height += mView.getMeasuredHeight();
            }
            return height;
        }

        private Bitmap loadBitmap(int reqWidth, int reqHeight, String path) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            int imageWidth = options.outWidth;;
            int imageHeight = options.outHeight;
            int inSampleSize = 1;

            if (imageWidth > reqWidth) {

                final int halfHeight = imageHeight / 2;
                final int halfWidth = imageWidth / 2;

                while ((halfHeight / inSampleSize) > reqHeight) {
                    inSampleSize *= 2;
                }
            }
            options.inJustDecodeBounds = false;
            options.inSampleSize = inSampleSize;
            Bitmap b =  BitmapFactory.decodeFile(path, options);
            int orientation = 1;
            try {
                ExifInterface exif = new ExifInterface(path);
                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            } catch (Exception e) {e.printStackTrace();}
            Matrix matrix = new Matrix();
            switch (orientation) {
                case 2:
                    matrix.setScale(-1, 1);
                    break;
                case 3:
                    matrix.setRotate(180);
                    break;
                case 4:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case 5:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case 6:
                    matrix.setRotate(90);
                    break;
                case 7:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case 8:
                    matrix.setRotate(-90);
                    break;
                default:
                    return b;
            }
            try {
                Bitmap oriented = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                b.recycle();
                return oriented;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return b;
            }
        }
    }

    private String getOrder(int position, int length) {
        String order;
        if (mSortingType.equals("0")) {
            switch (position) {
                case 0:
                    order = getString(R.string.firtime);
                    break;
                case 1:
                    order = getString(R.string.sectime);
                    break;
                default:
                    order = String.valueOf(position + 1) + ".";
                    break;
            }
        } else {
            if (position == length - 1) {
                order = getString(R.string.firtime);
            } else if (position == length - 2) {
                order = getString(R.string.sectime);
            } else {
                order = String.valueOf(length - position) + ".";
            }
        }
        return order;
    }
}
