package com.zeedroid.maparcade.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

import com.zeedroid.maparcade.dao.MapAddressDAO;
import com.zeedroid.maparcade.dao.MapRouteDAO;
import com.zeedroid.maparcade.dao.PointBackupDAO;
import com.zeedroid.maparcade.dao.PointDAO;
import com.zeedroid.maparcade.dao.PointExtraBackupDAO;
import com.zeedroid.maparcade.dao.PointExtraDAO;
import com.zeedroid.maparcade.entity.MapAddress;
import com.zeedroid.maparcade.entity.MapRoute;
import com.zeedroid.maparcade.entity.Point;
import com.zeedroid.maparcade.entity.PointBackup;
import com.zeedroid.maparcade.entity.PointExtra;
import com.zeedroid.maparcade.entity.PointExtraBackup;

//import javax.inject.Singleton;

/**
 * Created by Steve Dixon on 11/08/2017.
 */


//@Singleton
@Database(entities = {Point.class, MapRoute.class, PointExtra.class, MapAddress.class, PointBackup.class, PointExtraBackup.class}, version = 6)
@TypeConverters(DataConverters.class)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract PointDAO            getPointDAO();
    public abstract MapRouteDAO         getMapRouteDAO();
    public abstract PointExtraDAO       getPointExtraDAO();
    public abstract MapAddressDAO       getMapAddressDAO();
    public abstract PointBackupDAO      getPointBackupDAO();
    public abstract PointExtraBackupDAO getPointExtraBackupDAO();

    public static AppDatabase getAppDatabase(Context context){
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                       AppDatabase.class, "map_arcade_db")
                       .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                       .build();
        }
        return INSTANCE;
    }

    public static Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database){
            database.execSQL("ALTER TABLE `points` RENAME TO `points_tmp`;");
            database.execSQL("CREATE TABLE `points` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `path_id` INTEGER NOT NULL, `route_id` INTEGER, `point_position` INTEGER NOT NULL, `longitude` TEXT, `latitude` TEXT, FOREIGN KEY(`route_id`) REFERENCES `map_route`(`route_id`) ON UPDATE NO ACTION ON DELETE RESTRICT );");
            database.execSQL("INSERT INTO `points` (`id`, `path_id`, `route_id`, `point_position`, `longitude`, `latitude`) SELECT `id`, `path_id`, `route_id`, `point_position`, `longitude`, `latitude` FROM `points_tmp`;");
            database.execSQL("DROP TABLE `points_tmp`;");
            database.execSQL("ALTER TABLE `points_extra` RENAME TO `points_extra_tmp`;");
            database.execSQL("CREATE TABLE `points_extra` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `route_id` INTEGER, `point_type` TEXT, `position` TEXT, `point_color` TEXT, `point_shape` INTEGER NOT NULL, `longitude` REAL, `latitude` REAL);");
            database.execSQL("INSERT INTO `points_extra` (`id`, `route_id`, `point_type`, `position`, `point_color`, `point_shape`, `longitude`, `latitude`) SELECT `id`, `route_id`, `point_type`, `position`, `point_color`, `point_shape`, `longitude`, `latitude` FROM `points_extra_tmp`;");
            database.execSQL("DROP TABLE `points_extra_tmp`;");
        }
    };

    public static Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database){
            database.execSQL("CREATE TABLE `route_address` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `route_id` INTEGER, `road_id` INTEGER, `road_name` TEXT, FOREIGN KEY(`route_id`) REFERENCES `map_route`(`route_id`) ON UPDATE NO ACTION ON DELETE RESTRICT );");
        }
    };

    public static Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database){
            database.execSQL("ALTER TABLE `points` ADD COLUMN `keep` INTEGER;");
        }
    };

    public static Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database){
            database.execSQL("ALTER TABLE `points` DELETE COLUMN `keep`;");
        }
    };

    public static Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database){
            database.execSQL("CREATE TABLE `points_backup` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `undo_position` INTEGER NOT NULL, `undo_type` INTEGER NOT NULL, `point_position` INTEGER NOT NULL, `longitude` TEXT, `latitude` TEXT);");
            database.execSQL("CREATE INDEX `index_points_backup_id` ON `points_backup` (`id`)");
            database.execSQL("CREATE TABLE `points_extra_backup` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `undo_position` INTEGER, `undo_type` INTEGER NOT NULL, `point_type` TEXT, `position` TEXT, `point_color` TEXT, `point_shape` INTEGER NOT NULL, `longitude` REAL, `latitude` REAL);");
            database.execSQL("CREATE INDEX `index_points_extra_backup_id` ON `points_extra_backup` (`id`)");
        }
    };

    public static void destroyInstance(){
        INSTANCE = null;
    }
}
