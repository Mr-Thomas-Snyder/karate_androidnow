Feature: Comprehensive User Flow

Background:
  * adb.resetApp(pkgName)
  * adb.dismissPermissionDialog()
  * adb.waitFor('For you', 35)

Scenario: Complete onboarding and navigate tabs
  * snap()
  # 1. Onboarding - Follow a topic
  * adb.scrollUntilVisible('Compose')
  * adb.click('Compose')
  
  # 2. Wait for Done button activation
  * print 'Waiting for topic_followed log or Done button state...'
  * def logFound = adb.waitForLog('topic_followed', 15)
  * def doneFound = adb.waitFor('Done', 10)
  * snap()
  * assert logFound == true || doneFound == true
  
  # 3. Click Done
  * adb.click('Done')
  * adb.waitFor('For you', 20)
  * snap()
  
  # 4. Verify we are on the Feed (Onboarding should be gone)
  * assert !adb.exists('What are you interested in?')
  * print 'Onboarding successfully completed'
  
  # 5. Navigate to Saved
  * adb.click('Saved')
  * adb.waitFor('Saved', 15)
  * snap()
  
  # 6. Navigate to Interests
  * adb.click('Interests')
  * adb.waitFor('Interests', 15)
  * snap()
  * adb.scrollUntilVisible('Kotlin')
  * assert adb.exists('Kotlin')
  
  # 7. Back to For You
  * adb.click('For you')
  * adb.waitFor('For you', 15)
  * snap()
  * adb.home()
