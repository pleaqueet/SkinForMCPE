package com.example.skinspe;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DetailedActivity extends AppCompatActivity {


    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        ImageView imageView = (ImageView) findViewById(R.id.skin);

        InputStream inputStream = null;
        try{
            inputStream = getApplicationContext().getAssets().open("images/" + getIntent().getStringExtra("skin"));
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            imageView.setImageDrawable(drawable);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            try{
                if(inputStream!=null)
                    inputStream.close();
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }

    public void onExport(View view) throws IOException {
        ZipSkin(getIntent().getStringExtra("skin"), getIntent().getStringExtra("skin"));
        String p = getExternalFilesDir(null) + "/skin.mcpack";
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setData(Uri.parse("minecraft://?import=" + p));
            getApplicationContext().startActivity(intent);
        } catch (android.content.ActivityNotFoundException activityNotFoundException){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.mojang.minecraftpe")));
        }
    }


    public void onExportGallery(View view) {
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionStatus2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED && permissionStatus2 == PackageManager.PERMISSION_GRANTED) {
            ExportFile();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ExportFile();
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                ExportFile();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void ExportFile(){
        try {
            InputStream ims = getAssets().open("skins/" + getIntent().getStringExtra("skin"));
            String p = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ File.separator +getIntent().getStringExtra("skin");
            byte[] buffer = new byte[ims.available()];
            ims.read(buffer, 0, ims.available());
            FileOutputStream fileOutputStream = new FileOutputStream(p);
            fileOutputStream.write(buffer);
            fileOutputStream.close();
            Toast.makeText(this, "Exported to gallery", Toast.LENGTH_SHORT).show();
        } catch (IOException e){
            Toast.makeText(this, "Failed to export gallery", Toast.LENGTH_SHORT).show();
        }
    }


    public void ZipSkin(String skinName, String skinRes) throws IOException {
        String p = getExternalFilesDir(null) + "/skin.mcpack";
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(p));

        InputStream ims = getAssets().open("skins/" + skinRes);

        ZipEntry zipEntry1 = new ZipEntry("manifest.json");
        zipOutputStream.putNextEntry(zipEntry1);
        zipOutputStream.write(("{\n" +
                "\t\"format_version\": 1,\n" +
                "\t\"header\": {\n" +
                "\t\t\"name\": \""+ skinName +"\",\n" +
                "\t\t\"uuid\": \""+ UUID.randomUUID()+"\",\n" +
                "\t\t\"version\": [\n" +
                "\t\t\t1,\n" +
                "\t\t\t0,\n" +
                "\t\t\t0\n" +
                "\t\t]\n" +
                "\t},\n" +
                "\t\"modules\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"type\": \"skin_pack\",\n" +
                "\t\t\t\"uuid\": \""+UUID.randomUUID()+"\",\n" +
                "\t\t\t\"version\": [\n" +
                "\t\t\t\t1,\n" +
                "\t\t\t\t0,\n" +
                "\t\t\t\t0\n" +
                "\t\t\t]\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}").getBytes());

        ZipEntry zipEntry2 = new ZipEntry("skins.json");
        zipOutputStream.putNextEntry(zipEntry2);
        zipOutputStream.write(("{\n" +
                "\t\"geometry\": \"skins.json\",\n" +
                "\t\"skins\": [\n" +
                "\t\t{\n" +
                "\t\t\"localization_name\": \"current\",\n" +
                "\t\t\"geometry\": \"geometry.humanoid.custom\",\n" +
                "\t\t\"texture\": \"" + skinRes + "\",\n" +
                "\t\t\"type\": \"free\"\n" +
                "\t\t}\n" +
                "\t],\n" +
                "\t\"serialize_name\": \"" + skinName + "\",\n" +
                "\t\"localization_name\": \"" + skinName + "\"\n" +
                "}").getBytes());

        ZipEntry zipEntry3 = new ZipEntry(skinRes);
        byte[] buffer = new byte[ims.available()];
        ims.read(buffer, 0, ims.available());
        zipOutputStream.putNextEntry(zipEntry3);
        zipOutputStream.write(buffer);

        zipOutputStream.close();
    }
}