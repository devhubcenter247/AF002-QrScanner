package com.azg.alf_002_qrscan.ui.onboarding

import com.azg.alf_002_qrscan.R

sealed class OnboardingPage {
    data class Slide(
        val index: Int,
        val image: Int,
        val title: Int,
        val content: Int
    ) : OnboardingPage()

    data object FullAd : OnboardingPage()
}

fun setupOnboardingPage(
    isShowNativeFull1: Boolean = false,
): List<OnboardingPage> = buildList {
    add(
        OnboardingPage.Slide(
            index = 0,
            image = R.drawable.img_onboarding_1,
            title = R.string.intro_title_1,
            content = R.string.intro_content_1,
        )
    )
    add(
        OnboardingPage.Slide(
            index = 1,
            image = R.drawable.img_onboarding_2,
            title = R.string.intro_title_2,
            content = R.string.intro_content_2,
        )
    )

    if (isShowNativeFull1) {
        add(OnboardingPage.FullAd)
    }

    add(
        OnboardingPage.Slide(
            index = 2,
            image = R.drawable.img_onboarding_3,
            title = R.string.intro_title_3,
            content = R.string.intro_content_3,
        )
    )
    add(
        OnboardingPage.Slide(
            index = 2,
            image = R.drawable.img_onboarding_4,
            title = R.string.intro_title_4,
            content = R.string.intro_content_4,
        )
    )
}


fun setupOnboardingPreviewPage(): List<OnboardingPage> = buildList {
    add(
        OnboardingPage.Slide(
            index = 0,
            image = R.drawable.img_onboarding_1,
            title = R.string.intro_title_1,
            content = R.string.intro_content_1,
        )
    )
    add(
        OnboardingPage.Slide(
            index = 1,
            image = R.drawable.img_onboarding_2,
            title = R.string.intro_title_2,
            content = R.string.intro_content_2,
        )
    )
    add(
        OnboardingPage.Slide(
            index = 2,
            image = R.drawable.img_onboarding_3,
            title = R.string.intro_title_3,
            content = R.string.intro_content_3,
        )
    )
    add(
        OnboardingPage.Slide(
            index = 2,
            image = R.drawable.img_onboarding_4,
            title = R.string.intro_title_4,
            content = R.string.intro_content_4,
        )
    )
}
