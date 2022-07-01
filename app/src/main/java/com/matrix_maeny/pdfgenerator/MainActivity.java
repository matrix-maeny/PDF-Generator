package com.matrix_maeny.pdfgenerator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PostProcessor;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private WebView webView, printWeb = null;
    private PrintJob printJob;
    private boolean btnPressed = false;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initialize();

        webView.loadUrl("https://www.google.com");


    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initialize() {
        webView = findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                progressDialog = ProgressDialog.show(MainActivity.this, "", "loading...", true);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                printWeb = webView;
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // go to about activity

        switch (item.getItemId()) {
            case R.id.about_app:
                // go to about activity
                startActivity(new Intent(MainActivity.this,AboutActivity.class));
                break;
            case R.id.generate_pdf:
                generatePdf();
        }
        return super.onOptionsItemSelected(item);
    }

    private void generatePdf() {
        if (printWeb != null) {

            printTheWebPage(printWeb);
        } else {
            showToast("page is not fully loaded");
        }
    }

    private void printTheWebPage(WebView printWeb) {

        btnPressed = true;

        PrintManager manager = (PrintManager) getSystemService(PRINT_SERVICE);

        String jobName = getString(R.string.app_name) + " @matrix " + printWeb.getUrl();

        PrintDocumentAdapter adapter = printWeb.createPrintDocumentAdapter(jobName);

        if (manager != null) {

            printJob = manager.print(jobName, adapter, new PrintAttributes.Builder().build());
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (printJob != null && btnPressed) {

            if (printJob.isCompleted()) {
                showToast("Completed");
            } else if (printJob.isStarted()) {
                showToast("Started");
            } else if (printJob.isCancelled()) {
                showToast("Cancelled");
            } else if (printJob.isFailed()) {
                showToast("Failed");
            } else if (printJob.isBlocked()) {
                showToast("Blocked");
            } else if (printJob.isQueued()) {
                showToast("In Queue");
            }
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed() {

        if (printWeb != null && printWeb.canGoBack()) {
            printWeb.goBack();
        } else {
            super.onBackPressed();
        }
    }
}

