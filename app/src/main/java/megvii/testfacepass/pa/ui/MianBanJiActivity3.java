package megvii.testfacepass.pa.ui;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.serialport.SerialPort;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.badoo.mobile.util.WeakHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.common.pos.api.util.TPS980PosUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hwit.HwitManager;
import com.lztek.toolkit.Lztek;
import com.pingan.ai.access.common.PaAccessControlMessage;
import com.pingan.ai.access.common.PaAccessDetectConfig;
import com.pingan.ai.access.common.PaAccessNativeConfig;
import com.pingan.ai.access.entiry.PaAccessFaceInfo;
import com.pingan.ai.access.entiry.YuvInfo;
import com.pingan.ai.access.impl.OnPaAccessDetectListener;
import com.pingan.ai.access.manager.PaAccessControl;
import com.pingan.ai.access.result.PaAccessCompareFacesResult;
import com.pingan.ai.access.result.PaAccessDetectFaceResult;
import com.pingan.ai.access.result.PaAccessMultiFaceBaseInfo;
import com.sdsmdg.tastytoast.TastyToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import Lib.Reader.MT.Function;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.objectbox.Box;
import megvii.testfacepass.pa.MyApplication;
import megvii.testfacepass.pa.R;
import megvii.testfacepass.pa.beans.BaoCunBean;
import megvii.testfacepass.pa.beans.DaKaBean;
import megvii.testfacepass.pa.beans.HuiFuBean;
import megvii.testfacepass.pa.beans.IDCardBean;
import megvii.testfacepass.pa.beans.IDCardBean_;
import megvii.testfacepass.pa.beans.IDCardTakeBean;
import megvii.testfacepass.pa.beans.Subject;
import megvii.testfacepass.pa.beans.Subject_;
import megvii.testfacepass.pa.beans.XGBean;
import megvii.testfacepass.pa.beans.ZhiLingBean;
import megvii.testfacepass.pa.camera.CameraManager;
import megvii.testfacepass.pa.camera.CameraManager2;
import megvii.testfacepass.pa.camera.CameraPreview;
import megvii.testfacepass.pa.camera.CameraPreview2;
import megvii.testfacepass.pa.camera.CameraPreviewData;
import megvii.testfacepass.pa.camera.CameraPreviewData2;
import megvii.testfacepass.pa.dialog.MiMaDialog3;
import megvii.testfacepass.pa.dialog.MiMaDialog4;
import megvii.testfacepass.pa.tuisong_jg.MyServeInterface;
import megvii.testfacepass.pa.tuisong_jg.ServerManager;
import megvii.testfacepass.pa.utils.AppUtils;
import megvii.testfacepass.pa.utils.BitmapUtil;
import megvii.testfacepass.pa.utils.DateUtils;
import megvii.testfacepass.pa.utils.DengUT;
import megvii.testfacepass.pa.utils.FileUtil;
import megvii.testfacepass.pa.utils.GetDeviceId;
import megvii.testfacepass.pa.utils.GsonUtil;
import megvii.testfacepass.pa.utils.NV21ToBitmap;
import megvii.testfacepass.pa.utils.SettingVar;
import megvii.testfacepass.pa.utils.fx.FxTool;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class MianBanJiActivity3 extends Activity implements CameraManager.CameraListener,
        CameraManager2.CameraListener2, MyServeInterface, SensorEventListener {

    @BindView(R.id.xiping)
    ImageView xiping;
    @BindView(R.id.tishiyu)
    TextView tishiyu;
    @BindView(R.id.root_layout)
    RelativeLayout rootLayout;
    @BindView(R.id.logo)
    ImageView logo;

    private NetWorkStateReceiver netWorkStateReceiver = null;
    private SensorManager sm;
    private Box<Subject> subjectBox = null;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private Box<IDCardBean> idCardBeanBox = MyApplication.myApplication.getIdCardBeanBox();
    private static ServerManager serverManager;
    private Bitmap msrBitmap = null;
    //    private RequestOptions myOptions = new RequestOptions()
//            .fitCenter()
//            .error(R.drawable.erroy_bg)
//            .transform(new GlideCircleTransform(MyApplication.myApplication, 2, Color.parseColor("#ffffffff")));
//    // .transform(new GlideRoundTransform(MainActivity.this,10));
//
//    private RequestOptions myOptions2 = new RequestOptions()
//            .fitCenter()
//            .error(R.drawable.erroy_bg)
//            //   .transform(new GlideCircleTransform(MyApplication.myApplication, 2, Color.parseColor("#ffffffff")));
//            .transform(new GlideCircleTransform270(MyApplication.myApplication, 2, Color.parseColor("#ffffffff"), 270));
    private String serialnumber = GetDeviceId.getDeviceId(MyApplication.myApplication);

    private OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .writeTimeout(20000, TimeUnit.MILLISECONDS)
            .connectTimeout(20000, TimeUnit.MILLISECONDS)
            .readTimeout(20000, TimeUnit.MILLISECONDS)
//				    .cookieJar(new CookiesManager())
            //        .retryOnConnectionFailure(true)
            .build();
    private final Timer timer = new Timer();
    private TimerTask task;
    private final Timer timer2 = new Timer();
    private TimerTask task2;
    private LinkedBlockingQueue<ZhiLingBean.ResultBean> linkedBlockingQueue;
    /* 相机实例 */
    private CameraManager manager;
    private CameraManager2 manager2;
    /* 显示人脸位置角度信息 */
    // private XiuGaiGaoKuanDialog dialog = null;
    /* 相机预览界面 */
    private CameraPreview cameraView;
    private CameraPreview2 cameraView2;
    private static final int cameraWidth = 720;
    private static final int cameraHeight = 640;
    private boolean isOP = true;
    private int heightPixels;
    private int widthPixels;
    int screenState = 0;// 0 横 1 竖
    // private ConcurrentHashMap<Long, Integer> concurrentHashMap = new ConcurrentHashMap<Long, Integer>();
    private int dw, dh;
    private Box<BaoCunBean> baoCunBeanDao = null;
    private Box<HuiFuBean> huiFuBeanBox = null;
    private Box<DaKaBean> daKaBeanBox = null;
    private BaoCunBean baoCunBean = null;
    private TimeChangeReceiver timeChangeReceiver;
    private Handler mHandler;
    private static boolean isLink = true;
    private PaAccessControl paAccessControl;
    private Float mCompareThres;
    private static String faceId = "";
    private long feature2 = -1;
    private NV21ToBitmap nv21ToBitmap;
    private SoundPool soundPool;
    //定义一个HashMap用于存放音频流的ID
    private HashMap<Integer, Integer> musicId = new HashMap<>();
    private int pp = 0;
    private ReadThread mReadThread;
    private InputStream mInputStream;
    private int w, h, cameraH, cameraW;
    private float s1 = 0, s2 = 0;
    private Timer mTimer;//距离感应
    private TimerTask mTimerTask;//距离感应
    private int pm = 0;
    private boolean onP1 = true, onP2 = true;
    private boolean isPM = true;
    private boolean isPM2 = true;
    private float juli = 0;
    private String JHM = null;
    TextView tvTitle_Ir;
    TextView tvName_Ir;//识别结果弹出信息的名字
    TextView tvTime_Ir;//识别结果弹出信息的时间
    TextView tvFaceTips_Ir;//识别信息提示
    LinearLayout layout_loadbg_Ir;//识别提示大框
    RelativeLayout layout_true_gif_Ir, layout_error_gif_Ir;//蓝色图片动画 红色图片动画
    ImageView iv_true_gif_in_Ir, iv_true_gif_out_Ir, iv_error_gif_in_Ir, iv_error_gif_out_Ir;//定义旋转的动画
    Animation gifClockwise, gifAntiClockwise;
    LinearInterpolator lir_gif;
    private Box<IDCardTakeBean> idCardTakeBeanBox=MyApplication.myApplication.getIdCardTakeBeanBox();
    private int jiqiType=-1;
    private boolean isGET = true;
    private PaAccessDetectConfig paAccessDetectConfig;
    private int cishu=30;
    private int jidianqi=5000;
    private Lztek lztek=null;
    private Function mFuncs = null;
    private int loc_readerHandle=-1;
    private boolean isCLOSDLED =false;
    private  Subject subjectOnly=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        huiFuBeanBox = MyApplication.myApplication.getHuiFuBeanBox();
        baoCunBeanDao = MyApplication.myApplication.getBaoCunBeanBox();
        daKaBeanBox=MyApplication.myApplication.getDaKaBeanBox();
        baoCunBean = baoCunBeanDao.get(123456L);
        JHM = baoCunBean.getJihuoma();
        if (JHM == null)
            JHM = "";
        subjectBox = MyApplication.myApplication.getSubjectBox();
        mCompareThres = baoCunBean.getShibieFaZhi();
        if (baoCunBean.getDangqianChengShi2()!=null){
            switch (baoCunBean.getDangqianChengShi2()){
                case "天波":
                    jiqiType=0;
                    break;
                case "涂鸦":
                    jiqiType=1;
                    break;
                case "户外防水8寸屏":
                    jiqiType=2;
                    break;
                case "高通8寸屏":
                    jiqiType=3;
                    break;
            }
        }
        try {
            mFuncs = new Function(this, mHandler);
            loc_readerHandle = mFuncs.lc_init_ex(1, "/dev/ttyS1".toCharArray(), Integer.parseInt("9600"));
            if(loc_readerHandle == -1)
            {
                Toast.makeText(getApplicationContext(), "连接读卡器失败", Toast.LENGTH_SHORT).show();
            }
            else
            {
                int aa= mFuncs.lc_beep(loc_readerHandle, 2);
                Log.d("MianBanJiActivity3", loc_readerHandle+"   "+ aa);
            }
        }catch (NoClassDefFoundError error){
            Log.d("MianBanJiActivity3", error.getMessage()+"");
        }

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true){
//                    int st = 0;
//                    byte[] rData = new byte[128];
//                    int[] rlen = new int[2];
//                    if (loc_readerHandle!=-1){
//                        Log.d("MianBanJiActivity3", "ffffffff:"+loc_readerHandle);
//                        st = mFuncs.lc_getAutoReturnedData(loc_readerHandle, rData, rlen);
//                        Log.d("MianBanJiActivity3", "hh哈哈:"+st);
//                        if (st == 0)
//                        {
//                            String showStr="";
//                            int len=rlen[0];
//                            for(int i= 0; i<len; i++)
//                                showStr = showStr + " " + byteToHexString(rData[i]);
//                            Log.d("MianBanJiActivity3", showStr);
//                            String sdfds = byteToString(rData);
//                            Log.d("MianBanJiActivity3", sdfds);
//
//                        }
//                    }
//                }
//
//            }
//        }).start();


        //每分钟的广播
        // private TodayBean todayBean = null;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);//每分钟变化
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);//设置了系统时区
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);//设置了系统时间
        timeChangeReceiver = new TimeChangeReceiver();
        registerReceiver(timeChangeReceiver, intentFilter);
        linkedBlockingQueue = new LinkedBlockingQueue<>();
        EventBus.getDefault().register(this);//订阅
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        dw = dm.widthPixels;
        dh = dm.heightPixels;
        nv21ToBitmap = new NV21ToBitmap(MianBanJiActivity3.this);
        /* 初始化界面 */
        //  Log.d("MianBanJiActivity3", "jh:" + baoCunBean);
        //初始化soundPool,设置可容纳12个音频流，音频流的质量为5，
        AudioAttributes abs = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)   //设置允许同时播放的流的最大值
                .setAudioAttributes(abs)   //完全可以设置为null
                .build();
        //通过load方法加载指定音频流，并将返回的音频ID放入musicId中
        musicId.put(1, soundPool.load(this, R.raw.tongguo, 1));
        musicId.put(2, soundPool.load(this, R.raw.wuquanxian, 1));
        musicId.put(3, soundPool.load(this, R.raw.xinxibupipei, 1));
        musicId.put(4, soundPool.load(this, R.raw.xianshibie, 1));
        musicId.put(5, soundPool.load(this, R.raw.shuaka, 1));
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        try {
            lztek=Lztek.create(MyApplication.myApplication);
            lztek.gpioEnable(218);
            lztek.setGpioOutputMode(218);
        }catch (NoClassDefFoundError error){
            error.printStackTrace();
        }


        initView();

        if (baoCunBean != null) {
            try {
                if (baoCunBean.getJidianqi()!=0){
                    jidianqi=baoCunBean.getJidianqi();
                }
                if (baoCunBean.getMoshengrenPanDing()!=0){
                    cishu=baoCunBean.getMoshengrenPanDing();
                }
                //PaAccessControl.getInstance().getPaAccessDetectConfig();
                paAccessControl = PaAccessControl.getInstance();
                paAccessControl.setOnPaAccessDetectListener(onDetectListener);
                paAccessControl.setLogEnable(false);
                initFaceConfig();
                paAccessDetectConfig=paAccessControl.getPaAccessDetectConfig();
            } catch (Exception e) {
                TastyToast.makeText(MianBanJiActivity3.this,"初始化失败"+e.getMessage(),TastyToast.LENGTH_LONG,TastyToast.ERROR).show();
                Log.d("MianBanJiActivity3", "初始化失败" + e.getMessage());
                //   return;
            }
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(4000);
                w = cameraView.getMeasuredWidth();
                h = cameraView.getMeasuredHeight();
                cameraH = manager.getCameraheight();
                cameraW = manager.getCameraWidth();
                s1 = (float) w / (float) cameraH;
                s2 = (float) h / (float) cameraW;
                if (paAccessControl != null)
                    paAccessControl.startFrameDetect();
            }
        }).start();

        try {
            SerialPort mSerialPort = MyApplication.myApplication.getSerialPort();
            //mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

        } catch (Exception e) {
            Log.d("MianBanJiActivity", e.getMessage() + "dddddddd");
        }

        mReadThread = new ReadThread();
        mReadThread.start();



        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NotNull Message msg) {
                switch (msg.what) {
                    case 111: {
                        Subject subject = (Subject) msg.obj;
                        //Log.d("MianBanJiActivity3", "subject:" + subject);
                        if (subject.getTeZhengMa() != null) {
                            //  Log.d("MianBanJiActivity3", "ddd3");
                            //   faceView.setTC(BitmapFactory.decodeFile(MyApplication.SDPATH3 + File.separator + subject.getTeZhengMa() + ".png")
                            //        , subject.getName(), subject.getDepartmentName());
                            soundPool.play(musicId.get(1), 1, 1, 0, 0, 1);
                            DengUT.getInstance(baoCunBean).openDool();


                            //启动定时器或重置定时器
                            if (task != null) {
                                task.cancel();
                                //timer.cancel();
                                task = new TimerTask() {
                                    @Override
                                    public void run() {
                                        Message message = new Message();
                                        message.what = 222;
                                        mHandler.sendMessage(message);
                                    }
                                };
                                timer.schedule(task, jidianqi);
                            } else {
                                task = new TimerTask() {
                                    @Override
                                    public void run() {
                                        Message message = new Message();
                                        message.what = 222;
                                        mHandler.sendMessage(message);
                                    }
                                };
                                timer.schedule(task, jidianqi);
                            }
                        } else {
                            //  Log.d("MianBanJiActivity3", "ddd4");
                            soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
                            //faceView.setTC(BitmapUtil.rotateBitmap(msrBitmap, SettingVar.msrBitmapRotation), subject.getName(), subject.getDepartmentName());
                        }

                        break;
                    }
                    case 222: {//关门
                        DengUT.getInstance(baoCunBean).closeDool();
                        break;
                    }
                    case 333:
                        onP1 = true;
                        onP2 = true;
                        if (paAccessControl != null)
                            paAccessControl.startFrameDetect();
                        DengUT.getInstance(baoCunBean).openLOED();

                        break;
                    case 444:
                        onP1 = false;
                        onP2 = false;

                        if (paAccessControl != null)
                            paAccessControl.stopFrameDetect();
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                xiping.setVisibility(View.VISIBLE);
//                                tishiyu.setVisibility(View.VISIBLE);
//                            }
//                        });
//                        anim = ValueAnimator.ofFloat(0, 1.0f);
//                        anim.setDuration(4000);
//                        anim.setRepeatMode(ValueAnimator.REVERSE);
//                        anim.setRepeatCount(-1);
//                        Interpolator interpolator = new LinearInterpolator();
//                        anim.setInterpolator(interpolator);
//                        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                            @Override
//                            public void onAnimationUpdate(ValueAnimator animation) {
//                                float currentValue = (Float) animation.getAnimatedValue();
//                                tishiyu.setAlpha(currentValue);
//                                // 步骤5：刷新视图，即重新绘制，从而实现动画效果
//                                tishiyu.requestLayout();
//                            }
//                        });
//                        anim.start();
                        DengUT.getInstance(baoCunBean).closeLOED();
                        break;

                }
                return false;
            }
        });
        NfcManager mNfcManager = (NfcManager) getSystemService(Context.NFC_SERVICE);
        mNfcAdapter = mNfcManager.getDefaultAdapter();
        if (mNfcAdapter == null) {
            Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "设备不支持NFC", TastyToast.LENGTH_LONG, TastyToast.INFO);
            tastyToast.setGravity(Gravity.CENTER, 0, 0);
            tastyToast.show();
        } else if (!mNfcAdapter.isEnabled()) {
            Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "请先去设置里面打开NFC开关", TastyToast.LENGTH_LONG, TastyToast.INFO);
            tastyToast.setGravity(Gravity.CENTER, 0, 0);
            tastyToast.show();
        } else if ((mNfcAdapter != null) && (mNfcAdapter.isEnabled())) {

        }
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
        init_NFC();


        manager.open(getWindowManager(), SettingVar.cameraId, cameraWidth, cameraHeight);//前置是1
        if (baoCunBean.isHuoTi()) {
            if (SettingVar.cameraId == 1) {
                manager2.open(getWindowManager(), 0, cameraWidth, cameraHeight, SettingVar.cameraPreviewRotation2);//最后一个参数是红外预览方向
            } else {
                manager2.open(getWindowManager(), 1, cameraWidth, cameraHeight, SettingVar.cameraPreviewRotation2);//最后一个参数是红外预览方向
            }
        }
        guanPing();//关屏


    }


    private String  byteToHexString(byte mByte)
    {
        String hexStr;

        hexStr = Integer.toHexString(mByte & 0xff);
        if(hexStr.length() == 1)
            hexStr = '0'+ hexStr;

        return hexStr;
    }

    @OnClick(R.id.root_layout)
    public void onViewClicked() {
        if (baoCunBean.isShowShiPingLiu()){
            MiMaDialog3 miMaDialog=new MiMaDialog3(MianBanJiActivity3.this,baoCunBean.getMima2());
            WindowManager.LayoutParams params= miMaDialog.getWindow().getAttributes();
            params.width=dw;
            params.height=dh+60;
            miMaDialog.getWindow().setGravity(Gravity.CENTER);
            miMaDialog.getWindow().setAttributes(params);
            miMaDialog.show();
        }
    }



    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                try {
                    final byte[] buffer = new byte[64];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        // Log.d("ReadThread", "buffer.length:" + byteToString(buffer));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                readdd(buffer);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }


    private void readdd(byte[] idid) {
        String sdfds = byteToString(idid);
        if (sdfds != null) {
            sdfds = sdfds.substring(6, 14);
        } else {
            return;
        }
        sdfds = sdfds.toUpperCase();
        Log.d("MianBanJiActivity3", sdfds);
        List<IDCardBean> idCardBeanList = idCardBeanBox.query().equal(IDCardBean_.idCard, sdfds).build().find();
        if (idCardBeanList.size() > 0) {
            Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "验证成功!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
            tastyToast.setGravity(Gravity.CENTER, 0, 0);
            tastyToast.show();
            soundPool.play(musicId.get(1), 1, 1, 0, 0, 1);
            DengUT.getInstance(baoCunBean).openDool();
            IDCardBean cardBean=idCardBeanList.get(0);
            link_shuaka(sdfds,cardBean.getName());
            //启动定时器或重置定时器
            if (task != null) {
                task.cancel();
                //timer.cancel();
                task = new TimerTask() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 222;
                        mHandler.sendMessage(message);
                    }
                };
                timer.schedule(task, 6000);
            } else {
                task = new TimerTask() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = 222;
                        mHandler.sendMessage(message);
                    }
                };
                timer.schedule(task, 6000);
            }

            IDCardTakeBean takeBean=new IDCardTakeBean();
            takeBean.setIdCard(sdfds);
            takeBean.setName(cardBean.getName());
            takeBean.setTime(System.currentTimeMillis());
            idCardTakeBeanBox.put(takeBean);

        } else {
            Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "验证失败!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
            tastyToast.setGravity(Gravity.CENTER, 0, 0);
            tastyToast.show();
            soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
        }
    }


    @Override
    protected void onResume() {
        Log.d("MianBanJiActivity3", "重新开始");

        if (serverManager != null) {
            serverManager.stopServer();
            serverManager = null;
        }
        Log.d("MianBanJiActivity3", System.currentTimeMillis()+"");
        serverManager = new ServerManager(FileUtil.getIPAddress(getApplicationContext()), baoCunBean.getPort());
        serverManager.setMyServeInterface(MianBanJiActivity3.this);
        serverManager.startServer();

        super.onResume();
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
            if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(this.getIntent().getAction())) {
                processIntent(this.getIntent());
            }
        }
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(netWorkStateReceiver, filter);
        }

        if (baoCunBean.getDangqianChengShi2() != null && baoCunBean.getDangqianChengShi2().equals("天波")) {
            if (mTimerTask == null) {
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        int ret = TPS980PosUtil.getPriximitySensorStatus();
                        if (ret == 1) {
                            isPM2 = true;
                            //有人
                            if (isPM) {
                                isPM = false;
                                onP1 = true;
                                onP2 = true;
                                pm = 0;
                                if (paAccessControl != null)
                                    paAccessControl.startFrameDetect();
                                Message message = new Message();
                                message.what = 333;
                                mHandler.sendMessage(message);
                            }
                        } else {
                            isPM = true;
                            if (isPM2) {
                                pm++;
                                if (pm == 8) {
                                    if (paAccessControl != null)
                                        paAccessControl.stopFrameDetect();
                                    Message message = new Message();
                                    message.what = 444;
                                    mHandler.sendMessage(message);
                                    isPM2 = false;
                                    onP1 = false;
                                    onP2 = false;
                                    pm = 0;
                                }
                            }
                        }
                    }
                };
            }
            if (mTimer == null) {
                mTimer = new Timer();
            }
            mTimer.schedule(mTimerTask, 0, 1000);
        }
        if (baoCunBean.getDangqianChengShi2() != null && baoCunBean.getDangqianChengShi2().equals("涂鸦") && paAccessControl != null) {
            Sensor defaultSensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            sm.registerListener(this, defaultSensor, SensorManager.SENSOR_DELAY_NORMAL);
            if (mTimerTask == null) {
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (juli > 0) {
                            isPM2 = true;
                            //有人
                            if (isPM) {
                                isPM = false;
                                onP1 = true;
                                onP2 = true;
                                pm = 0;
                                if (paAccessControl != null)
                                    paAccessControl.startFrameDetect();
                                Message message = new Message();
                                message.what = 333;
                                mHandler.sendMessage(message);
                            }
                        } else {
                            isPM = true;
                            if (isPM2) {
                                pm++;
                                if (pm == 8) {
                                    if (paAccessControl != null)
                                        paAccessControl.stopFrameDetect();
                                    Message message = new Message();
                                    message.what = 444;
                                    mHandler.sendMessage(message);
                                    isPM2 = false;
                                    onP1 = false;
                                    onP2 = false;
                                    pm = 0;

                                    if (DengUT.isOPENRed) {
                                        DengUT.isOPENRed = false;
                                        DengUT.getInstance(baoCunBean).closeRed();
                                    }
                                    if (DengUT.isOPENGreen) {
                                        DengUT.isOPENGreen = false;
                                        DengUT.getInstance(baoCunBean).closeGreen();
                                    }
                                    if (DengUT.isOPEN) {
                                        DengUT.isOPEN = false;
                                        DengUT.getInstance(baoCunBean).closeWrite();
                                    }
                                }
                            }
                        }
                    }
                };
            }
            if (mTimer == null) {
                mTimer = new Timer();
            }
            mTimer.schedule(mTimerTask, 0, 1000);
        }

        if (baoCunBean.getDangqianChengShi2() != null && baoCunBean.getDangqianChengShi2().equals("户外防水8寸屏") && paAccessControl != null) {
            if (lztek!=null){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (true) {
                            SystemClock.sleep(180);
                            final int value = lztek.getGpioValue(218);
                            if (value==1){
                                isCLOSDLED=true;
                                Message message = new Message();
                                message.what = 333;
                                mHandler.sendMessage(message);
                            }
//                            int st = 0;
//                            byte[] rData = new byte[128];
//                            int[] rlen = new int[2];
//                            if (loc_readerHandle!=-1){
//                                Log.d("MianBanJiActivity3", "ffffffff:"+loc_readerHandle);
//                                st = mFuncs.lc_getAutoReturnedData(-1, rData, rlen);
//                                Log.d("MianBanJiActivity3", "ffffffff:"+st);
//                                if (st == 0)
//                                {
//                                    Log.d("MianBanJiActivity3", new String(rData));
//
//                                }
//                            }
                        }
                    }
                }).start();
            }
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.e("距离", "" + event.values[0]);
        juli = event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Override
    public void onNewIntent(Intent intent) {
        //  super.onNewIntent(intent);
        // Log.d("SheZhiActivity2", "intent:" + intent);
        processIntent(intent);
    }


    private YuvInfo rgb, ir;

    /* 相机回调函数 */
    @Override
    public void onPictureTaken(CameraPreviewData cameraPreviewData) {
        /* 如果SDK实例还未创建，则跳过 */
        if (paAccessControl == null && onP1) {
            return;
        }

        rgb = new YuvInfo(cameraPreviewData.nv21Data, SettingVar.cameraId, SettingVar.faceRotation, cameraPreviewData.width, cameraPreviewData.height);
        if (!baoCunBean.isHuoTi()) {
            //  Log.d("MianBanJiActivity3", "进来");
            paAccessControl.offerFrameBuffer(rgb);
        }
    }


    /* 相机回调函数 */
    @Override
    public void onPictureTaken2(CameraPreviewData2 cameraPreviewData) {
        /* 如果SDK实例还未创建，则跳过 */
        // Log.d("MianBanJiActivity3", "cameraPreviewData2.rotation:" + cameraPreviewData.front);
        if (paAccessControl == null && onP2) {
            return;
        }
        //  paAccessControl.offerFrameBuffer(cameraPreviewData.nv21Data, cameraPreviewData.width, cameraPreviewData.height,SettingVar.faceRotation, SettingVar.getCaneraID());
        try {
            ir = new YuvInfo(cameraPreviewData.nv21Data, cameraPreviewData.front, SettingVar.faceRotation2, cameraPreviewData.width, cameraPreviewData.height);
            if (rgb == null || !baoCunBean.isHuoTi())
                return;
            int result = paAccessControl.offerIrFrameBuffer(rgb, ir);//提供数据到队列
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Log.d("MianBanJiActivity3", "cameraPreviewData.result:" + result);
        /* 将相机预览帧转成SDK算法所需帧的格式 FacePassImage */
    }


    @Override
    public void onStarted(String ip) {
        Log.d("MianBanJiActivity3", "小服务器启动" + ip);
    }


    @Override
    public void onStopped() {
        Log.d("MianBanJiActivity3", "小服务器停止");
    }

    @Override
    public void onException(Exception e) {
        Log.d("MianBanJiActivity3", "小服务器异常" + e);
    }




    @Override
    protected void onPause() {
        super.onPause();
        Log.d("MianBanJiActivity3", "暂停");
        if (mNfcAdapter != null) {
            stopNFC_Listener();
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        if (sm != null)
            sm.unregisterListener(this);

    }


    private void initView() {
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

        setContentView(R.layout.activity_mianbanji3);

        ButterKnife.bind(this);

        ImageView shezhi = findViewById(R.id.shezhi);
        shezhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MiMaDialog4 miMaDialog = new MiMaDialog4(MianBanJiActivity3.this, baoCunBean.getMima());
                WindowManager.LayoutParams params = miMaDialog.getWindow().getAttributes();
                params.width = dw;
                params.height = dh;
//                miMaDialog.getWindow().setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                miMaDialog.getWindow().setAttributes(params);
                miMaDialog.show();
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        heightPixels = displayMetrics.heightPixels;
        widthPixels = displayMetrics.widthPixels;
        SettingVar.mHeight = heightPixels;
        SettingVar.mWidth = widthPixels;
        /* 初始化界面 */
        manager = new CameraManager();
        cameraView = (CameraPreview) findViewById(R.id.preview);
        manager.setPreviewDisplay(cameraView);
        /* 注册相机回调函数 */
        manager.setListener(this);

        manager2 = new CameraManager2();
        cameraView2 = findViewById(R.id.preview2);
        manager2.setPreviewDisplay(cameraView2);
        /* 注册相机回调函数 */
        manager2.setListener(this);

        tvName_Ir = findViewById(R.id.tvName_Ir);//名字
        tvTime_Ir = findViewById(R.id.tvTime_Ir);//时间
        tvFaceTips_Ir = findViewById(R.id.tvFaceTips_Ir);//识别信息提示
        layout_loadbg_Ir = findViewById(R.id.layout_loadbg_Ir);//头像区域的显示的底图背景

        layout_true_gif_Ir = findViewById(R.id.layout_true_gif_Ir);
        layout_error_gif_Ir = findViewById(R.id.layout_error_gif_Ir);
        iv_true_gif_in_Ir = findViewById(R.id.iv_true_gif_in_Ir);
        iv_true_gif_out_Ir = findViewById(R.id.iv_true_gif_out_Ir);
        iv_error_gif_in_Ir = findViewById(R.id.iv_error_gif_in_Ir);
        iv_error_gif_out_Ir = findViewById(R.id.iv_error_gif_out_Ir);
        tvTitle_Ir = findViewById(R.id.tvTitle_Ir);

        //region 动画
        gifClockwise = AnimationUtils.loadAnimation(this, R.anim.rotate_anim_face_clockwise);
        gifAntiClockwise = AnimationUtils.loadAnimation(this, R.anim.rotate_anim_face_anti_clockwise);
        lir_gif = new LinearInterpolator();//设置为匀速旋转
        gifClockwise.setInterpolator(lir_gif);
        gifAntiClockwise.setInterpolator(lir_gif);

        iv_true_gif_out_Ir.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        iv_error_gif_out_Ir.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        iv_true_gif_out_Ir.startAnimation(gifClockwise);
        iv_error_gif_out_Ir.startAnimation(gifClockwise);
        AssetManager mgr = getAssets();
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/hua.ttf");
        tvTitle_Ir.setTypeface(tf);
        if (baoCunBean.getWenzi1() == null) {
            tvTitle_Ir.setText("请设置公司名称");
        } else {
            tvTitle_Ir.setText(baoCunBean.getWenzi1());
        }
        if (baoCunBean.getLogo()!=null){
            try {
                logo.setImageBitmap(BitmapUtil.base64ToBitmap(baoCunBean.getLogo()));
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        showUIResult(1,"","");

    }


    OnPaAccessDetectListener onDetectListener = new OnPaAccessDetectListener() {
        //每一帧数据的回调
        @Override
        public void onFaceDetectFrame(int message, PaAccessDetectFaceResult faceDetectFrame) {

            if (message == 1001) {//没人脸
                faceId = "";
                feature2 = -1;
                subjectOnly=null;
                //  tishi.setVisibility(View.GONE);
                if (DengUT.isOPEN || DengUT.isOPENRed || DengUT.isOPENGreen) {
                    DengUT.isOPEN = false;
                    DengUT.isOPENGreen = false;
                    DengUT.isOPENRed = false;
                    DengUT.isOpenDOR = false;

                    DengUT.getInstance(baoCunBean).closeWrite();

                    //启动定时器或重置定时器
                    if (task2 != null) {
                        task2.cancel();
                        //timer.cancel();
                        task2 = new TimerTask() {
                            @Override
                            public void run() {
                                if (isCLOSDLED){
                                    Message message = new Message();
                                    message.what = 444;
                                    mHandler.sendMessage(message);
                                }
                            }
                        };
                        timer2.schedule(task2, 5000);
                    } else {
                        task2 = new TimerTask() {
                            @Override
                            public void run() {
                                if (isCLOSDLED){
                                    Message message = new Message();
                                    message.what = 444;
                                    mHandler.sendMessage(message);
                                }
                            }
                        };
                        timer2.schedule(task2, 5000);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showUIResult(1,"","");
                        }
                    });
                }

            }else if (message==1002){//检测到了人脸
                //启动定时器或重置定时器
                if (task2 != null) {
                    task2.cancel();
                    //   DengUT.isOPEN = true;
                }
            }

        }

        @Override
        public void onFaceDetectResult(int var1, PaAccessDetectFaceResult detectResult) {
            // Log.d("Robin","detectResult : " + detectResult.facePassFrame.blurness);
            if (detectResult == null)
                return;
            // Log.d("MianBanJiActivity3", "detectResult.feature:" + detectResult.feature);
            PaAccessCompareFacesResult paFacePassCompareResult = paAccessControl.compareFaceToAll(detectResult.feature);
            if (paFacePassCompareResult == null || paFacePassCompareResult.message != PaAccessControlMessage.RESULT_OK) {
                Log.d("MianBanJiActivity", "没有人脸信息");
                return;
            }
            if (!DengUT.isOPEN) {
                DengUT.isOPEN = true;

                DengUT.getInstance(baoCunBean).openWrite();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showUIResult(2,"","");
                    }
                });
            }
            //人脸信息完整的
            String id = paFacePassCompareResult.id;

            //百分之一误识为0.52；千分之一误识为0.56；万分之一误识为0.60 比对阈值可根据实际情况调整
            if (paFacePassCompareResult.compareScore > mCompareThres) {
             //   feature2 = detectResult.trackId;
                // 不相等 弹窗
                if (subjectOnly==null) {
                    Subject subject=null;
                    try {
                        subject = subjectBox.query().equal(Subject_.faceIds1, id).build().findUnique();
                        if (subject==null){
                            subject = subjectBox.query().equal(Subject_.faceIds2, id).build().findUnique();
                        }
                        if (subject==null){
                            subject = subjectBox.query().equal(Subject_.faceIds3, id).build().findUnique();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if (subject != null) {
                        subjectOnly=subject;
                        if (subject.getEntryTime()==0 || subject.getEntryTime()>=System.currentTimeMillis()){//没过期
                            Message message2 = Message.obtain();
                            message2.what = 111;
                            message2.obj = subject;
                            mHandler.sendMessage(message2);

                            if (!DengUT.isOPENGreen) {
                                DengUT.isOPENGreen = true;
                                DengUT.getInstance(baoCunBean).openGreen();
                            }
                            DengUT.isOPEN = true;
                            if (subject.getEntryTime()==0){
                                showUIResult(4,subject.getName(),"过期时间:永久");
                            }else {
                                showUIResult(4,subject.getName(),"过期时间:"+DateUtils.time(subject.getEntryTime()+""));
                            }
                            msrBitmap = nv21ToBitmap.nv21ToBitmap(detectResult.rgbFrame, detectResult.frameWidth, detectResult.frameHeight);
                            long bitmapId=System.currentTimeMillis();
                            BitmapUtil.saveBitmapToSD(msrBitmap,MyApplication.SDPATH2,bitmapId+".png");
                            link_shangchuanshualian(subject.getTeZhengMa(), msrBitmap, "face_0",bitmapId);

                        }else {
                            if (!DengUT.isOPENRed) {
                                DengUT.isOPENRed = true;
                                DengUT.getInstance(baoCunBean).openRed();
                            }
                            DengUT.isOPEN = true;
                            showUIResult(4,subject.getName(),"你的出入时间已过期");
                            msrBitmap = nv21ToBitmap.nv21ToBitmap(detectResult.rgbFrame, detectResult.frameWidth, detectResult.frameHeight);
                            long bitmapId=System.currentTimeMillis();
                            BitmapUtil.saveBitmapToSD(msrBitmap,MyApplication.SDPATH2,bitmapId+".png");
                            link_shangchuanshualian(subject.getTeZhengMa(), msrBitmap,  "face_1",bitmapId);
                        }
                    } else {
                        EventBus.getDefault().post("没有查询到人员信息,或者存在多条人员信息");
                    }
                }

            } else {
                //陌生人
                //  Log.d("MianBanJiActivity3", "陌生人"+id);
//                if (isShow)
//                tishi.setVisibility(View.VISIBLE)
                pp++;
                if (pp > cishu) {
                    faceId = "";
                    pp = 0;
                    if (feature2 == -1) {
                        feature2 = detectResult.trackId;
                        msrBitmap = nv21ToBitmap.nv21ToBitmap(detectResult.rgbFrame, detectResult.frameWidth, detectResult.frameHeight);
                        long bitmapId=System.currentTimeMillis();
                        BitmapUtil.saveBitmapToSD(msrBitmap,MyApplication.SDPATH2,bitmapId+".png");
                        link_shangchuanshualian("-1", msrBitmap,  "msl",bitmapId);
                        if (!baoCunBean.isMsrPanDing()){
                            return;
                        }
                        Subject subject1 = new Subject();
                        //图片在bitmabToBytes方法里面做了循转
                        // subject1.setTxBytes(BitmapUtil.bitmabToBytes(bitmap));
                        subject1.setId(System.currentTimeMillis());
                        subject1.setName("陌生人");
                        subject1.setTeZhengMa(null);
                        subject1.setPeopleType("3");
                        subject1.setDepartmentName("暂无进入权限!");
                        //linkedBlockingQueue.offer(subject1);
                        Message message2 = Message.obtain();
                        message2.what = 111;
                        message2.obj = subject1;
                        mHandler.sendMessage(message2);
                        DengUT.isOPEN = true;

                        if (!DengUT.isOPENRed) {
                            DengUT.isOPENRed = true;
                            DengUT.getInstance(baoCunBean).openRed();
                        }

                        showUIResult(3,"陌生人","");
                    } else if (feature2 != detectResult.trackId) {
                        faceId = "";
                        msrBitmap = nv21ToBitmap.nv21ToBitmap(detectResult.rgbFrame, detectResult.frameWidth, detectResult.frameHeight);
                        // Bitmap bitmap = BitmapUtil.getBitmap(facePassFrame.frame, facePassFrame.frmaeWidth, facePassFrame.frameHeight, facePassFrame.frameOri);
                        //  bitmap = BitmapUtil.getCropBitmap(bitmap, facePassFrame.rectX, facePassFrame.rectY, facePassFrame.rectW, facePassFrame.rectH);
                        long bitmapId=System.currentTimeMillis();
                        BitmapUtil.saveBitmapToSD(msrBitmap,MyApplication.SDPATH2,bitmapId+".png");
                        link_shangchuanshualian("-1", msrBitmap,  "msl",bitmapId);

                        if (!baoCunBean.isMsrPanDing()){
                            return;
                        }
                        Subject subject1 = new Subject();
                        // subject1.setW(bitmap.getWidth());
                        // subject1.setH(bitmap.getHeight());
                        //图片在bitmabToBytes方法里面做了循转
                        // subject1.setTxBytes(BitmapUtil.bitmabToBytes(bitmap));
                        subject1.setId(System.currentTimeMillis());
                        subject1.setName("陌生人");
                        subject1.setTeZhengMa(null);
                        subject1.setPeopleType("3");
                        subject1.setDepartmentName("暂无进入权限!");
                        // linkedBlockingQueue.offer(subject1);
                        Message message2 = Message.obtain();
                        message2.what = 111;
                        message2.obj = subject1;
                        mHandler.sendMessage(message2);
                        showUIResult(3,"陌生人","");

                        if (!DengUT.isOPENRed) {
                            DengUT.isOPENRed = true;
                            DengUT.getInstance(baoCunBean).openRed();
                        }
                        DengUT.isOPEN = true;
                    }
                }
            }
        }

        @Override
        public void onMultiFacesDetectFrameBaseInfo(int i, List<PaAccessMultiFaceBaseInfo> list) {

            //  Log.d("MianBanJiActivity3", "list.size():" + list.size());

        }
    };


    private void showFacePassFace(PaAccessDetectFaceResult detectResult) {
        //  faceView2.clear();
//        Log.d("MianBanJiActivity333", "detectResult.frameWidth:" + detectResult.frameWidth);
//        Log.d("MianBanJiActivity333", "detectResult.frameHeight:" + detectResult.frameHeight);
//                Log.d("MianBanJiActivity333", "widthPixels:" + widthPixels);
//                Log.d("MianBanJiActivity333", "heightPixels:" + heightPixels);
//                float s1 = (float) widthPixels / (float)detectResult.frameWidth;
//                float s2 = (float) heightPixels / (float)detectResult.frameHeight;

        //  Log.d("facefacelist", "width " + (face.rect.right - face.rect.left) + " height " + (face.rect.bottom - face.rect.top) );
        //  Log.d("facefacelist", "smile " + face.smile);

//            StringBuilder faceIdString = new StringBuilder();
//            faceIdString.append("ID = ").append(face.trackId);
//            StringBuilder faceRollString = new StringBuilder();
//            faceRollString.append("旋转: ").append((int) face.pose.roll).append("°");
//            StringBuilder facePitchString = new StringBuilder();
//            facePitchString.append("上下: ").append((int) face.pose.pitch).append("°");
//            StringBuilder faceYawString = new StringBuilder();
//            faceYawString.append("左右: ").append((int) face.pose.yaw).append("°");
//            StringBuilder faceBlurString = new StringBuilder();
//            faceBlurString.append("模糊: ").append(face.blur);
        Matrix mat = new Matrix();

//            Log.d("rrr", "w:" + w);
//            Log.d("rrr", "h:" + h);
//            Log.d("rrr", "cameraWidth:" + cameraWidth);
//            Log.d("rrr", "cameraHeight:" + cameraHeight);


        float left = 0;
        float top = 0;
        float right = 0;
        float bottom = 0;
//        Log.d("MianBanJiActivity3", "detectResult.rectX:" + detectResult.rectX);
//        Log.d("MianBanJiActivity3", "detectResult.rectY:" + detectResult.rectY);
//        Log.d("MianBanJiActivity3", "detectResult.rectW:" + detectResult.rectW);
//        Log.d("MianBanJiActivity3", "detectResult.rectH:" + detectResult.rectH);
//        Log.d("MianBanJiActivity3", "detectResult.rectH:------------------------");
        boolean mirror = false;
        switch (0) {
            case 0:
                left = w - detectResult.rectX * s1 - detectResult.rectW * s1;
                top = detectResult.rectY * s2 - 40;
                right = (w - detectResult.rectX * s1);
                bottom = detectResult.rectY * s2 + detectResult.rectH + 80;
                // mat.setScale(mirror ? -1 : 1, 1);
                // mat.postTranslate(mirror ? (float) cameraWidth : 0f, 0f);
                // mat.postScale((float) w / (float) cameraWidth, (float) h / (float) cameraHeight);
                break;
            case 90:
                // mat.setScale(mirror ? -1 : 1, 1);
                //  mat.postTranslate(mirror ? (float) cameraHeight : 0f, 0f);
                //  mat.postScale((float) w / (float) cameraHeight, (float) h / (float) cameraWidth);
                left = detectResult.rectY * s2 - 40;
                top = (w - detectResult.rectX * s1);
                right = detectResult.rectY * s2 + detectResult.rectH + 80;
                bottom = w - detectResult.rectX * s1 - detectResult.rectW * s1;
                break;
            case 180:
                mat.setScale(1, mirror ? -1 : 1);
                mat.postTranslate(0f, mirror ? (float) cameraHeight : 0f);
                mat.postScale((float) w / (float) cameraWidth, (float) h / (float) cameraHeight);
                left = detectResult.rectW + detectResult.rectX;
                top = detectResult.rectH + detectResult.rectY;
                right = detectResult.rectX;
                bottom = detectResult.rectY;
                break;
            case 270:
                mat.setScale(mirror ? -1 : 1, 1);
                mat.postTranslate(mirror ? (float) cameraHeight : 0f, 0f);
                mat.postScale((float) w / (float) cameraHeight, (float) h / (float) cameraWidth);
                left = cameraHeight - (detectResult.rectH + detectResult.rectY);
                top = detectResult.rectX;
                right = cameraHeight - detectResult.rectY;
                bottom = detectResult.rectW + detectResult.rectX;
        }
        RectF drect = new RectF();
        RectF srect = new RectF(left, top, right, bottom);

        mat.mapRect(drect, srect);
        // faceView2.addRect(drect);
        // faceView2.addId(detectResult.trackId + "");
//            faceView.addRoll(faceRollString.toString());
//            faceView.addPitch(facePitchString.toString());
//            faceView.addYaw(faceYawString.toString());
//            faceView.addBlur(faceBlurString.toString());
        //           faceView.addSmile(smileString.toString());
        //   faceView.invalidate();
        // faceView2.invalidate();
    }

    @Override
    protected void onStop() {
        Log.d("MianBanJiActivity3", "停止");
        if (paAccessControl != null) {
            paAccessControl.stopFrameDetect();
        }

        if (manager != null) {
            manager.release();
        }
        if (manager2 != null) {
            manager2.release();
        }
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.d("MianBanJiActivity3", "onRestart");
        super.onRestart();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("MianBanJiActivity3", "onStart");
    }


    @Override
    protected void onDestroy() {
        Log.d("MianBanJiActivity3", "onDestroy");

        if (mReadThread != null) {
            mReadThread.interrupt();
        }
        unregisterReceiver(timeChangeReceiver);
        unregisterReceiver(netWorkStateReceiver);
        EventBus.getDefault().unregister(this);//解除订阅
        if (manager != null) {
            manager.release();
        }
        timer.cancel();
        if (task != null)
            task.cancel();

        timer2.cancel();
        if (task2 != null)
            task2.cancel();

        DengUT.getInstance(baoCunBean).closeWrite();
        DengUT.getInstance(baoCunBean).closeGreen();
        DengUT.getInstance(baoCunBean).closeRed();

        super.onDestroy();
    }

    private static final int REQUEST_CODE_CHOOSE_PICK = 1;


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            if (keyCode == KeyEvent.KEYCODE_MENU) {
//              // startActivity(new Intent(MianBanJiActivity3.this, SheZhiActivity2.class));
//              //  finish();
//            }
//
//        }
//
//        return super.onKeyDown(keyCode, event);
//
//    }


    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(String event) {

        if (event.equals("ditu123")) {
            // if (baoCunBean.getTouxiangzhuji() != null)
            //    daBg.setImageBitmap(BitmapFactory.decodeFile(baoCunBean.getTouxiangzhuji()));
            baoCunBean = baoCunBeanDao.get(123456L);

            //   Log.d("MainActivity101", "dfgdsgfdgfdgfdg");
            return;
        }

        if (event.equals("kaimen")) {
            menjing1();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(jidianqi);
                    menjing2();
                }
            }).start();
            return;
        }
        if (event.equals("guanbimain")) {
            finish();
            return;
        }
        if (event.equals("configs")){
            //更新配置
            baoCunBean = baoCunBeanDao.get(123456L);
            initFaceConfig();
            mCompareThres=baoCunBean.getShibieFaZhi();
            if (baoCunBean.getWenzi1() == null) {
                tvTitle_Ir.setText("请设置公司名称");
            } else {
                tvTitle_Ir.setText(baoCunBean.getWenzi1());
            }
            if (baoCunBean.getJidianqi()!=0){
                jidianqi=baoCunBean.getJidianqi();
            }
            if (baoCunBean.getMoshengrenPanDing()!=0){
                cishu=baoCunBean.getMoshengrenPanDing();
            }
            if (baoCunBean.getLogo()!=null){
                try {
                    logo.setImageBitmap(BitmapUtil.base64ToBitmap(baoCunBean.getLogo()));
                    logo.invalidate();
                }catch (Exception e){
                    e.printStackTrace();
                    Log.d("MianBanJiActivity3", "设置logo异常:"+e.getMessage());
                }

            }
            return;
        }
        Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, event, TastyToast.LENGTH_LONG, TastyToast.INFO);
        tastyToast.setGravity(Gravity.CENTER, 0, 0);
        tastyToast.show();

    }


    class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (Objects.requireNonNull(intent.getAction())) {
                case Intent.ACTION_TIME_TICK:
                    //mianBanJiView.setTime(DateUtils.time(System.currentTimeMillis()+""));
                    // String riqi11 = DateUtils.getWeek(System.currentTimeMillis()) + "   " + DateUtils.timesTwo(System.currentTimeMillis() + "");
                    //  riqi.setTypeface(tf);

                    link_xintiao();

                    String xiaoshiss = DateUtils.timeMinute(System.currentTimeMillis() + "");
                    if (xiaoshiss.split(":")[0].equals("03") && xiaoshiss.split(":")[1].equals("40")) {

                        if (serverManager != null) {
                            serverManager.stopServer();
                            serverManager = null;
                        }
                        serverManager = new ServerManager(FileUtil.getIPAddress(getApplicationContext()), baoCunBean.getPort());
                        serverManager.setMyServeInterface(MianBanJiActivity3.this);
                        serverManager.startServer();
                    }


                    //1分钟一次指令获取
//                    if (baoCunBean.getHoutaiDiZhi() != null && !baoCunBean.getHoutaiDiZhi().equals("")) {
//                        if (isGET){
//                            isGET=false;
//                            link_get_zhiling();
//                        }
//                    }

                    break;
                case Intent.ACTION_TIME_CHANGED:
                    //设置了系统时间
                    // Toast.makeText(context, "system time changed", Toast.LENGTH_SHORT).show();
                    break;
                case Intent.ACTION_TIMEZONE_CHANGED:
                    //设置了系统时区的action
                    //  Toast.makeText(context, "system time zone changed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }


//    //获取指令
//    private void link_get_zhiling() {
//        if (baoCunBean.getHoutaiDiZhi() == null || baoCunBean.getHoutaiDiZhi().equals("")) {
//            return;
//        }
//        Request.Builder requestBuilder = new Request.Builder()
//                .header("Content-Type", "application/json")
//                .get()
//                .url(baoCunBean.getHoutaiDiZhi() + "/app/getCommands?" + "serialnumber=" + JHM);
//        // step 3：创建 Call 对象
//        Call call = okHttpClient.newCall(requestBuilder.build());
//        //step 4: 开始异步请求
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d("AllConnects", "请求失败" + e.getMessage());
//                isGET=true;
//            }
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                Log.d("AllConnects", "请求成功" + call.request().toString());
//                //获得返回体
//                try {
//                    ResponseBody body = response.body();
//                    String ss = body.string().trim();
//                    JsonObject jsonObject = GsonUtil.parse(ss).getAsJsonObject();
//                    Gson gson = new Gson();
//                    Log.d("AllConnects", "获取指令:" + ss);
//                    ZhiLingBean commandsBean = gson.fromJson(jsonObject, ZhiLingBean.class);
//                    if (commandsBean != null && commandsBean.getCode() == 0) {
//                        for (ZhiLingBean.ResultBean resultBean : commandsBean.getResult()) {
//                            linkedBlockingQueue.offer(resultBean);
//                        }
//                        if (linkedBlockingQueue.size()==0){
//                            isGET=true;
//                        }
//                    }else {
//                        isGET=true;
//                    }
//                } catch (Exception e) {
//                    isGET=true;
//                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");
//                }
//            }
//        });
//    }


    //上传记录
    private void link_shangchuanshualian(String id, Bitmap bitmap, String pepopleType,long bitmapId) {
        if (baoCunBean.getHoutaiDiZhi() == null || baoCunBean.getHoutaiDiZhi().equals("")) {
            DaKaBean daKaBean=new DaKaBean();
            daKaBean.setId(bitmapId);
            daKaBean.setPath("http://" + FileUtil.getIPAddress(getApplicationContext()) + ":" + baoCunBean.getPort() + "/getFaceBitmap2?id=" + id);
            daKaBean.setPersonId(id);
            daKaBean.setTime(bitmapId);
            daKaBean.setType("0");
            daKaBean.setState(1);
            daKaBeanBox.put(daKaBean);
            return;
        }
        Bitmap bb = BitmapUtil.rotateBitmap(bitmap, SettingVar.msrBitmapRotation);
        RequestBody body = null;
        if (pepopleType.equals("card_0")){
            body = new FormBody.Builder()
                    .add("personId", id + "")
                    .add("deviceKey", JHM)
                    .add("time",System.currentTimeMillis()+"" )
                    .add("type", pepopleType)
                    .add("path","")
                    .add("imgBase64", "")
                    .add("data", "")
                    .build();
        }else {
            body = new FormBody.Builder()
                    .add("personId", id + "")
                    .add("deviceKey", JHM)
                    .add("time", System.currentTimeMillis() + "")
                    .add("type", pepopleType)
                    .add("path", "http://" + FileUtil.getIPAddress(getApplicationContext()) + ":" + baoCunBean.getPort() + "/getFaceBitmap?id=" + id)
                    .add("imgBase64", Bitmap2StrByBase64(bb))
                    .add("data", "")
                    .build();
        }
        Request.Builder requestBuilder=null;
        try {
            requestBuilder = new Request.Builder()
                    .header("Content-Type", "application/json")
                    .post(body)
                    .url(baoCunBean.getHoutaiDiZhi());
        }catch (Exception e){
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "上传识别记录地址错误", TastyToast.LENGTH_LONG, TastyToast.INFO);
                    tastyToast.setGravity(Gravity.CENTER, 0, 0);
                    tastyToast.show();
                }
            });
            DaKaBean daKaBean=new DaKaBean();
            daKaBean.setId(bitmapId);
            daKaBean.setPath("http://" + FileUtil.getIPAddress(getApplicationContext()) + ":" + baoCunBean.getPort() + "/getFaceBitmap2?id=" + id);
            daKaBean.setPersonId(id);
            daKaBean.setTime(bitmapId);
            daKaBean.setType("0");
            daKaBean.setState(1);
            daKaBeanBox.put(daKaBean);

            return;
        }
        // step 3：创建 Call 对象

        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求失败" + e.getMessage());
                DaKaBean daKaBean=new DaKaBean();
                daKaBean.setId(bitmapId);
                daKaBean.setPath("http://" + FileUtil.getIPAddress(getApplicationContext()) + ":" + baoCunBean.getPort() + "/getFaceBitmap2?id=" + id);
                daKaBean.setPersonId(id);
                daKaBean.setTime(bitmapId);
                daKaBean.setType("0");
                daKaBean.setState(1);
                daKaBeanBox.put(daKaBean);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "上传识别记录失败", TastyToast.LENGTH_LONG, TastyToast.INFO);
                        tastyToast.setGravity(Gravity.CENTER, 0, 0);
                        tastyToast.show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("AllConnects", "请求成功" + call.request().toString());
                //获得返回体
                try {
                    ResponseBody body = response.body();
                    String ss = body.string().trim();


                    DaKaBean daKaBean=new DaKaBean();
                    daKaBean.setId(bitmapId);
                    daKaBean.setPath("http://" + FileUtil.getIPAddress(getApplicationContext()) + ":" + baoCunBean.getPort() + "/getFaceBitmap2?id=" + id);
                    daKaBean.setPersonId(id);
                    daKaBean.setTime(bitmapId);
                    daKaBean.setType("1");
                    daKaBean.setState(1);
                    daKaBeanBox.put(daKaBean);

                    Log.d("AllConnects", "上传识别记录" + ss);

                } catch (Exception e) {
                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");
                }
            }
        });
    }

    //上传记录
    private void link_shuaka(String id,String name) {
        if (baoCunBean.getHoutaiDiZhi() == null || baoCunBean.getHoutaiDiZhi().equals("")) {
            return;
        }
        RequestBody body = null;
        body = new FormBody.Builder()
                .add("id", id)
                .add("name", name)
                .add("serialnumber", JHM)
                .build();
        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .post(body)
                .url(baoCunBean.getHoutaiDiZhi() + "/app/updateIDcardTake");
        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求失败" + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "上传识别记录失败", TastyToast.LENGTH_LONG, TastyToast.INFO);
                        tastyToast.setGravity(Gravity.CENTER, 0, 0);
                        tastyToast.show();
                    }
                });
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("AllConnects", "请求成功" + call.request().toString());
                //获得返回体
                try {
                    ResponseBody body = response.body();
                    String ss = body.string().trim();

                    Log.d("AllConnects", "上传识别记录" + ss);

                } catch (Exception e) {
                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");

                }
            }
        });
    }

    //心跳
    private void link_xintiao() {
        if (baoCunBean.getXintiaoDIZhi() == null || baoCunBean.getXintiaoDIZhi().equals("")) {
            return;
        }
        RequestBody body = null;
        body = new FormBody.Builder()
                .add("deviceKey", JHM)
                .add("time", System.currentTimeMillis()+"")
                .add("ip", FileUtil.getIPAddress(getApplicationContext())+"")
                .add("version", AppUtils.getVersionName(MyApplication.myApplication)+"")
                .build();
        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .post(body)
                .url(baoCunBean.getXintiaoDIZhi());
        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "请求失败" + e.getMessage());
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "心跳请求失败", TastyToast.LENGTH_LONG, TastyToast.INFO);
//                        tastyToast.setGravity(Gravity.CENTER, 0, 0);
//                        tastyToast.show();
//                    }
//                });
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("AllConnects", "请求成功" + call.request().toString());
                //获得返回体
                try {
                    ResponseBody body = response.body();
                    String ss = body.string().trim();

                    Log.d("AllConnects", "心跳" + ss);

                } catch (Exception e) {
                    Log.d("WebsocketPushMsg", e.getMessage() + "gggg");

                }
            }
        });
    }

    //数据同步
    private void link_infoSync() {
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        JSONArray array = new JSONArray();
        List<HuiFuBean> huiFuBeanList = huiFuBeanBox.getAll();
        if (huiFuBeanList != null) {
            for (HuiFuBean bean : huiFuBeanList) {
                JSONObject object = new JSONObject();
                try {
                    object.put("pepopleId", bean.getPepopleId());
                    object.put("pepopleType", bean.getPepopleType());
                    object.put("type", bean.getType());
                    object.put("msg", bean.getMsg());
                    object.put("shortId", bean.getShortId());
                    object.put("serialnumber", JHM);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                array.put(object);
            }
        } else {
            isLink = false;
            return;
        }

        Log.d("MianBanJiActivity3", "数据同步：" + array.toString());
        RequestBody body = RequestBody.create(array.toString(), JSON);
        Request.Builder requestBuilder = new Request.Builder()
                .header("Content-Type", "application/json")
                .post(body)
                .url(baoCunBean.getHoutaiDiZhi() + "/app/infoSync");
        // step 3：创建 Call 对象
        Call call = okHttpClient.newCall(requestBuilder.build());
        //step 4: 开始异步请求
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("AllConnects", "数据同步请求失败" + e.getMessage());
                isLink = false;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("AllConnects", "请求成功" + call.request().toString());
                //获得返回体
                try {
                    ResponseBody body = response.body();
                    String ss = body.string().trim();
                    Log.d("AllConnects", "数据同步:" + ss);
                    for (HuiFuBean bean : huiFuBeanList) {
                        huiFuBeanBox.remove(bean);
                    }
                } catch (Exception e) {
                    Log.d("WebsocketPushMsg", e.getMessage() + "数据同步");
                } finally {
                    isLink = false;
                }
            }
        });
    }

    //信鸽信息处理
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onDataSynEvent(XGBean xgBean) {


    }


    private void guanPing() {
        Intent intent = new Intent();
        intent.setAction("LYD_SHOW_NAVIGATION_BAR");
        intent.putExtra("type", 0);
        this.sendBroadcast(intent);
        sendBroadcast(new Intent("com.android.internal.policy.impl.hideNavigationBar"));
        sendBroadcast(new Intent("com.android.systemui.statusbar.phone.statusclose"));
        if (jiqiType==2){//8寸防水面板机
            try {
                Lztek lztek=Lztek.create(MyApplication.myApplication);
                lztek.navigationBarSlideShow(false);
                lztek.hideNavigationBar();
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                HwitManager.HwitSetHideSystemBar(MianBanJiActivity3.this);
                HwitManager.HwitSetDisableSlideShowSysBar(1);
            }catch (NoClassDefFoundError error){
                error.printStackTrace();
            }

        }

    }

    private void menjing1() {
        // TPS980PosUtil.setJiaJiPower(1);
        DengUT.getInstance(baoCunBean).openDool();
        //  TPS980PosUtil.setRelayPower(1);
        Log.d("MianBanJiActivity3", "打开");
    }

    private void menjing2() {
        //  TPS980PosUtil.setJiaJiPower(0);
        DengUT.getInstance(baoCunBean).closeDool();
        // TPS980PosUtil.setRelayPower(0);
        Log.d("MianBanJiActivity3", "关闭");
    }


    /**
     * 获取本地化后的config
     * 注册和比对使用不同的设置
     */
    private void initFaceConfig() {
        //Robin 使用比对的设置
        PaAccessDetectConfig faceDetectConfig = PaAccessControl.getInstance().getPaAccessDetectConfig();
        faceDetectConfig.setFaceConfidenceThreshold(0.85f); //检测是不是人的脸。默认使用阀值 0.85f，阈值视具体 情况而定，最大为 1。
        faceDetectConfig.setYawThreshold(40);//人脸识别角度
        faceDetectConfig.setRollThreshold(40);
        faceDetectConfig.setPitchThreshold(40);
        // 注册图片模糊度可以设置0.9f（最大值1.0）这样能让底图更清晰。比对的模糊度可以调低一点，这样能加快识别速度，识别模糊度建议设置0.1f
        faceDetectConfig.setBlurnessThreshold(0.5f);
        faceDetectConfig.setMinBrightnessThreshold(30); // 人脸图像最小亮度阀值，默认为 30，数值越小越 暗，太暗会影响人脸检测和活体识别，可以根据 需求调整。
        faceDetectConfig.setMaxBrightnessThreshold(240);// 人脸图像最大亮度阀值，默认为 240，数值越大 越亮，太亮会影响人脸检测和活体识别，可以根 据需求调整。
        faceDetectConfig.setAttributeEnabled(false);//人脸属性开关，默认关闭。会检测出人脸的性别 和年龄。人脸属性的检测会消耗运算资源，可视 情况开启，未开启性别和年龄都返回-1
        faceDetectConfig.setLivenessEnabled(baoCunBean.isHuoTi());//活体开关
        // faceDetectConfig.setTrackingMode(true); //Robin 跟踪模式跟踪模式，开启后会提高检脸检出率，减小检脸耗时。门禁场景推荐开启。图片检测会强制关闭
        faceDetectConfig.setIrEnabled(baoCunBean.isHuoTi()); //非Ir模式，因为是单例模式，所以最好每个界面都设置是否开启Ir模式
        //faceDetectConfig.setMinScaleThreshold(0.1f);//设置最小检脸尺寸，可以用这个来控制最远检脸距离。默认采用最小值 0.1，约 1.8 米，在 640*480 的预览分辨率下，最小人脸尺寸 为(240*0.1)*(240*0.1)即 24*24。 0.2 的最远 识别距离约 1.2 米;0.3 的最远识别距离约约 0.8 米。detectFaceMinScale 取值范围 [0.1,0.3]。门禁场景推荐 0.1;手机场景推荐 0.3。
        PaAccessNativeConfig nativeConfig = new PaAccessNativeConfig();
        //Robin 检测人脸数为1
        nativeConfig.faceNumber = 1;
        paAccessControl.setPaAccessNativeConfig(nativeConfig);
        paAccessControl.setPaAccessDetectConfig(faceDetectConfig);
        PaAccessControl.getInstance().setPaAccessDetectConfig(faceDetectConfig);
    }


    private void init_NFC() {
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
    }

    private void stopNFC_Listener() {
        mNfcAdapter.disableForegroundDispatch(this);
    }

    public void processIntent(Intent intent) {
        //  String data = null;
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        // String[] techList = tag.getTechList();
        // Log.d("Mian", "tag.describeContents():" + tag.describeContents());
        byte[] ID;
        //  data = tag.toString();
        if (tag == null)
            return;
        ID = tag.getId();
//        data += "\n\nUID:\n" + byteToString(ID);
//        data += "\nData format:";
//        for (String tech : techList) {
//            data += "\n" + tech;
//        }
//         Log.d("MianBanJiActivity3", byteToString(ID));
        String sdfds = byteToString(ID);
        if (sdfds != null) {
            Log.d("MianBanJiActivity3", sdfds);
            sdfds = sdfds.toUpperCase();
            List<IDCardBean> idCardBeanList = idCardBeanBox.query().equal(IDCardBean_.idCard, sdfds).build().find();
            if (idCardBeanList.size() > 0) {
                Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "验证成功!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                tastyToast.setGravity(Gravity.CENTER, 0, 0);
                tastyToast.show();
                soundPool.play(musicId.get(1), 1, 1, 0, 0, 1);
                DengUT.getInstance(baoCunBean).openDool();
                IDCardBean cardBean=idCardBeanList.get(0);
                link_shuaka(sdfds,cardBean.getName());
                //启动定时器或重置定时器
                if (task != null) {
                    task.cancel();
                    task = new TimerTask() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = 222;
                            mHandler.sendMessage(message);
                        }
                    };
                    timer.schedule(task, 6000);
                } else {
                    task = new TimerTask() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = 222;
                            mHandler.sendMessage(message);
                        }
                    };
                    timer.schedule(task, 6000);
                }

                IDCardTakeBean takeBean=new IDCardTakeBean();
                takeBean.setIdCard(sdfds);
                takeBean.setName(cardBean.getName());
                takeBean.setTime(System.currentTimeMillis());
                idCardTakeBeanBox.put(takeBean);

            } else {
                Toast tastyToast = TastyToast.makeText(MianBanJiActivity3.this, "验证失败!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                tastyToast.setGravity(Gravity.CENTER, 0, 0);
                tastyToast.show();
                soundPool.play(musicId.get(2), 1, 1, 0, 0, 1);
            }
        }


    }

    /**
     * 将byte数组转化为字符串
     *
     * @param src
     * @return
     */
    public static String byteToString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            // System.out.println(buffer);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }


    public class NetWorkStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            //检测API是不是小于23，因为到了API23之后getNetworkInfo(int networkType)方法被弃用
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

                //获得ConnectivityManager对象
                ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                //获取ConnectivityManager对象对应的NetworkInfo对象
                //以太网
                NetworkInfo wifiNetworkInfo1 = connMgr.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
                //获取WIFI连接的信息
                NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                //获取移动数据连接的信息
                NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (wifiNetworkInfo1.isConnected() || wifiNetworkInfo.isConnected() || dataNetworkInfo.isConnected()) {
                    //有网
                    Log.d("MianBanJiActivity3", "有网1");
                    if (serverManager != null) {
                        serverManager.stopServer();
                        serverManager = null;
                    }
                    serverManager = new ServerManager(FileUtil.getIPAddress(getApplicationContext()), baoCunBean.getPort());
                    serverManager.setMyServeInterface(MianBanJiActivity3.this);
                    serverManager.startServer();

                } else {
                    //没网
                    Log.d("MianBanJiActivity3", "没网1");
                    if (serverManager != null) {
                        serverManager.stopServer();
                        serverManager = null;
                    }
                }

//				if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
//					Toast.makeText(context, "WIFI已连接,移动数据已连接", Toast.LENGTH_SHORT).show();
//				} else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
//					Toast.makeText(context, "WIFI已连接,移动数据已断开", Toast.LENGTH_SHORT).show();
//				} else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
//					Toast.makeText(context, "WIFI已断开,移动数据已连接", Toast.LENGTH_SHORT).show();
//				} else {
//					Toast.makeText(context, "WIFI已断开,移动数据已断开", Toast.LENGTH_SHORT).show();
//				}
//API大于23时使用下面的方式进行网络监听
            } else {

                //获得ConnectivityManager对象
                ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                //获取所有网络连接的信息
                Network[] networks = connMgr.getAllNetworks();
                //用于存放网络连接信息
                StringBuilder sb = new StringBuilder();
                //通过循环将网络信息逐个取出来
                Log.d("MianBanJiActivity3", "networks.length:" + networks.length);
                if (networks.length == 0) {
                    //没网
                    Log.d("MianBanJiActivity3", "没网2");
                    if (serverManager != null) {
                        serverManager.stopServer();
                        serverManager = null;
                    }
                }
                for (Network network : networks) {
                    //获取ConnectivityManager对象对应的NetworkInfo对象
                    NetworkInfo networkInfo = connMgr.getNetworkInfo(network);
                    if (networkInfo.isConnected()) {
                        //连接上
                        Log.d("MianBanJiActivity3", "有网2");
                        if (serverManager != null) {
                            serverManager.stopServer();
                            serverManager = null;
                        }
                        serverManager = new ServerManager(FileUtil.getIPAddress(getApplicationContext()), baoCunBean.getPort());
                        serverManager.setMyServeInterface(MianBanJiActivity3.this);
                        serverManager.startServer();
                        break;
                    }
                }

            }
        }
    }


    /**
     * 通过Base32将Bitmap转换成Base64字符串
     *
     * @param bit
     * @return
     */
    public String Bitmap2StrByBase64(Bitmap bit) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bit.compress(Bitmap.CompressFormat.JPEG, 90, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }



    /**
     * 显示结果UI
     *
     * @param state 状态 1 初始状态  2 识别中,出现提示语  3 识别失败  4 识别成功
     */
    protected void showUIResult(final int state, final String name, final String detectFaceTime) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("MianBanJiActivity3", "state:" + state);
                switch (state) {
                    case 1: {//初始状态
                        layout_true_gif_Ir.setVisibility(View.INVISIBLE);//蓝色图片动画
                        layout_error_gif_Ir.setVisibility(View.INVISIBLE);//红色图片动画
                        iv_true_gif_in_Ir.setVisibility(View.INVISIBLE);//蓝色圈内层
                        iv_true_gif_out_Ir.setVisibility(View.INVISIBLE);//蓝色圈外层
                        iv_error_gif_in_Ir.setVisibility(View.INVISIBLE);//红色圈内层
                        iv_error_gif_out_Ir.setVisibility(View.INVISIBLE);//红色圈外层
                        layout_loadbg_Ir.setVisibility(View.INVISIBLE);//识别结果大框
                        tvName_Ir.setVisibility(View.GONE);//姓名
                        tvTime_Ir.setVisibility(View.GONE);//时间
                        tvFaceTips_Ir.setVisibility(View.GONE);//识别提示
                        tvName_Ir.setText("");
                        tvTime_Ir.setText("");
                        tvFaceTips_Ir.setText("");
                        break;
                    }
                    case 2: {//识别中,出现提示语
                        layout_true_gif_Ir.setVisibility(View.VISIBLE);//蓝色图片动画
                        layout_error_gif_Ir.setVisibility(View.INVISIBLE);//红色图片动画
                        iv_true_gif_in_Ir.setVisibility(View.VISIBLE);//蓝色圈内层
                        iv_true_gif_out_Ir.setVisibility(View.VISIBLE);//蓝色圈外层
                        iv_error_gif_in_Ir.setVisibility(View.INVISIBLE);//红色圈内层
                        iv_error_gif_out_Ir.setVisibility(View.INVISIBLE);//红色圈外层
                        layout_loadbg_Ir.setVisibility(View.VISIBLE);//识别结果大框
                        layout_loadbg_Ir.setBackgroundResource(R.mipmap.true_bg);//切换背景
                        tvName_Ir.setVisibility(View.GONE);//姓名
                        tvTime_Ir.setVisibility(View.GONE);//时间
                        tvFaceTips_Ir.setVisibility(View.VISIBLE);//识别提示
                        tvName_Ir.setText("");
                        tvTime_Ir.setText("");
                        tvFaceTips_Ir.setText("识别中,请稍后...");
                        break;
                    }
                    case 3: {//识别失败
                        layout_true_gif_Ir.setVisibility(View.INVISIBLE);//蓝色图片动画
                        layout_error_gif_Ir.setVisibility(View.VISIBLE);//红色图片动画
                        iv_true_gif_in_Ir.setVisibility(View.INVISIBLE);//蓝色圈内层
                        iv_true_gif_out_Ir.setVisibility(View.INVISIBLE);//蓝色圈外层
                        iv_error_gif_in_Ir.setVisibility(View.VISIBLE);//红色圈内层
                        iv_error_gif_out_Ir.setVisibility(View.VISIBLE);//红色圈外层
                        layout_loadbg_Ir.setVisibility(View.VISIBLE);//识别结果大框
                        layout_loadbg_Ir.setBackgroundResource(R.mipmap.error_bg);//切换背景
                        tvName_Ir.setVisibility(View.GONE);//姓名
                        tvTime_Ir.setVisibility(View.GONE);//时间
                        tvFaceTips_Ir.setVisibility(View.VISIBLE);//识别提示
                        tvName_Ir.setText("");
                        tvTime_Ir.setText("");
                        tvFaceTips_Ir.setText("无权限通过,请重试");
                        break;
                    }
                    case 4: {//识别成功
                        layout_true_gif_Ir.setVisibility(View.VISIBLE);//蓝色图片动画
                        layout_error_gif_Ir.setVisibility(View.INVISIBLE);//红色图片动画
                        iv_true_gif_in_Ir.setVisibility(View.VISIBLE);//蓝色圈内层
                        iv_true_gif_out_Ir.setVisibility(View.VISIBLE);//蓝色圈外层
                        iv_error_gif_in_Ir.setVisibility(View.INVISIBLE);//红色圈内层
                        iv_error_gif_out_Ir.setVisibility(View.INVISIBLE);//红色圈外层
                        layout_loadbg_Ir.setVisibility(View.VISIBLE);//识别结果大框
                        layout_loadbg_Ir.setBackgroundResource(R.mipmap.true_bg);//切换背景
                        tvName_Ir.setVisibility(View.VISIBLE);//姓名
                        tvTime_Ir.setVisibility(View.VISIBLE);//时间
                        tvFaceTips_Ir.setVisibility(View.GONE);//识别提示
                        tvName_Ir.setText(name);
                        tvTime_Ir.setText(detectFaceTime);
                        tvFaceTips_Ir.setText("");
                        break;
                    }
                }
            }
        });
    }

}
