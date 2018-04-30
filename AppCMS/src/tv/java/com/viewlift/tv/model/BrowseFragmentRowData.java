package com.viewlift.tv.model;

import com.viewlift.models.data.appcms.api.ContentDatum;
import com.viewlift.models.data.appcms.ui.page.Component;

import java.util.List;

/**
 * Created by nitin.tyagi on 7/1/2017.
 */

public class BrowseFragmentRowData {
         public ContentDatum contentData;
         public List<String> relatedVideoIds;
         public List<Component> uiComponentList;
         public String action;
         public String blockName;

         //thisproperty will be use in case component is a player component.
         public boolean isPlayerComponent;
         public boolean isSearchPage;
         public int rowNumber;
         public boolean infoHover;
}