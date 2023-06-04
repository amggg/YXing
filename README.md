怎么集成:

1、在根目录的build.gradle中添加jitpack依赖：

```
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

2、在project的build.gradle中添加YXing依赖：

```
  implementation 'com.github.amggg:YXing:releaseVersion'
```

简单调用：

Activity中启动：

```
  ScanCodeConfig.create(MainActivity.this)
                                    //设置扫码页样式 ScanStyle.NONE：无  ScanStyle.QQ ：仿QQ样式   ScanStyle.WECHAT ：仿微信样式    ScanStyle.CUSTOMIZE ： 自定义样式
                                    .setStyle(style)
                                    //扫码成功是否播放音效  true ： 播放   false ： 不播放
                                    .setPlayAudio(false)
                                    .buidler()
                                    //跳转扫码页   扫码页可自定义样式
                                    .start(ScanCodeActivity.class);
```

Fragment中启动：

```
  ScanCodeConfig.create(MainActivity.this, mFragment)
                                    //设置扫码页样式 ScanStyle.NONE：无  ScanStyle.QQ ：仿QQ样式   ScanStyle.WECHAT ：仿微信样式    ScanStyle.CUSTOMIZE ： 自定义样式
                                    .setStyle(style)
                                    //扫码成功是否播放音效  true ： 播放   false ： 不播放
                                    .setPlayAudio(false)
                                    .buidler()
                                    //跳转扫码页   扫码页可自定义样式
                                    .start(ScanCodeActivity.class);
```

全部参数：

```

    ScanCodeConfig.create(MainActivity.this)
                                    //设置扫码页样式 ScanStyle.NONE：无  ScanStyle.QQ ：仿QQ样式   ScanStyle.WECHAT ：仿微信样式  ScanStyle.CUSTOMIZE ： 自定义样式
                                    .setStyle(ScanStyle.CUSTOMIZE)
                                    //扫码成功是否播放音效  true ： 播放   false ： 不播放
                                    .setPlayAudio(true)
                                    //设置音效音频
                                    .setAudioId(R.raw.beep)
                                    ////////////////////////////////////////////
                                    //以下配置 在style为 ScanStyle.CUSTOMIZE 时生效
                                    //设置扫码框位置  left ： 边框左边位置   top ： 边框上边位置   right ： 边框右边位置   bottom ： 边框下边位置   单位/dp
//                                    .setScanRect(new ScanRect(50, 200, 300, 450), false)
                                    //是否限制识别区域为设定扫码框大小  true:限制  false：不限制   默认false：识别区域为整个屏幕
                                    .setLimitRect(true)
                                    //设置扫码框位置 scanSize : 扫码框大小   offsetX ： x轴偏移量    offsetY ：y轴偏移量   单位 /px
                                    .setScanSize(600, 0, 0)
                                    //是否显示边框上四个角标 true ： 显示  false ： 不显示
                                    .setShowFrame(true)
                                    //设置边框上四个角标颜色
                                    .setFrameColor(R.color.whilte)
                                    //设置边框上四个角标圆角  单位 /dp
                                    .setFrameRadius(2)
                                    //设置边框上四个角宽度 单位 /dp
                                    .setFrameWith(4)
                                    //设置边框上四个角长度 单位 /dp
                                    .setFrameLength(15)
                                    //设置是否显示边框外部阴影 true ： 显示  false ： 不显示
                                    .setShowShadow(true)
                                    //设置边框外部阴影颜色
                                    .setShadeColor(R.color.black_tran30)
                                    //设置扫码条运动方式   ScanMode.REVERSE : 往复运动   ScanMode.RESTART ：重复运动    默认ScanMode.RESTART
                                    .setScanMode(ScanMode.REVERSE)
                                    //设置扫码条扫一次时间  单位/ms  默认3000
                                    .setScanDuration(3000)
                                    //设置扫码条图片
                                    .setScanBitmapId(R.mipmap.scan_wechatline)
                                    //////////////////////////////////////////////
                                    //////////////////////////////////////////////
                                    //以下配置在 setIdentifyMultiple 为 true 时生效
                                    //设置是否开启识别多个二维码 true：开启 false：关闭   开启后识别到多个二维码会停留在扫码页 手动选择需要解析的二维码后返回结果
                                    .setIdentifyMultiple(isMultiple)
                                    //设置 二维码提示按钮的宽度 单位：px
                                    .setQrCodeHintDrawableWidth(120)
                                    //设置 二维码提示按钮的高度 单位：px
                                    .setQrCodeHintDrawableHeight(120)
                                    //设置 二维码提示按钮的Drawable资源
//                                    .setQrCodeHintDrawableResource(R.mipmap.in)
                                    //设置 二维码提示Drawable 是否开启缩放动画效果
                                    .setStartCodeHintAnimation(true)
                                    //设置 二维码选择页 背景透明度
                                    .setQrCodeHintAlpha(0.5f)
                                    //////////////////////////////////////////////
                                    .buidler()
                                    //跳转扫码页   扫码页可自定义样式
                                    .start(MyScanActivity.class);

```

接收扫码数据：

```
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case ScanCodeConfig.QUESTCODE:
                    //接收扫码结果
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        int codeType = extras.getInt(ScanCodeConfig.CODE_TYPE);
                        String code = extras.getString(ScanCodeConfig.CODE_KEY);
                        tvCode.setText(String.format(
                                "扫码结果：\n" +
                                        "码类型: %s  \n" +
                                        "码值  : %s", codeType == 0 ? "一维码" : "二维码", code));
                    }
                    break;
                case ALBUM_QUEST_CODE:
                    //接收图片识别结果
                    String code = ScanCodeConfig.scanningImage(this, data.getData());
                    tvCode.setText(String.format("识别结果： %s", code));
                    break;
                default:
                    break;
            }
        }
    }
```

更多功能请查看使用文档。

使用文档：http://18390826440.3vkj.club

简书：https://www.jianshu.com/p/c549f91cb9c5

APP下载链接：https://www.pgyer.com/FMi9

![image](http://18390826440.3vkj.club/img/20230604213832.jpg)
![image](http://18390826440.3vkj.club/img/20230604213840.jpg)
