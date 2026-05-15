Feature: Bookmarking

Background:
  * adb.resetApp(pkgName)
  * adb.waitFor('For you', 20)

Scenario: Bookmark item and verify in Saved
  # Ensure we are past onboarding
  * if (adb.exists('Done')) { adb.click('Done'); adb.sleep(2000); }

  # 1. Bookmark first item
  * eval adb.click('Bookmark') || adb.click('newsResourceCard')
  * snap()

  # 2. Verify in Saved tab
  * adb.click('Saved')
  * adb.waitFor('Saved', 10)
  * snap()
  # Check that saved list is not empty
  * assert !adb.exists('nothing saved') 
  * adb.home()
