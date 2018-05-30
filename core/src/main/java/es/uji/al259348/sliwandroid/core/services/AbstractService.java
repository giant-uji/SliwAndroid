package es.uji.al259348.sliwandroid.core.services;

import android.content.Context;

public abstract class AbstractService implements Service {

    private Context context;

    public AbstractService(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

}
