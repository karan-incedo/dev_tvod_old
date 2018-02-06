package com.viewlift.views.adapters;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viewlift.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
 * Created by viewlift on 7/25/17.
 */

public class SearchSuggestionsAdapter extends CursorAdapter {

    private static final String TAG = "SearchSuggestionTag_";

    @BindView(R.id.search_suggestion_film_name_text)
    TextView filmTitle;

    @BindView(R.id.search_suggestion_runtime_text)
    TextView runtime;

    private SearchableInfo searchableInfo;

    public SearchSuggestionsAdapter(Context context, Cursor c, SearchableInfo searchableInfo,
                                    boolean autoRequery) {
        super(context, c, autoRequery);
        this.searchableInfo = searchableInfo;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return layoutInflater.inflate(R.layout.search_suggestion, parent, false);
    }

    @Override
    @SuppressWarnings("StringBufferReplaceableByString")
    public void bindView(View view, Context context, Cursor cursor) {
        ButterKnife.bind(this, view);

        filmTitle.setText(cursor.getString(1));
        int runtimeAsInteger = Integer.valueOf(cursor.getString(2));

        if (runtimeAsInteger / 60 < 1) {
            runtime.setText(new StringBuilder().append(cursor.getString(2))
                    .append(" ")
                    .append(context.getString(R.string.runtime_seconds_abbreviation)).toString());
        } else if (runtimeAsInteger / 60 < 2) {
            runtime.setText(new StringBuilder().append(Integer.valueOf(cursor.getString(2)) / 60)
                    .append(" ")
                    .append(context.getString(R.string.runtime_minute_abbreviation)).toString());
        } else {
            runtime.setText(new StringBuilder().append(Integer.valueOf(cursor.getString(2)) / 60)
                    .append(" ")
                    .append(context.getString(R.string.runtime_minutes_abbreviation)).toString());
        }
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        Cursor cursor;
        String query = ((constraint == null) ? "" : constraint.toString());

        try {
            cursor = getSearchManagerSuggestions(searchableInfo, query, 5);
            if (cursor != null) {
                cursor.getCount();
                return cursor;
            }
        } catch (RuntimeException e) {
            //Log.w(TAG, "runQueryOnBackgroundThread: " + e.getMessage());
        }

        return super.runQueryOnBackgroundThread(constraint);
    }

    @SuppressWarnings("SameParameterValue")
    private Cursor getSearchManagerSuggestions(SearchableInfo searchable, String query, int limit) {
        if (searchable == null) {
            return null;
        }

        String authority = searchable.getSuggestAuthority();
        if (authority == null) {
            return null;
        }

        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_CONTENT)
                .authority(authority)
                .query("")  // TODO: Remove, workaround for a bug in Uri.writeToParcel()
                .fragment("");  // TODO: Remove, workaround for a bug in Uri.writeToParcel()

        // if content path provided, insert it now
        final String contentPath = searchable.getSuggestPath();
        if (contentPath != null) {
            uriBuilder.appendEncodedPath(contentPath);
        }

        uriBuilder.appendPath(SearchManager.SUGGEST_URI_PATH_QUERY);
        String selection = searchable.getSuggestSelection();
        String[] selArgs = null;

        if (selection != null) {
            selArgs = new String[]{query};
        } else {
            uriBuilder.appendPath(query);
        }

        if (limit > 0) {
            uriBuilder.appendQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT,
                    String.valueOf(limit));
        }

        Uri uri = uriBuilder.build();

        // finally, make the query
        return mContext.getContentResolver().query(uri, null, selection, selArgs, null);
    }
}
