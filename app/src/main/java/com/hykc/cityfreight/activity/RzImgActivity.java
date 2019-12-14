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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hykc.cityfreight.R;
import com.hykc.cityfreight.app.Constants;
import com.hykc.cityfreight.entity.UDriver;
import com.hykc.cityfreight.utils.GlideImageLoader;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.utils.SharePreferenceUtil;
import com.hykc.cityfreight.view.LoadingDialogFragment;
import com.hykc.cityfreight.view.SelectDialog;
import com.google.gson.Gson;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class RzImgActivity extends BaseActivity implements View.OnClickListener {
    public static final int SFZ_Z = 1;//身份证正面
    public static final int SFZ_F = 2;//身份证反面
    public static final int JSZ = 3;//驾驶证
    public static final int XSZ = 4;//行驶证
    public static final int DLYSZ = 5;//道路运输证
    public static final int CYZGZ = 6;//从业资格证
    public static final int XSZ_Z = 7;//行驶证副页正
    public static final int XSZ_F = 8;//行驶证副页反
    public static final int GCZZY = 9;//挂车证主页
    public static final int GCZFY_Z = 10;//挂车证副页正
    public static final int GCZFY_F = 11;//挂车证副页反
    public static final int DLYSZ_GS=12;//道路运输经营许可证
    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    private final LoadingDialogFragment loadView = LoadingDialogFragment.getInstance();
/*    public  boolean isSFZ_Z = false;//身份证正面是否成功
    public  boolean isSFZ_F = false;//身份证反面是否成功
    public  boolean isJSZ = false;//驾驶证是否成功
    public  boolean isXSZ = false;//行驶证是否成功
    public  boolean isDLYSZ = false;//道路运输证是否成功
    public  boolean isCYZGZ = false;//从业资格证是否成功
    public  boolean isXSZ_Z = false;//行驶证副页正是否成功
    public  boolean isXSZ_F = false;//行驶证副页反是否成功
    public  boolean isGCZZY = false;//挂车证主页是否成功
    public boolean isGCZFY_Z = false;//挂车证副页正是否成功
    public boolean isGCZFY_F = false;//挂车证副页反是否成功*/
    private int selectType = -1;
    private ImageView mImgBack;
    private UDriver entity = new UDriver();
    private ImageView mImgCard_Z;
    private ImageView mImgCard_F;
    private ImageView mImgJsz;
    private ImageView mImgXsz;
    private ImageView mImgDlysz;
    private ImageView mImgCyzgz;
    private ImageView mImgXSZ_Z;
    private ImageView mImgXSZ_F;
    private ImageView mImgXSZFB;
    private ImageView mImgGCZL;
    private ImageView mImgGCZLFYF;
    private ImageView mImgDLYSJYXKZ_GS;
    private Button mBtnOk;
    private String id;
    private Map<String, String> textMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_rz_img);
        initImagePicker();
        init();
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

    @Override
    public void init() {
        id=SharePreferenceUtil.getInstance(this).getUserId();
        String userinfo=SharePreferenceUtil.getInstance(RzImgActivity.this).getUserinfo();
        try {
            JSONObject object = new JSONObject(userinfo);
            String mobile=object.getString("mobile");
            if(TextUtils.isEmpty(mobile)){
                Toast.makeText(this, "mobile为空，请重新登录！", Toast.LENGTH_SHORT).show();
                return;
            }
            entity.setMobile(mobile);
            int id=object.getInt("id");
            if(id==0){
                Toast.makeText(this, "id为空，请重新登录！", Toast.LENGTH_SHORT).show();
                return;
            }
            entity.setId(id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        initViews();
        initEvent();
        getUserRzInfo();

    }

    private void getUserRzInfo(){
        if(TextUtils.isEmpty(id)){
            Toast.makeText(this, "id为空，请重新登录！", Toast.LENGTH_SHORT).show();
            return;
        }
        String userinfo=SharePreferenceUtil.getInstance(RzImgActivity.this).getUserinfo();
        try {
            JSONObject object = new JSONObject(userinfo);
            String token=object.getString("token");
            if(TextUtils.isEmpty(token)){
                Toast.makeText(this, "token为空，请重新登录！", Toast.LENGTH_SHORT).show();
                return;
            }
            Map<String,String> map=new HashMap<>();
            map.put("mobile",id);
            map.put("token",token);
            RequestManager.getInstance()
                    .mServiceStore
                    .getDirverInfo(map)
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                        @Override
                        public void onSuccess(String msg) {
                            Log.e("getDirverInfo onSuccess", msg);
                            analysisJson(msg);
                        }

                        @Override
                        public void onError(String msg) {
                            Log.e("getDirverInfo onError", msg);
                        }
                    }));
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    private void analysisJson(String msg) {
        try {
            JSONObject object=new JSONObject(msg);
            if(object.getBoolean("success")){
                String str=object.getString("entity");
                Gson gson=new Gson();
                UDriver uDriver= gson.fromJson(str,UDriver.class);

                if(null!=uDriver){

                    downLoadImg(uDriver);

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private void initViews() {
        mImgBack=findViewById(R.id.img_back);
        mImgXSZ_Z = findViewById(R.id.img_xsz_z);
        mImgXSZ_F = findViewById(R.id.img_xsz_f);
        mImgXSZFB = findViewById(R.id.img_xszfb);
        mImgGCZL = findViewById(R.id.img_gczl);
        mImgCard_Z = findViewById(R.id.img_card_z);
        mImgCard_F = findViewById(R.id.img_card_f);
        mImgJsz = findViewById(R.id.img_jsz);
        mImgXsz = findViewById(R.id.img_xsz);
        mImgDlysz = findViewById(R.id.img_dlysz);
        mImgCyzgz = findViewById(R.id.img_cyzgz);
        mBtnOk = findViewById(R.id.btn_ok);
        mImgGCZLFYF=findViewById(R.id.img_gczfyf);
        mImgDLYSJYXKZ_GS=findViewById(R.id.img_dlysxkz_gs);
    }

    private void initEvent() {
        mImgCard_Z.setOnClickListener(this);
        mImgCard_F.setOnClickListener(this);
        mImgJsz.setOnClickListener(this);
        mImgXsz.setOnClickListener(this);
        mImgDlysz.setOnClickListener(this);
        mImgCyzgz.setOnClickListener(this);
        mBtnOk.setOnClickListener(this);
        mImgXSZ_Z.setOnClickListener(this);
        mImgXSZ_F.setOnClickListener(this);
        mImgXSZFB.setOnClickListener(this);
        mImgGCZL.setOnClickListener(this);
        mImgGCZLFYF.setOnClickListener(this);
        mImgDLYSJYXKZ_GS.setOnClickListener(this);

        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
    }

    private String getUrl(String name){
        if(TextUtils.isEmpty(name)){
            return null;
        }
        if(name.contains("driver_picture/"+id+"/")){
            name=name.replaceAll("driver_picture/"+id+"/","");
        }
        return Constants.WEBSERVICE_URL+"/driver_picture/"+id+"/"+name;
    }

    private void downLoadImg(UDriver uDriver){
        entity.setSfz_z_url(uDriver.getSfz_z_url());
        entity.setSfz_f_url(uDriver.getSfz_f_url());
        entity.setJsz_url(uDriver.getJsz_url());
        entity.setXsz_url(uDriver.getXsz_url());
        entity.setDlysz_url(uDriver.getDlysz_url());
        entity.setCyzgz_url(uDriver.getCyzgz_url());
        entity.setXsz_z_url(uDriver.getXsz_z_url());
        entity.setXsz_f_url(uDriver.getXsz_f_url());
        entity.setGczzy_url(uDriver.getGczzy_url());
        entity.setGczfy_z_url(uDriver.getGczfy_z_url());
        entity.setGczfy_f_url(uDriver.getGczfy_f_url());
        entity.setDlysjyxkz_gs(uDriver.getDlysjyxkz_gs());
        if(!TextUtils.isEmpty(uDriver.getSfz_z_url())){
            Glide.with(this)
                    .load(getUrl(uDriver.getSfz_z_url()))
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgCard_Z);
        }
        if(!TextUtils.isEmpty(uDriver.getSfz_f_url())){
            Glide.with(this)
                    .load(getUrl(uDriver.getSfz_f_url()))
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgCard_F);
        }
        if(!TextUtils.isEmpty(uDriver.getJsz_url())){
            Glide.with(this)
                    .load(getUrl(uDriver.getJsz_url()))
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgJsz);
        }
        if(!TextUtils.isEmpty(uDriver.getXsz_url())){
            Glide.with(this)
                    .load(getUrl(uDriver.getXsz_url()))
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgXsz);
        }
        if(!TextUtils.isEmpty(uDriver.getDlysz_url())){
            Glide.with(this)
                    .load(getUrl(uDriver.getDlysz_url()))
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgDlysz);
        }
        if(!TextUtils.isEmpty(uDriver.getCyzgz_url())){

            Glide.with(this)
                    .load(getUrl(uDriver.getCyzgz_url()))
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgCyzgz);
        }
        if(!TextUtils.isEmpty(uDriver.getXsz_z_url())){
            Glide.with(this)
                    .load(getUrl(uDriver.getXsz_z_url()))
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgXSZ_Z);
        }
        if(!TextUtils.isEmpty(uDriver.getXsz_f_url())){
            Glide.with(this)
                    .load(getUrl(uDriver.getXsz_f_url()))
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgXSZ_F);
        }
        if(!TextUtils.isEmpty(uDriver.getGczzy_url())){

            Glide.with(this)
                    .load(getUrl(uDriver.getGczzy_url()))
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgXSZFB);
        }
        if(!TextUtils.isEmpty(uDriver.getGczzy_url())){
            Glide.with(this)
                    .load(getUrl(uDriver.getGczzy_url()))
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgGCZL);
        }
        if(!TextUtils.isEmpty(uDriver.getGczfy_f_url())){
            Glide.with(this)
                    .load(getUrl(uDriver.getGczfy_f_url()))
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgGCZLFYF);
        }
        if(!TextUtils.isEmpty(uDriver.getDlysjyxkz_gs())){
            Glide.with(this)
                    .load(getUrl(uDriver.getDlysjyxkz_gs()))
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgDLYSJYXKZ_GS);
        }


    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.img_card_z:
                selectType = SFZ_Z;
                setImg();
                break;
            case R.id.img_card_f:
                selectType = SFZ_F;
                setImg();
                break;
            case R.id.img_jsz:
                selectType = JSZ;
                setImg();
                break;
            case R.id.img_xsz:
                selectType = XSZ;
                setImg();
                break;
            case R.id.img_dlysz:
                selectType = DLYSZ;
                setImg();
                break;
            case R.id.img_cyzgz:
                selectType = CYZGZ;
                setImg();
                break;
            case R.id.btn_ok:
                doSave();
                break;
            case R.id.img_xsz_z:
                selectType = XSZ_Z;
                setImg();
                break;
            case R.id.img_xsz_f:
                selectType = XSZ_F;
                setImg();
                break;
            case R.id.img_xszfb:
                selectType = GCZZY;
                setImg();
                break;
            case R.id.img_gczl:
                selectType = GCZFY_Z;
                setImg();
                break;
            case R.id.img_gczfyf:
                selectType = GCZFY_F;
                setImg();
                break;
            case R.id.img_dlysxkz_gs:
                selectType = DLYSZ_GS;
                setImg();
                break;
        }

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
        String type =  getTime(new Date())+".jpg";
        ImageView imageView = null;
        if (item == null) {
            return;
        }
        if(TextUtils.isEmpty(item.path)){
            return;
        }
        switch (selectType) {

            case SFZ_Z:
                imageView = mImgCard_Z;
                break;
            case SFZ_F:
                imageView = mImgCard_F;
                break;
            case JSZ:
                imageView = mImgJsz;
                break;
            case XSZ:
                imageView = mImgXsz;
                break;
            case DLYSZ:
                imageView = mImgDlysz;
                break;
            case CYZGZ:
                imageView = mImgCyzgz;
                break;
            case XSZ_Z:
                imageView = mImgXSZ_Z;
                break;
            case XSZ_F:
                imageView = mImgXSZ_F;
                break;
            case GCZZY:
                imageView = mImgXSZFB;
                break;
            case GCZFY_Z:
                imageView = mImgGCZL;
                break;
            case GCZFY_F:
                imageView = mImgGCZLFYF;
                break;
            case DLYSZ_GS:
                imageView=mImgDLYSJYXKZ_GS;
                break;
        }
        if (imageView != null) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImagePicker.getInstance().getImageLoader().displayImage(RzImgActivity.this, item.path, imageView, 0, 0);
            saveDataToMap(type,item.path);
        }
    }

    private void uploadImg(String fileName,int type,String uploadBuffer) {
        upLoadImg(fileName,type,uploadBuffer);

    }




    private void saveDataToMap(String fileName,String path) {
        Bitmap bitmap = compressImg(path);
        String ImgBuffer = bitmapToBase64(bitmap);
        String uploadBuffer=ImgBuffer.replaceAll("\\+","-");
        if(TextUtils.isEmpty(uploadBuffer)){
            Toast.makeText(this, "照片为空，请重新选择！", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (selectType) {
            case SFZ_Z:
                uploadImg(fileName,SFZ_Z,uploadBuffer);
                break;
            case SFZ_F:

                uploadImg(fileName,SFZ_F,uploadBuffer);
                break;
            case JSZ:

                uploadImg(fileName,JSZ,uploadBuffer);
                break;
            case XSZ:

                uploadImg(fileName,XSZ,uploadBuffer);
                break;
            case DLYSZ:

                uploadImg(fileName,DLYSZ,uploadBuffer);
                break;
            case CYZGZ:

                uploadImg(fileName,CYZGZ,uploadBuffer);
                break;
            case XSZ_Z:

                uploadImg(fileName,XSZ_Z,uploadBuffer);
                break;
            case XSZ_F:

                uploadImg(fileName,XSZ_F,uploadBuffer);
                break;
            case GCZZY:

                uploadImg(fileName,GCZZY,uploadBuffer);
                break;
            case GCZFY_Z:

                uploadImg(fileName,GCZFY_Z,uploadBuffer);
                break;
            case GCZFY_F:

                uploadImg(fileName,GCZFY_F,uploadBuffer);
                break;
            case DLYSZ_GS:
                uploadImg(fileName,DLYSZ_GS,uploadBuffer);
                break;
        }
    }
    private String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT).replaceAll(" ", "");
                try {
                    if (baos != null) {
                        baos.flush();
                        baos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        return result;
    }

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

    private void doSave() {
        if (entity == null) {
            Toast.makeText(this, "基本信息为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (entity.getId()==0) {
            Toast.makeText(this, "用户信息为空，请重新登录！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(entity.getMobile())) {
            Toast.makeText(this, "手机号为空，请重新登录！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(entity.getSfz_z_url())) {
            Toast.makeText(this, "请选择身份证正面照！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(entity.getSfz_f_url())) {
            Toast.makeText(this, "请选择身份证反面照！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(entity.getJsz_url())) {
            Toast.makeText(this, "请选择驾驶证照片！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(entity.getXsz_url())) {
            Toast.makeText(this, "请选择行驶证照片！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(entity.getDlysz_url())) {
            Toast.makeText(this, "请选择道路运输证照片！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(entity.getCyzgz_url())) {
            Toast.makeText(this, "请选择从业资格证照片！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(entity.getXsz_z_url())) {
            Toast.makeText(this, "请选择行驶证副页正面照片！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(entity.getXsz_f_url())) {
            Toast.makeText(this, "请选择行驶证副页反面照片！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(entity.getGczzy_url())) {
            Toast.makeText(this, "请选择行挂车证主页照片！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(entity.getGczfy_z_url())) {
            Toast.makeText(this, "请选择挂车证副页正照片！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(entity.getGczfy_f_url())) {
            Toast.makeText(this, "请选择挂车证副页反照片！", Toast.LENGTH_SHORT).show();
            return;
        }


        final LoadingDialogFragment loadingDialogFragment=LoadingDialogFragment.getInstance();
        loadingDialogFragment.showF(getSupportFragmentManager(),"saveView");
        Map<String,String> map=new HashMap<>();
        map.put("id",entity.getId()+"");
        map.put("mobile",entity.getMobile());
        map.put("sfz_z_url",entity.getSfz_z_url());
        map.put("sfz_f_url",entity.getSfz_f_url());
        map.put("jsz_url",entity.getJsz_url());
        map.put("xsz_url",entity.getXsz_url());
        map.put("dlysz_url",entity.getDlysz_url());
        map.put("cyzgz_url",entity.getCyzgz_url());
        map.put("xsz_z_url",entity.getXsz_z_url());
        map.put("xsz_f_url",entity.getXsz_f_url());
        map.put("gczzy_url",entity.getGczzy_url());
        map.put("gczfy_z_url",entity.getGczfy_z_url());
        map.put("gczfy_f_url",entity.getGczfy_f_url());
        if(TextUtils.isEmpty(entity.getDlysjyxkz_gs())){
            entity.setDlysjyxkz_gs("");
        }
        map.put("dlysjyxkz_gs",entity.getDlysjyxkz_gs());
        RequestManager.getInstance()
                .mServiceStore
                .updateDriverImg(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String s) {
                        loadingDialogFragment.dismissAllowingStateLoss();
                        try {
                            JSONObject jsonObject=new JSONObject(s);
                            if(jsonObject.getBoolean("success")){
                                Toast.makeText(RzImgActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
                                mBtnOk.setEnabled(false);
                                mBtnOk.setClickable(false);
                                mBtnOk.setBackgroundResource(R.drawable.btn_unselect_bg);
                                Intent intent = new Intent(RzImgActivity.this, MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                            }else {
                                String error=jsonObject.getString("msg");
                                Toast.makeText(RzImgActivity.this, "上传失败！"+error, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e("upLoadImgToService", msg);
                        Toast.makeText(RzImgActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();
                        loadingDialogFragment.dismissAllowingStateLoss();
                    }
                }));

    }

    private String getTime(Date date){
        SimpleDateFormat format0 = new SimpleDateFormat("yyyyMMddhhmmss");
        String time = format0.format(date);
        return time;
    }
    private void upLoadImg(final String fileName,final int imgType,String uploadBuffer){
        loadView.show(getSupportFragmentManager(), "uploadLoading");
        Map<String, String> m = new HashMap<>();
        m.put("mobile", entity.getMobile());
        m.put("fileName", fileName);
        m.put("base64", uploadBuffer);
        m.put("id",entity.getId()+"");
        m.put("imgType",imgType+"");
        RequestManager.getInstance()
                .mServiceStore
                .upLoadReImg(m)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String s) {
                        if (loadView != null) {
                            loadView.dismiss();
                        }
                        try {
                            JSONObject jsonObject=new JSONObject(s);
                            if(jsonObject.getBoolean("success")){
                                Toast.makeText(RzImgActivity.this, "上传成功！", Toast.LENGTH_SHORT).show();
                                String url=jsonObject.getString("entity");
                                setPicUrl(imgType,url);
                            }else {
                                String error=jsonObject.getString("message");
                                Toast.makeText(RzImgActivity.this, "上传失败！"+error, Toast.LENGTH_SHORT).show();
                            }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e("upLoadImgToService", msg);
                        Toast.makeText(RzImgActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();
                        if (loadView != null) {
                            loadView.dismiss();
                        }
                    }
                }));
    }

    private void setPicUrl(int imgType,String url) {
        if(entity==null){
            return;
        }
        switch (imgType) {
            case SFZ_Z:
              entity.setSfz_z_url(url);
                break;
            case SFZ_F:
                entity.setSfz_f_url(url);
                break;
            case JSZ:
               entity.setJsz_url(url);
                break;
            case XSZ:
               entity.setXsz_url(url);
                break;
            case DLYSZ:
                entity.setDlysz_url(url);
                break;
            case CYZGZ:
               entity.setCyzgz_url(url);
                break;
            case XSZ_Z:
               entity.setXsz_z_url(url);
                break;
            case XSZ_F:
               entity.setXsz_f_url(url);
                break;
            case GCZZY:
              entity.setGczzy_url(url);
                break;
            case GCZFY_Z:
               entity.setGczfy_z_url(url);
                break;
            case GCZFY_F:
               entity.setGczfy_f_url(url);
                break;
            case DLYSZ_GS:
                entity.setDlysjyxkz_gs(url);
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setImg() {
        List<String> names = new ArrayList<>();
        names.add("拍照");
        names.add("相册");
        showDialog(new SelectDialog.SelectDialogListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        ImagePicker.getInstance().setSelectLimit(1);
                        Intent intent = new Intent(RzImgActivity.this, ImageGridActivity.class);
                        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                        startActivityForResult(intent, REQUEST_CODE_SELECT);
                        break;
                    case 1:
                        ImagePicker.getInstance().setSelectLimit(1);
                        Intent intent1 = new Intent(RzImgActivity.this, ImageGridActivity.class);
                        startActivityForResult(intent1, REQUEST_CODE_SELECT);
                        break;
                    default:
                        break;
                }
            }
        }, names);
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
}
