package com.wokesolutions.ignes.ignes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ReportFormActivity extends Activity {
    private Button mUpload;
    private int mRequestCode = 1;
    private ImageView mPicture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportform);

        mPicture = (ImageView)findViewById(R.id.report_image);

        mUpload = (Button) findViewById(R.id.report_upload_button);
        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);

                if(intent.resolveActivity(getPackageManager())!=null)
                    startActivityForResult(intent, mRequestCode);
            }
        });

    }

    public void onActivityResult(int requestcode, int resultcode, Intent data){
        if(requestcode == mRequestCode)
            if(resultcode == RESULT_OK){
            Bundle bundle = new Bundle();
            bundle = data.getExtras();
            Bitmap bmp;
            bmp = (Bitmap) bundle.get("data");
            mPicture.setImageBitmap(bmp);

            }


    }
}
