package com.zeedroid.maparcade.database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.os.AsyncTask;

import com.zeedroid.maparcade.entity.Point;

/**
 * Created by Steve Dixon on 13/08/2017.
 */

public class AddPointViewModel extends AndroidViewModel{
    private AppDatabase appDatabase;

    public AddPointViewModel(Application application){
        super(application);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());
    }

/*    public void addPoint(Point point){
        new AddpointAsyncTask(appDatabase).execute(point);
    }

    private static class AddPointAsyncTask extends AsyncTask<Point, Void, Void>{
        private AppDatabase db;

        public AddPointAsyncTask(AppDatabase appDatabase){
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(Point point,
                                      Void, Void){
            db.pointDAO().insert(point);
            return null;
        }
    }*/
}
