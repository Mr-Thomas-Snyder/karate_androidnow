Feature: Exclusive Topic Propagation

Background:
  * adb.resetApp(pkgName)
  * adb.dismissPermissionDialog()
  * adb.waitFor('For you', 35)

Scenario: Follow Headlines and verify in Interests
  # 1. Follow Headlines from Onboarding
  * adb.scrollUntilVisible('Headlines')
  * adb.click('Headlines')
  * adb.click('Done')
  * adb.waitFor('For you', 20)
  * snap()

  # 2. Verify in Interests
  * adb.click('Interests')
  * adb.waitFor('Interests', 15)
  * snap()
  * adb.scrollUntilVisible('Headlines')
  * assert adb.exists('Headlines')
  
  # 3. Unfollow from Interests
  * adb.click('Headlines')
  * adb.sleep(1000)
  * snap()

  # 4. Verify For You reset
  * adb.click('For you')
  * adb.waitFor('What are you interested in?', 15)
  * snap()
  * adb.home()
