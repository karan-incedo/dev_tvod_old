package com.viewlift.models.data.appcms.downloads;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class DownloadMediaMigration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        Log.e("Migration","Migration START");
        RealmSchema schema = realm.getSchema();
        RealmObjectSchema realmObjectSchema = schema.get("DownloadVideoRealm");

        if(!realmObjectSchema.hasField("artistName")){
            realmObjectSchema.addField("artistName",String.class);
        }

        if(!realmObjectSchema.hasField("directorName")){
            realmObjectSchema.addField("directorName",String.class);
        }

        if(!realmObjectSchema.hasField("songYear")){
            realmObjectSchema.addField("songYear",String.class);
        }

        if(!realmObjectSchema.hasField("contentType")){
            realmObjectSchema.addField("contentType",String.class);
        }

        if(!realmObjectSchema.hasField("mediaType")){
            realmObjectSchema.addField("mediaType",String.class);
        }

        if(!realmObjectSchema.hasField("playListName")){
            realmObjectSchema.addField("playListName",String.class);
        }
        if(!realmObjectSchema.hasField("episodeNum")){
            realmObjectSchema.addField("episodeNum",String.class);
        }
        if(!realmObjectSchema.hasField("showName")){
            realmObjectSchema.addField("showName",String.class);
        }
        if(!realmObjectSchema.hasField("seasonNum")){
            realmObjectSchema.addField("seasonNum",String.class);
        }
        if(!realmObjectSchema.hasField("genre")){
            realmObjectSchema.addField("genre",String.class);
        }

        if(!realmObjectSchema.hasField("transactionEndDate")){
            realmObjectSchema.addField("transactionEndDate",long.class);
        }
        if(!realmObjectSchema.hasField("subscriptionType")){
            realmObjectSchema.addField("subscriptionType",String.class);
        }
        if(!realmObjectSchema.hasField("rentStartWatchTime")){
            realmObjectSchema.addField("rentStartWatchTime",long.class);
        }
        if(!realmObjectSchema.hasField("rentalPeriod")){
            realmObjectSchema.addField("rentalPeriod",float.class);
        }
        if(!realmObjectSchema.hasField("isRentStartTimeSyncedWithServer")){
            realmObjectSchema.addField("isRentStartTimeSyncedWithServer",boolean.class);
        }
        oldVersion++;
        Log.e("Migration","Migration Done");
    }
}
