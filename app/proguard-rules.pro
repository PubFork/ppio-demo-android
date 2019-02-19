# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontusemixedcaseclassnames          #Do not use case-insensitive class names when confusing
#-dontskipnonpubliclibraryclasses    #Do not skip non-public classes in the library
-verbose                             #Print obfuscated details
-dontoptimize                        #This option is recommended for no optimization.
-dontpreverify                       #Without pre-verification, Android does not need to speed up the confusion.
-ignorewarnings                      #Ignore warning
-optimizationpasses 5                #Specify the compression level of the code

-keepattributes SourceFile,LineNumberTable

#app not confused
-keep class io.pp.net_disk_demo.*
-keep class io.pp.net_disk_demo.**{*;}

#the packages go of poss.aar not confused
-keep class go.*
-keep class go.**{*;}

#the package poss of poss.aar not confused
-keep class poss.*
-keep class poss.**{*;}

#native method not confused
-keepclasseswithmembernames class * {
    native <methods>;
}
#v4 packages not confused
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }

#JavaBean
-keepclassmembers public class cn.net.duqian.bean.** {
   void set*(***);
   *** get*();
}
-keep class com.xx.duqian_cloud.JavaScriptInterface { *; }#webview js

#Third party framework
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

#Gson
#If has use Gson parsing package, directly add the following lines to successfully confuse, or it will report an error.
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

-keep  class org.alexd.jsonrpc.*
-keep  class org.alexd.jsonrpc.**{*;}

#Bugly
-dontwarn com.tencent.bugly
-keep class com.tencent.bugly.**{*;}

-dontwarn com.fasterxml.jackson
-keep class com.fasterxml.jackson.*
-keep class com.fasterxml.jackson.**{*;}

-dontwarn okhttp3
-keep class okhttp3.*
-keep class okhttp3.**{*;}