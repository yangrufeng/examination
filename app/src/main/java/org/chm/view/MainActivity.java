package org.chm.view;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.support.v7.app.ActionBar.LayoutParams;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.chm.adpter.RecyclerViewAdapter;
import org.chm.base.BaseActivity;
import org.chm.base.BaseFragment;
import org.chm.bean.Answer;
import org.chm.bean.Qid;
import org.chm.bean.Question;
import org.chm.common.Common;
import org.chm.common.FileUtils;
import org.chm.common.MyAlertDialog;
import org.chm.dummy.DummyData;
import org.chm.R;
import org.chm.excel.AnswerExcel;
import org.chm.excel.QuestionExcel;
import org.chm.fragment.CheckBoxFragment;
import org.chm.fragment.QuestionFragment;
import org.chm.fragment.RadioBoxFragment;
import org.chm.popupwindow.NavPopupWindow;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements BaseFragment.OnFragmentInteractionListener {
    final int RIGHT = 0;
    final int LEFT = 1;
    final int UP = 2;
    final int DOWN = 3;
    private PopupWindow mNavPopupWindow;
    private GestureDetector gestureDetector;
    private View bottom;
    private SharedPreferences sp;
    private static int ANIM_TYPE_TOP_TO_BOTTOM = 1;
    private static int ANIM_TYPE_BOTTOM_TO_TOP = 2;
    private static int ANIM_TYPE_FADE = 3;
    private static final int CREATE_FOLDER_SUCCESS = 0;
    private static final int CREATE_FOLDER_FAILED = -1;

    public final static int DISTANT = 200;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (null != mNavPopupWindow && mNavPopupWindow.isShowing()) {
                    mNavPopupWindow.dismiss();
                } else {
                    MyAlertDialog.Builder builder = new MyAlertDialog.Builder(this);
                    builder.setMessage("您确定要退出么？");
                    builder.setTitle("提示");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            android.os.Process.killProcess(android.os.Process.myPid());    //获取PID
                            System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                }
                break;
            case KeyEvent.KEYCODE_MENU:
                if (null == mNavPopupWindow || !mNavPopupWindow.isShowing()) {
                    getPopupWindowInstance();
                    mNavPopupWindow.showAtLocation(this.findViewById(R.id.main), Gravity.START,
                            0, 0);
                }
                break;
            default:
                break;
        }
        return true;
    }
    private Handler handler = new Handler() {
        @Override
        // 当有消息发送出来的时候就执行Handler的这个方法
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CREATE_FOLDER_SUCCESS:
                    loadExcel();
                    break;
                case CREATE_FOLDER_FAILED:
                    throw new RuntimeException("创建目录失败!");
                default:
                    throw new IllegalArgumentException("数据无效！");
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = this.getSharedPreferences("tk", Context.MODE_PRIVATE);

        new Thread() {
            @Override
            public void run() {
                FileUtils.createSDCardDir(Common.QUESTION_FOLDER);
                handler.sendEmptyMessage(CREATE_FOLDER_SUCCESS);
            }
        }.start();


        gestureDetector = new GestureDetector(this, onGestureListener);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        bottom = this.findViewById(R.id.bottom_layout);
        if (bottom != null) {
            ImageView up = (ImageView)bottom.findViewById(R.id.btn_up);
            up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (DummyData.isInitalization()) {
                        Qid previous = DummyData.getPrevious();
                        showUI(previous.getId(), previous.getAnswerId(), ANIM_TYPE_TOP_TO_BOTTOM);
                    }
                }
            });
            ImageView down = (ImageView)bottom.findViewById(R.id.btn_down);
            down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (DummyData.isInitalization()) {
                        Qid next = DummyData.getNext();
                        showUI(next.getId(), next.getAnswerId(), ANIM_TYPE_BOTTOM_TO_TOP);
                    }
                }
            });
            bottom.setVisibility(View.GONE);
        }
        DummyData.setContentResolver(this.getContentResolver());
    }

    private void loadExcel() {
        /*
         * 已存在题库不需重新加载
         */
        if ("信息技术题库".equals(sp.getString("QUESTION_BANK", ""))) {
            return;
        }
        Uri uri;
        long rowNum = 0;
        List<Question> qList = QuestionExcel.readExcel_("test.xls");
        List<Answer> aList = AnswerExcel.readExcel_("test.xls");
        if (qList.size() != aList.size() || qList.isEmpty() || aList.isEmpty()) {
            System.out.println("问题列表长度："+qList.size());
            System.out.println("答案列表长度："+aList.size());
            throw new IllegalArgumentException("题库数据破损！"+aList.size());
        }
        List<Qid> qidList = new ArrayList<>();
        for (int i = 0; i < qList.size(); i++) {
            Qid qid = new Qid();
            qid.setId(qList.get(i).getId());
            qid.setAnswerId(aList.get(i).getId());
            qid.setFinished(false);
            qid.setParentId("0"); // 题库ID，暂时只有一个题库
            qidList.add(qid);
        }
        ContentResolver resolver = this.getContentResolver();
        for (Question q : qList) {
            ContentValues values = new ContentValues();
            values.put("id", q.getId());
            values.put("text", q.getText());
            values.put("type", q.getType());
            uri = resolver.insert(Uri.parse("content://org.chm.provider.myprovider/question"), values);
            rowNum = ContentUris.parseId(uri);
            if (rowNum == -1) {
                throw new RuntimeException("插入数据失败！");
            }
        }
        for (Answer a : aList) {
            ContentValues values = new ContentValues();
            values.put("id", a.getId());
            values.put("type", a.getType());
            values.put("ans1", a.getAns1());
            values.put("ans2", a.getAns2());
            values.put("ans3", a.getAns3());
            values.put("ans4", a.getAns4());
            uri = resolver.insert(Uri.parse("content://org.chm.provider.myprovider/answer"), values);
            rowNum = ContentUris.parseId(uri);
            if (rowNum == -1) {
                throw new RuntimeException("插入数据失败！");
            }
        }
        for (Qid qid : qidList) {
            ContentValues values = new ContentValues();
            values.put("id", qid.getId());
            values.put("answerId", qid.getAnswerId());
            values.put("parentId", qid.getParentId());
            values.put("isFinished", qid.isFinished());
            uri = resolver.insert(Uri.parse("content://org.chm.provider.myprovider/qid"), values);
            rowNum = ContentUris.parseId(uri);
            if (rowNum == -1) {
                throw new RuntimeException("插入数据失败！");
            }
        }
        sp.edit().putString("QUESTION_BANK", "信息技术题库").apply();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            /*
             * 防止由于OnItemClick或OnItemLongClick引起的冲突
			 */
            if (e1 == null || e2 == null) {
                return false;
            }
            float x = e2.getX() - e1.getX();
            float y = e2.getY() - e1.getY();
//            if (e2.getX() < e1.getX() + DISTANT || x < y) {
//                return false;
//            }
            if (x - DISTANT > 0 && Math.abs(x) > Math.abs(y)) {
                doResult(RIGHT);
            } else if (x - DISTANT < 0 && Math.abs(x) > Math.abs(y)) {
                doResult(LEFT);
            } else if (y - DISTANT > 0 && Math.abs(y) > Math.abs(x)) {
                doResult(UP);
            } else if (y - DISTANT < 0 && Math.abs(y) > Math.abs(x)) {
                doResult(DOWN);
            }
            return true;
        }
    };

    public void doResult(int action) {

        switch (action) {
            case RIGHT:
                System.out.println("go right");
                if (null == mNavPopupWindow || !mNavPopupWindow.isShowing()) {
                    getPopupWindowInstance();
                    mNavPopupWindow.showAtLocation(this.findViewById(R.id.main), Gravity.START,
                            0, 0);
                }
                break;

            case LEFT:
                System.out.println("go left");
                if (null != mNavPopupWindow && mNavPopupWindow.isShowing()) {
                    mNavPopupWindow.dismiss();
                    return;
                }
                break;
            case UP:
                System.out.println("go up");
                if (null != mNavPopupWindow && mNavPopupWindow.isShowing()) {
                    return;
                }
                if (DummyData.isInitalization()) {
                    Qid previous = DummyData.getPrevious();
                    showUI(previous.getId(), previous.getAnswerId(), ANIM_TYPE_TOP_TO_BOTTOM);
                }
                break;
            case DOWN:
                System.out.println("go down");
                if (null != mNavPopupWindow && mNavPopupWindow.isShowing()) {
                    return;
                }
                if (DummyData.isInitalization()) {
                    Qid next = DummyData.getNext();
                    showUI(next.getId(), next.getAnswerId(), ANIM_TYPE_BOTTOM_TO_TOP);
                }

                break;
        }
    }

    private void initPopuptWindow() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View popupWindow = layoutInflater.inflate(R.layout.popupwinow, null);
        RecyclerView recyclerView = (RecyclerView) popupWindow.findViewById(R.id.waterfall_content);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        RecyclerViewAdapter recycleAdapter = new RecyclerViewAdapter(this, recyclerView);
        recyclerView.setAdapter(recycleAdapter);
        recyclerView.addItemDecoration(new SpaceItem(10));
        recycleAdapter.setOnItemClickLitener(new RecyclerViewAdapter.OnItemClickLitener() {

            @Override
            public void onItemClick(View view, int position) {
                showUI(view.getTag(R.id.tag_qid_id).toString(), view.getTag(R.id.tag_qid_answer_id).toString(), ANIM_TYPE_FADE);
                System.out.println("bbbbbbbbbbbbb"+position);
                DummyData.setCurrentIndex(position);
            }
        });

		/*
		 * 获取屏幕分辨率
		 */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        mNavPopupWindow = new NavPopupWindow(this, popupWindow,
                (int) (displayMetrics.widthPixels * 0.5),
                LayoutParams.MATCH_PARENT);
        mNavPopupWindow.setFocusable(true);
        Bitmap bm = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.popupwindow_bg);
        Drawable bg = new BitmapDrawable(bm);
        mNavPopupWindow.setBackgroundDrawable(bg);
        mNavPopupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        mNavPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });
    }

    private void showUI(String qId, String aId, int animType) {
        //创建修改实例
        Fragment qFragment = QuestionFragment.newInstance(qId, "");
        FragmentTransaction transaction = MainActivity.this.getFragmentManager().beginTransaction();
        // Replace whatever is in thefragment_container view with this fragment,
        // and add the transaction to the backstack
        if (animType == ANIM_TYPE_TOP_TO_BOTTOM) {
            transaction.setCustomAnimations(R.animator.dync_in_from_top, R.animator.dync_out_to_bottom);
        } else if (animType == ANIM_TYPE_BOTTOM_TO_TOP) {
            transaction.setCustomAnimations(R.animator.dync_in_from_bottom, R.animator.dync_out_to_top);
        } else {
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        }

        transaction.replace(R.id.question_layout, qFragment);
        transaction.addToBackStack(null);


        Answer a = DummyData.getAnswerById(aId);
        String type = a.getType();

        //创建修改实例
        Fragment newFragment = null;
        if (type.equals(Common.Q_TYPE_SINGLE)) {
            newFragment = RadioBoxFragment.newInstance(a.getId(), "");
        } else if (type.equals(Common.Q_TYPE_MULTIPLE)) {
            newFragment = CheckBoxFragment.newInstance(a.getId(), "");
        } else if (type.equals(Common.Q_TYPE_JUDGE)) {
            newFragment = QuestionFragment.newInstance(a.getId(), "");
        } else {
            // do nothing
        }

        if (null == newFragment) {
            return;
        }

        transaction.replace(R.id.answer_layout, newFragment);
        transaction.addToBackStack(null);
        //提交修改
        transaction.commit();
        if (bottom != null && View.GONE == bottom.getVisibility()) {
            bottom.setVisibility(View.VISIBLE);
        }

        if (null != mNavPopupWindow && mNavPopupWindow.isShowing()) {
            mNavPopupWindow.dismiss();
        }
    }
    /*
	 * 获取PopupWindow实例
	 */
    private void getPopupWindowInstance() {
        if (null != mNavPopupWindow && mNavPopupWindow.isShowing()) {
            mNavPopupWindow.dismiss();
        } else {
            initPopuptWindow();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        if (uri == null) {
            return;
        }

        /*
         * 点选答案
         */
        if (uri.getPath().contains("answer")) {
             DummyData.setCurrentQuestionAnswer(ContentUris.parseId(uri)+"");
        }

    }

    class SpaceItem extends RecyclerView.ItemDecoration {
        private int space;

        public SpaceItem(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = (space + space);
        }
    }
}
