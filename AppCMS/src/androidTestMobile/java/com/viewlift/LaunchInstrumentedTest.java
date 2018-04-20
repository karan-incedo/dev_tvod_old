package com.viewlift;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.viewlift.mobile.AppCMSLaunchActivity;
import com.viewlift.presenters.AppCMSPresenter;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.UiAutomatorScreenshotStrategy;
import tools.fastlane.screengrab.locale.LocaleTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class LaunchInstrumentedTest {


    private Context instrumentationCtx;

    @ClassRule
    public static final LocaleTestRule localeTestRule = new LocaleTestRule();

    @Rule
    public ActivityTestRule<AppCMSLaunchActivity> activityTestRule =
            new ActivityTestRule<>(AppCMSLaunchActivity.class, true, false);


    @Before
    public void setup() {
        instrumentationCtx = InstrumentationRegistry.getContext();
    }

    @Test
    public void test_launchActivity() throws Exception {

        Screengrab.setDefaultScreenshotStrategy(new UiAutomatorScreenshotStrategy());

        activityTestRule.launchActivity(new Intent(InstrumentationRegistry.getTargetContext(), AppCMSLaunchActivity.class));
        Thread.sleep(5000);
        Screengrab.screenshot("splash_screen");
        Thread.sleep(10000);
        Screengrab.screenshot("home_screen");
        Thread.sleep(5000);
        try {
            onView(withText("Shows")).perform(click());
            Thread.sleep(10000);
            Screengrab.screenshot("show_screen");
        } catch (NoMatchingViewException e) {
            // View is not in hierarchy
        }
        try {
            onView(withText("Movies")).perform(click());
            Thread.sleep(10000);
            Screengrab.screenshot("movies_screen");
        } catch (NoMatchingViewException e) {
            // View is not in hierarchy
        }
        try {
            onView(withText("Menu")).perform(click());
            Thread.sleep(10000);
            Screengrab.screenshot("menu_screen");
        } catch (NoMatchingViewException e) {
            // View is not in hierarchy
        }
        try {
            onView(withText("VIDEOS")).perform(click());
            Thread.sleep(10000);
            Screengrab.screenshot("menu_screen");
        } catch (NoMatchingViewException e) {
            // View is not in hierarchy
        }
        try {
            onView(withText("TEAMS")).perform(click());
            Thread.sleep(10000);
            Screengrab.screenshot("team_screen");
        } catch (NoMatchingViewException e) {
            // View is not in hierarchy
        }
        try {
            onView(withText("MORE")).perform(click());
            Thread.sleep(10000);
            Screengrab.screenshot("more_screen");
        } catch (NoMatchingViewException e) {
            // View is not in hierarchy
        }
//        Activity activity = activityTestRule.getActivity();
//        String package_name = Utils.getProperty("AppPackageName", activity);
//        AppCMSPresenter appCMSPresenter = ((AppCMSApplication) activity.getApplication()).getAppCMSPresenterComponent().appCMSPresenter();

    }


    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private <T> Matcher<T> firstView(final Matcher<T> matcher) {
        return new BaseMatcher<T>() {
            boolean isFirst = true;

            @Override
            public boolean matches(final Object item) {
                if (isFirst && matcher.matches(item)) {
                    isFirst = false;
                    return true;
                }
                return false;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("should return first matching item");
            }
        };
    }

}
