package com.viewlift.models.data.appcms.downloads;

import android.util.Log;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class DownloadMediaMigration implements RealmMigration {

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        Log.e("Migration","Migration START");
        RealmSchema schema = realm.getSchema();
        RealmObjectSchema realmObjectSchema = schema.get("DownloadVideoRealm");
        RealmObjectSchema downloadClosedCaption = schema.create("DownloadClosedCaptionRealm");

        if(!downloadClosedCaption.hasField("id")){
            downloadClosedCaption.addField("id",String.class);
        }

        if(!downloadClosedCaption.hasField("publishDate")){
            downloadClosedCaption.addField("publishDate",String.class);
        }

        if(!downloadClosedCaption.hasField("updateDate")){
            downloadClosedCaption.addField("updateDate",String.class);
        }

        if(!downloadClosedCaption.hasField("addedDate")){
            downloadClosedCaption.addField("addedDate",String.class);
        }

        if(!downloadClosedCaption.hasField("permalink")){
            downloadClosedCaption.addField("permalink",String.class);
        }

        if(!downloadClosedCaption.hasField("siteOwner")){
            downloadClosedCaption.addField("siteOwner",String.class);
        }

        if(!downloadClosedCaption.hasField("registeredDate")){
            downloadClosedCaption.addField("registeredDate",String.class);
        }

        if(!downloadClosedCaption.hasField("url")){
            downloadClosedCaption.addField("url",String.class, FieldAttribute.PRIMARY_KEY);
        }

        if(!downloadClosedCaption.hasField("format")){
            downloadClosedCaption.addField("format",String.class);
        }

        if(!downloadClosedCaption.hasField("language")){
            downloadClosedCaption.addField("language",String.class);
        }

        if(!downloadClosedCaption.hasField("size")){
            downloadClosedCaption.addField("size",float.class);
        }

        if(!downloadClosedCaption.hasField("gistId")){
            downloadClosedCaption.addField("gistId",String.class);
        }
        if(!downloadClosedCaption.hasField("ccFileEnqueueId")){
            downloadClosedCaption.addField("ccFileEnqueueId",long.class);
        }

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

        if(!realmObjectSchema.hasField("endDate")){
            realmObjectSchema.addField("endDate",long.class);
        }
        if(!realmObjectSchema.hasField("subscriptionType")){
            realmObjectSchema.addField("subscriptionType",String.class);
        }
        oldVersion++;
        Log.e("Migration","Migration Done");
    }
}
