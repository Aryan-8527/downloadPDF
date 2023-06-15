package com.example.pdfgeneratedownloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button downloadpdf;
    LinearLayout linearLayouts , linears ;
    ScrollView scrollView ;
    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 10 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        downloadpdf = findViewById(R.id.pdf_download);
        linearLayouts = findViewById(R.id.linearlayout);
        linears = findViewById(R.id.xyz_layout);
        scrollView = findViewById(R.id.scrool);

        downloadpdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
                } else {
                    generatePDF();
                }
            }
        });
    }

    private void generatePDF() {

        Bitmap bitmap2 = Bitmap.createBitmap(
                scrollView.getChildAt(0).getWidth(),
                scrollView.getChildAt(0).getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(bitmap2);
        canvas2.drawColor(Color.WHITE);

        Drawable bgDrawable = scrollView.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas2);
        else
            canvas2.drawColor(Color.WHITE);
             scrollView.draw(canvas2);

        int contentHeight = scrollView.getChildAt(0).getHeight();
        int scrollViewHeight = scrollView.getHeight();
        int pageCount = (int) Math.ceil((double) contentHeight / scrollViewHeight);


        Document document = new Document();
        String filename = "employeeProfile.pdf";
        File pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) , filename );
        try {
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            for (int pageNumber = 0; pageNumber < pageCount; pageNumber++) {
                // Create a new page in the document
                document.newPage();

                // Calculate the visible portion of the ScrollView content for the current page
                int startOffset = pageNumber * scrollViewHeight;
                int endOffset = Math.min(startOffset + scrollViewHeight, contentHeight);

                // Create a bitmap for the current page's content
                Bitmap pageBitmap = Bitmap.createBitmap(bitmap2, 0, startOffset, scrollView.getChildAt(0).getWidth(), endOffset - startOffset);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                pageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image pageImage = Image.getInstance(stream.toByteArray());
                pageImage.scaleToFit(document.getPageSize().getWidth(), document.getPageSize().getHeight());
                document.add(pageImage);
            }
            document.close();
            Toast.makeText(this, "PDF generated successfully!", Toast.LENGTH_SHORT).show();

        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            Log.d("TAG", "generatePDF: "+e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
     }
}