package com.slerpio.teachme;

import android.content.ActivityNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.shockwave.pdfium.PdfDocument;
import com.slerpio.teachme.helper.DateUtils;
import com.slerpio.teachme.helper.StreamUtils;
import com.slerpio.teachme.model.Domain;
import com.slerpio.teachme.service.DocumentService;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MaterialDetailTypePdfActivity extends AppCompatActivity implements OnPageChangeListener, OnLoadCompleteListener, OnPageErrorListener {
    private static final String TAG = MaterialDetailTypePdfActivity.class.getName();
    @BindView(R.id.pdfView)
    PDFView pdfView;
    @BindView(R.id.materialDescription)
    TextView materialDescription;
    @BindView(R.id.clock)
    TextView clock;
    @BindView(R.id.fullname)
    TextView fullName;
    private Domain material;
    @Inject
    Retrofit retrofit;
    DocumentService documentService;
    @NonNull
    CompositeDisposable disposable = new CompositeDisposable();
    private ParcelFileDescriptor descriptor;
    private int pageNumber = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.material = new Domain(getIntent().getStringExtra("material"));
        setContentView(R.layout.activity_material_detail_type_pdf);
        ButterKnife.bind(this);

        ((App)getApplication()).getNetOauthComponent().inject(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(material.getString("title"));
        }
        this.documentService = retrofit.create(DocumentService.class);
        initView();
        openRenderer();

    }

    private void initView() {
        this.clock.setText(DateUtils.printDate(material.getLong("created_at")));
        this.materialDescription.setText(material.getString("description"));
        this.fullName.setText(material.getDomain("user").getString("fullname"));
    }

    private void openRenderer(){
        File file = new File(Environment.getExternalStorageDirectory() + "/TeachMe/", "Teachme-"+ material.getLong("created_at") + ".pdf");
        if(!file.getParentFile().isDirectory()) {
            file.getParentFile().mkdirs();
        }
        if(file.exists() && file.canRead()){
            try {
                openFile(file);
            } catch (IOException e) {
                Log.e(TAG, "openRenderer: ", e);
            }
        }else{
            disposable.add(documentService.getDocument(material.getLong("document_id")).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(response ->{
                InputStream in = response.byteStream();
                FileOutputStream out = new FileOutputStream(file);
                StreamUtils.copyStream(in, out);
                StreamUtils.close(in);
                StreamUtils.close(out);
                openFile(file);

            }, error ->{
                if (error instanceof ActivityNotFoundException){
                    Snackbar.make(findViewById(android.R.id.content), R.string.failed_to_open_pdf_file, Snackbar.LENGTH_LONG).show();
                }
            }));
        }
    }
    private void openFile(File file) throws IOException {
         pdfView.fromFile(file).defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .load();
    }


    @Override
    public void onPageChanged(int page, int pageCount) {
        this.pageNumber = page;

    }

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        Log.e(TAG, "title = " + meta.getTitle());
        Log.e(TAG, "author = " + meta.getAuthor());
        Log.e(TAG, "subject = " + meta.getSubject());
        Log.e(TAG, "keywords = " + meta.getKeywords());
        Log.e(TAG, "creator = " + meta.getCreator());
        Log.e(TAG, "producer = " + meta.getProducer());
        Log.e(TAG, "creationDate = " + meta.getCreationDate());
        Log.e(TAG, "modDate = " + meta.getModDate());

    }

    @Override
    public void onPageError(int page, Throwable t) {
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.system_error), Snackbar.LENGTH_LONG).show();
    }
}
