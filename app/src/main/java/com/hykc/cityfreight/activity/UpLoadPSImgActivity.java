package com.hykc.cityfreight.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.entity.UWaybill;
import com.hykc.cityfreight.utils.GlideImageLoader;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.view.LoadingDialogFragment;
import com.hykc.cityfreight.view.SelectDialog;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class UpLoadPSImgActivity extends BaseActivity {
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    public static final int IMAGE_ITEM_ADD = -1;
    public static final int RESULT_OK=1002;
    private ImageView mImgBack;
    private UWaybill uWaybill;
    private ImageView mImg_thz;
    private boolean isSuccess=false;
    private TextView mTextTips;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_img);
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
        initImagePicker();
        mImgBack =  findViewById(R.id.img_back);
        uWaybill=(UWaybill)getIntent().getSerializableExtra("entity");
        mImg_thz=findViewById(R.id.img_thz);
        mImg_thz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImg();
            }
        });
        mTextTips=findViewById(R.id.tv_tips);
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> imgs = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (imgs != null && imgs.size() > 0) {
                    ImageItem imageItem=imgs.get(0);
                    upLoadImg(imageItem);
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                ArrayList<ImageItem> imgs = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (imgs != null && imgs.size() > 0) {
                    ImageItem imageItem=imgs.get(0);
                    upLoadImg(imageItem);
                }
            }
        }
    }

    private void upLoadImg(ImageItem imageItem){
        if (imageItem == null) {
            Toast.makeText(this, "请选择照片！", Toast.LENGTH_SHORT).show();
            return;
        }
        mImg_thz.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ImagePicker.getInstance().getImageLoader().displayImage(UpLoadPSImgActivity.this, imageItem.path, mImg_thz, 0, 0);
        Bitmap bitmap = compressImg(imageItem.path);
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,
                50, baos2);
        byte[] bytes = baos2.toByteArray();
        String uploadBuffer = Base64.encodeToString(bytes, Base64.DEFAULT).replaceAll(" ","").replaceAll("\\+","-");
        doSave(uploadBuffer);
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



    private void doSave(String uploadBuffer){
        //保存提货照片到服务器
        final LoadingDialogFragment dialogFragment=LoadingDialogFragment.getInstance();
        dialogFragment.showF(getSupportFragmentManager(),"doSave");
        Map<String, String> map = new HashMap<>();
        map.put("id", uWaybill.getId() + "");
        map.put("waybillId",uWaybill.getWaybillId()+"");
        map.put("fileName",  "PICKUP_IMG.jpg");
        map.put("base64",  uploadBuffer);
        RequestManager.getInstance()
                .mServiceStore
                .upLoadPickUpImg(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("updateUWaybillStatu", msg);
                        dialogFragment.dismissAllowingStateLoss();
                        try {
                            JSONObject object = new JSONObject(msg);
                            if (object.getBoolean("success")) {
                                mTextTips.setVisibility(View.GONE);
                                Toast.makeText(UpLoadPSImgActivity.this, "上传成功！", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent();
                                intent.putExtra("entity", uWaybill);
                                setResult(RESULT_OK,intent);
                                finish();
                                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                            } else {
                                mTextTips.setVisibility(View.VISIBLE);
                                Toast.makeText(UpLoadPSImgActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                    @Override
                    public void onError(String msg) {
                        mTextTips.setVisibility(View.VISIBLE);
                        dialogFragment.dismissAllowingStateLoss();
                        Toast.makeText(UpLoadPSImgActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();
                    }
                }));

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
                        Intent intent = new Intent(UpLoadPSImgActivity.this, ImageGridActivity.class);
                        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                        startActivityForResult(intent, REQUEST_CODE_SELECT);
                        break;
                    case 1:
                        Intent intent1 = new Intent(UpLoadPSImgActivity.this, ImageGridActivity.class);
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
    private String getTime(Date date){
        SimpleDateFormat format0 = new SimpleDateFormat("yyyyMMddhhmmss");
        String time = format0.format(date);
        return time;
    }







}
