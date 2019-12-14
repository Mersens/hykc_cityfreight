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

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.entity.UCardEntity;
import com.hykc.cityfreight.utils.GlideImageLoader;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.utils.SharePreferenceUtil;
import com.hykc.cityfreight.view.ExitDialogFragment;
import com.hykc.cityfreight.view.LoadingDialogFragment;
import com.hykc.cityfreight.view.SelectDialog;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UpLoadBankUserImgActivity extends BaseActivity {
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    private ImageView mImgBack;
    private Button mBtn;
    private ImageView mImgSFZ_Z;
    private ImageView mImgSFZ_F;
    private TextView mTextSFZ_Z_Tips;
    private TextView mTextSFZ_F_Tips;
    private int selectType=-1;
    private int SFZ_Z=1;
    private int SFZ_F=2;
    private String sfz_z_url=null;
    private String sfz_f_url=null;
    private boolean is_sfz_z_success=false;
    private boolean is_sfz_f_success=false;
    private UCardEntity entity=null;
    private int selectIndex=0;
    private String userid=null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_upload_bank_user_img);
        init();
    }

    @Override
    public void init() {
        userid= SharePreferenceUtil.getInstance(this).getUserId();
        entity= (UCardEntity) getIntent().getSerializableExtra("entity");
        selectIndex=getIntent().getIntExtra("index",0);
        initImagePicker();
        initViews();
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
    private void initEvent() {
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
        mImgSFZ_Z.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(entity==null){
                    Toast.makeText(UpLoadBankUserImgActivity.this, "银行卡信息为空！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                selectType=SFZ_Z;
                setImg();

            }
        });
        mImgSFZ_F.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(entity==null){
                    Toast.makeText(UpLoadBankUserImgActivity.this, "银行卡信息为空！",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                selectType=SFZ_F;
                setImg();
            }
        });
    }

    private void initViews() {
        mImgBack=findViewById(R.id.img_back);
        mBtn=findViewById(R.id.btn_ok);
        mImgSFZ_Z=findViewById(R.id.img_sfz_z);
        mImgSFZ_F=findViewById(R.id.img_sfz_f);
        mTextSFZ_Z_Tips=findViewById(R.id.tv_sfz_z_tips);
        mTextSFZ_F_Tips=findViewById(R.id.tv_sfz_f_tips);
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

                        Intent intent = new Intent(UpLoadBankUserImgActivity.this, ImageGridActivity.class);
                        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                        startActivityForResult(intent, REQUEST_CODE_SELECT);
                        break;
                    case 1:
                        Intent intent1 = new Intent(UpLoadBankUserImgActivity.this, ImageGridActivity.class);
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
            ImagePicker.getInstance().getImageLoader().displayImage(UpLoadBankUserImgActivity.this,
                    item.path, imageView, 0, 0);
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

        if(TextUtils.isEmpty(userid)){
            userid="";
        }
        final LoadingDialogFragment dialogFragment=LoadingDialogFragment.getInstance();
        dialogFragment.showF(getSupportFragmentManager(),"uploadBankUserImg");
        Map<String,String> map=new HashMap<>();
        map.put("base64",base64);
        map.put("imgType",selectType+"");
        map.put("mobile",userid);
        map.put("cardNum",entity.getAccount());
        map.put("id",entity.getId()+"");
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
                                    entity.setUseridentity_z_img(sfz_z_url);
                                    is_sfz_z_success=true;
                                    mTextSFZ_Z_Tips.setText("上传成功！");
                                    mTextSFZ_Z_Tips.setTextColor(getResources().getColor(R.color.colorAccent));
                                }else if(selectType==2){
                                    sfz_f_url=object.getString("url");
                                    entity.setUseridentity_f_img(sfz_f_url);
                                    is_sfz_f_success=true;
                                    mTextSFZ_F_Tips.setText("上传成功！");
                                    mTextSFZ_F_Tips.setTextColor(getResources().getColor(R.color.colorAccent));
                                }
                                Toast.makeText(UpLoadBankUserImgActivity.this, "上传成功！", Toast.LENGTH_SHORT).show();

                            }else {

                                Toast.makeText(UpLoadBankUserImgActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("uploadBankUserImg","uploadBankUserImg==="+msg);
                    }
                    @Override
                    public void onError(String msg) {
                        dialogFragment.dismissAllowingStateLoss();
                        Toast.makeText(UpLoadBankUserImgActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();
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



    private void onBack(){
        if(is_sfz_f_success && is_sfz_z_success){
            //上传成功
            Intent data=new Intent();
            data.putExtra("index",selectIndex);
            data.putExtra("entity",entity);
            setResult(RESULT_OK,data);
            finish();
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        }else {
            //上传失败
            showTipsView("照片未上传成功！"+"确定退出？");

        }

    }

    private void showTipsView(String msg){
        final ExitDialogFragment exitDialogFragment=ExitDialogFragment.getInstance(msg);
        exitDialogFragment.showF(getSupportFragmentManager(),"showTipsView");
        exitDialogFragment.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                exitDialogFragment.dismissAllowingStateLoss();
            }

            @Override
            public void onClickOk() {
                exitDialogFragment.dismissAllowingStateLoss();
                Intent data=new Intent();
                data.putExtra("index",selectIndex);
                data.putExtra("entity",entity);
                setResult(RESULT_OK,data);
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

            }
        });


    }


}
