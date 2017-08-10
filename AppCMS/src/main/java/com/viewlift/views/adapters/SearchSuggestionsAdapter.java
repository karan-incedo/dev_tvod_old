package com.viewlift.views.adapters;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.viewlift.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
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
    public void bindView(View view, Context context, Cursor cursor) {
        ButterKnife.bind(this, view);

        filmTitle.setText(cursor.getString(1));
        runtime.setText(new StringBuilder().append(cursor.getString(2))
                .append(" ")
                .append(context.getString(R.string.minutes_for_runtime)).toString());
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
            Log.w(TAG, "runQueryOnBackgroundThread: " + e.getMessage());
        }

        return super.runQueryOnBackgroundThread(constraint);
    }

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

        // append standard suggestion query path
        uriBuilder.appendPath(SearchManager.SUGGEST_URI_PATH_QUERY);

        // get the query selection, may be null
        String selection = searchable.getSuggestSelection();

        // inject query, either as selection args or inline
        String[] selArgs = null;

        if (selection != null) {    // use selection if provided
            selArgs = new String[]{query};
        } else {                    // no selection, use REST pattern
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
