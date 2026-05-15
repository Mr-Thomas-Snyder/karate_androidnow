Feature: News Detail and Browser Navigation

Background:
  * adb.resetApp(pkgName)
  * adb.dismissPermissionDialog()
  * adb.waitFor('For you', 35)

Scenario: Open news item and return from browser
  # Ensure we are past onboarding
  * if (adb.exists('Done')) { adb.click('Compose'); adb.click('Done'); adb.sleep(3000); }

  # Click first news item
  * print 'Locating news item...'
  * adb.swipeUp()
  * adb.sleep(2000)
  * snap()
  
  # Tap in the middle of the screen where a news card typically resides
  * print 'Tapping news card...'
  * adb.runCommand('shell input tap 500 1000')
  
  * print 'Waiting for browser to open...'
  * def browserOpened = adb.waitForForegroundPackage('chrome', 20) || adb.waitForForegroundPackage('browser', 20)
  * snap()

  # Verify browser foreground
  * def fgPkg = adb.getForegroundPackage()
  * print 'Foreground package: ' + fgPkg
  * assert !fgPkg.contains('nowinandroid') || fgPkg.contains('chrome') || fgPkg.contains('browser')

  # Go back using system keyevent
  * adb.back()
  * adb.sleep(4000)
  * snap()

  # Verify back in NiA
  * def fgPkgBack = adb.getForegroundPackage()
  * print 'Foreground package after back: ' + fgPkgBack
  * assert fgPkgBack.contains('nowinandroid')
  * adb.home()
