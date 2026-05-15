Feature: In-Feed Topic Following

Background:
  * adb.resetApp(pkgName)
  * adb.waitFor('For you', 20)

Scenario: Follow topic from news card
  # Ensure we are past onboarding
  * if (adb.exists('Done')) { adb.click('Done'); adb.sleep(2000); }

  # 1. Follow a topic chip from the feed
  * if (adb.exists('Compose')) adb.click('Compose')
  * snap()
  
  # 2. Verify in Interests tab
  * adb.click('Interests')
  * adb.waitFor('Interests', 10)
  * snap()
  * assert adb.exists('Compose')
  * adb.home()
