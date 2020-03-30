package megvii.testfacepass.pa.ui;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;


import android.graphics.Bitmap;
import android.graphics.Typeface;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;



import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;



import com.pingan.ai.access.common.PaAccessDetectConfig;


import com.pingan.ai.access.entiry.YuvInfo;
import com.pingan.ai.access.impl.OnPaAccessDetectListener;
import com.pingan.ai.access.manager.PaAccessControl;



import com.pingan.ai.access.result.PaAccessDetectFaceResult;
import com.pingan.ai.access.result.PaAccessMultiFaceBaseInfo;


import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import io.objectbox.Box;

import megvii.testfacepass.pa.MyApplication;
import megvii.testfacepass.pa.R;
import megvii.testfacepass.pa.beans.BaoCunBean;
import megvii.testfacepass.pa.beans.Subject;

import megvii.testfacepass.pa.camera.CameraManager;
import megvii.testfacepass.pa.camera.CameraManager2;
import megvii.testfacepass.pa.camera.CameraPreview;
import megvii.testfacepass.pa.camera.CameraPreview2;
import megvii.testfacepass.pa.camera.CameraPreviewData;
import megvii.testfacepass.pa.camera.CameraPreviewData2;


import megvii.testfacepass.pa.dialog.RegisterDialog;

import megvii.testfacepass.pa.utils.FileUtil;
import megvii.testfacepass.pa.utils.NV21ToBitmap;
import megvii.testfacepass.pa.utils.SettingVar;
import megvii.testfacepass.pa.utils.ToastUtils;


public class YuLanActivity extends Activity implements CameraManager.CameraListener, View.OnClickListener ,CameraManager2.CameraListener2{

    private enum FacePassSDKMode {
        MODE_ONLINE,
        MODE_OFFLINE
    }
    private int pp = 0;
    private static FacePassSDKMode SDK_MODE = FacePassSDKMode.MODE_OFFLINE;
    private static final String DEBUG_TAG = "FacePassDemo";
    private static final int MSG_SHOW_TOAST = 1;
    private static final int DELAY_MILLION_SHOW_TOAST = 2000;
    /* 人脸识别Group */
    private static final String group_name = "facepasstestx";
    RegisterDialog registerDialog;

  /* SDK 实例对象 */
  //  FacePassHandler mFacePassHandler;

    /* 相机实例 */
    private CameraManager manager;

    /* 显示人脸位置角度信息 */
    private TextView faceBeginTextView;

    /* 显示faceId */
    private TextView faceEndTextView;

    /* 相机预览界面 */
    private CameraPreview cameraView;

    private boolean isLocalGroupExist = true;
    private Bitmap msrBitmap = null;
    /* 在预览界面圈出人脸 */
   // private FaceView faceView;
    private static String faceId = "";
    private long feature2 = -1;
    private NV21ToBitmap nv21ToBitmap;
    private ScrollView scrollView;

    /* 相机是否使用前置摄像头 */
    private static boolean cameraFacingFront = true;
    /* 相机图片旋转角度，请根据实际情况来设置
     * 对于标准设备，可以如下计算旋转角度rotation
     * int windowRotation = ((WindowManager)(getApplicationContext().getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay().getRotation() * 90;
     * Camera.CameraInfo info = new Camera.CameraInfo();
     * Camera.getCameraInfo(cameraFacingFront ? Camera.CameraInfo.CAMERA_FACING_FRONT : Camera.CameraInfo.CAMERA_FACING_BACK, info);
     * int cameraOrientation = info.orientation;
     * int rotation;
     * if (cameraFacingFront) {
     *     rotation = (720 - cameraOrientation - windowRotation) % 360;
     * } else {
     *     rotation = (windowRotation - cameraOrientation + 360) % 360;
     * }
     */
    private int cameraRotation;

    private static final int cameraWidth = 1280;
    private static final int cameraHeight = 720;


    private int heightPixels;
    private int widthPixels;

    int screenState = 0;// 0 横 1 竖

    /* 网络请求队列*/
   // RequestQueue requestQueue;


    Button visible;
    LinearLayout ll;
    FrameLayout frameLayout;
    private int buttonFlag = 0;
    /*Toast 队列*/
    LinkedBlockingQueue<Toast> mToastBlockQueue;
    /*DetectResult queue*/
    ArrayBlockingQueue<byte[]> mDetectResultQueue;
   // ArrayBlockingQueue<FacePassImage> mFeedFrameQueue;
    /*recognize thread*/
  //  RecognizeThread mRecognizeThread;

   // FeedFrameThread mFeedFrameThread;
    /*底库同步*/
    private Button mSyncGroupBtn;
   // private AlertDialog mSyncGroupDialog;

    private Button mFaceOperationBtn;
    /*图片缓存*/

   ;

    private Box<BaoCunBean> baoCunBeanDao = null;
    private BaoCunBean baoCunBean = null;
    private Box<Subject> subjectBox = null;
    private CameraManager2 manager2;
   // private PaAccessControl paFacePass;
    private CameraPreview2 cameraView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToastBlockQueue = new LinkedBlockingQueue<>();
        mDetectResultQueue = new ArrayBlockingQueue<byte[]>(5);
       // mFeedFrameQueue = new ArrayBlockingQueue<FacePassImage>(1);
        //initAndroidHandler();
        baoCunBeanDao = MyApplication.myApplication.getBaoCunBeanBox();
        baoCunBean = baoCunBeanDao.get(123456L);
        subjectBox=MyApplication.myApplication.getSubjectBox();
      //  mFacePassHandler=MyApplication.myApplication.getFacePassHandler();
        nv21ToBitmap = new NV21ToBitmap(YuLanActivity.this);



        /* 初始化界面 */
        initView();

        manager.open(getWindowManager(), SettingVar.cameraId, cameraWidth, cameraHeight);//前置是1
        if (SettingVar.cameraId==1){
            manager2.open(getWindowManager(), 0, cameraWidth, cameraHeight, SettingVar.cameraPreviewRotation2);//最后一个参数是红外预览方向
        }else {
            manager2.open(getWindowManager(), 1, cameraWidth, cameraHeight, SettingVar.cameraPreviewRotation2);//最后一个参数是红外预览方向
        }


    }


    OnPaAccessDetectListener onDetectListener = new OnPaAccessDetectListener() {
        //每一帧数据的回调
        @Override
        public void onFaceDetectFrame(int message, PaAccessDetectFaceResult faceDetectFrame) {


        }

        @Override
        public void onFaceDetectResult(int var1, PaAccessDetectFaceResult detectResult) {

        }

        @Override
        public void onMultiFacesDetectFrameBaseInfo(int i, List<PaAccessMultiFaceBaseInfo> list) {

            Log.d("MianBanJiActivity3", "list.size():" + list.size());

        }
    };









    @Override
    protected void onResume() {
        //checkGroup();
       // initToast();
        /* 打开相机 */
       // manager.open(getWindowManager(), SettingVar.cameraId, cameraWidth, cameraHeight);
       // paFacePass.startFrameDetect();
     //   adaptFrameLayout();
        super.onResume();
    }



    @Override
    protected void onPause() {
        super.onPause();


    }



    private YuvInfo rgb, ir;
    /* 相机回调函数 */
    @Override
    public void onPictureTaken(final CameraPreviewData cameraPreviewData) {



    }



    private void initView() {

        int windowRotation = ((WindowManager) (getApplicationContext().getSystemService(Context.WINDOW_SERVICE))).getDefaultDisplay().getRotation() * 90;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        SharedPreferences preferences = getSharedPreferences(SettingVar.SharedPrefrence, Context.MODE_PRIVATE);
        SettingVar.isSettingAvailable = preferences.getBoolean("isSettingAvailable", SettingVar.isSettingAvailable);
        SettingVar.cameraId = preferences.getInt("cameraId", SettingVar.cameraId);
        SettingVar.faceRotation = preferences.getInt("faceRotation", SettingVar.faceRotation);
        SettingVar.cameraPreviewRotation = preferences.getInt("cameraPreviewRotation", SettingVar.cameraPreviewRotation);
        SettingVar.cameraFacingFront = preferences.getBoolean("cameraFacingFront", SettingVar.cameraFacingFront);
        SettingVar.cameraPreviewRotation2 = preferences.getInt("cameraPreviewRotation2", SettingVar.cameraPreviewRotation2);
        SettingVar.faceRotation2 = preferences.getInt("faceRotation2", SettingVar.faceRotation2);
        SettingVar.msrBitmapRotation = preferences.getInt("msrBitmapRotation", SettingVar.msrBitmapRotation);



        Log.i("orientation", String.valueOf(windowRotation));
        final int mCurrentOrientation = getResources().getConfiguration().orientation;

        if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            screenState = 1;
        } else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            screenState = 0;
        }
        setContentView(R.layout.activity_yulan);

        mSyncGroupBtn = findViewById(R.id.btn_group_name);
        mSyncGroupBtn.setOnClickListener(this);

        mFaceOperationBtn = findViewById(R.id.btn_face_operation);
        mFaceOperationBtn.setOnClickListener(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        heightPixels = displayMetrics.heightPixels;
        widthPixels = displayMetrics.widthPixels;
        SettingVar.mHeight = heightPixels;
        SettingVar.mWidth = widthPixels;
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        AssetManager mgr = getAssets();
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/Univers LT 57 Condensed.ttf");
        /* 初始化界面 */
       // faceEndTextView = (TextView) this.findViewById(R.id.tv_meg2);
       // faceEndTextView.setTypeface(tf);
       Button fanhui = findViewById(R.id.fanhui);
       fanhui.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               finish();
           }
       });
        SettingVar.cameraSettingOk = false;
       // ll = (LinearLayout) this.findViewById(R.id.ll);
       // ll.getBackground().setAlpha(100);
       // visible = (Button) this.findViewById(R.id.visible);
      //  visible.setBackgroundResource(R.drawable.debug);
//        visible.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (buttonFlag == 0) {
//                    ll.setVisibility(View.VISIBLE);
//                    if (mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT) {
//                        visible.setBackgroundResource(R.drawable.down);
//                    } else if (mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
//                        visible.setBackgroundResource(R.drawable.right);
//                    }
//                    buttonFlag = 1;
//                } else if (buttonFlag == 1) {
//                    buttonFlag = 0;
//                    if (SettingVar.isButtonInvisible)
//                        ll.setVisibility(View.INVISIBLE);
//                    else
//                        ll.setVisibility(View.GONE);
//                    visible.setBackgroundResource(R.drawable.debug);
//                }
//
//            }
//        });
        manager = new CameraManager();
        cameraView = (CameraPreview) findViewById(R.id.preview);
        manager.setPreviewDisplay(cameraView);
       // frameLayout = (FrameLayout) findViewById(R.id.frame);
        /* 注册相机回调函数 */
        manager.setListener(this);

        manager2 = new CameraManager2();
        cameraView2 = findViewById(R.id.preview2);
        manager2.setPreviewDisplay(cameraView2);
        /* 注册相机回调函数 */
        manager2.setListener(this);
    }


    @Override
    protected void onStop() {

        mToastBlockQueue.clear();
        mDetectResultQueue.clear();

        if (manager != null) {
            manager.release();
        }
        super.onStop();
    }

    @Override
    protected void onRestart() {
       // faceView.clear();
       // faceView.invalidate();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
      //  mRecognizeThread.isInterrupt = true;

     //   mRecognizeThread.interrupt();
//        if (requestQueue != null) {
//            requestQueue.cancelAll("upload_detect_result_tag");
//            requestQueue.cancelAll("handle_sync_request_tag");
//            requestQueue.cancelAll("load_image_request_tag");
//            requestQueue.stop();
//        }
        if (manager2 != null) {
            manager2.release();
        }
        if (manager != null) {
            manager.release();
        }
        if (mToastBlockQueue != null) {
            mToastBlockQueue.clear();
        }


        //paFacePass.releasePaAccessControl();

     //   EventBus.getDefault().unregister(this);//解除订阅
        super.onDestroy();
    }


    /* 相机回调函数 */
    @Override
    public void onPictureTaken2(CameraPreviewData2 cameraPreviewData) {
        /* 如果SDK实例还未创建，则跳过 */
        // Log.d("MianBanJiActivity3", "cameraPreviewData2.rotation:" + cameraPreviewData.front);
//        if (paAccessControl == null) {
//            return;
//        }
//        //  paAccessControl.offerFrameBuffer(cameraPreviewData.nv21Data, cameraPreviewData.width, cameraPreviewData.height,SettingVar.faceRotation, SettingVar.getCaneraID());
//        try {
//            ir = new YuvInfo(cameraPreviewData.nv21Data, cameraPreviewData.front, 270, cameraPreviewData.width, cameraPreviewData.height);
//            if (rgb == null || !baoCunBean.isHuoTi())
//                return;
//            int result = paAccessControl.offerIrFrameBuffer(rgb, ir);//提供数据到队列
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // Log.d("MianBanJiActivity3", "cameraPreviewData.result:" + result);
        /* 将相机预览帧转成SDK算法所需帧的格式 FacePassImage */
    }

    private static final int REQUEST_CODE_CHOOSE_PICK = 1;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_group_name:
                //paFacePass.startFrameDetect();

                break;
            case R.id.btn_face_operation:
                //showAddFaceDialog();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            //从相册选取照片后读取地址
            case REQUEST_CODE_CHOOSE_PICK:
                if (resultCode == RESULT_OK) {
                    String path = "";
                    Uri uri = data.getData();
                    String[] pojo = {MediaStore.Images.Media.DATA};
                    CursorLoader cursorLoader = new CursorLoader(this, uri, pojo, null, null, null);
                    Cursor cursor = cursorLoader.loadInBackground();
                    if (cursor != null) {
                        cursor.moveToFirst();
                        path = cursor.getString(cursor.getColumnIndex(pojo[0]));
                    }
                    if (!TextUtils.isEmpty(path) && "file".equalsIgnoreCase(uri.getScheme())) {
                        path = uri.getPath();
                    }
                    if (TextUtils.isEmpty(path)) {
                        try {
                            path = FileUtil.getPath(getApplicationContext(), uri);
                        } catch (Exception e) {
                            Log.d("YuLanActivity", e.getMessage());
                        }
                    }
                    if (TextUtils.isEmpty(path)) {
                        ToastUtils.show(YuLanActivity.this, "图片选取失败");
                        return;
                    }
                    if (!TextUtils.isEmpty(path) && mFaceOperationDialog != null && mFaceOperationDialog.isShowing()) {
                        EditText imagePathEdt = (EditText) mFaceOperationDialog.findViewById(R.id.et_face_image_path);
                        imagePathEdt.setText(path);
                    }
                }
                break;
        }
    }



//
//    private AlertDialog mFaceShiPingDialog;
//
//    private void shipingAddFaceDialog(final Bitmap fileBitmap, final float[] feature) {
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//        if (mFaceShiPingDialog != null && !mFaceShiPingDialog.isShowing()) {
//            mFaceShiPingDialog.show();
//            return;
//        }
//        if (mFaceShiPingDialog != null && mFaceShiPingDialog.isShowing()) {
//           ImageView ii= mFaceShiPingDialog.findViewById(R.id.touxiang);
//           ii.setImageBitmap(fileBitmap);
//            return;
//        }
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(YuLanActivity.this);
//        View view = LayoutInflater.from(YuLanActivity.this).inflate(R.layout.layout_dialog_face_operation22, null);
//        builder.setView(view);
//
//        final EditText name = (EditText) view.findViewById(R.id.name);
//        final EditText bumen = (EditText) view.findViewById(R.id.bumen);
//
//        Button chongpai =view.findViewById(R.id.chongpai);
//        Button queding=view.findViewById(R.id.queding);
//        ImageView touxiang = (ImageView) view.findViewById(R.id.touxiang);
//        ImageView closeIv = (ImageView) view.findViewById(R.id.iv_close);
//        touxiang.setImageBitmap(fileBitmap);

//        queding.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (name.getText().toString().trim().equals("")){
//                    ToastUtils.show(YuLanActivity.this, "姓名必填");
//                }else {
//                    //开始添加
//                    shipinFaceId= String.valueOf(System.currentTimeMillis());
//                    int result = paFacePass.addFace(shipinFaceId, feature, MyApplication.GROUP_FRAME);//指定group
//                    if (result != PaAccessControlMessage.RESULT_OK) {
//                        ToastUtils.show2(YuLanActivity.this, "注册失败 message=" + result);
//
//                        return;
//                    }
//                    BitmapUtil.saveBitmapToSD(fileBitmap, MyApplication.SDPATH3, shipinFaceId + ".png");
//
//                    Subject subject=new Subject();
//                    subject.setId(System.currentTimeMillis());
//                    subject.setDepartmentName(bumen.getText().toString().trim());
//                    subject.setName(name.getText().toString().trim());
//                    subject.setPeopleType("员工");
//                    subject.setTeZhengMa(shipinFaceId);
//                    subjectBox.put(subject);
//                    shipinFaceId=null;
//                    ToastUtils.show(YuLanActivity.this, "入库成功");
//                    mFaceShiPingDialog.dismiss();
//                }
//
//
//            }
//        });
//
//
//
//        chongpai.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                paFacePass.startFrameDetect();
//                isLink=true;
//            }
//        });
//
//        closeIv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mFaceShiPingDialog.dismiss();
//            }
//        });
//
//        WindowManager m = getWindowManager();
//        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
//        mFaceShiPingDialog = builder.create();
//        WindowManager.LayoutParams attributes = mFaceShiPingDialog.getWindow().getAttributes();
//        attributes.gravity=Gravity.CENTER;
//        attributes.height = d.getHeight();
//        attributes.width = d.getWidth();
//        mFaceShiPingDialog.getWindow().setAttributes(attributes);
//        mFaceShiPingDialog.show();
//
//            }
//        });
 //   }

    private AlertDialog mFaceOperationDialog;
//
//    private void showAddFaceDialog() {
//
//
//        if (mFaceOperationDialog != null && !mFaceOperationDialog.isShowing()) {
//            mFaceOperationDialog.show();
//            return;
//        }
//        if (mFaceOperationDialog != null && mFaceOperationDialog.isShowing()) {
//            return;
//        }
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        View view = LayoutInflater.from(this).inflate(R.layout.layout_dialog_face_operation, null);
//        builder.setView(view);
//
//        final EditText faceImagePathEt = (EditText) view.findViewById(R.id.et_face_image_path);
//        final EditText faceTokenEt = (EditText) view.findViewById(R.id.et_face_token);
//        final EditText groupNameEt = (EditText) view.findViewById(R.id.et_group_name);
//
//        Button choosePictureBtn = (Button) view.findViewById(R.id.btn_choose_picture);
//        Button addFaceBtn = (Button) view.findViewById(R.id.btn_add_face);
//      //  Button getFaceImageBtn = (Button) view.findViewById(R.id.btn_get_face_image);
//        Button deleteFaceBtn = (Button) view.findViewById(R.id.btn_delete_face);
//        Button bindGroupFaceTokenBtn =  view.findViewById(R.id.btn_bind_group);
//       // Button getGroupInfoBtn = (Button) view.findViewById(R.id.btn_get_group_info);
//        Button queding=view.findViewById(R.id.queding);
//
//        ImageView closeIv = (ImageView) view.findViewById(R.id.iv_close);
//
//      //  final ListView groupInfoLv = (ListView) view.findViewById(R.id.lv_group_info);
//
//       // final FaceTokenAdapter faceTokenAdapter = new FaceTokenAdapter();
//
//      //  groupNameEt.setText(group_name);
//
//
//        queding.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (faceTokenEt.getText().toString().trim().equals("")){
//                    toast("姓名必填");
//                }else {
//
//                        if (faceImagePathEt.getText().toString().trim().equals("")){
//                            ToastUtils.show(YuLanActivity.this, "请先选择图片");
//                            return;
//                        }
//
//                        //开始添加
//                        String imagePath = faceImagePathEt.getText().toString();
//                        if (TextUtils.isEmpty(imagePath)) {
//                            ToastUtils.show(YuLanActivity.this, "请输入正确的图片路径");
//                            return;
//                        }
//
//                        File imageFile = new File(imagePath);
//                        if (!imageFile.exists()) {
//                            ToastUtils.show(YuLanActivity.this, "图片不存在");
//                            return;
//                        }
//
//                        Bitmap bmp=null;
//                    PaAccessDetectFaceResult detectResult = PaAccessControl.getInstance().detectFaceByFile(imagePath);
//                        if (detectResult!=null && detectResult.message==PaAccessControlMessage.RESULT_OK){
//                            bmp = BitmapUtil.getCropBitmap(BitmapFactory.decodeFile(imagePath), detectResult.rectX, detectResult.rectY
//                                    , detectResult.rectW, detectResult.rectH);
//                            tupianFaceId= String.valueOf(System.currentTimeMillis());
////                    boolean attributeEnable = PaAccessControl.getInstance().getFaceDetectConfig().isAttributeEnable();
////                    String gender = "";
////                    StringBuilder buffer = new StringBuilder();
////                    buffer.append(id);
////                    if (attributeEnable) { //Robin 开启了人脸属性的检测
////                        gender = getGender(result.gender);
////                        buffer.append(",性别：" + gender);
////                        buffer.append(",年龄：" + result.age);
////                    }
////                    buffer.append(" 注册成功");
//
//                            //从原图中拿到的人脸小图
////            bmp = BitmapUtil.getCropBitmap(bmp, result.facePassImage.rectX, result.facePassImage.rectY, result.facePassImage.rectW, result.facePassImage.rectH);
//
//                            BitmapUtil.saveBitmapToSD(bmp, MyApplication.SDPATH3, tupianFaceId + ".png");
//
//                            PaAccessControl.getInstance().addFace(tupianFaceId, detectResult.feature, MyApplication.GROUP_IMAGE);
//                            Subject subject=new Subject();
//                            subject.setId(System.currentTimeMillis());
//                            subject.setDepartmentName(groupNameEt.getText().toString().trim());
//                            subject.setName(faceTokenEt.getText().toString().trim());
//                            subject.setPeopleType("员工");
//                            subject.setTeZhengMa(tupianFaceId);
//                            subjectBox.put(subject);
//                            tupianFaceId=null;
//                            ToastUtils.show(YuLanActivity.this, "入库成功");
//                            mFaceOperationDialog.dismiss();
//
//                        }else {
//                            ToastUtils.show2(YuLanActivity.this, "入库失败");
//                            mFaceOperationDialog.dismiss();
//                        }
//
//
//                }
//
//
//            }
//        });
//
//        closeIv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mFaceOperationDialog.dismiss();
//            }
//        });
//
//        choosePictureBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intentFromGallery = new Intent(Intent.ACTION_GET_CONTENT);
//                intentFromGallery.setType("image/*"); // 设置文件类型
//                intentFromGallery.addCategory(Intent.CATEGORY_OPENABLE);
//                try {
//                    startActivityForResult(intentFromGallery, REQUEST_CODE_CHOOSE_PICK);
//                } catch (ActivityNotFoundException e) {
//                    toast("请安装相册或者文件管理器");
//                }
//            }
//        });
//
//        addFaceBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//
//
//            }
//        });
//
//        deleteFaceBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                toast("请不要点我");
//              //  Log.d(DEBUG_TAG, "delete face  " + result);
//
//            }
//        });
//
//        bindGroupFaceTokenBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//
//            }
//        });



//
//        WindowManager m = getWindowManager();
//        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
//        mFaceOperationDialog = builder.create();
//        WindowManager.LayoutParams attributes = mFaceOperationDialog.getWindow().getAttributes();
//        attributes.gravity=Gravity.CENTER;
//        attributes.height = d.getHeight();
//        attributes.width = d.getWidth();
//        mFaceOperationDialog.getWindow().setAttributes(attributes);
//        mFaceOperationDialog.show();
//    }




    private void toast(String msg) {
        Toast.makeText(YuLanActivity.this, msg, Toast.LENGTH_SHORT).show();
    }



    /**
     * 获取本地化后的config
     * 注册和比对使用不同的设置
     */
    private void initConfig() {
        //Robin 使用注册的设置
        PaAccessDetectConfig faceDetectConfig = PaAccessControl.getInstance().getPaAccessDetectConfig();
        faceDetectConfig.setMinBrightnessThreshold(30);
        faceDetectConfig.setMaxBrightnessThreshold(240);
        //TODO Robin 注册图片模糊度可以设置0.9f（最大值1.0）这样能让底图更清晰。比对的模糊度可以调低一点，这样能加快识别速度，识别模糊度建议设置0.1f
        faceDetectConfig.setBlurnessThreshold(0.8f);
        faceDetectConfig.setLivenessEnabled(false); //Robin 注册不进行活体检测
        PaAccessControl.getInstance().setPaAccessDetectConfig(faceDetectConfig);
    }


}
