& "C:\Program Files\Java\jdk-22\bin\javac.exe" -Xlint:deprecation -d D:\minter\bin -cp "D:\minter\lib\*" -sourcepath D:\minter\src D:\minter\src\interpreter\*.java D:\minter\src\functions\*.java D:\minter\src\modules\*.java D:\minter\src\memory\*.java D:\minter\src\minter.java
& "C:\Program Files\Java\jdk-22\bin\java.exe" --enable-preview -XX:+ShowCodeDetailsInExceptionMessages -cp "D:\minter\lib\*;D:\minter\bin" minter test/check.minter
& "C:\Program Files\Java\jdk-22\bin\jar.exe" cfm D:\minter\dist\minter.jar manifest.txt -C D:\minter\bin .
