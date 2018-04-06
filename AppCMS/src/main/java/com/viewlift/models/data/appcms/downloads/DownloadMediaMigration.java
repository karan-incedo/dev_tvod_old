package com.viewlift.models.data.appcms.downloads;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;
import io.realm.internal.Table;

public class DownloadMediaMigration implements RealmMigration{

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        // DynamicRealm exposes an editable schema
        RealmSchema schema = realm.getSchema();
        if (oldVersion==0 ){
            RealmObjectSchema realmObjectSchema = schema.get("DownloadVideoRealm");

            realmObjectSchema.addField("artistName",String.class)
                            .addField("directorName",String.class)
                            .addField("songYear",String.class);


        }
    }
}
