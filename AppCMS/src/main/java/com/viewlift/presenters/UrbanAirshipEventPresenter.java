package com.viewlift.presenters;

import org.threeten.bp.Period;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import javax.inject.Inject;

/**
 * Created by viewlift on 12/18/17.
 */

public class UrbanAirshipEventPresenter {
    private static final String SUBSCRIPTION_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    private static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC+00:00");

    private String loggedInStatusGroup;
    private String loggedInStatusTag;
    private String loggedOutStatusTag;
    private String subscriptionStatusGroup;
    private String subscribedTag;
    private String subscriptionAboutToExpireTag;
    private String unsubscribedTag;
    private int daysBeforeSubscriptionEndForNotification;

    @Inject
    public UrbanAirshipEventPresenter(String loggedInStatusGroup,
                                      String loggedInStatusTag,
                                      String loggedOutStatusTag,
                                      String subscriptionStatusGroup,
                                      String subscribedTag,
                                      String subscriptionAboutToExpireTag,
                                      String unsubscribedTag) {
        this.loggedInStatusGroup = loggedInStatusGroup;
        this.loggedInStatusTag = loggedInStatusTag;
        this.loggedOutStatusTag = loggedOutStatusTag;
        this.subscriptionStatusGroup = subscriptionStatusGroup;
        this.subscribedTag = subscribedTag;
        this.subscriptionAboutToExpireTag = subscriptionAboutToExpireTag;
        this.unsubscribedTag = unsubscribedTag;
    }

    public void sendUserLoginEvent(String userId) {

    }

    public void sendUserLogoutEvent(String userId) {

    }

    public void sendSubscribedEvent(String userId) {

    }

    public void sendSubscriptionAboutToExpireEvent(String userId) {

    }

    public void sendUnsubscribedEvent(String userId) {

    }

    public boolean subscriptionAboutToExpire(String subscriptionEndDate) {
        if (!subscriptionExpired(subscriptionEndDate)) {
            ZonedDateTime nowTime = ZonedDateTime.now(UTC_ZONE_ID);
            ZonedDateTime subscriptionEndTime = ZonedDateTime.from(DateTimeFormatter.ofPattern(SUBSCRIPTION_DATE_FORMAT).parse(subscriptionEndDate));
            Period daysBeforeSubscriptionEnd = Period.ofDays(daysBeforeSubscriptionEndForNotification);

        }

        return false;
    }

    public boolean subscriptionExpired(String subscriptionEndDate) {
        ZonedDateTime nowTime = ZonedDateTime.now(UTC_ZONE_ID);
        ZonedDateTime subscriptionEndTime = ZonedDateTime.from(DateTimeFormatter.ofPattern(SUBSCRIPTION_DATE_FORMAT).parse(subscriptionEndDate));

        return subscriptionEndTime.toEpochSecond() < nowTime.toEpochSecond();
    }
}
