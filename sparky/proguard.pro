-dontobfuscate
-dontoptimize
-dontshrink
-dontnote
-ignorewarnings

-printmapping myapplication.map

-keep public class com.happydroids.sparky.SparkyMain {
    public static void main(java.lang.String[]);
}

-keep public class * extends java.JApplet
-keepclassmembers public class jflowmap.*Applet { *; }
-keepclassmembers public class * extends java.JApplet { *; }

-keepclassmembers public class org.apache.log4j.* { *** get*(); boolean is*(); void set*(***); }

-keepclassmembers class * extends java.lang.Enum {
    *** values();
}
