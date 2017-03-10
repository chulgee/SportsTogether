# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\chulchoice\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Retrofit 2.X
## https://square.github.io/retrofit/ ##

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * implements java.io.Serializable
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v4.app.ListFragment



-keep public class org.spongycastle.** {
  <fields>;
  <methods>;
}

-keep public class org.apache.** {
  <fields>;
  <methods>;
}

-ignorewarnings

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#-keepclassmembers class * extends android.app.Activity {
#   public void *(android.view.View);
#}


-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}



# Retrofit
-keep class com.google.gson.** { *; }
-keep public class com.google.gson.** {public private protected *;}
-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-keep class javax.xml.stream.** { *; }
-keep class retrofit2.** { *; }
-keep class com.google.appengine.** { *; }
-keepattributes *Annotation*
-keepattributes Signature
-dontwarn com.squareup.okhttp.*
-dontwarn rx.**
-dontwarn javax.xml.stream.**
-dontwarn com.google.appengine.**
-dontwarn java.nio.file.**
-dontwarn org.codehaus.**



-dontwarn retrofit2.**
-dontwarn org.codehaus.mojo.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

-keepattributes EnclosingMethod
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclasseswithmembers interface * {
    @retrofit2.* <methods>;
}
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

-keep class com.iron.dragon.sportstogether.data.** { *; }
-keepclassmembers class com.iron.dragon.sportstogether.data.** { *; }
-keep class com.iron.dragon.sportstogether.http.** { *; }
-keepclassmembers class com.iron.dragon.sportstogether.http.** { *; }
-keep class com.iron.dragon.sportstogether.ui.adapter.** { *; }
-keepclassmembers class com.iron.dragon.sportstogether.ui.adapter.** { *; }
-keep class com.iron.dragon.sportstogether.ui.adapter.item.NewsListItem.** { *; }

-keepattributes *Annotation*,EnclosingMethod
-keep class reflection.using.package.name.** { *; }

-keep class com.iron.dragon.sportstogether.ui** {
    public *;
 }

-keepclassmembers interface com.iron.dragon.sportstogether.** {*;}

-keepattributes SourceFile,LineNumberTable
-keepclasseswithmembers class * {
    native <methods>;
 }

 #RxJava
 -dontwarn sun.misc.**

 -keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
 }

 -keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
     rx.internal.util.atomic.LinkedQueueNode producerNode;
 }

 -keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
     rx.internal.util.atomic.LinkedQueueNode consumerNode;
 }

 -dontnote rx.internal.util.PlatformDependent

 #Socket.io
 -keep class com.github.nkzawa.*
 -keepclasseswithmembers class com.github.nkzawa.* { ; }
 -keep class socket.io-client.
 -keepclasseswithmembers class socket.io-client.* { ; }
 -keep class io.socket.
 -keepclasseswithmembers allowshrinking class io.socket.* { *; }

 #JSONObject
 -optimizationpasses 5
 -dontusemixedcaseclassnames
 -dontskipnonpubliclibraryclasses
 -dontpreverify
 -verbose
 -optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

 -keep public class * extends android.app.Activity
 -keep public class * extends android.app.Application
 -keep public class * extends android.app.Service
 -keep public class * extends android.content.BroadcastReceiver
 -keep public class * extends android.content.ContentProvider
 -keep public class * extends android.app.backup.BackupAgentHelper
 -keep public class * extends android.preference.Preference
 -keep public class com.android.vending.licensing.ILicensingService

 -keepclasseswithmembernames class * {
     native <methods>;
 }

 -keepclasseswithmembers class * {
     public <init>(android.content.Context, android.util.AttributeSet);
 }

 -keepclasseswithmembers class * {
     public <init>(android.content.Context, android.util.AttributeSet, int);
 }

 -keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
 }

 -keepclassmembers enum * {
     public static **[] values();
     public static ** valueOf(java.lang.String);
 }

 -keep class * implements android.os.Parcelable {
   public static final android.os.Parcelable$Creator *;
 }
 -dontwarn java.lang.invoke.*