package io.pp.net_disk_demo.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 *https://blog.csdn.net/qq_35619188/article/details/74011385
 */

import io.pp.net_disk_demo.R;

public class StatusBarUtil {

    public static final int DEFAULT_STATUS_BAR_ALPHA = 112;
    private static final int FAKE_STATUS_BAR_VIEW_ID = R.id.statusbarutil_fake_status_bar_view;
    private static final int FAKE_TRANSLUCENT_VIEW_ID = R.id.statusbarutil_translucent_view;
    private static final int TAG_KEY_HAVE_SET_OFFSET = -123;

    /**
     * Set status bar color
     *
     * @param activity Activity that needs to be set
     * @param color    Status bar color value
     */
    public static void setColor(Activity activity, @ColorInt int color) {
        setColor(activity, color, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * Set status bar color
     *
     * @param activity       Activity that needs to be set
     * @param color          Status bar color value
     * @param statusBarAlpha Status bar transparency
     */
    public static void setColor(Activity activity, @ColorInt int color, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setStatusBarColor(calculateStatusColor(color, statusBarAlpha));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            View fakeStatusBarView = decorView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
            if (fakeStatusBarView != null) {
                if (fakeStatusBarView.getVisibility() == View.GONE) {
                    fakeStatusBarView.setVisibility(View.VISIBLE);
                }
                fakeStatusBarView.setBackgroundColor(calculateStatusColor(color, statusBarAlpha));
            } else {
                decorView.addView(createStatusBarView(activity, color, statusBarAlpha));
            }
            setRootView(activity);
        }
    }

    /**
     * Set the status bar color for the sliding back interface
     *
     * @param activity Activity that needs to be set
     * @param color    Status bar color value
     */
    public static void setColorForSwipeBack(Activity activity, int color) {
        setColorForSwipeBack(activity, color, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * Set the status bar color for the sliding back interface
     *
     * @param activity       Activity that needs to be set
     * @param color          Status bar color value
     * @param statusBarAlpha Status bar transparency
     */
    public static void setColorForSwipeBack(Activity activity, @ColorInt int color, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup contentView = ((ViewGroup) activity.findViewById(android.R.id.content));
            View rootView = contentView.getChildAt(0);
            int statusBarHeight = getStatusBarHeight(activity);
            if (rootView != null && rootView instanceof CoordinatorLayout) {
                final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) rootView;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    coordinatorLayout.setFitsSystemWindows(false);
                    contentView.setBackgroundColor(calculateStatusColor(color, statusBarAlpha));
                    boolean isNeedRequestLayout = contentView.getPaddingTop() < statusBarHeight;
                    if (isNeedRequestLayout) {
                        contentView.setPadding(0, statusBarHeight, 0, 0);
                        coordinatorLayout.post(new Runnable() {
                            @Override
                            public void run() {
                                coordinatorLayout.requestLayout();
                            }
                        });
                    }
                } else {
                    coordinatorLayout.setStatusBarBackgroundColor(calculateStatusColor(color, statusBarAlpha));
                }
            } else {
                contentView.setPadding(0, statusBarHeight, 0, 0);
                contentView.setBackgroundColor(calculateStatusColor(color, statusBarAlpha));
            }
            setTransparentForWindow(activity);
        }
    }

    /**
     * Set the status bar solid color without translucency
     *
     * @param activity Activity that needs to be set
     * @param color    Status bar color value
     */
    public static void setColorNoTranslucent(Activity activity, @ColorInt int color) {
        setColor(activity, color, 0);
    }

    /**
     * Set the status bar color (no translucent effect below 5.0, not recommended)
     *
     * @param activity Activity that needs to be set
     * @param color    Status bar color value
     */
    @Deprecated
    public static void setColorDiff(Activity activity, @ColorInt int color) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        transparentStatusBar(activity);
        ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
        // Remove the translucent rectangle to avoid overlaying
        View fakeStatusBarView = contentView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
        if (fakeStatusBarView != null) {
            if (fakeStatusBarView.getVisibility() == View.GONE) {
                fakeStatusBarView.setVisibility(View.VISIBLE);
            }
            fakeStatusBarView.setBackgroundColor(color);
        } else {
            contentView.addView(createStatusBarView(activity, color));
        }

        setRootView(activity);
    }

    /**
     * Make the status bar translucent
     * <p>
     * Applies to the image as the background interface, you need to fill the image into the status bar.
     *
     * @param activity Activity that needs to be set
     */
    public static void setTranslucent(Activity activity) {
        setTranslucent(activity, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * Make the status bar translucent
     * <p>
     * Applies to the image as the background interface, you need to fill the image into the status bar.
     *
     * @param activity       Activity that needs to be set
     * @param statusBarAlpha Status bar transparency
     */
    public static void setTranslucent(Activity activity,
                                      @IntRange(from = 0, to = 255) int statusBarAlpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        setTransparent(activity);
        addTranslucentView(activity, statusBarAlpha);
    }

    /**
     * The root layout is CoordinatorLayout, making the status bar translucent
     *
     * <p>
     * Applies to the image as the background interface, you need to fill the image into the status bar.
     *
     * @param activity       Activity that needs to be set
     * @param statusBarAlpha Status bar transparency
     */
    public static void setTranslucentForCoordinatorLayout(Activity activity,
                                                          @IntRange(from = 0, to = 255) int statusBarAlpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        transparentStatusBar(activity);
        addTranslucentView(activity, statusBarAlpha);
    }

    /**
     * Set the status bar to be completely transparent
     *
     * @param activity Activity that needs to be set
     */
    public static void setTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        transparentStatusBar(activity);
        setRootView(activity);
    }

    /**
     * Make the status bar transparent (more than 5.0 translucent effect, not recommended)
     * <p>
     * Applicable to the interface of the image as a background, in this case, the image needs to be filled into the status bar.
     *
     * @param activity Activity that needs to be set
     */
    @Deprecated
    public static void setTranslucentDiff(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Set the status bar to be transparent
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setRootView(activity);
        }
    }

    /**
     * Set the status bar color for the DrawerLayout layout
     *
     * @param activity     Activity that needs to be set
     * @param drawerLayout DrawerLayout
     * @param color        Status bar color value
     */
    public static void setColorForDrawerLayout(Activity activity, DrawerLayout drawerLayout,
                                               @ColorInt int color) {
        setColorForDrawerLayout(activity, drawerLayout, color, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * Set the status bar color for the DrawerLayout layout, solid color
     *
     * @param activity     Activity that needs to be set
     * @param drawerLayout DrawerLayout
     * @param color        Status bar color value
     */
    public static void setColorNoTranslucentForDrawerLayout(Activity
                                                                    activity, DrawerLayout drawerLayout, @ColorInt int color) {
        setColorForDrawerLayout(activity, drawerLayout, color, 0);
    }

    /**
     * Set the status bar color for the DrawerLayout layout
     *
     * @param activity       Activity that needs to be set
     * @param drawerLayout   DrawerLayout
     * @param color          Status bar color value
     * @param statusBarAlpha Status bar transparency
     */
    public static void setColorForDrawerLayout(Activity activity, DrawerLayout
            drawerLayout, @ColorInt int color, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // Generate a rectangle with a status bar size
        // Add statusBarView to the layout
        ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
        View fakeStatusBarView = contentLayout.findViewById(FAKE_STATUS_BAR_VIEW_ID);
        if (fakeStatusBarView != null) {
            if (fakeStatusBarView.getVisibility() == View.GONE) {
                fakeStatusBarView.setVisibility(View.VISIBLE);
            }
            fakeStatusBarView.setBackgroundColor(color);
        } else {
            contentLayout.addView(createStatusBarView(activity, color), 0);
        }

        // Setting padding top when content layout is not LinearLayout
        if (!(contentLayout instanceof LinearLayout) && contentLayout.getChildAt(1) != null) {
            contentLayout.getChildAt(1).setPadding(contentLayout.getPaddingLeft(), getStatusBarHeight(activity) + contentLayout.getPaddingTop(), contentLayout.getPaddingRight(), contentLayout.getPaddingBottom());
        }

        // Setting properties
        setDrawerLayoutProperty(drawerLayout, contentLayout);
        addTranslucentView(activity, statusBarAlpha);
    }

    /**
     * Set the DrawerLayout property
     *
     * @param drawerLayout              DrawerLayout
     * @param drawerLayoutContentLayout Content layout of DrawerLayout
     */
    private static void setDrawerLayoutProperty(DrawerLayout
                                                        drawerLayout, ViewGroup drawerLayoutContentLayout) {
        ViewGroup drawer = (ViewGroup) drawerLayout.getChildAt(1);
        drawerLayout.setFitsSystemWindows(false);
        drawerLayoutContentLayout.setFitsSystemWindows(false);
        drawerLayoutContentLayout.setClipToPadding(true);
        drawer.setFitsSystemWindows(false);
    }

    /**
     * Set the status bar color for the DrawerLayout layout (no translucent effect below 5.0, not recommended)
     *
     * @param activity     Activity that needs to be set
     * @param drawerLayout DrawerLayout
     * @param color        Status bar color value
     */
    @Deprecated
    public static void setColorForDrawerLayoutDiff(Activity
                                                           activity, DrawerLayout drawerLayout, @ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Generate a rectangle with a status bar size
            ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
            View fakeStatusBarView = contentLayout.findViewById(FAKE_STATUS_BAR_VIEW_ID);
            if (fakeStatusBarView != null) {
                if (fakeStatusBarView.getVisibility() == View.GONE) {
                    fakeStatusBarView.setVisibility(View.VISIBLE);
                }
                fakeStatusBarView.setBackgroundColor(calculateStatusColor(color, DEFAULT_STATUS_BAR_ALPHA));
            } else {
                // Add statusBarView to the layout
                contentLayout.addView(createStatusBarView(activity, color), 0);
            }
            // Setting padding top when content layout is not LinearLayout
            if (!(contentLayout instanceof LinearLayout) && contentLayout.getChildAt(1) != null) {
                contentLayout.getChildAt(1).setPadding(0, getStatusBarHeight(activity), 0, 0);
            } // Setting properties
            setDrawerLayoutProperty(drawerLayout, contentLayout);
        }
    }

    /**
     * Set the status bar transparent for the DrawerLayout layout
     *
     * @param activity     Activity that needs to be set
     * @param drawerLayout DrawerLayout
     */
    public static void setTranslucentForDrawerLayout(Activity activity, DrawerLayout
            drawerLayout) {
        setTranslucentForDrawerLayout(activity, drawerLayout, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * Set the status bar transparent for the DrawerLayout layout
     *
     * @param activity     Activity that needs to be set
     * @param drawerLayout DrawerLayout
     */
    public static void setTranslucentForDrawerLayout(Activity
                                                             activity, DrawerLayout drawerLayout,
                                                     @IntRange(from = 0, to = 255) int statusBarAlpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        setTransparentForDrawerLayout(activity, drawerLayout);
        addTranslucentView(activity, statusBarAlpha);
    }

    /**
     * Set the status bar transparent for the DrawerLayout layout
     *
     * @param activity     Activity that needs to be set
     * @param drawerLayout DrawerLayout
     */
    public static void setTransparentForDrawerLayout(Activity
                                                             activity, DrawerLayout drawerLayout) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
        // Setting padding top when content layout is not LinearLayout
        if (!(contentLayout instanceof LinearLayout) && contentLayout.getChildAt(1) != null) {
            contentLayout.getChildAt(1).setPadding(0, getStatusBarHeight(activity), 0, 0);
        } // Setting properties
        setDrawerLayoutProperty(drawerLayout, contentLayout);
    }

    /**
     * Set the status bar to be transparent for DrawerLayout layout (more than 5.0 translucent effect, not recommended)
     *
     * @param activity     Activity that needs to be set
     * @param drawerLayout DrawerLayout
     */
    @Deprecated
    public static void setTranslucentForDrawerLayoutDiff(Activity
                                                                 activity, DrawerLayout drawerLayout) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Set the status bar transparent
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Set content layout properties
            ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
            contentLayout.setFitsSystemWindows(true);
            contentLayout.setClipToPadding(true);
            // Set drawer layout properties
            ViewGroup vg = (ViewGroup) drawerLayout.getChildAt(1);
            vg.setFitsSystemWindows(false);
            // Set the DrawerLayout property
            drawerLayout.setFitsSystemWindows(false);
        }
    }


    ////////

    /**
     * Set the status bar color for the LeftDrawerLayout layout
     *
     * @param activity         Activity that needs to be set
     * @param leftDrawerLayout LeftDrawerLayout
     * @param color            Status bar color value
     */
    public static void setColorForLeftDrawerLayout(Activity activity, LeftDrawerLayout leftDrawerLayout,
                                                   @ColorInt int color) {
        setColorForLeftDrawerLayout(activity, leftDrawerLayout, color, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * Set the status bar color for the LeftDrawerLayout layout, solid color
     *
     * @param activity         Activity that needs to be set
     * @param leftDrawerLayout DrawerLayout
     * @param color            Status bar color value
     */
    public static void setColorNoTranslucentForLeftDrawerLayout(Activity
                                                                        activity, LeftDrawerLayout leftDrawerLayout, @ColorInt int color) {
        setColorForLeftDrawerLayout(activity, leftDrawerLayout, color, 0);
    }

    /**
     * Set the status bar color for the LeftDrawerLayout layout
     *
     * @param activity         Activity that needs to be set
     * @param leftDrawerLayout DrawerLayout
     * @param color            Status bar color value
     * @param statusBarAlpha   Status bar transparency
     */
    public static void setColorForLeftDrawerLayout(Activity activity, LeftDrawerLayout
            leftDrawerLayout, @ColorInt int color, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // Generate a rectangle with a status bar size
        // Add statusBarView to the layout
        ViewGroup drawer = (ViewGroup) leftDrawerLayout.getChildAt(0);
        ViewGroup contentLayout = (ViewGroup) leftDrawerLayout.getChildAt(1);
        View fakeStatusBarView = contentLayout.findViewById(FAKE_STATUS_BAR_VIEW_ID);
        if (fakeStatusBarView != null) {
            if (fakeStatusBarView.getVisibility() == View.GONE) {
                fakeStatusBarView.setVisibility(View.VISIBLE);
            }
            fakeStatusBarView.setBackgroundColor(Color.TRANSPARENT);
        } else {
            drawer.addView(createStatusBarView(activity, color), 0);
            contentLayout.addView(createStatusBarView(activity, color), 0);
        }

        // Setting padding top when content layout is not LinearLayout
        if (!(drawer instanceof LinearLayout) && drawer.getChildAt(1) != null) {
            drawer
                    .getChildAt(1)
                    .setPadding(drawer.getPaddingLeft(),
                            getStatusBarHeight(activity) + drawer.getPaddingTop(),
                            drawer.getPaddingRight(),
                            drawer.getPaddingBottom());
        }

        // Setting padding top when content layout is not LinearLayout
        if (!(contentLayout instanceof LinearLayout) && contentLayout.getChildAt(1) != null) {
            contentLayout
                    .getChildAt(1)
                    .setPadding(contentLayout.getPaddingLeft(),
                            getStatusBarHeight(activity) + contentLayout.getPaddingTop(),
                            contentLayout.getPaddingRight(),
                            contentLayout.getPaddingBottom());
        }

        // Setting properties
        setLeftDrawerLayoutProperty(leftDrawerLayout, contentLayout);

        addTranslucentView(activity, statusBarAlpha);
    }

    /**
     * Set the LeftDrawerLayout property
     *
     * @param leftDrawerLayout          LeftDrawerLayout
     * @param drawerLayoutContentLayout Content layout of LeftDrawerLayout
     */
    private static void setLeftDrawerLayoutProperty(LeftDrawerLayout
                                                            leftDrawerLayout, ViewGroup drawerLayoutContentLayout) {
        ViewGroup drawer = (ViewGroup) leftDrawerLayout.getChildAt(0);

        leftDrawerLayout.setFitsSystemWindows(false);

        drawer.setFitsSystemWindows(false);
        drawer.setClipToPadding(true);
        drawerLayoutContentLayout.setFitsSystemWindows(false);
        drawerLayoutContentLayout.setClipToPadding(true);

    }

    /**
     * Set the status bar color for the LeftDrawerLayout layout (no translucent effect below 5.0, not recommended)
     *
     * @param activity         Activity that needs to be set
     * @param leftDrawerLayout LeftDrawerLayout
     * @param color            Status bar color value
     */
    @Deprecated
    public static void setColorForLeftDrawerLayoutDiff(Activity
                                                               activity, LeftDrawerLayout leftDrawerLayout, @ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Generate a rectangle with a status bar size
            ViewGroup contentLayout = (ViewGroup) leftDrawerLayout.getChildAt(1);
            View fakeStatusBarView = contentLayout.findViewById(FAKE_STATUS_BAR_VIEW_ID);
            if (fakeStatusBarView != null) {
                if (fakeStatusBarView.getVisibility() == View.GONE) {
                    fakeStatusBarView.setVisibility(View.VISIBLE);
                }
                fakeStatusBarView.setBackgroundColor(calculateStatusColor(color, DEFAULT_STATUS_BAR_ALPHA));
            } else {
                // Add statusBarView to the layout
                contentLayout.addView(createStatusBarView(activity, color), 0);
            }
            // Setting padding top when content layout is not LinearLayout
            if (!(contentLayout instanceof LinearLayout) && contentLayout.getChildAt(1) != null) {
                contentLayout.getChildAt(1).setPadding(0, getStatusBarHeight(activity), 0, 0);
            } // Setting properties
            setLeftDrawerLayoutProperty(leftDrawerLayout, contentLayout);
        }
    }

    /**
     * Set the status bar to be transparent for the LeftDrawerLayout layout
     *
     * @param activity         Activity that needs to be set
     * @param leftDrawerLayout LeftDrawerLayout
     */
    public static void setTranslucentForLeftDrawerLayout(Activity activity, LeftDrawerLayout
            leftDrawerLayout) {
        setTranslucentForLeftDrawerLayout(activity, leftDrawerLayout, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * Set the status bar to be transparent for the LeftDrawerLayout layout
     *
     * @param activity         Activity that needs to be set
     * @param leftDrawerLayout LeftDrawerLayout
     */
    public static void setTranslucentForLeftDrawerLayout(Activity
                                                                 activity, LeftDrawerLayout leftDrawerLayout,
                                                         @IntRange(from = 0, to = 255) int statusBarAlpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        setTransparentForLeftDrawerLayout(activity, leftDrawerLayout);
        addTranslucentView(activity, statusBarAlpha);
    }

    /**
     * Set the status bar to be transparent for the LeftDrawerLayout layout
     *
     * @param activity         Activity that needs to be set
     * @param leftDrawerLayout LeftDrawerLayout
     */
    public static void setTransparentForLeftDrawerLayout(Activity
                                                                 activity, LeftDrawerLayout leftDrawerLayout) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        ViewGroup contentLayout = (ViewGroup) leftDrawerLayout.getChildAt(1);
        // Setting padding top when content layout is not LinearLayout
        if (!(contentLayout instanceof LinearLayout) && contentLayout.getChildAt(1) != null) {
            contentLayout.getChildAt(1).setPadding(0, getStatusBarHeight(activity), 0, 0);
        } // Setting properties
        setLeftDrawerLayoutProperty(leftDrawerLayout, contentLayout);
    }

    /**
     * Set the status bar to be transparent for the LeftDrawerLayout layout (more than 5.0 translucent effect, not recommended)
     *
     * @param activity         Activity that needs to be set
     * @param leftDrawerLayout LeftDrawerLayout
     */
    @Deprecated
    public static void setTranslucentForLeftDrawerLayoutDiff(Activity
                                                                     activity, LeftDrawerLayout leftDrawerLayout) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // Set the status bar to be transparent
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // Set content layout properties
            ViewGroup contentLayout = (ViewGroup) leftDrawerLayout.getChildAt(1);
            contentLayout.setFitsSystemWindows(true);
            contentLayout.setClipToPadding(true);
            // Set drawer layout properties
            ViewGroup vg = (ViewGroup) leftDrawerLayout.getChildAt(0);
            vg.setFitsSystemWindows(false);
            // Set the LeftDrawerLayout property
            leftDrawerLayout.setFitsSystemWindows(false);
        }
    }


    /**
     * Set the status bar to be transparent for the interface that is the ImageView interface.
     *
     * @param activity       Activity that needs to be set
     * @param needOffsetView View that needs to be offset downward
     */
    public static void setTransparentForImageView
    (Activity activity, View needOffsetView) {
        setTranslucentForImageView(activity, 0, needOffsetView);
    }

    /**
     * Set the status bar to be transparent for the interface that is the ImageView (using the default transparency)
     *
     * @param activity       Activity that needs to be set
     * @param needOffsetView View that needs to be offset downward
     */
    public static void setTranslucentForImageView(Activity
                                                          activity, View needOffsetView) {
        setTranslucentForImageView(activity, DEFAULT_STATUS_BAR_ALPHA, needOffsetView);
    }

    /**
     * Set the status bar to be transparent for the interface where the header is ImageView
     *
     * @param activity       Activity that needs to be set
     * @param statusBarAlpha Status bar transparency
     * @param needOffsetView View that needs to be offset downward
     */
    public static void setTranslucentForImageView(Activity
                                                          activity,
                                                  @IntRange(from = 0, to = 255) int statusBarAlpha, View
                                                          needOffsetView) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        setTransparentForWindow(activity);
        addTranslucentView(activity, statusBarAlpha);
        if (needOffsetView != null) {
            Object haveSetOffset = needOffsetView.getTag(TAG_KEY_HAVE_SET_OFFSET);
            if (haveSetOffset != null && (Boolean) haveSetOffset) {
                return;
            }
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) needOffsetView.getLayoutParams();
            layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin + getStatusBarHeight(activity), layoutParams.rightMargin, layoutParams.bottomMargin);
            needOffsetView.setTag(TAG_KEY_HAVE_SET_OFFSET, true);
        }
    }

    /**
     * For the fragment header is the ImageView's setting status bar is transparent
     *
     * @param activity       The corresponding activity of the fragment
     * @param needOffsetView View that needs to be offset downward
     */
    public static void setTranslucentForImageViewInFragment
    (Activity activity, View needOffsetView) {
        setTranslucentForImageViewInFragment(activity, DEFAULT_STATUS_BAR_ALPHA, needOffsetView);
    }

    /**
     * For the fragment header is the ImageView's setting status bar is transparent
     *
     * @param activity       The corresponding activity of the fragment
     * @param needOffsetView View that needs to be offset downward
     */
    public static void setTransparentForImageViewInFragment
    (Activity activity, View needOffsetView) {
        setTranslucentForImageViewInFragment(activity, 0, needOffsetView);
    }

    /**
     * Set the status bar to transparent for the fragment whose Image is the ImageView
     *
     * @param activity       The corresponding activity of the fragment
     * @param statusBarAlpha Status bar transparency
     * @param needOffsetView View that needs to be offset downward
     */
    public static void setTranslucentForImageViewInFragment
    (Activity activity,
     @IntRange(from = 0, to = 255) int statusBarAlpha, View
             needOffsetView) {
        setTranslucentForImageView(activity, statusBarAlpha, needOffsetView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            clearPreviousSetting(activity);
        }
    }

    /**
     * Hide pseudo status bar View
     *
     * @param activity Called Activity
     */
    public static void hideFakeStatusBarView(Activity
                                                     activity) {
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        View fakeStatusBarView = decorView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
        if (fakeStatusBarView != null) {
            fakeStatusBarView.setVisibility(View.GONE);
        }
        View fakeTranslucentView = decorView.findViewById(FAKE_TRANSLUCENT_VIEW_ID);
        if (fakeTranslucentView != null) {
            fakeTranslucentView.setVisibility(View.GONE);
        }
    } ///////////////////////////////////////////////////////////////////////////////////

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void clearPreviousSetting(Activity activity) {
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        View fakeStatusBarView = decorView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
        if (fakeStatusBarView != null) {
            decorView.removeView(fakeStatusBarView);
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setPadding(0, 0, 0, 0);
        }
    }

    /**
     * Add a translucent rectangular strip
     *
     * @param activity       Activity that needs to be set
     * @param statusBarAlpha Transparent value
     */
    private static void addTranslucentView(Activity
                                                   activity, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
        View fakeTranslucentView = contentView.findViewById(FAKE_TRANSLUCENT_VIEW_ID);
        if (fakeTranslucentView != null) {
            if (fakeTranslucentView.getVisibility() == View.GONE) {
                fakeTranslucentView.setVisibility(View.VISIBLE);
            }
            fakeTranslucentView.setBackgroundColor(Color.argb(statusBarAlpha, 0, 0, 0));
        } else {
            contentView.addView(createTranslucentStatusBarView(activity, statusBarAlpha));
        }
    }

    /**
     * Generate a colored rectangle of the same size as the status bar
     *
     * @param activity Activity that needs to be set
     * @param color    Status bar color value
     * @return Status bar rectangle
     */
    private static View createStatusBarView(Activity
                                                    activity, @ColorInt int color) {
        return createStatusBarView(activity, color, 0);
    }

    /**
     * Generate a translucent rectangular strip of the same size as the status bar
     *
     * @param activity Activity that needs to be set
     * @param color    Status bar color value
     * @param alpha    Transparent value
     * @return Status bar rectangle
     */
    private static View createStatusBarView(Activity
                                                    activity, @ColorInt int color, int alpha) {
        // Draw a rectangle as high as the status bar
        View statusBarView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(calculateStatusColor(color, alpha));
        statusBarView.setId(FAKE_STATUS_BAR_VIEW_ID);
        return statusBarView;
    }

    /**
     * Set the root layout parameters
     */
    private static void setRootView(Activity activity) {
        ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(true);
                //
                //((ViewGroup) childView).setClipToPadding(true);
                ((ViewGroup) childView).setClipToPadding(false);
                //
            }
        }
    }

    /**
     * set transparent
     */
    private static void setTransparentForWindow(Activity
                                                        activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * set status bar transparent
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void transparentStatusBar
    (Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * Create a semi-transparent rectangle View
     *
     * @param alpha transparency
     * @return Translucent View
     */
    private static View createTranslucentStatusBarView
    (Activity activity, int alpha) {
        // Draw a rectangle as high as the status bar
        View statusBarView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
        statusBarView.setId(FAKE_TRANSLUCENT_VIEW_ID);
        return statusBarView;
    }

    /**
     * Get status bar height
     *
     * @param context context
     * @return Status bar height
     */
    private static int getStatusBarHeight(Context context) {
        // Get status bar height
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * Calculate the status bar color
     *
     * @param color color value
     * @param alpha alpha value
     * @return Final status bar color
     */
    private static int calculateStatusColor(
            @ColorInt int color, int alpha) {
        if (alpha == 0) {
            return color;
        }
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }
}