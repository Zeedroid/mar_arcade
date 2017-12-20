package com.zeedroid.maparcade;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.zeedroid.maparcade.database.AppDatabase;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import android.arch.persistence.room.testing.MigrationTestHelper;
import static com.zeedroid.maparcade.database.AppDatabase.MIGRATION_1_2;

/**
 * Created by User on 27/10/2017.
 */

@RunWith(AndroidJUnit4.class)
public class MigrationTest {
    @Rule
    public MigrationTestHelper helper;

    public MigrationTest(){
        helper = new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
                AppDatabase.class.getCanonicalName(),
                new FrameworkSQLiteOpenHelperFactory());
    }

    @Test
    public void migrate_1_2() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase("TEST_DB", 1);

        // db.execSQL(...)     insert data at database level 1 that will be used in migration test

        // db.close();         close database prepare for next version

        helper.runMigrationsAndValidate(
                "TEST_DB",
                2,
                true,
                MIGRATION_1_2);

        // test data after migration to prove it has worked.

    }
}
