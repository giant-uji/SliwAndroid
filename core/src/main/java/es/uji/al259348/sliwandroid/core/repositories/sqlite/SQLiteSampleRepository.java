package es.uji.al259348.sliwandroid.core.repositories.sqlite;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.sql.SQLException;
import java.util.List;

import es.uji.al259348.sliwandroid.core.model.Sample;
import es.uji.al259348.sliwandroid.core.repositories.SampleRepository;

public class SQLiteSampleRepository implements SampleRepository {

    private SQLiteSliwDatabase db;
    private RuntimeExceptionDao<Sample, String> dao;

    public SQLiteSampleRepository(Context context) {
        this.db = OpenHelperManager.getHelper(context, SQLiteSliwDatabase.class);
        this.dao = this.db.getRuntimeExceptionDao(Sample.class);
    }

    @Override
    public void onDestroy() {
        OpenHelperManager.releaseHelper();
    }

    @Override
    public Sample save(Sample sample) {
        dao.createOrUpdate(sample);

        ForeignCollection<Sample.WifiScanResult> scanResults = dao.getEmptyForeignCollection("scanResults");
        for (Sample.WifiScanResult wifiScanResult : sample.getScanResults()) {
            wifiScanResult.sample = sample;
            scanResults.add(wifiScanResult);
        }

        sample.setScanResults(scanResults);

        return sample;
    }

    @Override
    public void remove(Sample sample) {
        int rowsUpdated = dao.delete(sample);
        Log.d("SampleRepo", "Resultado eliminar muestra: " + rowsUpdated);
    }

    @Override
    public long count() {
        return dao.countOf();
    }

    @Override
    public Sample findById(String id) {
        return dao.queryForId(id);
    }

    @Override
    public List<Sample> findAll() {
        return dao.queryForAll();
    }

}
