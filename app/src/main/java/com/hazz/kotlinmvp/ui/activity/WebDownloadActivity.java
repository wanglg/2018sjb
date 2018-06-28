package com.hazz.kotlinmvp.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hazz.kotlinmvp.BuildConfig;
import com.hazz.kotlinmvp.R;
import com.hazz.kotlinmvp.utils.AppUtils;
import com.hazz.kotlinmvp.utils.PackageUtils;
import com.hazz.kotlinmvp.utils.okhttp.OkHttpUtil;
import com.hazz.kotlinmvp.utils.okhttp.ProgressListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WebDownloadActivity extends AppCompatActivity {

    private String url;
    private ProgressBar download_progress;
    private TextView download_text;
    public String basePath = "";
    private AlertDialog alertDialog;
    public static final String bagname = "com.bxvip.app.dafa02";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_force);
        url = getIntent().getStringExtra("url");
        basePath = AppUtils.Companion.getDiskFileDir(this);
        download_text = (TextView) findViewById(R.id.download_text);
        download_progress = (ProgressBar) findViewById(R.id.download_progress);
        download_progress.setMax(100);

        final String fileName = url.split("/")[url.split("/").length - 1];
        //下载
        OkHttpUtil.downloadFile(url, new ProgressListener() {
            @Override
            public void onProgress(long currentBytes, long contentLength, boolean done) {
                int progress = (int) (currentBytes * 100 / contentLength);
//                    Log.e("kezi","下载 。。。。"+progress);
                download_progress.setProgress(progress);
                download_text.setText(progress + "%");
                if (done) {
                    File file = new File(basePath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    //download_progress.setVisibility(View.GONE);
                    openFile(new File(basePath + "/" + fileName));
                }
            }
        }, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response != null) {
                    InputStream is = response.body().byteStream();
                    File file = new File(basePath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    FileOutputStream fos = new FileOutputStream(new File(basePath + "/" + fileName));
                    int len = 0;
                    byte[] buffer = new byte[2048];
                    while (-1 != (len = is.read(buffer))) {
                        fos.write(buffer, 0, len);
                    }
                    fos.flush();
                    fos.close();
                    is.close();
                }
            }
        });
    }

    //打开APK程序代码
    private void openFile(File file) {
        String[] command = {"chmod", "777", file.getPath()};
        ProcessBuilder builder = new ProcessBuilder(command);
        try {
            builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileName = url.split("/")[url.split("/").length - 1];
        install = PackageUtils.install(getApplicationContext(), new File(basePath + "/" + fileName).getAbsolutePath());
        Log.i("kezi", "openFile 。。。。" + install);

//        String fileName = basePath + "/"+file.getName();
//        Log.e("kezi", "getAbsolutePath  "+file.getAbsolutePath());
//        final Intent intent = new Intent(Intent.ACTION_VIEW);
//
//        if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
//            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
//            Uri apkUri =
//                    FileProvider.getUriForFile(this, pgName+".fileprovider", new File(basePath + "/"+file.getName()));
//            // 由于没有在Activity环境下启动Activity,设置下面的标签
////            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            //添加这一句表示对目标应用临时授权该Uri所代表的文件
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            Log.e("kezi", "apkUri  "+apkUri.toString());
//            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
//        } else {
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setDataAndType(Uri.fromFile(new File(basePath + "/"+file.getName())), "application/vnd.android.package-archive");
//        }
//        startActivity(intent);
////           startActivityForResult(intent, 201);
//        download_text.setVisibility(View.GONE);
//        download_progress.setVisibility(View.GONE);
//        uninstallAPP();

    }

    int install = -1;

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("kezi", "onResume 。。。。" + install);
        BroadcastReceiver installedReceiver = new WebDownloadActivity.AppInstallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addDataScheme("package");
        this.registerReceiver(installedReceiver, filter);
    }


    class AppInstallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
                String packageName = intent.getData().getSchemeSpecificPart();
                Log.i("kezi", "app installed ");
                if ("com.bxvip.app.dafa01".equals(packageName) || "com.bxvip.app.dafa02".equals(packageName)) {
//                    PackageUtils.uninstall(getApplicationContext(), BuildConfig.APPLICATION_ID);
                    if (alertDialog == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(WebDownloadActivity.this);
                        // 设置参数
                        builder.setTitle("提示")
                                .setMessage("是否卸载旧版APP")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {// 积极

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        PackageUtils.uninstall(getApplicationContext(), BuildConfig.APPLICATION_ID);

                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {// 消极

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                PackageManager pm = getPackageManager();
                                Intent intent = pm.getLaunchIntentForPackage(packageName);
                                if (intent != null) {
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }

                            }
                        });
                        alertDialog = builder.create();
                        alertDialog.show();
                    }

                }
//                if (install == PackageUtils.INSTALL_SUCCEEDED){
//                    install = -1;
//                }
            } else if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
                Log.d("tag", "app uninstalled");
            }
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.e("kezi", "requestCode  "+requestCode+",  resultCode:"+resultCode);
//        if (requestCode == 201){
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    download_text.setVisibility(View.GONE);
//                    download_progress.setVisibility(View.GONE);
//                    uninstallAPP();
//                }
//            },5000);
//
//        }
//    }
//
//    private void uninstallAPP() {
//        Uri uri = Uri.fromParts("package", pgName, null);
//        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }
}
