package com.wmods.wppenhacer.ads

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.button.MaterialButton
import com.wmods.wppenhacer.R
import java.util.concurrent.atomic.AtomicBoolean

object AdHelper {

    private const val TAG = "AdHelper"

    // Master toggle to enable or disable ads across the app
    @JvmField
    var ADS_ENABLED: Boolean = false

    // Default official Google test ad unit IDs
    // Replace with your real production Ad Unit IDs when ready!
    @JvmField
    var NATIVE_AD_UNIT_ID: String = "ca-app-pub-3940256099942544/2247696110"
    @JvmField
    var BANNER_AD_UNIT_ID: String = "ca-app-pub-3940256099942544/6300978111"

    private val isInitialized = AtomicBoolean(false)

    @JvmStatic
    fun initialize(context: Context, onComplete: (() -> Unit)? = null) {
        if (!ADS_ENABLED) return

        if (isInitialized.compareAndSet(false, true)) {
            try {
                MobileAds.initialize(context) { initializationStatus ->
                    Log.d(TAG, "Google Mobile Ads initialized: $initializationStatus")
                    onComplete?.invoke()
                }
            } catch (e: Throwable) {
                Log.e(TAG, "Failed to initialize Google Mobile Ads", e)
            }
        }
    }

    /**
     * Loads a Native Ad and populates it into the given container.
     */
    @JvmOverloads
    @JvmStatic
    fun loadNativeAd(
        context: Context,
        container: ViewGroup,
        adUnitId: String = NATIVE_AD_UNIT_ID,
        onLoaded: ((NativeAd) -> Unit)? = null,
        onFailed: ((LoadAdError) -> Unit)? = null
    ) {
        if (!ADS_ENABLED) {
            container.visibility = View.GONE
            return
        }
        try {
            val adLoader = AdLoader.Builder(context, adUnitId)
                .forNativeAd { nativeAd ->
                    val inflater = LayoutInflater.from(context)
                    val adView = inflater.inflate(R.layout.layout_native_ad, container, false) as NativeAdView
                    populateNativeAdView(nativeAd, adView)

                    container.removeAllViews()
                    container.addView(adView)
                    container.visibility = View.VISIBLE
                    onLoaded?.invoke(nativeAd)
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.w(TAG, "Native ad failed to load: ${loadAdError.message} (Code: ${loadAdError.code})")
                        container.visibility = View.GONE
                        onFailed?.invoke(loadAdError)
                    }
                })
                .withNativeAdOptions(
                    NativeAdOptions.Builder()
                        .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                        .build()
                )
                .build()

            val adRequest = AdRequest.Builder().build()
            adLoader.loadAd(adRequest)
        } catch (e: Throwable) {
            Log.e(TAG, "Error initiating native ad load", e)
            container.visibility = View.GONE
        }
    }

    /**
     * Populates all subviews within the NativeAdView.
     */
    @JvmStatic
    fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        // Headline
        val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
        headlineView.text = nativeAd.headline
        adView.headlineView = headlineView

        // Body
        val bodyView = adView.findViewById<TextView>(R.id.ad_body)
        if (nativeAd.body == null) {
            bodyView.visibility = View.GONE
        } else {
            bodyView.visibility = View.VISIBLE
            bodyView.text = nativeAd.body
            adView.bodyView = bodyView
        }

        // Call to action button
        val ctaView = adView.findViewById<MaterialButton>(R.id.ad_call_to_action)
        if (nativeAd.callToAction == null) {
            ctaView.visibility = View.GONE
        } else {
            ctaView.visibility = View.VISIBLE
            ctaView.text = nativeAd.callToAction
            adView.callToActionView = ctaView
        }

        // App Icon / Logo
        val iconView = adView.findViewById<ImageView>(R.id.ad_app_icon)
        if (nativeAd.icon == null) {
            iconView.visibility = View.GONE
        } else {
            iconView.visibility = View.VISIBLE
            iconView.setImageDrawable(nativeAd.icon?.drawable)
            adView.iconView = iconView
        }

        // Star Rating
        val starRatingView = adView.findViewById<RatingBar>(R.id.ad_stars)
        if (nativeAd.starRating == null || nativeAd.starRating!! <= 0.0) {
            starRatingView?.visibility = View.GONE
        } else {
            starRatingView?.visibility = View.VISIBLE
            starRatingView?.rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView = starRatingView
        }

        // Advertiser
        val advertiserView = adView.findViewById<TextView>(R.id.ad_advertiser)
        if (nativeAd.advertiser == null) {
            advertiserView?.visibility = View.GONE
        } else {
            advertiserView?.visibility = View.VISIBLE
            advertiserView?.text = nativeAd.advertiser
            adView.advertiserView = advertiserView
        }

        // Assign the native ad object to the view
        adView.setNativeAd(nativeAd)
    }

    /**
     * Loads a standard Banner Ad into the given container.
     */
    @JvmStatic
    fun loadBannerAd(
        context: Context,
        container: ViewGroup,
        adUnitId: String = BANNER_AD_UNIT_ID,
        adSize: AdSize = AdSize.BANNER
    ) {
        if (!ADS_ENABLED) {
            container.visibility = View.GONE
            return
        }
        try {
            val adView = AdView(context).apply {
                this.adUnitId = adUnitId
                setAdSize(adSize)
            }
            adView.adListener = object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.w(TAG, "Banner ad failed to load: ${loadAdError.message}")
                    container.visibility = View.GONE
                }

                override fun onAdLoaded() {
                    container.visibility = View.VISIBLE
                }
            }
            container.removeAllViews()
            container.addView(adView)
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        } catch (e: Throwable) {
            Log.e(TAG, "Error loading banner ad", e)
            container.visibility = View.GONE
        }
    }
}
