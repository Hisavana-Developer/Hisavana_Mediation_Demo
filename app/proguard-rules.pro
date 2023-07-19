-optimizationpasses 5
-allowaccessmodification
-optimizations !code/simplification/arithmetic
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
# dex does not like code run through proguard optimize and preverify steps.
-dontusemixedcaseclassnames
-ignorewarnings

-printmapping mapping.txt

-renamesourcefileattribute SourceFile

-keepattributes SouceFile,LineNumberTable

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep class com.transsion.view.utils.* {
    *;
}
-keep class com.cyin.himgr.clean.widget.* {
    public *;
}
-keep class de.greenrobot.dao.* {
    public *;
}
-keep class com.cyin.himgr.clean.dao.* {
    public *;
}
-keep class com.transsion.lib.harassment.* {
    *;
}
-keep class cyin.himgr.harassmentintercept.service.InterceptSercice {
    *;
}
-keep class com.cyin.himgr.harassmentintercept.service.InterceptSercice$HarassInterceptImpl {
    *;
}
-keep class com.android.phone.aidl.* {
    *;
}
-keep class org.apache.http.** {
    *;
}
-keep public class com.cyin.himgr.ads.**{*;}

#-dontwarn com.tencent.bugly.**
#-keep public class com.tencent.bugly.**{*;}

#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int, int);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
  public <fields>;
  private <fields>;
  public <methods>;
}
-keepclassmembers class **.R$* {
    public static <fields>;
}

# v4 包的混淆 start
-keepattributes *Annotation*

-dontwarn **CompatHoneycomb
-dontwarn **CompatHoneycombMR2
-dontwarn **CompatCreatorHoneycombMR2
-keep public class * extends android.app.Fragment
#v4 end

#HIOS:add MANAGER-81 by xiaodong.xu 20170328 on
-keepattributes Signature

-dontwarn com.google.gson.**
-keep class com.google.gson.** {*;}

-dontwarn com.google.gson.reflect.**
-keep class com.google.gson.reflect.** {*;}
-dontwarn transsion.widgetslib.util.**
-keep class transsion.widgetslib.util.** {*;}

#-keep class sun.misc.Unsafe { *; }
-dontwarn sun.misc.Unsafe

-keep class com.google.gson.examples.android.model.** { *; }

# LeakCanary
-keep class org.eclipse.mat.** { *; }
-keep class com.squareup.leakcanary.** { *; }

-dontwarn com.google.common.collect.MinMaxPriorityQueue
-dontwarn com.google.common.**
# test.espresso
-keep class com.google.common.collect.MapMakerInternalMap$ReferenceEntry
-keep class com.google.common.cache.LocalCache$ReferenceEntry
-dontwarn com.google.common.cache.**
-dontwarn com.google.common.primitives.**
-dontwarn com.google.common.util.concurrent.RateLimiter

-keep class net.sqlcipher.** {
    *;
}

-dontwarn net.sqlcipher.**

-dontwarn retrofit2.**
-keep class retrofit2.** {
    *;
}

-dontwarn okio.**
-keep class okio.** {
    *;
}

-dontwarn okhttp3.**
-keep class okhttp3.** {
    *;
}

-dontwarn com.transsion.trashupload.**
-keep class com.transsion.trashupload.** {
    *;
}

-keepattributes EnclosingMethod
-keepattributes InnerClasses
-dontnote

-dontwarn org.greenrobot.**
-keep class org.greenrobot.** { *; }
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties

-dontwarn rx.**
-keep class rx.** { *;}

-dontwarn net.sqlcipher.**
-keep class net.sqlcipher.** { *;}
-keep class com.cyin.himgr.gamemode.model.UpdateMapSQL{*;}
####---------------Begin: proguard configuration for MIntegralSdk---------
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.mintegral.** {*; }
-keep interface com.mintegral.** {*; }
-dontwarn com.mintegral.**
-keep class **.R$* { public static final int mintegral*; }
-keep class com.alphab.** {*; }
-keep interface com.alphab.** {*; }
####---------------End: proguard configuration for MIntegralSdk------------

####---------------start: proguard configuration for BrotherProduct------------
-keep public class com.transsion.beans.model.**{*;}

-keep public class android.webkit.JavascriptInterface {}
# Support for Android Advertiser ID.
-keep class com.google.android.gms.common.GooglePlayServicesUtil {*;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient {*;}
-keep class com.google.android.gms.ads.identifier.AdvertisingIdClient$Info {*;}
# Support for Google Play Services
# http://developer.android.com/google/play-services/setup.html
-keep class * extends java.util.ListResourceBundle {
 protected Object[][] getContents();
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
 public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
 @com.google.android.gms.common.annotation.KeepName *;
}
-keepnames class * implements android.os.Parcelable {
 public static final ** CREATOR;
}

-keep class com.transsion.remoteconfig.FunctionStatus{*;}
-keep class com.cyin.himgr.smartclean.bean.SmartCleanItem{*;}

####---------------Start: proguard configurationfor glide----------------------
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
####---------------End: proguard configurationfor glide----------------------

-keep public class com.transsion.resultrecommendfunction.bean.**{*;}
-keep public class com.cyin.himgr.share.ShareManager
-keep public class com.cyin.himgr.share.util.HelpShareUtil{*;}
-keep public class com.transsion.resultrecommendfunction.presenter.ResultManager{*;}
-keep public class com.transsion.utils.ReflexUtil{*;}
-keep public interface android.content.pm.IPackageStatsObserver{*;}
-keep public interface android.content.pm.IPackageDataObsrver{*;}


-keep public class  com.facebook.ads.internal.settings.*{
public *;
}

