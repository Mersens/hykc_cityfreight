package com.hykc.cityfreight.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.BankCardParams;
import com.baidu.ocr.sdk.model.BankCardResult;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.hykc.cityfreight.R;
import com.hykc.cityfreight.entity.UDriver;
import com.hykc.cityfreight.utils.FileUtil;
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
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AddBankActivity extends BaseActivity {
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;

    private TextView mTextSB;
    private ImageView mImgBack;
    private EditText mEditAccount;
    private EditText mEditName;
    private EditText mEditAddr;
    private EditText mEditUserName;
    private Button mBtnSave;
    private boolean hasGotToken=false;
    private static final int REQUEST_CODE_CAMERA = 102;
    private UDriver uDriver=null;
    private LinearLayout mLayoutImg;
    private ImageView mImgSFZ_Z;
    private ImageView mImgSFZ_F;
    private static int EDIT_OK=1001;
    private int selectType=-1;
    private int SFZ_Z=1;
    private int SFZ_F=2;
    private String sfz_z_url=null;
    private String sfz_f_url=null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_bank);
        init();
    }

    @Override
    public void init() {
        initImagePicker();
        initAccessToken();
        initView();
        initEvent();

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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (EDIT_OK == msg.what) {
                //输入完成
                String userName=uDriver.getDriverName();
                String inputName=mEditUserName.getText().toString();
                if(!userName.equals(inputName)){
                    if(mLayoutImg.getVisibility()==View.GONE){
                        mLayoutImg.setVisibility(View.VISIBLE);
                    }
                }else {
                    if(mLayoutImg.getVisibility()==View.VISIBLE){
                        mLayoutImg.setVisibility(View.GONE);
                    }
                }
            }
        }
    };
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.sendEmptyMessage(EDIT_OK);
        }
    };


    @Override
    protected void onDestroy() {

        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    private void initAccessToken() {
        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                Log.e("initAccessToken",error.getMessage());
            }
        }, getApplicationContext());
    }
    private void initEvent() {
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
        mTextSB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddBankActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_BANK_CARD);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
            }
        });
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSave();

            }
        });

        mImgSFZ_Z.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String account = mEditAccount.getText().toString().trim();
                if(TextUtils.isEmpty(account)){
                    Toast.makeText(AddBankActivity.this, "请先填写银行卡信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                selectType=SFZ_Z;
                setImg();
            }
        });
        mImgSFZ_F.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = mEditAccount.getText().toString().trim();
                if(TextUtils.isEmpty(account)){
                    Toast.makeText(AddBankActivity.this, "请先填写银行卡信息", Toast.LENGTH_SHORT).show();
                    return;
                }
                selectType=SFZ_F;
                setImg();
            }
        });
        mEditUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mHandler.removeCallbacks(mRunnable);
                //800毫秒没有输入认为输入完毕
                mHandler.postDelayed(mRunnable, 800);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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
                        Intent intent = new Intent(AddBankActivity.this, ImageGridActivity.class);
                        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                        startActivityForResult(intent, REQUEST_CODE_SELECT);
                        break;
                    case 1:
                        Intent intent1 = new Intent(AddBankActivity.this, ImageGridActivity.class);

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



    private void setImage(ImageItem item) {
        ImageView imageView = null;
        if (item == null) {
            return;
        }
        switch (selectType) {
            case 1:
                imageView = mImgSFZ_Z;
                break;
            case 2:
                imageView = mImgSFZ_F;
                break;
        }
        if (imageView != null) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImagePicker.getInstance().getImageLoader().displayImage(AddBankActivity.this, item.path, imageView, 0, 0);
            uploadImg(item.path);
        }
    }
    private void uploadImg(String path) {
        Bitmap bitmap = compressImg(path);
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,
                50, baos2);
        byte[] bytes = baos2.toByteArray();
        String uploadBuffer = Base64.encodeToString(bytes, Base64.DEFAULT).replaceAll("\\+","-");
        upLoadImgToService(uploadBuffer);
    }

    private void upLoadImgToService(String base64) {
        String account = mEditAccount.getText().toString().trim();
        if(TextUtils.isEmpty(account)){
            Toast.makeText(AddBankActivity.this, "请先填写银行卡信息", Toast.LENGTH_SHORT).show();
            return;
        }
        final LoadingDialogFragment dialogFragment=LoadingDialogFragment.getInstance();
        dialogFragment.showF(getSupportFragmentManager(),"uploadBankUserImg");
        Map<String,String> map=new HashMap<>();
        map.put("base64",base64);
        map.put("imgType",selectType+"");
        map.put("mobile",uDriver.getMobile());
        map.put("cardNum",account);
        RequestManager.getInstance()
                .mServiceStore
                .uploadBankUserImg(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        dialogFragment.dismissAllowingStateLoss();
                        try {
                            JSONObject object=new JSONObject(msg);
                            if(object.getBoolean("success")){
                                if(selectType==1){
                                    sfz_z_url=object.getString("url");
                                }else if(selectType==2){
                                    sfz_f_url=object.getString("url");
                                }
                                Toast.makeText(AddBankActivity.this, "上传成功！", Toast.LENGTH_SHORT).show();

                            }else {

                                Toast.makeText(AddBankActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("uploadBankUserImg","uploadBankUserImg==="+msg);
                    }
                    @Override
                    public void onError(String msg) {
                        dialogFragment.dismissAllowingStateLoss();
                        Toast.makeText(AddBankActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();
                        Log.e("uploadBankUserImg", msg);
                    }
                }));

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

    private void initView() {
        mLayoutImg=findViewById(R.id.layout_img);
        mImgSFZ_Z=findViewById(R.id.img_sfz_z);
        mImgSFZ_F=findViewById(R.id.img_sfz_f);
        mImgBack=findViewById(R.id.img_back);
        mTextSB=findViewById(R.id.tv_sb_bank);
        mEditAccount=findViewById(R.id.editAccount);
        mEditName=findViewById(R.id.editBank);
        mEditAddr=findViewById(R.id.editAddress);
        mBtnSave=findViewById(R.id.btn_ok);
        mEditUserName=findViewById(R.id.editName);
        String userinfo = SharePreferenceUtil.getInstance(this).getUserinfo();
        if(TextUtils.isEmpty(userinfo)){
            Toast.makeText(this, "用户信息为空，请重新登录", Toast.LENGTH_SHORT).show();
            return;
        }
        Gson gson=new Gson();
         uDriver= gson.fromJson(userinfo,UDriver.class);
         if(null==uDriver){
             Toast.makeText(this, "用户信息为空，请重新登录", Toast.LENGTH_SHORT).show();
             return;
         }
       // mEditUserName.setText(uDriver.getDriverName());
    }

    private void doSave() {
        String account = mEditAccount.getText().toString().trim();
        String bank=mEditName.getText().toString().trim();
        String address=mEditAddr.getText().toString().trim();
        String userName=mEditUserName.getText().toString().trim();
        String user=uDriver.getDriverName();
        if(!user.equals(userName)){
            if(mLayoutImg.getVisibility()==View.GONE){
                mLayoutImg.setVisibility(View.VISIBLE);
            }
            if(TextUtils.isEmpty(sfz_z_url)){
                Toast.makeText(this, "请上传身份证正面照", Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(sfz_f_url)){
                Toast.makeText(this, "请上传身份证反面照", Toast.LENGTH_SHORT).show();
                return;
            }

        }else {
            if(mLayoutImg.getVisibility()==View.VISIBLE){
                mLayoutImg.setVisibility(View.GONE);
            }
        }

        if (TextUtils.isEmpty(account)) {
            Toast.makeText(AddBankActivity.this, "账户不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(account.length()<4){
            Toast.makeText(AddBankActivity.this, "请输入正确账户！", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(bank)) {
            Toast.makeText(AddBankActivity.this, "银行名称不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(address)) {
            Toast.makeText(AddBankActivity.this, "开户行地址不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        String userid=SharePreferenceUtil.getInstance(this).getUserId();
        if (TextUtils.isEmpty(userid)) {
            Toast.makeText(AddBankActivity.this, "用户手机号为空！请重新登录", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(userName)){
            Toast.makeText(AddBankActivity.this, "用户姓名为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        uploadBankBandInfo(userid,account,bank,address,userName);
    }

    private void uploadBankBandInfo(String userid, String account, String bank, String address,String userName) {
        String type = "银行卡";
        final LoadingDialogFragment loadingDialog = LoadingDialogFragment.getInstance();
        loadingDialog.showF(getSupportFragmentManager(), "ZFBOrWXBandInfo");
        String useridentity_z_img="";//身份证正面
        String useridentity_f_img="";//身份证反面
        if(!TextUtils.isEmpty(sfz_z_url)){
            useridentity_z_img=sfz_z_url;
        }
        if(!TextUtils.isEmpty(sfz_f_url)){
            useridentity_f_img=sfz_f_url;
        }
        Map<String,String> map=new HashMap<>();
        map.put("cardType","3");
        map.put("name",userName);
        map.put("account",account);
        map.put("mobile",userid);
        map.put("address",address);
        map.put("bank",bank);
        map.put("useridentity_z_img",useridentity_z_img);
        map.put("useridentity_f_img",useridentity_f_img);
        RequestManager.getInstance()
                .mServiceStore
                .addCardInfo(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        loadingDialog.dismissAllowingStateLoss();
                        Log.e("uploadCardInfo", "====" + msg);
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject object=new JSONObject(msg);
                            if(object.getBoolean("success")){
                                Toast.makeText(AddBankActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                                finish();
                                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                            }else {
                                String error=object.getString("msg");
                                Toast.makeText(AddBankActivity.this, error, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(String msg) {
                        Toast.makeText(AddBankActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Log.e("onError", "====" + msg);
                        loadingDialog.dismissAllowingStateLoss();
                    }
                }));
    }

public void recBankCard(String filePath){
    BankCardParams param = new BankCardParams();
    param.setImageFile(new File(filePath));

// 调用银行卡识别服务
       OCR.getInstance(this).recognizeBankCard(param, new OnResultListener<BankCardResult>() {
        @Override
        public void onResult(BankCardResult result) {
            // 调用成功，返回BankCardResult对象
            if(null!=result){
                String bankName=result.getBankName();
                String bankCardNumber=result.getBankCardNumber();
                if(!TextUtils.isEmpty(bankName)){
                    mEditName.setText(bankName);
                }
                if(!TextUtils.isEmpty(bankCardNumber)){
                    mEditAccount.setText(bankCardNumber);
                }
            }
        }
        @Override
        public void onError(OCRError error) {
            // 调用失败，返回OCRError对象
        }
    });

}


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("resultCode","resultCode===="+requestCode);
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
                String filePath = FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath();
                if (!TextUtils.isEmpty(contentType)) {
                    if (CameraActivity.CONTENT_TYPE_BANK_CARD.equals(contentType)) {
                        recBankCard( filePath);
                    }
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> imgs = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (imgs != null ) {
                    setImage(imgs.get(0));
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                Log.e("requestCode","irequestCode===="+requestCode);
                ArrayList<ImageItem> imgs = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (imgs != null ) {
                    Log.e("imgs","imgs===="+imgs.size());
                    setImage(imgs.get(0));
                }else{
                    Log.e("imgs","imgs====null");
                }
            }
        }


    }


}
