function fn() {
  var env = karate.env; // get system property 'karate.env'
  karate.log('karate.env system property was:', env);
  
  if (!env) {
    env = 'dev';
  }
  
  var AdbDriver = Java.type('com.android.nowinandroid.utils.AdbDriver');
  var adb = new AdbDriver();

  var config = {
    env: env,
    adb: adb,
    pkgName: 'com.google.samples.apps.nowinandroid.demo.debug',
    snap: function(name) {
      var bytes = adb.screenshot();
      if (bytes) {
        karate.embed(bytes, 'image/png');
      }
    }
  };

  // Cleanup old files
  adb.cleanup();

  return config;
}
