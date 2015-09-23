package com.maxiee.heartbeat.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.maxiee.heartbeat.R;
import com.maxiee.heartbeat.common.FileUtils;
import com.maxiee.heartbeat.common.TimeUtils;
import com.maxiee.heartbeat.common.tagview.Tag;
import com.maxiee.heartbeat.common.tagview.TagView;
import com.maxiee.heartbeat.database.api.AddImageApi;
import com.maxiee.heartbeat.database.api.GetAllThoughtApi;
import com.maxiee.heartbeat.database.api.GetImageByEventKeyApi;
import com.maxiee.heartbeat.database.api.GetLabelsByEventKeyApi;
import com.maxiee.heartbeat.database.api.GetOneEventApi;
import com.maxiee.heartbeat.model.Event;
import com.maxiee.heartbeat.model.Thoughts;
import com.maxiee.heartbeat.ui.adapter.ThoughtTimeaxisAdapter;
import com.maxiee.heartbeat.ui.dialog.EditEventDialog;

import java.util.ArrayList;

/**
 * Created by maxiee on 15-6-13.
 */
public class EventDetailActivity extends AppCompatActivity {
    private final static String TAG = EventDetailActivity.class.getSimpleName();

    public static final String EXTRA_NAME = "id";

    public final static int EVENT_DETAIL = 200;
    public final static int EVENT_DETAIL_MODIFIED = 201;

    private static final int ADD_IMAGE = 1127;

    private Event mEvent;

    private TextView mTvEvent;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ThoughtTimeaxisAdapter mAdapter;
    private TagView mTagView;
    private TextView mTvTime;
    private ImageView mImageBackDrop;
    private View mCardEvent;
    private int mId;
    private TextView mAddImageText;
    private String mImagePath;
    private Thoughts mThoughts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        Intent intent = getIntent();
        mId = intent.getIntExtra(EXTRA_NAME, -1);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle("");
        mTvEvent = (TextView) findViewById(R.id.tv_event);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mTagView = (TagView) findViewById(R.id.tagview);
        mTvTime = (TextView) findViewById(R.id.tv_time);
        mImageBackDrop = (ImageView) findViewById(R.id.backdrop);
        mCardEvent = (View) findViewById(R.id.card_event);
        mAddImageText = (TextView) findViewById(R.id.add_imgae);

        mEvent =  new GetOneEventApi(this, mId).exec();
        mTvEvent.setText(mEvent.getmEvent());
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        updateTagView();

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
                EditEventDialog dialog = new EditEventDialog(
                        EventDetailActivity.this,
                        mEvent
                );
                dialog.setOnEditFinishedListener(new EditEventDialog.OnEditFinishedListener() {
                    @Override
                    public void update(String event) {
                        mTvEvent.setText(event);
                        updateTagView();
                        setResult(EVENT_DETAIL_MODIFIED);
                    }

                    @Override
                    public void remove() {
                        setResult(EVENT_DETAIL_MODIFIED);
                        finish();
                    }
                });
                dialog.show();
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

        initImage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mThoughts = new GetAllThoughtApi(this, mEvent.getmId()).exec();
        mAdapter = new ThoughtTimeaxisAdapter(mThoughts);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initImage() {
        mImagePath = new GetImageByEventKeyApi(this, mEvent.getmId()).exec();
        if (mImagePath == null) {
            mAddImageText.setVisibility(View.VISIBLE);
            mImageBackDrop.setOnClickListener(new View.OnClickListener() {
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
            return;
        }
        mAddImageText.setVisibility(View.INVISIBLE);
        Glide.with(this)
                .load(mImagePath)
                .into(mImageBackDrop);
        mImageBackDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(EventDetailActivity.this, GalleryActivity.class);
                i.putExtra(GalleryActivity.EVENT_ID, mEvent.getmId());
                startActivity(i);
            }
        });
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
            generateLongImage();
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateTagView() {
        mTagView.clear();
        ArrayList<String> labels = new GetLabelsByEventKeyApi(this, mEvent.getmId()).exec();
        if (labels != null) {
            for (String label: labels) {
                mTagView.addTag(new Tag(label));
            }
        }
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
            new AddImageApi(EventDetailActivity.this, mEvent.getmId(), path).exec();
            initImage();
        }
    }

    private void generateLongImage() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = 0;
        int width = displaymetrics.widthPixels;

        if (mImagePath != null) {
            height += mImageBackDrop.getMeasuredHeight();
        }

        height += mCardEvent.getMeasuredHeight();

        int rvItemNum = mThoughts.length();
        FrameLayout fl = new FrameLayout(this);
        View item = getLayoutInflater().inflate(R.layout.item_thought_timeaxis, fl);
        TextView tv = (TextView) item.findViewById(R.id.tv_thought);
        ImageView iv = (ImageView) item.findViewById(R.id.image_thought);
        CardView cv = (CardView) item.findViewById(R.id.card);
        item.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        int screenWidth = tv.getMeasuredWidth();
        iv.setVisibility(View.GONE);
        int imageMaxHeight = 300;

        // 尺寸获取
        for (int i=0; i<rvItemNum; i++) {
            tv.setText(mThoughts.get(i).getThought());
            if (mThoughts.get(i).hasImage()) {
                iv.setVisibility(View.VISIBLE);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mThoughts.get(i).getPath(), options);
                int imageWidth = options.outWidth;;
                int imageHeight = options.outHeight;
                int inSampleSize = 1;

                if (imageWidth > screenWidth || width > screenWidth) {

                    final int halfHeight = imageHeight / 2;
                    final int halfWidth = imageWidth / 2;

                    while ((halfHeight / inSampleSize) > imageMaxHeight
                            && (halfWidth / inSampleSize) > imageMaxHeight) {
                        inSampleSize *= 2;
                    }
                }
                BitmapFactory.Options options2 = new BitmapFactory.Options();
                options2.inSampleSize = inSampleSize;
                Bitmap bmp =  BitmapFactory.decodeFile(mThoughts.get(i).getPath(), options2);
                iv.setImageBitmap(bmp);

//                iv.setVisibility(View.VISIBLE);
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inJustDecodeBounds = true;
//                BitmapFactory.decodeFile(mThoughts.get(i).getPath(), options);
//                int imageWidth = options.outWidth;
//                int imageHeight = options.outHeight;
//                float ratio = screenWidth * 1f / imageWidth;
//                Log.d("maxiee", "带图感想比例值:" + String.valueOf(ratio));
//                int screenHeight = (int) ((float) imageHeight * ratio);
//                Log.d("maxiee", "带图感想高度测量:" + String.valueOf(screenHeight));
//                height += screenHeight;
//                imageHeightList[i] = screenHeight;
            } else {
                iv.setVisibility(View.GONE);
            }
            item.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            height += item.getMeasuredHeight();
        }

        Log.d("maxiee", "生成Bitmap:" + String.valueOf(width) + "," + String.valueOf(height));
        Bitmap bitmap = Bitmap.createBitmap(
                width,
                height,
                Bitmap.Config.ARGB_8888);
        Canvas bitmapHolder = new Canvas(bitmap);

        int yPos = 0;
        if (mImagePath != null) {
            int backDropHeight = mImageBackDrop.getMeasuredHeight();
            mImageBackDrop.layout(0, 0, width, backDropHeight);
            mImageBackDrop.buildDrawingCache();
            Bitmap backDropBitmap = mImageBackDrop.getDrawingCache();
            if (backDropBitmap != null) {
                bitmapHolder.drawBitmap(backDropBitmap, 0, 0, null);
            }
            yPos += backDropHeight;
        }

        int cardEventHeight = mCardEvent.getMeasuredHeight();
        mCardEvent.layout(0, 0, width, cardEventHeight);
        mCardEvent.buildDrawingCache();
        Bitmap eventBitmap = mCardEvent.getDrawingCache();
        if (eventBitmap != null) {
            bitmapHolder.drawBitmap(eventBitmap, 0, yPos, null);
        }
        yPos += cardEventHeight;

        Bitmap itemBitmap;
        int childHeight;
        for (int i=0; i<rvItemNum; i++) {
            tv.setText(mThoughts.get(i).getThought());
            if (mThoughts.get(i).hasImage()) {
                iv.setVisibility(View.VISIBLE);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mThoughts.get(i).getPath(), options);
                int imageWidth = options.outWidth;;
                int imageHeight = options.outHeight;
                int inSampleSize = 1;

                if (imageWidth > screenWidth || width > screenWidth) {

                    final int halfHeight = imageHeight / 2;
                    final int halfWidth = imageWidth / 2;

                    while ((halfHeight / inSampleSize) > imageMaxHeight
                            && (halfWidth / inSampleSize) > imageMaxHeight) {
                        inSampleSize *= 2;
                    }
                }
                BitmapFactory.Options options2 = new BitmapFactory.Options();
                options2.inSampleSize = inSampleSize;
                Bitmap bmp =  BitmapFactory.decodeFile(mThoughts.get(i).getPath(), options2);
                iv.setImageBitmap(bmp);
            } else {
                iv.setVisibility(View.GONE);
            }
            item.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            childHeight = item.getMeasuredHeight();
            item.layout(0, 0, width, childHeight);
            item.buildDrawingCache();
            itemBitmap = item.getDrawingCache();
            if (itemBitmap != null) {
                bitmapHolder.drawBitmap(itemBitmap, 0, yPos, null);
            }
            item.destroyDrawingCache();
            yPos += childHeight;
        }

        final String savedPath = FileUtils.saveLongImage(EventDetailActivity.this, bitmap);
        if (!savedPath.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    EventDetailActivity.this,
                    R.style.AppTheme_Dialog);
            builder.setTitle(getString(R.string.long_image));
            builder.setMessage(getString(R.string.generate_ok) + savedPath);
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.view, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse("file://" + savedPath), "image/*");
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
        }
    }
}
