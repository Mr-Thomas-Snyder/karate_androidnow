Feature: App Launch and Initial State

Background:
  * adb.resetApp(pkgName)
  * adb.dismissPermissionDialog()
  
Scenario: Verify app launch and onboarding presence
  * print 'Waiting for app to load...'
  * def loaded = adb.waitFor('For you', 35)
  * assert loaded == true
  * adb.waitFor('Now in Android', 10)
  * snap()
  
  # Check if we are on the onboarding screen
  * assert adb.exists('Now in Android')
  * print 'App title and For You tab confirmed'
  
  # Verify bottom tabs are present
  * assert adb.exists('Saved')
  * assert adb.exists('Interests')
  * adb.home()
  * print 'Bottom navigation tabs confirmed'
