-dontpreverify
-dontobfuscate
-dontoptimize
-dontshrink
-dontnote
-ignorewarnings

-printmapping myapplication.map

-keep public class com.happydroids.sparky.SparkyMain {
    public static void main(java.lang.String[]);
}


-keepclassmembers class * extends java.lang.Enum {
    *** values();
}
