package com.hykc.cityfreight.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alct.mdp.model.Image;
import com.baidu.location.LocationClient;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hykc.cityfreight.R;
import com.hykc.cityfreight.app.AlctConstants;
import com.hykc.cityfreight.app.Constants;
import com.hykc.cityfreight.entity.UWaybill;
import com.hykc.cityfreight.service.MqttManagerV3;
import com.hykc.cityfreight.utils.AlctManager;
import com.hykc.cityfreight.utils.GlideImageLoader;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.utils.SharePreferenceUtil;
import com.hykc.cityfreight.view.ExitDialogFragment;
import com.hykc.cityfreight.view.ImageExampleDialog;
import com.hykc.cityfreight.view.LoadingDialogFragment;
import com.hykc.cityfreight.view.SelectDialog;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UpLoadImgActivity extends BaseActivity {
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    public static final int XHZ = 1;//卸货照
    public static final int HDZ = 2;//回单照
    private static final String TYPE_HDZ="HDZ.jpg";
    private static final String TYPE_XHZ="XHZ.jpg";
    private ImageView mImgBack;
    private ImageView mImgXHZ;
    private ImageView mImgHDZ;
    private Button mBtnOk;
    private int maxImgCount = 1;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private int selectType = -1;
    private UWaybill uWaybill;
    private boolean isSuccessXHZ=false;
    private boolean isSuccessHDZ=false;
    private boolean isAnlXHZ=false;
    private boolean isAnlHDZ=false;
    private String userid;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private String address;
    private Map<Integer,String> map=new HashMap<>();
    private LocationClient mLocClient;
    private View imglinView;
    private boolean isOthers=false;
    private TextView tv_ysc;
    private TextView mTextXHZExample;
    private TextView mTextHDZExample;
    private boolean isAlctUploadHDZ=false;
    private boolean isAlctUploadXHZ=false;
    private LoadingDialogFragment dialogFragment=new LoadingDialogFragment();
    AlctManager alctManager=AlctManager.newInstance();
    MqttManagerV3 mqttManagerV3;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_upload_img);

        init();
    }

    @Override
    public void init() {
        initImagePicker();
        mImgBack =  findViewById(R.id.img_back);

        mTextXHZExample=findViewById(R.id.tv_xhz_example);
        mTextHDZExample=findViewById(R.id.tv_hdz_example);
        uWaybill = (UWaybill) getIntent().getSerializableExtra("entity");
        mImgXHZ = findViewById(R.id.img_xhz);
        mImgHDZ = findViewById(R.id.img_hdz);
        mBtnOk = findViewById(R.id.btn_ok);
        userid=SharePreferenceUtil.getInstance(this).getUserId();
        mqttManagerV3=MqttManagerV3.getInstance(userid);
        selImageList = new ArrayList<>();

        alctManager.setOnAlctResultListener(new MyAlctListener());
        initEvent();
        getPicInfo();
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(false);                            //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(1);                        //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    class MyAlctListener implements AlctManager.OnAlctResultListener{
        @Override
        public void onSuccess(int type,UWaybill uWaybill) {
            Log.e("wwc alct","wwc alct success   "+type);
            if (type == AlctConstants.REGISTER_SUCCESS) {
                return;
            }
            int updateType=-1;
            try {
                JSONObject object = new JSONObject();
                if (type == AlctConstants.XHZ_SUCCESS) {
                    isAlctUploadXHZ=true;
                    object.put("type", type + "");
                    object.put("success","true");
                    object.put("msg","卸货照上传成功！");
                    object.put("waybillId",uWaybill.getWaybillId());
                    mqttManagerV3.sendWithThread(object.toString(), "");
                    updateType=3;
                    uWaybill.setAlctUnloadMsg("卸货照上传成功！");
                    upAlctImgMsg(uWaybill,"卸货照上传成功！");
                    Toast.makeText(UpLoadImgActivity.this, "卸货照上传成功!", Toast.LENGTH_SHORT).show();
                } else if (type == AlctConstants.HDZ_SUCCESS) {
                    isAlctUploadHDZ=true;
                    object.put("type", type + "");
                    object.put("success","true");
                    object.put("msg","回单照上传成功！");
                    object.put("waybillId",uWaybill.getWaybillId());
                    mqttManagerV3.sendWithThread(object.toString(), "");
                    Toast.makeText(UpLoadImgActivity.this, "回单照上传成功!", Toast.LENGTH_SHORT).show();

                }
                if(updateType!=-1){
                    updateOrderAlctMsg(updateType,uWaybill);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onError(int type, UWaybill uWaybill, String msg) {
            String message=null;
            if (type == AlctConstants.REGISTER_ERROR) {
                return;
            }
            int updateType=-1;
            if(type==AlctConstants.XHZ_ERROR){
                isAlctUploadXHZ=true;
                message="卸货照上传失败！"+msg;
                updateType=3;
                uWaybill.setAlctUnloadMsg(message);
                upAlctImgMsg(uWaybill,message);
                Toast.makeText(UpLoadImgActivity.this, "卸货照上传失败!", Toast.LENGTH_SHORT).show();

            }else if(type==AlctConstants.HDZ_ERROR){
                isAlctUploadHDZ=false;
                message="回单照上传失败！"+msg;
                Toast.makeText(UpLoadImgActivity.this, "回单照上传失败!", Toast.LENGTH_SHORT).show();

            }

            try {
                JSONObject object = new JSONObject();
                object.put("type", type + "");
                object.put("success","false");
                object.put("msg",message);
                object.put("waybillId",uWaybill.getWaybillId());
                mqttManagerV3.sendWithThread(object.toString(), "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(updateType!=-1){
                updateOrderAlctMsg(updateType,uWaybill);
            }
        }
    }
    private void updateOrderAlctMsg( int updateType,UWaybill uWaybill){
        String pickupMsg=uWaybill.getPickupMsg();
        if(pickupMsg==null){
            pickupMsg="";
        }
        String unloadMsg=uWaybill.getUnloadMsg();
        if(unloadMsg==null){
            unloadMsg="";
        }
        String alctUnloadMsg=uWaybill.getAlctUnloadMsg();
        if(alctUnloadMsg==null){
            alctUnloadMsg="";
        }
        Map<String, String> map = new HashMap<>();
        map.put("id",uWaybill.getId()+"");
        map.put("updateType",updateType+"");
        map.put("pickupMsg",pickupMsg);
        map.put("unloadMsg",unloadMsg);
        map.put("alctUnloadMsg",alctUnloadMsg);
        RequestManager.getInstance()
                .mServiceStore
                .updateOrderAlctMsg(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("updateOrderAlctMsg", msg);
                    }
                    @Override
                    public void onError(String msg) {
                        Log.e("updateOrderAlctMsg", msg);
                    }
                }));
    }
    private void getPicInfo() {
        String unloadUrl=uWaybill.getUnloadURL();
        Log.e("unloadUrl","unloadUrl"+Constants.WEBSERVICE_URL+unloadUrl);
        if(!TextUtils.isEmpty(unloadUrl)){
            Glide.with(UpLoadImgActivity.this)
                    .load(Constants.WEBSERVICE_URL+unloadUrl)
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgXHZ);
            new BitmapThread(Constants.WEBSERVICE_URL+unloadUrl,TYPE_XHZ).start();
        }
        String podUrl=uWaybill.getPodURL();
        if(!TextUtils.isEmpty(podUrl)){
            Glide.with(UpLoadImgActivity.this)
                    .load(Constants.WEBSERVICE_URL+podUrl)
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgHDZ);
            new BitmapThread(Constants.WEBSERVICE_URL+podUrl,TYPE_HDZ).start();
        }
    }
    private void initEvent() {
        mImgXHZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showXHZPicView(1,"xhzview");
            }
        });
        mImgHDZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHDZPicView(2,"hdzview");
            }
        });

        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSave();
            }
        });
        mTextXHZExample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogView(1,"xhzDialog");
            }
        });
        mTextHDZExample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogView(2,"hdzDialog");
            }
        });
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void showDialogView(int type,String tag){
        final ImageExampleDialog dialog=ImageExampleDialog.getInstance(type);
        dialog.showF(getSupportFragmentManager(),tag);
        dialog.setOnButtonClickListener(new ImageExampleDialog.OnButtonClickListener() {
            @Override
            public void onClick() {
                dialog.dismissAllowingStateLoss();
            }
        });
    }

    private void showXHZPicView(int type,String tag){
        final ImageExampleDialog dialog=ImageExampleDialog.getInstance(type);
        dialog.showF(getSupportFragmentManager(),tag);
        dialog.setOnButtonClickListener(new ImageExampleDialog.OnButtonClickListener() {
            @Override
            public void onClick() {
                dialog.dismissAllowingStateLoss();
                selectType = XHZ;
                setImg();
            }
        });
    }
    private void showHDZPicView(int type,String tag){
        final ImageExampleDialog dialog=ImageExampleDialog.getInstance(type);
        dialog.showF(getSupportFragmentManager(),tag);
        dialog.setOnButtonClickListener(new ImageExampleDialog.OnButtonClickListener() {
            @Override
            public void onClick() {
                dialog.dismissAllowingStateLoss();
                selectType = HDZ;
                setImg();
            }
        });
    }
    private void doSave() {
        if(TextUtils.isEmpty(map.get(XHZ))){
            Toast.makeText(this, "请选择卸货照！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(map.get(HDZ))){
            Toast.makeText(this, "请选择回单照！", Toast.LENGTH_SHORT).show();
            return;
        }
        doUpLoad();
    }

    private void doUpLoad() {
        mBtnOk.setClickable(false);
        mBtnOk.setEnabled(false);
        mBtnOk.setBackgroundResource(R.drawable.btn_no_click_bg);
        uploadUnloadImage(map.get(XHZ));
        uploadPODImage(map.get(HDZ));

    }
    //卸货照
    private void uploadUnloadImage(String uploadBuffer) {
        if(uWaybill==null){
            return;
        }
        if(alctManager==null){
            alctManager=AlctManager.newInstance();
            alctManager.setOnAlctResultListener(new MyAlctListener());
        }
        alctManager.uploadUnloadImage(uWaybill,uploadBuffer);
       /* String string=uWaybill.getWaybillId()+";"+uWaybill.getAlctCode();
        Log.e("string=====","string==="+string);
        MDPLocationCollectionManager.uploadUnloadImage(UpLoadImgActivity.this, uWaybill.getWaybillId(),
                uWaybill.getAlctCode(), getImage(uploadBuffer,"unload"), new OnResultListener() {
                    @Override
                    public void onSuccess() {
                        isAlctUploadXHZ=true;
                        Toast.makeText(UpLoadImgActivity.this, "卸货照上传成功！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String s, String s1) {
                        Log.e("uploadUnloadImage",s1);

                        isAlctUploadXHZ=false;
                        Toast.makeText(UpLoadImgActivity.this, "卸货照上传失败！", Toast.LENGTH_SHORT).show();

                    }
                });*/
    }

    //回单照
    private void uploadPODImage(String uploadBuffer) {
        if(uWaybill==null){
            return;
        }
        if(alctManager==null){
            alctManager=AlctManager.newInstance();
            alctManager.setOnAlctResultListener(new MyAlctListener());
        }
        alctManager.uploadPODImage(uWaybill,uploadBuffer);
        /*String string=uWaybill.getWaybillId()+";"+uWaybill.getAlctCode();
        Log.e("string=====","string==="+uploadBuffer);
        MDPLocationCollectionManager.uploadPODImage(UpLoadImgActivity.this, uWaybill.getWaybillId(),
                uWaybill.getAlctCode(), getImage(uploadBuffer,"pod"), new OnResultListener() {
                    @Override
                    public void onSuccess() {
                        isAlctUploadHDZ=true;
                        Toast.makeText(UpLoadImgActivity.this, "回单照上传成功！", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(String s, String s1) {
                        Log.e("uploadPODImage",s1);
                        isAlctUploadHDZ=false;
                        Toast.makeText(UpLoadImgActivity.this, "回单照上传失败！", Toast.LENGTH_SHORT).show();
                    }
                });*/
    }
    private String getNowtime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String time = sdf.format(new Date());
        return  time;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> imgs = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (imgs != null && imgs.size() > 0) {
                    setImage(imgs.get(0));
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                ArrayList<ImageItem> imgs = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (imgs != null && imgs.size() > 0) {
                    setImage(imgs.get(0));
                }
            }
        }
    }

    private void setImage(ImageItem item) {
        ImageView imageView = null;
        if (item == null) {
            return;
        }
        switch (selectType) {
            case XHZ:
                imageView = mImgXHZ;
                break;
            case HDZ:
                imageView = mImgHDZ;
                break;
        }
        if (imageView != null) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImagePicker.getInstance().getImageLoader().displayImage(UpLoadImgActivity.this, item.path, imageView, 0, 0);
            uploadImg(item.path);
        }
    }

    private void uploadImg(String path) {
        Bitmap bitmap = compressImg(path);
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,
                50, baos2);
        byte[] bytes = baos2.toByteArray();
        String uploadBuffer = Base64.encodeToString(bytes, Base64.DEFAULT).replaceAll(" ","");
        if(selectType==XHZ){
            map.put(XHZ,uploadBuffer);
            upLoadImgToService(XHZ,uploadBuffer,TYPE_XHZ);
        }else if(selectType==HDZ){
            map.put(HDZ,uploadBuffer);
            upLoadImgToService(HDZ,uploadBuffer,TYPE_HDZ);
        }
    }


    //上传照片到服务器
    private void upLoadImgToService(final int type, String buffer, String fileName){
        dialogFragment.showF(getSupportFragmentManager(),"unloadView");
        Map<String, String> map = new HashMap<>();
        map.put("id", uWaybill.getId()+"");
        map.put("type",type+"");
        map.put("fileName",fileName);
        map.put("base64",buffer.replaceAll("\\+","-"));
        RequestManager.getInstance()
                .mServiceStore
                .upLoadOrderImg(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        dialogFragment.dismissAllowingStateLoss();
                        try {
                            JSONObject object=new JSONObject(msg);
                            if(object.getBoolean("success")){
                                if(type==HDZ){
                                    isSuccessHDZ=true;
                                }else if(type==XHZ){
                                    isSuccessXHZ=true;
                                }
                                Toast.makeText(UpLoadImgActivity.this, "上传成功！", Toast.LENGTH_SHORT).show();

                            }else {

                                Toast.makeText(UpLoadImgActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("upLoadOrderImg","upLoadOrderImg==="+msg);
                    }
                    @Override
                    public void onError(String msg) {
                        dialogFragment.dismissAllowingStateLoss();
                        Toast.makeText(UpLoadImgActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();

                        Log.e("upLoadOrderImg", msg);
                    }
                }));
    }

    private String getTime(Date date){
        SimpleDateFormat format0 = new SimpleDateFormat("yyyyMMddhhmmss");
        String time = format0.format(date);
        return time;
    }

    private Image getImage(String buffer, String imgName){
        Image img=new Image();
        img.setBaiduLatitude(getLocation(uWaybill.getTolat()));
        img.setBaiduLongitude(getLocation(uWaybill.getTolong()));
        img.setFileExt("jpg");
        img.setFileData("data:image/jpeg;base64,"+buffer);
        img.setImageTakenDate(getNowtime());
        img.setFileName(imgName);
        img.setLocation(uWaybill.getToLocation());
        String getImage=getLocation(uWaybill.getTolat())+";"+getLocation(uWaybill.getTolong());
        Log.e("getImage","getImage==="+getImage);
        return img;

    }

    private double getLocation(String loc){
        return Double.parseDouble(loc);
    }

    //图片压缩
    private Bitmap compressImg(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
        BitmapFactory.decodeFile(imagePath, options);
        int h = options.outHeight;
        int w = options.outWidth;
        float hh = 1280f;//这里设置高度为1280f
        float ww = 720f;//这里设置宽度为720f
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (options.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (options.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
        options.inSampleSize = be; // 设置为刚才计算的压缩比例
        return BitmapFactory.decodeFile(imagePath, options); // 解码文件
    }

    private void setImg() {
        List<String> names = new ArrayList<>();
        names.add("拍照");
        // names.add("相册");
        showDialog(new SelectDialog.SelectDialogListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        isOthers=false;
                        Intent intent = new Intent(UpLoadImgActivity.this, ImageGridActivity.class);
                        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                        startActivityForResult(intent, REQUEST_CODE_SELECT);
                        break;
                    case 1:
                        Intent intent1 = new Intent(UpLoadImgActivity.this, ImageGridActivity.class);
                        startActivityForResult(intent1, REQUEST_CODE_SELECT);
                        break;
                    default:
                        break;
                }
            }
        }, names);
    }



    @Override
    public void onBackPressed() {
        if(isSuccessXHZ && isSuccessHDZ){
            if(isAlctUploadHDZ && isAlctUploadXHZ){
                super.onBackPressed();
            }else {
                confirmExit("请点击按钮上传图片");
            }

        }else {
            confirmExit("请上传图片");
        }

    }
    private void confirmExit(String msg) {
        //退出操作
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.show(getSupportFragmentManager(), "uploadImg");
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismiss();
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }

            @Override
            public void onClickOk() {
                dialog.dismiss();

            }
        });


    }
    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(this, R.style
                .transparentFrameWindowStyle,
                listener, names);
        if (!this.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();



    }



    class BitmapThread extends Thread {
        private String bitmapUrl;
        private String type;
        BitmapThread(String bitmapUrl,String type) {
            this.bitmapUrl = bitmapUrl;
            this.type=type;
        }
        @Override
        public void run() {
            Bitmap bitmap = null;
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(bitmapUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                }
                if(bitmap!=null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                    if(TYPE_HDZ.equals(type)){
                        isSuccessHDZ=true;
                        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,
                                50, baos2);
                        byte[] bytes = baos2.toByteArray();
                        String uploadBuffer = Base64.encodeToString(bytes, Base64.DEFAULT)
                                .replaceAll(" ","");
                        map.put(HDZ,uploadBuffer);
                    }else if(TYPE_XHZ.equals(type)){
                        isSuccessXHZ=true;
                        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,
                                50, baos2);
                        byte[] bytes = baos2.toByteArray();
                        String uploadBuffer = Base64.encodeToString(bytes, Base64.DEFAULT)
                                .replaceAll(" ","");
                        map.put(XHZ,uploadBuffer);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private void upAlctImgMsg(UWaybill waybill,String msg){
        Map<String,String> map=new HashMap<>();
        map.put("id",waybill.getId()+"");
        map.put("upimageMsg",msg);
        RequestManager.getInstance()
                .mServiceStore
                .upAlctImgMsg(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("upAlctImgMsg","upLoadOrderImg==="+msg);
                    }
                    @Override
                    public void onError(String msg) {
                        Log.e("upAlctImgMsg", msg);
                    }
                }));
    }

}
