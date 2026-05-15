Feature: Interests and For You Sync

Background:
  * adb.resetApp(pkgName)
  * adb.dismissPermissionDialog()
  * adb.waitFor('For you', 35)

Scenario: Follow on Interests and verify on For You
  # 1. Go to Interests and follow Compose
  * adb.click('Interests')
  * adb.waitFor('Interests', 10)
  * adb.scrollUntilVisible('Compose')
  * adb.click('Compose')
  * snap()

  # 2. Go to For You and verify Done is enabled
  * adb.click('For you')
  * adb.waitFor('Done', 10)
  * snap()
  * assert adb.exists('What are you interested in?')
  
  # 3. Go back to Interests and unfollow
  * adb.click('Interests')
  * adb.waitFor('Interests', 10)
  * adb.scrollUntilVisible('Compose')
  * adb.click('Compose')
  * snap()

  # 4. Verify For You is back to disabled state
  * adb.click('For you')
  * adb.sleep(2000)
  * snap()
  * adb.home()
