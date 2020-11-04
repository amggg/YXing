怎么集成：
1.在根目录的build.gradle中添加jitpack依赖：

allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}

2.在project的build.gradle中添加YXing依赖：

  implementation 'com.github.amggg:YXing:V1.0.1'

功能：
1、扫码功能。
2、生成二维码和带logo二维码 （大小圆角可设置）。
3、识别相册内二维码图片。

注意事项：
1.在进入扫码界面前， 自行动态请求相机权限。
2.minSdk >= 21 (android5.0 及以上)

如何使用：
1.简单调用：

  ScanCodeConfig.create(MainActivity.this)
                                    //设置扫码页样式 ScanStyle.NONE：无  ScanStyle.QQ ：仿QQ样式   ScanStyle.WECHAT ：仿微信样式
                                    .setStyle(style)
                                    //扫码成功是否播放音效  true ： 播放   false ： 不播放
                                    .setPlayAudio(false)
                                    .buidler()
                                    //跳转扫码页   扫码页可自定义样式
                                    .start(ScanCodeActivity.class);

获取扫码结果：

  @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //接收扫码结果
        if(resultCode == RESULT_OK && requestCode == ScanCodeConfig.QUESTCODE && data != null){
            Bundle extras = data.getExtras();
            if(extras != null){
                String code = extras.getString(ScanCodeConfig.CODE_KEY);
                tvCode.setText(String.format("%s%s", "结果： " , code));
            }
        }
    }

内置两种样式可供使用， 通过setStyle方法 设置。


自定义扫码界面流程：
1.新建Activity 继承 ScanCodeActivity

public class MyScanActivity extends ScanCodeActivity 

2.重写getLayoutId() 和 initData() 方法
getLayoutId返回你自己定义的布局文件id
initData() 和平常一样 初始化数据 监听等等

public class MyScanActivity extends ScanCodeActivity {

    private AppCompatButton btnOpenFlash;

    @Override
    public int getLayoutId() {
        return R.layout.activity_myscan;
    }

    @Override
    public void initData() {
        super.initData();
        btnOpenFlash = findViewById(R.id.btn_openflash);

        btnOpenFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MyScanActivity.this, "打开闪光灯", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

3.布局文件中先将下面代码复制进去：

<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/rlparent"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    <androidx.camera.view.PreviewView
        android:id="@+id/pvCamera"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
    <!--开始自定义界面-->

</RelativeLayout>

PreviewView是扫码界面， 下面可以任意添加自己的布局了：
下面尝试一下：

<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/rlparent"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <androidx.camera.view.PreviewView
        android:id="@+id/pvCamera"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

    <!--开始自定义界面-->

    <androidx.appcompat.widget.AppCompatButton
        android:id="@+id/btn_openflash"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="打开闪光灯"
        android:layout_alignParentBottom="true"
        />

</RelativeLayout>

4.start()方法参数 替换成自定义的Activity:

ScanCodeConfig.create(MainActivity.this)
                                    //设置扫码页样式 ScanStyle.NONE：无  ScanStyle.QQ ：仿QQ样式   ScanStyle.WECHAT ：仿微信样式
                                    .setStyle(style)
                                    //扫码成功是否播放音效  true ： 播放   false ： 不播放
                                    .setPlayAudio(true)
                                    .buidler()
                                    //跳转扫码页   扫码页可自定义样式
                                    .start(MyScanActivity.class);

好了完成了。


除了扫码功能外，还可以生成二维码：
1.单独的二维码：
Bitmap bitmap = ScanCodeConfig.createQRCode("star");

2.带logo的二维码：
Bitmap bitmap = ScanCodeConfig.createQRcodeWithLogo("star", BitmapFactory.decodeResource(getResources(), R.mipmap.timg));


二维码宽高， logo宽高， 圆角都可以自行设置：

/**
     * 生成二维码
     *
     * @param text 需要生成二维码的文字、网址等
     * @param size 需要生成二维码的大小（）
     * @return bitmap
     */
    public static Bitmap createQRCode(String text, int size) {

 /** 生成带logo 二维码
     * @param text  文字
     * @param size   二维码大小 1 ：1
     * @param logo   logo
     * @param logoWith logo宽
     * @param logoHigh  logo高
     * @param logoRaduisX  logo x圆角
     * @param logoRaduisY  logo y圆角
     * @return
     */
    public static Bitmap createQRcodeWithLogo(String text, int size, Bitmap logo, int logoWith, int logoHigh, float logoRaduisX, float logoRaduisY){

除了生成二维码， 从相册识别二维码也是必不可少的：
调用以下方法， 把选中的图片uri传进去 就可以获取到二维码的内容了。。

 /**
     * 解码uri二维码图片
     * @return
     */
    public static String scanningImage(Activity mActivity, Uri uri) {

打完 收工~~~
